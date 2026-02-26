---
title: Strategy（策略）
date: "2026-01-29"
categories:
  - springboot
description: 类型：模式（Pattern）
---
# Strategy（策略）

> **类型**：模式（Pattern）

## 一句话
Strategy 定义一组可互换的算法并将其封装为策略对象，使算法可在运行时被选择与替换而不改变使用算法的上下文结构。

## 严格定义
设策略接口为 $S$，策略实现集合为 $\\{s_1,\\dots,s_n\\}$。上下文对象持有 `strategy: S`，并把某类算法调用委托给 `strategy`；策略替换仅改变 `strategy` 的取值而不改变上下文调用点与接口。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - `Strategy`
  - `ConcreteStrategy`
  - `Context(strategy)`
- 约束：
  - 策略接口需覆盖变化点；上下文与策略之间参数/返回值契约需要稳定。

## 常用构造/操作（仅列出接口与符号）
- 选择：`setStrategy(strategy)`
- 执行：`context.execute(...) -> strategy.algorithm(...)`

## 关系：上级/下级/等价/特例/推广
- 相关：Bridge（见 [Bridge.md](Bridge.md)）、State（见 [State.md](State.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → Strategy → GoFDesignPatterns。

