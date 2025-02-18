import argparse

from tokeniser_vectorizer import TokenizerVectorizer


def generate_embeddings(data_dir, vectorisation_method, pre_trained_model=None, binary=False):
    tokenizer_vectorizer = TokenizerVectorizer(vectorization_method=vectorisation_method,
                                               data_dir=data_dir, binary=binary,
                                               pre_trained_model=pre_trained_model)

    if data_dir == 'data/code_search_net_relevance.hf':
        dataset_name = 'CodeSearchNet'
    elif data_dir == 'data/menagerie_unique_pairs.csv':
        dataset_name = 'Menagerie'
    elif data_dir == 'data/menagerie_unique_pairs_truncated.csv':
        dataset_name = 'Menagerie-Truncated'
    else:
        print('Unknown Dataset')
        raise FileNotFoundError('Unknown Dataset')

    if vectorisation_method == 'pre-trained':
        data = tokenizer_vectorizer.get_pre_trained_tokenized_data()
    else:
        data = tokenizer_vectorizer.data

    embeddings = tokenizer_vectorizer.get_embeddings(data)
    data = data.add_column('embed', embeddings)

    pre_trained_model = pre_trained_model.replace('/', '_')
    data.save_to_disk(f'data/{dataset_name}_{pre_trained_model}_embeddings.hf')



def main():
    parser = argparse.ArgumentParser(description='Generate Embeddings for a given model and dataset')
    parser.add_argument('-vectorizer', dest='vectorizer', required=True,
                        help='Use -vectorizer to select a vectorizer from: ' +
                             ' '.join(TokenizerVectorizer.VECTORISATION_METHODS))
    parser.add_argument('-pre-trained', dest='pre_trained', default=None, help='A HuggingFace model for vectorisation')
    parser.add_argument('-dataset', dest='dataset', default='CodeSearchNet', help='The dataset to use for training and evaluation')
    args = parser.parse_args()

    if args.vectorizer not in TokenizerVectorizer.VECTORISATION_METHODS:
        print('Select a vectorizer from: ' + ' '.join(TokenizerVectorizer.VECTORISATION_METHODS))
        return

    if args.vectorizer == 'pre-trained' and args.pre_trained is None:
        print("Provided a pre-trained HuggingFace model for vectorisation")
        return

    if args.dataset == 'CodeSearchNet':
        data_dir = 'data/code_search_net_relevance.hf'
    elif args.dataset == 'Menagerie':
        data_dir = 'data/menagerie_unique_pairs.csv'
    elif args.dataset == 'Menagerie-Truncated':
        data_dir = 'data/menagerie_unique_pairs_truncated.csv'
    else:
        print('Select a dataset from: ' + ' '.join(['CodeSearchNet', 'Menagerie', 'Menagerie-Truncated']))
        return


    generate_embeddings(data_dir, args.vectorizer, args.pre_trained, binary=False)


if __name__ == '__main__':
    main()