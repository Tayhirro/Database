---
title: 时序差分（Temporal Difference）
date: "2026-05-14"
categories:
  - 神经网络
  - 强化学习
description: 用一步奖励和下一个状态的估计更新价值函数，有偏但方差小。
---

# 时序差分（Temporal Difference）

## 1. 一句话
- 用一步奖励 $r_{t+1}$ 和下一个状态的估计 $\gamma V(s_{t+1})$ 更新价值函数。

## 2. 先明确：我们在估计什么

$G_t$ 是一条轨迹的累积回报，它是**随机变量**——每次采出来的值都不同，没法直接"估计"一个随机变量本身。

我们能估计的是它的**期望**：$V(s) = \mathbb{E}[G_t \mid s_t = s]$。期望是一个确定的函数，不是随机变量。MC 和 TD 都是估计这个期望的方法，只是逼近方式不同。

## 3. 定义

**TD目标**：
$$\hat{v}_t = r_{t+1} + \gamma V(s_{t+1})$$

**TD误差**：
$$\delta_t = r_{t+1} + \gamma V(s_{t+1}) - V(s_t)$$

**更新规则**：
$$V(s_t) \leftarrow V(s_t) + \alpha \delta_t$$

## 4. 与贝尔曼期望方程的联系

**贝尔曼期望方程**：
$$Q(s,a) = \mathbb{E}[r_{t+1} + \gamma Q(s_{t+1}, a_{t+1}) | s_t=s, a_t=a]$$

**近似**：
用 $V(s_{t+1})$ 代替 $Q(s_{t+1}, a_{t+1})$（因为 $a_{t+1}$ 还未执行）：
$$Q(s,a) \approx r_{t+1} + \gamma V(s_{t+1})$$

其中 $V(s_{t+1}) = \sum_{a'} \pi(a'|s_{t+1}) Q(s_{t+1}, a')$ 是对所有可能动作的平均。

**TD**：
用 $r_{t+1} + \gamma V(s_{t+1})$ 作为 $Q(s,a)$ 的估计，是贝尔曼方程的有偏近似。

## 5. 特点

- 有偏估计：因为用 $V(s_{t+1})$ 近似，而 $V$ 本身不准确
- 方差小：只用一步奖励，噪声小
- 不需要等episode结束就能更新（online learning）

## 6. 变体

- TD(0)：只看一步
- TD($\lambda$)：看多步，用 $\lambda$ 控制权重
- Q-learning：用TD更新Q

## 7. 相关模块

- [ValueFunction.md](../价值函数/ValueFunction.md)：价值函数定义
- [GeneralizedAdvantageEstimation.md](GeneralizedAdvantageEstimation.md)：GAE
