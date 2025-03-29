import argparse
import os

import pandas as pd
import torch
from tqdm import tqdm
from transformers import pipeline

def load_pipeline(tokenizer, filename):
    id2label = {1: 'A', 2: 'A+', 6: 'B', 7: 'B+', 3: 'A++', 5: 'B-', 0: 'A-', 4: 'C'}
    print("Loading pipeline from {}".format(filename))
    device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
    pipe = pipeline(task='text-classification', tokenizer=tokenizer, model=filename, device=device)
    pipe.model.config.id2label = id2label
    print(pipe)
    print("Pipeline loaded")
    return pipe

def grade(pipeline, data, i):
    print("Grading")

    predictions = []
    for index, row in tqdm(data.iterrows()):
        try:
            if len(row['text']) < pipeline.tokenizer.model_max_length:
                pred = pipeline(row['text'])
                predictions.append(pred[0]['label'])
            else:
                predictions.append(None)
        except RuntimeError as e:
            if 'The expanded size of the tensor' in str(e):
                print("Skipping")
                predictions.append(None)
            else:
                raise RuntimeError(e)

    data[f'predictions_{i}'] = predictions
    print("Grading done")
    return data

def main():
    parser = argparse.ArgumentParser(description='Evaluate consistency of model')
    parser.add_argument('-tokenizer', dest='tokenizer', type=str, default='microsoft/codebert-base')
    parser.add_argument('-model_path', dest='model_path', type=str, help='The path of the model to test')
    parser.add_argument('-sample_path', dest='sample_path', type=str, help='The path of the sample to test the model against')
    args = parser.parse_args()

    print('Parsing Arguments')
    if not os.path.exists(args.model_path):
        raise FileNotFoundError("Model path not found")

    if not os.path.exists(args.sample_path):
        raise FileNotFoundError("Sample path not found")


    pipeline = load_pipeline(args.tokenizer, args.model_path)
    data = pd.read_csv(args.sample_path)
    CONSISTENCY_RUNS = 4


    for i in range(CONSISTENCY_RUNS):
        data = grade(pipeline, data, i)

    print('Saving...')
    data.to_csv(f'data/consistency_grades_sample.csv', index=False)


if __name__ == "__main__":
    main()