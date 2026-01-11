# VAE（Variational Autoencoder）

## 1. 一句话
- 把 AE 的潜空间改成概率模型：学习 `q(z|x)` 与 `p(x|z)`，通过最大化 `ELBO` 做生成与表征学习。

## 2. 关键对象
- 先验：`p(z)`（常用 `N(0,I)`）
- 编码器/推断网络：`q_φ(z|x)`
- 解码器/生成网络：`p_θ(x|z)`

## 3. 训练目标（ELBO）
- 入口：`modules/ELBO.md`
- 常写成：重构项 `E_q[log p(x|z)]` + 正则项 `-KL(q(z|x)||p(z))`

## 4. 关键流程（核心数据流）
- 训练：`x -> Encoder -> (mu, logvar) -> reparam -> z -> Decoder -> x_hat/x_logits -> (recon + KL) -> backprop 更新参数`
- 生成：`z ~ N(0, I) -> Decoder -> x_hat`

## 5. 关键技巧
- 重参数化：`modules/ReparameterizationTrick.md`

## 6. Tensor 级例子（图片 H×W×3）
- RGB 图片的 shape 对齐与 loss 计算：[VAE_TensorLevelExample.md](../examples/VAE_TensorLevelExample.md)

## 7. 常见坑 & Debug
- Posterior collapse（尤其是强解码器/文本任务）
- KL 权重/退火（KL annealing）、β-VAE 等策略

## 8. 扩展
- β-VAE、IWAE、VQ-VAE、CVAE（见 `models/CVAE.md`）
