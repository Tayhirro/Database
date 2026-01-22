# 单射（Injective / One-to-One）

## 一句话
单射表示“不同输入不会映到同一输出”，在线性映射中等价于核空间为零。

## 严格定义
设 $f:X\to Y$ 为函数。称 $f$ 为单射（injective），若
$$
f(x_1)=f(x_2)\ \Rightarrow\ x_1=x_2.
$$

线性映射情形：设 $A:V\to W$ 为线性映射，则 $A$ 单射当且仅当
$$
\ker(A)=\{0\}.
$$

## 接口：数据 + 约束
- 数据：函数 $f:X\to Y$（或线性映射 $A:V\to W$）。
- 输出：性质判定（是否单射）。
- 约束：线性情形使用 $\ker(A)$（见 [math/线性代数/modules/Kernel.md](Kernel.md)）。

## 常用构造/操作（仅列接口与符号）
- 单射的逆像性质：对任意 $y\in Y$，原像 $f^{-1}(\{y\})$ 至多包含一个元素。
- 线性情形的等价刻画（有限维）：
  - $A$ 单射 $\Leftrightarrow \dim(\ker(A))=0$。
  - 若 $A$ 表示为矩阵 $A\in\mathbb{F}^{m\times n}$，则 $A$ 单射 $\Leftrightarrow Ax=0$ 仅有平凡解。

## 关系：上级/下级/等价/特例/推广
- 上级：函数、线性映射。
- 等价（线性）：单射 $\Leftrightarrow \ker(A)=\{0\}$。
- 相关：像空间（见 [math/线性代数/modules/Image.md](Image.md)）、秩与秩-零化度定理（见 [math/线性代数/modules/Rank.md](Rank.md)）。

## 把新概念挂回框架（多级索引轨迹）
math → 线性代数 → 线性映射 → 单射（injective）。

