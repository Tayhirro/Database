# 标量场（Scalar Field）

## 1. 一句话
- 标量场就是 $f:\Omega \to \mathbb{R}$（把定义域解释成空间位置的标量函数）。

## 2. 接口：数据 + 约束（像类型签名）
- 定义域（空间/区域）：$\Omega \subseteq \mathbb{R}^n$
- 值域：$\mathbb{R}$（或 $\mathbb{C}$）
- 正则性（可选但常用）：
  - 连续：$f \in C(\Omega)$
  - 可微：$f \in C^1(\Omega)$
  - 二阶可微：$f \in C^2(\Omega)$

## 3. 例子
- 温度场：$T(x,y) \in \mathbb{R}$
- 密度场/势能场：$\rho(x)$、$U(x)$

## 4. 常用操作（向量分析）
- 梯度：$\nabla f$
- 拉普拉斯：$\Delta f = \nabla\cdot\nabla f$
- 入口：见 [math/微积分/modules/VectorOperators.md](../../modules/VectorOperators.md)

## 5. 与主框架的关系
- 作为函数对象：见 [math/微积分/structures/functions/ScalarFunction.md](../functions/ScalarFunction.md)
