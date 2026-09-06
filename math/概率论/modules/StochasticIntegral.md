---
title: 随机积分（Stochastic Integral）
date: "2026-07-12"
categories:
  - math
description: 随机积分 ∫g(s)dW_s 是对布朗运动增量的加权累加；它解释了 SDE 中 dW_t 项如何从 0 积分到 t。
---
# 随机积分（Stochastic Integral）

> [!note]
> 这是 SDE 主线的附录页。先读 [随机微分方程（SDE）主线](SDE.md)，只有当你想弄清楚 $\int dW_s$、$\int g(s)dW_s$ 的积分含义时再看本页。

## 一句话

随机积分

$$
\int_0^t g(s)\,dW_s
$$

可以先理解成“把很多个布朗运动小增量 $dW_s$ 按系数 $g(s)$ 加权累加起来”。

## 层次位置

上游对象：
- [随机过程](../structures/StochasticProcess.md)：提供时间索引的随机变量 $X_t$；
- [布朗运动](../processes/BrownianMotion.md)：提供增量 $dW_t$。

本页关注：
- $\int_0^t dW_s$ 怎么等于 $W_t$；
- $\int_0^t g(s)dW_s$ 的离散累加直觉；
- 简单 SDE 从微分形式变成积分形式。

下游主题：
- [随机微分方程](SDE.md)：把普通时间积分和随机积分合在同一个增量方程里；
- [从 SDE 到 Fokker-Planck](SDEToFokkerPlanck.md)：从 SDE 推出密度演化。

## 最简单的积分：$\int_0^t dW_s$

把区间 $[0,t]$ 切成很多小段：

$$
0=t_0<t_1<\cdots<t_n=t.
$$

每一小段上的 $dW_s$ 对应布朗增量：

$$
W_{t_{i+1}}-W_{t_i}.
$$

所以：

$$
\int_0^t dW_s
\approx
\sum_{i=0}^{n-1}\left(W_{t_{i+1}}-W_{t_i}\right).
$$

这个求和会望远镜相消：

$$
\sum_{i=0}^{n-1}\left(W_{t_{i+1}}-W_{t_i}\right)
=
W_t-W_0.
$$

标准布朗运动 $W_0=0$，因此：

$$
\boxed{
\int_0^t dW_s=W_t
}
$$

也就是说，$\int_0^t dW_s$ 不是普通面积，而是从 $0$ 到 $t$ 的布朗总位移。

## 例子：$dX_t=dW_t$

若

$$
dX_t=dW_t,
$$

从 $0$ 到 $t$ 积分：

$$
\int_0^t dX_s=\int_0^t dW_s.
$$

得到：

$$
X_t-X_0=W_t-W_0.
$$

若 $X_0=x_0$ 且 $W_0=0$，则：

$$
X_t=x_0+W_t.
$$

因为

$$
W_t\sim\mathcal{N}(0,t),
$$

所以：

$$
\boxed{
X_t\mid X_0=x_0\sim\mathcal{N}(x_0,t)
}
$$

这解释了为什么纯布朗运动从 $x_0$ 出发，经过时间 $t$ 后会变成以 $x_0$ 为均值、方差为 $t$ 的正态分布。

## 带确定性系数的随机积分

如果噪声前面有一个确定性系数 $g(s)$：

$$
\int_0^t g(s)\,dW_s,
$$

离散理解为：

$$
\sum_{i=0}^{n-1} g(t_i)\left(W_{t_{i+1}}-W_{t_i}\right).
$$

每一项都是“系数 $\times$ 布朗增量”。由于不同时间段的布朗增量独立，且

$$
W_{t_{i+1}}-W_{t_i}\sim\mathcal{N}(0,t_{i+1}-t_i),
$$

这个随机积分仍然是均值为 $0$ 的高斯随机变量，并且方差由平方系数累加得到：

$$
\boxed{
\int_0^t g(s)\,dW_s
\sim
\mathcal{N}\left(0,\int_0^t g(s)^2\,ds\right)
}
$$

若 $g(s)=g$ 是常数，则：

$$
\int_0^t g\,dW_s=gW_t\sim\mathcal{N}(0,g^2t).
$$

## 简单 SDE 的积分

考虑：

$$
dX_t=\mu\,dt+\sigma\,dW_t.
$$

从 $0$ 到 $t$ 积分：

$$
X_t-X_0
=
\int_0^t \mu\,ds
+
\int_0^t \sigma\,dW_s.
$$

第一项是普通积分：

$$
\int_0^t \mu\,ds=\mu t.
$$

第二项是随机积分：

$$
\int_0^t \sigma\,dW_s=\sigma W_t.
$$

所以：

$$
X_t=X_0+\mu t+\sigma W_t.
$$

若 $X_0=x_0$，则：

$$
\boxed{
X_t\sim\mathcal{N}(x_0+\mu t,\sigma^2t)
}
$$

但注意：这个过程一般不能普通求导，因为它含有不可导的 $W_t$。写

$$
dX_t=\mu\,dt+\sigma\,dW_t
$$

表达的是“小步增量规则”，不是普通意义下的 $dX_t/dt$。

## 和普通积分的区别

| 积分 | 累加的东西 | 结果 |
|---|---|---|
| $\int_0^t f(s)\,ds$ | 确定性小量 $f(s)ds$ | 普通数值或函数 |
| $\int_0^t g(s)\,dW_s$ | 随机小增量 $g(s)(W_{s+ds}-W_s)$ | 随机变量 |

当 $g$ 是确定性函数时，$\int_0^t g(s)dW_s$ 是高斯随机变量。若 $g$ 还依赖随机过程本身，例如 $g(X_s,s)$，就进入真正的 Itô 积分框架，需要更严格的适应性和极限定义。

## 相关页

- 随机过程：见 [../structures/StochasticProcess.md](../structures/StochasticProcess.md)
- 布朗运动：见 [../processes/BrownianMotion.md](../processes/BrownianMotion.md)
- 随机微分方程（SDE）主线：见 [SDE.md](SDE.md)
- SDE 到 Fokker-Planck 附录：见 [SDEToFokkerPlanck.md](SDEToFokkerPlanck.md)
- 正态分布：见 [../distributions/正态分布.md](../distributions/正态分布.md)
