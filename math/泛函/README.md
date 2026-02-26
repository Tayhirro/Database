---
title: "泛函（Functional Analysis）笔记组织说明（可扩展 & 速查）"
date: "2026-01-13"
categories:
  - math
description: 导航：math/README.md ｜math/索引.md ｜本分支：math/泛函/索引.md ｜math/泛函/概念图.md
---
# 泛函（Functional Analysis）笔记组织说明（可扩展 & 速查）

导航：[math/README.md](../README.md) ｜[math/索引.md](../索引.md) ｜本分支：[math/泛函/索引.md](索引.md) ｜[math/泛函/概念图.md](概念图.md)

这部分按“结构（空间）+ 态射（算子/泛函）+ 收敛（范畴/拓扑）+ 工具定理”的方式组织：  
你查概念时先定位它属于哪类“空间”，再看它允许什么“算子/泛函”，最后看要用哪种“收敛/拓扑”与对应定理。

---

## 1. 目录结构（入口 → 概念页 → 模块复用）
- [math/泛函/README.md](README.md)：入口与组织方式（本页）
- [math/泛函/索引.md](索引.md)：术语索引（中文｜英文｜一句话｜链接）
- [math/泛函/概念图.md](概念图.md)：概念关系图（继承/依赖/常用路线）

概念页（“空间类”与“典型函数空间”）：
- [math/泛函/structures/spaces/NormedSpace.md](structures/spaces/NormedSpace.md)
- [math/泛函/structures/spaces/BanachSpace.md](structures/spaces/BanachSpace.md)
- [math/泛函/structures/spaces/InnerProductSpace.md](structures/spaces/InnerProductSpace.md)
- [math/泛函/structures/spaces/HilbertSpace.md](structures/spaces/HilbertSpace.md)
- [math/泛函/structures/function-spaces/Lp.md](structures/function-spaces/Lp.md)
- [math/泛函/structures/function-spaces/Sobolev.md](structures/function-spaces/Sobolev.md)

横切模块（跨多个空间复用）：
- [math/泛函/modules/Operators.md](modules/Operators.md)：线性算子、有界性、伴随、谱（后续扩展）
- [math/泛函/modules/Duality.md](modules/Duality.md)：对偶空间、算子范数、Riesz 表示（Hilbert）
- [math/泛函/modules/Convergence.md](modules/Convergence.md)：范数/弱/弱* 收敛与紧性套路
- [math/泛函/modules/Theorems.md](modules/Theorems.md)：Hahn–Banach、开映射、闭图像、Banach–Steinhaus 等

例子与练习（最小工作例子/自测）：
- [math/泛函/examples/Examples.md](examples/Examples.md)
- [math/泛函/exercises/Exercises.md](exercises/Exercises.md)

---

## 2. 速查路线（你遇到概念时怎么落位）
1) 它在哪个空间里？（`Normed/Banach/Hilbert/Lp/Sobolev`…）
2) 你研究的是“点”（向量/函数）还是“箭头”（算子/泛函）？
3) 你用哪种拓扑/收敛？（范数、弱、弱*）
4) 你要用哪条大定理？（Hahn–Banach / 开映射 / 闭图像 / 一致有界…）

---

## 3. 每个概念页的固定模板（方便扩展）
每页尽量保持同一结构，利于“扫一眼就能定位”：
1) 一句话：是什么/干什么  
2) 定义：数据 + 公理（或等价刻画）  
3) 典型例子/反例：至少 2 个  
4) 常见构造：子空间/商空间/完备化/直和  
5) 与算子/对偶的交互：最常用结论  
6) 速查：常用定理入口与关键词
