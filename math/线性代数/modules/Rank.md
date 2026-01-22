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
- 记号：$\ker(A)$、$\operatorname{Im}(A)$ 与 $\operatorname{Null}(A)$ 见 [math/线性代数/modules/Kernel.md](Kernel.md)、[math/线性代数/modules/Image.md](Image.md)、[math/线性代数/modules/NullSpace.md](NullSpace.md)（或汇总页 [math/线性代数/modules/FundamentalSubspaces.md](FundamentalSubspaces.md)）。

## 相关定理及其证明
### 秩-零化度定理（Rank–Nullity Theorem）
设 $A:\mathbb{F}^n\to\mathbb{F}^m$ 为线性映射，则
$$
n=\operatorname{rank}(A)+\dim(\operatorname{Null}(A)).
$$
其中 $\operatorname{rank}(A)=\dim(\operatorname{Im}(A))$，$\operatorname{Null}(A)=\ker(A)$。
记号补充：$\ker(A)$、$\operatorname{Im}(A)$ 与 $\operatorname{Null}(A)$ 的定义见 [math/线性代数/modules/Kernel.md](Kernel.md)、[math/线性代数/modules/Image.md](Image.md)、[math/线性代数/modules/NullSpace.md](NullSpace.md)。

#### 证明结构（分层）
将证明中“像向量线性无关”的关键步骤拆成三层：

1) **结构层：核与补空间的直和分解**  
取 $K=\ker(A)$ 的一组基 $\{v_1,\ldots,v_k\}$ 并扩充为 $V=\mathbb{F}^n$ 的一组基 $\{v_1,\ldots,v_k,v_{k+1},\ldots,v_n\}$。令
$$
W=\operatorname{span}\{v_{k+1},\ldots,v_n\}.
$$
则 $V=K\oplus W$，并且 $K\cap W=\{0\}$（因为 $K$ 与 $W$ 分别由这组基的两部分张成）。直和记号见 [math/线性代数/modules/DirectSum.md](DirectSum.md)。

2) **映射层：限制到补空间后得到单射**  
考虑限制映射 $A|_W:W\to \mathbb{F}^m$。若 $w\in W$ 且 $A(w)=0$，则 $w\in\ker(A)=K$；又 $w\in W$，因此 $w\in K\cap W=\{0\}$，从而 $w=0$。即
$$
\ker(A|_W)=\{0\},
$$
所以 $A|_W$ 是单射（单射定义见 [math/线性代数/modules/InjectiveMap.md](InjectiveMap.md)）。

3) **线性无关层：单射保持线性无关性**  
对线性映射 $T:W\to W'$，若 $T$ 单射，则任意线性无关向量组 $\{w_i\}\subseteq W$ 的像 $\{T(w_i)\}$ 线性无关：若 $\sum_i \alpha_i T(w_i)=0$，则 $T(\sum_i \alpha_i w_i)=0$，由单射得 $\sum_i \alpha_i w_i=0$，再由 $\{w_i\}$ 线性无关得各 $\alpha_i=0$。  
在本证明中，$\{v_{k+1},\ldots,v_n\}$ 是 $W$ 的一组基，因此线性无关；由 $A|_W$ 单射可推出 $\{A(v_{k+1}),\ldots,A(v_n)\}$ 线性无关。

#### 抽象视角（可选）：商空间观点
核空间给出商向量空间 $V/\ker(A)$（见 [math/线性代数/modules/QuotientVectorSpace.md](QuotientVectorSpace.md)）；线性映射 $A$ 诱导出
$$
\tilde{A}:V/\ker(A)\to \operatorname{Im}(A),\qquad [v]\mapsto A(v),
$$
并且该映射是同构（良定义、单射、满射）。在有限维情形下，这将 $\operatorname{Im}(A)$ 的维数与 $V/\ker(A)$ 的维数等同，从而与秩-零化度的维数恒等式一致。

**证明**：令 $K=\ker(A)$，取 $K$ 的一组基 $\{v_1,\ldots,v_k\}$，其中 $k=\dim K$。将其扩充为 $\mathbb{F}^n$ 的一组基
$$
\{v_1,\ldots,v_k,v_{k+1},\ldots,v_n\}.
$$
考虑向量组 $\{A(v_{k+1}),\ldots,A(v_n)\}$。

1) 其张成 $\operatorname{Im}(A)$：对任意 $x\in\mathbb{F}^n$，存在唯一系数 $c_1,\ldots,c_n$ 使
$$
x=\sum_{i=1}^n c_i v_i.
$$
线性性给出
$$
A(x)=\sum_{i=1}^n c_i A(v_i)=\sum_{i=k+1}^n c_i A(v_i),
$$
因为 $i\le k$ 时 $v_i\in\ker(A)$，从而 $A(v_i)=0$。因此任意像向量都被 $\{A(v_{k+1}),\ldots,A(v_n)\}$ 张成。

2) 其线性无关：若
$$
\sum_{i=k+1}^n c_i A(v_i)=0,
$$
则 $A\!\left(\sum_{i=k+1}^n c_i v_i\right)=0$，从而 $\sum_{i=k+1}^n c_i v_i\in\ker(A)$。又因为 $\{v_1,\ldots,v_k\}$ 是 $\ker(A)$ 的基，存在系数 $d_1,\ldots,d_k$ 使
$$
\sum_{i=k+1}^n c_i v_i=\sum_{i=1}^k d_i v_i.
$$
移项得到
$$
\sum_{i=1}^k (-d_i) v_i+\sum_{i=k+1}^n c_i v_i=0.
$$
由于 $\{v_1,\ldots,v_n\}$ 是一组基，故其线性无关，进而 $c_{k+1}=\cdots=c_n=0$。

由 1) 与 2) 可知 $\{A(v_{k+1}),\ldots,A(v_n)\}$ 是 $\operatorname{Im}(A)$ 的一组基，因此
$$
\operatorname{rank}(A)=\dim(\operatorname{Im}(A))=n-k.
$$
整理得 $n=\operatorname{rank}(A)+k=\operatorname{rank}(A)+\dim(\ker(A))=\operatorname{rank}(A)+\dim(\operatorname{Null}(A))$。
证毕。

## 关系：上级/下级/等价/特例/推广
- 上级：线性映射（把 $A$ 视作线性变换）。
- 等价：秩 $\Leftrightarrow$ 非零奇异值个数（见 [math/线性代数/modules/SVD.md](SVD.md)）。
- 相关：低秩（low rank）与低秩近似（见 [math/线性代数/modules/LowRankApproximation.md](LowRankApproximation.md)）。

## 把新概念挂回框架（多级索引轨迹）
math → 线性代数 → 对象（矩阵/线性映射）→ 子空间（像/核）→ 秩（rank）。
