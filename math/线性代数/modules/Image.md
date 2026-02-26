---
title: "像空间（Image / Range, Im(A)）"
date: "2026-01-22"
categories:
  - math
description: 线性映射  的像空间（image/range）是所有可能输出向量所组成的集合。
---
# 像空间（Image / Range, `Im(A)`）

## 一句话
线性映射 $A:V\to W$ 的像空间（image/range）是所有可能输出向量所组成的集合。

## 严格定义
设 $\mathbb{F}\in\{\mathbb{R},\mathbb{C}\}$，$V,W$ 为 $\mathbb{F}$-向量空间，$A:V\to W$ 为线性映射，则
$$
\operatorname{Im}(A)\;=\;\{A(v):\ v\in V\}\subseteq W.
$$

## 接口：数据 + 约束
- 数据：线性映射 $A:V\to W$。
- 输出：集合 $\operatorname{Im}(A)\subseteq W$。
- 约束：$A$ 为线性映射（从而 $\operatorname{Im}(A)$ 是 $W$ 的线性子空间）。

## 常用构造/操作（仅列出接口与符号）
- 维数：$\dim(\operatorname{Im}(A))$（即 $\operatorname{rank}(A)$，见 [math/线性代数/modules/Rank.md](Rank.md)）。
- 矩阵情形：若 $A$ 由矩阵 $A\in\mathbb{F}^{m\times n}$ 表示，则
$$
\operatorname{Im}(A)=\operatorname{Col}(A)=\operatorname{span}\{a_1,\ldots,a_n\}\subseteq \mathbb{F}^m,
$$
其中 $a_i$ 是 $A$ 的第 $i$ 列。

## 关系：上级/下级/等价/特例/推广
- 上级：线性映射、子空间。
- 等价（矩阵视角）：$\operatorname{Im}(A)=\operatorname{Col}(A)$。
- 相关：线性映射的基本子空间（见 [math/线性代数/modules/FundamentalSubspaces.md](FundamentalSubspaces.md)）。

## 把新概念挂回框架（多级索引轨迹）
math → 线性代数 → 线性映射 → 像空间（image/range）。

