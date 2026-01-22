# 矩阵的秩（Rank）

## 一句话
矩阵的秩（rank）是其列空间（或行空间）的维数，刻画线性变换能“保留”的独立方向数量。

## 严格定义
设 $A\in\mathbb{R}^{m\times n}$（或 $\mathbb{C}^{m\times n}$），定义
$$
\operatorname{rank}(A)\;=\;\dim(\operatorname{Col}(A))\;=\;\dim(\operatorname{Row}(A)).
$$
其中 $\operatorname{Col}(A)\subseteq \mathbb{F}^m$ 为列空间，$\operatorname{Row}(A)\subseteq \mathbb{F}^n$ 为行空间，$\mathbb{F}\in\{\mathbb{R},\mathbb{C}\}$。

等价刻画（常用）：
- $\operatorname{rank}(A)$ 等于 $A$ 的最大线性无关列（或行）的个数。
- 若 $A=U\Sigma V^\top$ 为 SVD，则 $\operatorname{rank}(A)$ 等于 $\Sigma$ 中非零奇异值的个数。
- $\operatorname{rank}(A)=\dim(\operatorname{Im}(A))$，把 $A$ 看作线性映射 $x\mapsto Ax$ 时的像空间维数。

## 接口：数据 + 约束
- 数据：矩阵 $A\in\mathbb{F}^{m\times n}$。
- 约束：无；但“数值秩（numerical rank）”通常需要额外阈值（见与奇异值衰减相关的定义）。

## 常用构造/操作（仅列接口与符号）
- 列空间/行空间：$\operatorname{Col}(A)$，$\operatorname{Row}(A)$。
- 核空间（零空间）：$\operatorname{Null}(A)=\{x:Ax=0\}$。
- 秩-零化度定理（rank-nullity）：$n=\operatorname{rank}(A)+\dim(\operatorname{Null}(A))$（对 $A:\mathbb{F}^n\to\mathbb{F}^m$）。

## 关系：上级/下级/等价/特例/推广
- 上级：线性映射（把 $A$ 视作线性变换）。
- 等价：秩 $\Leftrightarrow$ 非零奇异值个数（见 [math/线性代数/modules/SVD.md](SVD.md)）。
- 相关：低秩（low rank）与低秩近似（见 [math/线性代数/modules/LowRankApproximation.md](LowRankApproximation.md)）。

## 把新概念挂回框架（多级索引轨迹）
math → 线性代数 → 对象（矩阵/线性映射）→ 子空间（像/核）→ 秩（rank）。

