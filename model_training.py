#!/usr/bin/env python
# coding: utf-8

# In[5]:


import pandas as pd
from transformers import RobertaTokenizer, RobertaModel
import torch
from torch import nn, optim
from torch.utils.data import Dataset, WeightedRandomSampler, DataLoader
from tqdm import tqdm
import seaborn as sns
from sklearn.model_selection import train_test_split
from datetime import datetime

tqdm.pandas()


# ### Load Dataset

# In[6]:


df = pd.read_pickle('data/processed.pickle')
df.head()


# ### Get Embeddings

# In[7]:


EMBEDDING_DIMENSION = 400

def get_tokens_and_embeddings(natural_language, code, tokenizer, model):

    nl_tokens = tokenizer.tokenize(natural_language)
    code_tokens = tokenizer.tokenize(code)

    tokens = ['CLS'] + nl_tokens + ['SEP'] + code_tokens + ['EOS'] # Take from CodeBERT paper

    if len(tokens) > EMBEDDING_DIMENSION:
        tokens = tokens[:EMBEDDING_DIMENSION]

    token_ids = tokenizer.convert_tokens_to_ids(tokens)
    embedding = model(torch.tensor(token_ids)[None, :])[0][0]

    pad_value_before = (EMBEDDING_DIMENSION - len(tokens)) // 2
    pad_value_after = pad_value_before + ((EMBEDDING_DIMENSION - len(tokens)) % 2)

    padded = nn.functional.pad(embedding, (0, 0, pad_value_before, pad_value_after), "constant", 0)

    return tokens, len(tokens), padded.detach().numpy()


# In[8]:


device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
bert_tokenizer = RobertaTokenizer.from_pretrained("microsoft/codebert-base")
bert_model = RobertaModel.from_pretrained("microsoft/codebert-base")
bert_model.to(device)

print()


# In[9]:


df[['tokens', 'token_count', 'embeddings']] = df.progress_apply(lambda x: get_tokens_and_embeddings(x['docstring'], x['code'], tokenizer=bert_tokenizer, model=bert_model), axis=1, result_type='expand')
df = df.drop(columns=['split'])
df.head()


# In[10]:


df.shape


# ### Explore data
# All below adapted from: https://towardsdatascience.com/pytorch-tabular-multiclass-classification-9f8211a123ab

# In[11]:


sns.countplot(df, x='relevance')


# #### Create splits

# In[12]:


X = df[['embeddings']]
y = df[['relevance']]

random_seed = 123


# In[13]:


X_trainval, X_test, y_trainval, y_test = train_test_split(X, y, test_size=0.2, stratify=y, random_state=random_seed)
X_train, X_val, y_train, y_val = train_test_split(X_trainval, y_trainval, test_size=0.1, stratify=y_trainval, random_state=random_seed)


# In[14]:


sns.countplot(y_train, x='relevance')


# In[15]:


sns.countplot(y_val, x='relevance')


# In[16]:


sns.countplot(y_test, x='relevance')


# ### Create dataloader

# In[17]:


class RelevanceDataset(Dataset):
    def __init__(self, X_data, y_data):
        self.X_data = torch.tensor(X_data.reset_index(drop=True).embeddings)
        self.y_data = torch.tensor(y_data.reset_index(drop=True).relevance).long()

    def __getitem__(self, item):
        return self.X_data[item], self.y_data[item]

    def __len__(self):
        return len(self.X_data)


# In[18]:


train_dataset = RelevanceDataset(X_train, y_train)
val_dataset = RelevanceDataset(X_val, y_val)
test_dataset = RelevanceDataset(X_test, y_test)


# #### Oversample Dataset

# In[19]:


target_list = []

for _, t in train_dataset:
    target_list.append(t)


target_list = torch.tensor(target_list)

class_count = [i for i in y_train.value_counts().values]
class_weights = 1./torch.tensor(class_count, dtype=torch.float)

class_weights_all = class_weights[target_list]

weighted_sampler = WeightedRandomSampler(weights=class_weights_all, num_samples=len(class_weights_all), replacement=True)


# ### Model Parameters

# In[20]:


EPOCHS = 300
BATCH_SIZE = 16
LEARNING_RATE = 0.0007

KERNEL_SIZE = 5
POOL_SIZE = 2
HIDDEN_LAYER_SIZE = 256

NUM_FEATURES = 400
NUM_CLASSES = 4


# #### Setup Dataloaders

# In[21]:


train_loader = DataLoader(dataset=train_dataset, batch_size=BATCH_SIZE, sampler=weighted_sampler)
val_loader = DataLoader(dataset=val_dataset, batch_size=1)


