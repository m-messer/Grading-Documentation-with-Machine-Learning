from data_curation import DataCurator
from transformers import Trainer, TrainingArguments, AutoModelForSequenceClassification
from torch import nn, tensor, cuda
import wandb
from sklearn.model_selection import StratifiedKFold
import numpy as np
import evaluate


class Train:
    def __init__(self, pre_trained_model, data_dir, wandb_project, output_dir, learning_rate, batch_size,
                 epochs, weight_decay, binary=False):

        with open('secrets/wandb_api_key.txt') as f:
            wandb.login(key=f.read())

            wandb.init(
                project=wandb_project
            )

        self.data_curator = DataCurator(pre_trained_model, data_dir, binary)
        self.data = self.data_curator.get_tokenized_data()
        self.train_test_data = self.data.train_test_split(test_size=0.2)
        self.weights = self.calculate_class_weights()


        if binary:
            self.id2label = {0: 'irrelevant', 1: 'relevant'}
            self.label2id = {'irrelevant': 0, 'relevant': 1}

            label_count = 2
        else:
            label_count = 4

        self.model = AutoModelForSequenceClassification.from_pretrained(pre_trained_model, num_labels=label_count,
                                                                        id2label=self.id2label, label2id=self.label2id)
        device = "cuda:0" if cuda.is_available() else "cpu"
        self.model.to(device)

        self.training_arguments = TrainingArguments(
            output_dir=output_dir,
            learning_rate=learning_rate,
            per_device_train_batch_size=batch_size,
            per_device_eval_batch_size=batch_size,
            num_train_epochs=epochs,
            weight_decay=weight_decay,
            evaluation_strategy="epoch",
            save_strategy="epoch",
            load_best_model_at_end=True,
            save_total_limit=5,
            push_to_hub=False,
            report_to=["wandb"]
        )

        self.f1 = evaluate.load('f1')
        self.accuracy = evaluate.load('accuracy')
        self.recall = evaluate.load('recall')
        self.precision = evaluate.load('precision')

    def calculate_class_weights(self):
        class_count = self.data.to_pandas().groupby('label').count()['input_ids'].to_list()
        total = sum(class_count)
        return [1 - (val / total) for val in class_count]

    def compute_metrics(self, eval_pred):
        predictions, labels = eval_pred
        predictions = np.argmax(predictions, axis=1)

        accuracy_res = self.accuracy.compute(predictions=predictions, references=labels)
        f1_macro_res = self.f1.compute(predictions=predictions, references=labels, average='macro')
        f1_micro_res = self.f1.compute(predictions=predictions, references=labels, average='micro')
        f1_weighted_res = self.f1.compute(predictions=predictions, references=labels, average='weighted')
        recall_macro_res = self.recall.compute(predictions=predictions, references=labels, average='macro')
        recall_micro_res = self.recall.compute(predictions=predictions, references=labels, average='micro')
        recall_weighted_res = self.recall.compute(predictions=predictions, references=labels, average='weighted')
        precision_macro_res = self.precision.compute(predictions=predictions, references=labels, average='macro')
        precision_micro_res = self.precision.compute(predictions=predictions, references=labels, average='micro')
        precision_weighted_res = self.precision.compute(predictions=predictions, references=labels, average='weighted')

        return {'accuracy': accuracy_res,
                'f1_macro': f1_macro_res, 'f1_micro': f1_micro_res, 'f1_weighted': f1_weighted_res,
                'recall_macro': recall_macro_res, 'recall_micro': recall_micro_res, 'recall_weighted': recall_weighted_res,
                'precision_macro': precision_macro_res, 'precision_micro': precision_micro_res, 'precision_weighted': precision_weighted_res,
                }

    def train_with_cross_validation(self, number_splits):
        folds = StratifiedKFold(n_splits=number_splits)

        splits = folds.split(np.zeros(self.train_test_data['train'].num_rows), self.train_test_data['train']['label'])

        for train_idxs, val_idxs in splits:
            train_data = self.train_test_data['train'].select(train_idxs)
            validation_data = self.train_test_data['train'].select(val_idxs)

            trainer = CustomTrainer(
                weights=self.calculate_class_weights(),
                model=self.model,
                args=self.training_arguments,
                train_dataset=train_data,
                eval_dataset=validation_data,
                tokenizer=self.data_curator.tokenizer,
                data_collator=self.data_curator.data_collator,
                compute_metrics=self.compute_metrics,
            )

            trainer.train()


class CustomTrainer(Trainer):
    def __init__(self, weights, model, args, train_dataset, eval_dataset, tokenizer, data_collator, compute_metrics):
        self.weights = weights
        super().__init__(model=model,
                         args=args,
                         train_dataset=train_dataset,
                         eval_dataset=eval_dataset,
                         tokenizer=tokenizer,
                         data_collator=data_collator,
                         compute_metrics=compute_metrics)

    def compute_loss(self, model, inputs, return_outputs=False):
        labels = inputs.pop('labels')
        outputs = model(**inputs)
        logits = outputs.get('logits')
        loss_fn = nn.CrossEntropyLoss(weight=tensor(self.weights, device=model.device))
        loss = loss_fn(logits.view(-1, self.model.config.num_labels), labels.view(-1))

        return (loss, outputs) if return_outputs else loss


if __name__ == '__main__':
    train = Train(
        pre_trained_model='microsoft/codebert-base',
        output_dir='m-messer/JavaDoc_Code_Relevance_Classifier',
        batch_size=8,
        data_dir='data/code_search_net_relevance.hf',
        epochs=100,
        weight_decay=0.01,
        binary=True,
        wandb_project='JavaDoc-Relevance-Binary-Classifier',
        learning_rate=2e-5
    )
    train.train_with_cross_validation(10)
