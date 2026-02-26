---
title: 例子库（最小工作例子）
date: "2026-01-09"
categories:
  - math
description: "给定向量 v_1,...,v_n，验证 Gram 矩阵 G_{ij}=<v_i,v_j> 一定是 PSD（直接算 c^T G c）。"
---
# 例子库（最小工作例子）

## PSD / Gram
- 给定向量 `v_1,...,v_n`，验证 Gram 矩阵 `G_{ij}=<v_i,v_j>` 一定是 PSD（直接算 `c^T G c`）。

## 核方法
- 线性核：`k(x,y)=x^T y`，写出 `K = X X^T` 并解释为什么 PSD。
- 多项式核：`k(x,y)=(x^T y + c)^p`（`c≥0`），用“闭包性质”说明它是核（见 `math/线性代数/modules/KernelMethods.md`）。

