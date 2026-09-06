---
title: VAE（Variational Autoencoder）
date: "2026-01-20"
categories:
  - 神经网络
description: "把 AE 的潜空间改成概率模型：学习 q(z|x) 与 p(x|z)，通过最大化 ELBO 做生成与表征学习。"
---
# VAE（Variational Autoencoder）

真实世界的数据背后存在一个低维原因z，于是生成对z采样期望生成图片（z=(年龄，生日，时间)，decoder训练为根据这些生成内容) 

## 1. 一句话
- 把 AE 的潜空间改成概率模型：学习 `q(z|x)` 与 `p(x|z)`，通过最大化 `ELBO` 做生成与表征学习。
- z和x关联  ---由输入输出一个变分分布 拟合 真实z分布 --- 方便采样 
	-  通过积分边缘 推导
	-  通过贝叶斯推导

## 2. ELBO 推导（两条等价路径）

### 2.1 问题设定：为什么需要 ELBO

生成模型定义：

$$
z \sim p(z), \qquad x \sim p_\theta(x \mid z)
$$

训练目标是最大化对数似然：

$$
\max_\theta \; \log p_\theta(x)
$$

但 $p_\theta(x)$ 要对所有 $z$ 积分：

$$
p_\theta(x) = \int p_\theta(x \mid z)\,p(z)\,dz
$$

这个积分算不了（$z$ 的维度太高，穷举不可能）。同时，真实后验也算不了：

$$
p_\theta(z \mid x) = \frac{p_\theta(x \mid z)\,p(z)}{p_\theta(x)}
$$

分母正是那个难算的积分。因此两个困难本质上是同一个：

$$
p_\theta(x) \text{ 难算} \quad\Longleftrightarrow\quad p_\theta(z \mid x) \text{ 难算}
$$

**解决思路**：引入一个可计算、可采样的近似后验 $q_\phi(z \mid x)$（encoder），用它来构造一个对数似然的下界（ELBO），最大化 ELBO 等价于间接最大化 $\log p_\theta(x)$。
- 这种ELBO会在真实后验p(x|z)and pz 之间平衡，然后同时根据这个训练一个decoder
	- 直接在z捞针会导致梯度根本无效，很难学习
	- 通过引入ELBO 和条件先验，在周围采样使得梯度能流动



下面两条路径都能推出同一个 ELBO。


### 2.2 路径一：贝叶斯分解（精确恒等式）

**回答的问题**：ELBO 和真实对数似然之间**差多少**？

---

**Step 1：写出恒等式**

从联合分布出发：

$$
p_\theta(x) = \frac{p_\theta(x \mid z)\,p(z)}{p_\theta(z \mid x)}
$$

取对数：

$$
\log p_\theta(x) = \log p(z) + \log p_\theta(x \mid z) - \log p_\theta(z \mid x) \tag{1}
$$

---

**Step 2：对近似后验取期望**

式 (1) 对任意 $z$ 成立，因此对 $q_\phi(z \mid x)$ 取期望依然成立（$\log p_\theta(x)$ 与 $z$ 无关，期望不变）：

$$
\log p_\theta(x) = \mathbb{E}_{q_\phi(z \mid x)}\Bigl[\log p(z) + \log p_\theta(x \mid z) - \log p_\theta(z \mid x)\Bigr] \tag{2}
$$

---

**Step 3：用 KL 消去不可算的 $\log p_\theta(z \mid x)$**

KL 散度的定义：

$$
D_{\mathrm{KL}}\bigl(q_\phi(z \mid x) \,\|\, p_\theta(z \mid x)\bigr)
= \mathbb{E}_{q}\bigl[\log q_\phi(z \mid x) - \log p_\theta(z \mid x)\bigr]
$$

移项，把 $\mathbb{E}_q[-\log p_\theta(z \mid x)]$ 解出来：

$$
\mathbb{E}_q[-\log p_\theta(z \mid x)] = D_{\mathrm{KL}}(q \,\|\, p_\theta(\cdot \mid x)) - \mathbb{E}_q[\log q_\phi(z \mid x)] \tag{3}
$$

