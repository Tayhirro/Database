---
title: 训练范式（Paradigms）
date: "2026-02-03"
categories:
  - agent
description: 导航：training/README.md
---
# 训练范式（Paradigms）

导航：[training/README.md](../README.md)

训练所使用的学习方法论，独立于训练阶段。

---

## 定义

Training Paradigm：模型从数据中学习的方法论，定义了学习信号的来源与优化目标。

---

## 条目列表

- [SelfSupervised](SelfSupervised.md)：自监督学习
- [SupervisedLearning](SupervisedLearning.md)：监督学习
- [ReinforcementLearning](ReinforcementLearning.md)：强化学习
- [PreferenceOptimization](PreferenceOptimization.md)：偏好优化

---

## 范式对比

| 范式 | 学习信号 | 优化目标 | 典型应用 |
|------|----------|----------|----------|
| Self-Supervised | 数据自身结构 | 预测被遮蔽/下一 token | Pretraining |
| Supervised | 标注数据 | 最小化预测与标签差异 | SFT |
| Reinforcement Learning | 奖励信号 | 最大化累积奖励 | RLHF, Reasoning |
| Preference Optimization | 偏好对比 | 偏好样本概率更高 | DPO, KTO |

---

## 关系

- 上级：[Training](../README.md)
- 被应用于：各训练阶段
