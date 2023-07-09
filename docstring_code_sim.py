import pandas as pd
from huggingface_hub import login
from datasets import load_dataset
from transformers import RobertaTokenizer, RobertaModel
import torch.nn as nn
import torch.nn.functional as F
import torch
import re


# # JavaDoc-Code Similarity
# ### Login to Huggingface

NUMBER_OF_CLASSES = 10
MAX_TENSOR_SIZE = 514


def main():
    with open('secrets/hugging_face_key.txt') as f:
        login(f.read())


# ### Load dataset
    ds = load_dataset("bigcode/starcoderdata", data_dir="java", split="train", streaming=True)
    ds_filtered = ds.filter(lambda s: s['max_stars_count'] > 1000)

    df = pd.DataFrame()

    for i, row in enumerate(iter(ds_filtered)):
        if not is_ascii(row['content']):
            continue

        proc_df = preprocess(row)
        df = pd.concat([df, proc_df], ignore_index=True)

        if i == NUMBER_OF_CLASSES:
            break

    df.head()
    df.to_csv('data/processed.csv')

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    bert_tokenizer = RobertaTokenizer.from_pretrained("microsoft/codebert-base")
    bert_model = RobertaModel.from_pretrained("microsoft/codebert-base")
    bert_model.to(device)

    results_df = df.copy(deep=True)

    results_df['sim'] = df.apply(lambda x: get_cosine_sim(x['docstring'], x['code'],
                                                          tokenizer=bert_tokenizer, model=bert_model), axis=1)
    results_df.head()

    print("Total number of docstring/code pairs", results_df.size)
    print("Number of failed embeddings: ", results_df.sim.isna().sum())
    print("Average docstring length: ", results_df.docstring.apply(len).mean())
    print("Average code length:", results_df.code.apply(len).mean())
    print("Average sim score:", results_df.sim.mean())
    print("Average number of max stars", results_df.stars.mean())

    results_df.loc[results_df.sim.notnull()].reset_index(drop=True).to_csv('data/sim.csv')


def preprocess(row):
    # Remove licence, imports and packages
    preproc_row = re.split(r'import [A-Za-z.]*;', row['content'])

    if len(preproc_row) >= 1:
        content = preproc_row[len(preproc_row) - 1]

        # Remove JavaDoc tags and compiler annotations
        # content = re.sub(r'\{?@.*', '<annotation>', content)

        class_level = re.split(r'\*/', content, 1)

        if len(class_level) > 1:
            function_level = re.split(r'/\*\*', class_level[1].strip()[:-1])
        else:
            function_level = re.split(r'/\*\*', class_level[0].strip()[:-1])

        docstrings = []
        codes = []

        for func in function_level:
            doc_code = func.split('*/')

            if len(doc_code) <= 1:
                continue

            docstring = doc_code[0].replace('*', '').strip()
            docstrings.append(docstring)

            code = doc_code[1].strip()
            codes.append(code)

        return pd.DataFrame({"docstring": docstrings, "code": codes, 'stars': row['max_stars_count'], 'repo': row['max_stars_repo_name']})

def is_ascii(s):
    return all(ord(c) < 128 for c in s)

def get_average_embeddings(natural_language, code, tokenizer, model):
    nl_tokens = tokenizer.tokenize(natural_language)
    code_tokens = tokenizer.tokenize(code)

    if len(nl_tokens) > MAX_TENSOR_SIZE or len(code_tokens) > MAX_TENSOR_SIZE:
        return None, None

    nl_tokens_ids = tokenizer.convert_tokens_to_ids(nl_tokens)
    code_token_ids = tokenizer.convert_tokens_to_ids(code_tokens)

    try:
        nl_embeddings = model(torch.tensor(nl_tokens_ids)[None, :])[0]
        code_embeddings = model(torch.tensor(code_token_ids)[None, :])[0]
    except IndexError as e:
        print("Index Error: ", e)
        print("With code_token_ids:", code_token_ids)
        print("Model output:", model(torch.tensor(code_token_ids)))
        return None, None


    if nl_embeddings.size()[1] < code_embeddings.size()[1]:
        nl_embeddings = F.pad(nl_embeddings, (0, 0, code_embeddings.size()[1] - nl_embeddings.size()[1], 0))
    elif code_embeddings.size()[1] < nl_embeddings.size()[1]:
        code_embeddings = F.pad(code_embeddings, (0, 0, nl_embeddings.size()[1] - code_embeddings.size()[1], 0))


    nl_agg = torch.mean(nl_embeddings, 2)
    code_agg = torch.mean(code_embeddings, 2)

    return nl_agg, code_agg


def get_cosine_sim(natural_language, code, tokenizer, model):

    nl_agg, code_agg = get_average_embeddings(natural_language, code, tokenizer, model)

    if nl_agg is None or code_agg is None:
        return None

    cos = nn.CosineSimilarity(dim=1)

    return cos(nl_agg, code_agg).item()


if __name__ == "__main__":
    main()