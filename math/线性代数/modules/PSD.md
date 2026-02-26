---
title: "半正定矩阵（PSD, Positive Semidefinite）"
date: "2026-01-09"
categories:
  - math
description: PSD（半正定）刻画“二次型永不为负”，是投影/最小二乘/协方差/核方法（Gram 矩阵）的底座。
---
# 半正定矩阵（PSD, Positive Semidefinite）

## 1. 一句话
- PSD（半正定）刻画“二次型永不为负”，是投影/最小二乘/协方差/核方法（Gram 矩阵）的底座。

## 2. 定义
### 2.1 实矩阵（最常用）
设 `A∈R^{n×n}` 为**对称矩阵**。若对任意 `x∈R^n` 都有
- `x^T A x ≥ 0`
则称 `A` **半正定**，记作 `A \succeq 0`（PSD）。

若对任意 `x≠0` 都有 `x^T A x > 0`，则称 `A` **正定**，记作 `A \succ 0`（PD）。

### 2.2 复矩阵（可选）
`A∈C^{n×n}` 为 Hermitian（`A=A*`），且 `x* A x ≥ 0`（对任意 `x`），则 `A \succeq 0`。

## 3. 等价刻画（速查）
对实对称矩阵 `A`，以下等价：
- `A \succeq 0`
- `A` 的所有特征值 `λ_i ≥ 0`
- 存在矩阵 `B` 使 `A = B^T B`（Gram/平方根分解）
- 存在向量 `v_1,...,v_n` 使 `A_{ij} = <v_i, v_j>`（`A` 是某组向量的 Gram 矩阵）

> 注：Cholesky 分解 `A=LL^T` 在 `A \succ 0` 时最稳定；`A \succeq 0` 时也常可做“带主元/截断”的版本，但要允许零对角元。

## 4. 常用闭包性质（用来“拼”PSD）
- `A,B \succeq 0` => `A+B \succeq 0`
- `c≥0` => `cA \succeq 0`
- 任意矩阵 `M`：`M^T A M \succeq 0`（合同变换保 PSD）
- Schur 乘积定理：`A,B \succeq 0` => `A \odot B \succeq 0`（Hadamard/逐元素乘积仍 PSD）

## 5. Gram 矩阵一定 PSD（核方法的关键一步）
给定向量 `v_1,...,v_n`（在某个内积空间里），定义 Gram 矩阵 `G`：
- `G_{ij} = <v_i, v_j>`

则对任意系数 `c∈R^n`：
- `c^T G c = Σ_{i,j} c_i c_j <v_i,v_j> = <Σ_i c_i v_i, Σ_j c_j v_j> = ||Σ_i c_i v_i||^2 ≥ 0`

所以 `G \succeq 0`。

## 6. 你怎么证明一个矩阵是 PSD（常用套路）
- 直接验定义：把 `x^T A x` 化成平方和/范数平方
- 找分解：写成 `A=B^T B` 或 `A=QΛQ^T` 且 `Λ≥0`
- 识别为 Gram：把 `A_{ij}` 写成某种内积 `<v_i,v_j>`
