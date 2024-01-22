from functools import partial

import wandb
from data_processor import get_data

"""
TODO:
 - Gain access to model GPT - API? Or local??
 - Run training set prompts
 - Run test prompts and save output
 - Process output to get labels
 - Calculate test metrics
 - Log to WandB
 - Create more than one prompt??
"""


class Train:
    TRAIN_PROMPT = "{nl} - {code} - {label}"
    TEST_PROMPT = "{nl} - {code}"

    def __init__(self, data_dir, wandb_project, preprocess=False):
        with open('secrets/wandb_api_key.txt') as f:
            wandb.login(key=f.read())

        self.wandb_project = wandb_project
        self.data = get_data(data_dir=data_dir, pre_process=preprocess)

        self.train_test_data = self.data.train_test_split(test_size=0.2)
        self.train_test_data['train'] = self.train_test_data['train'].map(partial(self.__create_prompt,
                                                                                  prompt=self.TRAIN_PROMPT))
        self.train_test_data['test'] = self.train_test_data['test'].map(partial(self.__create_prompt,
                                                                                prompt=self.TEST_PROMPT))

        print(self.train_test_data['train'][0]['prompt'])
        print(self.train_test_data['test'][0]['prompt'])

    def __create_prompt(self, row, prompt):
        row['prompt'] = prompt.format(nl=row['query'], code=row['func_code_string'], label=row['label'])

        return row


if __name__ == '__main__':
    train = Train(data_dir='data/code_search_net_relevance.hf', wandb_project='')