---

**Step 4：代回整理**

把 (3) 代入 (2)：

$$
\begin{aligned}
\log p_\theta(x)
&= \mathbb{E}_q[\log p(z) + \log p_\theta(x \mid z)] + D_{\mathrm{KL}} - \mathbb{E}_q[\log q_\phi] \\[4pt]
&= \mathbb{E}_q[\log p_\theta(x \mid z)] + \mathbb{E}_q[\log p(z) - \log q_\phi(z \mid x)] + D_{\mathrm{KL}}
\end{aligned}
$$

后两项合并为 KL：

$$
\boxed{
\log p_\theta(x)
= \underbrace{\mathbb{E}_{q_\phi}[\log p_\theta(x \mid z)] \;-\; D_{\mathrm{KL}}\bigl(q_\phi(z \mid x) \,\|\, p(z)\bigr)}_{\text{ELBO}}
\;+\; \underbrace{D_{\mathrm{KL}}\bigl(q_\phi(z \mid x) \,\|\, p_\theta(z \mid x)\bigr)}_{\geq 0}
}
$$

---

**Step 5：结论**

因为 KL ≥ 0：

$$
\log p_\theta(x) \geq \mathrm{ELBO}(x)
$$

而且 gap 就是近似后验与真实后验之间的 KL：

$$
\log p_\theta(x) - \mathrm{ELBO} = D_{\mathrm{KL}}\bigl(q_\phi(z \mid x) \,\|\, p_\theta(z \mid x)\bigr)
$$

- 当 $q_\phi = p_\theta(z \mid x)$ 时 gap = 0，ELBO = 真实似然

**这条 Eq. 4 里面其实藏着两个"力"**：

把最终式子重新看一遍：

$$
\log p_\theta(x) = \underbrace{\mathbb{E}_q[\log p_\theta(x \mid z)]}_{\text{① 重建力}} \;-\; \underbrace{D_{\mathrm{KL}}(q_\phi \,\|\, p(z))}_{\text{② 正则力}} \;+\; \underbrace{D_{\mathrm{KL}}(q_\phi \,\|\, p_\theta(z \mid x))}_{\text{③ 变分间隙}}
$$

- **② 正则力**（ELBO 内部）：最大化 ELBO 会**显式**地把 $q_\phi$ 推向先验 $p(z)$。这是防止后验崩塌的约束，保证生成时从 $\mathcal{N}(0,I)$ 采样能命中有效区域。
- **③ 变分间隙**（ELBO 之外）：最大化 ELBO 会**隐式**地把 $q_\phi$ 推向真实后验 $p_\theta(z \mid x)$。因为 $\log p_\theta$ 对给定的 $x$ 是定值，推高 ELBO 就等价于压缩 gap，也就是缩小 $q_\phi$ 和 $p_\theta(z \mid x)$ 的距离。

> 训练时这两个力**同时起作用**：往后验 $p_\theta(z \mid x)$ 的方向保证 encoder 编码出对当前 $x$ 有信息量的 $z$；往先验 $p(z)$ 的方向保证 $z$ 空间整齐、生成时可采到。两者之间的平衡就是 VAE 的核心。


### 2.3 路径二：Jensen 不等式（直接构造下界）

**回答的问题**：ELBO 为什么是**下界**？

---

**Step 1：从边缘积分出发**

$$
\log p_\theta(x) = \log \int p_\theta(x, z)\,dz = \log \int p_\theta(x \mid z)\,p(z)\,dz
$$

> ⚠️ 这里积分的是联合分布 $p_\theta(x, z)$，不是后验 $p_\theta(z \mid x)$。积后验等于 1，得不到 $p_\theta(x)$。

---

**Step 2：引入 $q_\phi(z \mid x)$**

在积分里乘除同一个分布：

