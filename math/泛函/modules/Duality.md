---
title: 对偶与表示（Duality）
date: "2026-01-09"
categories:
  - math
description: 对偶空间把“点的几何”转成“线性测试函数”的语言，是弱拓扑、表示定理与 Hilbert 对偶识别的入口。
---
# 对偶与表示（Duality）

## 1. 一句话
- 对偶空间把“点的几何”转成“线性测试函数”的语言，是弱拓扑与表示定理的入口。

## 2. 对偶空间
对赋范空间 `X`：
- `X*`：所有连续线性泛函 `f: X -> R/C` 的集合
- 范数：`||f|| = sup_{||x||≤1} |f(x)|`
- 在有限维空间里，“线性泛函”自动连续，所以代数对偶与连续对偶没有区别。

## 3. 为什么 Hilbert 空间会有“表示”
如果 `H` 有内积，那么每个向量 `u∈H` 都会自然诱导出一个线性泛函：
- 实情形：`phi_u(v) := <u,v>`
- 复情形（按本仓“第一变量线性”的约定）：`phi_u(v) := <v,u>`

也就是说，内积先给了我们一个从“向量”到“线性泛函”的映射。  
Riesz 表示定理说明：在 Hilbert 空间里，这个映射不但存在，而且**恰好把所有连续线性泛函都捕获完了**。

## 4. 有限维实内积空间上的 Riesz 表示
设 `V` 是有限维实内积空间。对任意线性泛函 `phi ∈ V*`，存在唯一的向量 `u ∈ V`，使得对任意 `v ∈ V`，
$$
\phi(v)=\langle u,v\rangle.
$$

### 4.1 这条定理为什么挂在这里
- 主题上，它说的是“线性泛函如何由向量表示”，所以属于“对偶与表示”，而不是单独挂在“内积空间定义”页里。
- 结构上，有限维实内积空间自动完备，因此本质上是一般 Hilbert 空间 Riesz 表示的一个特例。

### 4.2 证明思路（有限维版）
取 `V` 的一组正交归一基 `e_1,...,e_n`，定义
$$
u := \sum_{i=1}^n \phi(e_i)e_i.
$$
若 `v = \sum_{i=1}^n a_i e_i`，则
$$
\phi(v)=\sum_{i=1}^n a_i\phi(e_i)=\left\langle \sum_{i=1}^n \phi(e_i)e_i,\ \sum_{i=1}^n a_i e_i \right\rangle = \langle u,v\rangle.
$$
所以存在性成立。

唯一性也直接：若 `\langle u_1,v\rangle = \langle u_2,v\rangle` 对所有 `v` 都成立，则
$$
\langle u_1-u_2, v\rangle = 0,\qquad \forall v\in V.
$$
取 `v = u_1-u_2`，得到 `||u_1-u_2||^2 = 0`，故 `u_1=u_2`。

## 5. 一般 Hilbert 空间上的 Riesz 表示
设 `H` 是实或复 Hilbert 空间，则对任意连续线性泛函 `phi ∈ H*`，存在唯一 `u ∈ H`，使得：
- 实情形：`phi(v)=<u,v>`
- 复情形（按本仓约定）：`phi(v)=<v,u>`

并且这个对应是等距同构：
$$
H \cong H^*,\qquad ||\phi_u|| = ||u||.
$$

如果你采用“第二变量线性”的内积约定，那么复 Hilbert 空间里常写成 `phi(v)=<u,v>`；区别只在共轭放在哪个变量上，定理内容本身不变。

## 6. 你该记住什么
- 对偶空间 `X*` 的元素是“线性测试函数”。
- 在一般赋范空间里，`X*` 只是一个新的空间；不一定能直接和 `X` 识别。
- 在 Hilbert 空间里，Riesz 表示把“连续线性泛函”与“向量”一一对应起来，所以 `H*` 可以用 `H` 本身来表示。
- 有限维实内积空间的结论，就是这条主线最容易上手的版本。

## 7. 速查
- 内积空间基础：见 `math/泛函/structures/spaces/InnerProductSpace.md`
- Hilbert 空间：见 `math/泛函/structures/spaces/HilbertSpace.md`
- 弱拓扑/弱收敛用 `X*` 来定义（见 `math/泛函/modules/Convergence.md`）
