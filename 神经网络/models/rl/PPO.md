---
title: PPO（Proximal Policy Optimization）
date: "2026-03-07"
categories:
  - 神经网络
description: 在策略梯度中通过 clipping 或 KL 约束控制新旧策略偏移，兼顾稳定性与实现简洁度。
---
# PPO（Proximal Policy Optimization）

## 1. 一句话
- PPO 是最常用的 on-policy RL 算法之一：用“截断目标”限制策略更新步子，避免一次更新把策略改崩。

## 2. 目标（解决什么问题）
- 纯策略梯度更新不稳定，容易 policy collapse。
- TRPO 稳但实现复杂；PPO 用近似方式实现“别走太远”，工程更友好。

## 3. 核心目标函数
- 概率比：

$$
r_t(\theta)=\frac{\pi_\theta(a_t|s_t)}{\pi_{\theta_{old}}(a_t|s_t)}
$$

- clipped objective：

$$
L^{clip}(\theta)=\mathbb{E}\left[\min\left(r_t(\theta)\hat A_t,\;\text{clip}(r_t(\theta),1-\epsilon,1+\epsilon)\hat A_t\right)\right]
$$

- 常见总损失（最小化形式）：

$$
L = -L^{clip} + c_v L_{value} - c_e\,\mathcal{H}
$$

## 4. 训练流程（简版）
1. 用旧策略采样 rollout。
2. 用 Critic + GAE 计算 `\hat A_t` 与 value target。
3. 对同一批数据做多轮 mini-batch 更新（epoch）。
4. 更新后将当前策略设为新旧策略，继续采样。

## 5. 为什么有效（直觉）
- 当优势为正时，希望 `r_t` 增大；但超过 `1+\epsilon` 后不再继续鼓励。
- 当优势为负时，希望 `r_t` 变小；但低于 `1-\epsilon` 后也不再继续惩罚。
- 等价于“有方向地更新，但步长受限”。

## 6. 常见超参
- `clip_range`：常见 `0.1 ~ 0.3`
- `gamma`：常见 `0.99`
- `gae_lambda`：常见 `0.95`
- `entropy_coef`：探索强度
- `value_coef`：value loss 权重

## 7. 常见坑
- 同一批数据训练 epoch 过多，导致过拟合 old policy 数据。
- advantage 不归一化，更新方向噪声大。
- value 函数学不稳，导致 policy 被错误优势信号带偏。

## 8. 关联模块
- [../../modules/rl/MarkovDecisionProcess.md](../../modules/rl/MarkovDecisionProcess.md)
- [../../modules/rl/PolicyGradient.md](../../modules/rl/PolicyGradient.md)
- [../../modules/rl/ActorCritic.md](../../modules/rl/ActorCritic.md)
- [../../modules/rl/GeneralizedAdvantageEstimation.md](../../modules/rl/GeneralizedAdvantageEstimation.md)
