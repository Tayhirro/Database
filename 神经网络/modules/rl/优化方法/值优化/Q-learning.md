---
title: Q-learning
date: "2026-05-18"
categories:
  - 神经网络
description: 基于贝尔曼最优方程的无模型值迭代算法，用 TD 更新 Q 并隐含优化策略。
---
# Q-learning

## 1. 一句话
Q-learning 用 TD 误差更新 Q，逼近最优 Q*，策略直接取 argmax Q。

## 2. 更新规则

$$
Q(s_t, a_t) \leftarrow Q(s_t, a_t) + \alpha \left[ r_t + \gamma \max_a Q(s_{t+1}, a) - Q(s_t, a_t) \right]
$$

其中 r + γ max Q(s', a') 是 TD target，与 target 的差是 TD 误差。

## 3. 设计逻辑

Q-learning 是在无模型条件下近似 Bellman 最优方程：
$$
Q^{*}(s,a) = \mathbb{E}_{s'} \left[ r + \gamma \max_{a'} Q^{*}(s',a') \mid s,a \right]
$$

- 右边用单步采样 r + γ max Q(s',a') 代替期望 → 有偏但方差小
- off-policy策略 
- 策略梯度不行：隐式策略由 Q 定义：$\pi_{\theta}(a|s) = \mathbb{1}[a = \arg\max_b Q_{\theta}(s,b)]$ 指示函数输入为Q
	- argmaxbQ(s,b) / Q(s,b) 无法求解

## 4. 关键点
- 行为策略和目标策略分离 → off-policy
- 更新目标有 max → 会高估 Q（overestimation bias）
- 表格型，状态动作空间有限时保证收敛


## 5. 参考
- [BellmanEquation.md](../../价值函数/BellmanEquation.md)：Q* 最优方程
- [TemporalDifference.md](../../价值估计/TemporalDifference.md)：TD 更新
