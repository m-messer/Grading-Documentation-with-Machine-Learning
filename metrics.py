import evaluate
import numpy as np
from sklearn.metrics import log_loss

f1 = evaluate.load('f1')
accuracy = evaluate.load('accuracy')
recall = evaluate.load('recall')
precision = evaluate.load('precision')


def compute_metrics(eval_pred):
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
    loss = log_loss(y_true=labels, y_pred=prediction_prob, labels=labels)

    return {'accuracy': accuracy_res,
            'f1_macro': f1_macro_res, 'f1_micro': f1_micro_res, 'f1_weighted': f1_weighted_res,
            'recall_macro': recall_macro_res, 'recall_micro': recall_micro_res,
            'recall_weighted': recall_weighted_res, 'precision_macro': precision_macro_res,
            'precision_micro': precision_micro_res, 'precision_weighted': precision_weighted_res,
            'loss': loss
            }


def compute_metrics_prompting(predictions, labels):
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


def format_metrics(metrics, prefix):
    return {prefix + "/" + key: item for key, item in metrics.items()}
