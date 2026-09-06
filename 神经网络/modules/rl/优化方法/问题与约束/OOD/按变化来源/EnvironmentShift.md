---
title: Environment Shift
date: "2026-08-10"
categories:
  - 神经网络
  - 强化学习
aliases:
  - Environment OOD
  - 环境变化
note_type: shift-source
shift_source: environment
description: 将环境视为 OOD 的变化来源，并映射到观测、状态访问、转移、奖励和任务等偏移位置。
---

# Environment Shift

## 1. 定位

Environment Shift 回答的是“**什么外部条件变了**”，不是一个与 Action OOD、State OOD 互斥的数学位置。

一个新环境往往同时改变多个变量，因此应继续标注它具体诱发了哪些偏移。

## 2. 从环境变化映射到偏移位置

| 环境变化 | 直接影响 | 诱发的 OOD 位置 |
|---|---|---|
| 光照、背景、纹理、相机外参 | $o=h_e(s)$ | [[ObservationOOD|Observation OOD]] |
| 初始物体位置、布局、障碍物 | 初始状态分布 $p_0(s)$ 与访问分布 | [[StateVisitationOOD|State-visitation OOD]]，也常伴随 Observation OOD |
| 摩擦、质量、柔顺性、接触几何 | $P_e(s'\mid s,u)$ | [[TransitionRewardOOD|Transition OOD]] |
| 奖励规则、成功判据、安全成本 | $R_e(s,u,l)$ | [[TransitionRewardOOD|Reward OOD]] |
| 新目标、新任务组合 | $l$ 或任务分布 | [[LanguageGoalTaskOOD|Goal / Task OOD]] |

## 3. 综合案例

### 3.1 从实验室桌面换到家庭厨房

- 背景、光照、物体外观改变：Observation OOD。
- 物体初始位置和遮挡关系改变：State-visitation OOD，通常伴随 Observation OOD。
- 台面摩擦和容器质量改变：Transition OOD。
- 用户提出新的整理目标：Goal / Task OOD。

所以“新厨房”不是一个足够精确的 OOD 标签，而是多个偏移的共同来源。

### 3.2 从晴天道路换到雨雪道路

- 能见度、反光和摄像头水滴：Observation OOD。
- 车辆可能进入不同交通构型：State-visitation OOD。
- 轮胎抓地力和制动距离：Transition OOD。
- 安全速度或成本权重改变：Reward OOD。

### 3.3 游戏版本更新

- 地图贴图更新：Observation OOD。
- 新地图结构和出生点：初始状态与 State-visitation shift。
- 角色移动速度或碰撞规则修改：Transition OOD。
- 得分规则和任务目标修改：Reward / Task OOD。

## 4. 为什么不能都叫 State OOD

如果把所有“环境变了”都归为 State OOD，就无法区分：

- 当前输入图像变了；
- 策略进入了新世界状态；
- 相同状态与控制的物理后果变了；
- 成功标准变了。

这些问题需要的数据、损失函数和适应方式不同，所以 Environment 应作为来源标签，而不是把所有后果揉成一个 State 类别。

## 5. 常见处理

- 视觉增强和多场景预训练：主要覆盖 Observation shift。
- 初始状态随机化和更广轨迹采集：主要扩大 State visitation coverage。
- Dynamics randomization、robust RL：主要覆盖预设 Transition shift。
- System identification、online adaptation：识别并适应当前环境参数。
- 奖励重定义或任务条件化：处理 Reward / Task shift。

## 6. 如何记录 Environment Shift

记录案例时，至少拆成四列：环境变化、被改变的 MDP/观测组件、对应 OOD 位置、验证方法。不要只留下“换了场景”“存在 domain gap”这种无法指导实验的描述。

## 7. 相关笔记

- [[OODTaxonomy|OOD 分类总览]]
- [[EmbodimentShift|Embodiment Shift]]
- [[ObservationOOD|Observation OOD]]
- [[StateVisitationOOD|State-visitation OOD]]
- [[TransitionRewardOOD|Transition / Reward OOD]]
