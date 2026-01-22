# 低秩近似（Low-Rank Approximation）与截断 SVD

## 一句话
低秩近似是在“秩不超过 $k$”的约束下用一个矩阵 $A_k$ 去逼近原矩阵 $A$，而截断 SVD 给出在常见范数下的最优解。

## 严格定义
给定 $A\in\mathbb{F}^{m\times n}$ 与整数 $k\ge 0$，低秩近似问题通常指：
$$
\min_{B\in\mathbb{F}^{m\times n}}\ \lVert A-B\rVert\quad \text{s.t.}\ \operatorname{rank}(B)\le k,
$$
其中 $\lVert\cdot\rVert$ 是选定的矩阵范数（常见为谱范数 $\lVert\cdot\rVert_2$ 或 Frobenius 范数 $\lVert\cdot\rVert_F$）。

设 $A$ 的 SVD 为 $A=U\Sigma V^\top$，奇异值按 $\sigma_1\ge\sigma_2\ge\cdots\ge 0$ 排列。定义截断 SVD：
$$
A_k = U_k\Sigma_k V_k^\top,
$$
其中 $U_k,V_k$ 取前 $k$ 列奇异向量，$\Sigma_k=\operatorname{diag}(\sigma_1,\ldots,\sigma_k)$。

Eckart–Young–Mirsky 定理（最优性）：
- 在 Frobenius 范数下，$A_k$ 是所有秩不超过 $k$ 的矩阵中使 $\lVert A-B\rVert_F$ 最小的解。
- 在谱范数下，$A_k$ 也是最优解之一。

对应的误差表达（常用）：
$$
\lVert A-A_k\rVert_2=\sigma_{k+1},\qquad
\lVert A-A_k\rVert_F^2=\sum_{i>k}\sigma_i^2.
$$

## 接口：数据 + 约束
- 数据：矩阵 $A\in\mathbb{F}^{m\times n}$；目标秩 $k\in\{0,1,\ldots,\operatorname{rank}(A)\}$；范数选择（$\lVert\cdot\rVert_2$ 或 $\lVert\cdot\rVert_F$ 常见）。
- 约束：$\operatorname{rank}(A_k)\le k$；若用截断 SVD 构造，则由 [math/线性代数/modules/SVD.md](SVD.md) 的 $U,\Sigma,V$ 给出。

## 常用构造/操作（仅列接口与符号）
- 截断 SVD：$A_k=U_k\Sigma_kV_k^\top$。
- 因子分解形式（显示低秩）：$A_k=(U_k\Sigma_k^{1/2})(\Sigma_k^{1/2}V_k^\top)=PQ^\top$，其中 $P\in\mathbb{F}^{m\times k}$，$Q\in\mathbb{F}^{n\times k}$。
- 能量/方差保留（以 Frobenius 范数计）：$\frac{\sum_{i\le k}\sigma_i^2}{\sum_i\sigma_i^2}$（常用于解释“只保留前 $k$ 个主成分/方向”的信息占比）。

## 关系：上级/下级/等价/特例/推广
- 上级：矩阵范数、优化（带秩约束的逼近问题）。
- 等价/实现：截断 SVD（见 [math/线性代数/modules/SVD.md](SVD.md)）。
- 相关：矩阵秩（见 [math/线性代数/modules/Rank.md](Rank.md)）、PCA、矩阵补全（matrix completion）、低秩正则化（如核范数）。

## 把新概念挂回框架（多级索引轨迹）
math → 线性代数 → 工具（矩阵分解）→ SVD → 截断 SVD → 低秩近似（Eckart–Young–Mirsky）。

