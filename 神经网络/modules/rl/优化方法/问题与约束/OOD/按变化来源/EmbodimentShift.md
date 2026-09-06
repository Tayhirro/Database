---
title: Embodiment Shift
date: "2026-08-10"
categories:
  - 神经网络
  - 强化学习
aliases:
  - Embodiment OOD
  - 机器人本体变化
note_type: shift-source
shift_source: embodiment
description: 将机器人本体变化视为复合 OOD 来源，区分观测映射、动作接口、状态空间与转移动力学的变化。
---

# Embodiment Shift

## 1. 定位

Embodiment Shift 指机器人本体、传感器、执行器或控制接口发生变化。它通常不是单一变量的 OOD，而是一组耦合变化：

$$
o=h_e(s),
\qquad
u=g_e(a),
\qquad
s'\sim P_e(\cdot\mid s,u).
$$

当 embodiment $e$ 改变时，$h_e$、$g_e$、$P_e$ 以及状态/动作空间都可能改变。

## 2. 它会诱发哪些偏移

| 本体变化 | 直接影响 | 诱发的 OOD 位置 |
|---|---|---|
| 相机位置、数量、分辨率改变 | 观测映射 $h_e$ | [[ObservationOOD|Observation OOD]] |
| 关节数、状态字段、坐标系改变 | 状态表示或状态空间 | 输入 schema 不匹配，并可能诱发 [[StateVisitationOOD]] |
| 动作维度、归一化、坐标系、控制频率改变 | 动作映射 $g_e$ 或 $\mathcal A$ | 动作接口不匹配；映射后也可能形成 [[ActionOODAndExtrapolationError|Action-support OOD]] |
| 质量、惯量、夹爪和执行器改变 | 转移 $P_e$ | [[TransitionRewardOOD|Transition OOD]] |
| 可达空间和运动约束改变 | 可实现状态与轨迹 | State-visitation 与 Transition shift |

## 3. 如何理解动作空间改变

$$
\mathcal A_{\mathrm{test}}\neq\mathcal A_{\mathrm{train}}
$$

描述的是最明显的接口不兼容，例如从 7 维末端动作换成 14 维关节动作。但这不是 Embodiment Shift 的完整定义：即使两个系统都输出相同维数的 $a$，只要 $g_e(a)$ 的物理语义不同，原策略和 Q 也可能失效。

因此要分别检查：

1. 动作 schema 是否兼容；
2. 归一化与坐标系是否对齐；
3. 相同动作表示是否产生相近控制；
4. 相同控制是否仍具有相近动力学后果。

## 4. 典型例子

### 4.1 同维动作，不同语义

两台机械臂都接收 7 维向量，但一台把前三维解释为世界坐标系速度，另一台解释为末端局部坐标系位移。动作维数相同并不代表 $g_e(a)$ 相同。

### 4.2 末端执行器变化

从双指夹爪换成吸盘时：

- 接触和抓取动力学改变；
- 可完成的抓取姿态改变；
- 动作序列和成功条件可能改变；
- 腕部相机视野也可能因安装结构变化。

### 4.3 传感器配置变化

从固定第三视角相机换成腕部相机，主要先产生 Observation OOD；如果策略因此采取不同轨迹，又会继续诱发 State-visitation OOD。

### 4.4 移动底盘变化

从差速轮式底盘换成全向底盘，动作空间、可达轨迹和动力学同时改变。只做动作维度 padding 不能保证策略兼容。

## 5. 常见处理

- 统一 action/state schema 与显式 embodiment token。
- 每个机器人使用适配器，把公共动作表示映射到本体控制。
- 多机器人预训练与 embodiment-aware representation。
- 动力学随机化、system identification 与在线适应。
- 在目标机器人上重新校准策略、价值函数或动力学模型，而非只检查动作数值范围。

## 6. 评估清单

- 观测字段、单位、时间同步和归一化是否一致？
- 动作维度、坐标系、控制模式、频率和时域是否一致？
- 同一个动作表示是否产生相同方向和量级的真实控制？
- 工作空间、关节限制、碰撞几何和末端工具是否改变？
- 相同任务状态下的 one-step transition 是否仍然接近？
- 是否分别报告了 zero-shot transfer、adapter transfer 与目标机器人微调后的结果？

## 7. 相关笔记

- [[OODTaxonomy|OOD 分类总览]]
- [[EnvironmentShift|Environment Shift]]
- [[ObservationOOD|Observation OOD]]
- [[StateVisitationOOD|State-visitation OOD]]
- [[ActionOODAndExtrapolationError|Action OOD 与价值外推误差]]
- [[TransitionRewardOOD|Transition / Reward OOD]]
