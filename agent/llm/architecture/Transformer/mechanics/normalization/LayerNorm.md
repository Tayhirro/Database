---
aliases: [Layer Normalization, 层归一化]
tags: [normalization, transformer, mean-subtraction]
---

# LayerNorm

## 一句话

沿特征维度减去均值、除以标准差，并通过可学习的 $\gamma$ 缩放与 $\beta$ 平移进行变换的归一化方法。

## 严格定义

$$
y = \gamma \cdot \frac{x - \mu}{\sqrt{\sigma^2 + \epsilon}} + \beta
$$

其中：
- $\mu = \frac{1}{d}\sum_{i=1}^d x_i$（逐样本特征均值）
- $\sigma^2 = \frac{1}{d}\sum_{i=1}^d (x_i - \mu)^2$（逐样本特征方差）

## 与 RMSNorm 的核心差异

### 1. 中心化（Mean Subtraction）
- **LayerNorm**: 必须减去 $\mu$，使输出均值为0
- **RMSNorm**: 不减去 $\mu$，保留输入的均值信息

### 2. 参数数量
- **LayerNorm**: 2d 参数（$\gamma$ 和 $\beta$ 各 d 维）
- **RMSNorm**: d 参数（仅 $\gamma$）

### 3. 数值稳定性场景
在 LLM 的残差连接中，输入通常已通过前一层归一化近似零均值，此时显式减 $\mu$ 成为冗余计算，RMSNorm 通过省略此步骤提升 throughput。

## 关系

- **下级/简化**: [[RMSNorm]] —— 移除 $\mu$ 和 $\beta$ 的轻量级变体
- **相关**: [Bias（偏置/截距）](Bias.md) —— $\beta$ 属于“平移（shift）/偏置”项的一个实例
- **上级**: [[归一化]] → [[深度学习]]
