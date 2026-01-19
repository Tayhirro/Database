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


-p(x)=∫ p(z)p(x|z)dz 
-最大化p(x) --->logp(x) = logEpz p(x|z) >= Epz logp(x|z)  
-logp(x)−Ep(z)​[logp(x∣z)]=KL(p(z)∥p(z∣x))≥0
-Ep(z)​[logp(x∣z)] 负的多 --->KL大（pz和pz|x分布不同）--->Epz logp(x|z)  小 

-且本身为负（维度越高，负的越多）
-z则直接学成E(x)
### 推导证明：高斯似然下 $\mathbb{E}_{p(z)}[\log p(x|z)]$ 为何常为负

**前提假设**：
- 设 $x \in \mathbb{R}^D$（D 维数据）
- decoder 采用各向同性高斯：$p(x|z) = \mathcal{N}(x; \mu_\theta(z), \sigma^2 I)$

**Step 1：写出 log-likelihood 闭式**

由高斯分布的概率密度函数：

$$\log p(x|z) = -\frac{D}{2}\log(2\pi\sigma^2) - \frac{1}{2\sigma^2}\|x - \mu_\theta(z)\|^2$$

- 第一项：维度线性项（常数）
- 第二项：重建误差项（非负）

**Step 2：对 $p(z)$ 取期望**

$$\mathbb{E}_{p(z)}[\log p(x|z)] = -\frac{D}{2}\log(2\pi\sigma^2) - \frac{1}{2\sigma^2}\mathbb{E}_{p(z)}\|x - \mu_\theta(z)\|^2$$

**Step 3：推出严格上界**

注意第二项 $\geq 0$（平方范数的期望非负），因此：

$$\mathbb{E}_{p(z)}[\log p(x|z)] \leq -\frac{D}{2}\log(2\pi\sigma^2)$$

**Step 4：分析为何为负**

- 只要 $2\pi\sigma^2 > 1$（常见 $\sigma$ 不太小），右边就是**负的**
- 且随 $D$（维度）**线性变负**
- 再加上重建误差那项（必为负），就会**更负**

> **严谨来源**：即使你把重建误差做到 0，上面那个 $-\frac{D}{2}\log(2\pi\sigma^2)$ 也会随维度把它压得很低；而用先验采样时重建误差通常不可能接近 0，所以会更负。

---
-引入变分思想
-`(logpθ(x)) L(x) = Eqϕ​(z∣x)​[logpθ​(x∣z)] - KL(qϕ​(z∣x) ∥ pθ​(z∣x))`
- 最大化elbo = 最小化负 elbo
- Eqϕ​(z∣x)​[logpθ​(x∣z)]∝−Eqϕ​(z∣x)​∥x−fθ​(z)∥^2 ---MSE推导（正比）
- KL推导




-------------------------------------------------------------


 -如果引入变分思想  
 - `p(x)=∫ p(z)p(x|z)dz`
 - X-->得到z  qz|x(p(x|z))  --->p(x|z)  则相关




## 3. 关键对象
- 先验：`p(z)`（常用 `N(0,I)`）
- 编码器/推断网络：`q_φ(z|x)`
- 解码器/生成网络：`p_θ(x|z)`

## 4. 训练目标（ELBO）
- 入口：`modules/ELBO.md`
- ERM 视角：VAE 在做“无监督的 ERM”，把 `u` 取为 `x`，把 `loss` 取为 `-ELBO(x)`（见：[modules/Loss.md](../modules/Loss.md)）
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
