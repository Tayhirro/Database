---
title: 随机微分方程（SDE）主线
date: "2026-07-12"
categories:
  - math
description: SDE 主入口：从 dX_t=fdt+gdW_t 的小步增量出发，连接布朗运动、随机积分、Fokker-Planck 密度演化和 probability flow ODE。
---
# 随机微分方程（SDE）主线

## 一句话

SDE 先描述**单个随机粒子怎么动**：

$$
dX_t=f(X_t,t)\,dt+g(t)\,dW_t
$$

然后通过 Fokker-Planck 方程，描述**整团粒子的概率密度怎么动**：

$$
\partial_t p_t
=
-\nabla\cdot(fp_t)+\frac12g^2\Delta p_t
$$

最后在 diffusion / score-based model 里，可以继续改写成确定性 probability flow ODE：

$$
\frac{dx}{dt}=f(x,t)-\frac12g(t)^2\nabla_x\log p_t(x)
$$

## 读法

这页是 SDE 相关内容的主入口。先读这页，只有卡在具体细节时再跳附录。

| 你卡在哪里 | 读哪里 |
|---|---|
| $X_t$ 为什么是一族随机变量，$dX_t$ 是什么 | [随机过程](../structures/StochasticProcess.md) |
| $W_t$、$dW_t$、$\sqrt{\Delta t}\epsilon$ 是什么 | [布朗运动](../processes/BrownianMotion.md) |
| $\int dW_s$、$\int g(s)dW_s$ 怎么理解 | [随机积分附录](StochasticIntegral.md) |
| 转移核、Dirac delta、测试函数弱形式完全推导 | [SDE 到 Fokker-Planck 附录](SDEToFokkerPlanck.md) |
| diffusion 里的反向 SDE / PF-ODE | [生成模型.md](../../../神经网络/models/生成模型.md) |

## 1. SDE 到底描述什么

随机过程 $\{X_t\}_{t\ge0}$ 可以理解成每个时间 $t$ 都有一个随机变量。$X_t$ 表示“粒子在时刻 $t$ 的位置”。

SDE

$$
dX_t=f(X_t,t)\,dt+g(t)\,dW_t
$$

不是普通导数方程，而是小步增量规则。离散成一个很小的时间步：

$$
X_{t+\Delta t}
\approx
X_t+f(X_t,t)\Delta t+g(t)\sqrt{\Delta t}\epsilon,
\qquad
\epsilon\sim\mathcal N(0,1)
$$

逐项看：

- $f(X_t,t)\Delta t$：漂移，表示平均往哪里走；
- $g(t)\sqrt{\Delta t}\epsilon$：扩散，表示随机抖动多强；
- $\sqrt{\Delta t}\epsilon$：布朗运动增量 $W_{t+\Delta t}-W_t$ 的离散写法。

## 2. 微分形式和积分形式

微分形式：

$$
dX_t=f(X_t,t)\,dt+g(t)\,dW_t
$$

对应积分形式：

$$
X_t
=
X_0
+
\int_0^t f(X_s,s)\,ds
+
\int_0^t g(s)\,dW_s
$$

如果扩散系数也依赖状态：

$$
dX_t=f(X_t,t)\,dt+g(X_t,t)\,dW_t
$$

对应：

$$
X_t
=
X_0
+
\int_0^t f(X_s,s)\,ds
+
\int_0^t g(X_s,s)\,dW_s
$$

第二个积分是 Itô 随机积分。主线理解时，只要先记住：它是在累加很多个“系数 $\times$ 布朗小增量”。

## 3. 为什么不能当普通 ODE

普通 ODE：

$$
dX_t=f(X_t,t)\,dt
$$

可以写成：

$$
\frac{dX_t}{dt}=f(X_t,t)
$$

但 SDE 不能写成：

$$
\frac{dX_t}{dt}
=
f(X_t,t)+g(t)\frac{dW_t}{dt}
$$

原因是布朗运动不可导。因为：

$$
W_{t+\Delta t}-W_t\sim\mathcal N(0,\Delta t)
$$

所以：

$$
\frac{W_{t+\Delta t}-W_t}{\Delta t}
\sim
\mathcal N\left(0,\frac1{\Delta t}\right)
$$

当 $\Delta t\to0$ 时方差发散，因此 $dW_t$ 只能理解成随机增量，不是普通导数。

## 4. SDE 回答单粒子问题

给定当前粒子在 $x$：

$$
X_t=x
$$

SDE 告诉你下一小步的条件分布：

$$
X_{t+\Delta t}\mid X_t=x
\approx
x+f(x,t)\Delta t+g(t)\sqrt{\Delta t}\epsilon
$$

也就是：

$$
X_{t+\Delta t}\mid X_t=x
\sim
\mathcal N\left(x+f(x,t)\Delta t,\;g(t)^2\Delta t\right)
$$

这个条件密度记作短时间转移核：

$$
q_{\Delta t}(y\mid x)
$$

它回答的是：

$$
\text{当前在 }x\text{，下一小步跑到 }y\text{ 附近的密度是多少？}
$$

## 5. 从单粒子到整团密度

如果整团粒子的当前密度是：

$$
X_t\sim p_t(x)
$$

这里先把 $p_t(x)$ 当成时刻 $t$ 的概率密度。分布、概率质量和概率密度的基础定义见 [../structures/Distribution.md](../structures/Distribution.md)。

那么下一时刻 $y$ 处的密度来自所有旧位置 $x$ 的贡献：

$$
p_{t+\Delta t}(y)
=
\int q_{\Delta t}(y\mid x)p_t(x)\,dx
$$

这里：

- $p_t(x)$：当前在 $x$ 附近的概率密度；
- $q_{\Delta t}(y\mid x)$：从 $x$ 到 $y$ 的短时间条件密度；
- 积分：把所有来源 $x$ 的贡献加起来。

