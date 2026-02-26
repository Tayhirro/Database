---
title: "商向量空间（Quotient Vector Space, $V/U$）"
date: "2026-01-22"
categories:
  - math
description: 商向量空间把子空间  中的向量视为“零方向”并进行等价类折叠，从而把  按  的方向进行压缩。
---
# 商向量空间（Quotient Vector Space, $V/U$）

## 一句话
商向量空间把子空间 $U$ 中的向量视为“零方向”并进行等价类折叠，从而把 $V$ 按 $U$ 的方向进行压缩。

## 严格定义
设 $\mathbb{F}\in\{\mathbb{R},\mathbb{C}\}$，$V$ 为 $\mathbb{F}$-向量空间，$U\le V$ 为子空间。

定义等价关系：对 $v,w\in V$，
$$
v\sim w \;\Longleftrightarrow\; v-w\in U.
$$
等价类记作 $[v]=v+U$。所有等价类构成集合 $V/U$，并定义运算
$$
[v]+[w]=[v+w],\qquad a[v]=[av]\quad (a\in\mathbb{F}),
$$
则 $V/U$ 成为向量空间，称为商向量空间（quotient vector space）。

## 接口：数据 + 约束
- 数据：向量空间 $V$ 与其子空间 $U\le V$。
- 输出：向量空间 $V/U$（其元素为等价类 $[v]$）。
- 约束：$U$ 必须是子空间（保证加法与数乘在等价类上良定义）。

## 常用构造/操作（仅列接口与符号）
- 商映射（canonical projection）：
$$
\pi:V\to V/U,\qquad \pi(v)=[v].
$$
- 维数（有限维）：若 $V$ 有限维，则
$$
\dim(V/U)=\dim(V)-\dim(U).
$$
- 诱导映射（线性）：若 $A:V\to W$ 为线性映射且 $U\subseteq \ker(A)$，则可定义
$$
\tilde{A}:V/U\to W,\qquad \tilde{A}([v])=A(v),
$$
该定义在 $U\subseteq\ker(A)$ 下良定义。

## 关系：上级/下级/等价/特例/推广
- 上级：子空间、等价关系与商对象（集合层面的“商”见 `math/离散/modules/Quotient.md`）。
- 相关：秩-零化度定理的商空间视角（见 [math/线性代数/modules/Rank.md](Rank.md)）、直和与补空间（见 [math/线性代数/modules/DirectSum.md](DirectSum.md)）。

## 把新概念挂回框架（多级索引轨迹）
math → 线性代数 → 向量空间/子空间 → 商向量空间（quotient）。

