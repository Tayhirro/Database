---
title: 子对象：子群 / 正规子群 / 理想（Subobject）
date: "2026-01-08"
categories:
  - math
description: H ⊆ G 且对群运算封闭，并包含单位元与逆元。
---
# 子对象：子群 / 正规子群 / 理想（Subobject）

## 1. 子群（subgroup）
- `H ⊆ G` 且对群运算封闭，并包含单位元与逆元。

## 2. 正规子群（normal subgroup）
- `N ⊲ G`：对所有 `g∈G`，有 `gNg^{-1}=N`。
- 作用：只有正规子群才能做商群 `G/N`，并让运算良定义。

## 3. 理想（ideal）
在环 `R` 中，`I ⊆ R` 称为（双边）理想，如果：
- `(I,+)` 是 `R` 的加法子群
- `r∈R, a∈I => ra∈I` 且 `ar∈I`
作用：理想是环同态的核，也是商环 `R/I` 的“许可条件”。
