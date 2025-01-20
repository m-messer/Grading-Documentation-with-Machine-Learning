from datasets import Dataset, interleave_datasets, DatasetDict


def get_label_info(binary: bool):
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


def get_data(data_dir: str, binary: bool = False):
    """
    Loads the data from the disk, runs preprocessing steps and converts multiclass data to binary
    :param data_dir: The path of the dataset to load
    :param binary: If the dataset should be converted to binary (classes 2 and 3 become 1)
    :return: The preprocessed dataset ready for model training
    """
    data = Dataset.load_from_disk(data_dir)

    if binary:
        data = data.map(__convert_to_binary)

    return data


def over_sample(original_dataset):
    class_0_data = original_dataset.filter(lambda row: row['label'] == 0)
    class_1_data = original_dataset.filter(lambda row: row['label'] == 1)
    class_2_data = original_dataset.filter(lambda row: row['label'] == 2)
    class_3_data = original_dataset.filter(lambda row: row['label'] == 3)

    over_sampled_train_data = interleave_datasets([class_0_data, class_1_data, class_2_data, class_3_data],
                               seed=100, stopping_strategy='all_exhausted')
    # over_sampled_train_data = interleave_datasets([class_0_data, class_1_data],
    #                                               seed=100, stopping_strategy='all_exhausted')
    return over_sampled_train_data


def __convert_to_binary(row):
    if row['label'] in [0, 1]:
        row['label'] = 0
    if row['label'] in [2, 3]:
        row['label'] = 1

    return row


if __name__ == '__main__':
    raw_df = get_data(data_dir='../data/code_search_net_relevance.hf', pre_process=False).to_csv('../data/raw.csv')
    proc_df = get_data(data_dir='../data/code_search_net_relevance.hf', pre_process=True).to_csv('../data/proc.csv')
