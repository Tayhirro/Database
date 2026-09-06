---
title: Observation OOD
date: "2026-08-10"
categories:
  - 神经网络
  - 强化学习
aliases:
  - 观测分布偏移
  - Visual OOD
  - Input OOD
note_type: problem
ood_dimension: observation
description: 讨论模型实际输入的图像、语言外传感器与本体观测超出训练分布的问题，并与完整世界状态偏移区分。
---

# Observation OOD

## 1. 定义

策略通常无法直接访问完整世界状态 $s$，而是接收观测：

$$
o=h_e(s).
$$

当测试输入偏离训练观测分布时：

$$
o_{\mathrm{test}}\not\sim p_{\mathrm{train}}(o),
$$

$\pi(a\mid o,l)$、$Q(o,a,l)$ 或 world model 就是在模型输入空间外泛化。

## 2. 典型例子

| 类型 | 训练条件 | 测试变化 | 为什么属于 Observation OOD |
|---|---|---|---|
| 光照 | 白天、均匀照明 | 夜间、逆光、强阴影 | 相同场景对应的像素分布改变 |
| 外观 | 纯色杯子 | 透明杯、反光金属杯、花纹杯 | 物体功能相近，但视觉表征不同 |
| 相机 | 固定俯视相机 | 腕部相机、相机偏转、分辨率下降 | 观测映射 $h_e$ 改变 |
| 遮挡 | 目标完整可见 | 手臂、其他物体遮住目标 | 可用观测信息减少 |
| 深度/力觉 | 完整且校准良好 | 噪声、漂移、饱和、缺失帧 | 非视觉传感器分布改变 |
| 本体感觉 | 关节角使用弧度且已归一化 | 单位、零点或归一化方式改变 | 数值 schema 看似兼容但语义改变 |
| 多模态 | RGB + 深度均存在 | 测试时深度模态缺失 | 输入模态组合超出训练覆盖 |

这里的关键是**模型收到的 $o$ 变了**，不要求真实世界状态一定变化。

## 3. 与 State-visitation OOD 的区别

| 场景 | 世界状态 $s$ | 观测映射或输入 $o$ | 主要问题 |
|---|---|---|---|
| 同一桌面与物体构型，只把灯光调暗 | 可近似不变 | 改变 | Observation OOD |
| 多步误差把杯子推到未覆盖位置 | 改变并进入低覆盖区域 | 通常也会改变 | 主要是 [[StateVisitationOOD|State-visitation OOD]]，可能伴随 Observation OOD |
| 换相机后机器人又走入新构型 | 改变 | 改变 | 两种 OOD 同时存在 |

在 MDP 中，$s$ 是机器人、物体和场景构成的完整世界状态，不是只指机器人关节。Observation 则是 $s$ 经传感器与本体配置映射后的可见部分。

## 4. 为什么 CQL 不能直接解决

CQL 约束的是给定输入条件下的数据外动作价值。如果观测本身没见过：

$$
Q(o_{\mathrm{OOD}},a,l),
$$

网络仍只能依赖表示泛化。压低数据外动作的 Q，并不会自动修复视觉或传感器表征。

## 5. 常见处理

- 视觉与传感器增强：颜色、裁剪、遮挡、噪声和视角随机化。
- 多场景、多相机、多传感器数据与表示预训练。
- 域不变表示、domain adaptation 或 test-time adaptation。
- OOD detection 与不确定性校准；低置信度时拒绝、回退或重规划。
- 传感器校准、schema 对齐和缺失模态处理。

## 6. 容易误判的例子

- **物体换了位置**：如果新位置属于训练轨迹没覆盖的世界构型，主要是 [[StateVisitationOOD|State-visitation OOD]]；图像随之变化时也会伴随 Observation OOD。
- **桌面摩擦改变**：当前图像可能完全一样，但动作后果改变，主要是 [[TransitionRewardOOD|Transition OOD]]。
- **指令换成同义句**：视觉输入不变，属于 [[LanguageGoalTaskOOD|Language OOD]]。
- **图像很新但任务仍能完成**：OOD 描述的是分布偏移，不等于一定失败；它只表示训练数据不能直接保证该输入上的性能。

## 7. 变化来源

- [[EnvironmentShift|Environment Shift]] 常通过光照、背景和相机位置改变 $h_e(s)$。
- [[EmbodimentShift|Embodiment Shift]] 常通过传感器布置、本体状态字段和预处理改变 $h_e$。

它们是来源，Observation OOD 是偏移发生的位置。

## 8. 如何评估

- 按光照、相机、物体外观、遮挡和传感器噪声分别建立测试切片。
- 保持任务和世界状态尽量不变，只改变一个观测因素，避免把 Transition 或 Task shift 混进实验。
- 除任务成功率外，记录感知置信度、策略熵、价值误差和校准误差。
- 比较单因素变化与多因素组合变化，检查模型是否只对训练增强范围内稳健。

## 9. 相关笔记

- [[OODTaxonomy|OOD 分类总览]]
- [[StateVisitationOOD|State-visitation OOD]]
- [[ActionOODAndExtrapolationError|Action OOD 与价值外推误差]]
- [[LanguageGoalTaskOOD|Language / Goal / Task OOD]]
- [[TransitionRewardOOD|Transition / Reward OOD]]
