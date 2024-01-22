from functools import partial

from openai import OpenAI

import wandb
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

    def __init__(self, data_dir, wandb_project, preprocess=False):
        with open('secrets/wandb_api_key.txt') as f:
            wandb.login(key=f.read())

        self.model = 'gpt-3.5-turbo'

        self.wandb_project = wandb_project
        self.data = get_data(data_dir=data_dir, pre_process=preprocess)

        self.train_test_data = self.data.train_test_split(test_size=0.2)
        self.train_test_data['train'] = self.train_test_data['train'].map(partial(self.__create_prompt,
                                                                                  prompt=self.PROMPT))
        self.train_test_data['test'] = self.train_test_data['test'].map(partial(self.__create_prompt,
                                                                                prompt=self.PROMPT))

    def __create_prompt(self, row, prompt):
        row['prompt'] = prompt.format(nl=row['query'], code=row['func_code_string'], label=row['label'])

        return row

    def send_gpt_request(self):
        client = OpenAI()

        # TODO: How to format messages to GPT for CoT? Any examples?

        completion = client.chat.completions.create(
            model=self.model,
            messages=[
                {'role': 'system', 'content': 'You will be provided a description and a piece of code, and your task '
                                              'is to classify is between 0 and 3.'},
                {'role': 'user', 'content': self.train_test_data['train'][0]['prompt']},
                {'role': 'assistant', 'content': 'The answer is {label}'.format(label=self.train_test_data['train'][0]['label'])},
                {'role': 'user', 'content': self.train_test_data['train'][1]['prompt']},
            ]
        )

        print(completion.choices[0].message.content)


if __name__ == '__main__':
    train = Train(data_dir='data/code_search_net_relevance.hf', wandb_project='')

    train.send_gpt_request()
