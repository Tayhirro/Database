# 微积分（Calculus）笔记组织说明（面向对象 & 插件化）

导航：[math/README.md](../README.md) ｜[math/索引.md](../索引.md) ｜本分支：[math/微积分/索引.md](索引.md) ｜[math/微积分/概念图.md](概念图.md)

这部分用“面向对象/接口 + 插件化”的方式组织：
- 先搭一个**不偏科**的微积分主框架（极限→导数→积分→级数→ODE）
- 再把具体子主题（例如“场/向量分析”）作为插件挂载：可独立扩展、可随时加深

---

## 1. 目录结构（入口 → 索引 → 概念图 → 结构页 → 模块）
- [math/微积分/README.md](README.md)：入口与组织方式（本页）
- [math/微积分/索引.md](索引.md)：术语索引（中文｜英文｜一句话｜链接）
- [math/微积分/概念图.md](概念图.md)：概念关系图（主线 + 插件）

结构页（像“类文档”，一概念一页）：
- 主框架对象（通用）：
  - [math/微积分/structures/functions/ScalarFunction.md](structures/functions/ScalarFunction.md)：标量函数 `f: D -> R`
  - [math/微积分/structures/functions/VectorValuedFunction.md](structures/functions/VectorValuedFunction.md)：向量值函数 `g: D -> R^m`
  - [math/微积分/structures/geometry/ParametricCurve.md](structures/geometry/ParametricCurve.md)：参数曲线 `γ: I -> R^n`
- 插件对象（场/向量分析）：
  - [math/微积分/structures/fields/ScalarField.md](structures/fields/ScalarField.md)：标量场 `f(x)`（位置 -> 标量）
  - [math/微积分/structures/fields/VectorField.md](structures/fields/VectorField.md)：向量场 `v(x)`（位置 -> 向量）

模块（对对象做的操作/定理接口；“插件形式”组织）：
- 主线插件：
  - [math/微积分/modules/Limits.md](modules/Limits.md)：极限/连续（ε-δ）
  - [math/微积分/modules/Differentiation.md](modules/Differentiation.md)：导数/偏导/梯度/Jacobian
  - [math/微积分/modules/Integration.md](modules/Integration.md)：积分（从一元到多元的接口）
  - [math/微积分/modules/Series.md](modules/Series.md)：级数/Taylor（近似接口）
  - [math/微积分/modules/ODE.md](modules/ODE.md)：常微分方程（最小框架）
- 向量分析插件（你补充“场”的位置在这里）：
  - [math/微积分/modules/VectorOperators.md](modules/VectorOperators.md)：`∇f`、`∇·v`、`∇×v`、`Δf` 等

例子与练习（混合主线 + 插件，避免偏向）：
- [math/微积分/examples/Examples.md](examples/Examples.md)
- [math/微积分/exercises/Exercises.md](exercises/Exercises.md)

---

## 2. “场”在这个框架里怎么用（插件口径）
“场”不是微积分的全部，它是微积分里非常常用的一类对象：把“位置”作为输入的函数。
- 标量场：`f: Ω -> R`（见 `structures/fields/ScalarField.md`）
- 向量场：`v: Ω -> R^n`（见 `structures/fields/VectorField.md`）

它通常通过这些接口与主线连接：
- `Differentiation`：梯度/Jacobian（局部线性化）
- `VectorOperators`：`∇/div/curl/Δ` 这套专用算子
- `Integration`：线积分/面积分/体积分（后续可插：Green/Stokes/散度定理）

