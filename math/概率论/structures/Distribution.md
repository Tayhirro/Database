---
title: 分布（Distribution / Law）
date: "2026-07-12"
categories:
  - math
description: 分布是随机变量在值域空间上诱导出的概率测度；概率质量、概率密度、PMF、PDF、CDF 都是描述分布的不同方式。
---
# 分布（Distribution / Law）

## 一句话

分布回答的是：

$$
\text{随机变量 }X\text{ 落到某个集合 }A\text{ 里的概率是多少？}
$$

正式写法是：

$$
P_X(A)=\mathbb{P}(X\in A)=\mathbb{P}(X^{-1}(A)).
$$

这里：

- $\mathbb{P}$ 是样本空间 $\Omega$ 上的原始概率；
- $P_X$ 是随机变量 $X$ 在值域空间上诱导出来的分布；
- $A$ 是值域空间里的集合，例如区间、区域、类别集合。

所以分布不是“一个公式”本身，而是一个规则：

$$
A \longmapsto P_X(A).
$$

也就是：给我一个集合 $A$，我告诉你 $X$ 落在 $A$ 里的概率。

## 和随机变量的关系

随机变量是函数：

$$
X:\Omega\to S.
$$

它把底层结果 $\omega$ 映射成值域里的数值或对象 $X(\omega)$。分布则是把样本空间上的概率 $\mathbb{P}$ 推到值域空间 $S$ 上。

这一步叫推前（pushforward）：

$$
P_X = X_\#\mathbb{P}.
$$

通俗地说：

- 随机变量 $X$ 负责“把结果变成数值”；
- 分布 $P_X$ 负责“描述这些数值出现的概率规律”。

## 概率质量是什么

概率质量（probability mass）不是一个新的基本单位。它就是某个点或某个区域里总共有多少概率。

如果 $A$ 是值域空间里的集合，那么：

$$
\text{集合 }A\text{ 里的概率质量}
=
P_X(A)
=
\mathbb{P}(X\in A).
$$

概率质量满足：

$$
0\le P_X(A)\le 1,
\qquad
P_X(S)=1.
$$

其中 $S$ 是随机变量的整个值域空间。

直觉上，分布像一团概率材料。概率质量问的是“一块区域里一共有多少材料”。不同分布只是描述这团材料在空间里怎么摆放。

## 离散分布：点上可以有质量

如果 $X$ 是离散随机变量，例如 $X\in\{0,1,2\}$，可以直接问每个点的概率：

$$
p_X(x)=P_X(\{x\})=\mathbb{P}(X=x).
$$

这个 $p_X(x)$ 叫概率质量函数（PMF, probability mass function）。

例如：

$$
\mathbb{P}(X=0)=0.2,\qquad
\mathbb{P}(X=1)=0.5,\qquad
\mathbb{P}(X=2)=0.3.
$$

那么：

$$
P_X(\{0,2\})=0.2+0.3=0.5.
$$

离散情形下，总概率由求和得到：

$$
\sum_x p_X(x)=1.
$$

## 连续分布：点上通常没有质量

如果 $X$ 是连续随机变量，通常有：

$$
\mathbb{P}(X=x)=0.
$$

这不是说 $x$ 不可能出现，而是说连续空间里单个点太薄，真正有概率的是区间或区域。

如果分布有概率密度函数（PDF, probability density function）$p_X(x)$，那么区域 $A$ 的概率质量由积分给出：

$$
P_X(A)
=
\mathbb{P}(X\in A)
=
\int_A p_X(x)\,dx.
$$

例如一维区间：

$$
\mathbb{P}(a\le X\le b)
=
\int_a^b p_X(x)\,dx.
$$

连续情形下，总概率由全空间积分得到：

$$
\int_S p_X(x)\,dx=1.
$$

## 密度和质量的区别

| 名称 | 符号 | 问的问题 | 结果 |
|---|---|---|---|
| 概率质量 | $P_X(A)$ | 区域 $A$ 里一共有多少概率？ | 一个 $[0,1]$ 之间的数 |
| 概率密度 | $p_X(x)$ | 点 $x$ 附近单位空间有多“浓”？ | 可以大于 $1$ |
| 概率质量函数 | $p_X(x)=P(X=x)$ | 离散点 $x$ 上有多少概率？ | 一个 $[0,1]$ 之间的数 |

容易混的是：连续分布里的 $p_X(x)$ 是密度，不是点概率。真正的概率要对区域积分：

$$
\text{概率质量}
=
\text{密度在区域上的积分}.
$$

## CDF：用左侧区域描述分布

实值随机变量还有一种常用描述方式，叫累积分布函数（CDF）：

$$
F_X(x)=\mathbb{P}(X\le x).
$$

它问的是：$X$ 落在 $(-\infty,x]$ 这段区域里的概率质量是多少。

如果 $X$ 有密度 $p_X$，那么：

$$
F_X(x)=\int_{-\infty}^x p_X(u)\,du.
$$

反过来，在足够光滑的地方：

$$
p_X(x)=\frac{d}{dx}F_X(x).
$$

所以 CDF、PDF、PMF 都是在描述同一个东西：分布。

## 在随机过程里的分布

随机过程是一族随机变量：

$$
\{X_t\}_{t\ge 0}.
$$

固定一个时间 $t$，$X_t$ 本身就是随机变量，因此它有自己的分布：

$$
X_t\sim P_t.
$$

如果 $P_t$ 有密度，就写成：

$$
X_t\sim p_t(x).
$$

在 SDE 和 Fokker-Planck 里，$p_t(x)$ 表示时刻 $t$ 的概率密度。说“概率质量被搬运/扩散”，意思是某个区域 $A$ 里的总概率

$$
\int_A p_t(x)\,dx
$$

会随着时间变化。

因此：

- SDE 描述单个随机状态 $X_t$ 怎么动；
- Fokker-Planck 描述分布 $P_t$ 或密度 $p_t$ 怎么随时间变化。

## 速查

| 说法 | 含义 |
|---|---|
| $X\sim P_X$ | 随机变量 $X$ 的分布是 $P_X$ |
| $X\sim p_X(x)$ | 口语化写法，表示 $X$ 有密度 $p_X$ |
| $P_X(A)$ | $X$ 落在集合 $A$ 里的概率 |
| $p_X(x)$ | 连续情形下的概率密度，或离散情形下的概率质量函数，需看上下文 |
| $F_X(x)$ | 累积分布函数，$P(X\le x)$ |

## 相关页

- 概率空间：见 [ProbabilitySpace.md](ProbabilitySpace.md)
- 随机变量：见 [RandomVariable.md](RandomVariable.md)
- 随机过程：见 [StochasticProcess.md](StochasticProcess.md)
- 常见分布条目：见 [../distributions/README.md](../distributions/README.md)
- SDE 主线：见 [../modules/SDE.md](../modules/SDE.md)
