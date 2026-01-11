# ELBO（Evidence Lower Bound）

## 1. 一句话
- 变分推断里用来“最大化可计算下界”的目标函数，常用于 VAE。

## 2. 核心公式
给定模型 `p(x,z)=p(z)p(x|z)` 和变分分布 `q(z|x)`：
- `log p(x) >= ELBO(x)`
- `ELBO(x) = E_{q(z|x)}[log p(x|z)] - KL(q(z|x) || p(z))`

等价分解（常用来理解为什么是“下界”）：
- `log p(x) = ELBO(x) + KL(q(z|x) || p(z|x))`

## 3. 在 VAE 里怎么用
- 最大化 `ELBO` ⇔ 同时做两件事：  
  1) 重构项：`E_q[log p(x|z)]`（让生成器能复原 x）  
  2) 正则项：`- KL(q(z|x) || p(z))`（让后验别偏离先验太远）

相关页：
- KL：`modules/KLDivergence.md`
- 重参数化：`modules/ReparameterizationTrick.md`
- VAE：`models/VAE.md`

