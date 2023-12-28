from sklearn.metrics import log_loss

from data_curation import DataCurator
import wandb
import numpy as np
import evaluate
from random import seed
from sklearn.linear_model import LogisticRegressionCV
from transformers import AutoModel
import torch
import torch.nn as nn


class Train:
    def __init__(self, pre_trained_model, data_dir, wandb_project, output_dir, learning_rate, batch_size,
                 epochs, weight_decay, binary=False):

        seed(100)

        with open('secrets/wandb_api_key.txt') as f:
            wandb.login(key=f.read())

            wandb.init(
                project=wandb_project
            )

        self.data_curator = DataCurator(pre_trained_model, data_dir, binary)
        self.data = self.data_curator.get_tokenized_data()
        self.train_test_data = self.data.train_test_split(test_size=0.2)

        self.model = None

        if binary:
            self.id2label = {0: 'irrelevant', 1: 'relevant'}
            self.label2id = {'irrelevant': 0, 'relevant': 1}

        self.f1 = evaluate.load('f1')
        self.accuracy = evaluate.load('accuracy')
        self.recall = evaluate.load('recall')
        self.precision = evaluate.load('precision')

        # TODO: Log config with wandb

    def calculate_class_weights(self):
        class_count = self.data.to_pandas().groupby('label').count()['input_ids'].to_list()
        total = sum(class_count)
        return [1 - (val / total) for val in class_count]

    def compute_metrics(self, eval_pred):
        predictions, labels = eval_pred

        accuracy_res = self.accuracy.compute(predictions=predictions, references=labels)
        f1_macro_res = self.f1.compute(predictions=predictions, references=labels, average='macro')
        f1_micro_res = self.f1.compute(predictions=predictions, references=labels, average='micro')
        f1_weighted_res = self.f1.compute(predictions=predictions, references=labels, average='weighted')
        recall_macro_res = self.recall.compute(predictions=predictions, references=labels, average='macro')
        recall_micro_res = self.recall.compute(predictions=predictions, references=labels, average='micro')
        recall_weighted_res = self.recall.compute(predictions=predictions, references=labels, average='weighted')
        precision_macro_res = self.precision.compute(predictions=predictions, references=labels, average='macro')
        precision_micro_res = self.precision.compute(predictions=predictions, references=labels, average='micro')
        precision_weighted_res = self.precision.compute(predictions=predictions, references=labels, average='weighted')
        loss = log_loss(y_true=labels, y_pred=predictions)

        return {'accuracy': accuracy_res,
                'f1_macro': f1_macro_res, 'f1_micro': f1_micro_res, 'f1_weighted': f1_weighted_res,
                'recall_macro': recall_macro_res, 'recall_micro': recall_micro_res,
                'recall_weighted': recall_weighted_res, 'precision_macro': precision_macro_res,
                'precision_micro': precision_micro_res, 'precision_weighted': precision_weighted_res,
                'loss': loss
                }

    def get_embeddings(self, split: str):
        """
        Uses the Pre-trained model to generate the code embeddings from the vectorised data

        :param split: The split of the dataset to do
        :return: The context embedding vectors
        """

        model = AutoModel.from_pretrained(self.data_curator.pre_trained_model)
        data = self.train_test_data[split]

        max_size = max([len(sent) for sent in data['input_ids']])
        print(max_size)

        embeddings = []

        for row in data:
            # TODO: switch the processing embeddings to a better process (max pooling is probably the best)

            # Get first element of the tensor to get the 2D array of the embeddings
            embed = model(torch.tensor(row['input_ids'])[None, :])[0][0].detach().numpy()
            pad_size = max_size - embed.shape[0]
            pad = np.pad(embed, [(0, pad_size), (0, 0)], mode='constant')

            means = [np.mean(token_vector) for token_vector in pad]

            embeddings.append(means)

        return embeddings

    def train_with_cross_validation(self, number_splits):
        # TODO: Implement Straified CV, add wandb logging, loss function?

        X = self.get_embeddings('train')

        self.model = LogisticRegressionCV(cv=number_splits, max_iter=500)
        y = self.train_test_data['train']['label']

        self.model.fit(X, y)
        metrics = self.compute_metrics((self.model.predict(X), y))

        eval_results_formatted = {"eval/" + key: item for key, item in metrics.items()}

        print("Eval Results:")
        print(str(eval_results_formatted))
        wandb.log(eval_results_formatted)

    def evaluate(self):
        X = self.get_embeddings('test')
        y = self.train_test_data['test']['label']

        metrics = self.compute_metrics((self.model.predict(X), y))

        eval_results_formatted = {"test/" + key: item for key, item in metrics.items()}

        print("Test Results:")
        print(str(eval_results_formatted))
        wandb.log(eval_results_formatted)


if __name__ == '__main__':
    train = Train(
        pre_trained_model='microsoft/codebert-base',
        output_dir='m-messer/JavaDoc_Code_Relevance_Classifier',
        batch_size=8,
        data_dir='data/code_search_net_relevance.hf',
        epochs=100,
        weight_decay=0.01,
        binary=True,
        wandb_project='JavaDoc-Relevance-Binary-Classifier',
        learning_rate=2e-5
    )
    train.train_with_cross_validation(10)
    train.evaluate()
