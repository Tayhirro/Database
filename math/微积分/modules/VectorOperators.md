---
title: 向量分析算子（Vector Operators）
date: "2026-01-21"
categories:
  - math
description: 标量场： 向量场：， 变量：
---
# 向量分析算子（Vector Operators）

## 1. 统一记号
- 标量场：$f:\Omega \to \mathbb{R}$
- 向量场：$v:\Omega \to \mathbb{R}^n$，$v=(v_1,...,v_n)$
- 变量：$x=(x_1,...,x_n)$

## 2. 严格定义（坐标口径与前提）
以下都在标准坐标下给出，并默认所需偏导存在（例如 $f\in C^1$、$v\in C^1$）。

## 3. 梯度（gradient）
若 $f \in C^1$，定义
- $\nabla f = (\partial f/\partial x_1, ..., \partial f/\partial x_n)$

等价刻画：对任意方向 $u\in \mathbb{R}^n$，
$$
D_uf(x)=\langle \nabla f(x),u\rangle,
$$
其中 $\langle\cdot,\cdot\rangle$ 为 $\mathbb{R}^n$ 的标准内积。

## 4. 散度（divergence）
若 $v \in C^1$，定义
- $\nabla\cdot v = \sum_{i=1}^n \partial v_i/\partial x_i$

等价刻画：若 $Dv(x)$ 表示导数（Jacobian）对应的线性映射，则
$$
\mathrm{div}\,v(x)=\mathrm{tr}(Dv(x)).
$$

## 5. 旋度（curl）
### 5.1 三维（`n=3`）
若 $v=(v_1,v_2,v_3) \in C^1$，定义
- $\nabla\times v = ( \partial v_3/\partial x_2 - \partial v_2/\partial x_3,  \partial v_1/\partial x_3 - \partial v_3/\partial x_1,  \partial v_2/\partial x_1 - \partial v_1/\partial x_2 )$

### 5.2 二维（`n=2`，常用标量旋度）
对 `v=(v_1,v_2)`，常定义一个标量
- $curl(v) := \partial v_2/\partial x_1 - \partial v_1/\partial x_2$

## 6. 拉普拉斯（Laplacian）
若 $f \in C^2$，定义
- $\Delta f = \nabla\cdot\nabla f = \sum_{i=1}^n \partial^2 f/\partial x_i^2$
