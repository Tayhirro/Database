# 参数曲线（Parametric Curve）

## 1. 一句话
- 参数曲线是 `γ: I -> R^n`，用一个参数 `t` 描述轨迹/路径。

## 2. 接口：数据 + 约束（像类型签名）
- 参数区间：`I ⊆ R`（常见 `[a,b]`）
- 映射：`γ(t) = (x_1(t),...,x_n(t))`
- 正则性（常用）：
  - 可导：`γ ∈ C^1(I;R^n)`（能谈速度 `γ'(t)`）

## 3. 例子
- 直线：`γ(t)=p+t d`
- 圆：`γ(t)=(cos t, sin t)`（二维）

## 4. 与主线插件的连接
- 速度/切向量：`γ'(t)`（见 [math/微积分/modules/Differentiation.md](../../modules/Differentiation.md)）
- 线积分接口（后续扩展）：见 [math/微积分/modules/Integration.md](../../modules/Integration.md)

