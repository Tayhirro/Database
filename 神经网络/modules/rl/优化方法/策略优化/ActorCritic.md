---
title: Actor-Critic
date: "2026-03-07"
categories:
  - 神经网络
description: Actor 负责策略，Critic 负责价值评估；二者配合降低策略梯度方差并提高样本效率。
---
# Actor-Critic

## 1. 一句话
- Actor-Critic 把“决策”和“评估”拆开：Actor 产动作分布，Critic 评估状态或动作价值。

## 2. 定义 / 公式（最常用那版）
- Actor 更新（示意）：

$$
L_{actor}=-\mathbb{E}[\log\pi_\theta(a_t|s_t)\,\hat A_t]
$$

- Critic 更新（常见 MSE）：

$$
L_{critic}=\mathbb{E}\left[(V_\phi(s_t)-\hat V_t)^2\right]
$$

- 常配熵正则：

$$
L = L_{actor} + c_v L_{critic} - c_e\,\mathcal{H}(\pi_\theta)
$$

## 3. 设计
- actor直接输出概率
- 优势函数降低方差
- V更新 配合TD 有偏加速

- V pi 网络  -->actor critic loss

## 4. 一个关键细节：在线更新导致梯度有偏

策略梯度公式要求轨迹 $\tau$ 来自**当前策略** $\pi_\theta$：

$$
\nabla J(\theta) = \mathbb{E}_{\tau \sim \pi_\theta} \left[ G(\tau) \nabla_\theta \log \pi_\theta(\tau) \right]
$$

如果每步都更新策略（在线更新），后面采到的 $r_{t+1}, r_{t+2}, ...$ 是在新策略 $\pi_{\theta'}$ 下产生的，不是 $\pi_\theta$。于是梯度变成了：

$$
\mathbb{E}_{\tau \sim \text{mixed policy}} \left[ G \nabla_\theta \log \pi_\theta \right]
$$

而不是 $\mathbb{E}_{\tau \sim \pi_\theta}[\cdot]$，这就产生了偏差。

### 数值例子

两状态 MDP，$S_1$ 选 $a$ 得 +1，$S_2$ 选 $b$ 得 +1，否则 0。初始策略 $\pi_0$ 在 $S_1$ 选 $a$ 概率 0.5。

- 第 1 步：$S_1$，$\pi_0$ 选了 $a$，得 +1，更新 $\pi_0 \to \pi_1$（$a$ 概率升到 0.6）
- 第 2 步：$S_2$，$\pi_1$ 选了 $b$，得 +1，更新 $\pi_1 \to \pi_2$
- 第 3 步：$S_1$，$\pi_2$ 选 $a$ 概率 0.6 继续选 $a$，得 +1，更新

到第 3 步时，$G_1 = r_1 + \gamma r_2 + \dots$ 中的 $r_2$ 是在 $\pi_1$ 下采的，不是 $\pi_0$。如果用 $G_1$ 更新 $\pi_0$，$\pi_0$ 会以为"选了 $a$ 之后后续回报很高"，但这个高回报部分来自之后策略的改进，不是 $\pi_0$ 自己的功劳。这就是**策略改进偏差**（非平稳性偏差）。

### 实际做法

避免这个偏差的办法：采样时固定策略，收集一批完整轨迹后再统一更新，然后丢弃旧数据。A2C、PPO 都是这个模式——收集一个 batch → 更新 → 丢弃 → 重新采样。

## 6. 常用变体 / 记号差异
- On-policy：A2C/A3C、PPO。
- Off-policy：DDPG、TD3、SAC（用 replay buffer）。
- Critic 可估计 `V(s)` 或 `Q(s,a)`。

## 7. 在哪些模型里出现
- PPO、TRPO、A2C/A3C、SAC、DDPG、TD3 都是 Actor-Critic 框架变体。

## 8. 速查
- 关键词：actor、critic、advantage、entropy、value loss。
- 常见坑：
  - actor 学太快，critic 跟不上，优势估计失真
  - value loss 权重过大，策略更新被淹没
  - 忘记对回报做尺度控制（如 reward normalization）
