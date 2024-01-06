from datasets import interleave_datasets, Dataset
import seaborn as sns
from matplotlib import pyplot as plt


def over_sample(original_dataset):
    class_0_data = original_dataset.filter(lambda row: row['label'] == 0)
    class_1_data = original_dataset.filter(lambda row: row['label'] == 1)
    class_2_data = original_dataset.filter(lambda row: row['label'] == 2)
    class_3_data = original_dataset.filter(lambda row: row['label'] == 3)

    return interleave_datasets([class_0_data, class_1_data, class_2_data, class_3_data],
                               seed=100, stopping_strategy='all_exhausted')


if __name__ == '__main__':
    data = Dataset.load_from_disk('data/code_search_net_relevance.hf')
    new_data = over_sample(data)
    print(new_data)

    sns.countplot(new_data.to_pandas(), x='label')
    plt.savefig('plots/over_sample_test.pdf')
