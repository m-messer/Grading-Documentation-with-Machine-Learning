import argparse
from pathlib import Path

import optuna
from datasets import DatasetDict
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
import data_processing.data_processor


class Train:
    """
    The class used for fine-tuning existing large languge models
    """
    def __init__(self, data_dir, wandb_project, pre_trained_model, pre_process=False,
                 binary=False, folds=10):
        """
        The constructor used to setup the HuggingFace trainer and Weights and Biases for logging.
        10-fold CV used by default.
        :param data_dir: The path of the dataset to use for training and testing
        :param wandb_project: The weights and biases project to log results to
        :param pre_trained_model: The HuggingFace model name
        :param pre_process: If the data should be pre-processed before training
        :param binary: If the data should be converted to binary before training
        :param folds: The number of folds in cross-validation (default 10).
        """

        self.trainer = None
        self.training_arguments = None
        set_seed(100)

        wandb.login()

        self.wandb_project = wandb_project
        self.folds = folds
        self.pre_trained_model = pre_trained_model
        self.pre_process = pre_process

        self.tokenizer_vectorizer = TokenizerVectorizer(vectorization_method='pre-trained', data_dir=data_dir,
                                                        binary=binary, pre_trained_model=pre_trained_model)

        self.data = self.tokenizer_vectorizer.get_pre_trained_tokenized_data()

        self.train_test_data = self.data.train_test_split(test_size=0.2)

        if pre_process:
            old_test_class_count = self.train_test_data['test'].to_pandas()['label'].value_counts()
            print("BEFORE OVERSAMPLE")

            print(old_test_class_count)
            self.train_test_data = data_processing.data_processor.over_sample(self.train_test_data)
            print('OVER SAMPLE DATA')
            print(self.train_test_data)

            print(self.train_test_data['test'].to_pandas()['label'].value_counts())

            new_test_class_count = self.train_test_data['test'].to_pandas()['label'].value_counts()
            print("AFTER OVERSAMPLE")
            print(new_test_class_count)

            self.id2label, self.label2id, label_count = get_label_info(binary=binary)

        self.model = AutoModelForSequenceClassification.from_pretrained(pre_trained_model, num_labels=label_count,
                                                                        id2label=self.id2label, label2id=self.label2id)
        device = "cuda:0" if cuda.is_available() else "cpu"
        self.model.to(device)

    def train_with_cross_validation(self, trial):
        """
        The training loop used to fine-tune the large language model.
        :param trial: The optuna trial used for hyperparamter tuning.
        :return: None
        """
        config = dict(trial.params)
        config['trial.number'] = trial.number

        if self.pre_process:
            tags = ['preprocessed', 'binary']
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
        epochs = trial.suggest_categorical('epochs', [10, 50, 75])

        self.training_arguments = TrainingArguments(
            output_dir='../huggingface_models',
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

        self.trainer.save_model('best_model')

    def evaluate(self):
        """
        Generates metric results from a withheld test set and the fine-tuned models predictions
        :return: The test accuracy
        """
        self.model.eval()

        model_predictions = self.trainer.predict(self.train_test_data['test'])
        print(model_predictions.metrics)

        eval_results_formatted = \
            {"test/" + key.split('_', 1)[1]: item for key, item in model_predictions.metrics.items()}

        print("Test Results:")
        print(str(eval_results_formatted))
        wandb.log(eval_results_formatted)
        return eval_results_formatted['test/accuracy']

    def objective(self, trial):
        """
        The objective function for Optuna Hyperparameter search
        :param trial: The Optuna trial for hyperparameter tuning
        :return: The test accuracy
        """
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
        binary=True,
        wandb_project='JavaDoc-Relevance-Classifier-Validation',
        pre_process=args.pre_process,
    )

    study = optuna.create_study(direction='maximize')
    study.optimize(train.objective, n_trials=args.n_trails)

    wandb.finish()


if __name__ == '__main__':
    main()
