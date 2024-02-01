import csv
import os
from collections import namedtuple
from datetime import datetime

import openai
from openai import OpenAI

import asyncio
import random
from data_processing.data_processor import get_data, get_label_info


class Train:
    def __init__(self, data_dir, preprocess=False, binary=False):
        self.client = OpenAI()
        self.preprocessed = preprocess

        self.model = 'gpt-3.5-turbo'

        self.data = get_data(data_dir=data_dir, pre_process=preprocess, binary=binary)

        self.binary = binary
        self.id2label, _, _ = get_label_info(binary)
        self.labels = ', '.join(self.id2label.values())

        self.data = self.data.map(self.__convert_labels_to_words)

        self.Result = namedtuple('Result', 'epochs messages response label')
        self.results = []

    def __convert_labels_to_words(self, x):
        x['label'] = self.id2label[x['label']]
        return x

    def __make_messages(self, rand_input):
        messages = [{'role': 'system', 'content': 'You will be provided a description and a piece of code, '
                                                  'and your task is to say if the description is relevant to '
                                                  'the code, using {labels} only'.format(labels=self.labels)},
                    {'role': 'user',
                     'content': 'The description is {nl} and the source code is {code}.'
                     .format(nl=rand_input['query'],
                             code=rand_input['func_code_string'])}]

        return messages

    async def send_gpt_request(self, rand_input):
        print(rand_input)

        messages = self.__make_messages(rand_input=rand_input)

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

        return messages, response, rand_input['label']

    async def train(self, epochs):
        for i in range(0, epochs):
            random_sample = random.sample(range(0, self.data.shape[0]), 1)
            messages, response, label = await self.send_gpt_request(rand_input=self.data[random_sample])
            self.results.append(self.Result(epochs=epochs, messages=messages, response=response, label=label))

            # Wait to not spam the GPT API with requests
            await asyncio.sleep(2)

    def save_to_csv(self):
        timestr = datetime.now().strftime("%Y%m%d-%H%M%S")

        if self.binary:
            path = '../data/standard_prompting_results_binary.csv'
        else:
            path = '../data/standard_prompting_results.csv'

        if not os.path.isfile(path):
            with open(path, 'w') as f:
                w = csv.writer(f)
                w.writerow(('time', 'epochs', 'messages', 'response', 'label'))

        with open(path, 'a') as f:
            w = csv.writer(f)
            w.writerows([(timestr, result.epochs, result.messages,
                          result.response, result.label) for result in self.results])

        self.results = []


if __name__ == '__main__':
    train = Train(data_dir='../data/code_search_net_relevance.hf',
                  preprocess=True, binary=False)

    asyncio.run(train.train(epochs=50))
    train.save_to_csv()
