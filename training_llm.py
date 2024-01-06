import argparse
from pathlib import Path

import optuna
from matplotlib import pyplot as plt

from tokeniser_vectorizer import TokenizerVectorizer
from transformers import Trainer, TrainingArguments, AutoModelForSequenceClassification, set_seed
from torch import nn, tensor, cuda
import wandb
from sklearn.model_selection import StratifiedKFold
import numpy as np
import evaluate
import seaborn as sns


class CustomTrainer(Trainer):
    def __init__(self, weights, model, args, train_dataset, eval_dataset, tokenizer, data_collator, compute_metrics):
        self.weights = weights
        super().__init__(model=model,
                         args=args,
                         train_dataset=train_dataset,
                         eval_dataset=eval_dataset,
                         tokenizer=tokenizer,
                         data_collator=data_collator,
                         compute_metrics=compute_metrics)

    def compute_loss(self, model, inputs, return_outputs=False):
        labels = inputs.pop('labels')
        outputs = model(**inputs)
        logits = outputs.get('logits')
        loss_fn = nn.CrossEntropyLoss(weight=tensor(self.weights, device=model.device))
        loss = loss_fn(logits.view(-1, self.model.config.num_labels), labels.view(-1))

        return (loss, outputs) if return_outputs else loss

class Train:
    def __init__(self, data_dir, wandb_project, pre_trained_model, pre_process=False,
                 binary=False, folds=10):

        self.training_arguments = None
        set_seed(100)

        with open('secrets/wandb_api_key.txt') as f:
            wandb.login(key=f.read())

        self.wandb_project = wandb_project
        self.folds = folds
        self.pre_trained_model = pre_trained_model
        self.pre_process = pre_process

        self.data_curator = TokenizerVectorizer(vectorization_method='pre-trained', data_dir=data_dir,
                                                binary=binary, pre_trained_model=pre_trained_model,
                                                pre_process=pre_process)

        self.data = self.data_curator.get_pre_trained_tokenized_data()

        self.train_test_data = self.data.train_test_split(test_size=0.2)
        self.weights = self.calculate_class_weights()

        Path('plots').mkdir(exist_ok=True)

        sns.countplot(self.train_test_data['train'].to_pandas(), x='label')
        plt.savefig('plots/train_data.pdf')
        sns.countplot(self.train_test_data['test'].to_pandas(), x='label')
        plt.savefig('plots/test_data.pdf')

        if binary:
            self.id2label = {0: 'irrelevant', 1: 'relevant'}
            self.label2id = {'irrelevant': 0, 'relevant': 1}

            label_count = 2
        else:
            self.id2label = {0: 'irrelevant', 1: 'partially irrelevant', 2: 'partially relevant',  3: 'relevant'}
            self.label2id = {'irrelevant': 0, 'partially irrelevant': 1, 'partially relevant': 2, 'relevant': 1}

            label_count = 4

        self.model = AutoModelForSequenceClassification.from_pretrained(pre_trained_model, num_labels=label_count,
                                                                        id2label=self.id2label, label2id=self.label2id)
        device = "cuda:0" if cuda.is_available() else "cpu"
        self.model.to(device)

        self.f1 = evaluate.load('f1')
        self.accuracy = evaluate.load('accuracy')
        self.recall = evaluate.load('recall')
        self.precision = evaluate.load('precision')

    def calculate_class_weights(self):
        class_count = self.data.to_pandas().groupby('label').count()['input_ids'].to_list()
        total = sum(class_count)
        return [1 - (val / total) for val in class_count]

    def format_metrics(self, metrics, prefix):
        return {prefix + "/" + key: item for key, item in metrics.items()}

    def compute_metrics(self, eval_pred):
        predictions, labels = eval_pred
        predictions = np.argmax(predictions, axis=1)

        accuracy_res = self.accuracy.compute(predictions=predictions, references=labels)['accuracy']
        f1_macro_res = self.f1.compute(predictions=predictions, references=labels, average='macro')['f1']
        f1_micro_res = self.f1.compute(predictions=predictions, references=labels, average='micro')['f1']
        f1_weighted_res = self.f1.compute(predictions=predictions, references=labels, average='weighted')['f1']
        recall_macro_res = self.recall.compute(predictions=predictions, references=labels, average='macro')['recall']
        recall_micro_res = self.recall.compute(predictions=predictions, references=labels, average='micro')['recall']
        recall_weighted_res = self.recall.compute(predictions=predictions, references=labels, average='weighted')[
            'recall']
        precision_macro_res = self.precision.compute(predictions=predictions, references=labels, average='macro')[
            'precision']
        precision_micro_res = self.precision.compute(predictions=predictions, references=labels, average='micro')[
            'precision']
        precision_weighted_res = self.precision.compute(predictions=predictions, references=labels, average='weighted')[
            'precision']

        return {'accuracy': accuracy_res,
                'f1_macro': f1_macro_res, 'f1_micro': f1_micro_res, 'f1_weighted': f1_weighted_res,
                'recall_macro': recall_macro_res, 'recall_micro': recall_micro_res,
                'recall_weighted': recall_weighted_res, 'precision_macro': precision_macro_res,
                'precision_micro': precision_micro_res, 'precision_weighted': precision_weighted_res,
                }

    def train_with_cross_validation(self, trial):
        config = dict(trial.params)
        config['trial.number'] = trial.number

        if self.pre_process:
            tags= ['preprocessed']
        else:
            tags = None

        wandb.init(
            project=self.wandb_project,
            group="Fine-Tuned LLM:" + self.pre_trained_model,
            tags=tags,
            reinit=True
        )

        learning_rate = trial.suggest_float('learning_rate', 1e-6, 1e-4, log=True)
        batch_size = trial.suggest_categorical('batch_size', [16, 32])
        epochs = trial.suggest_categorical('epochs', [10, 50, 100])

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

            trainer = CustomTrainer(
                weights=self.calculate_class_weights(),
                model=self.model,
                args=self.training_arguments,
                train_dataset=train_data,
                eval_dataset=validation_data,
                tokenizer=self.data_curator.tokenizer,
                data_collator=self.data_curator.data_collator,
                compute_metrics=self.compute_metrics,
            )

            trainer.train()

    def evaluate(self):
        self.model.eval()

        evaluator = evaluate.evaluator('text-classification')

        eval_results = evaluator.compute(
            model_or_pipeline=self.model,
            data=self.train_test_data['test'],
            label_mapping=self.label2id,
            tokenizer=self.data_curator.tokenizer,
        )

        eval_results_formatted = {"test/" + key: item for key, item in eval_results.items()}

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
        pre_process=args.pre_process
    )

    study = optuna.create_study(direction='maximize')
    study.optimize(train.objective, n_trials=args.n_trails)


if __name__ == '__main__':
    main()
