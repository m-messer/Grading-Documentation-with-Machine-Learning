import argparse
import itertools
from pathlib import Path

import optuna
from matplotlib import pyplot as plt

from metrics import compute_metrics
from tokeniser_vectorizer import TokenizerVectorizer
from transformers import Trainer, TrainingArguments, AutoModelForSequenceClassification, set_seed
from torch import cuda
import wandb
from sklearn.model_selection import StratifiedKFold
import numpy as np
import seaborn as sns
from peft import LoraConfig, get_peft_model, TaskType


class Train:
    def __init__(self, data_dir, wandb_project, pre_trained_model, pre_process=False,
                 binary=False, folds=10):

        self.lora_model = None
        self.trainer = None
        self.training_arguments = None
        set_seed(100)

        with open('secrets/wandb_api_key.txt') as f:
            wandb.login(key=f.read())

        self.wandb_project = wandb_project
        self.folds = folds
        self.pre_trained_model = pre_trained_model
        self.pre_process = pre_process

        self.tokenizer_vectorizer = TokenizerVectorizer(vectorization_method='pre-trained', data_dir=data_dir,
                                                        binary=binary, pre_trained_model=pre_trained_model,
                                                        pre_process=pre_process)

        self.data = self.tokenizer_vectorizer.get_pre_trained_tokenized_data()

        self.train_test_data = self.data.train_test_split(test_size=0.2)

        Path('plots').mkdir(exist_ok=True)

        sns.countplot(self.train_test_data['train'].to_pandas(), x='label')
        plt.savefig('plots/train_data.pdf')
        sns.countplot(self.train_test_data['test'].to_pandas(), x='label')
        plt.savefig('plots/test_data.pdf')

        self.id2label, self.label2id, label_count = get_label_info(binary=binary)

        self.model = AutoModelForSequenceClassification.from_pretrained(pre_trained_model, num_labels=label_count,
                                                                        id2label=self.id2label, label2id=self.label2id)

        device = "cuda:0" if cuda.is_available() else "cpu"
        self.model.to(device)

    def train_with_cross_validation(self, trial):
        config = dict(trial.params)
        config['trial.number'] = trial.number

        if self.pre_process:
            tags = ['preprocessed', 'no custom weights', 'DEV']
        else:
            tags = None

        wandb.init(
            project=self.wandb_project,
            group="LORA:" + self.pre_trained_model,
            tags=tags,
            reinit=True
        )

        learning_rate = trial.suggest_float('learning_rate', 1e-6, 1e-4, log=True)
        batch_size = trial.suggest_categorical('batch_size', [16, 32])
        epochs = trial.suggest_categorical('epochs', [10, 50, 100])

        target_modules = ['query', 'value', 'key', 'dense', 'linear', 'embeddings']
        combinations = []

        for r in range(1, len(target_modules) + 1):
            combinations.extend([list(x) for x in itertools.combinations(iterable=target_modules, r=r)])

        lora_modules = trial.suggest_categorical('lora_modules', combinations)
        lora_rank = trial.suggest_categorical('lora_rank', [8, 16, 32, 64])

        lora_config = LoraConfig(
            r=lora_rank,
            lora_alpha=32,
            target_modules=lora_modules,
            lora_dropout=0.1,
            bias="lora_only",
            modules_to_save=['decode_head'],
            task_type=TaskType.SEQ_CLS
        )

        self.lora_model = get_peft_model(self.model, lora_config)

        self.training_arguments = TrainingArguments(
            output_dir='huggingface_models',
            learning_rate=learning_rate,
            per_device_train_batch_size=batch_size,
            per_device_eval_batch_size=batch_size,
            num_train_epochs=epochs,
            evaluation_strategy="epoch",
            save_strategy="epoch",
            load_best_model_at_end=True,
            save_total_limit=5,
            push_to_hub=False,
            report_to=["wandb"]
        )

        folds = StratifiedKFold(n_splits=self.folds)

        splits = folds.split(np.zeros(self.train_test_data['train'].num_rows), self.train_test_data['train']['label'])

        for train_idxs, val_idxs in splits:
            train_data = self.train_test_data['train'].select(train_idxs)
            validation_data = self.train_test_data['train'].select(val_idxs)

            self.trainer = Trainer(
                model=self.lora_model,
                args=self.training_arguments,
                train_dataset=train_data,
                eval_dataset=validation_data,
                tokenizer=self.tokenizer_vectorizer.tokenizer,
                data_collator=self.tokenizer_vectorizer.data_collator,
                compute_metrics=compute_metrics,
            )

            self.trainer.train()

    def evaluate(self):
        self.lora_model.eval()

        model_predictions = self.trainer.predict(self.train_test_data['test'])
        print(model_predictions.metrics)

        eval_results_formatted = \
            {"test/" + key.split('_', 1)[1]: item for key, item in model_predictions.metrics.items()}

        print("Test Results:")
        print(str(eval_results_formatted))
        wandb.log(eval_results_formatted)
        return eval_results_formatted['test/accuracy']

    def objective(self, trial):
        self.train_with_cross_validation(trial)
        test_acc = self.evaluate()
        return test_acc


def main():
    parser = argparse.ArgumentParser(description='Fine-Tune LLM Models')
    parser.add_argument('-n_trails', dest='n_trails', default=10, type=int, help='The number of Optuna trials')
    parser.add_argument('-pre-trained', dest='pre_trained',
                        help='A HuggingFace model for vectorisation and fine-tuning')
    parser.add_argument('-pre-process', dest='pre_process', default=False, help='Run preprocessing steps',
                        action='store_true')
    args = parser.parse_args()

    if args.pre_trained is None:
        print("Please supply a hugging face model to fine tune, using -pre-trained")
        return

    train = Train(
        pre_trained_model=args.pre_trained,
        data_dir='data/code_search_net_relevance.hf',
        binary=False,
        wandb_project='JavaDoc-Relevance-Binary-Classifier',
        pre_process=args.pre_process,
    )

    study = optuna.create_study(direction='maximize')
    study.optimize(train.objective, n_trials=args.n_trails)


if __name__ == '__main__':
    main()
