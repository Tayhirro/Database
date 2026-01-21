# 向量场（Vector Field）

## 1. 一句话
- 向量场就是 $v:\Omega \to \mathbb{R}^n$（把定义域解释成空间位置的向量值函数）。

## 2. 严格定义（欧氏空间口径）
设 $\Omega \subseteq \mathbb{R}^n$，向量场是映射
$$
v:\Omega \to \mathbb{R}^n,\qquad x\mapsto v(x).
$$

## 3. 严格定义（流形口径：切丛截面）
若 $M$ 是光滑流形，记其切丛为 $\pi:TM\to M$。一个（光滑）向量场是一个（光滑）截面
$$
X:M\to TM,\qquad \pi\circ X=\mathrm{id}_M,
$$
即对每个 $p\in M$，有 $X(p)\in T_pM$。

## 4. 接口：数据 + 约束（像类型签名）
- 定义域：$\Omega \subseteq \mathbb{R}^n$
- 值域：$\mathbb{R}^n$
- 分量表示：$v(x) = (v_1(x),...,v_n(x))$
- 正则性（常用）：
  - 连续：$v \in C(\Omega;\mathbb{R}^n)$
  - 可微：$v \in C^1(\Omega;\mathbb{R}^n)$

## 5. 例子
- 风场/速度场：$v(x,y) = (v_x(x,y), v_y(x,y))$
- 梯度场：$v = \nabla f$

## 6. 常用操作（向量分析）
- Jacobian：$J_v(x) = [\partial v_i/\partial x_j]$
- 散度：$\nabla\cdot v = \sum_i \partial v_i/\partial x_i$
- 旋度：$\nabla\times v$
- 入口：见 [math/微积分/modules/VectorOperators.md](../../modules/VectorOperators.md)

## 7. 与主框架的关系
- 作为向量值函数对象：见 [math/微积分/structures/functions/VectorValuedFunction.md](../functions/VectorValuedFunction.md)
