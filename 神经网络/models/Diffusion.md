---
title: Diffusion（Diffusion Models / DDPM）
date: "2026-01-11"
categories:
  - 神经网络
description: 通过“前向逐步加噪 + 学习反向去噪”来建模数据分布的生成模型族。
---
# Diffusion（Diffusion Models / DDPM）

## 1. 一句话
- 先把真实样本 $x_0$ 一点点加噪，直到变成接近纯高斯噪声；再训练一个网络学会“每一步去掉一点噪声”，最终从纯噪声还原出样本。

## 2. 目标（解决什么问题）
- 目标还是学数据分布 $p_{\text{data}}(x)$，只是它不直接“一步生成”，而是拆成很多个更容易学的小去噪步骤。
- 直觉上，直接从随机向量一下子生成高清图很难；但如果只要求模型回答“当前这张带噪图里，噪声大概是什么”，问题就容易很多。
- 所以 Diffusion 的核心不是“直接画图”，而是“反复做局部去噪”，最后把随机噪声慢慢修成有结构的样本。

## 3. 核心结构（把流程先看懂）

### 3.1 整体数据流

```text
真实样本 x_0
   │
   ├─ 前向过程（固定，不训练）
   │    x_0 -> x_1 -> x_2 -> ... -> x_T
   │    每一步都加一点高斯噪声
   │
   └─ 训练网络学反向过程
        输入：某一步的带噪样本 x_t 和时间步 t
        输出：这一步里的噪声 ε_hat = ε_θ(x_t, t)
```

```text
推理 / 采样时：
x_T ~ N(0, I)
  -> x_{T-1}
  -> x_{T-2}
  -> ...
  -> x_0
```

- 训练时喂给模型的是“真实样本加噪后的中间态 $x_t$”
- 采样时喂给模型的是“纯噪声开始逐步去噪得到的中间态 $x_t$”
- 两边都围绕同一件事：给定 $x_t$，预测当前这一步该去掉多少噪声

### 3.2 三个最重要的符号
- $\beta_t$：第 $t$ 步加多少噪声（noise schedule）
- $\alpha_t = 1 - \beta_t$
- $\bar{\alpha}_t = \prod_{s=1}^t \alpha_s$：从第 1 步累计到第 $t$ 步后，原始信号还剩多少

## 4. 前向过程（固定加噪，不需要学习）

### 4.1 一步一步加噪

$$
q(x_t|x_{t-1}) = \mathcal{N}(x_t; \sqrt{\alpha_t}x_{t-1}, \beta_t I)
$$

- 含义：第 $t$ 步把 $x_{t-1}$ 稍微缩小一点，再加一点高斯噪声
- $\beta_t$ 一般很小，所以每一步变化都不大
- 当步数很多时，样本会逐渐失去原始结构，最后 $x_T$ 接近纯噪声 $\mathcal{N}(0, I)$

### 4.2 训练里最关键的闭式公式

虽然定义上是一步一步加噪，但可以直接从 $x_0$ 跳到任意 $x_t$：

$$
x_t = \sqrt{\bar{\alpha}_t}x_0 + \sqrt{1-\bar{\alpha}_t}\,\epsilon,\quad \epsilon \sim \mathcal{N}(0,I)
$$

这条式子非常关键，因为它说明：
- 训练时不需要真的把 $x_0 \to x_1 \to x_2 \to \dots \to x_t$ 全跑一遍
- 只要随机采样一个 $t$，再采一个噪声 $\epsilon$，就能直接构造出这一步的带噪样本 $x_t$

这也是很多人第一次看 Diffusion 时最容易卡住的点：
- **训练时**：通常是“随机抽一个时间步，直接合成 $x_t$”
- **采样时**：才是“真的从 $T$ 到 $1$ 一步一步往回走”

## 5. 反向过程（学习去噪）

理论上我们想学的是：

$$
p_\theta(x_{t-1}|x_t)
$$

也就是：已知当前带噪样本 $x_t$，如何得到更干净一点的 $x_{t-1}$。

但实践里通常不让网络直接输出 $x_{t-1}$，而是让它预测当前噪声：

$$
\epsilon_\theta(x_t, t)
$$

直觉是：
- $x_t$ 由“信号 + 噪声”混合而来
- 如果网络能估计出其中的噪声 $\epsilon$
- 那就能反推出“还剩下多少原始信号”，从而构造去噪的下一步

因此 Diffusion 常见说法是：
- DDPM 在学反向高斯链 $p_\theta(x_{t-1} \mid x_t)$
- 实现上常等价成“预测噪声 $\epsilon$”
- 再进一步，也能写成预测 score 或预测 $x_0$ / $v$

