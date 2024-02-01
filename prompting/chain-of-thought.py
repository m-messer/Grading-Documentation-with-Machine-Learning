import csv
import os
from collections import namedtuple
from datetime import datetime
from functools import partial

import openai
from openai import OpenAI

import asyncio
import random
from data_processing.data_processor import get_data, get_label_info


class Train:
    PROMPT = 'Take the description of source code and classify if the description is relevant ' \
             'the code, by answering {labels}. For example the input is: description {nl} and the source code ' \
             '{code}, will result in {label}.'

    def __init__(self, data_dir, preprocess=False, binary=False):
        self.client = OpenAI()
        self.preprocessed = preprocess

        self.model = 'gpt-3.5-turbo'

        self.data = get_data(data_dir=data_dir, pre_process=preprocess, binary=binary)

        self.binary = binary
        self.id2label, _, _ = get_label_info(binary)
        self.labels = ', '.join(self.id2label.values())

        self.data = self.data.map(self.__convert_labels_to_words)
        self.data = self.data.map(partial(self.__create_prompt,
                                          prompt=self.PROMPT))

        self.Result = namedtuple('Result', 'epochs number_of_examples messages response label')
        self.results = []

    def __convert_labels_to_words(self, x):
        x['label'] = self.id2label[x['label']]
        return x

    def __create_prompt(self, row, prompt):
        row['prompt'] = prompt.format(nl=row['query'], code=row['func_code_string'], label=row['label'],
                                      labels=self.labels)

        return row

    def __make_messages(self, number_of_examples):
        messages = [
            {'role': 'system', 'content': 'You will be provided a description and a piece of code, '
                                          'and your task is to say if the description is relevant to '
                                          'the code, using {labels} only'.format(labels=self.labels)},
        ]

        random_sample = random.sample(range(0, self.data.shape[0]), number_of_examples + 1)
        random_sample_df = self.data.select(random_sample)

        for i in range(0, number_of_examples):
            messages.append({'role': 'user',
                             'content': random_sample_df[i]['prompt']})

        messages.append({'role': 'user',
                         'content': 'The description is {nl} and the source code is {code}.'
                        .format(nl=random_sample_df[number_of_examples]['query'],
                                code=random_sample_df[number_of_examples]['func_code_string'])})

        test_label = random_sample_df[number_of_examples]['label']
        print(messages, test_label)
        return messages, test_label

    async def send_gpt_request(self, number_of_examples):

        messages, test_label = self.__make_messages(number_of_examples=number_of_examples)

        try:
            completion = self.client.chat.completions.create(
                model=self.model,
                messages=messages
            )
        except openai.BadRequestError:
            print("Message too long")
            return None, None, None

        response = completion.choices[0].message.content
        print("RESPONSE: ", response)

        return messages, response, test_label

    async def train(self, epochs, number_of_examples):
        for i in range(0, epochs):
            messages, response, label = await self.send_gpt_request(number_of_examples=number_of_examples)
            self.results.append(self.Result(epochs=epochs, number_of_examples=number_of_examples,
                                            messages=messages, response=response, label=label))

            # Wait to not spam the GPT API with requests
            await asyncio.sleep(2)

    def save_to_csv(self):
        timestr = datetime.now().strftime("%Y%m%d-%H%M%S")

        if self.binary:
            path = '../data/cot_results_binary.csv'
        else:
            path = '../data/cot_results.csv'

        if not os.path.isfile(path):
            with open(path, 'w') as f:
                w = csv.writer(f)
                w.writerow(('time', 'epochs', 'number_of_examples', 'messages', 'response', 'label'))

        with open(path, 'a') as f:
            w = csv.writer(f)
            w.writerows([(timestr, result.epochs, result.number_of_examples, result.messages,
                          result.response, result.label) for result in self.results])

        self.results = []


if __name__ == '__main__':
    train = Train(data_dir='../data/code_search_net_relevance.hf',
                  preprocess=True, binary=False)

    asyncio.run(train.train(epochs=50, number_of_examples=3))
    train.save_to_csv()
    asyncio.run(train.train(epochs=50, number_of_examples=5))
    train.save_to_csv()
    asyncio.run(train.train(epochs=50, number_of_examples=10))
    train.save_to_csv()
    asyncio.run(train.train(epochs=50, number_of_examples=20))
    train.save_to_csv()
