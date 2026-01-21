# 标量函数（Scalar Function）

## 1. 一句话
- 标量函数就是 `f: D -> R`；微积分里大多数对象都能先降维理解成“函数”。

## 2. 接口：数据 + 约束（像类型签名）
- 定义域：`D ⊆ R^n`（一元/多元只是 `n` 的差别）
- 值域：`R`（或 `C`）
- 正则性（可选）：
  - 连续：`f ∈ C(D)`
  - 一阶可微：`f ∈ C^1(D)`
  - 二阶可微：`f ∈ C^2(D)`

## 3. 例子与反例
- 一元：`f(x)=sin(x)`（`D=R`）
- 多元：`f(x,y)=x^2+y^2`（`D=R^2`）
- 反例：`f(x)=|x|` 在 `x=0` 不可导

## 4. 你能对它做什么（主线插件）
- 极限/连续：见 [math/微积分/modules/Limits.md](../../modules/Limits.md)
- 导数/偏导/梯度：见 [math/微积分/modules/Differentiation.md](../../modules/Differentiation.md)
- 积分：见 [math/微积分/modules/Integration.md](../../modules/Integration.md)
- 局部近似（Taylor）：见 [math/微积分/modules/Series.md](../../modules/Series.md)

