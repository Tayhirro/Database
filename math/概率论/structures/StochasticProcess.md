---
title: 随机过程（Stochastic Process）
date: "2026-07-12"
categories:
  - math
description: 随机过程是一族按时间索引的随机变量；dX_t 表示过程的小时间增量，而不一定是普通导数。
---
# 随机过程（Stochastic Process）

## 一句话

随机过程 $\{X_t\}_{t\ge 0}$ 是一族按时间编号的随机变量。可以把 $X_t$ 理解成“系统在时刻 $t$ 的随机状态”。

## 层次位置

上游对象：
- [概率空间](ProbabilitySpace.md)：提供原始结果、事件和概率；
- [随机变量](RandomVariable.md)：随机过程中的每个 $X_t$ 都是一个随机变量。

本页关注：
- $X_t$ 作为一族随机变量的含义；
- 固定 $t$ 和固定 $\omega$ 的区别；
- $dX_t$ 为什么先理解成小时间增量。

下游对象/工具：
- [布朗运动](../processes/BrownianMotion.md)：一种特殊随机过程；
- [随机微分方程（SDE）主线](../modules/SDE.md)：用 $dt$ 和 $dW_t$ 描述随机过程的增量，并继续走到密度演化；
- [随机积分附录](../modules/StochasticIntegral.md)：需要细看布朗增量如何累加时再打开。

## 从随机变量到随机过程

随机变量 $X$ 是一个从样本空间到数值空间的可测函数：

$$
X:\Omega\to\mathbb{R}.
$$

随机过程则是很多个随机变量排成一条时间线：

$$
X_0,\;X_{0.1},\;X_1,\;X_2,\ldots
$$

更正式地说：

$$
X_t:\Omega\to\mathbb{R},\qquad t\ge 0.
$$

固定一个时间 $t$，$X_t$ 是随机变量；固定一个原始结果 $\omega$，函数

$$
t\mapsto X_t(\omega)
$$

是一条样本路径。

## $dX_t$ 是什么意思

当写

$$
dX_t
$$

时，意思不是对单个随机变量做新的代数操作，而是在说随机过程的一小段时间增量：

$$
dX_t \quad\text{对应}\quad X_{t+\Delta t}-X_t.
$$

如果这个过程的样本路径足够光滑，那么可以有普通导数。但很多随机过程，尤其是布朗运动驱动的过程，不具备普通导数。

## 可普通求导的例子

令

$$
X_t=tZ,\qquad Z\sim\mathcal{N}(0,1).
$$

一次实验先抽出一个 $Z$，之后整条路径就是普通函数 $t\mapsto tZ$。所以：

$$
\frac{dX_t}{dt}=Z.
$$

导数本身也是随机变量。并且：

$$
X_t\sim\mathcal{N}(0,t^2),
\qquad
\frac{dX_t}{dt}\sim\mathcal{N}(0,1).
$$

再比如：

$$
X_t=t^2Z+3t.
$$

则：

$$
\frac{dX_t}{dt}=2tZ+3.
$$

固定 $t$ 时：

$$
\frac{dX_t}{dt}\sim\mathcal{N}(3,4t^2).
$$

这里没有随机微积分，只有“每条路径都是普通可导函数”。

## 布朗运动为什么不能普通求导

布朗运动 $W_t$ 对每个固定的 $t$ 都是随机变量：

$$
W_t\sim\mathcal{N}(0,t).
$$

但它的样本路径几乎处处不可导。看差商：

$$
\frac{W_{t+\Delta t}-W_t}{\Delta t}.
$$

因为布朗增量满足：

$$
W_{t+\Delta t}-W_t\sim\mathcal{N}(0,\Delta t),
$$

所以：

$$
\frac{W_{t+\Delta t}-W_t}{\Delta t}
\sim
\mathcal{N}\left(0,\frac{1}{\Delta t}\right).
$$

当 $\Delta t\to0$ 时，方差发散。因此不能把 $dW_t$ 理解成普通导数 $W'(t)dt$。

## 和 SDE 记号的关系

随机微分方程

$$
dX_t=f(X_t,t)\,dt+g(t)\,dW_t
$$

应当先理解成小步增量规则：

$$
X_{t+\Delta t}-X_t
\approx
f(X_t,t)\Delta t
+
g(t)\sqrt{\Delta t}\epsilon,
\qquad
\epsilon\sim\mathcal{N}(0,1).
$$

这里的 $dX_t$ 是过程增量，$dW_t$ 是布朗运动增量。它不是普通 ODE：

$$
\frac{dX_t}{dt}=f(X_t,t)+g(t)\frac{dW_t}{dt}.
$$

右边的 $dW_t/dt$ 通常不存在。

SDE 本体的积分形式、小步离散形式和例子见 [../modules/SDE.md](../modules/SDE.md)。

## 容易混淆

| 写法 | 正确理解 |
|---|---|
| $X_t$ | 时刻 $t$ 的随机变量 |
| $t\mapsto X_t(\omega)$ | 固定一次实验后的样本路径 |
| $dX_t$ | 随机过程的一小段时间增量 |
| $\frac{dX_t}{dt}$ | 只有样本路径足够光滑时才有普通意义 |
| $dW_t$ | 布朗运动的小随机增量，不是 $W'(t)dt$ |

## 相关页

- 随机变量：见 [RandomVariable.md](RandomVariable.md)
- 布朗运动：见 [../processes/BrownianMotion.md](../processes/BrownianMotion.md)
- 随机微分方程（SDE）主线：见 [../modules/SDE.md](../modules/SDE.md)
- 随机积分附录：见 [../modules/StochasticIntegral.md](../modules/StochasticIntegral.md)
