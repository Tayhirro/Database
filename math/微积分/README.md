# 微积分（Calculus）笔记组织说明（面向对象 & 框架）

导航：[math/README.md](../README.md) ｜[math/索引.md](../索引.md) ｜本分支：[math/微积分/索引.md](索引.md) ｜[math/微积分/概念图.md](概念图.md)

这部分先搭一个可检索的微积分主框架：对象（结构页）+ 操作（模块页）。细分主题后续按需补充为独立页面。

---

## 1. 目录结构（入口 → 索引 → 概念图 → 结构页 → 模块）
- [math/微积分/README.md](README.md)：入口与组织方式（本页）
- [math/微积分/索引.md](索引.md)：术语索引（中文｜英文｜一句话｜链接）
- [math/微积分/概念图.md](概念图.md)：概念关系图（对象与模块）

结构页（像“类文档”，一概念一页）：
- [math/微积分/structures/functions/ScalarFunction.md](structures/functions/ScalarFunction.md)：标量函数 $f:D \to \mathbb{R}$
- [math/微积分/structures/functions/VectorValuedFunction.md](structures/functions/VectorValuedFunction.md)：向量值函数 $g:D \to \mathbb{R}^m$
- [math/微积分/structures/geometry/ParametricCurve.md](structures/geometry/ParametricCurve.md)：参数曲线 $\gamma:I \to \mathbb{R}^n$

模块（对对象做的操作/定理接口）：
- [math/微积分/modules/Limits.md](modules/Limits.md)：极限/连续（ε-δ）
- [math/微积分/modules/Differentiation.md](modules/Differentiation.md)：导数/偏导/梯度/Jacobian
- [math/微积分/modules/Integration.md](modules/Integration.md)：积分（从一元到多元的接口）
- [math/微积分/modules/Series.md](modules/Series.md)：级数/Taylor（近似接口）
- [math/微积分/modules/ODE.md](modules/ODE.md)：常微分方程（最小框架）

例子与练习：
- [math/微积分/examples/Examples.md](examples/Examples.md)
- [math/微积分/exercises/Exercises.md](exercises/Exercises.md)
