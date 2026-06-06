---
title: Deep Q-Network (DQN)
date: "2026-05-18"
categories:
  - 神经网络
description: 用神经网络近似 Q*，引入目标网络和经验回放解决不稳定问题。
---
# Deep Q-Network (DQN)

## 1. 一句话
DQN = Q-learning + 神经网络 + 目标网络 + 经验回放。

## 2. 三个关键改进

### 经验回放（Replay Buffer）
- 把转移 (s, a, r, s') 存进 buffer，训练时随机采样
- 打破样本间的时序相关性，稳定训练

### 目标网络（Target Network）
- 维护一份冻结的 Q_target，定期从 Q_current 复制
- TD target 用 Q_target 算，减少 bootstrap 的目标抖动

### 更新规则

$$
L = \mathbb{E}_{(s,a,r,s') \sim D} \left[ \left( r + \gamma \max_{a'} Q_{\text{target}}(s', a') - Q_{\theta}(s,a) \right)^2 \right]
$$

## 3. 常见改进变体

| 变体 | 改动 |
|------|------|
| Double DQN | 用 Q_current 选动作，Q_target 估值，缓解高估 |
| Dueling DQN | Q = V + A，分离状态价值和动作优势 |
| PER | 按 TD 误差大小采样，优先学"意外"的样本 |

## 4. 参考
- [Q-learning.md](Q-learning.md)：Q-learning 基础
- [TemporalDifference.md](../../价值估计/TemporalDifference.md)：TD 更新
