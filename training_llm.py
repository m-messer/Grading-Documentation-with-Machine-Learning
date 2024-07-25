import argparse
from pathlib import Path

import optuna
import pandas as pd
from matplotlib import pyplot as plt
from sampling import sample_data
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
    """
    The class used for fine-tuning existing large languge models
    """
    def __init__(self, data_dir, wandb_project, pre_trained_model, sampling_method='None',
                 binary=False, folds=10):
        """
        The constructor used to setup the HuggingFace trainer and Weights and Biases for logging.
        10-fold CV used by default.
        :param data_dir: The path of the dataset to use for training and testing
        :param wandb_project: The weights and biases project to log results to
        :param pre_trained_model: The HuggingFace model name
        :param sampling_method: Which sampling method to use when training
        :param binary: If the data should be converted to binary before training
        :param folds: The number of folds in cross-validation (default 10).
        """

        self.trainer = None
        self.training_arguments = None
        set_seed(100)

        with open('../secrets/wandb_api_key.txt') as f:
            wandb.login(key=f.read())

        self.wandb_project = wandb_project
        self.folds = folds
        self.pre_trained_model = pre_trained_model
        self.sampling_method = sampling_method

        self.tokenizer_vectorizer = TokenizerVectorizer(vectorization_method='pre-trained', data_dir=data_dir,
                                                        binary=binary, pre_trained_model=pre_trained_model)

        self.data = self.tokenizer_vectorizer.get_pre_trained_tokenized_data()

        self.train_test_data = self.data.train_test_split(test_size=0.2)

        self.train_val_X = pd.DataFrame(self.tokenizer_vectorizer.get_embeddings(self.train_test_data['train']))
        self.train_val_y = pd.DataFrame(self.train_test_data['train']['label'], columns=['label'])

        self.train_val_X, self.train_val_y = sample_data(self.train_val_X, self.train_val_y, self.sampling_method)

        Path('../plots').mkdir(exist_ok=True)

        sns.countplot(self.train_test_data['train'].to_pandas(), x='label')
        plt.savefig('plots/train_data.pdf')

        self.id2label, self.label2id, label_count = get_label_info(binary=binary)

        self.model = AutoModelForSequenceClassification.from_pretrained(pre_trained_model, num_labels=label_count,
                                                                        id2label=self.id2label, label2id=self.label2id)

        self.model.resize_token_embeddings(len(self.tokenizer_vectorizer.tokenizer))
        device = "cuda" if cuda.is_available() else "cpu"
        self.model.to(device)

    def train_with_cross_validation(self, trial):
        """
        The training loop used to fine-tune the large language model.
        :param trial: The optuna trial used for hyperparamter tuning.
        :return: None
        """
        config = dict(trial.params)
        config['trial.number'] = trial.number

        tags = [self.sampling_method]

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
            output_dir='../huggingface_models',
            learning_rate=learning_rate,
            per_device_train_batch_size=batch_size,
            per_device_eval_batch_size=batch_size,
            num_train_epochs=epochs,
            evaluation_strategy="epoch",
            save_strategy="epoch",
            load_best_model_at_end=True,
            save_total_limit=5,
            save_only_model=True,
            push_to_hub=False,
            report_to=["wandb"]
        )

        folds = StratifiedKFold(n_splits=self.folds)

        splits = folds.split(np.zeros(self.train_test_data['train'].num_rows), self.train_test_data['train']['label'])

        for train_idxs, val_idxs in splits:
            train_X = self.train_val_X.iloc[train_idxs]
            validation_X = self.train_val_X.iloc[val_idxs]

            train_y = self.train_val_y.iloc[train_idxs]['label']
            validation_y = self.train_val_y.iloc[val_idxs]['label']

            train_data = pd.concat([train_X, train_y], axis=1)
            validation_data = pd.concat([validation_X, validation_y], axis=1)

            self.trainer = Trainer(
                model=self.model,
                args=self.training_arguments,
                train_dataset=train_data,
                eval_dataset=validation_data,
                tokenizer=self.tokenizer_vectorizer.tokenizer,
                data_collator=self.tokenizer_vectorizer.data_collator,
                compute_metrics=compute_metrics,
                max_size=2048
            )

            self.trainer.train()

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
    parser.add_argument('-sampling-method', dest='sampling_method', default='None', help='The sampling method to use')
    args = parser.parse_args()

    if args.pre_trained is None:
        print("Please supply a hugging face model to fine tune, using -pre-trained")
        return

    train = Train(
        pre_trained_model=args.pre_trained,
        data_dir='../data/code_search_net_relevance.hf',
        binary=False,
        wandb_project='JavaDoc-Relevance-Classifier-Journal-CodeSearchNet',
        sampling_method=args.sampling_method
    )

    print("GPU COUNT: {}".format(torch.cuda.device_count()))
    for i in range(torch.cuda.device_count()):
        print("GPU NAME: {}".format(torch.cuda.get_device_name(i)))

    study = optuna.create_study(direction='maximize')
    study.optimize(train.objective, n_trials=args.n_trails)


if __name__ == '__main__':
    main()
