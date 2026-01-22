# 核空间（Kernel, `ker(A)`）

## 一句话
线性映射 $A:V\to W$ 的核空间（kernel）是所有被映射到零向量的输入所组成的集合。

## 严格定义
设 $\mathbb{F}\in\{\mathbb{R},\mathbb{C}\}$，$V,W$ 为 $\mathbb{F}$-向量空间，$A:V\to W$ 为线性映射，则
$$
\ker(A)\;=\;\{v\in V:\ A(v)=0\}.
$$

## 接口：数据 + 约束
- 数据：线性映射 $A:V\to W$。
- 输出：集合 $\ker(A)\subseteq V$。
- 约束：$A$ 为线性映射（从而 $\ker(A)$ 是 $V$ 的线性子空间）。

## 常用构造/操作（仅列出接口与符号）
- 矩阵情形：若 $A$ 由矩阵 $A\in\mathbb{F}^{m\times n}$ 表示（$A:\mathbb{F}^n\to\mathbb{F}^m$），则
$$
\ker(A)=\operatorname{Null}(A)=\{x\in\mathbb{F}^n:\ Ax=0\},
$$
见 [math/线性代数/modules/NullSpace.md](NullSpace.md)。
- 维数：$\dim(\ker(A))$（也称 nullity）。

## 关系：上级/下级/等价/特例/推广
- 上级：线性映射、子空间。
- 等价（矩阵视角）：$\ker(A)=\operatorname{Null}(A)$，见 [math/线性代数/modules/NullSpace.md](NullSpace.md)。
- 相关：线性映射的基本子空间（见 [math/线性代数/modules/FundamentalSubspaces.md](FundamentalSubspaces.md)）。

## 把新概念挂回框架（多级索引轨迹）
math → 线性代数 → 线性映射 → 核空间（kernel）。

