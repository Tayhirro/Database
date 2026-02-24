# GAN（Generative Adversarial Network）

## 1. 一句话
- 用两个网络对抗训练：生成器 G 造假样本，判别器 D 区分真假；纳什均衡时 G 学到数据分布。

## 2. 目标（解决什么问题）
- 从噪声 `z ~ p(z)` 生成逼真样本 `x = G(z)`，使得 `p_G(x) ≈ p_data(x)`
- 与 VAE 的区别：不需要显式定义似然 `p(x|z)`，也不需要推断后验 `q(z|x)`；只需要一个判别器来"打分"
- 与 Diffusion 的区别：采样只需 G 的一次前向（快），不需要多步迭代去噪

## 3. 核心结构（数据流）

```
z ~ p(z)  ──→  Generator G(z)  ──→  x_fake
                                       ↓
                                  Discriminator D  ──→  D(x) ∈ (0,1)
                                       ↑
真实数据 x_real  ───────────────────────┘
```

- **Generator G**：输入噪声 z（通常 `z ~ N(0,I)`），输出生成样本 `x_fake = G(z)`
- **Discriminator D**：输入样本 x（真或假），输出 `D(x) ∈ (0,1)` 表示"是真实数据的概率"
- 两者同时训练，形成博弈

## 4. 损失 / 训练目标

### 4.1 Minimax 价值函数

$$\min_G \max_D V(D, G) = \mathbb{E}_{x \sim p_{data}}[\log D(x)] + \mathbb{E}_{z \sim p(z)}[\log(1 - D(G(z)))]$$

拆开来看：
- **D 想最大化 V**：把真样本判为 1（`log D(x)` 大），把假样本判为 0（`log(1 - D(G(z)))` 大）
- **G 想最小化 V**：让 D 把假样本也判为 1（`D(G(z))` 大 → `log(1 - D(G(z)))` 小）

### 4.2 最优判别器 D*

固定 G，对 V 关于 D(x) 求导，令其为零：

$$D^*(x) = \frac{p_{data}(x)}{p_{data}(x) + p_G(x)}$$

**推导**：对每个 x，V 中关于 D(x) 的部分是：

$$f(D) = p_{data}(x) \log D + p_G(x) \log(1 - D)$$

$$f'(D) = \frac{p_{data}(x)}{D} - \frac{p_G(x)}{1 - D} = 0$$

$$\Rightarrow D^*(x) = \frac{p_{data}(x)}{p_{data}(x) + p_G(x)}$$

当 $p_G = p_{data}$ 时，$D^*(x) = 1/2$——判别器完全分不清真假。

### 4.3 最优 G 等价于最小化 JS 散度

将 $D^*$ 代入 $V$：

$$V(D^*, G) = \mathbb{E}_{p_{data}}\left[\log \frac{p_{data}}{p_{data} + p_G}\right] + \mathbb{E}_{p_G}\left[\log \frac{p_G}{p_{data} + p_G}\right]$$

$$= -\log 4 + 2 \cdot JSD(p_{data} \| p_G)$$

其中 JSD 是 Jensen-Shannon 散度（对称版 KL）：

$$JSD(p \| q) = \frac{1}{2}KL(p \| m) + \frac{1}{2}KL(q \| m), \quad m = \frac{p + q}{2}$$

- G 的最优解：$p_G = p_{data}$，此时 $JSD = 0$，$V = -\log 4$
- 对照 VAE 最小化 KL（[modules/KLDivergence.md](../modules/KLDivergence.md)），GAN 最小化的是 JSD——对称且有界（$JSD \in [0, \log 2]$）

### 4.4 实践中 G 的损失：非饱和版本

原始目标 `log(1 - D(G(z)))` 在训练早期有严重梯度消失：
- 早期 G 很差 → `D(G(z)) ≈ 0` → `log(1 - 0) ≈ 0` → **梯度几乎为零**

实践中换成 **非饱和损失**：

$$L_G = -\mathbb{E}_{z}[\log D(G(z))]$$

- 当 `D(G(z)) ≈ 0` 时，`-log(0) → ∞` → 梯度很大，G 能快速学习
- 两者在最优点（`D(G(z)) = 1/2`）梯度方向一致

## 5. 训练流程（伪代码）

```python
for each iteration:
    # —— 训练 D（k 步，通常 k=1）——
    x_real ~ p_data                 # 采样真实数据
    z ~ p(z)                        # 采样噪声
    x_fake = G(z).detach()          # 生成假样本，不传梯度给 G
    L_D = -[log D(x_real) + log(1 - D(x_fake))]
    更新 D 的参数

    # —— 训练 G（1 步）——
    z ~ p(z)
    x_fake = G(z)
    L_G = -log D(x_fake)            # 非饱和版本
    更新 G 的参数（D 冻结）
```

