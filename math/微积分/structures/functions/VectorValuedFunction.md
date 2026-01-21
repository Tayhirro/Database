# 向量值函数（Vector-Valued Function）

## 1. 一句话
- 向量值函数就是 $g:D \to \mathbb{R}^m$。

## 2. 严格定义
向量值函数是映射 $g:D\to \mathbb{R}^m$。等价地，它由 $m$ 个标量函数组成：
$$
g(x)=(g_1(x),\dots,g_m(x)),\qquad g_i:D\to \mathbb{R}.
$$

## 3. 接口：数据 + 约束（像类型签名）
- 定义域：$D \subseteq \mathbb{R}^n$
- 值域：$\mathbb{R}^m$
- 分量表示：$g(x) = (g_1(x),...,g_m(x))$
- 正则性（常用）：
  - 连续：$g \in C(D;\mathbb{R}^m)$
  - 可微：$g \in C^1(D;\mathbb{R}^m)$（能谈 Jacobian）

## 4. 例子
- $g(x)=(x, x^2)$（$D=\mathbb{R}$，输出在 $\mathbb{R}^2$）
- $g(x,y)=(x+y, xy)$（$D=\mathbb{R}^2$，输出在 $\mathbb{R}^2$）
- 参数曲线：$\gamma: I \to \mathbb{R}^n$ 是 `n=1` 的特例，见 [math/微积分/structures/geometry/ParametricCurve.md](../geometry/ParametricCurve.md)

## 5. 最常用导数对象
- Jacobian：$J_g(x) = [\partial g_i/\partial x_j]$
- 入口：见 [math/微积分/modules/Differentiation.md](../../modules/Differentiation.md)

## 6. 可微的严格口径（Fréchet 导数）
若 $D$ 是 $\mathbb{R}^n$ 的开子集，称 $g$ 在 $x$ 处可微，若存在一个线性映射 $A:\mathbb{R}^n\to \mathbb{R}^m$ 使得
$$
\lim_{h\to 0}\frac{\|g(x+h)-g(x)-Ah\|}{\|h\|}=0.
$$
此时 $A$ 记为 $Dg(x)$；在标准基下它对应的矩阵就是 Jacobian $J_g(x)$。
