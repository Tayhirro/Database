---
title: 策略梯度（Policy Gradient）
date: "2026-03-07"
categories:
  - 神经网络
description: 直接优化参数化策略的目标函数，核心是 log-derivative trick 与 advantage 加权更新。
---
# 策略梯度（Policy Gradient）

## 1. 一句话
- 策略梯度方法直接优化策略 $\pi_\theta(a|s)$

## 2. 定义 / 公式
- 目标函数：

$$
J(\theta)=\mathbb{E}_{\tau\sim\pi_\theta}[G(\tau)]
$$

- 其中轨迹 $\tau = (s_0, a_0, r_1, s_1, a_1, ...)$，$G(\tau)$ 是累积回报。

### 推导策略梯度

**Step 1**：写成积分形式

$$
J(\theta) = \int G(\tau) \pi_\theta(\tau) d\tau
$$

**Step 2**：对θ求梯度

$$
\nabla_\theta J(\theta) = \int G(\tau) \nabla_\theta \pi_\theta(\tau) d\tau
$$

**Step 3**：用log导数技巧

$$
\nabla_\theta \pi_\theta(\tau) = \pi_\theta(\tau) \nabla_\theta \log \pi_\theta(\tau)
$$

代入得：
$$
\nabla_\theta J(\theta) = \int G(\tau) \pi_\theta(\tau) \nabla_\theta \log \pi_\theta(\tau) d\tau = \mathbb{E}[G(\tau) \nabla_\theta \log \pi_\theta(\tau)]
$$

**Step 4**：展开轨迹概率

$$
\pi_\theta(\tau) = p(s_0) \pi_\theta(a_0|s_0) p(s_1|s_0,a_0) \pi_\theta(a_1|s_1) \cdots
$$

取log：

$$
\log \pi_\theta(\tau) = \log p(s_0) + \log \pi_\theta(a_0|s_0) + \log p(s_1|s_0,a_0) + \log \pi_\theta(a_1|s_1) + \cdots
$$

对θ求梯度（环境转移概率不依赖θ，梯度为0）：

$$
\nabla_\theta \log \pi_\theta(\tau) = \sum_t \nabla_\theta \log \pi_\theta(a_t|s_t)
$$

**Step 5**：代入得到策略梯度定理

$$
\nabla_\theta J(\theta) = \mathbb{E}\left[\sum_t G_t \nabla_\theta \log \pi_\theta(a_t|s_t)\right]
$$

其中 $G_t$ 是 $Q(s_t, a_t)$ 的采样估计，用 $A_t = Q(s_t, a_t) - V(s_t)$ 代替可降低方差：

$$
\nabla_\theta J(\theta)=\mathbb{E}_{\pi_\theta}\left[\nabla_\theta\log\pi_\theta(a_t|s_t)\,A_t\right]
$$

- 其中 $A_t$ 可用回报、TD 残差或 GAE 估计。

## 3. 训练流程

**目标**：让策略π拿到更多回报

**Step 1**：用当前策略π采样，得到轨迹 $\{(s_t, a_t, r_{t+1})\}$

**Step 2**：估计价值函数
- 用蒙特卡洛：$G_t$ 估计 $Q(s_t, a_t)$
- 或用TD：估计 $V(s_t)$

**Step 3**：计算优势
$A_t = Q(s_t, a_t) - V(s_t)$
或用TD误差近似：$A_t \approx r_{t+1} + \gamma V(s_{t+1}) - V(s_t)$

**Step 4**：更新策略
- 如果 $A_t > 0$：增加 $\pi(a_t|s_t)$ 的概率
- 如果 $A_t < 0$：降低 $\pi(a_t|s_t)$ 的概率

具体更新：
$$\theta \leftarrow \theta + \eta \nabla_\theta \log \pi_\theta(a_t|s_t) \cdot A_t.detach$$

**Step 5**：重复 Step 1-4

## 4. 直觉（为什么这么设计）
- 如果某动作带来更高优势，就提升该动作概率；反之降低概率。
- $\log \pi$ 形式让采样策略也能做梯度估计（score function trick）。

## 5. 常用变体 / 记号差异
- REINFORCE：直接用整段回报，方差大。
- Actor-Critic：用价值网络估计 baseline，方差更低。
- PPO/TRPO：给更新步长加约束，避免策略崩掉。

## 6. 在哪些模型里出现
- REINFORCE、A2C/A3C、TRPO、PPO、GRPO、RLHF 的策略优化部分。

## 7. 速查
- 关键词：`logprob`、advantage、baseline、entropy bonus。
- 常见坑：
  - 旧策略与新策略偏移过大导致训练不稳定
  - 仅优化策略损失，不管 value/entropy，容易退化
  - 采样批量太小，梯度方差太高
