# 向量场（Vector Field）

## 1. 一句话
- 向量场就是 $v:\Omega \to \mathbb{R}^n$（把定义域解释成空间位置的向量值函数）。

## 2. 接口：数据 + 约束（像类型签名）
- 定义域：$\Omega \subseteq \mathbb{R}^n$
- 值域：$\mathbb{R}^n$
- 分量表示：$v(x) = (v_1(x),...,v_n(x))$
- 正则性（常用）：
  - 连续：$v \in C(\Omega;\mathbb{R}^n)$
  - 可微：$v \in C^1(\Omega;\mathbb{R}^n)$

## 3. 例子
- 风场/速度场：$v(x,y) = (v_x(x,y), v_y(x,y))$
- 梯度场：$v = \nabla f$

## 4. 常用操作（向量分析）
- Jacobian：$J_v(x) = [\partial v_i/\partial x_j]$
- 散度：$\nabla\cdot v = \sum_i \partial v_i/\partial x_i$
- 旋度：$\nabla\times v$
- 入口：见 [math/微积分/modules/VectorOperators.md](../../modules/VectorOperators.md)

## 5. 与主框架的关系
- 作为向量值函数对象：见 [math/微积分/structures/functions/VectorValuedFunction.md](../functions/VectorValuedFunction.md)
