---
title: ELBO（Evidence Lower Bound）
date: "2026-01-15"
categories:
  - 神经网络
description: 变分推断里用来“最大化可计算下界”的目标函数，常用于 VAE。
---
# ELBO（Evidence Lower Bound）

## 1. 一句话
- 变分推断里用来“最大化可计算下界”的目标函数，常用于 VAE。



## 2. 核心公式
给定模型 `p(x,z)=p(z)p(x|z)` 和变分分布 `q(z|x)`：
- `log p(x) >= ELBO(x)`
- `ELBO(x) = E_{q(z|x)}[log p(x|z)] - KL(q(z|x) || p(z))`

### 2.1 符号说明（`p` vs `q`）
- `x`：观测数据（比如一张图/一段轨迹）
- `z`：潜变量（压缩表示/意图等，模型里“看不见但解释数据”的东西）
- `p(z)`：先验（prior），在**没看到 `x`**之前对 `z` 的假设；VAE 里常设 `N(0,I)`
- `p(x|z)`：似然/解码器（decoder），给定 `z` 生成 `x` 的分布
- `p(z|x)`：真实后验（posterior），给定 `x` 反推 `z` 的分布（通常难算/不可积）
- `q(z|x)`：变分后验（variational posterior），用来近似 `p(z|x)` 的“可算分布”；VAE 里由 Encoder 输出其参数（如 `μ(x), σ(x)`）

等价分解（常用来理解为什么是“下界”）：
- `log p(x) = ELBO(x) + KL(q(z|x) || p(z|x))`
- `log p(x) = KL(q(z∣x)∥p(z∣x)) = Eq(z∣x)​[logp(z∣x)/q(z∣x)​] = Eq​[logq(z∣x)−logp(z∣x)]`
- `log p(x)= Eq​[logp(x,z)−logq(z∣x)]+KL(q(z∣x)∥p(z∣x))`

### 2.2 为什么要引入并训练 `q(z|x)`？
-  `p(x) = ∫ p(z)p(x|z) dz`，问题是：这个积分（以及它的梯度）在高维时通常不可解析/不可直接算。
- **`p(x|z)` 由神经网络参数化** 后，`p(x)` 变成一个连续混合分布，`∫ p(z)p(x|z)dz` 一般没有闭式解，进而 `p(z|x)=p(z)p(x|z)/p(x)` 也算不出来。
- 引入 `q(z|x)` 的一个直观方式是“重要性采样改写”（对任意 `q` 都成立，只要覆盖 `p` 的支持集）：
  - `p(x) = ∫ q(z|x) * (p(z)p(x|z)/q(z|x)) dz = E_{q(z|x)}[p(z)p(x|z)/q(z|x)]`
  - 但 `log p(x) = log E_q[...]` 仍然难优化（log 在期望外面）。
- ELBO 的关键是用 Jensen 把 log “搬进来”，得到一个可算下界：
  - `log p(x) = log E_q[p(z)p(x|z)/q(z|x)] >= E_q[log p(z)p(x|z) - log q(z|x)] = ELBO(x)`
- 所以训练时需要同时学：
  - `p_θ(x|z)`（decoder/likelihood 参数 `θ`）
  - `q_φ(z|x)`（encoder/variational posterior 参数 `φ`，让下界更紧、也让“给定 x 做推断”可用）

### 2.3 `p(x)` 是什么？为什么要最大化？能超过 1 吗？
- `p(x)` 是数据点 `x` 在模型下的边缘似然（evidence）：`p(x)=∫ p(z)p(x|z)dz`。
- 我们希望观测到的数据在模型下“更可能”，所以训练目标是让数据集的 `log p(x)` 尽量大（等价于最小化 NLL）；VAE 用 `ELBO(x)` 作为 `log p(x)` 的可优化下界。

## 3. 在 VAE 里怎么用
- 最大化 `ELBO` ⇔ 同时做两件事：  
  1) 重构项：`E_q[log p(x|z)]`（让生成器能复原 x）  
  2) 正则项：`- KL(q(z|x) || p(z))`（让后验别偏离先验太远）

## 5. 条件版 ELBO（CVAE：给定 `x` 建模 `y`）
- 有监督/条件生成里常见设定：给定条件输入 `x`（已知），生成目标 `y`（GT）。
- 生成模型写成：`p(y,z|x) = p(z|x) p(y|x,z)`（也常用简化先验 `p(z)`）
- 条件似然是：`p(y|x) = ∫ p(z|x) p(y|x,z) dz`
- 条件版 ELBO：
  - `log p(y|x) >= E_{q(z|x,y)}[log p(y|x,z)] - KL(q(z|x,y)||p(z|x))`
- 所以训练时“像你说的那样”走：`(x,y) -> q(z|x,y) -> p(y|x,z)`（重构的是 `y`，不是 `x`）
- 测试/生成时没有 `y`：`x -> p(z|x)` 采样 `z`，再 `p(y|x,z)` 解码得到多条候选 `y`

相关页：
- KL：`modules/probabilistic/KLDivergence.md`
- 重参数化：`modules/probabilistic/ReparameterizationTrick.md`
- VAE：`models/VAE.md`
