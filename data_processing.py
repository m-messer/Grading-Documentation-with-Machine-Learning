#!/usr/bin/env python
# coding: utf-8

# In[26]:


import pandas as pd
from huggingface_hub import login
from datasets import load_dataset
import time
from tqdm import tqdm


# # JavaDoc-Code Similarity
# ### Login to Huggingface

# In[27]:


with open('secrets/hugging_face_key.txt') as f:
    login(f.read())


# ### Load dataset

# In[28]:


ds = load_dataset("code_search_net", "java", split='train', streaming=True).shuffle(buffer_size=10_000, seed=42)


# In[29]:


row = next(iter(ds))
print(row.keys())
print(row['func_documentation_string'])
print(row['func_code_string'])
print(row['repository_name'])


# ### Get Relevance Data
# From https://github.com/github/CodeSearchNet#human-relevance-judgements

# In[30]:


relevance_df = pd.read_csv('data/annotationStore.csv')
relevance_df = relevance_df[relevance_df['Language'] == 'Java']
relevance_df.head()


# In[31]:


def get_relevance(repo_url):
    row = relevance_df[relevance_df['GitHubUrl'] == repo_url]

    if row is not None:
        return row.Relevance

    return None


# In[32]:


get_relevance('https://github.com/ontop/ontop/blob/ddf78b26981b6129ee9a1a59310016830f5352e4/core/optimization/src/main/java/it/unibz/inf/ontop/iq/optimizer/FlattenUnionOptimizer.java#L45-L50')


# ### Preprocess data

# In[33]:


def preprocess(row):
    relevance = get_relevance(row['func_code_url'])

    return pd.DataFrame({"docstring": row['func_documentation_string'], "code": row['func_code_string'], 'relevance': relevance, 'repo': row['repository_name']}, index=[0])


# In[34]:


test = preprocess(row)
test


# ### Process Data

# In[35]:


# Used to check of docstring is written in a different language other than English.
def is_ascii(s):
    return all(ord(c) < 128 for c in s)


# In[36]:


NUMBER_OF_CLASSES = 100
WAIT_TIME = 3

df = pd.DataFrame()

for i, row in tqdm(enumerate(iter(ds))):
    if not is_ascii(row['func_documentation_string']):
        continue

    proc_df = preprocess(row)
    df = pd.concat([df, proc_df], ignore_index=True)

    time.sleep(WAIT_TIME)

    if i == NUMBER_OF_CLASSES:
        break

df.head()


# In[37]:


df.to_csv('data/processed.csv')

