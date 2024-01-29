from functools import partial

import optuna
from openai import OpenAI

import asyncio
import wandb
import random
from data_processing.data_processor import get_data
from training.metrics import compute_metrics_cot

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

    def __init__(self, data_dir, wandb_project, preprocess=False, number_of_examples=5, epochs=10):
        with open('../secrets/wandb_api_key.txt') as f:
            wandb.login(key=f.read())

        self.client = OpenAI()

        self.model = 'gpt-3.5-turbo'
        self.number_of_examples = number_of_examples
        self.epochs = epochs

        self.wandb_project = wandb_project
        self.data = get_data(data_dir=data_dir, pre_process=preprocess)
        self.data = self.data.map(partial(self.__create_prompt,
                                          prompt=self.PROMPT))

        self.predictions = []
        self.labels = []

    def __create_prompt(self, row, prompt):
        row['prompt'] = prompt.format(nl=row['query'], code=row['func_code_string'], label=row['label'])

        return row

    def __make_messages(self):
        messages = [
            {'role': 'system', 'content': 'You will be provided a description and a piece of code, and your task '
                                          'is to classify is between 0 and 3.'},
        ]

        random_sample = random.sample(range(0, self.data.shape[0]), self.number_of_examples + 1)
        random_sample_df = self.data.select(random_sample)

        for i in range(0, self.number_of_examples):
            messages.append({'role': 'user',
                             'content': random_sample_df[i]['prompt'] + 'The answer is ' +
                                        str(random_sample_df[i]['label'])})

        messages.append({'role': 'user',
                         'content': random_sample_df[self.number_of_examples]['prompt']})

        test_label = random_sample_df[self.number_of_examples]['label']

        return messages, test_label

    def __format_results(self, response, label):
        pred = [int(s) for s in response.replace('.', '').split() if s.isdigit()][0]
        self.predictions.append(pred)
        self.labels.append(label)

    async def send_gpt_request(self):

        messages, test_label = self.__make_messages()

        completion = self.client.chat.completions.create(
            model=self.model,
            messages=messages
        )

        response = completion.choices[0].message.content

        self.__format_results(response, test_label)

    async def train(self, trial):
        tags = ['DEV']

        number_of_examples = trial.suggest_categorical('number_of_examples', [3, 5, 10])
        self.number_of_examples = number_of_examples

        config = dict(trial.params)
        config['trial.number'] = trial.number
        config['epochs'] = self.epochs

        wandb.init(
            project=self.wandb_project,
            group="CoT:" + self.model,
            tags=tags,
            reinit=True,
            config=config
        )

        for i in range(0, self.epochs):
            await self.send_gpt_request()

            metrics = compute_metrics_cot(self.predictions, self.labels)
            results_formatted = {"test/" + key: item for key, item in metrics.items()}
            results_formatted['epoch'] = i
            print(results_formatted)
            wandb.log(results_formatted)

            # Wait to not spam the GPT API with requests
            await asyncio.sleep(10)

        return metrics['accuracy']
    def objective(self, trial):
        return asyncio.run(train.train(trial))

if __name__ == '__main__':
    train = Train(data_dir='../data/code_search_net_relevance.hf', wandb_project='JavaDoc-Relevance-Binary-Classifier')

    study = optuna.create_study(direction='maximize')
    study.optimize(train.objective, n_trials=5)


