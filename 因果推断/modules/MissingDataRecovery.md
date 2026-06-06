---
title: 缺失数据恢复（Recovering from Missing Data）
date: "2026-03-30"
categories:
  - 因果推断
description: 因果方法把“为什么缺失”显式建模为一个机制问题，从而超出传统 model-free 缺失数据范式，对可恢复性做结构化判断。
---
# 缺失数据恢复（Recovering from Missing Data）

## 1. 一句话

- 因果视角下的缺失数据问题，不只是“缺了多少”，而是“为什么会缺，以及这种缺失机制是否允许我们恢复目标量”。

## 2. 基本设定

常见写法是：

- `X`：真实但可能缺失的变量
- `R_X`：缺失指示变量，`R_X = 1` 表示 `X` 被观测到
- `X^*`：观测代理变量

其中：

- 当 `R_X = 1` 时，`X^* = X`
- 当 `R_X = 0` 时，`X^*` 为缺失标记

## 3. 传统视角与因果视角的差别

- 传统缺失数据文献常按：
  - MCAR（完全随机缺失）
  - MAR（随机缺失）
  - MNAR（非随机缺失）
  来分类
- 但这类分类若不配合结构建模，往往难以判断更复杂目标是否仍可识别

因果视角会进一步问：

- 缺失由哪些变量驱动
- 缺失机制是否与目标变量值本身相关
- 当前图结构下，目标因果量或概率量是否 recoverable

## 4. 为什么因果图有帮助

- 一旦把缺失机制也画进模型，问题就变成：
  - 给定观测到的是 `X^*` 和 `R_X`
  - 原始目标量是否能被写成观测量的函数
- 这使“能否恢复”成为结构化可判定的问题，而不是经验性猜测

## 5. 在 Pearl 七工具中的位置

- CACM 文章中的 Tool 6：
  - `Recovering from missing data`
- Pearl 强调，传统 model-free 范式通常局限在“missing at random”情形
- 因果模型则允许显式刻画 missingness process，并判断何时仍可一致恢复目标关系

## 6. 典型可问问题

- `P(Y \mid X)` 在存在缺失时还能否恢复
- 平均处理效应在部分协变量缺失时能否识别
- 缺失机制依赖于未观测变量时，还需要哪些额外假设

## 7. 为什么这和一般插补不同

- 插补（imputation）是计算层面的补值策略
- recoverability 是识别层面的判断：
  - “这个目标量理论上还能不能被唯一推出”
- 若目标本身不可恢复，再复杂的插补也无法从根本上弥补识别缺失

## 8. 常见坑

- 把“可以做插补”误当成“目标量一定可识别”
- 只讨论缺失比例，不讨论缺失机制
- 只用 MCAR / MAR / MNAR 标签，却不说明变量之间的结构关系

## 9. 相关页

- 一般识别工具：见 [DoCalculus.md](DoCalculus.md)
- 对象基础：见 [../structures/StructuralCausalModel.md](../structures/StructuralCausalModel.md)
- 论文总览：见 [../论文/TheSevenToolsOfCausalInference.md](../论文/TheSevenToolsOfCausalInference.md)
