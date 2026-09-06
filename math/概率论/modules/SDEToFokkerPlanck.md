---
title: 从 SDE 到 Fokker-Planck（转移核、Dirac delta、弱形式）
date: "2026-07-12"
categories:
  - math
description: 解释已知 SDE 后，如何从短时间转移核推导概率密度 p_t 的演化方程，以及为什么要用测试函数/弱形式绕开 Dirac delta 极限。
---
# 从 SDE 到 Fokker-Planck（转移核、Dirac delta、弱形式）

> [!note]
> 这是 SDE 主线的详细推导附录。先读 [随机微分方程（SDE）主线](SDE.md)；如果你卡在转移核、Dirac delta、测试函数或 Taylor 展开，再看本页的不跳步推导。

## 一句话

这页解决的问题是：

$$
\boxed{
\text{已知单个粒子的 SDE 小步运动规则，怎么推出整团概率密度 }p_t(x)\text{ 的变化方程？}
}
$$

它属于“随机过程/SDE 的密度演化”这条线，连接：

- [随机过程](../structures/StochasticProcess.md)：$X_t$ 是按时间索引的一族随机变量；
- [布朗运动](../processes/BrownianMotion.md)：$dW_t$ 是连续时间高斯噪声增量；
- [随机积分](StochasticIntegral.md)：SDE 的积分形式；
- [随机微分方程](SDE.md)：SDE 记号、小步离散和积分形式；
- [Diffusion 模型中的 SDE / PF-ODE](../../../神经网络/models/生成模型.md)：Fokker-Planck 和 probability flow 的应用。

## 层次位置

上游概念：
- [随机过程](../structures/StochasticProcess.md)：提供 $X_t$；
- [布朗运动](../processes/BrownianMotion.md)：提供 $dW_t$；
- [随机积分](StochasticIntegral.md)：解释 SDE 的积分形式；
- [随机微分方程](SDE.md)：给出单粒子的随机运动规则。

本页关注：
- 从 SDE 的短时间转移核出发；
- 解释为什么直接对 $p_{t+\Delta t}-p_t$ 求极限会碰到 Dirac delta；
- 用测试函数/弱形式推导 Fokker-Planck；
- 把 Fokker-Planck 改写成 PF-ODE 的接口。

下游主题：
- Diffusion / score-based model 中的反向 SDE 和 PF-ODE。

## 先把层次摆清楚

SDE：

$$
dX_t=f(X_t,t)\,dt+g(t)\,dW_t
$$

描述的是**单个随机粒子怎么动**。离散一小步就是：

$$
X_{t+\Delta t}
\approx
X_t+f(X_t,t)\Delta t+g(t)\sqrt{\Delta t}\epsilon,
\qquad
\epsilon\sim\mathcal{N}(0,1)
$$

但很多问题关心的不是单个粒子，而是很多粒子的整体分布：

$$
X_t\sim p_t(x)
$$

Fokker-Planck 方程要回答的是：

$$
p_t(x)\quad\text{怎么随时间变成}\quad p_{t+\Delta t}(x)?
$$

也就是：

$$
\partial_t p_t(x)=?
$$

## 1. 最简单例子：$dX_t=dW_t$

先不看一般 SDE，只看纯布朗运动：

$$
dX_t=dW_t
$$

它的离散小步是：

$$
X_{t+\Delta t}=X_t+\sqrt{\Delta t}\epsilon,
\qquad
\epsilon\sim\mathcal{N}(0,1)
$$

如果当前粒子在 $x$：

$$
X_t=x
$$

那么下一步：

$$
X_{t+\Delta t}=x+\sqrt{\Delta t}\epsilon
$$

因此：

$$
X_{t+\Delta t}\mid X_t=x
\sim
\mathcal{N}(x,\Delta t)
$$

这个条件分布的密度记作短时间转移核：

$$
q_{\Delta t}(y\mid x)
=
\frac{1}{\sqrt{2\pi\Delta t}}
\exp\left(
-\frac{(y-x)^2}{2\Delta t}
\right)
$$

它的意思是：

$$
\Pr(X_{t+\Delta t}\in[y,y+dy]\mid X_t=x)
\approx
q_{\Delta t}(y\mid x)\,dy
$$

也就是“现在在 $x$，下一小步跑到 $y$ 附近的概率密度”。

## 2. 用转移核更新整体密度

当前整体密度是 $p_t(x)$。新位置 $y$ 的概率密度来自所有旧位置 $x$ 的贡献：

