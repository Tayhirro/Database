---
title: 独立性与条件独立（Independence & Conditional Independence）
date: "2026-03-29"
categories:
  - math
description: 独立与条件独立的标准定义、常用等价形式以及符号 ⊥ 的严格含义。
---
# 独立性与条件独立（Independence & Conditional Independence）

## 1. 定义

设 $(\Omega, \mathcal{F}, \mathbb{P})$ 是概率空间。若事件 $A,B \in \mathcal{F}$ 满足

$$
\mathbb{P}(A \cap B) = \mathbb{P}(A)\mathbb{P}(B),
$$

则称事件 $A$ 与 $B$ 相互独立。

若随机变量 $X$ 与 $Y$ 所生成的 $\sigma$-代数相互独立，则称随机变量 $X$ 与 $Y$ 独立，记作

$$
X \perp Y.
$$

这里的 $\sigma(X)$ 可以理解成“所有只靠 $X$ 的取值就能判断的事件集合”。关于原像 $X^{-1}(A)$、可测性以及 $\sigma(X)$ 的来历，见 [../structures/RandomVariable.md](../structures/RandomVariable.md)。

在离散或连续情形下，常用下列表达：

$$
p(x,y) = p(x)p(y),
$$

或等价地，

$$
p(x \mid y) = p(x),
$$

前提是相应条件概率或条件密度有定义。

## 2. 条件独立

设 $X,Y,Z$ 是定义在同一概率空间上的随机变量。若在给定 $Z$ 的条件下，$X$ 与 $Y$ 的联合条件分布分解为边缘条件分布之积，即

$$
p(x,y \mid z) = p(x \mid z)p(y \mid z),
$$

则称 $X$ 与 $Y$ 在给定 $Z$ 的条件下条件独立，记作

$$
X \perp Y \mid Z.
$$

更严格地说，若对任意可测集合 $A$ 与 $B$，都有

$$
\mathbb{P}(X \in A, Y \in B \mid Z)
=
\mathbb{P}(X \in A \mid Z)\,\mathbb{P}(Y \in B \mid Z)
\quad \text{a.s.},
$$

则称 $X$ 与 $Y$ 在给定 $Z$ 的条件下条件独立。

在相应条件概率定义良好的地方，上述定义也可写成

$$
p(x \mid y,z) = p(x \mid z),
$$

或

$$
p(y \mid x,z) = p(y \mid z).
$$

## 3. 记号说明

符号 $\perp$ 在概率论中表示独立性。这个符号的来源是记号约定，不表示几何中的垂直关系。

因此，

$$
y_t \perp P_S^t(u) \mid P_C^t(u)
$$

应读作：在给定 $P_C^t(u)$ 的条件下，$y_t$ 与 $P_S^t(u)$ 条件独立。

这句话的严格含义是：一旦 $P_C^t(u)$ 已知，$P_S^t(u)$ 不再为 $y_t$ 提供额外的概率信息。若使用条件分布表示，则对应

$$
p\!\left(y_t \mid P_S^t(u), P_C^t(u)\right)
=
p\!\left(y_t \mid P_C^t(u)\right).
$$

## 4. 与相关性、协方差的关系

独立性是关于联合分布分解的性质，强于“协方差为零”或“相关系数为零”。

若 $X$ 与 $Y$ 独立，且相关期望存在，则有

$$
\mathbb{E}[XY] = \mathbb{E}[X]\mathbb{E}[Y],
$$

从而

$$
\operatorname{Cov}(X,Y) = 0.
$$

反向结论一般不成立。协方差为零只说明线性相关性消失，不足以推出独立。

## 5. 在后续主题中的作用

独立性和条件独立是概率论与统计推断中的基础概念。条件概率、条件期望、Bayes 公式、图模型中的 d-separation，以及因果推断中的识别准则，都以这两个概念为前提。

在因果图中看到的

$$
X \perp Y \mid Z
$$

是概率分布层面的条件独立陈述；图上的 d-separation 则是读取这类陈述的图判据。两者不能混同。

## 6. 速查

| 记号 | 含义 |
|------|------|
| $X \perp Y$ | $X$ 与 $Y$ 独立 |
| $X \perp Y \mid Z$ | 给定 $Z$ 时，$X$ 与 $Y$ 条件独立 |
| $p(x,y)=p(x)p(y)$ | 独立的联合分布分解 |
| $p(x,y \mid z)=p(x \mid z)p(y \mid z)$ | 条件独立的联合条件分布分解 |
| $p(x \mid y,z)=p(x \mid z)$ | 条件独立的等价写法 |
