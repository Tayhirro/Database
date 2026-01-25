# Frobenius 范数（Frobenius norm, $\lVert\cdot\rVert_F$）

## 一句话
Frobenius 范数是矩阵空间上的范数，等于把矩阵当作一个长向量后取欧几里得范数（也称 Hilbert–Schmidt 范数）。

## 严格定义
设 $\mathbb{F}\in\{\mathbb{R},\mathbb{C}\}$，$A=[a_{ij}]\in\mathbb{F}^{m\times n}$，定义
$$
\lVert A\rVert_F \;:=\;\left(\sum_{i=1}^m\sum_{j=1}^n |a_{ij}|^2\right)^{1/2}.
$$

等价定义：
- 迹形式：
$$
\lVert A\rVert_F = \sqrt{\operatorname{tr}(A^*A)}.
$$
- SVD 形式：若 $A=U\Sigma V^*$，奇异值为 $\{\sigma_k\}$，则
$$
\lVert A\rVert_F^2=\sum_k \sigma_k^2.
$$
- 向量化形式：令 $\operatorname{vec}(A)$ 为按某种固定顺序堆叠矩阵元素得到的向量，则
$$
\lVert A\rVert_F=\lVert \operatorname{vec}(A)\rVert_2.
$$

对应的 Frobenius 内积（Hilbert–Schmidt 内积）：
$$
\langle A,B\rangle_F := \operatorname{tr}(A^*B),
\qquad \lVert A\rVert_F=\sqrt{\langle A,A\rangle_F}.
$$

## 接口：数据 + 约束
- 数据：矩阵 $A\in\mathbb{F}^{m\times n}$。
- 输出：实数 $\lVert A\rVert_F\in\mathbb{R}_{\ge 0}$。
- 约束：无。

## 常用构造/操作（仅列出接口与符号）
- 不变性：对任意正交/酉矩阵 $U,V$（尺寸匹配），有 $\lVert UAV\rVert_F=\lVert A\rVert_F$。
- 与谱范数的关系：$\lVert A\rVert_2 \le \lVert A\rVert_F \le \sqrt{\operatorname{rank}(A)}\,\lVert A\rVert_2$。
- 截断 SVD 的误差表达（Frobenius 口径）：见 [math/线性代数/modules/LowRankApproximation.md](LowRankApproximation.md)。

## 关系：上级/下级/等价/特例/推广
- 上级：矩阵范数（matrix norm）、内积诱导范数。
- 等价：Frobenius 范数 $\Leftrightarrow$ 奇异值的 $\ell^2$ 范数（见 [math/线性代数/modules/SVD.md](SVD.md)）。
- 相关：低秩近似（在 $\lVert\cdot\rVert_F$ 下的最优性见 [math/线性代数/modules/LowRankApproximation.md](LowRankApproximation.md)）。

## 把新概念挂回框架（多级索引轨迹）
math → 线性代数 →（矩阵/线性算子）→ 矩阵范数 → Frobenius 范数（$\lVert\cdot\rVert_F$）。

