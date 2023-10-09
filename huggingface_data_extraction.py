#!/usr/bin/env python
# coding: utf-8

# In[197]:


import pandas as pd
from datasets import load_dataset
from tqdm import tqdm

tqdm.pandas()


# ### Load CodeNetSearch Dataset and Append Relevance Scores

# In[198]:


relevance_df = pd.read_csv('data/annotationStore.csv')
relevance_df = relevance_df[relevance_df['Language'] == 'Java']
relevance_df.head()


# In[199]:


# ds_train = load_dataset("code_search_net", "java", split='train+test+validation')
ds_train = load_dataset("code_search_net", "java", split='train[:1%]')
ds_train


# In[200]:


def get_relevance(repo_url):
    row = relevance_df[relevance_df['GitHubUrl'] == repo_url]

    if not row.empty:
        return row.Relevance.iloc[0]

    return None

# Used to check of docstring is written in a different language other than English.
def is_ascii(s):
    return all(ord(c) < 128 for c in s)


# In[201]:


get_relevance('https://github.com/spring-projects/spring-boot/blob/0b27f7c70e164b2b1a96477f1d9c1acba56790c1/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/info/GitProperties.java#L106-L118')


# In[202]:


relevance_list = []

for i, row in tqdm(enumerate(iter(ds_train))):
    try:
        if not is_ascii(row['func_documentation_string']):
            relevance_list.append(None)
            continue
    except StopIteration:
        break

    score = get_relevance(row['func_code_url'])
    relevance_list.append(score)

assert len(relevance_list) == len(ds_train)
assert any(relevance_list) is not None


# In[209]:


for a in relevance_list:
    if a is not None:
        print(a)


# In[203]:


ds_train = ds_train.add_column("relevance", relevance_list)
ds_train


# In[204]:


ds_train = ds_train.remove_columns(['repository_name', 'func_path_in_repository', 'func_name', 'whole_func_string', 'language', 'func_code_url', 'split_name'])
ds_train


# In[210]:


ds_train_filtered = ds_train.filter(lambda scored: scored['relevance'] is not None)
ds_train_filtered


# In[206]:


ds_train_filtered.save_to_disk("data/code_search_net_relevance.hf")

