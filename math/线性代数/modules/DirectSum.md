# 直和（Direct Sum, $\oplus$）

## 一句话
向量空间的直和描述“两个子空间只在零向量处相交且共同张成整个空间”，从而每个向量都有唯一的分解。

## 严格定义
设 $\mathbb{F}\in\{\mathbb{R},\mathbb{C}\}$，$V$ 为 $\mathbb{F}$-向量空间，$U,W\le V$ 为子空间。

称 $V$ 是 $U$ 与 $W$ 的（内部）直和，记作
$$
V = U \oplus W,
$$
若满足
$$
V = U + W \quad \text{且}\quad U\cap W=\{0\}.
$$

等价表述（唯一分解）：
对任意 $v\in V$，存在唯一一对 $(u,w)\in U\times W$ 使 $v=u+w$。

更一般地，若 $U_1,\ldots,U_k\le V$，则
$$
V=\bigoplus_{i=1}^k U_i
$$
表示任意 $v\in V$ 都可唯一写为 $v=\sum_{i=1}^k u_i$，其中 $u_i\in U_i$。

## 接口：数据 + 约束
- 数据：向量空间 $V$；子空间 $U,W\le V$（或有限多个子空间）。
- 约束：内部直和的判定条件是 $V=U+W$ 且 $U\cap W=\{0\}$。

## 常用构造/操作（仅列接口与符号）
- 补空间（complement）：给定子空间 $U\le V$，若存在 $W\le V$ 使 $V=U\oplus W$，则称 $W$ 是 $U$ 的一个补空间。
- 维数（有限维）：若 $V=U\oplus W$ 且维数有限，则
$$
\dim V = \dim U + \dim W.
$$
- 通过“取子空间基并扩充为全空间基”构造补空间：若 $\{u_1,\ldots,u_k\}$ 为 $U$ 的一组基，扩充为 $V$ 的一组基 $\{u_1,\ldots,u_k,w_{k+1},\ldots,w_n\}$，则 $W=\operatorname{span}\{w_{k+1},\ldots,w_n\}$ 满足 $V=U\oplus W$。

## 关系：上级/下级/等价/特例/推广
- 上级：子空间、向量空间。
- 等价：直和 $\Leftrightarrow$ 唯一分解。
- 相关：核空间与补空间分解（见 [math/线性代数/modules/Rank.md](Rank.md) 的秩-零化度证明结构）、商向量空间（见 [math/线性代数/modules/QuotientVectorSpace.md](QuotientVectorSpace.md)）。

## 把新概念挂回框架（多级索引轨迹）
math → 线性代数 → 向量空间/子空间 → 直和（direct sum）。

