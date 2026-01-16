# 概率论（Probability）笔记组织说明（可扩展 & 速查）

导航：[math/README.md](../README.md) ｜[math/索引.md](../索引.md) ｜本分支：[math/概率论/索引.md](索引.md) ｜[math/概率论/概念图.md](概念图.md)

这部分按“面向对象/类型系统”的方式组织：
- `ProbabilitySpace`：`(Ω, F, P)`（底层样本空间 + σ-代数 `F` + 概率测度 `P`）
- `RandomVariable`：`X: (Ω, F) -> (R^d, B(R^d))`（可测映射；`B(R^d)` 为 Borel σ-代数）
- `Distribution`：`P_X`（由随机变量诱导的分布）

常用操作（条件、独立、期望、收敛）作为 `modules/` 里的可复用工具箱；例题与练习独立放在 `examples/` 与 `exercises/`。

---

## 目录结构（入口 → 索引 → 概念图 → 模块）
- [math/概率论/README.md](README.md)：入口与组织方式（本页）
- [math/概率论/索引.md](索引.md)：术语索引（中文｜英文｜一句话｜链接）
- [math/概率论/概念图.md](概念图.md)：概念关系图（依赖链/常用路线）

模块（解题工具箱，跨概念复用）：
- `math/概率论/modules/`（待逐步补全）

结构页（“类/接口”风格：对象/公理/性质/例子）：
- `math/概率论/structures/`（待逐步补全，模板见 `structures/_TEMPLATE.md`）

分布页（单个分布的定义/性质/关系，按需扩展子目录）：
- `math/概率论/distributions/`（入口见 [math/概率论/distributions/README.md](distributions/README.md)）

例子与练习：
- [math/概率论/examples/Examples.md](examples/Examples.md)
- [math/概率论/exercises/Exercises.md](exercises/Exercises.md)

---

## 建议学习路线（缺哪块读哪块）
- 概率空间与事件：`(Ω, F, P)`、σ-代数、可测性直觉
- 随机变量与分布：分布函数/密度/质量函数、常见分布族
- 期望与方差：线性性、协方差、常用不等式
- 条件：条件概率/条件期望、Bayes、独立性
- 收敛：依概率/几乎处处/分布收敛（与积分/期望交换的条件）
