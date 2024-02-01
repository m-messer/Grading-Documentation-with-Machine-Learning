#!/usr/bin/env python
# coding: utf-8

# In[30]:


import pandas as pd
from metrics import compute_metrics
from transformers import AutoModelForSequenceClassification, AutoTokenizer, Trainer, DataCollatorWithPadding
from datasets import Dataset


# # Evaluate Best Model Against Graded Student Submissions
# 
# #### Load data and get in same form as training

# In[4]:


df = pd.read_csv('../data/graded_docstring_code_pairs.csv', index_col=0)
df = df.drop(columns=['file_name', 'id'])
df.head()


# In[5]:


def format_str(string):
    for char in ['\r\n', '\r', '\n']:
        string = string.replace(char, ' ')
    return string

def concat_nl_and_code(data):
    return format_str(data['docstring'] + '<CODESPLIT>' + data['function'])

df['text'] = df.apply(lambda x: concat_nl_and_code(x), axis=1)
df.head()


# #### Load Model and setup evaluation

# In[35]:


ds = Dataset.from_pandas(df)


tokenizer = AutoTokenizer.from_pretrained('../models/best')
model = AutoModelForSequenceClassification.from_pretrained('../models/best')

model.eval()

data_tokens = ds.map(lambda x: tokenizer(x['text'], truncation=True, padding=True))

data_collator = DataCollatorWithPadding(tokenizer=tokenizer)

trainer = Trainer(
    model=model,
    tokenizer=tokenizer,
    compute_metrics=compute_metrics,
    data_collator=data_collator
)

model_predictions = trainer.predict(data_tokens)
print(model_predictions.metrics)

eval_results_formatted = \
            {"test/" + key.split('_', 1)[1]: item for key, item in model_predictions.metrics.items()}

print("Test Results:")
print(str(eval_results_formatted))


# 
