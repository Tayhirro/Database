# 向量值函数（Vector-Valued Function）

## 1. 一句话
- 向量值函数就是 `g: D -> R^m`；它把“一个输入点”映射成一个向量输出。

## 2. 接口：数据 + 约束（像类型签名）
- 定义域：`D ⊆ R^n`
- 值域：`R^m`
- 分量表示：`g(x) = (g_1(x),...,g_m(x))`
- 正则性（常用）：
  - 连续：`g ∈ C(D;R^m)`
  - 可微：`g ∈ C^1(D;R^m)`（能谈 Jacobian）

## 3. 例子（与插件的关系）
- 参数曲线：`γ: I -> R^n`（`n=1` 输入的特例），见 [math/微积分/structures/geometry/ParametricCurve.md](../geometry/ParametricCurve.md)
- 向量场：`v: Ω -> R^n`（把 `D` 解释成空间位置），见 [math/微积分/structures/fields/VectorField.md](../fields/VectorField.md)
- 工程对照：光流/位移 `f(u,v)=(Δu,Δv)` 是 `R^2 -> R^2` 的向量值函数

## 4. 最常用导数对象
- Jacobian：`J_g(x) = [∂g_i/∂x_j]`
- 入口：见 [math/微积分/modules/Differentiation.md](../../modules/Differentiation.md)

