# VAE（Variational Autoencoder）

## 1. 一句话
- 把 AE 的潜空间改成概率模型：学习 `q(z|x)` 与 `p(x|z)`，通过最大化 `ELBO` 做生成与表征学习。

## 2. 本质（概率化降维 + 生成建模）
- 把高维数据 `x` 看成由低维潜变量 `z` 生成：`p(x)=∫ p(z)p(x|z)dz`（潜变量模型的“降维”视角）。
- VAE 的核心是同时学两条路：  
  1) 生成路（decoder）：`p_θ(x|z)` 能从 `z` 采样生成 `x`  
  2) 推断路（encoder）：`q_φ(z|x)` 近似难算的真实后验 `p(z|x)`（用 ELBO 把它们绑在一起训练）
- 对照：GMM（隐类别）、HMM（隐状态）、因子分析（少数因子）也都是“隐变量解释观测”；VAE 只是把生成/推断用神经网络参数化了。入口：[modules/DimensionalityReduction.md](../modules/DimensionalityReduction.md)
## 3. 关键对象
- 先验：`p(z)`（常用 `N(0,I)`）
- 编码器/推断网络：`q_φ(z|x)`
- 解码器/生成网络：`p_θ(x|z)`

## 4. 训练目标（ELBO）
- 入口：`modules/ELBO.md`
- 常写成：重构项 `E_q[log p(x|z)]` + 正则项 `-KL(q(z|x)||p(z))`

## 5. 关键流程（核心数据流）
- 训练：`x -> Encoder -> (mu, logvar) -> reparam -> z -> Decoder -> x_hat/x_logits -> (recon + KL) -> backprop 更新参数`
- 生成：`z ~ N(0, I) -> Decoder -> x_hat`

## 6. 关键技巧
- 重参数化：`modules/ReparameterizationTrick.md`

## 7. Tensor 级例子（图片 H×W×3）
- RGB 图片的 shape 对齐与 loss 计算：[VAE_TensorLevelExample.md](../examples/VAE_TensorLevelExample.md)

## 8. 常见坑 & Debug
- Posterior collapse（尤其是强解码器/文本任务）
- KL 权重/退火（KL annealing）、β-VAE 等策略

## 9. 扩展
- β-VAE、IWAE、VQ-VAE、CVAE（见 `models/CVAE.md`）
