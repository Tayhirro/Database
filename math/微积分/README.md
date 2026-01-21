# 微积分 / 向量分析（Calculus & Vector Calculus）笔记组织说明（面向对象 & 速查）

导航：[math/README.md](../README.md) ｜[math/索引.md](../索引.md) ｜本分支：[math/微积分/索引.md](索引.md) ｜[math/微积分/概念图.md](概念图.md)

这部分用“面向对象/接口”的方式组织：  
把 **标量场**、**向量场** 当作“对象类型（data + 约束）”，把 **梯度/散度/旋度/拉普拉斯** 当作“对对象的操作（模块）”。

---

## 1. 目录结构（入口 → 索引 → 概念图 → 结构页 → 模块）
- [math/微积分/README.md](README.md)：入口与组织方式（本页）
- [math/微积分/索引.md](索引.md)：术语索引（中文｜英文｜一句话｜链接）
- [math/微积分/概念图.md](概念图.md)：概念关系图（依赖链/常用路线）

结构页（像“类文档”，一概念一页）：
- [math/微积分/structures/fields/ScalarField.md](structures/fields/ScalarField.md)：标量场 `f(x)`（每个位置一个标量）
- [math/微积分/structures/fields/VectorField.md](structures/fields/VectorField.md)：向量场 `v(x)`（每个位置一个向量）

模块（对结构做的常用操作/定理接口）：
- [math/微积分/modules/Operators.md](modules/Operators.md)：`∇f`、`∇·v`、`∇×v`、`Δf`、Jacobian 等

例子与练习（最小工作例子/自测）：
- [math/微积分/examples/Examples.md](examples/Examples.md)
- [math/微积分/exercises/Exercises.md](exercises/Exercises.md)

---

## 2. “场”一句话（和你的工程直觉对齐）
- “场” = 函数：输入是位置 `x`，输出是数/向量/张量。
  - 标量场：`f: Ω -> R`
  - 向量场：`v: Ω -> R^n`
- 在 CV/深度学习里常见的 “flow/displacement field” 就是 **二维向量场**（对每个像素给 `(Δu,Δv)`）。

