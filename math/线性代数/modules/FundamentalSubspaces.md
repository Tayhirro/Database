# 线性映射的基本子空间（Fundamental Subspaces）

## 一句话
线性映射（或矩阵）对应的一组典型子空间可用于统一描述“可达输出”“不可区分输入”等结构。

## 严格定义
设 $\mathbb{F}\in\{\mathbb{R},\mathbb{C}\}$，$A:\mathbb{F}^n\to\mathbb{F}^m$ 为线性映射（由矩阵 $A\in\mathbb{F}^{m\times n}$ 表示）。与 $A$ 相关的子空间包括：
- 核空间：$\ker(A)\subseteq \mathbb{F}^n$（见 [math/线性代数/modules/Kernel.md](Kernel.md)）。
- 像空间：$\operatorname{Im}(A)\subseteq \mathbb{F}^m$（见 [math/线性代数/modules/Image.md](Image.md)）。
- 行空间：$\operatorname{Row}(A)\subseteq \mathbb{F}^n$。
- 列空间：$\operatorname{Col}(A)\subseteq \mathbb{F}^m$，且 $\operatorname{Col}(A)=\operatorname{Im}(A)$（见 [math/线性代数/modules/Image.md](Image.md)）。
- 左零空间：$\operatorname{Null}(A^\top)\subseteq \mathbb{F}^m$（或复数情形的 $\operatorname{Null}(A^*)$）。

## 接口：数据 + 约束
- 数据：线性映射 $A:\mathbb{F}^n\to\mathbb{F}^m$（或矩阵 $A\in\mathbb{F}^{m\times n}$）。
- 输出：若干与 $A$ 相关的子空间（见“严格定义”列表）。
- 约束：$A$ 为线性映射。

## 常用构造/操作（仅列出接口与符号）
- 维数关系：$\operatorname{rank}(A)=\dim(\operatorname{Im}(A))$（见 [math/线性代数/modules/Rank.md](Rank.md)），并满足秩-零化度定理（见 [math/线性代数/modules/Rank.md](Rank.md)）。

## 关系：上级/下级/等价/特例/推广
- 上级：子空间、线性映射。
- 下级：核空间/像空间/零空间（见 [math/线性代数/modules/Kernel.md](Kernel.md)、[math/线性代数/modules/Image.md](Image.md)、[math/线性代数/modules/NullSpace.md](NullSpace.md)）。
- 相关：线性方程组与消元（用于显式求解这些子空间的基）。

## 把新概念挂回框架（多级索引轨迹）
math → 线性代数 → 线性映射 → 基本子空间（fundamental subspaces）。

