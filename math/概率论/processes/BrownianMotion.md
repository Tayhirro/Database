---
title: 布朗运动（Brownian Motion / Wiener Process）
date: "2026-07-10"
categories:
  - math
description: 布朗运动是连续时间随机过程，核心性质是独立平稳的高斯增量：W_{t+Δt}-W_t ~ N(0, Δt)。
---
# 布朗运动（Brownian Motion / Wiener Process）

## 一句话

布朗运动，也叫标准维纳过程，是连续时间里“独立小随机扰动不断累加”的极限过程。它是随机微分方程

$$
dX_t=f(X_t,t)\,dt+g(t)\,dW_t
$$

里的基本噪声来源。

## 层次位置

上游对象：
- [概率空间](../structures/ProbabilitySpace.md)：布朗运动定义在概率空间上；
- [随机变量](../structures/RandomVariable.md)：每个 $W_t$ 都是随机变量；
- [随机过程](../structures/StochasticProcess.md)：布朗运动是满足特殊增量性质的随机过程。

本页关注：
- $W_t-W_s\sim\mathcal{N}(0,t-s)$ 的高斯增量；
- 为什么布朗增量尺度是 $\sqrt{\Delta t}$；
- 为什么 $dW_t$ 不是普通微分小量；
- Itô 规则中 $(dW_t)^2=dt$ 的直觉来源。

下游工具/主题：
- [随机微分方程（SDE）主线](../modules/SDE.md)：用 $dW_t$ 驱动随机过程，并继续走到 Fokker-Planck / PF-ODE；
- [随机积分附录](../modules/StochasticIntegral.md)：需要细看 $\int dW_s$ 时再打开；
- [SDE 到 Fokker-Planck 附录](../modules/SDEToFokkerPlanck.md)：需要不跳步推导密度方程时再打开。

## 定义

在概率空间 $(\Omega,\mathcal{F},\mathbb{P})$ 上，一个一维标准布朗运动是随机过程 $\{W_t\}_{t\ge 0}$，满足：

1. $W_0=0$；
2. 对任意 $0\le s<t$，增量 $W_t-W_s$ 与过去的信息独立；
3. 增量只依赖时间长度：

$$
W_t-W_s\sim \mathcal{N}(0,t-s);
$$

4. 几乎所有样本路径 $t\mapsto W_t(\omega)$ 都连续。

多维布朗运动 $\mathbf{W}_t\in\mathbb{R}^d$ 可以理解成 $d$ 个彼此独立的一维标准布朗运动拼成的向量。

## 最核心的增量公式

对一个小时间步 $\Delta t$，布朗运动的位移增量满足：

$$
\Delta W_t
=
W_{t+\Delta t}-W_t
\sim
\mathcal{N}(0,\Delta t).
$$

等价地，可以写成：

$$
\Delta W_t
\overset{d}{=}
\sqrt{\Delta t}\,\epsilon,
\qquad
\epsilon\sim\mathcal{N}(0,1).
$$

这里的 $\overset{d}{=}$ 表示“分布相同”。所以 $dW_t$ 在数值离散化里对应的就是 $\sqrt{\Delta t}\epsilon$。

多维情形下：

$$
\Delta \mathbf{W}_t
\overset{d}{=}
\sqrt{\Delta t}\,\boldsymbol{\epsilon},
\qquad
\boldsymbol{\epsilon}\sim\mathcal{N}(0,I).
$$

## 方差随时间累积的设计逻辑

布朗运动想刻画的是连续时间里的随机游走。它不是“每一步固定抖一下”的离散过程，而是要满足几个自然要求：

- 没有固定方向：平均位移为 0；E=0
- 不同时间段的随机扰动互相独立；

（对应方差线性增长）
- 运动规律不依赖绝对时刻，只依赖经过了多长时间； 
- 时间可以任意切分，切得更细不应该改变同一段总时间里的随机强度。

设

$$
v(t)=\operatorname{Var}(W_t).
$$

如果先走 $s$ 时间，再走 $t$ 时间，那么

$$
W_{s+t}=W_s+(W_{s+t}-W_s).
$$

布朗运动要求后一段增量 $W_{s+t}-W_s$ 与前一段独立，而且后一段只取决于时间长度 $t$。所以方差应该满足：

$$
v(s+t)=v(s)+v(t).
$$

这一步就是核心。因为独立随机变量的方差会相加，而时间区间也可以相加。连续且满足这个加法关系的函数只能是线性的：

$$
v(t)=ct.
$$

常数 $c$ 表示单位时间内注入多少随机强度。标准布朗运动只是把单位选成 $c=1$，于是：

$$
\operatorname{Var}(W_t)=t.
$$

因此任意长度为 $\Delta t$ 的小时间段都有：

$$
W_{t+\Delta t}-W_t\sim\mathcal{N}(0,\Delta t).
$$

这不是为了公式好看而硬规定成 $\Delta t$，而是由“独立增量 + 时间可拼接 + 连续时间极限稳定”共同逼出来的。它表达的直觉是：时间越长，累计随机扰动越多，位置越不确定；但典型偏移距离是标准差 $\sqrt{t}$，不是 $t$。

