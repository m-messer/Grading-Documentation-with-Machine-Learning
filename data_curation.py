from datasets import Dataset
from transformers import AutoTokenizer, DataCollatorWithPadding


class DataCurator:

    def __init__(self, pre_trained_model, data_dir, binary=False):
        self.pre_trained_model = pre_trained_model
        self.tokenizer = AutoTokenizer.from_pretrained(pre_trained_model)
        self.data = Dataset.load_from_disk(data_dir)
        self.data_collator = DataCollatorWithPadding(tokenizer=self.tokenizer)
        self.binary = binary

        if self.binary:
            self.data = self.data.map(self.convert_to_binary)

    def convert_to_binary(self, row):
        if row['label'] in [2, 3]:
            row['label'] = 1

        return row

    def preprocess(self, row):
        return self.tokenizer(row['text'], truncation=True, padding=True)

    def get_tokenized_data(self):
        data_tokens = self.data.map(self.preprocess)
        data_tokens = data_tokens.remove_columns(['func_code_string', 'func_code_tokens',
                                                  'func_documentation_string', 'func_documentation_tokens'])
        return data_tokens


if __name__ == "__main__":
    data_curator = DataCurator('microsoft/codebert-base', 'data/code_search_net_relevance.hf', True)
    tokenized_data = data_curator.get_tokenized_data()
    print(tokenized_data)
    print(tokenized_data[0])