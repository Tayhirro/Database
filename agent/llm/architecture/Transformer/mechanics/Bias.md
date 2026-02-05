---
aliases: [Bias, 偏置, 截距, Intercept]
tags: [affine, linear-map, linear-layer, transformer, attention]
---

# Bias（偏置/截距）

## 一句话
Bias（偏置/截距）是仿射映射 $x \mapsto Wx+b$ 中的加性参数，用于在共享权重 $W$ 的前提下对输出进行平移（translation）。

## 严格定义

### 线性映射 vs 仿射映射
- 线性映射（linear map）：$f(x)=Wx$，必要条件是 $f(0)=0$。
- 仿射映射（affine map）：$g(x)=Wx+b$，满足 $g(0)=b$；$b$ 对应整体平移项。

以集合像（image）的语言表述：对任意集合 $S$，
- $W(S)$ 的像必过原点；
- $W(S)+b$ 的像是 $W(S)$ 的平移，不要求过原点。

### “共享同一个 $W$”时的可实现集合差异（批量/多样本视角）
令一批输入堆叠为矩阵 $X\in\mathbb{R}^{n\times d}$，输出为 $Y\in\mathbb{R}^{n\times m}$。

**无偏置（线性层）**：
$$
Y=XW
$$
对任意输出列 $y_k$（$Y$ 的第 $k$ 列），有 $y_k=Xw_k$，因此
$$
y_k \in \mathrm{col}(X)
$$

**有偏置（仿射层）**（按样本维广播）：
$$
Y=XW+\mathbf{1}b^\top
$$
其中 $\mathbf{1}\in\mathbb{R}^n$ 为全 1 向量。对任意输出列 $y_k$，
$$
y_k=Xw_k+b_k\mathbf{1}\;\;\Rightarrow\;\; y_k \in \mathrm{span}(\mathrm{col}(X),\mathbf{1})
$$
等价地，$y_k\in \mathrm{col}([X\;\;\mathbf{1}])$（在 $X$ 的列上拼接常量列）。

以上给出“bias 提供额外平移自由度”的一种矩阵化刻画：当 $\mathbf{1}\notin\mathrm{col}(X)$ 时，允许的输出集合在样本维方向上增加了独立的“常量方向”。

## 接口：数据 + 约束

| 项 | 形式 | 约束 | 说明 |
|---|---|---|---|
| 输入 | $x\in\mathbb{R}^d$ 或 $X\in\mathbb{R}^{n\times d}$ | 实数向量/矩阵 | 以最后一维为特征维 |
| 权重 | $W\in\mathbb{R}^{d\times m}$ | 共享参数 | 线性部分 |
| 偏置 | $b\in\mathbb{R}^{m}$ | 共享参数 | 平移项；按样本/位置广播 |
| 输出 | $y\in\mathbb{R}^m$ 或 $Y\in\mathbb{R}^{n\times m}$ | 实数向量/矩阵 | 仿射输出 |

## 常用构造/操作（仅列出接口与符号）
- 线性层（无 bias）：$y = Wx$
- 仿射层（带 bias）：$y = Wx + b$
- 批量/序列广播（概念级）：对 $X\in\mathbb{R}^{B\times L\times d}$，$b\in\mathbb{R}^{m}$ 可按 $(B,L)$ 维广播到 $\mathbb{R}^{B\times L\times m}$。

### 在 Transformer 投影层中的出现方式（概念级）
对输入特征矩阵 $X$（按 token 维堆叠），常见投影写法为：
$$
Q=XW_Q+\mathbf{1}b_Q^\top,\quad K=XW_K+\mathbf{1}b_K^\top,\quad V=XW_V+\mathbf{1}b_V^\top
$$
其中 $b_Q,b_K,b_V$ 为投影层的偏置向量（是否存在依实现/配置）。

## 关系
- 上级：仿射映射（由线性映射加平移项构成）。
- 对比：线性映射（无偏置）与仿射映射（带偏置）。
- 相关：[LayerNorm](LayerNorm.md)（包含平移参数 $\beta$）；
- 相关：[RMSNorm](RMSNorm.md)（无平移参数 $\beta$）。

## 挂载路径
LLM → Architecture → Transformer → mechanics → Bias

