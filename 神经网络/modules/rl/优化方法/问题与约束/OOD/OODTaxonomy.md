---
title: 强化学习 OOD 分类总览
date: "2026-08-10"
categories:
  - 神经网络
  - 强化学习
aliases:
  - OOD Taxonomy
  - OOD 分类
note_type: moc
description: 用“偏移发生在哪里”和“变化由什么引起”两套正交索引组织强化学习中的 OOD。
---

# 强化学习 OOD 分类总览

## 1. 先分清两个问题

OOD 最容易写乱，是因为下面两个问题经常被放进同一层目录：

1. **偏移发生在哪里？**——观测、状态访问、动作支持、任务条件，还是转移/奖励。
2. **变化由什么引起？**——环境变化、机器人本体变化，或其他数据采集变化。

它们不是两套互斥标签。Environment 与 Embodiment 是**变化来源**，可以同时造成多个位置的分布偏移；因此不能和 Action OOD、Observation OOD 并列成同一级类别。

## 2. 统一记号

对语言条件机器人系统，可写成：

$$
o=h_e(s),
\qquad
u=g_e(a),
\qquad
s'\sim P_e(\cdot\mid s,u),
\qquad
r=R_e(s,u,l).
$$

- $s$：完整世界状态，包括机器人、物体和场景；它不等于“只含机器人关节状态”。
- $o$：相机、力觉、本体感觉等模型实际收到的观测，通常只是 $s$ 的部分映射。
- $a$：策略输出的动作表示；$u$ 是控制接口解释后真正施加到系统上的控制。
- $l$：语言、目标或任务条件。
- $e$：环境与机器人配置，它会影响观测映射、动作接口、转移和奖励。

## 3. 第一轴：按偏移发生的位置

这一轴适合做数学诊断，也是 OOD 问题的主目录。

| 偏移位置 | 典型判据 | 回答的问题 |
|---|---|---|
| [[ObservationOOD|Observation OOD]] | $o_{\mathrm{test}}\not\sim p_{\mathrm{train}}(o)$ | 模型实际收到的图像或传感器输入是否超出训练分布？ |
| [[StateVisitationOOD|State-visitation OOD]] | $d^\pi(s)\neq d^\mu(s)$ | 执行策略后是否进入了数据轨迹很少覆盖的世界状态？ |
| [[ActionOODAndExtrapolationError|Action OOD]] | $a\notin\operatorname{support}\mathcal D(a\mid o,l)$ | 当前候选动作是否缺少数据支持？ |
| [[LanguageGoalTaskOOD|Language / Goal / Task OOD]] | $(l,\mathcal T)_{\mathrm{test}}$ 超出训练条件分布 | 指令表达、目标语义或技能组合是否为新条件？ |
| [[TransitionRewardOOD|Transition / Reward OOD]] | $P_{\mathrm{test}}\neq P_{\mathrm{train}}$ 或 $R_{\mathrm{test}}\neq R_{\mathrm{train}}$ | 相同状态和控制的后果或价值标准是否改变？ |

> [!important] State 与 Observation 不相等
> 在 MDP 中，$s$ 是完整世界状态；但视觉策略通常只看到 $o$。新光照可能只改变 $o=h_e(s)$，而策略误差累积进入新构型则改变访问分布 $d^\pi(s)$。两者可能一起发生，但不是同一个数学对象。

## 4. 第二轴：按变化来源

这一轴用于解释偏移为什么出现，不作为互斥的 OOD 主分类。

| 变化来源 | 可能改变的映射或分布 | 可能诱发的偏移位置 |
|---|---|---|
| [[EnvironmentShift|Environment Shift]] | 场景外观、初始构型、物理参数、任务规则 | Observation、State visitation、Transition、Reward、Task |
| [[EmbodimentShift|Embodiment Shift]] | $h_e$、$g_e$、状态/动作空间、$P_e$ | Observation、Action interface、State visitation、Transition |

例如“换了一台机器人”不是只对应一种 OOD：相机位置改变会造成 Observation OOD，控制向量语义改变会造成动作接口不匹配，质量和执行器改变又会造成 Transition OOD。

## 5. 因果关系不等于分类关系

不同偏移位置之间可以形成因果链：

$$
\text{Observation OOD}
\rightarrow
\text{错误动作}
\rightarrow
\text{State-visitation OOD},
$$

