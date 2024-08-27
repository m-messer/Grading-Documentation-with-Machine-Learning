
from imblearn.over_sampling import RandomOverSampler, SMOTE

def sample_data(train_val_X, train_val_y, sampling_method):
    if sampling_method == 'RandomOverSample':
        ros = RandomOverSampler(random_state=0)
        return ros.fit_resample(train_val_X, train_val_y)
    elif sampling_method == 'SMOTE':
        smote = SMOTE(random_state=0)
        return smote.fit_resample(train_val_X, train_val_y)