反过来看，如果每个 $\Delta t$ 小步都采一个方差为 1 的位移，那么同样的总时间 $T$ 被切成 $T/\Delta t$ 步后，总方差会变成 $T/\Delta t$。当 $\Delta t\to 0$ 时它会发散，说明这种定义不能得到稳定的连续时间随机运动。

## 为什么是 $\sqrt{\Delta t}$ 尺度
为了保持方差累计的确定增长：
假设每小步位移的尺度是 $(\Delta t)^\alpha$，那么每步方差尺度是 $(\Delta t)^{2\alpha}$。总时间 $T$ 内有 $T/\Delta t$ 步，所以累计方差尺度为：

$$
\frac{T}{\Delta t}(\Delta t)^{2\alpha}
=
T(\Delta t)^{2\alpha-1}.
$$

当 $\Delta t\to 0$ 时：

- 若 $\alpha>1/2$，累计方差趋于 0，随机性消失；
- 若 $\alpha<1/2$，累计方差发散；
- 只有 $\alpha=1/2$，累计方差保持有限且非零。

所以连续随机极限逼出了唯一合适的尺度：

$$
\Delta W_t \sim \sqrt{\Delta t}.
$$

## $dW_t$ 不是普通微积分小量

普通 ODE 里

$$
dX_t=f(X_t,t)\,dt
$$

可以理解成 $dX_t/dt=f(X_t,t)$。但 SDE 里的

$$
dX_t=f(X_t,t)\,dt+g(t)\,dW_t
$$

不能把 $dW_t$ 写成 $W'(t)dt$，因为布朗运动几乎处处不可导。直观看：

$$
\frac{W_{t+\Delta t}-W_t}{\Delta t}
\sim
\frac{\sqrt{\Delta t}\epsilon}{\Delta t}
=
\frac{1}{\sqrt{\Delta t}}\epsilon.
$$

当 $\Delta t\to 0$ 时，这个差商的尺度会发散，所以 $W'(t)$ 通常不存在。

因此 $dW_t$ 是随机微积分里的增量记号。做数值模拟时，通常用 Euler-Maruyama 离散化：

$$
X_{t+\Delta t}
\approx
X_t
+
f(X_t,t)\Delta t
+
g(t)\sqrt{\Delta t}\epsilon.
$$

关于“为什么 $X_t$ 是随机变量还可以写 $dX_t$”，见 [StochasticProcess.md](../structures/StochasticProcess.md)。SDE 的主线入口见 [../modules/SDE.md](../modules/SDE.md)；如果只想细看 $\int_0^t dW_s$ 和 $\int_0^t g(s)dW_s$，再看 [../modules/StochasticIntegral.md](../modules/StochasticIntegral.md)。

## Itô 规则和扩散项

因为

$$
dW_t\sim\sqrt{dt},
$$

所以二阶项

$$
(dW_t)^2
$$

和 $dt$ 是同阶量。在 Itô 随机微积分里，这被写成二次变差规则：

$$
(dW_t)^2=dt.
$$

同时有：

$$
dt^2=0,
\qquad
dt\,dW_t=0.
$$

这条规则是 SDE 和普通 ODE 的关键差异。对

$$
dX_t=f(X_t,t)\,dt+g(t)\,dW_t
$$

而言，噪声项平方后产生

$$
(g(t)dW_t)^2=g(t)^2dt,
$$

所以二阶导数项不会消失。最终在概率密度演化里，会出现 Fokker-Planck 方程中的扩散项：

$$
\frac{1}{2}g(t)^2\Delta p_t.
$$

Fokker-Planck 的主线解释见 [../modules/SDE.md](../modules/SDE.md)；短时间转移核、Dirac delta 和测试函数弱形式的不跳步推导见 [../modules/SDEToFokkerPlanck.md](../modules/SDEToFokkerPlanck.md)。

## 和 SDE / Diffusion Models 的关系

在 diffusion model 的连续时间视角里，前向加噪常写成：

$$
d\mathbf{x}
=
\mathbf{f}(\mathbf{x},t)\,dt
+
g(t)\,d\mathbf{W}_t.
$$

离散到一个小步长就是：

$$
\mathbf{x}_{t+\Delta t}
\approx
\mathbf{x}_t
+
\mathbf{f}(\mathbf{x}_t,t)\Delta t
+
g(t)\sqrt{\Delta t}\boldsymbol{\epsilon}.
$$

所以可以把 $g(t)$ 理解成“当前时刻噪声注入强度”。$d\mathbf{W}_t$ 提供标准高斯随机增量，$g(t)$ 决定这个增量被放大多少。

## 相关页

- 概率空间：见 [ProbabilitySpace.md](../structures/ProbabilitySpace.md)
- 随机变量：见 [RandomVariable.md](../structures/RandomVariable.md)
- 随机过程：见 [StochasticProcess.md](../structures/StochasticProcess.md)
- 随机微分方程（SDE）主线：见 [../modules/SDE.md](../modules/SDE.md)
- 随机积分附录：见 [../modules/StochasticIntegral.md](../modules/StochasticIntegral.md)
- SDE 到 Fokker-Planck 附录：见 [../modules/SDEToFokkerPlanck.md](../modules/SDEToFokkerPlanck.md)
- 正态分布：见 [../distributions/正态分布.md](../distributions/正态分布.md)
- Diffusion 模型中的 SDE 视角：见 [../../../神经网络/models/生成模型.md](../../../神经网络/models/生成模型.md)