也可能是：

$$
\text{Action OOD}
\rightarrow
\text{Q 外推误差}
\rightarrow
\text{策略利用虚假高值}.
$$

有因果关系不意味着应把这些问题合并成一个类别。诊断时仍要指出错误首先发生在哪个变量或映射上。

## 6. 快速诊断顺序

1. 模型收到的 $o$ 是否超出训练输入分布？检查 [[ObservationOOD]]。
2. 当前完整世界状态是否位于训练轨迹很少访问的区域？检查 [[StateVisitationOOD]]。
3. 当前 $(o,l)$ 下的动作是否有数据支持？检查 [[ActionOODAndExtrapolationError]]。
4. 指令、目标或技能组合是否是新条件？检查 [[LanguageGoalTaskOOD]]。
5. 相同 $(s,u)$ 的后果或奖励是否改变？检查 [[TransitionRewardOOD]]。
6. 最后追溯来源：是 [[EnvironmentShift|环境变化]]、[[EmbodimentShift|本体变化]]，还是策略自身的误差累积？

## 7. 方法覆盖范围

| 方法 | 主要直接处理 | 不自动保证 |
|---|---|---|
| CQL / Cal-QL | Action-support OOD 下的保守价值估计 | policy 或 value function 在新观测、新状态、新任务、新动力学上的泛化 |
| IQL | critic 训练时避免直接查询数据外动作 | policy extraction 与其他输入维度的 OOD |
| 数据增强 / 表示预训练 | 部分 Observation OOD | 转移规律和奖励变化 |
| Recovery policy | 限制 State-visitation OOD 继续扩大 | 任意新任务或新本体的正确泛化 |
| Domain randomization / Robust RL | 预先定义扰动范围内的 Observation 或 Transition shift | 扰动集合之外的任意 OOD |
| System identification / adaptation | 可识别或可在线适应的 Transition / Embodiment shift | 无数据、不可辨识变化下的可靠性 |

> [!warning]
> “使用了 CQL，所以解决了 OOD”是不完整的。更准确的说法是：CQL 主要缓解固定数据下的 **OOD-action value extrapolation**。

## 8. 综合例子

| 测试变化 | 首先检查的 OOD | 原因 |
|---|---|---|
| 同一物体和位置，只把白天光照换成夜间光照 | [[ObservationOOD|Observation OOD]] | 世界任务近似不变，但像素分布改变 |
| 机器人连续几步抓偏，最后把物体推到桌边 | [[StateVisitationOOD|State-visitation OOD]] | 策略进入训练轨迹很少覆盖的世界构型 |
| 离线数据中的关节速度只在 $[-0.2,0.2]$，策略输出 $0.8$ | [[ActionOODAndExtrapolationError|Action OOD]] | 动作超出条件数据支持域 |
| “拿起马克杯”换成“把喝水的容器取来” | [[LanguageGoalTaskOOD|Language OOD]] | 底层任务可能相同，但表达方式改变 |
| 训练只有单步抓取，测试要求“打开抽屉后把杯子放进去” | [[LanguageGoalTaskOOD|Task OOD]] | 出现未覆盖的技能组合和时序约束 |
| 同一推力作用于更重的物体 | [[TransitionRewardOOD|Transition OOD]] | 相同控制产生不同状态变化 |
| 原来只奖励到达目标，现在还惩罚能耗和碰撞 | [[TransitionRewardOOD|Reward OOD]] | 状态转移可以相同，但价值标准改变 |
| 从双指夹爪换成吸盘机器人 | [[EmbodimentShift|Embodiment Shift]] | 这是变化来源，可能同时诱发 Observation、Action interface、State visitation 和 Transition shift |

## 9. 记录新案例时的模板

遇到一个新的 OOD 案例，不要只写“测试环境和训练环境不同”，而应依次记录：

1. **保持不变的是什么？**例如任务、世界构型、动作接口或动力学。
2. **直接改变的变量是什么？**是 $o$、$d(s)$、动作支持、$l/\mathcal T$、$P$ 还是 $R$。
3. **变化来源是什么？**环境、本体、策略误差累积或数据采集偏差。
4. **模型在哪一步失效？**感知、决策、价值估计、控制映射还是后果预测。
5. **处理方法覆盖哪一层？**不要把只处理 Action OOD 的方法写成解决所有 OOD。
