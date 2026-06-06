---
title: Exercises（因果推断）
date: "2026-03-26"
categories:
  - 因果推断
description: 自测题：区分 see 和 do，判断 collider，写出最小 backdoor 调整式。
---
# Exercises（因果推断）

## 1. `see` vs `do`

- 用一句话解释为什么 `P(Y|X=x)` 一般不等于 `P(Y|do(X=x))`。

## 2. collider

- 在图 `X -> Z <- Y` 中，默认路径是开还是关？
- 如果对 `Z` 条件化，会发生什么？

## 3. backdoor

- 给定图 `Z -> X`, `Z -> Y`, `X -> Y`
- 写出 `P(Y|do(X=x))` 的 backdoor adjustment 公式

## 4. SCM 接口

- 解释 `M = (U, V, F, P(U))` 四个部分各自表示什么

## 5. 反事实

- `Y_x` 和 `P(Y|do(X=x))` 的区别是什么？
