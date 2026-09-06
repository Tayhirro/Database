---
title: Action OOD 与价值外推误差
date: "2026-08-09"
categories:
  - 神经网络
  - 强化学习
aliases:
  - Extrapolation Error
  - OOD Action
  - Action OOD
note_type: problem
data_regime:
  - offline
ood_dimension: action-support
description: 专门讨论固定数据下的动作支持域偏移、Q-value 外推错误及其自举传播。
---

# Action OOD 与价值外推误差

## 1. 一句话

Offline RL 无法向环境验证数据集外动作；一旦 Q-function 对这些动作给出虚假高值，策略优化和 Bellman bootstrap 就会利用并传播这个错误。

## 2. 外推误差怎样产生

设固定数据集只覆盖动作支持域：

$$
a\in\operatorname{support}\bigl(\mathcal D(\cdot\mid s)\bigr).
$$

函数逼近器仍然可以对未见动作 $a_{\mathrm{OOD}}$ 输出数值：

$$
Q_\theta(s,a_{\mathrm{OOD}})=100.
$$

标准 Q-learning 的 target 会查询最大值：

$$
y=r+\gamma\max_{a'}Q_{\bar\theta}(s',a').
$$

如果最大值恰好来自虚假的 OOD 高值，就会形成：

$$
\text{OOD action}
\rightarrow
\text{Q extrapolation error}
\rightarrow
\text{policy / max 利用错误}
\rightarrow
\text{bootstrap 传播}.
$$

在线 RL 尚可通过真实环境反馈纠正错误；offline RL 的数据固定，无法验证该动作，因此问题尤其严重。

## 3. 不要和普通 Q 高估混淆

| 概念 | 主要原因 | 数据集外动作是否必要 | 典型处理 |
|---|---|---|---|
| Maximization bias | 对带噪 Q 估计取 $\max$，选择误差与评价误差耦合 | 不必要 | Double DQN、双 Q |
| Extrapolation error | learned policy 与 dataset 分布偏移，查询缺少监督的 $(s,a)$ | 是核心条件 | CQL、IQL、策略约束 |

二者可能同时出现，但 Double DQN 或 TD3 的双 Q 并不能单独解决 offline-RL 数据支持域问题。

## 4. 三条主要处理路线

### 4.1 压低数据外动作的 Q

[[CQL]] 在 Bellman loss 外加入 conservative regularizer，使数据集外动作不容易获得虚假高值。

### 4.2 不查询数据外动作的 Q

[[IQL]] 用 dataset action 拟合 Q，通过 expectile regression 学习偏向高价值数据动作的 $V(s)$，避免在 critic 学习阶段直接评价当前 actor 生成的 unseen action。

### 4.3 约束 policy 靠近数据分布

通过 behavior cloning、KL 或显式 behavior model 限制：

$$
D_{\mathrm{KL}}\!\left(
\pi_{\mathrm{new}}(\cdot\mid s)
\,\|\,
\pi_\beta(\cdot\mid s)
\right).
$$

这样可以减少 OOD action，但约束过强时会牺牲 policy improvement。

## 5. 这篇笔记不处理什么

Action OOD 的判定条件是：在给定状态与任务条件下，动作超出数据支持域。

$$
a\notin\operatorname{support}\bigl(\mathcal D(a\mid s,l)\bigr).
$$

它不等于：

- 模型收到的图像或传感器输入没见过：见 [[ObservationOOD]]。
- 策略进入训练轨迹未覆盖的完整世界状态：见 [[StateVisitationOOD]]。
- 语言、目标或任务组合没见过：见 [[LanguageGoalTaskOOD]]。
- 相同状态与控制的后果或奖励改变：见 [[TransitionRewardOOD]]。
- 环境或机器人本体改变：它们是变化来源，见 [[EnvironmentShift]] 与 [[EmbodimentShift]]。

> [!warning] 方法边界
> CQL、IQL 和 Cal-QL 主要针对 action-support shift。即便它们完全消除了 OOD-action 高估，也不能推出 Q 在新观测、新状态、新任务或新动力学下仍然准确。

## 6. 与其他 OOD 的因果关系

Action OOD 与其他维度可以连续发生，但仍应分开诊断：

$$
\text{Action OOD}
\rightarrow
\text{执行异常动作}
\rightarrow
\text{进入未覆盖状态}
\rightarrow
\text{State-visitation OOD}.
$$

前一个问题发生在 $a$ 的条件支持域，后一个问题发生在完整世界状态 $s'$ 的访问分布；不能因为二者有因果关系就写成同一种 OOD。

## 7. 典型例子

### 7.1 连续控制

离线机械臂数据在某个状态附近只包含小幅末端位移：

$$
\Delta x,\Delta y,\Delta z\in[-0.02,0.02].
$$

如果 actor 输出 $\Delta x=0.15$，这个动作即使没有超出控制器允许的物理范围，也可能超出**数据条件支持域**。Action OOD 关心的不是动作是否合法，而是数据是否支持对它进行可靠评价。

### 7.2 离散动作

- 数据中某个游戏状态只出现过“向左”和“停留”，策略却选择“跳跃”。
- 推荐数据中某类用户从未展示过某种商品，Q 却把该商品估成最高价值。
- 医疗决策数据中某类病情没有某种治疗方案的记录，策略仍推荐该方案。

离散动作也会 OOD；它不要求动作数值超出某个连续区间。

### 7.3 条件支持比全局范围更重要

某个动作可能在整个数据集中出现过，但在当前状态下仍然 OOD：

$$
a\in\operatorname{support}\mathcal D(a)
\quad\text{但}\quad
a\notin\operatorname{support}\mathcal D(a\mid s,l).
$$

例如“快速闭合夹爪”在抓取阶段很常见，但在夹爪仍远离物体时可能没有任何数据支持。

## 8. 如何诊断

- 用行为模型 $\hat\pi_\beta(a\mid s,l)$ 的似然或距离估计动作支持。
- 检查 actor 动作与最近邻数据动作的距离，而不只检查全局最小值和最大值。
- 对不同支持密度区域分别画 Q-value、真实回报和误差。
- 比较部署策略与行为策略的 action distribution、KL 或 MMD。
- 检查 Bellman target 中的 $\max$ 或 actor action 是否频繁落在低密度区域。

## 9. 相关笔记

- [[OODTaxonomy|OOD 分类总览]]
- [[ObservationOOD|Observation OOD]]
- [[StateVisitationOOD|State-visitation OOD]]
- [[Q-learning DQN]]：$\max Q$ 与标准值优化。
- [[TemporalDifference]]：bootstrap 与 critic/actor 错误传播。
- [[CQL]]：保守价值正则。
- [[IQL]]：避免查询数据外动作。
- [[Cal-QL]]：对保守 Q 进行尺度校准。
