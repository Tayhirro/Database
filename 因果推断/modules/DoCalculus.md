---
title: do-calculus
date: "2026-03-30"
categories:
  - 因果推断
description: do-calculus 是 Pearl 提出的符号变换系统，用于把含 do-operator 的因果查询在给定图假设下化简为可由观测或实验数据识别的形式。
---
# do-calculus

## 1. 一句话

- `do-calculus` 是一套围绕 `P(\cdot \mid do(\cdot))` 的图驱动符号演算规则，用来判断因果效应能否识别，并在可识别时给出化简路径。

## 2. 它解决什么问题

- 后门准则只能处理一部分混杂控制问题。
- 当简单调整集不存在，或者查询中同时出现多个干预、选择偏差、环境变化时，需要更一般的识别工具。
- `do-calculus` 的目标正是：
  - 把含 `do(.)` 的表达式转化为不含 `do(.)`、或更容易估计的表达式
  - 如果无法化简，则明确表明在当前假设下不可识别

## 3. 直观语义

- `P(Y \mid X=x)` 是观测条件化。
- `P(Y \mid do(X=x))` 是结构方程替换后的分布。
- `do-calculus` 通过检查若干“改边后的图”中的 `d-separation` 关系，决定某个观察、某个动作能否被插入、删除或互换。

## 4. 它和后门准则的关系

- 后门准则是一个重要但特殊的识别条件。
- `do-calculus` 是更一般的演算系统；后门、前门、transportability 等很多结果都可以看成它的具体应用。
- 因此，知识组织上：
  - [BackdoorCriterion.md](BackdoorCriterion.md) 适合做“最常用的第一入口”
  - `do-calculus` 适合做“更一般的统一工具”

## 5. 规则层面的最小理解

- `do-calculus` 由三条变换规则组成。
- 这些规则不依赖具体数值形式，而依赖图上在不同“截断 / 改边”条件下的 `d-separation` 关系。
- 从使用者角度，最重要的不是死记规则编号，而是理解它允许三类操作：
  - 插入或删除观测变量
  - 在满足条件时把动作和观测互换
  - 在满足条件时插入或删除动作变量

## 6. 为什么它重要

- 它把“能不能从数据推出因果效应”从经验判断变成了可算法化的问题。
- 在 Pearl 的表述里，一个识别引擎应当：
  - 在可识别时产出 estimand
  - 在不可识别时明确返回 failure
- 这正是因果推断区别于“凭直觉调变量”的地方。

## 7. 在 Pearl 七工具中的位置

- 在 CACM 文章 *The Seven Tools of Causal Inference, with Reflections on Machine Learning* 中，`do-calculus` 是 Tool 2 的核心。
- Tool 2 的主题是：`Do-calculus and the control of confounding`
- 其中：
  - `backdoor` 负责最直接的混杂控制
  - `do-calculus` 负责更一般的干预识别

## 8. 常见应用场景

- 没有后门调整集，但仍希望识别 `P(Y \mid do(X=x))`
- 多阶段决策或策略评估中的干预化简
- 环境迁移、sample selection bias、transportability
- 缺失机制或选择机制被显式建模时的识别问题

## 9. 常见坑

- 把 `do-calculus` 当成“比后门更复杂的代数技巧”，忽略其图语义基础
- 只会机械变形表达式，却不检查每一步依赖的图条件是否成立
- 在图结构未明确时就直接谈 `do-calculus`，导致语义前提不清

## 10. 相关页

- 图上独立性：见 [DSeparation.md](DSeparation.md)
- 最常用识别准则：见 [BackdoorCriterion.md](BackdoorCriterion.md)
- 干预语义：见 [Intervention.md](Intervention.md)
- 论文总览：见 [../论文/TheSevenToolsOfCausalInference.md](../论文/TheSevenToolsOfCausalInference.md)