### 5.1 这里其实有三层东西，别混在一起

很多困惑都来自于把“网络输出什么”和“反向采样怎么更新”混成了一件事。标准 DDPM 里其实同时有下面三层：

1. **前向加噪分布（固定）**

$$
q(x_t \mid x_{t-1})
$$

- 这是人为设计好的加噪过程，不训练
- 它决定了训练时怎么把干净样本变成带噪样本

2. **真实后验分布（理论上存在，训练推导里会用到）**

$$
q(x_{t-1} \mid x_t, x_0) = \mathcal{N}(\tilde{\mu}_t(x_t, x_0), \tilde{\beta}_t I)
$$

- 这表示：如果你同时知道当前噪声态 $x_t$ 和原图 $x_0$，那么“上一步 $x_{t-1}$ 应该长什么样”其实可以写成闭式高斯分布
- 论文里经常先把这一层写出来，因为它是 ELBO 推导和反向过程设计的基础
- 但注意：**采样时你并不知道真实 $x_0$**

3. **模型学习的反向分布（真正部署时用）**

$$
p_\theta(x_{t-1} \mid x_t) = \mathcal{N}(\mu_\theta(x_t,t), \sigma_t^2 I)
$$

- 这是我们真正想学的对象
- 网络本身通常不直接输出整个高斯分布，而是输出一个更容易学的量，比如噪声 $\epsilon_\theta(x_t,t)$
- 然后再由固定公式把 $\epsilon_\theta$ 转成 $\mu_\theta$

所以论文里写“反向是一个高斯分布”，和实现里写“U-Net 预测噪声”并不冲突。两者只是同一件事的两种表述：

- **概率建模视角**：我在学习 $p_\theta(x_{t-1} \mid x_t)$
- **工程实现视角**：我让 U-Net 预测噪声，再用闭式公式算出这一步的均值 $\mu_\theta$

## 6. 损失 / 训练目标

### 6.1 最常见版本：预测噪声

训练时做下面四步：
1. 从数据集中取一个真实样本 $x_0$
2. 随机采样一个时间步 $t$
3. 采样噪声 $\epsilon \sim \mathcal{N}(0, I)$，构造

$$
x_t = \sqrt{\bar{\alpha}_t}x_0 + \sqrt{1-\bar{\alpha}_t}\,\epsilon
$$

4. 让网络预测这份噪声：$\hat{\epsilon} = \epsilon_\theta(x_t, t)$，然后做 MSE

$$
L = \mathbb{E}_{x_0,\epsilon,t}\left[\|\epsilon - \epsilon_\theta(x_t, t)\|^2\right]
$$

这就是最常见的 DDPM 训练目标。

### 6.2 为什么“预测噪声”就够了
- 因为 $x_t$ 的构成已经知道：一部分来自 $x_0$，一部分来自 $\epsilon$
- 一旦网络把 $\epsilon$ 估准，就能估计当前样本里有多少是真的结构、多少是噪声
- 所以“预测噪声”本质上就是在学“怎么去噪”

### 6.3 时间步 $t$ 为什么也要输入模型
- 因为不同 $t$ 的噪声强度完全不同
- $t=10$ 时图片还比较清楚，只是轻微带噪
- $t=900$ 时几乎已经看不出原图了
- 同一个 $x_t$ 的去噪策略会随噪声级别变化，所以模型必须知道当前是第几步

### 6.4 为什么论文里又写成“先得到一个分布，再采样到 $x_{t-1}$”


$$
p_\theta(x_{t-1} \mid x_t) = \mathcal{N}(\mu_\theta(x_t,t), \sigma_t^2 I)
$$

原因是：

- Diffusion 本质上是一个**逐步生成的概率模型**
- 生成时每一步都要回答：“给定现在的 $x_t$，上一步 $x_{t-1}$ 的条件分布是什么？”
- 所以论文必须先把“反向一步”写成一个分布

而“预测噪声”只是这个分布的一种参数化方式。典型做法是：

1. 网络先预测

$$
\hat{\epsilon} = \epsilon_\theta(x_t,t)
$$

2. 由 $\hat{\epsilon}$ 估计当前样本对应的干净样本

$$
\hat{x}_0 = \frac{x_t - \sqrt{1-\bar{\alpha}_t}\hat{\epsilon}}{\sqrt{\bar{\alpha}_t}}
$$

3. 再把 $\hat{x}_0$ 代回后验均值公式，或者直接用等价闭式公式得到

$$
\mu_\theta(x_t,t)=\frac{1}{\sqrt{\alpha_t}}\left(x_t-\frac{\beta_t}{\sqrt{1-\bar{\alpha}_t}}\hat{\epsilon}\right)
$$

