from datasets import Dataset
from sklearn.feature_extraction.text import CountVectorizer, TfidfVectorizer
from transformers import AutoTokenizer, DataCollatorWithPadding, AutoModel
import torch
import numpy as np


class MissingParameterError(Exception):
    def __init__(self, message):
        super().__init__(message)


class TokenizerVectorizer:

    VECTORISATION_METHODS = ['pre-trained', 'BoW', 'TfIdf']

    def __init__(self, vectorization_method, data_dir, binary=False, pre_trained_model=None):
        if vectorization_method is None:
            message = "Please supply a vectorisation method from:" + ' '.join(self.VECTORISATION_METHODS)
            raise MissingParameterError(message)

        self.data = Dataset.load_from_disk(data_dir)

        self.vectorizer_method = vectorization_method

        if vectorization_method == 'pre-trained':
            if pre_trained_model is None:
                message = "If using a pre-trained model for vectorisation, please supply"
                raise MissingParameterError(message)
            else:
                self.pre_trained_model = pre_trained_model
                self.tokenizer = AutoTokenizer.from_pretrained(pre_trained_model)

                if self.tokenizer.pad_token is None:
                    print("No PAD")
                    self.tokenizer.add_special_tokens({'pad_token': '[PAD]'})

                self.vectorizer = AutoModel.from_pretrained(self.pre_trained_model)
                self.data_collator = DataCollatorWithPadding(tokenizer=self.tokenizer)
        elif vectorization_method == 'BoW':
            self.vectorizer = CountVectorizer()
            self.vectorizer.fit_transform(self.data['text'])
        elif vectorization_method == 'TfIdf':
            self.vectorizer = TfidfVectorizer()
            self.vectorizer.fit_transform(self.data['text'])

        self.max_size = None

        if binary:
            self.data = self.data.map(self.convert_to_binary)

    def convert_to_binary(self, row):
        if row['label'] in [2, 3]:
            row['label'] = 1

        return row

    def __preprocess(self, row):
        return self.tokenizer(row['text'], truncation=True, padding=True)

    def get_pre_trained_tokenized_data(self):
        data_tokens = self.data.map(self.__preprocess)
        self.max_size = max([len(sent) for sent in data_tokens['input_ids']])
        return data_tokens

    def get_embeddings(self, data):
        if self.vectorizer_method == 'pre-trained':
            return self.__get_embeddings_pre_trained(data)
        else:
            return self.vectorizer.transform(data['text']).toarray()

    def __get_embeddings_pre_trained(self, data):
        """
        Uses the Pre-trained model to generate the code embeddings from the vectorised data

        :param data: The data to vectorise
        :return: The context embedding vectors
        """

        embeddings = []

        for row in data:
            # TODO: switch the processing embeddings to a better process (max pooling is probably the best)

            # Get first element of the tensor to get the 2D array of the embeddings
            embed = self.vectorizer(torch.tensor(row['input_ids'])[None, :])[0][0].detach().numpy()
            pad_size = self.max_size - embed.shape[0]
            pad = np.pad(embed, [(0, pad_size), (0, 0)], mode='constant')

            means = [np.mean(token_vector) for token_vector in pad]

            embeddings.append(means)

        return embeddings


if __name__ == "__main__":
    data_curator = TokenizerVectorizer(vectorization_method='TfIdf', pre_trained_model='microsoft/codebert-base',
                                       data_dir='data/code_search_net_relevance.hf', binary=True)
    # tokenized_data = data_curator.get_pre_trained_tokenized_data()
    embeddings = data_curator.get_embeddings(data_curator.data)
    print(embeddings)
    print(list(embeddings[0]))
    print(np.nonzero(embeddings[0]))