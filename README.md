# Grading Documentation with Machine Learning - Training and Data Repository

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