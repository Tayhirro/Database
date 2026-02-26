---
title: LifecycleStateMachine（显式生命周期与状态机）
date: "2026-01-28"
categories:
  - springboot
description: 类型：概念（Concept）
---
# LifecycleStateMachine（显式生命周期与状态机）

> **类型**：概念（Concept）

## 一句话
显式生命周期与状态机将组件抽象为具有可枚举状态与受约束迁移的系统，并以明确的入口方法或事件驱动状态变化。

## 严格定义
给定组件 $C$，若存在状态集合 $Q$、事件/操作集合 $\Sigma$ 与迁移关系 $\delta: Q \times \Sigma \rightarrow Q$，并且组件对外可用能力集合依赖于当前状态（例如在某些状态下不保证某些操作可用），则称该组件采用显式生命周期状态机；其中状态迁移的触发点应被显式表达（方法调用、事件、调度器等）。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - 状态集合：如 `CREATED` / `CONFIGURING` / `RUNNING` / `CLOSED`（仅为示例）
  - 触发集合：`start/init/refresh/close` 或等价事件集合
  - 不变式：每个状态下允许的操作子集与资源占用约束
- 约束：
  - 状态迁移应保持不变式；失败路径的状态与资源回收语义需被定义（例如回滚到 `CREATED` 或进入 `FAILED`）。

## 常用构造/操作（仅列出接口与符号）
- 定义状态：`enum State { ... }`
- 迁移入口：`transition(trigger)` / `refresh()` / `close()`

## 关系：上级/下级/等价/特例/推广
- 下级/特例：
  - 两阶段初始化：见 [../pattern/TwoPhaseInitialization.md](../pattern/TwoPhaseInitialization.md)
- 相关：
- 例化：
  - `ApplicationContext` 的可配置态 → 运行态迁移：见 [../../core/context/mechanism/ContextRefresh.md](../../core/context/mechanism/ContextRefresh.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → concept → LifecycleStateMachine → core/context/mechanism/ContextRefresh。
