---
title: 同构定理（Isomorphism Theorems）速查
date: "2026-01-09"
categories:
  - math
description: 目标：把“核/像/商”一口气联动起来。写法尽量统一，落地到群/环时只改“核是什么子对象”。
---
# 同构定理（Isomorphism Theorems）速查

> 目标：把“核/像/商”一口气联动起来。写法尽量统一，落地到群/环时只改“核是什么子对象”。

## 0. 预备：同态、核、像、商对象
见 `math/离散/modules/Morphism.md`、`math/离散/modules/Relation.md` 与 `math/离散/modules/Quotient.md`。

## 1. 第一同构定理（最常用）
若 `f: A -> B` 是同态，则：
- `A/ker(f) ≅ im(f)`

落地：
- 群：`G/ker(f) ≅ im(f)`（`ker(f)` 正规）
- 环：`R/ker(f) ≅ im(f)`（`ker(f)` 理想）

## 2. 第二同构定理（子对象与商的交换）
群版（最常见表述）：
给定群 `G`，`H ≤ G`，`N ⊲ G`，则
- `H/(H∩N) ≅ HN/N`

直觉：`HN` 是把 `H` 通过 `N` “加厚”，再与 `N` 做商。

## 3. 第三同构定理（商的商）
群版（`N ⊲ M ⊲ G`）：
- `(G/N)/(M/N) ≅ G/M`

环版同理（`I ⊆ J` 为理想）：
- `(R/I)/(J/I) ≅ R/J`

## 4. 用法提示（你做题时怎么秒用）
- 想用第一定理：先找同态 `f`，算核与像
- 想用第二/第三：先确认“能做商”的条件（群要正规，环要理想）
