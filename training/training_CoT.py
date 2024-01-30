import csv
from collections import namedtuple
from datetime import datetime
from functools import partial

import openai
from openai import OpenAI

import asyncio
import random
from data_processing.data_processor import get_data

"""
TODO:
 - Run training set prompts
 - Run test prompts and save output
 - Process output to get labels
 - Calculate test metrics
 - Log to WandB
 - Create more than one prompt??
 - Use more than one model??
"""


class Train:
    PROMPT = 'Take the description "{nl}" and the code "{code}" and classify how relevant the description is to ' \
             'the code between 0 and 3.'

    def __init__(self, data_dir, preprocess=False):
        self.client = OpenAI()
        self.preprocessed = preprocess

        self.model = 'gpt-3.5-turbo'

        self.data = get_data(data_dir=data_dir, pre_process=preprocess)
        self.data = self.data.map(partial(self.__create_prompt,
                                          prompt=self.PROMPT))

        self.Result = namedtuple('Result', 'epochs number_of_examples messages response label')
        self.results = []

    def __create_prompt(self, row, prompt):
        row['prompt'] = prompt.format(nl=row['query'], code=row['func_code_string'], label=row['label'])

        return row

    def __make_messages(self, number_of_examples):
        messages = [
            {'role': 'system', 'content': 'You will be provided a description and a piece of code, and your task '
                                          'is to classify is between 0 and 3.'},
        ]

        random_sample = random.sample(range(0, self.data.shape[0]), number_of_examples + 1)
        random_sample_df = self.data.select(random_sample)

        for i in range(0, number_of_examples):
            messages.append({'role': 'user',
                             'content': random_sample_df[i]['prompt'] + "Let's think step by step"})

        messages.append({'role': 'user',
                         'content': random_sample_df[number_of_examples]['prompt']})

        test_label = random_sample_df[number_of_examples]['label']

        return messages, test_label

    async def send_gpt_request(self, number_of_examples):

        messages, test_label = self.__make_messages(number_of_examples=number_of_examples)

        try:
            completion = self.client.chat.completions.create(
                model=self.model,
                messages=messages
            )
        except openai.BadRequestError:
            print("Message two long")

        response = completion.choices[0].message.content
        print("RESPONSE: ", response)

        return messages, response, test_label

    async def train(self, epochs, number_of_examples):
        for i in range(0, epochs):
            messages, response, label = await self.send_gpt_request(number_of_examples=number_of_examples)
            self.results.append(self.Result(epochs=epochs, number_of_examples=number_of_examples,
                                            messages=messages, response=response, label=label))

            # Wait to not spam the GPT API with requests
            await asyncio.sleep(10)

    def save_to_csv(self):
        timestr = datetime.now().strftime("%Y%m%d-%H%M%S")

        with open('../data/cot_results.csv', 'a') as f:
            w = csv.writer(f)
            w.writerow(('time', 'epochs', 'number_of_examples', 'messages', 'response', 'label'))    # field header
            w.writerows([(timestr, result.epochs, result.number_of_examples, result.messages,
                          result.response, result.label) for result in self.results])


if __name__ == '__main__':
    train = Train(data_dir='../data/code_search_net_relevance.hf',
                  preprocess=True)

    asyncio.run(train.train(epochs=50, number_of_examples=3))
    asyncio.run(train.train(epochs=50, number_of_examples=5))
    asyncio.run(train.train(epochs=50, number_of_examples=10))
    train.save_to_csv()