4. 最后采样

$$
x_{t-1} = \mu_\theta(x_t,t) + \sigma_t z,\quad z \sim \mathcal{N}(0,I)
$$

所以要把这两句话严格区分开：

- **训练监督的是噪声 $\epsilon$**
- **采样执行的是反向高斯更新 $x_t \to x_{t-1}$**

它们不是两套模型，而是同一个模型在“训练阶段”和“生成阶段”的不同使用方式。

### 6.5 从原论文目标到 MSE 的关系
加噪声与预测噪声求loss，一步步推回

原论文从变分下界（ELBO）出发：

$$
\mathcal{L}_{\text{vlb}}
=
\mathbb{E}\left[
D_{\mathrm{KL}}(q(x_T \mid x_0)\|p(x_T))
+ \sum_{t=2}^{T} D_{\mathrm{KL}}\bigl(q(x_{t-1}\mid x_t,x_0)\|p_\theta(x_{t-1}\mid x_t)\bigr)
- \log p_\theta(x_0 \mid x_1)
\right]
$$

然后在 DDPM 的常见设定下：

- 反向方差 $\sigma_t^2$ 取固定值或预定义值
- 均值 $\mu_\theta$ 用噪声预测 $\epsilon_\theta$ 来参数化

真实后验和模型反向过程都写成高斯：

$$
p_\theta(x_{t-1}\mid x_t)
=
\mathcal{N}\left(x_{t-1};\mu_\theta(x_t,t),\sigma_t^2I\right)
$$

$$
q(x_{t-1}\mid x_t,x_0)
=
\mathcal{N}\left(x_{t-1};\tilde{\mu}_t(x_t,x_0),\tilde{\beta}_tI\right)
$$

当 $p_\theta$ 的方差 $\sigma_t^2$ 固定后，KL 中和模型参数 $\theta$ 有关的部分只剩两个均值之间的距离：

$$
L_{t-1}
=
\mathbb{E}_q\left[
\frac{1}{2\sigma_t^2}
\left\|
\tilde{\mu}_t(x_t,x_0)-\mu_\theta(x_t,t)
\right\|^2
\right]
+C
$$

也就是说，原论文里的这一步 KL 可以看成 posterior mean $\tilde{\mu}_t(x_t,x_0)$ 和模型 mean $\mu_\theta(x_t,t)$ 之间的 MSE。又因为 DDPM 把 $\mu_\theta$ 进一步改写成由 $\epsilon_\theta(x_t,t)$ 决定的形式，所以这个均值 MSE 最后会化成一个与噪声预测误差等价的加权二次项。再进一步，论文和后续实现通常使用简化版目标：

$$
\mathcal{L}_{\text{simple}}=\mathbb{E}_{x_0,\epsilon,t}\left[\|\epsilon-\epsilon_\theta(x_t,t)\|^2\right]
$$



##  几何直觉
知乎专栏 https://zhuanlan.zhihu.com/p/11228697012


## 7. 训练流程（这部分最容易和采样搞混）

```python
for x0 in dataloader:
    t = Uniform({1, ..., T})                 # 随机抽一个时间步
    eps = Normal(0, I)                       # 采样高斯噪声
    xt = sqrt(alpha_bar[t]) * x0 + sqrt(1 - alpha_bar[t]) * eps
    eps_hat = model(xt, t)                   # 预测当前噪声
    loss = mse(eps_hat, eps)
    loss.backward()
    optimizer.step()
```

这里有三个关键理解：
- **训练时不是每次都从 $x_0$ 加噪到 $x_T$**
- **训练时也不是每次都完整做一遍反向去噪**
- 训练通常甚至**不会显式算出 $x_{t-1}$**，而只是学一个“共享的去噪器”：它在任意时间步 $t$ 都知道该怎么估计噪声

可以把它想成：
- 数据集给你很多干净图片 $x_0$
- 你随机把图片污染到不同程度，得到不同噪声级别的 $x_t$
- 然后逼模型回答：“这张图里加进去的噪声到底是什么？”

如果这个问题它在所有时间步上都答得准，那么采样时它就能真的一步一步把噪声去掉。

