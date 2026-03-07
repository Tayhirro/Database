---
title: "级数与 Taylor（Series & Taylor）"
date: "2026-01-21"
categories:
  - math
description: 级数/Taylor 是“把函数变成多项式”的插件：用可控误差做局部近似与计算。
---
# 级数与 Taylor（Series & Taylor）

## 1. 一句话
- 级数/Taylor 是“把函数变成多项式”的插件：用可控误差做局部近似与计算。

## 2. Taylor 展开（接口级）
- `f(x) ≈ Σ_{k=0}^n f^{(k)}(a)/k! · (x-a)^k`

## 3. 二阶 Taylor（最常用）
- 一元：`f(x) ≈ f(a) + f'(a)(x-a) + 1/2 f''(a)(x-a)^2`
- 多元：`f(x+Δ) ≈ f(x) + ∇f(x)^TΔ + 1/2·Δ^T H_f(x)Δ`

## 4. 关联入口
- 导数基础（含 Hessian 与二阶判别）：见 [math/微积分/basics/导数基础.md](../basics/导数基础.md)