$$
p_{t+\Delta t}(y)
=
\int q_{\Delta t}(y\mid x)p_t(x)\,dx
$$

这个式子逐块读：

- $p_t(x)$：当前有多少概率质量在 $x$；
- $q_{\Delta t}(y\mid x)$：这些概率质量有多少会从 $x$ 跑到 $y$；
- 对所有 $x$ 积分：把所有来源加起来，得到 $y$ 的新密度。

这就是连续状态下的 Chapman-Kolmogorov 更新。

## 3. 为什么 $p_t(y)$ 也能写成 delta 积分

原来的密度当然就是：

$$
p_t(y)
$$

这表示当前时刻在 $y$ 附近的概率密度。

但为了和 $p_{t+\Delta t}(y)$ 写成同一种“转移核作用在旧密度上”的形式，可以把“时间不走、粒子原地不动”写成：

$$
p_t(y)
=
\int \delta(y-x)p_t(x)\,dx
$$

这里 $\delta(y-x)$ 是 Dirac delta。它不是普通函数，而是一个“连续选择器”，满足：

$$
\int \delta(y-x)h(x)\,dx=h(y)
$$

所以把 $h(x)=p_t(x)$ 代进去，就得到：

$$
\int \delta(y-x)p_t(x)\,dx=p_t(y)
$$

离散版最直观。假设只有三个位置 $1,2,3$，要取出位置 $y=2$ 的值：

$$
p_t(2)
=
\sum_x \mathbf{1}\{x=2\}p_t(x)
$$

连续版就是：

$$
\sum_x \to \int dx,
\qquad
\mathbf{1}\{x=y\}\to \delta(y-x)
$$

因此：

$$
p_t(y)=\int\delta(y-x)p_t(x)\,dx
$$

只是连续版的“用选择器把 $x=y$ 那一点挑出来”。

## 4. 直接求密度导数为什么麻烦

我们想直接算：

$$
\frac{p_{t+\Delta t}(y)-p_t(y)}{\Delta t}
$$

把两项都写成转移核形式：

$$
p_{t+\Delta t}(y)
=
\int q_{\Delta t}(y\mid x)p_t(x)\,dx
$$

$$
p_t(y)
=
\int \delta(y-x)p_t(x)\,dx
$$

相减：

$$
p_{t+\Delta t}(y)-p_t(y)
=
\int
\left[
q_{\Delta t}(y\mid x)-\delta(y-x)
\right]
p_t(x)\,dx
$$

除以 $\Delta t$：

$$
\frac{p_{t+\Delta t}(y)-p_t(y)}{\Delta t}
=
\int
\frac{
q_{\Delta t}(y\mid x)-\delta(y-x)
}{\Delta t}
p_t(x)\,dx
$$

麻烦在于：

$$
q_{\Delta t}(y\mid x)\to\delta(y-x)
\qquad(\Delta t\to0)
$$

$q_{\Delta t}$ 是一个越来越窄、越来越高、面积始终为 $1$ 的高斯核。极限里的

$$
\frac{q_{\Delta t}-\delta}{\Delta t}
$$

不是普通函数的极限，而是分布意义下的极限。直接硬算会很不直观。

## 5. 测试函数/弱形式在干什么

换一种问法：不直接盯着某个点 $y$ 的密度变化，而是拿一个光滑函数 $\varphi$ 去“测量”整团分布。

$$
\int \varphi(y)p_t(y)\,dy
=
\mathbb{E}[\varphi(X_t)]
$$

你可以把 $\varphi$ 理解成打分器：

- $\varphi(y)=y$：测均值；
- $\varphi(y)=y^2$：测二阶矩；
- $\varphi$ 在某个区域附近凸起：测那片区域附近有多少概率质量。

这样不是妥协。原因是：如果对所有足够光滑的 $\varphi$，两个候选密度给出的积分都一样，那么这两个密度在分布意义下就是同一个。

现在看：

$$
\int \varphi(y)\frac{p_{t+\Delta t}(y)-p_t(y)}{\Delta t}\,dy
$$

把上一节的差分代入：

$$
=
\int \varphi(y)
\int
\frac{
q_{\Delta t}(y\mid x)-\delta(y-x)
}{\Delta t}
p_t(x)\,dx\,dy
$$

交换积分顺序：

$$
=
\int
\left[
\frac{
\int \varphi(y)q_{\Delta t}(y\mid x)\,dy
-
\int \varphi(y)\delta(y-x)\,dy
}{\Delta t}
\right]
p_t(x)\,dx
$$

