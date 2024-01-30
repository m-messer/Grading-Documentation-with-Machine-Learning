from datasets import Dataset, interleave_datasets


def get_label_info(binary):
    if binary:
        id2label = {0: 'irrelevant', 1: 'relevant'}
        label2id = {'irrelevant': 0, 'relevant': 1}

        label_count = 2
    else:
        id2label = {0: 'irrelevant', 1: 'partially irrelevant', 2: 'partially relevant', 3: 'relevant'}
        label2id = {'irrelevant': 0, 'partially irrelevant': 1, 'partially relevant': 2, 'relevant': 1}

        label_count = 4

    return id2label, label2id, label_count


def get_data(data_dir, binary=False, pre_process=False):
    data = Dataset.load_from_disk(data_dir)

    if pre_process:
        data = __over_sample(data)

    if binary:
        data = data.map(__convert_to_binary)

    return data


def __over_sample(original_dataset):
    class_0_data = original_dataset.filter(lambda row: row['label'] == 0)
    class_1_data = original_dataset.filter(lambda row: row['label'] == 1)
    class_2_data = original_dataset.filter(lambda row: row['label'] == 2)
    class_3_data = original_dataset.filter(lambda row: row['label'] == 3)

    return interleave_datasets([class_0_data, class_1_data, class_2_data, class_3_data],
                               seed=100, stopping_strategy='all_exhausted')


def __convert_to_binary(row):
    if row['label'] in [2, 3]:
        row['label'] = 1

    return row


if __name__ == '__main__':
    raw_df = get_data(data_dir='../data/code_search_net_relevance.hf', pre_process=False).to_csv('../data/raw.csv')
    proc_df = get_data(data_dir='../data/code_search_net_relevance.hf', pre_process=True).to_csv('../data/proc.csv')



