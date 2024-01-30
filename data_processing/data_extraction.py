#!/usr/bin/env python
# coding: utf-8

# In[83]:


import pandas as pd
from datasets import load_dataset
from tqdm import tqdm

tqdm.pandas()


# ### Load CodeNetSearch Dataset and Append Relevance Scores

# In[84]:


relevance_df = pd.read_csv('../data/annotationStore.csv')
relevance_df = relevance_df[relevance_df['Language'] == 'Java']
relevance_df.head()


# In[85]:


ds_train = load_dataset("code_search_net", "java", split='train+test+validation')
# ds_train = load_dataset("code_search_net", "java", split='train[:1%]')
ds_train


# In[86]:


def get_relevance(repo_url):
    row = relevance_df[relevance_df['GitHubUrl'] == repo_url]

    if not row.empty:
        cols = tuple(row.iloc[0][['Query', 'Relevance']])
        return cols[0], cols[1]

    return None, None

# Used to check of docstring is written in a different language other than English.
def is_ascii(s):
    return all(ord(c) < 128 for c in s)


# In[87]:


get_relevance('https://github.com/spring-projects/spring-boot/blob/0b27f7c70e164b2b1a96477f1d9c1acba56790c1/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/info/GitProperties.java#L106-L118')


# In[88]:


relevance_scores = []
queries = []

for i, row in tqdm(enumerate(iter(ds_train))):
    try:
        if not is_ascii(row['func_documentation_string']):
            relevance_scores.append(None)
            queries.append(None)
            continue
    except StopIteration:
        break

    query, score = get_relevance(row['func_code_url'])

    relevance_scores.append(score)
    queries.append(query)

assert len(relevance_scores) == len(ds_train)
assert any(relevance_scores) is not None


# In[89]:


for a in relevance_scores:
    if a is not None:
        print(a)


# In[90]:


len(relevance_scores)


# In[91]:


ds_train = ds_train.add_column("label", relevance_scores)
ds_train = ds_train.add_column("query", queries)
ds_train


# In[92]:


ds_train = ds_train.remove_columns(['repository_name', 'func_path_in_repository', 'func_name', 'whole_func_string', 'language', 'func_code_url', 'split_name', 'func_code_tokens', 'func_documentation_string', 'func_documentation_tokens'])
ds_train


# In[93]:


ds_train_filtered = ds_train.filter(lambda scored: scored['label'] is not None)
ds_train_filtered


# In[94]:


# Taken from CodeBERT Preprocessing steps

def format_str(string):
    for char in ['\r\n', '\r', '\n']:
        string = string.replace(char, ' ')
    return string


# In[95]:


def concat_nl_and_code(data):
    data['text'] = format_str(data['query'] + '<CODESPLIT>' + data['func_code_string'])

    return data

ds_train_filtered = ds_train_filtered.map(concat_nl_and_code)
ds_train_filtered


# In[96]:


ds_train_filtered[0]


# In[97]:


ds_train_filtered.save_to_disk("../data/code_search_net_relevance.hf")

