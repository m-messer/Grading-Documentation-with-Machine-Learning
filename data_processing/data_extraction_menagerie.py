import pandas as pd
from datasets import Dataset

def format_str(string):
    for char in ['\r\n', '\r', '\n']:
        string = string.replace(char, ' ')
    return string


def concat_nl_and_code(data):
    data['text'] = format_str(data['query'] + '<CODESPLIT>' + data['func_code_string'])

    return data


def main():
    df = pd.read_csv('../data/docstring_code_grades.csv', usecols=['grade_code', 'docstring', 'function'])
    df.columns = ['label', 'query', 'func_code_string']
    df = df.dropna()
    print(df.head())

    ds = Dataset.from_pandas(df)
    ds = ds.map(concat_nl_and_code)
    print(ds[0])
    print(ds)

    ds.save_to_disk("../data/menagerie.hf")

if __name__ == '__main__':
    main()