现在两个尖东西被“放进积分里”了：

$$
\int \varphi(y)q_{\Delta t}(y\mid x)\,dy
=
\mathbb{E}\left[\varphi(x+\sqrt{\Delta t}\epsilon)\right]
$$

以及：

$$
\int \varphi(y)\delta(y-x)\,dy=\varphi(x)
$$

所以整体变成：

$$
\int
\frac{
\mathbb{E}\left[\varphi(x+\sqrt{\Delta t}\epsilon)\right]-\varphi(x)
}{\Delta t}
p_t(x)\,dx
$$

尖核 $q_{\Delta t}$ 和 $\delta$ 不再直接出现，问题变成普通的 Taylor 展开。

## 6. Taylor 展开得到热方程

上一节已经把原问题变成了：

$$
\int \varphi(y)\frac{p_{t+\Delta t}(y)-p_t(y)}{\Delta t}\,dy
=
\int
\frac{
\mathbb{E}\left[\varphi(x+\sqrt{\Delta t}\epsilon)\right]-\varphi(x)
}{\Delta t}
p_t(x)\,dx
$$

现在只需要把右边括号里的东西算清楚。为了少写符号，先定义：

$$
h=\sqrt{\Delta t}\epsilon
$$

于是：

$$
\varphi(x+\sqrt{\Delta t}\epsilon)=\varphi(x+h)
$$

对 $\varphi(x+h)$ 在 $x$ 附近做二阶 Taylor 展开：

$$
\varphi(x+h)
=
\varphi(x)
+
\varphi'(x)h
+
\frac12\varphi''(x)h^2
+
\text{更高阶项}
$$

把 $h=\sqrt{\Delta t}\epsilon$ 代回去：

$$
\varphi(x+\sqrt{\Delta t}\epsilon)
=
\varphi(x)
+
\varphi'(x)\sqrt{\Delta t}\epsilon
+
\frac12\varphi''(x)(\sqrt{\Delta t}\epsilon)^2
+
\text{更高阶项}
$$

因为：

$$
(\sqrt{\Delta t}\epsilon)^2=\Delta t\,\epsilon^2
$$

所以：

$$
\varphi(x+\sqrt{\Delta t}\epsilon)
=
\varphi(x)
+
\varphi'(x)\sqrt{\Delta t}\epsilon
+
\frac12\varphi''(x)\Delta t\,\epsilon^2
+
\text{更高阶项}
$$

在推导 Fokker-Planck 时，我们只保留到 $\Delta t$ 这一阶。更高阶项在取期望后记成：

$$
o(\Delta t)
$$

意思是：它比 $\Delta t$ 还小，满足

$$
\frac{o(\Delta t)}{\Delta t}\to0
\qquad(\Delta t\to0)
$$

于是写成：

$$
\varphi(x+\sqrt{\Delta t}\epsilon)
=
\varphi(x)
+
\varphi'(x)\sqrt{\Delta t}\epsilon
+
\frac12\varphi''(x)\Delta t\,\epsilon^2
+
o(\Delta t)
$$

现在对两边取期望。注意这里的随机性只来自 $\epsilon$，而 $x,\Delta t,\varphi'(x),\varphi''(x)$ 都当成固定量。

左边：

$$
\mathbb{E}\left[\varphi(x+\sqrt{\Delta t}\epsilon)\right]
$$

右边逐项取期望：

$$
\mathbb{E}[\varphi(x)]=\varphi(x)
$$

因为 $\varphi(x)$ 不含随机变量 $\epsilon$。

第一阶噪声项：