$$
\log p_\theta(x)
= \log \int q_\phi(z \mid x)\,\frac{p_\theta(x, z)}{q_\phi(z \mid x)}\,dz
= \log \mathbb{E}_{q_\phi}\!\left[\frac{p_\theta(x, z)}{q_\phi(z \mid x)}\right]
$$

---

**Step 3：扔 Jensen 不等式**

$\log$ 是凹函数 → $\log\mathbb{E}[Y] \ge \mathbb{E}[\log Y]$：

$$
\log p_\theta(x)
\ge \mathbb{E}_{q_\phi}\!\left[\log\frac{p_\theta(x, z)}{q_\phi(z \mid x)}\right]
$$

---

**Step 4：展开联合分布**

$p_\theta(x, z) = p_\theta(x \mid z)\,p(z)$：

$$
\begin{aligned}
\log p_\theta(x)
&\ge \mathbb{E}_{q_\phi}\Bigl[\log p_\theta(x \mid z) + \log p(z) - \log q_\phi(z \mid x)\Bigr] \\[4pt]
&= \mathbb{E}_{q_\phi}[\log p_\theta(x \mid z)] \;-\; D_{\mathrm{KL}}\bigl(q_\phi(z \mid x) \,\|\, p(z)\bigr)
\end{aligned}
$$

右边就是 ELBO，和路径一完全一致。


### 2.4 两条路径的对比

| | 路径一：贝叶斯分解 | 路径二：Jensen 不等式 |
|---|---|---|
| **出 发 点** | $\log p_\theta(x)$ 恒等变形 | 边缘积分 $\log \int p_\theta(x, z)\,dz$ |
| **核心工具** | Bayes 公式 + KL 定义移项 | Jensen 不等式 $\log\mathbb{E} \ge \mathbb{E}\log$ |
| **得到的形式** | $\log p_\theta = \mathrm{ELBO} + D_{\mathrm{KL}}$ | $\log p_\theta \ge \mathrm{ELBO}$ |
| **精确度** | **精确恒等式**（gap 就是 KL） | **不等式**（未给出 gap 表达式） |
| **回答的问题** | 差多少？ | 为什么是下界？ |
| **终点** | 同一个 ELBO | 同一个 ELBO |

```text
                       p_θ(x) = ∫ p_θ(x|z) p(z) dz    ← 算不了
                                     │
                  ┌──────────────────┴──────────────────┐
                  │                                     │
          路径一：Bayes 移项                      路径二：Jensen 不等式
          log p = ELBO + KL                      log p ≥ ELBO
          回答：”差多少”                          回答：”为什么是下界”
                  │                                     │
                  └──────────────────┬──────────────────┘
                                     │
                              同一个 ELBO
                  E_q[log p(x|z)] − KL(q || p(z))
```

> **一句话总结**：路径一给出精确的 gap 表达式，路径二给出下界的直觉来源。训练时最大化的是同一个 ELBO。重构项推样本质量，KL 项把后验拉向先验，保证生成时可从 $p(z)$ 采样。


-------------------------------------------------------------
## 3. 关键对象
- 先验：`p(z)`（常用 `N(0,I)`）
- 编码器/推断网络：`q_φ(z|x)`
- 解码器/生成网络：`p_θ(x|z)`

## 4. 训练目标（ELBO）
- 入口：`modules/probabilistic/ELBO.md`
- ERM 视角：VAE 在做“无监督的 ERM”，把 `u` 取为 `x`，把 `loss` 取为 `-ELBO(x)`（见：[modules/training/Loss.md](../modules/training/Loss.md)）
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
- 重参数化：`modules/probabilistic/ReparameterizationTrick.md`

## 7. Tensor 级例子（图片 H×W×3）
- RGB 图片的 shape 对齐与 loss 计算：[VAE_TensorLevelExample.md](../examples/VAE_TensorLevelExample.md)

## 8. 常见坑 & Debug
- Posterior collapse（尤其是强解码器/文本任务）
- KL 权重/退火（KL annealing）、β-VAE 等策略

## 9. 扩展
- β-VAE、IWAE、VQ-VAE、CVAE（见 `models/CVAE.md`）
