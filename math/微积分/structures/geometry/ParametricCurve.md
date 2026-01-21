# 参数曲线（Parametric Curve）

## 1. 一句话
- 参数曲线是 $\gamma: I \to \mathbb{R}^n$，用一个参数 `t` 描述轨迹/路径。

## 2. 接口：数据 + 约束（像类型签名）
- 参数区间：$I \subseteq \mathbb{R}$（常见 `[a,b]`）
- 映射：$\gamma(t) = (x_1(t),...,x_n(t))$
- 正则性（常用）：
  - 可导：$\gamma \in C^1(I;\mathbb{R}^n)$（能谈速度 $\gamma'(t)$）

## 3. 例子
- 直线：$\gamma(t)=p+t d$
- 圆：$\gamma(t)=(\cos t, \sin t)$（二维）

## 4. 与主线模块的连接
- 速度/切向量：$\gamma'(t)$（见 [math/微积分/modules/Differentiation.md](../../modules/Differentiation.md)）
- 积分：见 [math/微积分/modules/Integration.md](../../modules/Integration.md)
