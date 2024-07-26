
from imblearn.over_sampling import RandomOverSampler, SMOTE, ADASYN
from imblearn.combine import SMOTEENN, SMOTETomek

VALID_SAMPLING_VALUES = ['RandomOverSample', 'SMOTE', 'ADASYN', 'SMOTEENN', 'SMOTETomek']

def sample_data(train_val_X, train_val_y, sampling_method):
    assert sampling_method in VALID_SAMPLING_VALUES
    if sampling_method == 'RandomOverSample':
        ros = RandomOverSampler(random_state=0)
        return ros.fit_resample(train_val_X, train_val_y)
    elif sampling_method == 'SMOTE':
        smote = SMOTE(random_state=0)
        return smote.fit_resample(train_val_X, train_val_y)
    elif sampling_method == 'ADASYN':
        ada = ADASYN(random_state=0)
        return ada.fit_resample(train_val_X, train_val_y)
    elif sampling_method == 'SMOTEENN':
        smoteen = SMOTEENN(random_state=0)
        return smoteen.fit_resample(train_val_X, train_val_y)
    elif sampling_method == 'SMOTETomek':
        smotetomek = SMOTETomek(random_state=0)
        return smotetomek.fit_resample(train_val_X, train_val_y)