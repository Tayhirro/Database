---
title: 强化学习算法（PPO / GRPO）
date: "2026-03-07"
categories:
  - 神经网络
description: 强化学习算法卡片入口。modules 放共用理论，models/rl 放具体算法。
---
# 强化学习算法（PPO / GRPO）

> `modules/` 放共用理论（MDP、价值函数、策略梯度、Actor-Critic、GAE）；这里放算法卡片。

## 入口
- PPO：[PPO.md](PPO.md)
- GRPO：[GRPO.md](GRPO.md)

## 速查：先学顺序
- 先补底座：`MDP -> Value Function -> Policy Gradient -> Actor-Critic -> GAE`
- 再看 PPO（最常见稳定基线）
- 最后看 GRPO（群体相对优势，常见于大模型后训练语境）