# #### Define Model

# In[59]:


class Classifier(nn.Module):
    def __init__(self, num_features, hidden_layer_size, kernel_size, pool_size, num_class):
        super(Classifier, self).__init__()

        self.conv1 = nn.Conv1d(num_features, hidden_layer_size, kernel_size)
        self.conv2 = nn.Conv1d(hidden_layer_size, hidden_layer_size, kernel_size)
        self.fc1 = nn.Linear(48384, 48384)
        self.fc_out = nn.Linear(48384, num_class)

        self.pool = nn.MaxPool1d(pool_size)
        self.flatten = nn.Flatten()
        self.relu = nn.ReLU()
        self.dropout = nn.Dropout()

    def forward(self, x):
        x = self.conv1(x)
        x = self.pool(x)

        x = self.conv2(x)
        x = self.pool(x)

        x = self.flatten(x)

        x = self.fc1(x)
        x = self.relu(x)
        x = self.dropout(x)

        x = self.fc_out(x)

        return x


# #### Train Model

# In[33]:


device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
print(device)


# In[60]:


model = Classifier(num_features=NUM_FEATURES, hidden_layer_size=HIDDEN_LAYER_SIZE, kernel_size=KERNEL_SIZE, pool_size=POOL_SIZE, num_class=NUM_CLASSES)

model.to(device)

criterion = nn.CrossEntropyLoss(weight=class_weights.to(device))
optimizer = optim.Adam(model.parameters(), lr=LEARNING_RATE)

model


# In[42]:


# TODO: What does this function measure?

def multi_acc(y_pred, y_test):
    y_pred_softmax = torch.log_softmax(y_pred, dim = 1)
    _, y_pred_tags = torch.max(y_pred_softmax, dim=1)

    correct_pred = (y_pred_tags == y_test)
    acc = correct_pred.sum() / len(correct_pred)

    return torch.round(acc * 100)


accuracy_stats = {
    'train': [],
    'val': []
}

loss_stats = {
    'train': [],
    'val': []
}


# In[61]:


print("Begin training...")


for epoch in tqdm(range(1, EPOCHS + 1)):
    train_epoch_loss = 0
    train_epoch_acc = 0

    # TRAINING

    model.train()

    for X_train_batch, y_train_batch in train_loader:
        X_train_batch = X_train_batch.to(device)
        y_train_batch = y_train_batch.to(device)

        optimizer.zero_grad()

        y_train_pred = model(X_train_batch)

        train_loss = criterion(y_train_pred, y_train_batch)
        train_acc = multi_acc(y_train_pred, y_train_batch)

        train_loss.backward()
        optimizer.step()

        train_epoch_acc += train_acc.item()
        train_epoch_loss += train_loss.item()


    # Validation

    with torch.no_grad():
        val_epoch_loss = 0
        val_epoch_acc = 0

        model.eval()

        for X_val_batch, y_val_batch in val_loader:
            X_val_batch = X_val_batch.to(device)
            y_val_batch = y_val_batch.to(device)

            y_val_pred = model(X_val_batch)

            val_epoch_loss += criterion(y_val_pred, y_val_batch).item()
            val_epoch_acc += multi_acc(y_val_pred, y_val_batch).item()

    loss_stats['train'].append(train_epoch_loss / len(train_loader))
    loss_stats['val'].append(val_epoch_loss / len(val_loader))

    accuracy_stats['train'].append(train_epoch_acc / len(train_loader))
    accuracy_stats['val'].append(val_epoch_acc / len(val_loader))

    print(f'Epoch {epoch+0.03}: | '
          f'Train Loss: {train_epoch_loss / len(train_loader):.5f} |'
          f'Validation Loss: {val_epoch_loss / len(val_loader):.5f} |'
          f'Train Accuracy:  {train_epoch_acc / len(train_loader)}:.3f |'
          f'Validation Accuracy: {val_epoch_acc / len(val_loader)}:.3f')


# ### Save Model and Data for Visualization and Testing

# In[ ]:


pd.DataFrame.from_dict(accuracy_stats).reset_index().melt(id_vars=['index']).rename(columns={'index': 'epochs'}).to_csv('data/accuracy_stats_' + str(datetime.now()) + '.csv')
pd.DataFrame.from_dict(loss_stats).reset_index().melt(id_vars=['index']).rename(columns={'index': 'epochs'}).to_csv('data/loss_stats_' + str(datetime.now()) + '.csv')

torch.save(model.state_dict(), "model_v1_" + str(datetime.now()) + ".pt")


# In[ ]:


torch.save(test_dataset, 'data/test_dataset_' + str(datetime.now()) + '.pt')

