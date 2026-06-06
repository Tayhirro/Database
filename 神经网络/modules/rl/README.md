---
title: 强化学习模块（RL Modules）
date: "2026-05-14"
categories:
  - 神经网络
  - 强化学习
description: 强化学习的核心模块，按多个维度组织。
---

# 强化学习模块（RL Modules）

## 逻辑层次

### 第1层：MDP 构成（问题定义）

RL 问题的基本要素：

| 要素           | 含义     | 说明          |
| ------------ | ------ | ----------- |
| $S$          | 状态空间   | 智能体观察到的环境状态 |
| $A$          | 动作空间   | 智能体可以执行的动作  |
| $P(s'\|s,a)$ | 状态转移概率 | 环境如何响应动作    |
| $R(s,a)$     | 奖励函数   | 每个动作的即时反馈   |
| $\gamma$     | 折扣因子   | 多看重未来       |

详见 [基础/MarkovDecisionProcess.md](基础/MarkovDecisionProcess.md)。

### 第2层：优化目标（要优化什么）

RL 的根本目标是最大化期望累积回报。但具体优化哪个对象，分出两条路线：

| 路线 | 优化对象 | 含义 |
|------|---------|------|
| 策略优化 | $J(\theta) = \mathbb{E}_{\pi_\theta}[G]$ | 直接优化策略参数 |
| 值优化 | $Q^*(s,a)$ 的最优方程 | 学出最优价值函数，策略是副产品 |

详见 [优化方法/README.md](优化方法/README.md)。

### 第3层：价值估计方法（怎么算需要的值）

两条路线都需要估计价值（策略优化需要 $Q^{\pi}$ 或 $A$ 来加权梯度，值优化需要 $Q_{k+1}$ 的 TD target）。估计方法：

| 方法 | 怎么做 | 偏差 | 方差 |
|------|-------|------|------|
| 蒙特卡洛 | 用完整轨迹 $G_t$ 估计 | 无偏 | 高 |
| TD | 用 $r + \gamma V(s')$ 估计 | 有偏 | 低 |
| GAE | 对 TD 残差做指数加权 | 可调 | 可调 |

详见 [价值估计/](价值估计/)。

### 第4层：动作采样（策略如何与环境交互）

独立于上述层次，策略在执行时如何选动作：

| 方法 | 动作选择方式 | 适用场景 | 代表算法 |
|------|-------------|---------|---------|
| Categorical分布 | 按概率采样离散动作 | 离散动作空间 | REINFORCE、A2C、PPO |
| 高斯分布 | 按概率采样连续动作 | 连续动作空间 | SAC、DDPG、TD3 |
| ε-greedy | 大部分选max，有时随机 | 离散动作空间 | Q-learning、DQN |
| softmax | 按Q值概率选择 | 离散动作空间 | SARSA |
| 确定性策略 | 直接输出动作 | 连续动作空间 | DDPG、TD3 |

### 第5层：On-policy vs Off-policy（数据能否复用）

| | On-policy | Off-policy |
|--|----------|-----------|
| 数据来源 | 当前策略采样，用完即弃 | 任意策略采样，存 replay buffer |
| 代表算法 | REINFORCE、A2C、PPO | Q-learning、DQN、DDPG、TD3、SAC |

## 推导流程：从目标到实现

### 路线A：策略优化

**目标**：最大化期望回报
$$J(\theta) = \mathbb{E}_{\tau \sim \pi_\theta}[G(\tau)]$$

**策略梯度定理**（推导详见 [PolicyGradient.md](优化方法/策略优化/PolicyGradient.md)）：
$$\nabla_\theta J(\theta) = \mathbb{E}\left[\sum_t G_t \nabla_\theta \log \pi_\theta(a_t|s_t)\right]$$

需要估计 G_t（Q 的采样估计）来加权梯度 → 维度1 的方法上场：
- MC → REINFORCE
- TD → Actor-Critic
- GAE → PPO

### 路线B：值优化

**目标**：学出最优 Q*，使 Q* 的 greedy 策略回报最高

**依据**：Bellman 最优方程（详见 [BellmanEquation.md](价值函数/BellmanEquation.md)）

$$Q^{*}(s,a) = R(s,a) + \gamma \sum_{s'} P(s'|s,a) \max_{a'} Q^{*}(s',a')$$

无模型下用 TD 更新 Q：
$$Q(s,a) \leftarrow Q(s,a) + \alpha \left[ r + \gamma \max_{a'} Q(s',a') - Q(s,a) \right]$$

**不需要策略梯度，策略是 Q 的副产品。**

## 文件结构

```
rl/
├── 基础/
│   └── MarkovDecisionProcess.md          # MDP定义
├── 价值函数/
│   ├── ValueFunction.md                  # V、Q、A定义
│   └── BellmanEquation.md                # Bellman期望方程 + 最优方程
├── 价值估计/
│   ├── MonteCarlo.md                     # 蒙特卡洛方法
│   ├── TemporalDifference.md             # TD方法
│   └── GeneralizedAdvantageEstimation.md # GAE
└── 优化方法/
    ├── README.md                         # 总览：argmax不可导 + 两条路线对比 + 迭代方式统一
    ├── 策略优化/
    │   ├── PolicyGradient.md             # 策略梯度定理
    │   ├── ActorCritic.md                # AC 框架 + 在线更新偏差
    │   ├── A2C.md                        # 同步多 worker AC
    │   ├── PPO.md                        # PPO（待补充）
    │   └── GRPO.md                       # GRPO（待补充）
    └── 值优化/
        ├── Q-learning.md                 # Q-learning
        ├── DQN.md                        # Deep Q-Network
        ├── SAC.md                        # SAC（待补充）
        └── TD3.md                        # TD3（待补充）
```

## 组合示例

### 策略优化路线
- REINFORCE = 蒙特卡洛 + 策略梯度
- Actor-Critic = TD + 策略梯度
- PPO = GAE + 策略梯度 + 约束更新

### 值优化路线
- Q-learning = TD + 值优化（单步更新 Q）
- DQN = TD + 值优化 + 神经网络 + replay buffer
- SAC = TD + 值优化 + 策略梯度（同时学 Q* 和 π，双路线混合）
