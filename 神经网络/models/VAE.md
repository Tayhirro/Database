# VAE（Variational Autoencoder）

## 1. 一句话
- 把 AE 的潜空间改成概率模型：学习 `q(z|x)` 与 `p(x|z)`，通过最大化 `ELBO` 做生成与表征学习。

## 2. 本质（概率化降维 + 生成建模）
- 把高维数据 `x` 看成由低维潜变量 `z` 生成：`p(x)=∫ p(z)p(x|z)dz`（潜变量模型的“降维”视角）。
- VAE 的核心是同时学两条路：  
  1) 生成路（decoder）：`p_θ(x|z)` 能从 `z` 采样生成 `x`  
  2) 推断路（encoder）：`q_φ(z|x)` 近似难算的真实后验 `p(z|x)`（用 ELBO 把它们绑在一起训练）
- 对照：GMM（隐类别）、HMM（隐状态）、因子分析（少数因子）也都是“隐变量解释观测”；VAE 只是把生成/推断用神经网络参数化了。入口：[modules/DimensionalityReduction.md](../modules/DimensionalityReduction.md)
- `z -> x` 这步在不同语境的名字（decoder mapping / pushforward / 参数化）见：[modules/LatentToDataMapping.md](../modules/LatentToDataMapping.md)

- 变分，ELBO ---- 拆分KL独立于θ ----
- 训练 vs 生成的一句话：训练时用“跟样本绑定”的后验采样 `z~q_φ(z|x)`（CVAE 则是 `z~q_φ(z|x,y)`）分布（的采样） 来教 decoder 学会重构/解释数据，影响数据的频率来进行训练decoder，同时用 `KL(q||p)` 把 `q` 拉向生成时要用的先验 `p(z)`（或条件先验 `p(z|x)`）；生成时只需从 `p(z)` / `p(z|x)` 采样 `z`。 
-`logpθ​(x)=L(x)+KL(qϕ​(z∣x) ∥ pθ​(z∣x))`

## 3. 关键对象
- 先验：`p(z)`（常用 `N(0,I)`）
- 编码器/推断网络：`q_φ(z|x)`
- 解码器/生成网络：`p_θ(x|z)`

## 4. 训练目标（ELBO）
- 入口：`modules/ELBO.md`
- 常写成：重构项 `E_q[log p(x|z)]` + 正则项 `-KL(q(z|x)||p(z))`

### 4.1 数字级例子：为什么 KL 会“整理潜空间”（但不改变真实结构）
假设 1 维潜变量（方便算），encoder 输出两簇后验：
- 对一半数据：`q(z|x)=N(μ=+10, σ=0.1)`
- 对另一半数据：`q(z|x)=N(μ=-10, σ=0.1)`

这时重构可能很好（两类被分得很开，decoder 很容易区分），但生成会很差：生成时用先验 `p(z)=N(0,1)` 采样，`z` 基本落在 `[-3,3]`，几乎抽不到 `±10`；而 decoder 主要在 `±10` 附近被训练过。

KL 会强烈惩罚“`μ` 太离谱”。对高斯有闭式：
- `KL(N(μ,σ^2) || N(0,1)) = 1/2 * (μ^2 + σ^2 - log σ^2 - 1)`
- 当 `μ=10, σ=0.1` 时，`μ^2/2=50` 已经很大，因此 KL 会逼 encoder 把均值往 0 拉、或者把方差变大、或者两者兼有。

结果是：
- `z` 空间里“被数据占用的区域”更靠近 0、更像高斯（更容易从 `N(0,1)` 采样到“有效 z”）
- decoder 也被迫学会：在这块更“规整”的 `z` 区域里，仍能重构/生成

注意：真实数据的结构（你在 `x` 空间看到的两簇/一张“流形”）没变；变的是“你用 `z` 怎么编码它们”以及“decoder 在 `z` 上怎么铺开生成”。

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
