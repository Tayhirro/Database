# 重参数化技巧（Reparameterization Trick）

## 1. 一句话
- 把“对随机变量采样”改写成“对噪声采样 + 可导的确定性变换”，让梯度能反传。

## 2. VAE 里的最常用写法（高斯）
当 `q(z|x)=N(μ(x), diag(σ(x)^2))`：
- 采样改写：`z = μ(x) + σ(x) ⊙ ε`，其中 `ε ~ N(0, I)`

这样 `ε` 的随机性与参数解耦，`μ,σ` 可通过反向传播优化。

## 3. 备注
- 离散变量常用替代：Gumbel-Softmax / straight-through（看任务选）

相关页：
- ELBO：`modules/ELBO.md`
- VAE：`models/VAE.md`

