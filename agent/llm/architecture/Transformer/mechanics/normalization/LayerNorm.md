---
title: LayerNorm
date: "2026-02-05"
categories:
  - agent
tags:
  - normalization
  - transformer
  - mean-subtraction
description: 沿特征维度减去均值、除以标准差，并通过可学习的 γ 缩放与 β 平移进行变换的归一化方法。
aliases:
  - Layer Normalization
  - 层归一化
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
- $\gamma, \beta \in \mathbb{R}^d$（可学习参数）
- $\epsilon$（数值稳定性常数，默认 $10^{-5}$）

## 接口：数据 + 约束

| 符号 | 类型 | 约束 | 说明 |
|------|------|------|------|
| $x$ | $\mathbb{R}^d$ 或 $\mathbb{R}^{B \times L \times d}$ | 任意实数 | 输入特征，最后一维为特征维 |
| $\gamma$ | $\mathbb{R}^d$ | 可学习，初始化为 1 | 逐元素缩放 |
| $\beta$ | $\mathbb{R}^d$ | 可学习，初始化为 0 | 逐元素平移 |
| $\epsilon$ | $\mathbb{R}$ | 常数，默认 $10^{-5}$ | 防止除零 |

## 核心特性

### 1. 中心化（Mean Subtraction）

减去均值 $\mu$，将输入的分布中心移到零点：

$$x' = x - \mu$$

- 消除输入的"直流偏移"（DC offset）
- 使输出均值为 0
- **代价**：这是一次**向量减法**，会改变向量方向（详见 [[Norm对比总结]]）

### 2. 方差归一化（Variance Normalization）

除以标准差 $\sigma$，将分布的宽度压缩到单位尺度：

$$x'' = \frac{x'}{\sqrt{\sigma^2 + \epsilon}}$$

- 消除不同特征维度之间的尺度差异
- 使输出方差为 1

### 3. 可学习仿射变换（Affine Transform）

通过 $\gamma$（缩放）和 $\beta$（平移）恢复表达能力：

$$y = \gamma \cdot x'' + \beta$$

- $\gamma$ 允许网络选择性放大/缩小某些特征
- $\beta$ 允许网络恢复必要的偏移
- **参数量**: $2d$（$\gamma$ 和 $\beta$ 各 $d$ 维）

## 几何意义

LayerNorm 执行的是**仿射变换**（Affine Transform）：

```
原始空间 → 平移到原点 → 缩放到单位方差 → 平移到新位置
         (减均值 μ)    (除以 σ)          (加 β)
```

- 改变了向量的**方向**和**模长**
- 相当于对特征空间进行坐标系变换

### 数值示例

输入 $x = [2.0, -1.0, 3.0]$，$d = 3$：

1. 均值：$\mu = \frac{2 - 1 + 3}{3} = 1.33$
2. 中心化：$x - \mu = [0.67, -2.33, 1.67]$（方向改变！）
3. 方差：$\sigma^2 = \frac{0.67^2 + 2.33^2 + 1.67^2}{3} = 2.89$
4. 归一化：$[0.39, -1.37, 0.98]$
5. 经 $\gamma, \beta$ 仿射变换后输出

## 计算步骤

```
1. 计算均值 μ:     O(d)
2. 减去均值 x - μ:  O(d)
3. 计算方差 σ²:     O(d)
4. 除以标准差:      O(d)
5. 缩放 + 平移:     O(d)
总计: ~5d 次操作
```

## 实现

```python
# PyTorch 内置
layer_norm = nn.LayerNorm(d, eps=1e-5)

# 等价手动实现
def layer_norm(x, gamma, beta, eps=1e-5):
    mu = x.mean(-1, keepdim=True)
    sigma = x.std(-1, keepdim=True, unbiased=False)
    return gamma * (x - mu) / (sigma + eps) + beta
```

## 适用场景

| 场景 | 原因 |
|------|------|
| Transformer Encoder（BERT） | 经典选择，稳定性好 |
| 输入分布偏移较大 | 中心化能有效消除偏移 |
| 需要平移自由度 | $\beta$ 提供额外表达能力 |
| CNN / RNN | 输入不一定零均值，需要完整归一化 |

## 关系

- **下级/简化**: [[RMSNorm]] —— 移除 $\mu$ 和 $\beta$ 的轻量级变体
- **对比**: [[Norm对比总结]] —— 与其他归一化方法的系统对比
- **相关**: Bias（偏置） —— $\beta$ 属于"平移/偏置"项的一个实例
- **上级**: [[归一化]] → [[深度学习]]
