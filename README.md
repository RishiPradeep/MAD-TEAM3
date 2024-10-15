# Team - 03

- **Hanish K R** - CB.EN.U4CSE21317
- **Rishi Pradeepkumar** - CB.EN.U4CSE21347

### Submitted to:
Dr. Guruprakash J  
Assistant Professor  
Amrita Vishwa Vidyapeetham, Coimbatore

## Models Used:

- **ResNet-50**
- **ResNet-34**
- **EfficientNet-B0**
- **SE-Net**
- Custom model being generated

### Confusion Matrices:

- ResNet-50
- ResNet-34
- EfficientNet-B0

## Graphical Representation of Models:

### ResNet-50
![ResNet-50](path/to/resnet50-image.png)

### ResNet-34
![ResNet-34](path/to/resnet34-image.png)

### EfficientNet-B0
![EfficientNet-B0](path/to/efficientnetb0-image.png)

---

### Sequential Model (Input shape: 128 x 3 x 224 x 224)

| Layer (type)           | Output Shape        | Param #  | Trainable |
|------------------------|---------------------|----------|-----------|
| Conv2d                 | 128 x 64 x 112 x 11 | 9408     | False     |
| BatchNorm2d            | 128                 | True     |
| ReLU                   |                     |          |
| MaxPool2d              | 128 x 64 x 56 x 56  |          |           |
| Conv2d                 | 4096                | False    |
| BatchNorm2d            | 128                 | True     |
| Conv2d                 | 36864               | False    |
| BatchNorm2d            | 128                 | True     |
| Conv2d                 | 16384               | False    |
| BatchNorm2d            | 512                 | True     |
| ReLU                   |                     |          |
| Conv2d                 | 16384               | False    |
| BatchNorm2d            | 512                 | True     |
| Conv2d                 | 36864               | False    |
| BatchNorm2d            | 128                 | True     |
| Conv2d                 | 16384               | False    |
| BatchNorm2d            | 512                 | True     |

*(The table continues with further Conv2d, BatchNorm2d layers as outlined in the document)*

---

## Optimizer and Loss Function

- **Optimizer**: `Adam`
- **Loss Function**: Flattened Loss of CrossEntropyLoss

### Model Summary:

- **Total params**: 25,616,448
- **Trainable params**: 2,161,536
- **Non-trainable params**: 23,454,912

---

### Callbacks Used:

- TrainEvalCallback
- CastToTensor
- Recorder
- ProgressCallback

### Confusion Matrix for Ensemble Model:
![Ensemble Model Confusion Matrix](path/to/ensemble-model-image.png)

