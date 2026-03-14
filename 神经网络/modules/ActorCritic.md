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

## 3. 直觉（为什么这么设计）
- 纯策略梯度方差大；引入 Critic 作为 baseline 后，梯度更稳。
- 纯价值法在连续动作不方便；Actor 直接输出连续动作分布更自然。

## 4. 常用变体 / 记号差异
- On-policy：A2C/A3C、PPO。
- Off-policy：DDPG、TD3、SAC（用 replay buffer）。
- Critic 可估计 `V(s)` 或 `Q(s,a)`。

## 5. 在哪些模型里出现
- PPO、TRPO、A2C/A3C、SAC、DDPG、TD3 都是 Actor-Critic 框架变体。

## 6. 速查
- 关键词：actor、critic、advantage、entropy、value loss。
- 常见坑：
  - actor 学太快，critic 跟不上，优势估计失真
  - value loss 权重过大，策略更新被淹没
  - 忘记对回报做尺度控制（如 reward normalization）
