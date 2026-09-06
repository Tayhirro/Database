---
title: Online RL 与 Offline RL
date: "2026-08-09"
categories:
  - 神经网络
  - 强化学习
aliases:
  - Online vs Offline RL
note_type: taxonomy
classification_axis: data-regime
description: 按训练期间能否继续与环境交互，区分在线强化学习与离线强化学习。
---

# Online RL 与 Offline RL

## 1. 判断标准

这一维只问一个问题：**训练期间还能不能向环境获取新 transition？**

| | Online RL | Offline RL |
|---|---|---|
| 数据 | 训练过程中持续增加 | 固定数据集 $\mathcal D$ |
| 错误纠正 | 可以执行新动作并观察后果 | 无法验证数据外动作 |
| 核心难点 | 探索成本、非平稳训练 | 分布偏移、OOD action、外推误差 |
| 示例 | A2C、在线 Q-learning / DQN | CQL、IQL、Cal-QL |

## 2. Replay buffer 不决定 Online / Offline

- DQN 一边与环境交互、一边向 replay buffer 添加数据：仍然是 **online**。
- 固定 replay buffer，训练期间永远不再加入环境数据：属于 **offline**。

因此：

$$
\text{replay buffer}\not\Rightarrow\text{offline RL}.
$$

## 3. 与 On-policy / Off-policy 的关系

Online / Offline 描述**数据是否继续增长**；On-policy / Off-policy 描述**数据由谁采集、正在学习谁的策略**。完整区别见 [[OnPolicyVsOffPolicy]]。

常见组合：

| 数据范式 | 策略关系 | 例子 |
|---|---|---|
| Online | On-policy | REINFORCE、A2C、PPO |
| Online | Off-policy | Q-learning、DQN、DDPG、TD3、SAC |
| Offline | Off-policy | CQL、IQL、Cal-QL |

## 4. 为什么普通 Off-policy 算法不等于 Offline RL 算法

标准 Q-learning / actor-critic 可以复用旧策略数据，但通常默认还能继续采集数据。固定数据下，learned policy 可能查询数据支持域外动作：

$$
a_{\mathrm{OOD}}\notin
\operatorname{support}\bigl(\mathcal D(\cdot\mid s)\bigr),
$$

从而触发 Q 外推误差。详见 [[OfflineRL|离线强化学习]] 与 [[ActionOODAndExtrapolationError|Action OOD 与价值外推误差]]。

## 5. 速查

- 能继续交互：Online。
- 数据已经封死：Offline。
- 是否使用 replay buffer：不能单独判断。
- 是否 off-policy：也不能单独判断。
