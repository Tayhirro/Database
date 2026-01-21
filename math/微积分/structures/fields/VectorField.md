# 向量场（Vector Field）

## 1. 一句话
- 向量场就是“每个位置一个向量”：本质是函数 `v: Ω -> R^n`（可视为 `VectorValuedFunction` 的一个常用特化：把定义域解释成空间位置）。

## 2. 接口：数据 + 约束（像类型签名）
- 定义域：`Ω ⊆ R^n`
- 值域：`R^n`
- 分量表示：`v(x) = (v_1(x),...,v_n(x))`
- 正则性（常用）：
  - 连续：`v ∈ C(Ω;R^n)`
  - 可微：`v ∈ C^1(Ω;R^n)`（能谈散度/旋度/Jacobian）

## 3. 例子（把“场”直觉固定住）
- 风场/速度场：`v(x,y) = (v_x(x,y), v_y(x,y))`
- 工程对照（CV）：光流/位移场 `f(u,v) = (Δu(u,v), Δv(u,v))`
- 来自标量场的梯度场：`v=∇f`

## 4. 你能对它做什么（插件：场/向量分析）
- Jacobian：`J_v(x) = [∂v_i/∂x_j]`
- 散度：`∇·v = Σ_i ∂v_i/∂x_i`
- 旋度：`∇×v`（3D 给向量；2D 常给标量）
- 入口：见 [math/微积分/modules/VectorOperators.md](../../modules/VectorOperators.md)

## 5. 与主框架的关系（插件化）
- 作为向量值函数对象：见 [math/微积分/structures/functions/VectorValuedFunction.md](../functions/VectorValuedFunction.md)

