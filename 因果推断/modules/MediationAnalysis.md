---
title: 中介分析（Mediation Analysis）
date: "2026-03-30"
categories:
  - 因果推断
description: 中介分析研究因果效应通过哪些机制传递，并把总效应拆成直接效应与间接效应，是解释性因果分析的核心工具之一。
---
# 中介分析（Mediation Analysis）

## 1. 一句话

- 中介分析关心的不是“`X` 是否影响 `Y`”，而是“`X` 通过什么机制影响 `Y`”。

## 2. 基本问题

设：

- `X`：处理 / 原因
- `M`：中介变量
- `Y`：结果变量

典型问题是：

- `X` 对 `Y` 的总效应中，有多少是通过 `M` 传递的
- 有多少不经过 `M`，而是直接到达 `Y`

## 3. 三类核心效应

给定两个处理水平 `x` 和 `x'`，常见定义包括：

- 总效应（total effect）：
  - `TE = E[Y_x - Y_{x'}]`
- 自然直接效应（natural direct effect, NDE）：
  - `NDE = E[Y_{x, M_{x'}} - Y_{x'}]`
- 自然间接效应（natural indirect effect, NIE）：
  - `NIE = E[Y_x - Y_{x, M_{x'}}]`

其中 `Y_{x, M_{x'}}` 表示：把 `X` 设为 `x`，但把中介固定在“若 `X=x'` 时本会达到的值”。

在加性口径下，常有：

$$
TE = NDE + NIE.
$$

## 4. 为什么它比平均效应更强

- 平均干预效应告诉我们“做或不做”总体上差多少。
- 中介分析进一步问：
  - 效应是沿哪条机制传递的
  - 哪部分是机制内传递，哪部分是绕开该机制的直接作用

因此它是解释性因果分析的重要工具。

## 5. 为什么它依赖反事实语义

- `NDE` 与 `NIE` 的定义同时出现了不同世界下的量，例如 `M_{x'}` 与 `Y_{x, M_{x'}}`
- 这类对象不能仅靠观测相关性来定义
- 也不能只靠简单平均干预效应来定义
- 它们依赖完整的结构因果语义，因此和 [Counterfactual.md](Counterfactual.md) 紧密相连

## 6. 典型图结构

最小中介图通常写成：

- `X -> M -> Y`
- 以及可能的直接边 `X -> Y`

如果还存在共同原因，例如：

- `Z -> X`
- `Z -> M`
- `Z -> Y`

则需要额外的识别假设与调整设计。

## 7. 在 Pearl 七工具中的位置

- 在 CACM 文章中，中介分析对应 Tool 4：
  - `Mediation analysis and the assessment of direct and indirect effects`
- Pearl 强调，它不仅回答效应大小问题，还回答机制解释问题。

## 8. 为什么它重要

- 解释系统机制，而不是只报告平均效应
- 支持政策设计：到底是改变处理本身，还是改变传递机制更有效
- 在医疗、教育、社会科学与可解释 AI 中都很常见

## 9. 常见坑

- 把回归里的“加一个中介变量后系数变小了”直接等同于中介效应识别
- 忽略 `X-M`、`M-Y`、`X-Y` 间可能存在的未测混杂
- 未说明效应定义是 controlled direct effect、natural direct effect 还是其他口径

## 10. 相关页

- 反事实语义：见 [Counterfactual.md](Counterfactual.md)
- 干预语义：见 [Intervention.md](Intervention.md)
- 对象基础：见 [../structures/StructuralCausalModel.md](../structures/StructuralCausalModel.md)
- 论文总览：见 [../论文/TheSevenToolsOfCausalInference.md](../论文/TheSevenToolsOfCausalInference.md)
