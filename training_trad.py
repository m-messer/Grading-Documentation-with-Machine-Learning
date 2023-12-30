from sklearn.linear_model import LogisticRegression
from sklearn.metrics import log_loss
from sklearn.model_selection import StratifiedKFold
from sklearn.svm import SVC
from sklearn.ensemble import RandomForestClassifier

from data_curation import DataCurator
import wandb
import numpy as np
import evaluate
from random import seed
from transformers import AutoModel
import torch
import optuna


class Train:
    def __init__(self, pre_trained_model, data_dir, wandb_project, binary=False, folds=10):

        seed(100)

        with open('secrets/wandb_api_key.txt') as f:
            wandb.login(key=f.read())

        self.wandb_project = wandb_project
        self.folds = folds

        self.data_curator = DataCurator(pre_trained_model, data_dir, binary)
        self.data = self.data_curator.get_tokenized_data()
        self.train_test_data = self.data.train_test_split(test_size=0.2)

        self.model = None

        self.max_size = max([len(sent) for sent in self.data['input_ids']])

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

    def get_embeddings(self, data):
        """
        Uses the Pre-trained model to generate the code embeddings from the vectorised data

        :param data: The data to vectorise
        :return: The context embedding vectors
        """

        model = AutoModel.from_pretrained(self.data_curator.pre_trained_model)

        embeddings = []

        for row in data:
            # TODO: switch the processing embeddings to a better process (max pooling is probably the best)

            # Get first element of the tensor to get the 2D array of the embeddings
            embed = model(torch.tensor(row['input_ids'])[None, :])[0][0].detach().numpy()
            pad_size = self.max_size - embed.shape[0]
            pad = np.pad(embed, [(0, pad_size), (0, 0)], mode='constant')

            means = [np.mean(token_vector) for token_vector in pad]

            embeddings.append(means)

        return embeddings

    def train_with_cross_validation(self, trial):

        # classifier_name = trial.suggest_categorical('classifier',
        #                                             ['Bernolli', 'DecisionTree', 'KNeighbours',
        #                                              'LogisticRegression', 'RandomForest'])

        classifier_name = 'LogisticRegression'

        if classifier_name == 'LogisticRegression':
            self.model = LogisticRegression(multi_class='multinomial', class_weight='balanced')
        else:
            rf_max_depth = trial.suggest_int('rf_max_depth', 2, 32, log=True)
            self.model = RandomForestClassifier(max_depth=rf_max_depth, n_estimators=10)

        config = dict(trial.params)
        config['trial.number'] = trial.number

        wandb.init(
            project=self.wandb_project,
            config=config,
            group='Traditional_Models',
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

            X_train = self.get_embeddings(train_data)
            y = train_data['label']

            self.model.fit(X_train, y)

            X_val = self.get_embeddings(validation_data)
            metrics = self.compute_metrics(self.model.predict(X_val),
                                           self.model.predict_proba(X_val), validation_data['label'])

            eval_results_formatted = self.format_metrics(metrics, 'eval')

            print("Eval Results:")
            print(str(eval_results_formatted))
            wandb.log(eval_results_formatted)

    def evaluate(self):
        X = self.get_embeddings(self.train_test_data['test'])
        y = self.train_test_data['test']['label']

        metrics = self.compute_metrics((self.model.predict(X), y))

        eval_results_formatted = {"test/" + key: item for key, item in metrics.items()}

        print("Test Results:")
        print(str(eval_results_formatted))
        wandb.log(eval_results_formatted)
        return metrics['accuracy']

    def objective(self, trial):
        self.train_with_cross_validation(trial)
        test_acc = self.evaluate()
        return test_acc


if __name__ == '__main__':
    # As limited on CREATE compute time: set vectorisation parameter outside of optuna?
    train = Train(
        pre_trained_model='microsoft/codebert-base',
        data_dir='data/code_search_net_relevance.hf',
        binary=False,
        wandb_project='JavaDoc-Relevance-Binary-Classifier',
    )

    study = optuna.create_study(direction='maximize')
    study.optimize(train.objective)
