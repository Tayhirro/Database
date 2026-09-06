---
title: JEPA（Joint Embedding Predictive Architecture）
date: "2026-07-11"
categories:
  - 神经网络
description: 在表征空间预测被遮挡或未来部分的自监督学习框架，强调学习语义级世界模型，而不是重建像素或依赖负样本对比。
---
# JEPA（Joint Embedding Predictive Architecture）

## 1. 一句话

JEPA 是一种**预测式自监督表征学习框架**：给模型一部分上下文，让它在 embedding 空间预测另一部分目标的表示。

它的核心不是生成图片本身，而是学习一个内部表征，使模型能回答：

$$
\text{给定已知部分，未知部分在语义表征上应该是什么？}
$$

所以 JEPA 更适合放在：

```text
modules/representation/JEPA.md
```

而不是 `models/生成模型.md`。因为它通常不直接建模 $p(x)$，也不从噪声采样生成完整样本；它更像是一个可复用的**表征学习 / 世界模型预训练范式**。

## 2. 核心结构

设输入样本为 $x$。JEPA 会把它切成两部分：

- context：模型可以看到的部分，记作 $x_{\text{ctx}}$
- target：模型需要预测的部分，记作 $x_{\text{tar}}$

常见结构是：

$$
h_{\text{ctx}}=f_\theta(x_{\text{ctx}})
$$

$$
h_{\text{tar}}=\operatorname{sg}(f_{\bar\theta}(x_{\text{tar}}))
$$

$$
\hat h_{\text{tar}}=g_\theta(h_{\text{ctx}}, m_{\text{tar}})
$$

其中：

- $f_\theta$：context encoder，把可见部分编码成表征
- $f_{\bar\theta}$：target encoder，常用 stop-gradient 或 EMA 形式提供稳定目标
- $g_\theta$：predictor，根据上下文表征和目标位置预测目标表征
- $m_{\text{tar}}$：目标区域的位置或 mask 信息

训练目标一般是让预测表征接近目标表征：

$$
\mathcal L
=
\left\|
\hat h_{\text{tar}}-h_{\text{tar}}
\right\|^2
$$

关键点是：预测发生在**表征空间**，不是像素空间。

### 2.1 Representation Collapse 与三种不对称性

因为 JEPA 不用显式负样本，也不直接重建像素，所以会遇到一个典型风险：**Representation Collapse**。

所谓 representation collapse，是指 encoder 对所有输入都输出几乎相同的常数向量：

$$
f(x)\approx c
$$

如果 online branch 和 target branch 都一起被梯度更新，那么一个坏的平凡解是：

$$
g_\phi(f_\theta(x_{\text{ctx}}))
\approx
f_{\bar\theta}(x_{\text{tar}})
\approx
c
$$

这时 MSE 可以很小，但表征没有任何区分能力。模型不是学会了世界结构，而是学会了“所有东西都输出同一个 embedding”。

I-JEPA / V-JEPA 主要用三种不对称性缓解这个问题：

```text
online / context branch:
x_ctx -> f_theta -> g_phi -> predicted target representation

target branch:
x_tar -> f_bar_theta -> stop-gradient -> target representation
```

第一，**target branch 使用 stop-gradient**：

$$
h_{\text{tar}}=\operatorname{sg}(f_{\bar\theta}(x_{\text{tar}}))
$$

这表示 target representation 只作为训练目标，不让 loss 的梯度反向更新 target encoder。梯度主要更新 context encoder 和 predictor：

$$
f_\theta,\quad g_\phi
$$

而不是直接更新：

$$
f_{\bar\theta}
$$

这样 target branch 不会为了配合 online branch 的当前输出而随意移动，它更像一个固定的学习目标。

第二，**target encoder 由 context encoder 的 EMA 更新**：

$$
\bar\theta
\leftarrow
m\bar\theta+(1-m)\theta
$$

其中 $m$ 通常接近 1。直觉上：

```text
context encoder：学生，正常反向传播，变化快
target encoder：老师，只做滑动平均，变化慢
```

这让 target representation 更稳定，避免目标每一步都剧烈变化。online branch 要追一个慢变化的目标，而不是追一个和自己同时乱动的目标。

第三，**只有 context branch 额外经过 predictor**：

$$
\hat h_{\text{tar}}=g_\phi(f_\theta(x_{\text{ctx}}),m_{\text{tar}})
$$

target branch 不经过 predictor：

$$
h_{\text{tar}}=\operatorname{sg}(f_{\bar\theta}(x_{\text{tar}}))
$$

这个 predictor 的作用不是“多加一层网络”这么简单，而是把两个任务分开：

- encoder 学通用表征；
- predictor 学“从上下文表征到目标区域表征”的转换。

因为 $x_{\text{ctx}}$ 和 $x_{\text{tar}}$ 本来就不是同一个输入，$f_\theta(x_{\text{ctx}})$ 不应该被迫直接等于 $f_{\bar\theta}(x_{\text{tar}})$。Predictor 负责根据上下文和目标位置，预测目标区域应该具有的表征。

