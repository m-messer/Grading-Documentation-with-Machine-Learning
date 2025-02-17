from datasets import Dataset, interleave_datasets, load_dataset

def __get_label_info_code_search_net(binary: bool):
    """
    Gets the ID, label and label count dependant if the model training is binary or multi-class.
    :param binary: If the model training is multiclass or not
    :return: The id2label and label2id dictionaries, as well as the label count.
    """
    if binary:
        id2label = {0: 'irrelevant', 1: 'relevant'}
        label2id = {'irrelevant': 0, 'relevant': 1}

        label_count = 2
    else:
        id2label = {0: 'irrelevant', 1: 'partially irrelevant', 2: 'partially relevant', 3: 'relevant'}
        label2id = {'irrelevant': 0, 'partially irrelevant': 1, 'partially relevant': 2, 'relevant': 3}

        label_count = 4

    return id2label, label2id, label_count

def __get_label_info_menagerie(binary: bool):
    """
    Gets the ID, label and label count dependant if the model training is binary or multi-class.
    :param binary: If the model training is multiclass or not
    :return: The id2label and label2id dictionaries, as well as the label count.
    """

    grades = ['F', 'D-', 'D', 'D+', 'C-', 'C', 'C+', 'B-', 'B', 'B+', 'A-', 'A', 'A+', 'A++']

    if binary:
        id2label = {0: 'fail', 1: 'pass'}
        label2id = {'fail': 0, 'pass': 1}

        label_count = 2
    else:
        id2label = {}
        label2id = {}
        for i, grade in enumerate(grades):
            id2label[i] = grade
            label2id[grade] = i

        label_count = len(grades)

    return id2label, label2id, label_count


def get_label_info(binary, dataset_name):
    if dataset_name == 'CodeSearchNet':
        __get_label_info_code_search_net(binary)
    elif dataset_name =='Menagerie':
        __get_label_info_menagerie(binary)


def __format_str(string):
    for char in ['\r\n', '\r', '\n']:
        string = string.replace(char, ' ')
    return string


def __concat_nl_and_code(data):
    data['text'] = __format_str(data['query'] + '<CODESPLIT>' + data['func_code_string'])

    return data


def __map_grades_to_label_multi(data):
    _, label2id, _ = __get_label_info_menagerie(False)
    data['label'] = label2id[data['grade']]

    return data


def get_data(data_dir: str, binary: bool = False):
    """
    Loads the data from the disk, runs preprocessing steps and converts multiclass data to binary
    :param data_dir: The path of the dataset to load
    :param binary: If the dataset should be converted to binary (classes 2 and 3 become 1)
    :return: The preprocessed dataset ready for model training
    """

    if data_dir.endswith('.hf'):
        data = Dataset.load_from_disk(data_dir)
    else:
        data = load_dataset("csv", data_files=data_dir)
        data = data['train'].remove_columns(['hash', 'grades', 'grade_count'])
        data = data.rename_columns({'function': 'func_code_string', 'docstring': 'query'})
        data = data.map(__concat_nl_and_code)
        data = data.map(__map_grades_to_label_multi)

    if binary:
        data = data.map(__convert_to_binary)

    return data


def over_sample_menagerie(original_dataset, binary):
    if not binary:
        filtered_datasets = []
        _, _, label_count = __get_label_info_menagerie(binary)

        for i in range(label_count):
            temp_dataset = original_dataset.filter(lambda row: row['label'] == i)

            if len(temp_dataset) > 0:
                filtered_datasets.append(temp_dataset)

        over_sampled_train_data = interleave_datasets(filtered_datasets,
                                                      seed=100, stopping_strategy='all_exhausted')
    else:
        class_0_data = original_dataset.filter(lambda row: row['label'] == 0)
        class_1_data = original_dataset.filter(lambda row: row['label'] == 1)
        over_sampled_train_data = interleave_datasets([class_0_data, class_1_data],
                                                      seed=100, stopping_strategy='all_exhausted')

    return over_sampled_train_data


def over_sample_code_search_net(original_dataset, binary):
    class_0_data = original_dataset.filter(lambda row: row['label'] == 0)
    class_1_data = original_dataset.filter(lambda row: row['label'] == 1)
    class_2_data = original_dataset.filter(lambda row: row['label'] == 2)
    class_3_data = original_dataset.filter(lambda row: row['label'] == 3)

    if not binary:
        over_sampled_train_data = interleave_datasets([class_0_data, class_1_data, class_2_data, class_3_data],
                                                      seed=100, stopping_strategy='all_exhausted')
    else:
        over_sampled_train_data = interleave_datasets([class_0_data, class_1_data],
                                                      seed=100, stopping_strategy='all_exhausted')
    return over_sampled_train_data


def over_sample(original_dataset, dataset_name, binary=False):
    if dataset_name == 'CodeSearchNet':
        return over_sample_code_search_net(original_dataset, binary)
    elif dataset_name == 'Menagerie':
        return over_sample_menagerie(original_dataset, binary)


def __convert_to_binary(row):
    # TODO: Update for Menagerie
    if row['label'] in [0, 1]:
        row['label'] = 0
    if row['label'] in [2, 3]:
        row['label'] = 1

    return row


if __name__ == '__main__':
    menagerie_df = get_data(data_dir='../data/menagerie_unique_pairs.csv')
    print('Menagerie')
    print(menagerie_df)
    print(menagerie_df.to_pandas()['grade'].value_counts())
    print(menagerie_df[0])
    oversampled_menagerie_df = over_sample(menagerie_df, dataset_name='Menagerie', binary=False)
    print(oversampled_menagerie_df)
    print(oversampled_menagerie_df.to_pandas()['grade'].value_counts())
    csn_df = get_data(data_dir='../data/code_search_net_relevance.hf')
    print('CodeSearchNet')
    print(csn_df)
    oversampled_csn_df = over_sample(csn_df, dataset_name='CodeSearchNet')
    print(oversampled_csn_df)
    print(oversampled_csn_df.to_pandas()['label'].value_counts())
