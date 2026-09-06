---
title: 蒙特卡洛方法（Monte Carlo）
date: "2026-05-14"
categories:
  - 神经网络
  - 强化学习
description: 用完整轨迹的累积回报估计价值函数，无偏但方差大。
---

# 蒙特卡洛方法（Monte Carlo）

## 1. 一句话
- 用完整轨迹的累积回报 $G_t$ 估计 $Q(s,a)$ 或 $V(s)$。

## 2. 先明确：估计的是期望，不是随机变量本身

$G_t$ 是随机变量，每次采样值不同。我们估计的是它的**期望**：$Q(s,a) = \mathbb{E}[G_t \mid s_t=s, a_t=a]$。MC 用单次采样的 $G_t$ 作为这个期望的近似——单次有噪声，但平均下来无偏。

## 3. 定义

**估计Q**：
$$Q(s,a) \approx G_t = \sum_{k=0}^{\infty} \gamma^k r_{t+k+1}$$

**估计V**：
$$V(s) \approx G_t$$

## 3. 与贝尔曼期望方程的联系

**贝尔曼期望方程**（递推形式）：
$$Q(s,a) = \mathbb{E}[r_{t+1} + \gamma Q(s_{t+1}, a_{t+1}) | s_t=s, a_t=a]$$

**展开**：
$$Q(s,a) = \mathbb{E}[r_{t+1} + \gamma r_{t+2} + \gamma^2 r_{t+3} + \cdots | s_t=s, a_t=a]$$

**蒙特卡洛**：
用完整轨迹的累积回报 $G_t$ 作为 $Q(s,a)$ 的采样估计，是贝尔曼方程展开形式的无偏估计。

## 5. 特点

- 无偏估计：$\mathbb{E}[G_t | s_t=s, a_t=a] = Q(s,a)$
- 方差大：因为用完整轨迹，噪声累积
- 需要等episode结束才能更新

## 6. 应用

- REINFORCE算法：用蒙特卡洛估计Q，配合策略梯度训练π

## 5. 相关模块

- [ValueFunction.md](../价值函数/ValueFunction.md)：价值函数定义
- [[PolicyGradient]]：策略梯度方法
