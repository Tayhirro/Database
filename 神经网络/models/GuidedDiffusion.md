---
title: Guided Diffusion（Classifier Guidance / CFG / Q-guidance）
date: "2026-03-25"
categories:
  - 神经网络
description: 在扩散采样过程中注入额外引导信号，让生成结果更符合类别、文本、奖励或 Q 值等目标。
---
# Guided Diffusion（Classifier Guidance / CFG / Q-guidance）

## 1. 一句话
- Guided Diffusion 的核心不是改掉 diffusion 本身，而是在“每一步反向去噪”时额外加一个引导信号，让采样朝你想要的方向偏。

## 2. 目标（解决什么问题）
- 普通 [Diffusion.md](Diffusion.md) 擅长学“数据分布长什么样”，但它默认只会采样“像数据”的样本，不一定会采到“最符合条件 / 最高奖励 / 最高 Q 值”的样本。
- guided diffusion 就是在保留这个生成先验的前提下，再加一个“控制力”：
  - 图像里：更符合类别、文本 prompt、参考图、风格约束
  - 决策 / 强化学习里：更符合奖励函数、约束条件、价值函数 `Q(s,a)`
- 所以它很适合那种“先学一个强大的 generative prior，再在采样时做定向控制”的场景。

## 3. 核心直觉（先别陷进公式）

### 3.1 没有 guidance 时
- 基础 diffusion 学的是：给定当前带噪样本 `x_t` 和时间步 `t`，应该往哪个方向去噪。
- 这相当于学了一个“回到数据流形”的方向场。

### 3.2 有 guidance 时
- guided diffusion 会在原本的去噪方向上，再叠加一个“朝目标走”的方向。
- 可以粗略理解成：

$$
\text{guided step}
\approx
\text{base denoise step} + w \cdot \text{guidance signal}
$$

- 其中：
  - `base denoise step` 保证结果仍然“像数据”
  - `guidance signal` 保证结果更“符合要求”
  - `w` 控制你到底多强地听这个引导

直觉上它像两股力：
- 一股力把你拉回“合理样本区域”
- 一股力把你推向“你真正想要的目标区域”

## 4. 常见三种 guidance

### 4.1 Classifier Guidance
- 训练一个额外分类器 `p_\phi(c|x_t)`，采样时用它的梯度告诉 diffusion：“往更像类别 `c` 的方向走。”
- 优点：
  - 控制逻辑清楚
  - 训练好的 diffusion 可以后接不同 classifier
- 缺点：
  - 还得单独训练一个 classifier
  - classifier 在高噪声步上不稳定时，guidance 会把采样带偏

### 4.2 Classifier-Free Guidance（CFG）
- 不再单独训练 classifier，而是让同一个模型同时学：
  - 条件生成：`p(x|c)`
  - 无条件生成：`p(x)`
- 采样时把两个预测线性组合：

$$
\hat{\epsilon}_{cfg}
=
\hat{\epsilon}_{uncond}
+
w\left(\hat{\epsilon}_{cond} - \hat{\epsilon}_{uncond}\right)
$$

- 这就是现在文本到图像里最常见的 guidance 方式。
- 直觉上：
  - `\hat{\epsilon}_{uncond}` 给“通用自然图像先验”
  - `\hat{\epsilon}_{cond} - \hat{\epsilon}_{uncond}` 给“朝 prompt 靠近的额外方向”

### 4.3 Reward / Q Guidance
- 把“类别概率”换成“奖励”或“价值函数 `Q`”。
- 这时 guidance 问题就变成：
  - 不是问“这张图像不像猫”
  - 而是问“这个动作 / 轨迹值不值得做”
- 于是 diffusion 采样时就会被高 reward、高 `Q` 的方向引导。

## 5. 它和 DQN 到底是什么关系

> [!note]
> 如果你说的“包含 DQN 的 diffusion”，更准确的理解通常是：**用 DQN 风格的 Q 值思想去引导 diffusion**。在连续动作场景里，常见做法往往不是“经典离散动作 DQN 原样照搬”，而是用 `Q(s,a)` critic 或 Q-learning loss 来引导 diffusion policy。

### 5.1 DQN 在做什么
- [rl/DQN.md](rl/DQN.md) 学的是动作价值函数：

$$
Q(s,a)
$$

- 给定状态 `s`，它评估每个动作 `a` 有多好，然后直接选

$$
a^* = \arg\max_a Q(s,a)
$$

- 这很适合**离散动作空间**，因为动作个数有限，可以枚举。

### 5.2 Guided Diffusion 在做什么
- diffusion 不一定直接枚举动作，而是：
  - 从噪声开始采样动作 / 轨迹
  - 每一步去噪时，参考 `Q` 或 reward 的梯度，把采样往高价值区域推
- 所以 guided diffusion 更像是：
  - 用 diffusion 表示复杂、多峰的动作分布
  - 再用 DQN 风格的价值信号做“软引导”