关键细节：
- 交替训练，不是联合优化
- D 通常比 G 多训练几步（让 D 先"学会鉴别"，G 才有有效的梯度信号）
- `detach()` 或 `stop_gradient` 很重要：训练 D 时不要把梯度传给 G

## 6. 推理 / 采样

```
z ~ N(0, I)  →  x = G(z)
```

- 采样就是 G 的一次前向传播
- 比 Diffusion 快得多（不需要迭代去噪）
- 比 VAE 的 decoder 采样类似，但没有结构化的潜空间

## 7. 常见坑 & Debug 清单

### 7.1 模式坍塌（Mode Collapse）
- **现象**：G 只生成少数几种样本，忽略了数据分布的其他模式
- **原因**：G 发现某几个输出能稳定骗过 D，就"偷懒"只产出这几个
- **缓解**：Mini-batch discrimination、Unrolled GAN、多样性正则

### 7.2 训练不稳定
- **现象**：D 和 G 的 loss 剧烈震荡，不收敛
- **原因**：两个网络在做博弈，不像单目标优化那样有保证收敛的理论
- **缓解**：谱归一化（Spectral Normalization）、梯度惩罚（Gradient Penalty）、学习率调整

### 7.3 梯度消失（D 太强）
- **现象**：D 太强，G 完全学不动
- **原因**：D 把所有假样本判为 0，`log(1 - D(G(z)))` 梯度趋近零
- **缓解**：用非饱和损失（§4.4）、标签平滑、降低 D 的学习率

### 7.4 评估困难
- GAN 没有显式似然，不能直接算 `log p(x)`
- 常用指标：
  - **FID**（Frechet Inception Distance）：越低越好，衡量生成分布与真实分布的距离
  - **IS**（Inception Score）：越高越好，衡量生成样本的质量和多样性

## 8. 扩展与对比

### 8.1 主要变体

| 变体 | 核心改进 |
| --- | --- |
| DCGAN | 用 CNN 替代全连接，引入 BN、去掉池化 |
| WGAN | 用 Wasserstein 距离替代 JS 散度，训练更稳定 |
| WGAN-GP | WGAN + 梯度惩罚替代 weight clipping |
| cGAN | 条件生成：G 和 D 都接收条件 y |
| Pix2Pix | cGAN + L1 重构损失，图像到图像翻译 |
| CycleGAN | 无配对数据的双向图像翻译（cycle consistency） |
| StyleGAN | 风格映射网络 + 逐层注入，高质量人脸生成 |
| BigGAN | 大规模训练 + 类别条件，ImageNet 级别生成 |

### 8.2 WGAN 为什么更稳定（直觉）
- 原始 GAN 最小化 JSD：当 $p_{data}$ 和 $p_G$ 的支撑集不重叠时，JSD 恒为 $\log 2$（常数） → 梯度为零
- WGAN 用 Wasserstein 距离（推土机距离）：即使不重叠也能给出有意义的梯度
- 代价：D 需要满足 Lipschitz 约束（weight clipping 或梯度惩罚）

### 8.3 与其他生成模型对比

|  | GAN | VAE | Diffusion |
| --- | --- | --- | --- |
| 训练目标 | 对抗博弈（JS 散度） | ELBO（KL + 重构） | 去噪得分匹配 |
| 采样速度 | 快（单次前向） | 快（单次前向） | 慢（需迭代） |
| 样本质量 | 高（尖锐） | 偏模糊 | 高 |
| 多样性 | 易 mode collapse | 好 | 好 |
| 似然估计 | 无 | 有（ELBO 下界） | 有 |
| 训练稳定性 | 差 | 好 | 好 |
| 潜空间 | 无结构 | 有结构（正则化） | 无显式潜空间 |

- VAE 详细笔记：[models/VAE.md](VAE.md)
- Diffusion 详细笔记：[models/Diffusion.md](Diffusion.md)

## 9. 参考
- Goodfellow et al., 2014. *Generative Adversarial Nets*
- Radford et al., 2016. *Unsupervised Representation Learning with DCGANs*
- Arjovsky et al., 2017. *Wasserstein GAN*
- Gulrajani et al., 2017. *Improved Training of WGANs* (WGAN-GP)
- Karras et al., 2019/2020. *StyleGAN / StyleGAN2*
- Mirza & Osindero, 2014. *Conditional Generative Adversarial Nets*
