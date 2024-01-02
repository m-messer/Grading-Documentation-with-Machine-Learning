import argparse

import matplotlib.pyplot as plt
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import log_loss
from sklearn.model_selection import StratifiedKFold
from sklearn.naive_bayes import BernoulliNB
from sklearn.neighbors import KNeighborsClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.tree import DecisionTreeClassifier

from tokeniser_vectorizer import TokenizerVectorizer
import wandb
import numpy as np
import evaluate
from random import seed
import optuna
import seaborn as sns
from pathlib import Path

class Train:
    ACCEPTED_MODELS = ['LogisticRegression', 'Bernolli', 'KNeighbours', 'DecisionTree', 'RandomForest']

    def __init__(self, data_dir, wandb_project, model_name, vectorisation_method, pre_trained_model=None,
                 binary=False, folds=10):

        seed(100)

        with open('secrets/wandb_api_key.txt') as f:
            wandb.login(key=f.read())

        self.wandb_project = wandb_project
        self.folds = folds
        self.model_name = model_name
        self.vectorisation_method = vectorisation_method

        self.tokenizer_vectorizer = TokenizerVectorizer(vectorization_method=vectorisation_method,
                                                        data_dir=data_dir, binary=binary,
                                                        pre_trained_model=pre_trained_model)

        if vectorisation_method == 'pre-trained':
            self.data = self.tokenizer_vectorizer.get_pre_trained_tokenized_data()
            self.train_test_data = self.data.train_test_split(test_size=0.2)
        else:
            self.train_test_data = self.tokenizer_vectorizer.data.train_test_split(test_size=0.2)

        Path('plots').mkdir(exist_ok=True)

        sns.countplot(self.train_test_data['train'].to_pandas(), x='label')
        plt.savefig('plots/train_data.pdf')
        sns.countplot(self.train_test_data['test'].to_pandas(), x='label')
        plt.savefig('plots/test_data.pdf')

        self.model = None

        if binary:
            self.labels = [0, 1]
        else:
            self.labels = [0, 1, 2, 3]

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

    def compute_metrics(self, predictions, prediction_prob, labels):

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
        loss = log_loss(y_true=labels, y_pred=prediction_prob, labels=self.labels)

        return {'accuracy': accuracy_res,
                'f1_macro': f1_macro_res, 'f1_micro': f1_micro_res, 'f1_weighted': f1_weighted_res,
                'recall_macro': recall_macro_res, 'recall_micro': recall_micro_res,
                'recall_weighted': recall_weighted_res, 'precision_macro': precision_macro_res,
                'precision_micro': precision_micro_res, 'precision_weighted': precision_weighted_res,
                'loss': loss
                }

    def train_with_cross_validation(self, trial):

        classifier_name = trial.suggest_categorical('classifier',
                                                    ['Bernolli', 'DecisionTree', 'KNeighbours',
                                                     'LogisticRegression', 'RandomForest'])

        if classifier_name == 'Bernolli':
            smoothing = trial.suggest_float('smoothing', 0, 1)
            self.model = BernoulliNB(alpha=smoothing)
        elif classifier_name == 'DecisionTree':
            dt_max_depth = trial.suggest_int('dt_max_depth', 2, 20, log=True)
            dt_min_samples_leaf = trial.suggest_int('dt_min_samples_leaf', 5, 100, log=True)
            dt_criterion = trial.suggest_categorical('dt_criterion', ['gini', 'entropy'])
            self.model = DecisionTreeClassifier(max_depth=dt_max_depth,
                                                min_samples_leaf=dt_min_samples_leaf, criterion=dt_criterion)
        elif classifier_name == 'KNeighbours':
            nn = trial.suggest_int('n_neighbours', 1, 10, log=True)
            self.model = KNeighborsClassifier(nn)
        elif classifier_name == 'LogisticRegression':
            self.model = LogisticRegression(multi_class='multinomial')
        else:
            rf_max_depth = trial.suggest_int('rf_max_depth', 2, 32, log=True)
            self.model = RandomForestClassifier(max_depth=rf_max_depth, n_estimators=10)

        config = dict(trial.params)
        config['trial.number'] = trial.number

        if self.vectorisation_method == 'pre-trained':
            tags = [self.vectorisation_method + ":" + self.tokenizer_vectorizer.pre_trained_model, self.model_name]
        else:
            tags = [self.vectorisation_method, self.model_name]

        wandb.init(
            project=self.wandb_project,
            config=config,
            group='Traditional_Models',
            tags=tags,
            reinit=True
        )

        folds = StratifiedKFold(n_splits=self.folds)

        splits = folds.split(np.zeros(self.train_test_data['train'].num_rows), self.train_test_data['train']['label'])

        split_count = 0
        for train_idxs, val_idxs in splits:
            split_count += 1
            wandb.log({'split': split_count})

            train_data = self.train_test_data['train'].select(train_idxs)
            validation_data = self.train_test_data['train'].select(val_idxs)

            X_train = self.tokenizer_vectorizer.get_embeddings(train_data)
            y = train_data['label']

            self.model.fit(X_train, y)

            X_val = self.tokenizer_vectorizer.get_embeddings(validation_data)
            metrics = self.compute_metrics(self.model.predict(X_val),
                                           self.model.predict_proba(X_val), validation_data['label'])

            eval_results_formatted = self.format_metrics(metrics, 'eval')

            print("Eval Results:")
            print(str(eval_results_formatted))
            wandb.log(eval_results_formatted)

    def evaluate(self):
        X = self.tokenizer_vectorizer.get_embeddings(self.train_test_data['test'])
        y = self.train_test_data['test']['label']

        metrics = self.compute_metrics(self.model.predict(X),
                                       self.model.predict_proba(X), y)

        eval_results_formatted = {"test/" + key: item for key, item in metrics.items()}

        print("Test Results:")
        print(str(eval_results_formatted))
        wandb.log(eval_results_formatted)
        return metrics['accuracy']

    def objective(self, trial):
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
    args = parser.parse_args()

    if args.model not in Train.ACCEPTED_MODELS:
        print('Select a model from: ' + ' '.join(Train.ACCEPTED_MODELS))
        return

    if args.vectorizer not in TokenizerVectorizer.VECTORISATION_METHODS:
        print('Select a vectorizer from: ' + ' '.join(TokenizerVectorizer.VECTORISATION_METHODS))
        return

    train = Train(
        pre_trained_model='microsoft/codebert-base',
        data_dir='data/code_search_net_relevance.hf',
        binary=False,
        wandb_project='JavaDoc-Relevance-Binary-Classifier',
        model_name=args.model,
        vectorisation_method=args.vectorizer
    )

    study = optuna.create_study(direction='maximize')
    study.optimize(train.objective)


if __name__ == '__main__':
    main()
