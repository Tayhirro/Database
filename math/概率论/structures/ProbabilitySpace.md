---
title: 概率空间（Probability Space）
date: "2026-03-29"
categories:
  - math
description: 概率空间是概率论的基础对象，写作 (Ω, F, P)，其中 Ω 是样本空间，F 是事件的 σ-代数，P 是定义在事件上的概率测度。
---
# 概率空间（Probability Space）

## 定义

概率空间是概率论中描述随机现象的基础结构，通常记作

$$
(\Omega, \mathcal{F}, \mathbb{P}).
$$

其中，$\Omega$ 是样本空间，$\mathcal{F}$ 是定义在 $\Omega$ 上的 $\sigma$-代数，$\mathbb{P}$ 是定义在 $\mathcal{F}$ 上的概率测度。

这三个对象承担的角色不同。$\Omega$ 给出“所有可能发生的原始结果”；$\mathcal{F}$ 指定“哪些结果集合被允许当作事件来谈论”；$\mathbb{P}$ 则为这些事件赋予概率。

## 三个组成部分

样本空间 $\Omega$ 是最底层的结果集合。若掷一次硬币，则可取

$$
\Omega = \{H, T\}.
$$

若连续观察一个实数值结果，例如一次测量所得长度，则常把 $\Omega$ 理解为更抽象的结果集合，而不直接等同于实数轴。

$\mathcal{F}$ 是 $\Omega$ 上的事件集合。它不是任意子集的随意拼装，而必须满足 $\sigma$-代数的封闭性条件。直观上，$\mathcal{F}$ 收集的是“可以合法谈论概率”的那些事件。

$\mathbb{P}$ 是定义在 $\mathcal{F}$ 上的函数

$$
\mathbb{P}: \mathcal{F} \to [0,1],
$$

满足

$$
\mathbb{P}(\Omega)=1
$$

以及对两两不交事件列 $\{A_n\}_{n \ge 1}$ 有

$$
\mathbb{P}\!\left(\bigcup_{n=1}^{\infty} A_n\right)
=
\sum_{n=1}^{\infty}\mathbb{P}(A_n).
$$

## $\sigma$-代数到底是什么

$\sigma$-代数 $\mathcal{F}$ 是 $\Omega$ 的子集族，并满足三条条件：

$$
\Omega \in \mathcal{F},
$$

若 $A \in \mathcal{F}$，则

$$
A^c \in \mathcal{F},
$$

若 $A_1,A_2,\dots \in \mathcal{F}$，则

$$
\bigcup_{n=1}^{\infty} A_n \in \mathcal{F}.
$$

因此，$\mathcal{F}$ 对补集和可数并封闭，也因此对可数交封闭。它的作用是规定“什么算事件”。概率测度 $\mathbb{P}$ 只对这些事件定义。

对初学者而言，可以先把 $\mathcal{F}$ 理解成“信息允许我们区分出来的事件集合”。它不是额外的几何空间，也不是随机变量所在的空间，而是 $\Omega$ 上一批可测子集的集合。

## 随机变量和概率空间的关系

随机变量 $X$ 不是 $\mathcal{F}$ 里的元素。随机变量是定义在 $\Omega$ 上的函数，通常写成

$$
X:\Omega \to \mathbb{R}.
$$

它把每个原始结果 $\omega \in \Omega$ 映射成一个数值。随机变量要能被称为“随机变量”，还要求它满足可测性条件；这一点不是概率空间本身的一部分，而是函数与 $\sigma$-代数之间的相容条件。

因此，概率空间先提供“底层结果 + 可谈论事件 + 概率”，随机变量再建立在这个基础上，把原始结果转换成数值对象。

## 一个最小例子

设掷两次硬币，则可以取

$$
\Omega = \{HH, HT, TH, TT\}.
$$

若取

$$
\mathcal{F} = 2^{\Omega},
$$

即 $\Omega$ 的幂集，那么每一个结果集合都可以当作事件。再定义概率

$$
\mathbb{P}(\{\omega\}) = \frac14, \qquad \omega \in \Omega.
$$

这就得到一个最基本的有限概率空间。

在这个空间上，可以定义随机变量 $X=$“正面朝上的次数”。于是

$$
X(HH)=2,\quad X(HT)=1,\quad X(TH)=1,\quad X(TT)=0.
$$

这个例子说明：概率空间描述的是原始不确定性，随机变量描述的是从原始不确定性中读取出的数值信息。

## 相关页

- 随机变量：见 [RandomVariable.md](RandomVariable.md)
- 期望：见 [../modules/Expectation.md](../modules/Expectation.md)
- 独立性：见 [../modules/Independence.md](../modules/Independence.md)
