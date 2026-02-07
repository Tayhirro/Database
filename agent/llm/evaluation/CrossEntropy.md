# 交叉熵损失（Cross-Entropy Loss）

导航：[evaluation/README.md](../README.md) | [Perplexity.md](./Perplexity.md)

**交叉熵**是语言模型训练中最核心的损失函数，衡量模型预测分布与真实分布之间的差异。

---

## 定义

对于离散概率分布，交叉熵定义为：

$$H(p, q) = -\sum_{i} p(i) \log q(i)$$

其中：
- $p$：真实分布（ground truth，one-hot编码）
- $q$：模型预测的分布（softmax输出）
- $i$：类别索引（词汇表中的token）

---

## 在语言模型中的应用

### 1. 单token的交叉熵

```python
# 真实标签：下一个token是 "猫"
target_token = "猫"
target_id = tokenizer.encode("猫")  # 假设为 42

# 模型预测：词汇表上每个token的概率分布
logits = model(input_ids)  # [vocab_size]
probs = softmax(logits)    # [0.01, 0.05, ..., 0.25, ...] 
                           # 假设 P(猫) = 0.25

# 交叉熵损失（one-hot情况下简化）
loss = -log(probs[target_id])  # -log(0.25) = 1.386
```

**简化公式**（one-hot目标）：
$$\mathcal{L} = -\log P_{\theta}(w_t | w_{<t})$$

### 2. 序列的交叉熵

```python
# 序列："我爱北京"
# 输入：["我", "爱", "北京"]
# 目标：["爱", "北", "京", "<EOS>"]

# 每个位置的损失
loss_t1 = -log P("爱" | "我")
loss_t2 = -log P("北" | "我爱")  
loss_t3 = -log P("京" | "我爱北")

# 平均交叉熵
avg_loss = (loss_t1 + loss_t2 + loss_t3) / 3
```

**公式**：
$$\mathcal{L} = -\frac{1}{T} \sum_{t=1}^{T} \log P_{\theta}(w_t | w_{<t})$$

---

## 直观理解

### 概率与损失的关系

| 模型信心 | 预测概率 | 交叉熵损失 |
|---------|---------|-----------|
| 非常确定 | 0.99 | -log(0.99) ≈ 0.01 |
| 比较确定 | 0.9 | -log(0.9) ≈ 0.105 |
| 一般 | 0.5 | -log(0.5) ≈ 0.693 |
| 不确定 | 0.1 | -log(0.1) ≈ 2.303 |
| 完全错误 | 0.01 | -log(0.01) ≈ 4.605 |

**特点**：
- 概率越接近1，损失越接近0
- 概率越接近0，损失趋向+∞
- 惩罚非常不对称：置信错误（低概率猜中）惩罚远大于不确定

### 为什么用 log？

```python
# 信息量角度：
# 一个概率为 p 的事件，携带的信息量为 -log(p)

# 如果 p = 1（必然事件），信息量 = 0（没有惊喜）
# 如果 p = 0.001（罕见事件），信息量 = 6.9（很大惊喜）

# 交叉熵 = 期望信息量
# 衡量用模型编码真实数据需要多少bit
```

---

## 与信息论的关系

### KL散度（相对熵）

$$D_{KL}(p||q) = H(p, q) - H(p)$$

- $H(p)$：真实分布的熵（常数）
- $H(p, q)$：交叉熵
- 最小化交叉熵 ≈ 最小化KL散度

**含义**：让模型分布 $q$ 尽可能接近真实分布 $p$。

---

## 代码实现

### PyTorch

```python
import torch
import torch.nn as nn

# 方法1：使用CrossEntropyLoss（推荐）
criterion = nn.CrossEntropyLoss()

# logits: [batch_size, seq_len, vocab_size]
# targets: [batch_size, seq_len]
loss = criterion(logits.view(-1, vocab_size), targets.view(-1))

# 方法2：手动计算
probs = torch.softmax(logits, dim=-1)
# 使用one-hot选择目标概率
target_probs = probs.gather(-1, targets.unsqueeze(-1)).squeeze(-1)
loss = -torch.log(target_probs).mean()
```

### 带mask的变长序列

```python
# 处理padding
loss = criterion(logits.view(-1, vocab_size), targets.view(-1))

# 或者手动计算时忽略padding
mask = (targets != pad_token_id).float()
loss = (-torch.log(target_probs) * mask).sum() / mask.sum()
```

---

## 常见问题

### Q1: 交叉熵为负数？

**不可能**。因为：
- softmax输出 ∈ (0, 1)
- log(x) < 0 当 x < 1
- -log(x) > 0

### Q2: 损失无穷大？

**数值稳定性问题**：
```python
# 错误：softmax后取log
probs = softmax(logits)  # 可能下溢为0
loss = -log(probs)       # log(0) = -inf

# 正确：使用log_softmax
log_probs = log_softmax(logits, dim=-1)
loss = -log_probs.gather(-1, targets.unsqueeze(-1))
```

### Q3: 多任务学习中的权重？

```python
# 不同任务可能有不同权重
total_loss = λ1 * ce_loss1 + λ2 * ce_loss2 + λ3 * ce_loss3
```

---

## 相关概念

| 概念 | 关系 |
|------|------|
| **困惑度** | PPL = exp(CE)，见 [Perplexity.md](./Perplexity.md) |
| **KL散度** | D_KL = CE - 熵 |
| **负对数似然** | NLL = CE（在分类问题中等价） |
| **Bits Per Character** | BPC = CE / ln(2)，以bit为单位 |

---

## 参考

- [Perplexity.md](./Perplexity.md) - 交叉熵的指数形式
- [SelfSupervised.md](../training/paradigms/SelfSupervised.md) - 语言模型训练范式
