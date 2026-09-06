---
title: 似然、负对数似然与交叉熵（Likelihood / NLL / Cross Entropy）
date: "2026-07-12"
categories:
  - math
description: 从最大似然目标出发，解释自回归分解下的负对数似然为什么等价于 one-hot 标签的交叉熵。
---
# 似然、负对数似然与交叉熵（Likelihood / NLL / Cross Entropy）

## 一句话

似然 $p_\theta(x)$ 表示：在参数为 $\theta$ 的模型世界里，观测到真实数据 $x$ 的概率或概率密度有多高。最大似然训练就是让真实数据在模型分布下尽可能“自然”。

## 层次位置

上游工具：
- [链式法则](../modules/ChainRule.md)：把联合概率拆成条件概率乘积；
- [期望](../modules/Expectation.md)：训练目标通常写成数据分布上的平均；
- [例子库](../examples/Examples.md)：交叉熵分解 $H(p,q)=H(p)+KL(p\|q)$。

本页关注：
- 最大似然目标；
- 负对数似然（NLL）；
- 自回归条件分解；
- one-hot 分类交叉熵。

下游主题：
- 语言模型、自回归图像模型、离散 token 生成模型的训练目标；
- diffusion / VAE / flow 等生成模型中的 likelihood 或 ELBO 目标。

## 最大似然

给定数据集 $\{x^{(i)}\}_{i=1}^N$，最大似然估计选择：

$$
\theta^*
=
\arg\max_\theta
\sum_i \log p_\theta(x^{(i)}).
$$

直觉是：如果参数 $\theta$ 合适，那么这些真实样本应该在模型分布 $p_\theta$ 下有较高概率或概率密度。

实践里常最小化负对数似然：

$$
\mathcal{L}_{\mathrm{NLL}}(\theta)
=
-\sum_i \log p_\theta(x^{(i)}).
$$

最大化 log-likelihood 和最小化 NLL 是同一件事。

## 自回归分解

若一个样本 $x$ 由 $K$ 个 token 或维度组成：

$$
x=(x_1,\ldots,x_K),
$$

则由链式法则：

$$
p_\theta(x)
=
\prod_{k=1}^K
p_\theta(x_k\mid x_{<k}).
$$

取对数：

$$
\log p_\theta(x)
=
\sum_{k=1}^K
\log p_\theta(x_k\mid x_{<k}).
$$

所以数据集上的 NLL 是：

$$
\mathcal{L}_{\mathrm{NLL}}(\theta)
=
-\sum_i\sum_k
\log p_\theta\left(x_k^{(i)}\mid x_{<k}^{(i)}\right).
$$

## 为什么等价于 one-hot 交叉熵

对某个位置 $k$，真实 token 是 $x_k$。把真实分布写成 one-hot：

$$
y(a)=
\begin{cases}
1, & a=x_k,\\
0, & a\ne x_k.
\end{cases}
$$

模型输出条件分布：

$$
q_\theta(a)=p_\theta(a\mid x_{<k}).
$$

交叉熵定义为：

$$
H(y,q_\theta)
=
-\sum_a y(a)\log q_\theta(a).
$$

由于 $y(a)$ 只有在真实 token 处为 1，其余为 0：

$$
H(y,q_\theta)
=
-\log q_\theta(x_k)
=
-\log p_\theta(x_k\mid x_{<k}).
$$

因此：

$$
\boxed{
\text{one-hot 交叉熵}
=
\text{单位置负对数似然}
}
$$

对所有样本和所有位置求和，就得到：

$$
\mathcal{L}(\theta)
=
-\sum_i\sum_k
\log p_\theta\left(x_k^{(i)}\mid x_{<k}^{(i)}\right).
$$

## 常见误区

- likelihood 是参数 $\theta$ 的函数；probability density 是对数据点 $x$ 的密度值。两者写法相同，视角不同。
- 最大似然不是让模型只记住一个最可能样本，而是让整个数据集中的样本在模型分布下都有高密度。
- 对 one-hot 标签，交叉熵只留下真实类别的 $-\log$ 概率；其它类别项乘以 0。

## 相关页

- 链式法则：见 [../modules/ChainRule.md](../modules/ChainRule.md)
- 期望：见 [../modules/Expectation.md](../modules/Expectation.md)
- 交叉熵分解例子：见 [../examples/Examples.md](../examples/Examples.md)
