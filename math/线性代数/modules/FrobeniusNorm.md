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

### Frobenius 内积（补充）
把矩阵空间 $\mathbb{F}^{m\times n}$ 当成一个内积空间时，最常用的就是 Frobenius（Hilbert–Schmidt）内积。

- 元素求和形式（最直观）：
  - 实数域 $\mathbb{F}=\mathbb{R}$：
    $$
    \langle A,B\rangle_F=\sum_{i=1}^m\sum_{j=1}^n a_{ij}b_{ij}=\operatorname{tr}(A^\mathsf{T}B).
    $$
  - 复数域 $\mathbb{F}=\mathbb{C}$（注意共轭）：
    $$
    \langle A,B\rangle_F=\sum_{i=1}^m\sum_{j=1}^n \overline{a_{ij}}\,b_{ij}=\operatorname{tr}(A^*B).
    $$
- 一个你会反复用到的“把双重求和写成 trace”的恒等式（实数情形）：
  - 若 $W,D\in\mathbb{R}^{J\times T}$，则
    $$
    \operatorname{tr}(W^\mathsf{T}D)=\sum_{t=1}^T (W^\mathsf{T}D)_{tt}
    =\sum_{t=1}^T\sum_{j=1}^J W_{jt}D_{jt}
    =\langle W,D\rangle_F.
    $$
  - 复数情形同理：$\operatorname{tr}(W^*D)=\langle W,D\rangle_F$。
- 与向量化的一致性：把矩阵当“长向量”后就是标准内积
  $$
  \langle A,B\rangle_F=\langle \operatorname{vec}(A),\operatorname{vec}(B)\rangle_2.
  $$
- 诱导的概念：
  - 范数：$\lVert A\rVert_F=\sqrt{\langle A,A\rangle_F}$
  - 角度/正交：$\cos\theta=\frac{\langle A,B\rangle_F}{\lVert A\rVert_F\lVert B\rVert_F}$，若 $\langle A,B\rangle_F=0$ 则称 $A\perp B$（矩阵正交）。
- 常用性质（快速用）：
  - 线性/共轭线性、对称性：$\langle A,B\rangle_F=\overline{\langle B,A\rangle_F}$
  - 正定性：$\langle A,A\rangle_F\ge 0$，且等号当且仅当 $A=0$
  - 正交/酉不变性：若 $U,V$ 为尺寸匹配的正交/酉矩阵，则
    $$
    \langle UAV,UBV\rangle_F=\langle A,B\rangle_F,\quad \lVert UAV\rVert_F=\lVert A\rVert_F.
    $$
- 在优化/矩阵微分里常用的“平方范数展开”（把它当作向量点积即可）：
  $$
  \lVert A-B\rVert_F^2=\lVert A\rVert_F^2+\lVert B\rVert_F^2-2\langle A,B\rangle_F.
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
