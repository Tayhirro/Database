---
title: 隐式 Q 学习（IQL）
date: "2026-08-09"
categories:
  - 神经网络
  - 强化学习
aliases:
  - Implicit Q-Learning
  - IQL
note_type: algorithm
data_regime:
  - offline
policy_relation:
  - off-policy
optimization_object:
  - value
  - policy
algorithm_family:
  - implicit-value-learning
description: 不在 critic 训练中查询数据集外动作，通过 expectile value 与优势加权行为克隆实现策略改进。
---

# 隐式 Q 学习（IQL）

## 1. 一句话

IQL 不让 critic 直接评价当前 actor 生成的 unseen action，而是在数据集动作上学习 $Q$ 和偏向高价值动作的 $V$，最后用 advantage-weighted behavior cloning 抽取策略。

## 2. 核心动机

Offline RL 同时追求两个冲突目标：

$$
\text{improve over behavior policy}
\qquad\text{vs}\qquad
\text{stay in dataset support}.
$$

普通 actor-critic 的 target 会查询：

$$
Q(s',a'),\qquad a'\sim\pi(\cdot\mid s'),
$$

其中 $a'$ 可能不在数据集中。IQL 改为只对 dataset transition 中出现的动作拟合 Q。

## 3. 三步训练

### 3.1 用 expectile regression 学 V

$$
\mathcal L_V(\psi)=
\mathbb E_{(s,a)\sim\mathcal D}
\left[
L_2^\tau\!\left(Q_{\bar\theta}(s,a)-V_\psi(s)\right)
\right],
$$

其中：

$$
L_2^\tau(u)=|\tau-\mathbb 1(u<0)|u^2.
$$

当 $\tau>0.5$ 时，$V(s)$ 更偏向数据集中高价值动作对应的 Q，而不是普通均值。

### 3.2 用 V 做 Q backup

$$
\mathcal L_Q(\theta)=
\mathbb E_{(s,a,r,s')\sim\mathcal D}
\left[
\left(r+\gamma V_\psi(s')-Q_\theta(s,a)\right)^2
\right].
$$

target 使用 $V(s')$，不需要先从当前 actor 采样 $a'$ 再查询 $Q(s',a')$。

### 3.3 优势加权行为克隆

定义：

$$
A(s,a)=Q(s,a)-V(s).
$$

策略目标可写成：

$$
\mathcal L_\pi=
-\mathbb E_{(s,a)\sim\mathcal D}
\left[
\exp\bigl(\beta A(s,a)\bigr)
\log\pi_\phi(a\mid s)
\right].
$$

高优势的 dataset action 获得更大权重，因此 policy 在数据支持域内偏向更好的行为。

## 4. 和 CQL 的区别

| | [[CQL]] | IQL |
|---|---|---|
| OOD action 处理 | 评价，但主动压低 Q | critic 训练时避免查询 |
| 关键机制 | conservative regularizer | expectile $V$ + advantage-weighted BC |
| policy 约束 | 通过保守 Q 间接约束 | 直接从 dataset action 做加权 BC |
| 主要风险 | 过度悲观 | expectile、温度敏感；policy 仍可能发生函数外推 |

## 5. 方法边界

- “不查询 OOD action”主要针对 critic 训练过程，不等于部署策略永远不会输出分布外动作。
- IQL 仍依赖表示学习在新状态上的泛化，不能自动解决 OOD task / domain。
- V-GPS 可以使用 IQL 的 Q-function 做候选重排，但论文主方法采用 [[Cal-QL]]。

## 6. 参考

- [Offline Reinforcement Learning with Implicit Q-Learning](https://arxiv.org/abs/2110.06169)
- [[ActionOODAndExtrapolationError|Action OOD 与价值外推误差]]
- [[OODTaxonomy|OOD 分类总览]]
- [[神经网络/modules/rl/优化方法/训练范式/OfflineRL|离线强化学习总览]]