如果直接算：

$$
\frac{p_{t+\Delta t}(y)-p_t(y)}{\Delta t}
$$

会碰到：

$$
\frac{q_{\Delta t}(y\mid x)-\delta(y-x)}{\Delta t}
$$

因为不走时间的转移核是 Dirac delta：

$$
p_t(y)=\int \delta(y-x)p_t(x)\,dx
$$

这一步直接算很别扭，因为 $q_{\Delta t}$ 在 $\Delta t\to0$ 时会越来越像 $\delta$。

## 6. 为什么用测试函数

为了绕开尖核极限，不直接看某个点的密度差，而是看任意平滑打分器 $\varphi$ 的平均读数：

$$
\mathbb E[\varphi(X_t)]
=
\int \varphi(x)p_t(x)\,dx
$$

SDE 容易告诉我们这个平均读数怎么变。以最简单的

$$
dX_t=dW_t
$$

为例：

$$
X_{t+\Delta t}=x+\sqrt{\Delta t}\epsilon
$$

于是：

$$
\int \varphi(y)q_{\Delta t}(y\mid x)\,dy
=
\mathbb E[\varphi(x+\sqrt{\Delta t}\epsilon)]
$$

对右边做 Taylor 展开并取期望：

$$
\mathbb E[\varphi(x+\sqrt{\Delta t}\epsilon)]
=
\varphi(x)+\frac12\varphi''(x)\Delta t+o(\Delta t)
$$

所以：

$$
\frac{
\mathbb E[\varphi(x+\sqrt{\Delta t}\epsilon)]-\varphi(x)
}{\Delta t}
\to
\frac12\varphi''(x)
$$

最终得到：

$$
\int \varphi(x)\partial_t p_t(x)\,dx
=
\int \frac12\varphi''(x)p_t(x)\,dx
$$

再分部积分两次，把导数从 $\varphi$ 转移到 $p_t$：

$$
\int \varphi''(x)p_t(x)\,dx
=
\int \varphi(x)p_t''(x)\,dx
$$

于是：

$$
\int \varphi(x)
\left[
\partial_t p_t(x)-\frac12p_t''(x)
\right]dx=0
$$

因为这对所有 $\varphi$ 都成立，所以：

$$
\partial_t p_t(x)=\frac12p_t''(x)
$$

这就是纯布朗运动的 Fokker-Planck，也就是热方程。完整不跳步推导见 [SDE 到 Fokker-Planck 附录](SDEToFokkerPlanck.md)。

## 7. 一般 SDE 的 Fokker-Planck

对一般一维 SDE：

$$
dX_t=f(X_t,t)\,dt+g(t)\,dW_t
$$

同样推导得到：

$$
\boxed{
\partial_t p_t
=
-\partial_x(fp_t)
+
\frac12g^2\partial_{xx}p_t
}
$$

多维写法：

$$
\boxed{
\partial_t p_t(x)
=
-\nabla_x\cdot(f(x,t)p_t(x))
+
\frac12g(t)^2\Delta_x p_t(x)
}
$$

直觉：

- $-\nabla\cdot(fp_t)$：漂移项 $f$ 把概率质量搬运；
- $\frac12g^2\Delta p_t$：噪声项把概率质量扩散摊开。

## 8. 到 probability flow ODE

Fokker-Planck 已经告诉我们密度怎么动：

$$
\partial_t p_t
=
-\nabla\cdot(fp_t)+\frac12g^2\Delta p_t
$$

如果想找一个确定性 ODE：

$$
\frac{dx}{dt}=v(x,t)
$$

让它驱动的密度也满足同样演化，则要把 Fokker-Planck 写成连续性方程：

$$
\partial_t p_t=-\nabla\cdot(p_t v)
$$

利用 score：

$$
s_t(x)=\nabla_x\log p_t(x)
$$

以及：

$$
\Delta p_t
=
\nabla\cdot(\nabla p_t)
=
\nabla\cdot(p_t s_t)
$$

可以得到：

$$
\partial_t p_t
=
-\nabla\cdot
\left[
p_t\left(f-\frac12g^2s_t\right)
\right]
$$

所以 probability flow ODE 的速度场是：

$$
\boxed{
v_{PF}(x,t)=f(x,t)-\frac12g(t)^2s_t(x)
}
$$

也就是：

$$
\boxed{
\frac{dx}{dt}
=
f(x,t)-\frac12g(t)^2\nabla_x\log p_t(x)
}
$$

## 9. 主线总结

```text
随机过程 X_t
  -> 布朗运动 W_t 提供 dW_t
  -> SDE 描述单个粒子小步怎么动
  -> 转移核 q_{\Delta t}(y|x) 描述从 x 到 y
  -> Fokker-Planck 描述整团密度 p_t 怎么动
  -> score 改写扩散项
  -> probability flow ODE 给出等价确定性速度场
```

最该记住的是：

$$
\boxed{
\text{SDE 是单粒子随机运动；Fokker-Planck 是概率密度演化；PF-ODE 是等价确定性流。}
}
$$

## 相关页

- 分布、概率质量和概率密度：见 [../structures/Distribution.md](../structures/Distribution.md)
- 随机过程：见 [../structures/StochasticProcess.md](../structures/StochasticProcess.md)
- 布朗运动：见 [../processes/BrownianMotion.md](../processes/BrownianMotion.md)
- 随机积分附录：见 [StochasticIntegral.md](StochasticIntegral.md)
- SDE 到 Fokker-Planck 附录：见 [SDEToFokkerPlanck.md](SDEToFokkerPlanck.md)
- Diffusion 模型中的 SDE 视角：见 [../../../神经网络/models/生成模型.md](../../../神经网络/models/生成模型.md)
