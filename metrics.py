import evaluate
import numpy as np
from sklearn.metrics import log_loss

f1 = evaluate.load('f1')
accuracy = evaluate.load('accuracy')
recall = evaluate.load('recall')
precision = evaluate.load('precision')


def compute_metrics(eval_pred):
    """
    The metrics function to use when fine-tuning LLMs
    :param eval_pred: A tuple of lists, with the format (predictions, labels)
    :return: A dictionary of metrics, including accuracy, F1, Recall and Precision
    """
    predictions, labels = eval_pred
    predictions = np.argmax(predictions, axis=1)

    accuracy_res = accuracy.compute(predictions=predictions, references=labels)['accuracy']
    f1_macro_res = f1.compute(predictions=predictions, references=labels, average='macro')['f1']
    f1_micro_res = f1.compute(predictions=predictions, references=labels, average='micro')['f1']
    f1_weighted_res = f1.compute(predictions=predictions, references=labels, average='weighted')['f1']
    recall_macro_res = recall.compute(predictions=predictions, references=labels, average='macro')['recall']
    recall_micro_res = recall.compute(predictions=predictions, references=labels, average='micro')['recall']
    recall_weighted_res = recall.compute(predictions=predictions, references=labels, average='weighted')[
        'recall']
    precision_macro_res = precision.compute(predictions=predictions, references=labels, average='macro')[
        'precision']
    precision_micro_res = precision.compute(predictions=predictions, references=labels, average='micro')[
        'precision']
    precision_weighted_res = precision.compute(predictions=predictions, references=labels, average='weighted')[
        'precision']

    return {'accuracy': accuracy_res,
            'f1_macro': f1_macro_res, 'f1_micro': f1_micro_res, 'f1_weighted': f1_weighted_res,
            'recall_macro': recall_macro_res, 'recall_micro': recall_micro_res,
            'recall_weighted': recall_weighted_res, 'precision_macro': precision_macro_res,
            'precision_micro': precision_micro_res, 'precision_weighted': precision_weighted_res,
            }


def compute_metrics_trad(predictions, prediction_prob, labels):
    """
   The metrics function to use when training traditional approaches
   :param predictions: The predictions as generated by SK-Learn
   :param prediction_prob: The probability of the predictions as generated by the SK-Learn
   :param labels: The groundtruth labels to evaluate the predictions against
   :return: A dictionary of metrics, including accuracy, F1, Recall and Precision
   """
    accuracy_res = accuracy.compute(predictions=predictions, references=labels)['accuracy']
    f1_macro_res = f1.compute(predictions=predictions, references=labels, average='macro')['f1']
    f1_micro_res = f1.compute(predictions=predictions, references=labels, average='micro')['f1']
    f1_weighted_res = f1.compute(predictions=predictions, references=labels, average='weighted')['f1']
    recall_macro_res = recall.compute(predictions=predictions, references=labels, average='macro')['recall']
    recall_micro_res = recall.compute(predictions=predictions, references=labels, average='micro')['recall']
    recall_weighted_res = recall.compute(predictions=predictions, references=labels, average='weighted')[
        'recall']
    precision_macro_res = precision.compute(predictions=predictions, references=labels, average='macro')[
        'precision']
    precision_micro_res = precision.compute(predictions=predictions, references=labels, average='micro')[
        'precision']
    precision_weighted_res = precision.compute(predictions=predictions, references=labels, average='weighted')[
        'precision']

    if prediction_prob is not None:
        loss = log_loss(y_true=labels, y_pred=prediction_prob, labels=labels)
    else:
        loss = None

    return {'accuracy': accuracy_res,
            'f1_macro': f1_macro_res, 'f1_micro': f1_micro_res, 'f1_weighted': f1_weighted_res,
            'recall_macro': recall_macro_res, 'recall_micro': recall_micro_res,
            'recall_weighted': recall_weighted_res, 'precision_macro': precision_macro_res,
            'precision_micro': precision_micro_res, 'precision_weighted': precision_weighted_res,
            'loss': loss
            }


def format_metrics(metrics, prefix):
    """
    Formats the metric output with a prefix for logging in Weights and Biases
    :param metrics: The metrics to format
    :param prefix: The prefix to add
    :return: The formatted metric dict
    """
    return {prefix + "/" + key: item for key, item in metrics.items()}
