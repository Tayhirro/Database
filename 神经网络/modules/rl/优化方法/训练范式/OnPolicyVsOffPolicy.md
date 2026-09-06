---
title: On-policy 与 Off-policy
date: "2026-08-09"
categories:
  - 神经网络
  - 强化学习
aliases:
  - On-policy vs Off-policy
note_type: taxonomy
classification_axis: policy-relation
description: 按行为策略与目标策略是否一致，区分 on-policy 与 off-policy。
---

# On-policy 与 Off-policy

## 1. 两个策略角色

- **Behavior policy $\mu$**：实际产生训练数据的策略。
- **Target policy $\pi$**：当前希望评估或优化的策略。

判断关系：

$$
\begin{cases}
\mu=\pi & \text{On-policy},\\
\mu\neq\pi & \text{Off-policy}.
\end{cases}
$$

## 2. On-policy

数据来自当前策略，策略更新后旧数据通常不能直接当作新策略数据重复使用。

- 优点：训练分布与目标策略一致。
- 代价：样本复用率低，需要持续与环境交互。
- 代表：REINFORCE、A2C、PPO。

## 3. Off-policy

可以使用旧策略、探索策略或其他行为策略产生的数据，学习不同的目标策略。

- 优点：可以使用 replay buffer，样本复用率高。
- 风险：behavior distribution 与 target-policy distribution 偏移。
- 代表：Q-learning、DQN、DDPG、TD3、SAC，以及大多数 Offline RL 方法。

Q-learning 的行为策略可以是 $\epsilon$-greedy，但 target 使用：

$$
r+\gamma\max_{a'}Q(s',a'),
$$

因此学习的是 greedy target policy，而不是行为策略本身。

## 4. Off-policy 不等于 Offline

> [!important]
> Off-policy 只说明“可以用别的策略的数据”；Offline 进一步规定“训练时只能使用一个固定数据集，不能再向环境取数据”。

| 算法 | Policy relation | Data regime |
|---|---|---|
| A2C | On-policy | Online |
| Q-learning / DQN | Off-policy | 通常 Online |
| CQL / IQL | Off-policy | Offline |
| Cal-QL | Off-policy | Offline 或 Offline-to-Online |

## 5. 为什么 Offline RL 通常是 Off-policy

固定数据由未知或历史 behavior policy 收集，而训练目标通常是改进后的新策略，所以 $\mu\neq\pi$。如果只评估或复现原 behavior policy，分布偏移较小，但也难以获得真正的 policy improvement。

详见 [[OnlineVsOffline]]、[[OfflineRL|离线强化学习]] 和 [[ActionOODAndExtrapolationError|Action OOD 与价值外推误差]]。