$$
\mathbb{E}\left[\varphi'(x)\sqrt{\Delta t}\epsilon\right]
=
\varphi'(x)\sqrt{\Delta t}\,\mathbb{E}[\epsilon]
$$

标准正态满足：

$$
\mathbb{E}[\epsilon]=0
$$

所以：

$$
\mathbb{E}\left[\varphi'(x)\sqrt{\Delta t}\epsilon\right]=0
$$

第二阶噪声项：

$$
\mathbb{E}\left[\frac12\varphi''(x)\Delta t\,\epsilon^2\right]
=
\frac12\varphi''(x)\Delta t\,\mathbb{E}[\epsilon^2]
$$

标准正态还满足：

$$
\mathbb{E}[\epsilon^2]=1
$$

所以：

$$
\mathbb{E}\left[\frac12\varphi''(x)\Delta t\,\epsilon^2\right]
=
\frac12\varphi''(x)\Delta t
$$

把三项合起来：

$$
\mathbb{E}\left[\varphi(x+\sqrt{\Delta t}\epsilon)\right]
=
\varphi(x)
+
0
+
\frac12\varphi''(x)\Delta t
+
o(\Delta t)
$$

也就是：

$$
\mathbb{E}\left[\varphi(x+\sqrt{\Delta t}\epsilon)\right]
=
\varphi(x)+\frac12\varphi''(x)\Delta t+o(\Delta t)
$$

现在回到括号：

$$
\mathbb{E}\left[\varphi(x+\sqrt{\Delta t}\epsilon)\right]-\varphi(x)
$$

代入刚才的结果：

$$
\mathbb{E}\left[\varphi(x+\sqrt{\Delta t}\epsilon)\right]-\varphi(x)
=
\left[
\varphi(x)+\frac12\varphi''(x)\Delta t+o(\Delta t)
\right]
-
\varphi(x)
$$

$\varphi(x)$ 抵消：

$$
\mathbb{E}\left[\varphi(x+\sqrt{\Delta t}\epsilon)\right]-\varphi(x)
=
\frac12\varphi''(x)\Delta t+o(\Delta t)
$$

再除以 $\Delta t$：

$$
\frac{
\mathbb{E}\left[\varphi(x+\sqrt{\Delta t}\epsilon)\right]-\varphi(x)
}{\Delta t}
=
\frac{
\frac12\varphi''(x)\Delta t+o(\Delta t)
}{\Delta t}
$$

拆开：

$$
=
\frac12\varphi''(x)
+
\frac{o(\Delta t)}{\Delta t}
$$

因为：

$$
\frac{o(\Delta t)}{\Delta t}=o(1)\to0
$$

所以：

$$
\frac{
\mathbb{E}\left[\varphi(x+\sqrt{\Delta t}\epsilon)\right]-\varphi(x)
}{\Delta t}
=
\frac12\varphi''(x)+o(1)
$$

令 $\Delta t\to0$：

$$
\frac{
\mathbb{E}\left[\varphi(x+\sqrt{\Delta t}\epsilon)\right]-\varphi(x)
}{\Delta t}
\to
\frac12\varphi''(x)
$$

把它代回整体积分：

$$
\int
\frac{
\mathbb{E}\left[\varphi(x+\sqrt{\Delta t}\epsilon)\right]-\varphi(x)
}{\Delta t}
p_t(x)\,dx
\to
\int \frac12\varphi''(x)p_t(x)\,dx
$$

而这个整体积分原本等于：

$$
\int \varphi(y)\frac{p_{t+\Delta t}(y)-p_t(y)}{\Delta t}\,dy
$$

当 $\Delta t\to0$ 时：

$$
\frac{p_{t+\Delta t}(y)-p_t(y)}{\Delta t}
\to
\partial_t p_t(y)
$$

所以左边变成：

$$
\int \varphi(y)\partial_t p_t(y)\,dy
=
\int \frac12\varphi''(x)p_t(x)\,dx
$$

这一步的含义是：对任意打分器 $\varphi$，它的平均读数变化率都可以由右边算出。

接下来要从“打分器平均值的变化”变成“密度自己的方程”。右边分部积分两次，把导数从 $\varphi$ 转移到 $p_t$。假设边界项为 0：

第一次分部积分：

$$
\int \varphi''(x)p_t(x)\,dx
=
\left[\varphi'(x)p_t(x)\right]_{-\infty}^{\infty}
-
\int \varphi'(x)p_t'(x)\,dx
$$

边界项为 0，所以：

$$
=
-\int \varphi'(x)p_t'(x)\,dx
$$

第二次分部积分：

$$
-\int \varphi'(x)p_t'(x)\,dx
=
-\left[\varphi(x)p_t'(x)\right]_{-\infty}^{\infty}
+
\int \varphi(x)p_t''(x)\,dx
$$

边界项仍为 0，所以：

$$
\int \varphi''(x)p_t(x)\,dx
=
\int \varphi(x)p_t''(x)\,dx
$$

因此：

$$
\int \varphi(x)\partial_t p_t(x)\,dx
=
\int \varphi(x)\frac12p_t''(x)\,dx
$$

移到一边：

$$
\int \varphi(x)
\left[
\partial_t p_t(x)-\frac12p_t''(x)
\right]dx
=
0
$$

这对所有测试函数 $\varphi$ 都成立。直觉上，如果括号里的函数在某个地方不是 0，就可以选一个只在那个地方附近凸起的 $\varphi$，让积分不是 0。因此括号里只能为 0：

$$
\boxed{
\partial_t p_t(x)=\frac12p_t''(x)
}
$$

这就是

$$
dX_t=dW_t
$$

对应的 Fokker-Planck 方程，也就是热方程。

## 7. 一般 SDE 的结果

一般一维 SDE：

$$
dX_t=f(X_t,t)\,dt+g(t)\,dW_t
$$

一小步：

$$
X_{t+\Delta t}
=
x+f(x,t)\Delta t+g(t)\sqrt{\Delta t}\epsilon
$$

同样对 $\varphi$ 展开，记：

$$
\Delta X=f(x,t)\Delta t+g(t)\sqrt{\Delta t}\epsilon
$$

则：

$$
\varphi(x+\Delta X)-\varphi(x)
\approx
\varphi'(x)\Delta X+\frac12\varphi''(x)(\Delta X)^2
$$

取期望：

$$
\mathbb{E}[\Delta X]=f(x,t)\Delta t
$$

$$
\mathbb{E}[(\Delta X)^2]=g(t)^2\Delta t+o(\Delta t)
$$

于是：

$$
\mathbb{E}\left[
\varphi(X_{t+\Delta t})-\varphi(X_t)
\mid X_t=x
\right]
=
\left[
f(x,t)\varphi'(x)
+
\frac12g(t)^2\varphi''(x)
\right]\Delta t
+
o(\Delta t)
$$

所以生成元是：

$$
\mathcal{L}\varphi
=
f\varphi'
+
\frac12g^2\varphi''
$$

对整体分布平均：

$$
\int \varphi\,\partial_t p_t\,dx
=
\int
\left[
f\varphi'
+
\frac12g^2\varphi''
\right]p_t\,dx
$$

分部积分，把导数转移到 $p_t$ 上：

$$
\boxed{
\partial_t p_t
=
-\partial_x(fp_t)
+
\frac12g^2\partial_{xx}p_t
}
$$

多维写法是：

$$
\boxed{
\partial_t p_t(x)
=
-\nabla_x\cdot(f(x,t)p_t(x))
+
\frac12g(t)^2\Delta_x p_t(x)
}
$$

## 8. 和 PF-ODE 的接口

Fokker-Planck 给出密度演化：

$$
\partial_t p_t
=
-\nabla\cdot(fp_t)
+
\frac12g^2\Delta p_t
$$

如果想把它改写成确定性 ODE 的连续性方程：

$$
\partial_t p_t=-\nabla\cdot(p_t v)
$$

就需要用 score：

$$
s_t(x)=\nabla_x\log p_t(x)
$$

因为：

$$
\Delta p_t
=
\nabla\cdot(\nabla p_t)
=
\nabla\cdot(p_t\nabla\log p_t)
=
\nabla\cdot(p_t s_t)
$$

所以：

$$
\partial_t p_t
=
-\nabla\cdot\left[
p_t\left(f-\frac12g^2s_t\right)
\right]
$$

因此 probability flow ODE 的速度场是：

$$
\boxed{
v_{PF}(x,t)=f(x,t)-\frac12g(t)^2s_t(x)
}
$$

这一步在生成模型里对应：SDE 的随机采样可以换成一个确定性 ODE 采样，只要知道或近似 score。

## 常见卡点

- $p_t(y)$ 本来就是密度在 $y$ 的值；写成 $\int\delta(y-x)p_t(x)dx$ 只是为了把“原地不动”也写成转移核。
- $\delta(y-x)$ 是连续选择器，不是普通函数；它的作用是把 $x=y$ 处的值挑出来。
- $q_{\Delta t}$ 是走了一个小时间步后的高斯转移核；$\delta$ 是没走时间的转移核。
- 直接求密度差分会碰到 $(q_{\Delta t}-\delta)/\Delta t$，这个极限不适合当普通函数看。
- 测试函数 $\varphi$ 的作用是先把尖核放进积分，变成 $\mathbb{E}[\varphi(x+\sqrt{\Delta t}\epsilon)]$，然后用 Taylor 展开。
- 弱形式不是凑合；对所有 $\varphi$ 的测量都一致，就确定了分布意义下的密度方程。

## 相关页

- [随机过程](../structures/StochasticProcess.md)
- [布朗运动](../processes/BrownianMotion.md)
- [随机积分](StochasticIntegral.md)
- [随机微分方程](SDE.md)
- [正态分布](../distributions/正态分布.md)
- [Diffusion 模型中的 SDE / PF-ODE](../../../神经网络/models/生成模型.md)
