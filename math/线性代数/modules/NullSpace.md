# 零空间（Null Space, `Null(A)`）

## 一句话
矩阵 $A$ 的零空间（null space）是齐次线性方程组 $Ax=0$ 的解空间。

## 严格定义
设 $\mathbb{F}\in\{\mathbb{R},\mathbb{C}\}$，$A\in\mathbb{F}^{m\times n}$，将 $A$ 视作线性映射 $A:\mathbb{F}^n\to\mathbb{F}^m,\ x\mapsto Ax$，定义
$$
\operatorname{Null}(A)\;=\;\{x\in\mathbb{F}^n:\ Ax=0\}.
$$
在该约定下 $\operatorname{Null}(A)=\ker(A)$，见 [math/线性代数/modules/Kernel.md](Kernel.md)。

## 接口：数据 + 约束
- 数据：矩阵 $A\in\mathbb{F}^{m\times n}$。
- 输出：集合 $\operatorname{Null}(A)\subseteq \mathbb{F}^n$。
- 约束：无；将矩阵解释为线性映射时，$\operatorname{Null}(A)$ 是 $\mathbb{F}^n$ 的线性子空间。

## 常用构造/操作（仅列出接口与符号）
- 求一组基：对 $Ax=0$ 做消元得到一般解，从而得到 $\operatorname{Null}(A)$ 的一组基。
- 维数：$\dim(\operatorname{Null}(A))$（也称 nullity）。

## 关系：上级/下级/等价/特例/推广
- 上级：线性方程组（齐次）、线性映射、子空间。
- 等价：$\operatorname{Null}(A)=\ker(A)$（矩阵视作线性映射）。
- 相关：像空间 $\operatorname{Im}(A)$（见 [math/线性代数/modules/Image.md](Image.md)）、秩与秩-零化度定理（见 [math/线性代数/modules/Rank.md](Rank.md)）、线性映射的基本子空间（见 [math/线性代数/modules/FundamentalSubspaces.md](FundamentalSubspaces.md)）。

## 把新概念挂回框架（多级索引轨迹）
math → 线性代数 → 矩阵/线性方程组 → 零空间（null space）。

