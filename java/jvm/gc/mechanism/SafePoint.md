---
title: SafePoint（安全点）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - gc
  - mechanism
description: 安全点（Safepoint）是 JVM 能够在满足一致性条件下让线程暂停/切换到可枚举状态的位置集合，用于支持 GC、线程栈枚举等运行态操作。
type: mechanism
---
# SafePoint（安全点）

## 一句话
安全点（Safepoint）是 JVM 能够在满足一致性条件下让线程暂停/切换到可枚举状态的位置集合，用于支持 GC、线程栈枚举等运行态操作。

## 严格定义
设线程执行序列在某些位置集合 $S$ 上满足可枚举性与一致性条件（实现定义），则这些位置称为安全点（Safepoint）。当 JVM 进入一次需要一致性视图的运行态操作（例如 Roots 枚举）时，会请求线程到达并停在 $S$ 上，使得 JVM 能够读取线程栈与相关运行时结构的稳定快照。

安全点与 Stop-The-World（STW）停顿相关但不等价：安全点提供一致性边界；是否需要全局停顿、停顿持续多久、哪些阶段可并发，取决于收集器实现与当前操作类型。

## 接口：数据 + 约束
- 数据：
  - 安全点集合 $S$（实现定义）
  - 线程状态（running / at-safepoint 等实现状态）
- 输入：
  - 进入 safepoint 的请求（例如 GC 触发）
- 输出：
  - 线程达到一致性状态后的可枚举视图（副作用）
- 约束：
  - 安全点插入位置与检查机制由 JVM 实现决定；本页不假定某特定插桩策略恒定不变。

## 常用构造/操作（仅列出接口与符号）
- Roots 枚举（依赖安全点）：见 [GCRoots.md](GCRoots.md)

## 关系：上级/下级/等价/特例/推广
- 上级：GC 概述（见 [GCOverview.md](GCOverview.md)）。
- 相关：可达性分析（见 [ReachabilityAnalysis.md](ReachabilityAnalysis.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → mechanism → SafePoint

