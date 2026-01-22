# 核空间/像空间/零空间（Kernel / Image / Null Space）

## 一句话
给定线性映射 $A:V\to W$，核空间（kernel）是被映射到 $0$ 的向量集合，像空间（image）是所有输出的集合；当 $A$ 用矩阵表示时，核空间也称零空间（null space）。

## 严格定义
设 $\mathbb{F}\in\{\mathbb{R},\mathbb{C}\}$，$V,W$ 为 $\mathbb{F}$-向量空间，$A:V\to W$ 为线性映射。

- 核空间（kernel / null space）：
$$
\ker(A)\;=\;\{v\in V:\ A(v)=0\}.
$$
- 像空间（image / range）：
$$
\operatorname{Im}(A)\;=\;\{A(v):\ v\in V\}\subseteq W.
$$

若 $A$ 在标准基下由矩阵 $A\in\mathbb{F}^{m\times n}$ 表示（即 $A:\mathbb{F}^n\to\mathbb{F}^m,\ x\mapsto Ax$），则：
- 零空间（null space）：
$$
\operatorname{Null}(A)\;=\;\{x\in\mathbb{F}^n:\ Ax=0\}.
$$
并且在此约定下 $\operatorname{Null}(A)=\ker(A)$。

## 接口：数据 + 约束
- 数据：线性映射 $A:V\to W$ 或矩阵 $A\in\mathbb{F}^{m\times n}$。
- 输出：$\ker(A)\subseteq V$，$\operatorname{Im}(A)\subseteq W$；矩阵情形还可给出 $\operatorname{Null}(A)\subseteq \mathbb{F}^n$。
- 约束：$A$ 为线性映射（从而核/像是线性子空间）。

## 常用构造/操作（仅列出接口与符号）
- 核空间（矩阵情形）：解齐次方程 $Ax=0$ 得到 $\operatorname{Null}(A)$ 的一组基。
- 像空间（矩阵情形）：列空间
$$
\operatorname{Col}(A)=\operatorname{span}\{a_1,\ldots,a_n\}\subseteq \mathbb{F}^m,
$$
其中 $a_i$ 是 $A$ 的第 $i$ 列；并且 $\operatorname{Im}(A)=\operatorname{Col}(A)$。
- 维数（与秩相关）：$\operatorname{rank}(A)=\dim(\operatorname{Im}(A))$，$\dim(\ker(A))=\dim(\operatorname{Null}(A))$。

## 关系：上级/下级/等价/特例/推广
- 上级：线性映射、子空间。
- 等价：$\ker(A)=\operatorname{Null}(A)$（矩阵视作线性映射）。
- 相关：秩（见 [math/线性代数/modules/Rank.md](Rank.md)）、秩-零化度定理（见 [math/线性代数/modules/Rank.md](Rank.md)）。

## 把新概念挂回框架（多级索引轨迹）
math → 线性代数 → 线性映射 → 核空间/像空间（ker/im）→ 秩/秩-零化度定理。

