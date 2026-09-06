---
title: A2C（Advantage Actor-Critic）
date: "2026-05-18"
categories:
  - 神经网络
  - 强化学习
note_type: algorithm
data_regime:
  - online
policy_relation:
  - on-policy
optimization_object:
  - policy
  - value
algorithm_family:
  - actor-critic
description: Actor-Critic 的同步多 worker 实现，收集 batch 后统一更新，避免在线更新偏差。
---
# A2C（Advantage Actor-Critic）

## 1. 一句话
A2C 是 Actor-Critic 的具体工程实现：多个 worker 同步采样，收集一批数据，统一更新策略和价值网络。

## 2. 与标准 AC 的区别

| | 标准 AC | A2C |
|--|--------|-----|
| worker 数 | 1 | 多个（并行环境） |
| 采样方式 | 每步更新 | 收集完整 batch 后统一更新 |
| advantage 估计 | 单步 TD 误差 | N-step return 或 GAE |
| 在线更新偏差 | 有（见 ActorCritic.md 第 4 节） | 无（采样时策略固定） |

## 3. 同步多 worker
- 同时开 N 个环境副本，每个 worker 用当前策略独立采样
- 各自采集一批经验后，汇总梯度统一更新
- 更新完成后，所有 worker 同步到新策略

## 4. 实际流程
1. 用当前策略在 N 个环境并行采样，收集 batch
2. 用 Critic 计算 advantage（N-step 或 GAE）
3. 汇总梯度，更新 Actor 和 Critic
4. 清空 batch，回到第 1 步

## 5. 参考
- [ActorCritic.md](ActorCritic.md)：AC 基础
- [GeneralizedAdvantageEstimation.md](../../价值估计/GeneralizedAdvantageEstimation.md)：GAE
