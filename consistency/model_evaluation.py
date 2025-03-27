import argparse
import os

import pandas as pd
import torch
from tqdm import tqdm
from transformers import pipeline


# TODO
# 4. Refactor Grade x4
# 5. Save to file

def load_pipeline(tokenizer, filename):
    print("Loading pipeline from {}".format(filename))
    device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
    pipe = pipeline(task='text-classification', tokenizer=tokenizer, model=filename, device=device)
    print("Pipeline loaded")
    return pipe

def grade(pipeline, data, i):
    print("Grading")
    # TODO: Return DataFrame with grades
    predictions = []
    for index, row in tqdm(data.iterrows()):
        out_dict = row.to_dict()
        out_dict['iteration'] = i
        try:
            pred = pipeline(row['text'])
            out_dict['prediction'] = pred[0]['label']
        except RuntimeError as e:
            if 'The expanded size of the tensor' in str(e):
                print("Skipping {}".format(row['text']))
                out_dict['prediction'] = None
            else:
                raise RuntimeError(e)

        predictions.append(out_dict)
        print(out_dict)

    print("Grading done")
    return pd.DataFrame(predictions)

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


    consistency_grades = []
    for i in range(CONSISTENCY_RUNS):
        consistency_grades.append(grade(pipeline, data, i))

    consistency_grades_df = pd.concat(consistency_grades)


    print('Saving...')
    consistency_grades_df.to_csv('data/consistency_grades.csv', index=False)


if __name__ == "__main__":
    main()