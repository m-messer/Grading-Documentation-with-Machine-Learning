from datasets import load_dataset

def __get_label_info_menagerie(binary: bool, truncated: bool = False):
    """
    Gets the ID, label and label count dependant if the model training is binary or multi-class.
    :param binary: If the model training is multiclass or not
    :param truncated: If using Menagerie-Truncated dataset or not.
    :return: The id2label and label2id dictionaries, as well as the label count.
    """

    if not truncated:
        grades = ['F', 'D-', 'D', 'D+', 'C-', 'C', 'C+', 'B-', 'B', 'B+', 'A-', 'A', 'A+', 'A++']
    else:
        grades = ['F', 'D', 'C','B', 'A']

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
        return __get_label_info_code_search_net(binary)
    elif 'Menagerie' in dataset_name:
        return __get_label_info_menagerie(binary, True if 'Truncated' in dataset_name else False)
    else:
        raise TypeError('Unknown Dataset')


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

def main():
    data = load_dataset("csv", data_files='data/menagerie_unique_pairs.csv')
    data = data['train'].remove_columns(['hash', 'grades', 'grade_count'])
    data = data.rename_columns({'function': 'func_code_string', 'docstring': 'query'})
    data = data.map(__concat_nl_and_code)
    data = data.map(__map_grades_to_label_multi)

    print(data)

    data.save_to_disk('data/menagerie.hf')

if __name__ == '__main__':
    main()