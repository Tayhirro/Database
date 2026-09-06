---
title: Language、Goal 与 Task OOD
date: "2026-08-10"
categories:
  - 神经网络
  - 强化学习
aliases:
  - Language OOD
  - Task OOD
  - Goal OOD
note_type: problem
ood_dimension: language-goal-task
description: 区分语言表述分布偏移、目标语义偏移与新任务或技能组合泛化。
---

# Language、Goal 与 Task OOD

## 1. 定义

语言条件策略或价值函数通常写成：

$$
\pi(a\mid s,l),
\qquad
Q(s,a,l).
$$

当测试指令、目标语义或任务组合超出训练条件分布时，就会发生 Language / Goal / Task OOD：

$$
l_{\mathrm{test}}\not\sim p_{\mathrm{train}}(l)
\quad\text{或}\quad
\mathcal T_{\mathrm{test}}\notin\operatorname{support}(\mathcal T_{\mathrm{train}}).
$$

## 2. Language OOD

Language OOD 主要是**表达方式变化，但底层任务可能没变**：

- 同义表达、口语、缩写或不同语言；
- 训练中少见的句式和指代；
- 长指令、否定、关系描述；
- 拼写错误或歧义表达。

例如“把杯子放进盒子”与“请将马克杯收纳到蓝色容器中”可能描述同一技能，但语言分布不同。

更多例子：

| 训练表达 | 测试表达 | 变化类型 |
|---|---|---|
| “拿起杯子” | “把喝水的容器取来” | 同义改写与常识指代 |
| “put the apple in the bowl” | “把苹果放进碗里” | 跨语言表达 |
| “把左边的杯子拿起来” | “拿起离盘子最近的杯子” | 关系描述改变 |
| 简短肯定句 | “不要拿红色的，拿蓝色的” | 否定与排除约束 |
| 单轮明确指令 | “把刚才那个放回原处” | 对话历史和指代依赖 |

## 3. Goal / Task OOD

Task OOD 主要是**要完成的行为或目标结构变化**：

- 新物体—目标组合；
- 未见技能或长程技能组合；
- 新的前置条件、顺序或关系约束；
- 相同词汇描述了训练中从未执行的任务。

所以“词都见过”不代表任务 in-distribution，“句子没见过”也不代表任务一定新。

| 训练任务 | 测试任务 | 主要变化 |
|---|---|---|
| 抓取杯子、抓取苹果 | 抓取订书机 | 新物体—技能组合 |
| 打开抽屉、抓取杯子 | 打开抽屉后把杯子放进去 | 新的时序组合 |
| 把物体放进任意盒子 | 只放进与物体同色的盒子 | 新关系约束 |
| 单步操作 | 完成长程整理任务 | 新规划深度与子任务依赖 |
| 固定目标位置 | “放到不挡住通道的位置” | 新目标函数或约束 |

## 4. 常见处理

- 使用大规模语言/VLM/VLA 预训练编码器。
- 指令释义、翻译与语言增强。
- 多任务和组合式任务训练。
- 结构化 goal representation、技能库与检索。
- 不确定性校准、歧义检测与澄清机制。
- 少样本适应、上下文学习或 task-conditioned fine-tuning。

## 5. 为什么 Action-OOD 方法不够

CQL、IQL、Cal-QL 可以限制动作支持域风险，但语言条件本身 OOD 时：

$$
Q(s,a,l_{\mathrm{OOD}})
$$

仍然可能错误理解“什么才算高价值”。这不是把数据外动作 Q 压低就能解决的问题。

## 6. 如何区分 Language、Goal 与 Task

可以做三个反事实检查：

1. **把测试指令改写成训练中常见句式后，任务是否还是新的？**若不新，主要是 Language OOD。
2. **保持语言表达不变，只替换目标对象或目标状态，是否超出训练组合？**若是，主要是 Goal OOD。
3. **对象和词汇都见过，但需要新的技能、顺序或规划结构吗？**若是，主要是 Task OOD。

三者可以同时发生。例如一条外语长指令既包含新表达，也要求从未训练过的技能组合。

## 7. 如何评估

- 将同一任务写成多种释义，单独测 Language robustness。
- 固定句式并系统替换对象、目标位置和关系，测试组合泛化。
- 按技能长度、前置条件数量和规划深度划分 Task OOD 难度。
- 同时报告语义理解正确率、子目标预测、动作成功率和失败类型。
- 不要把训练集中“没有出现过完整句子”直接当成新任务；应检查底层目标和技能结构。

## 8. 与其他 OOD 的边界

- 图像或传感器输入改变：[[ObservationOOD|Observation OOD]]。
- 多步执行进入训练轨迹未覆盖的世界状态：[[StateVisitationOOD|State-visitation OOD]]。
- 动作在当前条件下缺少离线数据支持：[[ActionOODAndExtrapolationError|Action OOD]]。
- 任务语义没变，但相同控制的物理后果改变：[[TransitionRewardOOD|Transition OOD]]。

## 9. 相关笔记

- [[OODTaxonomy|OOD 分类总览]]
- [[ObservationOOD|Observation OOD]]
- [[StateVisitationOOD|State-visitation OOD]]
- [[ActionOODAndExtrapolationError|Action OOD 与价值外推误差]]
- [[TransitionRewardOOD|Transition / Reward OOD]]
