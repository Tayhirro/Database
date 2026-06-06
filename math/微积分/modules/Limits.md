---
title: "极限与连续（Limits & Continuity）"
date: "2026-01-21"
categories:
  - math
description: 极限与连续是微积分的语义底座：先保证“趋近”的概念成立，再谈导数/积分。
---
# 极限与连续（Limits & Continuity）

## 1. 一句话
- 极限与连续是微积分的语义底座：先保证“趋近”的概念成立，再谈导数/积分。

## 2. 接口：对象与目标
- 对象：标量函数 `f: D -> R`（见 `structures/functions/ScalarFunction.md`）
- 目标：描述 `x -> a` 时 `f(x)` 的趋近行为

## 3. ε-δ 口径（统一接口）
- `lim_{x->a} f(x) = L`：对任意 `ε>0`，存在 `δ>0`，使得当 `0<||x-a||<δ` 时，有 `|f(x)-L|<ε`。

## 4. 连续
- `f` 在 `a` 处连续：`lim_{x->a} f(x) = f(a)`

## 5. 连续性的强弱层级
- 常见强弱关系：`Lipschitz 连续 => 一致连续 => 连续`
- Lipschitz 连续比“只要求极限接上函数值”更强，因为它直接给了一个统一的线性变化上界：
  - `||f(x)-f(y)|| <= L ||x-y||`
- 这类更强的正则性常在 ODE 唯一性、优化收敛分析和神经网络稳定性里出现。
- 进一步见 [math/微积分/modules/LipschitzContinuity.md](LipschitzContinuity.md)

