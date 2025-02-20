import argparse
import pickle
from copy import deepcopy
from pathlib import Path
from random import seed

import matplotlib.pyplot as plt
import numpy as np
import optuna
import seaborn as sns
from sklearn.ensemble import RandomForestClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import StratifiedKFold
from sklearn.naive_bayes import BernoulliNB
from sklearn.neighbors import KNeighborsClassifier
from sklearn.tree import DecisionTreeClassifier

import wandb

from data_processing.data_processor import over_sample
from metrics import compute_metrics_trad, format_metrics
from tokeniser_vectorizer import TokenizerVectorizer


class Train:
    """
    The class used for training using traditional approaches
    """
    ACCEPTED_MODELS = ['LogisticRegression', 'Bernolli', 'KNeighbours', 'DecisionTree', 'RandomForest']
    EMBEDDINGS_DICT = {
        'CodeSearchNet': {
            'bert-base-uncased': 'data/CodeSearchNet_bert-base-uncased_embeddings.hf',
            'microsoft/codebert-base': 'data/CodeSearchNet_microsoft_codebert-base_embeddings.hf',
            None: 'data/code_search_net_relevance.hf'
        },
        'Menagerie': {
            'bert-base-uncased': 'data/Menagerie_bert-base-uncased_embeddings.hf',
            'microsoft/codebert-base': 'data/Menagerie_microsoft_codebert-base_embeddings.hf',
            None: 'data/menagerie_unique_pairs.csv'
        },
        'Menagerie-Truncated': {
            'bert-base-uncased': 'data/Menagerie-Truncated_bert-base-uncased_embeddings.hf',
            'microsoft/codebert-base': 'data/Menagerie-Truncated_microsoft_codebert-base_embeddings.hf',
            None: 'data/menagerie_unique_pairs.csv'
        }
    }

    def __init__(self, dataset_name, wandb_project, model_name, vectorisation_method, pre_trained_model=None,
                 binary=False, folds=10, pre_process=False):
        """
        Sets up the training loop, and Weights and Biases logging.
        :param dataset_name: The name of the dataset to use for training and testing.
        :param wandb_project: The weights and biases project to log results to
        :param model_name: The model name from: 'LogisticRegression', 'Bernolli', 'KNeighbours', 'DecisionTree',
        'RandomForest'
        :param vectorisation_method: The vectorisation method to be used: ['pre-trained', 'BoW', 'TfIdf']
        :param pre_trained_model: If pre-trained vectorisation is used, the HuggingFace model
        :param binary: If the dataset should be convert to binary before training
        :param folds: The number of folds in cross-validation (default 10).
        :param pre_process: If the data should be pre-processed before training
        """

        self.dataset_name = dataset_name
        self.best_accuracy = 0
        seed(100)

        print('Setup WandB')
        wandb.login()

        self.wandb_project = wandb_project
        self.folds = folds
        self.model_name = model_name
        self.vectorisation_method = vectorisation_method
        self.pre_process = pre_process

        if dataset_name not in self.EMBEDDINGS_DICT:
            print('Unknown Dataset')
            raise FileNotFoundError('Unknown Dataset')

        if pre_trained_model is not None and pre_trained_model not in self.EMBEDDINGS_DICT[dataset_name]:
            print('Unknown Pre-Trained Model')
            raise FileNotFoundError('Unknown Pre-Trained Model')

        data_dir = self.EMBEDDINGS_DICT[dataset_name][pre_trained_model]

        self.tokenizer_vectorizer = TokenizerVectorizer(vectorization_method=vectorisation_method,
                                                        data_dir=data_dir, binary=binary,
                                                        pre_trained_model=pre_trained_model)


        if vectorisation_method == 'pre-trained':
            self.data = self.tokenizer_vectorizer.get_pre_trained_tokenized_data()
        else:
            self.data = self.tokenizer_vectorizer.data

        if pre_process:
            print('Pre-Processing')
            self.data = self.data.class_encode_column("label")
            self.data.to_csv('data/raw.csv')
            self.train_test_data = self.data.train_test_split(test_size=0.2)
            print(self.train_test_data)
            self.train_test_data['train'] = over_sample(self.train_test_data['train'], dataset_name, binary)
            print('OVER SAMPLE DATA')
            print(self.train_test_data)

            self.train_test_data['train'].to_csv('data/proc_train.csv')
            self.train_test_data['test'].to_csv('data/proc_test.csv')


            if dataset_name == 'Menagerie' or dataset_name == 'Menagerie-Truncated':
                print(self.train_test_data['test'].to_pandas()['grade'].value_counts())
            else:
                print(self.train_test_data['test'].to_pandas()['label'].value_counts())

        self.model = None

        if binary:
            self.labels = [0, 1]
        else:
            self.labels = [0, 1, 2, 3]

    def train_with_cross_validation(self, trial):
        """
        The training loop used to train the traditional models
        :param trial: The optuna trial used for hyperparamter tuning.
        :return: None
        """

        print('Training with cross validation')

        if self.model_name == 'Bernolli':
            smoothing = trial.suggest_float('smoothing', 0, 1)
            self.model = BernoulliNB(alpha=smoothing)
        elif self.model_name == 'DecisionTree':
            dt_max_depth = trial.suggest_int('dt_max_depth', 2, 20, log=True)
            dt_min_samples_leaf = trial.suggest_int('dt_min_samples_leaf', 5, 100, log=True)
            dt_criterion = trial.suggest_categorical('dt_criterion', ['gini', 'entropy'])
            self.model = DecisionTreeClassifier(max_depth=dt_max_depth,
                                                min_samples_leaf=dt_min_samples_leaf, criterion=dt_criterion)
        elif self.model_name == 'KNeighbours':
            nn = trial.suggest_int('n_neighbours', 1, 10, log=True)
            self.model = KNeighborsClassifier(nn)
        elif self.model_name == 'LogisticRegression':
            self.model = LogisticRegression(multi_class='multinomial')
        else:
            rf_max_depth = trial.suggest_int('rf_max_depth', 2, 32, log=True)
            self.model = RandomForestClassifier(max_depth=rf_max_depth, n_estimators=10)

        config = dict(trial.params)
        config['trial.number'] = trial.number

        tags = [self.vectorisation_method]

        if self.vectorisation_method == 'pre-trained':
            tags = [self.vectorisation_method + ":" + self.tokenizer_vectorizer.pre_trained_model]

        if self.pre_process:
            tags.append('preprocessed')

        tags.append(f'folds:{self.folds}')

        wandb.init(
            project=self.wandb_project,
            config=config,
            group=self.model_name,
            tags=tags,
            reinit=True
        )

        # Generates eval dataset using K-Fold Cross Validation
        folds = StratifiedKFold(n_splits=self.folds)

        splits = folds.split(np.zeros(self.train_test_data['train'].num_rows), self.train_test_data['train']['label'])

        split_count = 0
        for train_idxs, val_idxs in splits:
            split_count += 1
            wandb.log({'split': split_count})

            train_data = self.train_test_data['train'].select(train_idxs)
            validation_data = self.train_test_data['train'].select(val_idxs)

            if self.vectorisation_method != 'pre-trained':
                X_train = self.tokenizer_vectorizer.get_embeddings(train_data)
            else:
                X_train = train_data['embed']
            y = train_data['label']

            self.model.fit(X_train, y)

            if self.vectorisation_method != 'pre-trained':
                X_val = self.tokenizer_vectorizer.get_embeddings(validation_data)
            else:
                X_val = validation_data['embed']
            metrics = compute_metrics_trad(self.model.predict(X_val),
                                           self.model.predict_proba(X_val), validation_data['label'])

            eval_results_formatted = format_metrics(metrics, 'eval')

            print("Eval Results:")
            print(str(eval_results_formatted))
            wandb.log(eval_results_formatted)

    def evaluate(self):
        """
       Generates metric results from a withheld test set and the fine-tuned models predictions
       :return: The test accuracy
       """
        if self.vectorisation_method != 'pre-trained':
            X = self.tokenizer_vectorizer.get_embeddings(self.train_test_data['test'])
        else:
            X = self.train_test_data['test']['embed']
        y = self.train_test_data['test']['label']

        print("Test Data")
        print(self.train_test_data['test'].to_pandas()['label'].value_counts())

        metrics = compute_metrics_trad(self.model.predict(X),
                                       self.model.predict_proba(X), y)

        eval_results_formatted = {"test/" + key: item for key, item in metrics.items()}

        print("Test Results:")
        print(str(eval_results_formatted))
        wandb.log(eval_results_formatted)

        if eval_results_formatted['test/accuracy'] > self.best_accuracy:
            self.best_accuracy = eval_results_formatted['test/accuracy']
            print('Saving best model as pickle')
            with open(f"models/{wandb.run.name}_{self.dataset_name}_{self.vectorisation_method}_{self.model_name}.pkl", "wb") as f:
                pickle.dump(self.model, f)

        return metrics['accuracy']

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
    parser = argparse.ArgumentParser(description='Train Traditional Models')
    parser.add_argument('-model', dest='model', required=True,
                        help='Use -model to select a model from: ' + ' '.join(Train.ACCEPTED_MODELS))
    parser.add_argument('-vectorizer', dest='vectorizer', required=True,
                        help='Use -vectorizer to select a vectorizer from: ' +
                             ' '.join(TokenizerVectorizer.VECTORISATION_METHODS))
    parser.add_argument('-n_trails', dest='n_trails', default=10, type=int, help='The number of Optuna trials')
    parser.add_argument('-pre-trained', dest='pre_trained', default=None, help='A HuggingFace model for vectorisation')
    parser.add_argument('-pre-process', dest='pre_process', default=False, help='Run preprocessing steps',
                        action='store_true')
    parser.add_argument('-dataset', dest='dataset', default='CodeSearchNet', help='The dataset to use for training and evaluation')
    parser.add_argument('-folds', dest='folds', default=10, type=int, help='The number of folds for cross-validation')
    args = parser.parse_args()

    if args.model not in Train.ACCEPTED_MODELS:
        print('Select a model from: ' + ' '.join(Train.ACCEPTED_MODELS))
        return

    if args.vectorizer not in TokenizerVectorizer.VECTORISATION_METHODS:
        print('Select a vectorizer from: ' + ' '.join(TokenizerVectorizer.VECTORISATION_METHODS))
        return

    if args.vectorizer == 'pre-trained' and args.pre_trained is None:
        print("Provided a pre-trained HuggingFace model for vectorisation")
        return

    if args.dataset == 'CodeSearchNet':
        wandb_project = 'JavaDoc-Relevance-Classifier-Renewed'
    elif args.dataset == 'Menagerie':
        wandb_project = 'JavaDoc-Relevance-Classifier-Menagerie'
    elif args.dataset == 'Menagerie-Truncated':
        wandb_project = 'JavaDoc-Relevance-Classifier-Menagerie-Truncated'
    else:
        print('Select a dataset from: ' + ' '.join(['CodeSearchNet', 'Menagerie', 'Menagerie-Truncated']))
        return

    print('Creating Train object')
    train = Train(
        pre_trained_model=args.pre_trained,
        dataset_name=args.dataset,
        binary=False,
        wandb_project=wandb_project,
        model_name=args.model,
        vectorisation_method=args.vectorizer,
        pre_process=args.pre_process,
        folds=args.folds,
    )

    print('Creating and running study')

    study = optuna.create_study(direction='maximize')
    study.optimize(train.objective, n_trials=args.n_trails)

    print('Tidy up')

    wandb.finish()


if __name__ == '__main__':
    main()
