---
title: State-visitation OOD
date: "2026-08-10"
categories:
  - 神经网络
  - 强化学习
aliases:
  - State OOD
  - 状态访问分布偏移
note_type: problem
ood_dimension: state-visitation
description: 讨论策略执行后访问到离线轨迹未充分覆盖的完整世界状态，以及误差累积问题。
---

# State-visitation OOD

## 1. 这里的 state 是什么

在 MDP 中，$s$ 是决定未来转移与奖励所需的**完整世界状态**，可以同时包含：

- 机器人关节、速度、夹爪和控制器内部状态；
- 物体位置、姿态、速度和接触关系；
- 场景几何、障碍物以及任务相关变量。

所以 state 不是“只指机器人 state”。在部分可观测问题中，策略甚至拿不到完整 $s$，只能看到观测 $o$。

> [!note] 状态定义依赖建模边界
> 理论上可以把环境参数或机器人参数并入增广状态 $\tilde s=(s,e)$。但如果训练和测试使用相同的状态表示、模型也没有观察 $e$，这些参数变化通常按 [[TransitionRewardOOD|Transition OOD]] 或 [[EmbodimentShift|Embodiment Shift]] 诊断，而不是全部塞进 State-visitation OOD。

## 2. 定义

离线数据由行为策略 $\mu$ 收集，其状态占用分布为 $d^\mu(s)$。部署策略 $\pi$ 的多步行为可能进入另一片区域：

$$
d^\pi(s)\neq d^\mu(s).
$$

当当前世界状态在训练轨迹中很少或从未出现时，policy、critic 或 world model 就会在状态访问分布外泛化。

## 3. 它怎样产生

常见因果链是：

$$
\text{单步小误差}
\rightarrow
\text{状态轻微偏离}
\rightarrow
\text{下一步预测更差}
\rightarrow
\text{进入更远的未覆盖状态}.
$$

Action OOD、Observation OOD、动力学变化以及恢复失败都可能诱发 State-visitation OOD；但它们是原因，state-visitation 指的是最终访问分布 $d(s)$ 的变化。

## 4. 典型例子

| 场景 | 数据覆盖 | 部署时访问的状态 | 为什么属于 State-visitation OOD |
|---|---|---|---|
| 机械臂抓取 | 物体主要位于桌面中央 | 物体被碰到桌边或夹在障碍物后 | 世界构型超出训练轨迹覆盖 |
| 自动驾驶 | 正常车道保持 | 前车急刹后车辆偏到路肩 | 多步交互进入少见交通状态 |
| 游戏 | 数据来自较安全路线 | 新策略走入高风险房间 | 访问分布由策略选择改变 |
| 行走机器人 | 数据多为直立姿态 | 小误差积累成大倾角或半摔倒姿态 | 恢复区域缺少训练样本 |
| 推荐系统 | 历史日志来自旧推荐策略 | 新策略持续推荐某类内容，改变用户兴趣状态 | 策略反过来改变未来状态分布 |

> [!example] 不是必须“环境换了”才会发生
> 即使测试环境、动力学和初始状态都与训练完全一致，只要部署策略 $\pi$ 与数据策略 $\mu$ 的多步决策不同，就可能出现 $d^\pi(s)\neq d^\mu(s)$。

## 5. 与 Observation OOD 的区别

| 情况 | 主要归类 |
|---|---|
| 世界构型没变，只是相机、光照或传感器映射变了 | [[ObservationOOD|Observation OOD]] |
| 多步执行后杯子被推到训练轨迹从未出现的位置 | State-visitation OOD |
| 新机器人导致运动轨迹进入不同构型 | [[EmbodimentShift|Embodiment Shift]] 是来源，State-visitation OOD 是结果之一 |

同一个测试样本可以两者都有。例如机器人进入新构型，同时产生训练中没见过的图像。

## 6. 常见处理

- 扩大轨迹和初始状态覆盖。
- DAgger、在线数据聚合或 offline-to-online fine-tuning。
- Recovery policy、失败数据和安全回退。
- 不确定性检测与保守规划，避免继续深入未知区域。
- 世界模型或 model predictive control，通过新反馈持续重规划。

## 7. 方法边界

CQL、IQL 和 Cal-QL 主要控制当前条件下的动作支持风险。它们可能间接减少异常动作把系统推向未知状态，但不保证：

$$
Q(s_{\mathrm{OOD}},a)
$$

在未覆盖世界状态上仍然准确。

## 8. 如何诊断

- 比较训练数据与部署轨迹的 state occupancy、关键状态变量直方图和状态嵌入距离。
- 按时间步绘制 OOD 分数，观察偏移是否随 rollout 逐步扩大。
- 从相同初始状态分别执行行为策略与新策略，定位首次分叉的动作和状态。
- 单独统计恢复状态、失败前状态和安全边界附近的覆盖率。
- 区分“初始状态本来就新”与“策略执行后才进入新状态”，两者的数据补充方式不同。

## 9. 相关笔记

- [[OODTaxonomy|OOD 分类总览]]
- [[ObservationOOD|Observation OOD]]
- [[ActionOODAndExtrapolationError|Action OOD 与价值外推误差]]
- [[TransitionRewardOOD|Transition / Reward OOD]]
- [[EnvironmentShift|Environment Shift]]
- [[EmbodimentShift|Embodiment Shift]]
