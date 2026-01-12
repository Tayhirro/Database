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

## 3. 在 VAE 里怎么用
- 最大化 `ELBO` ⇔ 同时做两件事：  
  1) 重构项：`E_q[log p(x|z)]`（让生成器能复原 x）  
  2) 正则项：`- KL(q(z|x) || p(z))`（让后验别偏离先验太远）

## 4. 训练/推理/生成时“到底走哪条路”
- 真正的生成模型是 `p(x,z)=p(z)p(x|z)`，边缘似然是：
  - `p(x) = ∫ p(z)p(x|z) dz`（不是 `p(z)p(x|z)/q(z|x)`；`q` 只是用来近似后验的辅助分布）
- 训练时（学参数 `θ, φ`）：
  1) `x -> Encoder -> q_φ(z|x)`（输出 `μ(x), σ(x)`）
  2) `z ~ q_φ(z|x)`（用重参数化采样）
  3) `z -> Decoder -> p_θ(x|z)`（输出重构分布参数）
  4) loss = `-E_{q_φ}[log p_θ(x|z)] + KL(q_φ(z|x)||p(z))`
  - 你“没看到 `p(z)` 走网络”是正常的：`p(z)` 常直接设成 `N(0,I)`，它主要出现在 KL 正则项里
- 推理/重构时（给定 `x` 还原/编码）：
  - 仍然走 `x -> q_φ(z|x) -> z -> p_θ(x|z)`（这条链是“编码-重构”）
- 生成时（无条件采样新数据）：
  - 先从先验采样 `z ~ p(z)`，再解码 `z -> p_θ(x|z)` 得到新 `x`
  - 之所以能这样做，是因为训练时用 KL 把 `q_φ(z|x)` 拉近 `p(z)`，让 “从 `p(z)` 采样”落在 decoder 见过的 latent 区域里

相关页：
- KL：`modules/KLDivergence.md`
- 重参数化：`modules/ReparameterizationTrick.md`
- VAE：`models/VAE.md`
