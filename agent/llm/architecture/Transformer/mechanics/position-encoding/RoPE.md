---
title: RoPE
date: "2026-02-10"
categories:
  - agent
tags: "[transformer, position-encoding, relative-position, orthogonal-transformation]"
description: 通过正交旋转矩阵对词向量进行位置相关的旋转变换，使得 Query 与 Key 的点积结果仅依赖于相对位置距离  的乘性位置编码方法。
aliases: "[旋转位置编码, Rotary Position Embedding]"
---
# RoPE

## 一句话

通过正交旋转矩阵对词向量进行位置相关的旋转变换，使得 Query 与 Key 的点积结果仅依赖于相对位置距离 $(n-m)$ 的乘性位置编码方法。

## 严格定义

### 二维情形（基础单元）
设词向量在维度 $(2i, 2i+1)$ 上的分量为 $x \in \mathbb{R}^2$，位于序列位置 $m$，则 RoPE 变换为：
$$
x' = R(m\theta_i) x
$$
其中 $\theta_i = 10000^{-2i/d}$ 为第 $i$ 组的基础旋转单位，$R(\alpha)$ 为二维旋转矩阵：
$$
R(\alpha) = \begin{pmatrix} \cos\alpha & -\sin\alpha \\ \sin\alpha & \cos\alpha \end{pmatrix} \in SO(2)
$$

### 高维分块对角形式
对 $d$ 维向量，RoPE 为分块对角矩阵 $R_m \in \mathbb{R}^{d \times d}$：
$$
R_m = \text{diag}\left(R(m\theta_1), R(m\theta_2), \dots, R(m\theta_{d/2})\right)
$$

### 关键性质：相对位置内蕴性
在注意力计算中，位置 $m$ 的 Query 与位置 $n$ 的 Key 的交互为：
$$
\begin{aligned}
\text{Score} &= (R_m q)^T (R_n k) \\
&= q^T R_m^T R_n k \\
&= q^T R(n-m) k \quad \text{（利用 } R(\alpha)^T = R(-\alpha) \text{ 及和差角公式）}
\end{aligned}
$$
**必要条件**：最终注意力分数仅通过旋转矩阵 $R(n-m)$ 依赖于相对距离 $(n-m)$，与绝对位置 $m, n$ 无关。

## 接口：数据 + 约束

| 符号 | 类型 | 约束/定义域 | 备注 |
|------|------|-------------|------|
| $d$ | $\mathbb{N}^+$ | 偶数 | 模型维度，需为2的倍数以分组 |
| $i$ | $\mathbb{N}$ | $[0, d/2)$ | 维度组索引 |
| $\theta_i$ | $\mathbb{R}$ | $10000^{-2i/d}$ | 维度相关旋转基频，等比递减 |
| $R(\alpha)$ | $SO(2)$ | $R^T R = I, \det(R)=1$ | 正交矩阵，保证 $\|Rx\| = \|x\|$（模长保持）|
| $m, n$ | $\mathbb{N}$ | 序列位置 | 绝对索引，不出现在最终分数中 |

## 常用构造/操作

| 操作 | 接口/符号 | 说明 |
|------|-----------|------|
| 分组旋转 | $[x_{2i}, x_{2i+1}] \cdot R(m\theta_i)^T$ | 相邻两维构成复数平面上的旋转 |
| 复数高效实现 | $z' = z \cdot e^{i m \theta_i}$ | 将二维向量视为复数，避免显式矩阵乘法 |
| 外推调整 | 修改基数 $10000 \to B$ | 调整 $B$ 改变旋转速度，影响长序列外推能力 |
| 与正弦编码关系 | 正弦编码可视为 RoPE 在特定初始化下的线性近似 | [[正弦位置编码#与 RoPE 的数学联系]] |

## 关系

- **上级**: [[位置编码]]
- **对比/上级**: [[正弦位置编码]] —— 加法引入交叉噪声（$X_mP_n^T$ 项），RoPE 通过乘法消除
- **数学基础**: [[旋转矩阵]] —— $SO(2)$ 群的正交性保证模长不变
- **等价形式**: [[相对位置编码]] —— RoPE 实现了无需显式计算相对位置向量的相对编码
- **应用实例**: [[LLaMA]], [[ChatGLM]], [[PaLM]]（均使用 RoPE 或其变体）

## 推导细节

1. **旋转定义**：$q' = R(m\theta)q$, $k' = R(n\theta)k$
2. **转置性质**：$(AB)^T = B^T A^T$，且 $R(\alpha)^T = R(-\alpha)$（因 $\cos$ 偶函数，$\sin$ 奇函数）
3. **矩阵乘法**：$R(-m\theta)R(n\theta) = R((n-m)\theta)$（和差角公式）
4. **结果**：Score $= q^T R((n-m)\theta) k$，仅含相对距离 $(n-m)$

## 相关文档

- [RoPE长上下文扩展方法](./RoPE-Extensions.md) - ABF、YaRN等外推技术
- [正弦位置编码](./正弦位置编码.md) - 加法位置编码对比

## 挂载路径

[[深度学习]] → [[Transformer]] → [[位置编码]] → [[RoPE]]