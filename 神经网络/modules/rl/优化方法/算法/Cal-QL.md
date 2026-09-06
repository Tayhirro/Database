---
title: 校准 Q 学习（Cal-QL）
date: "2026-08-09"
categories:
  - 神经网络
  - 强化学习
aliases:
  - Calibrated Q-Learning
  - Cal-QL
note_type: algorithm
data_regime:
  - offline
  - offline-to-online
policy_relation:
  - off-policy
optimization_object:
  - value
  - policy
algorithm_family:
  - conservative-value-learning
description: 在 CQL 的保守价值学习上加入参考策略校准，使离线初始化更适合后续使用或在线微调。
---

# 校准 Q 学习（Cal-QL）

## 1. 一句话

Cal-QL 在 [[CQL]] 的保守性上加入 reference-policy value calibration：既不让 learned Q 高估目标策略，也避免它低于一个已知次优参考策略的合理价值尺度。

## 2. 为什么只保守还不够

CQL 通过压低 OOD action 的 Q 来防止策略利用外推错误，但保守正则过强时，所有 Q 都可能被压得过低。若之后进行在线微调，价值尺度不合理会导致：

- 新收集 transition 与离线 Q 初始化不匹配；
- policy improvement 信号弱或方向失真；
- 前期在线交互效率下降。

因此 Cal-QL 希望 learned Q 同时满足一种校准关系：

$$
Q^\mu(s,a)
\lesssim
Q_\theta(s,a)
\lesssim
Q^\pi(s,a),
$$

其中 $\mu$ 是 behavior policy 等次优 reference policy，$\pi$ 是当前学习策略。左侧避免过度悲观，右侧保留对 learned policy 的保守性。

## 3. 相对 CQL 的关键修改

Cal-QL 可将保守正则中的 policy-action Q 用 reference value 截住。示意写法为：

$$
\mathcal R_{\mathrm{Cal\text{-}QL}}(\theta)
=
\mathbb E_{s\sim\mathcal D,\,a\sim\pi}
\left[
\max\!\left(Q_\theta(s,a),Q^\mu(s,a)\right)
\right]
-
\mathbb E_{(s,a)\sim\mathcal D}[Q_\theta(s,a)].
$$

完整 critic objective 仍然是：

$$
\mathcal L_Q
=
\mathcal L_{\mathrm{Bellman}}
+\alpha\mathcal R_{\mathrm{Cal\text{-}QL}}.
$$

直觉上，CQL 负责“不要高估缺少证据的动作”，reference value 负责“也不要低到比已知策略还不合理”。

## 4. CQL、IQL、Cal-QL 对比

| 方法 | 核心问题 | 处理方式 |
|---|---|---|
| [[CQL]] | OOD action Q 被高估 | 保守正则压低 Q |
| [[IQL]] | critic 查询 unseen action | 用 expectile $V$ 避免该查询 |
| Cal-QL | CQL 可能过度悲观、尺度失准 | 用 reference policy value 校准保守 Q |

## 5. 与 V-GPS 的关系

Cal-QL 原始目标是获得适合 offline pre-training 与后续 online fine-tuning 的 Q 初始化。V-GPS 复用了其另一项能力：学习对 OOD/noisy action 更保守的 language-conditioned Q，并在部署时用它重排 generalist policy 的候选。

> [!important] Actor 不等于最终部署策略
> Cal-QL 训练流程可以包含 actor，但 V-GPS 最终保留的是 Q-function；候选动作仍由冻结的 VLA 生成。这样把“通用动作生成”与“价值判断”解耦。

## 6. 方法边界

- 主要缓解 action-support shift 和 Q-value scale 问题。
- 不保证 $Q(s_{\mathrm{OOD}},a,l)$ 在新任务、新语言、新物体或新 embodiment 上正确。
- Conservative / calibrated 不等于 semantic generalization。

## 7. 参考

- [Cal-QL: Calibrated Offline RL Pre-Training for Efficient Online Fine-Tuning](https://arxiv.org/abs/2303.05479)
- [[CQL]]
- [[ActionOODAndExtrapolationError|Action OOD 与价值外推误差]]
- [[OODTaxonomy|OOD 分类总览]]
- [[神经网络/modules/rl/优化方法/训练范式/OfflineRL|离线强化学习总览]]
