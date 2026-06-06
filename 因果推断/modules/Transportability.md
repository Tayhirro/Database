---
title: 适应性、外部效度与可迁移性（Adaptability, External Validity, and Transportability）
date: "2026-03-30"
categories:
  - 因果推断
description: 这类问题研究因果结论能否从一个环境迁移到另一个环境，以及环境变化、样本选择偏差和分布迁移分别影响了哪一部分机制。
---
# 适应性、外部效度与可迁移性（Adaptability, External Validity, and Transportability）

## 1. 一句话

- 这类问题问的是：在一个环境里学到的因果结论，换到另一个环境、另一批样本或另一种部署条件下，是否仍然成立，以及该如何修正。

## 2. 典型问题

- 一个实验在研究样本上成立，能否推广到目标人群
- 一个模型在训练环境有效，换环境后应怎样调整
- 样本存在选择偏差时，某个因果量是否还能恢复

这些问题在统计学中常分别表现为：

- external validity（外部效度）
- transportability（可迁移性）
- sample selection bias（样本选择偏差）

## 3. 为什么单看相关分布不够

- 观测分布变了，并不能唯一说明“哪条机制变了”
- 同样的表面分布变化，可能由：
  - 处理机制变化
  - 结果机制变化
  - 选择机制变化
  - 混杂结构变化

因此，如果没有因果结构，只看关联层分布漂移，往往无法知道应该如何修正已有结论。

## 4. 因果视角的核心思想

- 先显式表示哪些机制在不同环境中稳定，哪些不稳定
- 再判断目标因果量能否从源环境数据、目标环境少量观测或实验结果中识别
- 若可识别，则构造迁移公式；若不可识别，则明确说明缺失了什么信息

## 5. 与 do-calculus 的关系

- 在 Pearl 体系中，这类问题并不是一个与 `do-calculus` 平行的纯经验技巧
- 它们通常依赖：
  - 因果图或选择图（selection diagram）编码环境差异
  - `do-calculus` 判断哪些机制可以迁移、哪些量需要重新估计

因此，`transportability` 可以看成 `do-calculus` 在跨环境识别问题中的系统应用。

## 6. 在 Pearl 七工具中的位置

- 在 CACM 文章中，这对应 Tool 5：
  - `Adaptability, external validity, and sample selection bias`
- Pearl 的核心判断是：
  - robustness 不能只在 Association 层处理
  - 必须借助因果模型定位环境变化究竟发生在哪个机制上

## 7. 与机器学习的连接

- domain adaptation
- transfer learning
- lifelong learning
- policy transport across environments
- OOD generalization

这类问题若只在相关层处理，往往只能做经验重加权或分布匹配；而因果方法更关注机制不变性。

## 8. 常见坑

- 把所有分布偏移都当作同一种 covariate shift
- 只比较 `P(X)` 或 `P(Y|X)` 的变化，而不讨论结构机制变化
- 没有说明要迁移的是观测关联、干预效应，还是反事实结论

## 9. 相关页

- 干预与识别：见 [Intervention.md](Intervention.md)
- 一般识别工具：见 [DoCalculus.md](DoCalculus.md)
- 对象基础：见 [../structures/StructuralCausalModel.md](../structures/StructuralCausalModel.md)
- 论文总览：见 [../论文/TheSevenToolsOfCausalInference.md](../论文/TheSevenToolsOfCausalInference.md)
