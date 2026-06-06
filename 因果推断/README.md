---
title: "因果推断（Causal Inference）知识库组织说明（面向对象 & 速查）"
date: "2026-03-26"
categories:
  - 因果推断
description: 导航：因果推断/索引.md ｜因果推断/概念图.md ｜核心对象：SCM ｜常用操作：干预、d-separation、backdoor、反事实
---
# 因果推断（Causal Inference）知识库组织说明（面向对象 & 速查）

导航：[因果推断/索引.md](索引.md) ｜[因果推断/概念图.md](概念图.md)

这部分按“对象 / 结构 + 操作 / 推理规则”的方式组织。

- `structures/`：放“对象本身是什么”。当前核心对象是 [structures/StructuralCausalModel.md](structures/StructuralCausalModel.md)。
- `modules/`：放“对对象做什么”。当前包括 [modules/Intervention.md](modules/Intervention.md)、[modules/DSeparation.md](modules/DSeparation.md)、[modules/BackdoorCriterion.md](modules/BackdoorCriterion.md)、[modules/Counterfactual.md](modules/Counterfactual.md)、[modules/DoCalculus.md](modules/DoCalculus.md)、[modules/MediationAnalysis.md](modules/MediationAnalysis.md)、[modules/Transportability.md](modules/Transportability.md)、[modules/MissingDataRecovery.md](modules/MissingDataRecovery.md)、[modules/CausalDiscovery.md](modules/CausalDiscovery.md)。
- `examples/`：放最小例子，帮助把“看见”与“干预”分开。
- `exercises/`：放练习题与自测题。
- `论文/`：放综述与原论文笔记。当前新增 [论文/TheSevenToolsOfCausalInference.md](论文/TheSevenToolsOfCausalInference.md) 作为 Pearl 2019 CACM 综述入口。

---

## 1. 核心对象

- `StructuralCausalModel (SCM)`：因果世界的生成对象，负责说明变量怎么由其原因和噪声生成。
- 图（DAG）只是 `SCM` 的一个图形视图，不是全部语义。
- `SCM` 不是神经网络；它是一个更上位的因果建模框架。

---

## 2. 推荐目录结构

- `因果推断/README.md`：入口与组织方式
- `因果推断/索引.md`：术语索引（中文｜英文｜一句话｜链接）
- `因果推断/概念图.md`：对象、操作、查询之间的依赖关系
- `因果推断/structures/`：对象卡片
- `因果推断/modules/`：可复用规则与操作
- `因果推断/examples/Examples.md`：最小工作例子
- `因果推断/exercises/Exercises.md`：练习与检查点

---

## 3. 当前落点

- 对象入口：[structures/StructuralCausalModel.md](structures/StructuralCausalModel.md)
- 干预语义：[modules/Intervention.md](modules/Intervention.md)
- 图上独立性：[modules/DSeparation.md](modules/DSeparation.md)
- 调整准则：[modules/BackdoorCriterion.md](modules/BackdoorCriterion.md)
- 反事实：[modules/Counterfactual.md](modules/Counterfactual.md)
- 一般识别引擎：[modules/DoCalculus.md](modules/DoCalculus.md)
- 机制分解：[modules/MediationAnalysis.md](modules/MediationAnalysis.md)
- 跨环境迁移：[modules/Transportability.md](modules/Transportability.md)
- 缺失数据恢复：[modules/MissingDataRecovery.md](modules/MissingDataRecovery.md)
- 结构学习：[modules/CausalDiscovery.md](modules/CausalDiscovery.md)
- 综述入口：[论文/TheSevenToolsOfCausalInference.md](论文/TheSevenToolsOfCausalInference.md)

---

## 4. 建议学习路线

- 先懂 `SCM`：`M = (U, V, F, P(U))` 到底在表达什么
- 再读 Pearl 的总览综述，看“三层层级 + 七工具”的总框架：见 [论文/TheSevenToolsOfCausalInference.md](论文/TheSevenToolsOfCausalInference.md)
- 再懂“看见”和“干预”的区别：`P(Y|X=x)` vs `P(Y|do(X=x))`
- 再看图上的可识别规则：`d-separation`、`backdoor`、`do-calculus`
- 最后扩展到反事实、中介、迁移、缺失与发现：`Y_x`、mediation、transportability、missing data、causal discovery

---

## 5. 和机器学习的关系

- 因果推断不是神经网络分支，但会给表示学习、分布外泛化、因果表示学习、决策学习提供语义底座。
- 如果某页主要在讲“因果标签如何约束 embedding”，它仍可放在神经网络目录。
- 如果某页主要在讲“因果世界如何定义、如何干预、如何做反事实”，它应放在这里。