> [!note]
> 如果你看的论文不是直接写 $x_t$，而是写 noisy latent $\tilde{z}_i^k$ 和条件 $(z_i, h_i)$，本质也一样。只是把“图像空间上的扩散”换成了“潜空间上的条件扩散”：
> - U-Net 输入：当前 noisy latent $\tilde{z}_i^k$、时间步 $k$、条件 $(z_i,h_i)$
> - U-Net 输出：该步噪声预测 $\hat{\epsilon}_k$
> - scheduler / reverse update：利用 $\hat{\epsilon}_k$ 和固定噪声日程，把 $\tilde{z}_i^k$ 更新到 $\tilde{z}_i^{k-1}$
> 训练监督依然通常是噪声 MSE；“得到一个高斯分布再更新到前一步”依然是采样时的解析反推规则，而不是第二个神经网络。

## 8. 推理 / 采样（真正的一步步去噪）

### 8.1 采样流程

```text
先采一个纯噪声 x_T ~ N(0, I)
for t = T, T-1, ..., 1:
    用模型预测当前噪声 ε_hat = ε_θ(x_t, t)
    根据 ε_hat 计算更干净的 x_{t-1}
最终得到 x_0
```

DDPM 中常见的反推均值写法是：

$$
\mu_\theta(x_t,t)=\frac{1}{\sqrt{\alpha_t}}\left(x_t-\frac{\beta_t}{\sqrt{1-\bar{\alpha}_t}}\epsilon_\theta(x_t,t)\right)
$$

然后采样：

$$
x_{t-1} = \mu_\theta(x_t,t) + \sigma_t z,\quad z \sim \mathcal{N}(0,I)
$$

- 这里 $\mu_\theta$ 可以理解为“去掉一部分噪声后，下一步大概该在哪”
- 后面的随机项是为了让反向过程保持分布上的正确性
- 到最后一步时，噪声会越来越少，样本越来越清晰

### 8.2 为什么采样慢
- 因为真的要从 $T$ 走到 $1$
- 如果 $T=1000$，就要做 1000 次网络前向
- 所以 Diffusion 常常“质量高，但采样慢”

这也是它和 [GAN.md](GAN.md)、[VAE.md](VAE.md) 的典型区别：
- GAN / VAE 通常一次前向就能生成
- Diffusion 需要多步迭代

## 9. 一个最直观的流程图

```text
训练：
x_0(真实样本)
  -> 随机选 t
  -> 采样噪声 ε
  -> 合成 x_t = sqrt(alpha_bar_t) x_0 + sqrt(1-alpha_bar_t) ε
  -> 模型输入 (x_t, t)
  -> 输出 ε_hat
  -> 用 MSE(ε_hat, ε) 训练

采样：
x_T ~ N(0, I)
  -> 模型看 (x_T, T) 预测噪声
  -> 得到 x_{T-1}
  -> 模型看 (x_{T-1}, T-1) 预测噪声
  -> ...
  -> 得到 x_0
```

一句话总结主流程：
- **训练**是在学“任意噪声级别下，噪声长什么样”
- **采样**是在用这个能力“把纯噪声一点点擦干净”

## 10. 常见坑 & Debug 清单
- 把“训练流程”和“采样流程”混成一件事：这是最常见误区
- 不理解 $x_t$ 可以由闭式公式直接从 $x_0$ 构造：会误以为训练必须完整跑 $T$ 步
- $\beta_t$ 的 schedule 不合适：加噪太猛或太弱，都会让训练困难
- 时间步嵌入做得太弱：模型分不清当前噪声级别
- guidance 开太强：图像更听话，但多样性可能下降，甚至出现过曝/失真

## 11. 扩展与对比

### 11.1 常见扩展
- **DDIM**：把采样改得更接近确定性，可以少步采样，加速明显
- **Guided Diffusion / CFG**：在反向采样里加入额外引导，让生成更符合文本、类别、奖励或 Q 值；可继续看 [GuidedDiffusion.md](GuidedDiffusion.md)
- **Latent Diffusion**：先把图像压到潜空间里再扩散，计算量更低；Stable Diffusion 就属于这类
- **Score-based Models / SDE 视角**：把扩散统一到 score matching 与随机微分方程框架下理解

### 11.2 与其他生成模型的对比

|  | VAE | GAN | Diffusion |
| --- | --- | --- | --- |
| 核心思路 | 概率潜变量建模 | 对抗博弈 | 逐步去噪 |
| 采样方式 | 一次 decoder 前向 | 一次 generator 前向 | 多步迭代 |
| 训练稳定性 | 较好 | 较差 | 较好 |
| 样本质量 | 中等 | 高 | 高 |
| 速度 | 快 | 快 | 慢 |

## 12. 参考
- Ho et al., 2020. *Denoising Diffusion Probabilistic Models*
- Song et al., 2021. *Score-Based Generative Modeling through Stochastic Differential Equations*
- Rombach et al., 2022. *High-Resolution Image Synthesis with Latent Diffusion Models*