所以 JEPA 的训练不是对称地让两个 encoder 互相追逐，而是：

$$
\boxed{
\text{稳定 target 表征作为目标，让 context encoder + predictor 去预测它。}
}
$$

这三种不对称性共同打破了“两个分支一起塌到常数”的简单路径。

## 3. 为什么不是直接重建像素

像 MAE 这类 masked image modeling 方法，会让模型重建被遮挡区域的像素。这个目标有价值，但也容易把模型的一部分能力花在低层细节上，比如纹理、颜色、边缘和局部统计。

JEPA 选择预测 embedding，是为了让目标更偏向语义和结构：

$$
\text{像素重建：预测具体长什么样}
$$

$$
\text{JEPA：预测语义上应该是什么}
$$

例如遮住一张图中的动物头部，像素重建需要猜具体毛发、光照和纹理；JEPA 更关心这个位置在表征上是否符合“这是一只猫的头部”这样的高层结构。

## 4. 和对比学习的区别

对比学习通常通过正负样本来学表征：

$$
\text{拉近正样本，推远负样本}
$$

例如 SimCLR、MoCo、CLIP 都可以放在这个思路下理解。

JEPA 不依赖显式负样本，而是通过预测目标区域的 latent representation 来学习：

$$
\text{用上下文预测目标表征}
$$

所以它不是在问：

> 这两个样本是不是更相似？

而是在问：

> 给定当前上下文，缺失部分的合理表征应该是什么？

这使 JEPA 更接近“世界模型”的思想：模型通过预测不可见部分或未来状态，学习环境中稳定、可迁移的结构。

## 5. 和生成模型的关系

JEPA 和生成模型有共同的第一性原理：都在学习数据分布背后的结构。但它们的优化对象不同。

生成模型通常要学：

$$
p_\theta(x)
$$

或者学习从源分布到数据分布的运输：

$$
z\sim p(z)
\quad\longrightarrow\quad
x\sim p_{\text{data}}(x)
$$

JEPA 通常不要求模型输出完整样本，也不直接最大化图像似然。它学的是：

$$
p(\text{目标表征}\mid \text{上下文表征})
$$

也就是在表征空间里建立预测关系。

因此可以这样区分：

| 方法 | 训练信号 | 学到什么 |
| --- | --- | --- |
| 生成模型 | 像素/样本/score/分布级监督 | 如何生成或搬运到数据分布 |
| 对比学习 | 正负样本相似度 | 哪些样本应该靠近或远离 |
| JEPA | 上下文到目标表征的预测 | 数据内部结构和语义级可预测性 |

所以 JEPA 可以为生成模型、规划模型、视频理解、多模态模型提供强表征，但它本身不一定是一个完整的生成模型。

## 6. 和 VAE / Diffusion 的直觉连接

在 VAE 里，潜变量 $z$ 要保留关于样本 $x$ 的信息，使 decoder 能根据 $z$ 生成或重建：

$$
I(X;Z)>0
$$

在 Diffusion 里，中间态 $x_t$ 保留部分 $x_0$ 信息，使模型能学习局部去噪方向或 score。

JEPA 的对应说法是：context representation 必须保留足够信息，使 predictor 能约束 target representation：

$$
I(H_{\text{ctx}};H_{\text{tar}})>0
$$

如果上下文表征和目标表征完全无关，预测只能退化到目标表征的平均值；如果二者存在稳定统计关系，模型就能学到“哪些上下文对应哪些目标结构”。

这和生成模型里“避免随机硬配对导致条件均值坍缩”的逻辑是一致的：

$$
\boxed{
\text{训练信号必须让输入和目标之间存在可识别的统计关系。}
}
$$

## 7. 常见变体

- I-JEPA：面向图像，通常通过遮挡图像块，让模型根据可见区域预测目标区域的表征。
- V-JEPA：面向视频，预测时空中被遮挡或未来片段的表征，更接近学习物理世界和动态变化。
- 条件 JEPA / 多模态 JEPA：把文本、动作、音频或其他模态作为上下文，预测目标模态或未来状态的表征。

## 8. 常见误区

- JEPA 不是简单的 autoencoder：它不以重建完整输入为核心，而是预测目标部分的表征。
- JEPA 不是标准生成模型：它通常不直接从噪声采样生成 $x$。
- JEPA 不是对比学习：它不需要显式构造负样本来推远。
- JEPA 不保证自动学到人类可解释语义：它学习的是对预测目标有用的结构，语义是否清晰取决于数据、mask 策略、网络结构和训练目标。

相关页：
- 对比学习：[ContrastiveLearning.md](ContrastiveLearning.md)
- 度量学习：[MetricLearning.md](MetricLearning.md)
- 生成模型：[../../models/生成模型.md](../../models/生成模型.md)
