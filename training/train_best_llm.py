from pathlib import Path

from matplotlib import pyplot as plt

from data_processing.data_processor import get_label_info
from metrics import compute_metrics
from tokeniser_vectorizer import TokenizerVectorizer
from transformers import Trainer, TrainingArguments, AutoModelForSequenceClassification, set_seed
from torch import cuda
import wandb
from sklearn.model_selection import StratifiedKFold
import numpy as np
import seaborn as sns


class Train:
    def __init__(self, data_dir, wandb_project, pre_trained_model, pre_process=False,
                 binary=False, folds=10):

        self.trainer = None
        self.training_arguments = None
        set_seed(100)

        with open('../secrets/wandb_api_key.txt') as f:
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

        Path('../plots').mkdir(exist_ok=True)

        sns.countplot(self.train_test_data['train'].to_pandas(), x='label')
        plt.savefig('plots/train_data.pdf')
        sns.countplot(self.train_test_data['test'].to_pandas(), x='label')
        plt.savefig('plots/test_data.pdf')

        self.id2label, self.label2id, label_count = get_label_info(binary=binary)

        self.model = AutoModelForSequenceClassification.from_pretrained(pre_trained_model, num_labels=label_count,
                                                                        id2label=self.id2label, label2id=self.label2id)
        device = "cuda:0" if cuda.is_available() else "cpu"
        self.model.to(device)

    def train_with_cross_validation(self):

        if self.pre_process:
            tags = ['preprocessed', 'no custom weights', 'new_eval']
        else:
            tags = None

        wandb.init(
            project=self.wandb_project,
            group="Fine-Tuned LLM:" + self.pre_trained_model,
            tags=tags,
            reinit=True
        )

        learning_rate = 0.00005448171334719632
        batch_size = 16
        epochs = 50

        self.training_arguments = TrainingArguments(
            output_dir='../huggingface_models/best',
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
                model=self.model,
                args=self.training_arguments,
                train_dataset=train_data,
                eval_dataset=validation_data,
                tokenizer=self.tokenizer_vectorizer.tokenizer,
                data_collator=self.tokenizer_vectorizer.data_collator,
                compute_metrics=compute_metrics,
            )

            self.trainer.train()

    def evaluate(self):
        self.model.eval()

        model_predictions = self.trainer.predict(self.train_test_data['test'])
        print(model_predictions.metrics)

        eval_results_formatted = \
            {"test/" + key.split('_', 1)[1]: item for key, item in model_predictions.metrics.items()}

        print("Test Results:")
        print(str(eval_results_formatted))
        wandb.log(eval_results_formatted)
        return eval_results_formatted['test/accuracy']

    def save(self):
        self.trainer.save_model('models/best')


def main():
    train = Train(
        pre_trained_model='microsoft/codebert-base',
        data_dir='../data/code_search_net_relevance.hf',
        binary=False,
        wandb_project='JavaDoc-Relevance-Binary-Classifier',
        pre_process=True,
    )

    train.train_with_cross_validation()
    train.evaluate()
    train.save()


if __name__ == '__main__':
    main()
