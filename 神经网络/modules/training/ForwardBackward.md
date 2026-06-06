---
title: 前向传播与反向传播（Forward & Backward Propagation）
date: "2026-05-14"
categories:
  - 神经网络
description: 神经网络训练的核心流程：前向传播计算输出，反向传播计算梯度，优化器更新参数。
---

# 前向传播与反向传播（Forward & Backward Propagation）

## 1. 一句话
- 前向传播：输入数据经过层层计算得到输出
- 反向传播：从输出反向计算梯度，用于更新参数
---

## 2. 前向传播（Forward Propagation）

### 单层计算

$$z = Wx + b$$
$$a = \sigma(z)$$

其中：
- $x$：输入向量
- $W$：权重矩阵
- $b$：偏置向量
- $z$：线性变换结果
- $\sigma$：激活函数
- $a$：激活后的输出

### 多层网络

$$
\begin{aligned}
a^{(0)} &= x \quad \text{（输入层）} \\
z^{(l)} &= W^{(l)} a^{(l-1)} + b^{(l)} \\
a^{(l)} &= \sigma(z^{(l)}) \\
\hat{y} &= a^{(L)} \quad \text{（输出层）}
\end{aligned}
$$

---

## 3. 损失计算

$$L = \mathcal{L}(\hat{y}, y)$$

常见损失函数：
- MSE：$L = \frac{1}{2}(\hat{y} - y)^2$
- 交叉熵：$L = -\sum y_i \log \hat{y}_i$

详见 [Loss.md](Loss.md)

---

## 4. 反向传播（Backward Propagation）

### 核心思想

利用链式法则，从输出层向输入层逐层计算梯度。

### 链式法则

$$\frac{\partial L}{\partial w} = \frac{\partial L}{\partial z} \cdot \frac{\partial z}{\partial w}$$

### 逐层计算

**输出层**：
$$\delta^{(L)} = \frac{\partial L}{\partial z^{(L)}} = \frac{\partial L}{\partial a^{(L)}} \cdot \sigma'(z^{(L)})$$

**隐藏层**：
$$\delta^{(l)} = (W^{(l+1)})^T \delta^{(l+1)} \cdot \sigma'(z^{(l)})$$

**参数梯度**：
$$\frac{\partial L}{\partial W^{(l)}} = \delta^{(l)} (a^{(l-1)})^T$$
$$\frac{\partial L}{\partial b^{(l)}} = \delta^{(l)}$$

---

## 5. 参数更新

$$W^{(l)} \leftarrow W^{(l)} - \eta \frac{\partial L}{\partial W^{(l)}}$$
$$b^{(l)} \leftarrow b^{(l)} - \eta \frac{\partial L}{\partial b^{(l)}}$$

其中 $\eta$ 为学习率。

详见 [Optimizer.md](Optimizer.md)

---

## 6. 完整训练流程

```
for each epoch:
    for each batch (x, y):
        # 前向传播
        a = x
        for l = 1 to L:
            z = W[l] @ a + b[l]
            a = sigma(z)
        y_hat = a
        
        # 计算损失
        L = loss(y_hat, y)
        
        # 反向传播
        delta = dL/dz[L]
        for l = L to 1:
            dW[l] = delta @ a[l-1].T
            db[l] = delta
            delta = W[l].T @ delta * sigma'(z[l-1])
        
        # 参数更新
        for l = 1 to L:
            W[l] -= eta * dW[l]
            b[l] -= eta * db[l]
```

---

## 7. 实现细节

### 7.1 PyTorch nn.Module

**核心思想**：所有网络层都继承自 `nn.Module`，自动管理参数和前向传播。

**基本结构**：

```python
import torch
import torch.nn as nn

class MyLayer(nn.Module):
    def __init__(self, in_features, out_features):
        super().__init__()
        # 定义参数
        self.weight = nn.Parameter(torch.randn(out_features, in_features))
        self.bias = nn.Parameter(torch.zeros(out_features))
    
    def forward(self, x):
        # 前向传播
        return x @ self.weight.T + self.bias
```

**内置层示例**：

```python
# 线性层
linear = nn.Linear(in_features=784, out_features=256)

# 激活函数
relu = nn.ReLU()
sigmoid = nn.Sigmoid()

# 组合网络
model = nn.Sequential(
    nn.Linear(784, 256),
    nn.ReLU(),
    nn.Linear(256, 10)
)
```

**自动求导**：

```python
x = torch.randn(1, 784, requires_grad=True)
y = model(x)
loss = nn.CrossEntropyLoss()(y, target)
loss.backward()  # 自动计算梯度
# 梯度存储在 x.grad 和 model.parameters() 中
```

### 7.2 计算图与动态图构建（待补充）

### 7.3 自动微分原理（待补充）

### 7.4 C++底层实现（待补充）

---

## 8. 速查

| 概念 | 公式 | 含义 |
|------|------|------|
| 前向 | $z = Wx + b, a = \sigma(z)$ | 计算输出 |
| 损失 | $L = \mathcal{L}(\hat{y}, y)$ | 衡量误差 |
| 反向 | $\delta^{(l)} = (W^{(l+1)})^T \delta^{(l+1)} \cdot \sigma'(z^{(l)})$ | 计算梯度 |
| 更新 | $W \leftarrow W - \eta \nabla_W L$ | 更新参数 |

---

## 9. 相关模块

- [Initialization.md](Initialization.md)：参数初始化
- [Loss.md](Loss.md)：损失函数
- [Optimizer.md](Optimizer.md)：优化器
