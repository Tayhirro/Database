---
title: 保守 Q 学习（CQL）
date: "2026-08-09"
categories:
  - 神经网络
  - 强化学习
aliases:
  - Conservative Q-Learning
  - CQL
note_type: algorithm
data_regime:
  - offline
policy_relation:
  - off-policy
optimization_object:
  - value
algorithm_family:
  - conservative-value-learning
description: 通过保守 Q 正则抑制离线数据支持域外动作的价值过估计。
---

# 保守 Q 学习（CQL）

## 1. 一句话

CQL 在标准 Bellman loss 上增加保守正则，压低策略可能选择、但数据集没有充分覆盖的动作价值。

## 2. 它解决什么问题

标准 off-policy RL 直接用于固定数据集时，learned policy 会逐渐偏离 behavior policy，并查询缺少监督的 OOD action：

$$
a\sim\pi(\cdot\mid s),
\qquad
a\notin\operatorname{support}\bigl(\mathcal D(\cdot\mid s)\bigr).
$$

如果 $Q(s,a)$ 在这些动作上被高估，actor 或 $\max Q$ 会主动利用错误。完整因果链见 [[ActionOODAndExtrapolationError]]。

## 3. 目标函数

离散动作下，一种常见的 CQL 目标可写为：

$$
\mathcal L_{\mathrm{CQL}}=
\mathcal L_{\mathrm{Bellman}}
+\alpha\left(
\mathbb E_{s\sim\mathcal D}
\left[\log\sum_a\exp Q_\theta(s,a)\right]
-\mathbb E_{(s,a)\sim\mathcal D}[Q_\theta(s,a)]
\right).
$$

- 第一项拟合 Bellman target。
- `logsumexp` 项压低所有潜在高 Q 动作，尤其是数据外动作。
- dataset-action 项把数据中真实出现过的动作 Q 拉回去，避免所有值一起坍缩。
- $\alpha$ 控制保守强度；连续动作中通常通过采样近似动作期望。

## 4. 直觉

> [!summary]
> 数据里见过的动作可以根据真实 transition 学价值；没有见过的动作缺少证据，因此宁可低估，也不要让策略利用虚假高值。

CQL 的目标不是让每个 $(s,a)$ 的 Q 都成为逐点下界，而是让策略在 learned Q 下的期望价值保持保守。

## 5. 与普通 Q-learning 的关系

| | Q-learning / DQN | CQL |
|---|---|---|
| 数据 | 通常可继续与环境交互 | 固定离线数据集 |
| 基础 target | $r+\gamma\max Q(s',a')$ 或 actor target | 仍然使用 Bellman backup |
| 额外约束 | 无数据支持域约束 | conservative Q regularizer |
| 主要风险 | maximization bias、训练不稳 | 过度保守、低估有潜力的动作 |

## 6. 能解决与不能解决的 OOD

- 能缓解：OOD action 引起的 Q-value overestimation。
- 不能保证：新状态、新物体、新任务或新机器人的语义泛化。
- 不能保证：候选动作完全无意义时，仅靠 Q 选择仍能成功。

## 7. 与 Cal-QL 的关系

[[Cal-QL]] 建立在 CQL 上，保留保守性，同时用 reference policy value 防止 Q 被压到不合理的低尺度，更适合 offline pre-training 后继续 online fine-tuning，也可为 V-GPS 提供价值初始化。

## 8. 参考

- [Conservative Q-Learning for Offline Reinforcement Learning](https://arxiv.org/abs/2006.04779)
- [[神经网络/modules/rl/优化方法/训练范式/OfflineRL|离线强化学习总览]]
