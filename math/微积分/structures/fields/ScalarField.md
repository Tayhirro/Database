# 标量场（Scalar Field）

## 1. 一句话
- 标量场就是“每个位置一个数”：本质是函数 `f: Ω -> R`（可视为 `ScalarFunction` 的一个常用特化：把定义域解释成空间位置）。

## 2. 接口：数据 + 约束（像类型签名）
- 定义域（空间/区域）：`Ω ⊆ R^n`（也可以是曲面/流形上的区域）
- 值域：`R`（或 `C`）
- 正则性（可选但很关键）：
  - 连续：`f ∈ C(Ω)`
  - 可微：`f ∈ C^1(Ω)`（能谈梯度）
  - 二阶可微：`f ∈ C^2(Ω)`（能谈拉普拉斯）

## 3. 例子与反例
- 温度场：`T(x,y) ∈ R`
- 密度场/势能场：`ρ(x)`、`U(x)`
- 工程对照：灰度图像 `I(u,v)` 就是离散标量场（网格上的函数）

## 4. 你能对它做什么（插件：场/向量分析）
- 梯度：`∇f`（得到向量场）
- 拉普拉斯：`Δf = ∇·∇f`
- 入口：见 [math/微积分/modules/VectorOperators.md](../../modules/VectorOperators.md)

## 5. 与主框架的关系（插件化）
- 作为函数对象：见 [math/微积分/structures/functions/ScalarFunction.md](../functions/ScalarFunction.md)
- 极限/导数/积分这些主线模块一样适用：见 `modules/Limits/Differentiation/Integration`