### 5.3 为什么要这么做
- 在 offline RL 或多峰行为数据里，行为策略常常不是单峰高斯，可能有多个可行动作模式。
- 这时直接用简单 policy 去拟合，会把几个模式平均掉，得到一个“谁都不像”的动作。
- diffusion 的优势是：
  - 更容易表示多模态动作分布
  - 更容易保持“别跑出数据分布太远”
- `Q` guidance 的优势是：
  - 不只模仿数据，还能偏向更优动作

一句话：
- **DQN 提供“哪个好”的信号**
- **Diffusion 提供“怎么生成复杂动作分布”的机制**

## 6. 在强化学习里最常见的两条路线

### 6.1 Diffuser：trajectory-level guided diffusion
- 代表思路见 `Diffuser`
- 把整条轨迹 `(s_0, a_0, s_1, a_1, ..., s_H, a_H)` 当成 diffusion 的生成对象
- 先学“合理轨迹分布”
- 采样时再用 reward / return model 的梯度去引导整条轨迹

这个路线更像：
- diffusion 是一个**轨迹生成器 / planner**
- guidance 是一个**规划目标**

### 6.2 Diffusion-QL：action-level Q-guided diffusion
- 代表思路见 `Diffusion-QL`
- 不扩散整条轨迹，而是把 policy 本身建成条件 diffusion：

$$
\pi_\theta(a \mid s)
$$

- 训练目标可以理解成两部分：
  1. diffusion behavior cloning loss：学会生成接近数据集的动作
  2. maximizing Q term：鼓励生成更高 `Q(s,a)` 的动作

- 粗略写成：

$$
L_{\pi}
\approx
L_{\text{diffusion-BC}} - \alpha \,\mathbb{E}[Q(s,a)]
$$

这个路线更像：
- diffusion 是一个**高表达能力 policy**
- `Q` 是一个**把 policy 往更优动作拉的 critic**

## 7. 采样 / 训练流程怎么理解

### 7.1 普通 CFG 那条线

```text
x_T ~ N(0, I)
  -> 预测无条件噪声 ε_uncond
  -> 预测有条件噪声 ε_cond
  -> 线性组合成 ε_cfg
  -> 更新到 x_{t-1}
  -> 重复直到 x_0
```

### 7.2 Q-guided diffusion 那条线

```text
给定状态 s
  -> 从噪声采样候选动作 / 轨迹
  -> diffusion 负责每一步去噪
  -> Q / reward 提供“朝高价值方向走”的引导
  -> 最终得到 a_0 或整条轨迹
```

### 7.3 和“直接 argmax Q”有什么不同
- DQN：直接在动作集合里选 `argmax_a Q(s,a)`
- guided diffusion：先生成，再引导，再细化

所以 guided diffusion 更像“连续优化 / 逐步修正”，而不是“一步挑最大值”。

## 8. 常见坑 & Debug 清单
- guidance scale 开太大：结果更听话，但多样性下降，容易失真或 mode collapse
- classifier / reward / Q 模型不准：引导信号本身错了，diffusion 会被系统性带偏
- RL 里 Q 值过估计：会把采样推到 dataset 外的坏区域
- 只强调 Q，不保留 behavior prior：offline RL 很容易 OOD action 爆炸
- 采样步数多：比普通单步 policy 慢，部署成本更高
- 多峰数据上如果 diffusion 能力不够：guidance 再强也只是把一个差 policy 推来推去

## 9. 和相关模型的区别

|  | 普通 Diffusion | Guided Diffusion | DQN |
| --- | --- | --- | --- |
| 核心目标 | 学数据分布 | 学数据分布 + 定向控制 | 学 `Q(s,a)` |
| 输出对象 | 样本 / 动作 / 轨迹 | 样本 / 动作 / 轨迹 | 动作价值 |
| 控制方式 | 无或弱条件 | 采样时加 guidance | 直接 `argmax Q` |
| 适合动作空间 | 连续 / 高维 / 多模态很强 | 连续 / 高维 / 多模态很强 | 离散动作最自然 |
| RL 里的角色 | 生成 policy / 轨迹先验 | 可控 policy / planner | critic / value-based policy |

## 10. 关联笔记
- 扩散基础：[Diffusion.md](Diffusion.md)
- 价值函数：[../modules/rl/ValueFunction.md](../modules/rl/ValueFunction.md)
- MDP：[../modules/rl/MarkovDecisionProcess.md](../modules/rl/MarkovDecisionProcess.md)
- DQN：[rl/DQN.md](rl/DQN.md)
- PPO：[rl/PPO.md](rl/PPO.md)

## 11. 参考
- Ho et al., 2020. *Denoising Diffusion Probabilistic Models*
- Dhariwal & Nichol, 2021. *Diffusion Models Beat GANs on Image Synthesis*
- Ho & Salimans, 2022. *Classifier-Free Diffusion Guidance*
- Janner et al., 2022. *Planning with Diffusion for Flexible Behavior Synthesis*
- Wang et al., 2023. *Diffusion Policies as an Expressive Policy Class for Offline Reinforcement Learning*
