---
title: 标量场（Scalar Field）
date: "2026-01-21"
categories:
  - math
description: 标量场就是 （把定义域解释成空间位置的标量函数）。
---
# 标量场（Scalar Field）

## 1. 一句话
- 标量场就是 $f:\Omega \to \mathbb{R}$（把定义域解释成空间位置的标量函数）。

## 2. 严格定义（基底集合/参数空间上的赋值）
给定一个集合（基底集合/参数空间）$B$ 与一个集合 $V$，一个（取值于 $V$ 的）场就是一个映射
$$
F:B\to V.
$$
当 $B=\Omega \subseteq \mathbb{R}^n$ 且 $V=\mathbb{R}$ 时，$F$ 称为标量场（记作 $f$）。

## 3. 接口：数据 + 约束（像类型签名）
- 定义域（空间/区域）：$\Omega \subseteq \mathbb{R}^n$
- 值域：$\mathbb{R}$（或 $\mathbb{C}$）
- 正则性（可选但常用）：
  - 连续：$f \in C(\Omega)$
  - 可微：$f \in C^1(\Omega)$
  - 二阶可微：$f \in C^2(\Omega)$

## 4. 例子
- 温度场：$T(x,y) \in \mathbb{R}$
- 密度场/势能场：$\rho(x)$、$U(x)$

## 5. 常用操作（向量分析）
- 梯度：$\nabla f$
- 拉普拉斯：$\Delta f = \nabla\cdot\nabla f$
- 入口：见 [math/微积分/modules/VectorOperators.md](../../modules/VectorOperators.md)

## 6. 与主框架的关系
- 作为函数对象：见 [math/微积分/structures/functions/ScalarFunction.md](../functions/ScalarFunction.md)
