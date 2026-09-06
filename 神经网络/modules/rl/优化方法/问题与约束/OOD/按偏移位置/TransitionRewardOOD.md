---
title: Transition 与 Reward OOD
date: "2026-08-10"
categories:
  - 神经网络
  - 强化学习
aliases:
  - Transition OOD
  - Dynamics OOD
  - Reward OOD
note_type: problem
ood_dimension: transition-reward
description: 讨论相同世界状态与控制下，下一状态分布或奖励规律发生变化的问题。
---

# Transition 与 Reward OOD

## 1. 定义

Transition OOD 指测试环境的状态转移核不同于训练环境：

$$
P_{\mathrm{test}}(s'\mid s,u)
\neq
P_{\mathrm{train}}(s'\mid s,u).
$$

Reward OOD 指即时奖励、成功判据或成本规律改变：

$$
R_{\mathrm{test}}(s,u,l)
\neq
R_{\mathrm{train}}(s,u,l).
$$

这里用 $u$ 表示真正施加到系统上的控制，以便和策略输出的动作表示 $a$ 以及动作接口 $u=g_e(a)$ 区分。

## 2. 它和 State OOD 不是一回事

- State-visitation OOD 问：**当前处于哪个世界状态，这个状态在训练轨迹里见过吗？**
- Transition OOD 问：**给定同一个 $(s,u)$，接下来发生什么的规律是否变了？**

因此即使当前 $s$ 完全位于训练分布内，也可能因为摩擦、质量或控制延迟改变而产生 Transition OOD。

反过来，策略可以进入未见状态，但环境的底层转移规律仍和训练时相同。这时主要是 [[StateVisitationOOD|State-visitation OOD]]。

> [!note] 能否把 Dynamics 也并入 state？
> 如果定义增广状态 $\tilde s=(s,e)$，把摩擦、质量、机器人参数等上下文 $e$ 全部包含进去，那么 Dynamics shift 在数学上也可以改写为 latent-state / context shift。但通常的跨域评估固定策略使用的状态表示，直接比较训练 MDP 与测试 MDP 的 $P$；此时模型没有显式条件化到 $e$，相同表示下的后果规律变了，所以操作上仍单列 Transition OOD。[[StateVisitationOOD|State-visitation OOD]] 则专指所选状态空间中的占用分布 $d(s)$ 改变。

## 3. 典型例子

### 3.1 Transition OOD

- 质量、摩擦、惯量或柔顺性变化；
- 接触动力学、执行器延迟与控制噪声变化；
- 控制频率、动作持续时间或低层控制器变化；
- 夹爪、机械臂或可动物体的动力学变化。

### 3.2 Reward OOD

- 成功阈值、终止条件或安全成本改变；
- 相同动作后果在新任务中被赋予不同价值；
- reward model 或人类偏好分布发生变化。

## 4. 对价值学习的影响

离线训练得到的 Bellman target 对应旧规律：

$$
r_{\mathrm{train}}+
\gamma\mathbb E_{P_{\mathrm{train}}}[V(s')]
\neq
r_{\mathrm{test}}+
\gamma\mathbb E_{P_{\mathrm{test}}}[V(s')].
$$

所以即使 Q 对训练数据内动作很保守，它的数值和排序也可能在新转移或新奖励下失效。

## 5. 变化来源

- [[EnvironmentShift|Environment Shift]]：摩擦、物体质量、接触几何或任务规则变化。
- [[EmbodimentShift|Embodiment Shift]]：机器人质量、执行器、夹爪、低层控制器或动作接口变化。

这些是变化来源；Transition / Reward 是变化真正落到 MDP 的位置。

## 6. 常见处理

- Dynamics randomization 与 robust / distributionally robust RL。
- System identification 或 latent context inference。
- 在线适应、offline-to-online fine-tuning。
- World model 更新与 model-based replanning。
- 对新奖励重新标注、reward conditioning 或重新估计 value。

## 7. 更多例子与诊断

| 领域 | 训练条件 | 测试变化 | 主要偏移 |
|---|---|---|---|
| 机械臂 | 空塑料杯 | 装满水的杯子 | 质量与惯量改变，Transition OOD |
| 移动机器人 | 干燥地面 | 湿滑地面 | 相同轮速产生不同位移，Transition OOD |
| 仿真到现实 | 理想执行器、无延迟 | 电机死区、延迟和噪声 | Transition OOD |
| 自动驾驶 | 普通车辆响应 | 前车制动能力或驾驶风格改变 | 交互转移规律改变 |
| 游戏 | 固定敌人策略 | 敌人更新为新策略 | 环境响应模型改变 |
| 推荐系统 | 用户兴趣演化规律稳定 | 平台活动改变用户反馈模式 | Transition OOD |
| 机器人任务 | 只奖励任务成功 | 增加碰撞、时间和能耗惩罚 | Reward OOD |
| 偏好学习 | 固定 reward model | 人类偏好或评价标准改变 | Reward OOD |

诊断时可以：

- 固定尽可能相同的 $(s,u)$，比较训练与测试的 $s'$ 分布或预测残差。
- 检查 one-step dynamics error 是否系统性增大，而不只是长轨迹累积误差。
- 分别测试 $P$ 与 $R$：后果相同但得分变化属于 Reward OOD。
- 对质量、摩擦、延迟等候选参数做 system identification，确认偏移来源。
- 检查新变化是否落在训练时 domain randomization 的支持范围内。

## 8. 相关笔记

- [[OODTaxonomy|OOD 分类总览]]
- [[StateVisitationOOD|State-visitation OOD]]
- [[ObservationOOD|Observation OOD]]
- [[ActionOODAndExtrapolationError|Action OOD 与价值外推误差]]
- [[EnvironmentShift|Environment Shift]]
- [[EmbodimentShift|Embodiment Shift]]
