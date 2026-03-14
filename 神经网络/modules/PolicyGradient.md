---
title: 策略梯度（Policy Gradient）
date: "2026-03-07"
categories:
  - 神经网络
description: 直接优化参数化策略的目标函数，核心是 log-derivative trick 与 advantage 加权更新。
---
# 策略梯度（Policy Gradient）

## 1. 一句话
- 策略梯度方法直接优化策略 `\pi_\theta(a|s)`，而不是先学 Q 再贪心。

## 2. 定义 / 公式（最常用那版）
- 目标函数：

$$
J(\theta)=\mathbb{E}_{\tau\sim\pi_\theta}[G(\tau)]
$$

- 策略梯度定理（常用形式）：

$$
\nabla_\theta J(\theta)=\mathbb{E}_{\pi_\theta}\left[\nabla_\theta\log\pi_\theta(a_t|s_t)\,A_t\right]
$$

- 其中 `A_t` 可用回报、TD 残差或 GAE 估计。

## 3. 直觉（为什么这么设计）
- 如果某动作带来更高优势，就提升该动作概率；反之降低概率。
- `log π` 形式让采样策略也能做梯度估计（score function trick）。

## 4. 常用变体 / 记号差异
- REINFORCE：直接用整段回报，方差大。
- Actor-Critic：用价值网络估计 baseline，方差更低。
- PPO/TRPO：给更新步长加约束，避免策略崩掉。

## 5. 在哪些模型里出现
- REINFORCE、A2C/A3C、TRPO、PPO、GRPO、RLHF 的策略优化部分。

## 6. 速查
- 关键词：`logprob`、advantage、baseline、entropy bonus。
- 常见坑：
  - 旧策略与新策略偏移过大导致训练不稳定
  - 仅优化策略损失，不管 value/entropy，容易退化
  - 采样批量太小，梯度方差太高
