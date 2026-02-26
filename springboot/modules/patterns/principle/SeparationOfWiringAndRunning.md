---
title: SeparationOfWiringAndRunning（装配与运行分离）
date: "2026-01-28"
categories:
  - springboot
description: 类型：原则（Principle）
---
# SeparationOfWiringAndRunning（装配与运行分离）

> **类型**：原则（Principle）

## 一句话
装配与运行分离是一种将“描述系统结构与规则（wiring/configuration）”与“实例化并启动运行时对象网络（running）”分开表达与执行的组织原则。

## 严格定义
设系统 $S$ 的运行时对象图为 $G$，若存在一组可独立于 $G$ 构造的结构描述 $D$（例如依赖图、规则集合、拦截/增强策略、条件装配规则），并且存在显式的求值/启动过程 $E$ 将 $D$ 物化为 $G$，则称该系统满足装配与运行分离；其中 $D$ 的变更在 $E$ 之前具有确定语义，而 $E$ 之后的变更需以明确的增量或重建语义表达。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - 结构描述 $D$：定义集合、规则集合、策略集合
  - 求值/启动过程 $E$：将 $D$ 转换为运行时对象图 $G$
  - 运行时对象图 $G$：实例、依赖边、代理/增强结果
- 约束：
  - $D$ 与 $G$ 的一致性由 $E$ 保证；$E$ 之后若允许修改 $D$，需定义对 $G$ 的影响范围与时机。

## 常用构造/操作（仅列出接口与符号）
- 收集定义：`registerDefinition(d)` / `addRule(r)` / `addProcessor(p)`
- 启动求值：`evaluate(D)` / `refresh()` / `build()`

## 关系：上级/下级/等价/特例/推广
- 下级/特例：
  - 两阶段初始化：见 [../pattern/TwoPhaseInitialization.md](../pattern/TwoPhaseInitialization.md)
- 相关：
  - 模板方法：见 [../pattern/TemplateMethod.md](../pattern/TemplateMethod.md)
- 例化：
  - `ApplicationContext` 的配置态/运行态分离：见 [../../core/context/mechanism/ContextRefresh.md](../../core/context/mechanism/ContextRefresh.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → principle → SeparationOfWiringAndRunning → core/context/mechanism/ContextRefresh。
