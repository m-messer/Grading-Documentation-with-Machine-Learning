# Grading Documentation with Machine Learning - Training and Data Repository

### Erratum
In our paper “Grading Documentation with Machine Learning” which appeared in Artificial Intelligence in Education 2025 there is an error in how the data was pre-processed before training our machine learning model, resulting in overfitting models and incorrect metric results.
We found an error in how we oversampled the data, which resulted in the same labelled data being present in both the training and test dataset. As such our metric scores are incorrect and are significantly lower than previously published. Our best model’s accuracy was previously published as 89%, and all other models’ results are similarly affected. After fixing the error in our code and rerunning our best models experiment with the same hyperparameters and on the same hardware, we found that the correct accuracy was 46% when performing multi-class classification and 62% accurate when performing binary classification.

### Overview
This repository is for the paper titled "Grading Documentation with Machine Learning", and is formatted as follows:

Directories:
- data: Used to store all the raw and processed data, and the results. The raw dataset requires unziping and is called 
'code_search_relevance.hf.zip'
- data_processing: Contains the notebook used to process CodeSearchNet, and that is used to load the dataset and perform preprocessing steps
- plots: Contains all the generated plots used within the paper, and some other examples
- training: Provides all the classes used when training the ML models
  - To run these, you may have to move them from the folder, along with other required Python modules, such as data-proccessor.py

Files of note:
- metrics.py: Contains the functions used to calculate the metrics used in training and evaluation
- results.ipynb: Is the notebook used to present the results and generate the plots for our paper
- requirements.txt: Contains all the Python requirements 

All models were trained on a high performance cluster, with 32 CPU cores, and all fine-tuning also utilised an Nvidia A100 GPU.