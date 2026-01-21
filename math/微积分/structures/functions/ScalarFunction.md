# 标量函数（Scalar Function）

## 1. 一句话
- 标量函数就是 $f:D \to \mathbb{R}$。

## 2. 严格定义（集合论口径）
给定集合 $D$ 与集合 $Y$，一个函数 $f:D\to Y$ 可以定义为满足以下性质的子集（函数的图像）
$$
f \subseteq D\times Y
$$
使得对任意 $x\in D$，存在且仅存在一个 $y\in Y$ 使得 $(x,y)\in f$。此时记 $y=f(x)$。

在本页中取 $Y=\mathbb{R}$（或 $\mathbb{C}$）。

## 3. 接口：数据 + 约束（像类型签名）
- 定义域：$D \subseteq \mathbb{R}^n$（一元/多元只是 `n` 的差别）
- 值域：$\mathbb{R}$（或 $\mathbb{C}$）
- 正则性（可选）：
  - 连续：$f \in C(D)$
  - 一阶可微：$f \in C^1(D)$
  - 二阶可微：$f \in C^2(D)$

## 4. 例子与反例
- $f(x)=\sin x$（$D=\mathbb{R}$）
- $f(x,y)=x^2+y^2$（$D=\mathbb{R}^2$）
- `f(x)=|x|` 在 `x=0` 不可导

## 5. 你能对它做什么（主线模块）
- 极限/连续：见 [math/微积分/modules/Limits.md](../../modules/Limits.md)
- 导数/偏导/梯度：见 [math/微积分/modules/Differentiation.md](../../modules/Differentiation.md)
- 积分：见 [math/微积分/modules/Integration.md](../../modules/Integration.md)
- 级数/Taylor：见 [math/微积分/modules/Series.md](../../modules/Series.md)
