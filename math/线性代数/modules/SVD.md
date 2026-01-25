# 奇异值分解（SVD, Singular Value Decomposition）

## 一句话
奇异值分解把任意矩阵表示为两个正交/酉矩阵与一个非负对角矩阵的乘积，从而把矩阵的“方向变化”和“尺度变化”分离开。

## 严格定义
设 $A\in\mathbb{R}^{m\times n}$，则存在
- 正交矩阵 $U\in\mathbb{R}^{m\times m}$，$V\in\mathbb{R}^{n\times n}$，
- 对角矩阵 $\Sigma\in\mathbb{R}^{m\times n}$（主对角线元素非负，称为奇异值）

使得
$$
A = U\Sigma V^\top.
$$
- 等价UTAV = Σ

复数情形：$A\in\mathbb{C}^{m\times n}$ 时，$U,V$ 为酉矩阵，$V^\top$ 替换为 $V^*$。


记奇异值为 $\sigma_1\ge \sigma_2\ge \cdots \ge 0$（按降序排列），则常用等价关系包括：
- $A^\top A = V(\Sigma^\top \Sigma)V^\top$，$AA^\top = U(\Sigma\Sigma^\top)U^\top$，因此 $\sigma_i^2$ 是 $A^\top A$ 与 $AA^\top$ 的特征值。
- $\operatorname{rank}(A)$ 等于非零奇异值的个数（见 [math/线性代数/modules/Rank.md](Rank.md)）。

## 结构视角（输入/输出基 + 轴向缩放）
**旋转输入坐标系，把坐标轴对齐到“这个变换真正的主伸缩方向”上**，对齐后自然就只剩对角缩放 

把 $A:\mathbb{F}^n\to\mathbb{F}^m$ 看作线性映射，SVD 给出一种“在输入空间与输出空间各选一组正交基，使得 $A$ 在这对基下变成对角形”的表示。

设 $V=[v_1,\ldots,v_n]$，$U=[u_1,\ldots,u_m]$ 分别为右/左奇异向量组成的正交（酉）矩阵，则对每个奇异三元组 $(u_i,\sigma_i,v_i)$ 有
$$
A v_i = \sigma_i u_i.
$$
可将 $A=U\Sigma V^\top$ 理解为三个步骤的复合：
$$
\mathbb{F}^n \xrightarrow{\,V^\top\,} \mathbb{F}^n \xrightarrow{\,\Sigma\,} \mathbb{F}^m \xrightarrow{\,U\,} \mathbb{F}^m,
$$
其中 $V^\top$ 与 $U$ 是正交变换（改变坐标/旋转反射），$\Sigma$ 在一组两边对齐的轴上做非负缩放（$\sigma_i$ 作为缩放系数）。

等价的几何表述（以 $\lVert\cdot\rVert_2$ 为例）：单位球在 $A$ 作用下变成椭球，其主轴方向为 $u_i$，对应半轴长度为 $\sigma_i$。

与“压缩/低秩”的连接：若奇异值序列在某个 $k$ 后显著变小，则 $A$ 在除 $\operatorname{span}\{v_1,\ldots,v_k\}$ 以外的输入方向上尺度很小；截断 SVD 用前 $k$ 个方向构造秩不超过 $k$ 的近似（见 [math/线性代数/modules/LowRankApproximation.md](LowRankApproximation.md)）。

## 接口：数据 + 约束
- 数据：矩阵 $A\in\mathbb{F}^{m\times n}$。
- 输出：$U,\Sigma,V$ 满足 $A=U\Sigma V^\top$（或 $V^*$）。
- 约束：奇异值非负；$U,V$ 为正交/酉。

## 常用构造/操作（仅列接口与符号）
- 经济型（thin/economy）SVD：$A=U_r\Sigma_r V_r^\top$，其中 $U_r\in\mathbb{F}^{m\times r}$，$V_r\in\mathbb{F}^{n\times r}$，$\Sigma_r\in\mathbb{R}^{r\times r}$，$r=\operatorname{rank}(A)$。
- 截断 SVD（truncated SVD）：选定 $k<r$，用前 $k$ 个奇异值/奇异向量构造近似 $A_k=U_k\Sigma_kV_k^\top$（见 [math/线性代数/modules/LowRankApproximation.md](LowRankApproximation.md)）。
- 谱范数与 Frobenius 范数：$\lVert A\rVert_2=\sigma_1$，$\lVert A\rVert_F^2=\sum_i \sigma_i^2$（Frobenius 见 [math/线性代数/modules/FrobeniusNorm.md](FrobeniusNorm.md)）。

## 关系：上级/下级/等价/特例/推广
- 上级：矩阵分解（decomposition）。
- 相关：特征分解/谱定理（对称矩阵）、最小二乘（用 SVD 解释病态与正则化）、PCA（数据矩阵中心化后的 SVD）。
- 特例：对称正定矩阵的谱分解可视为 SVD 的特化情形。

## 把新概念挂回框架（多级索引轨迹）
math → 线性代数 → 工具（矩阵分解）→ SVD → 截断 SVD → 低秩近似。
