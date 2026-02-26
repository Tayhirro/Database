---
title: ParallelScavenge（收集器）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - gc
  - collector
description: Parallel Scavenge 是一类以并行回收阶段为特征、并以目标参数化其行为边界的收集器实现（名称与参数语义依 JVM 版本与实现而定）。
type: collector
---
# ParallelScavenge（收集器）

## 一句话
Parallel Scavenge 是一类以并行回收阶段为特征、并以目标参数化其行为边界的收集器实现（名称与参数语义依 JVM 版本与实现而定）。

## 严格定义
在收集器语义中，Parallel Scavenge 表示其回收阶段的并行执行组织，以及存在以目标形式暴露的参数化接口（例如以吞吐量/停顿等目标表达的参数集合；目标与实现路径依版本而定）。本页将其视为“目标驱动的并行收集器”分类实体，不将具体默认值或阶段图作为稳定口径。

## 接口：数据 + 约束
- 数据：
  - 并行 GC 工作线程组织
  - 目标参数（参数名与解释方式依实现）
- 输入：
  - 分配压力与触发条件
- 输出：
  - 回收后的堆空间变化与停顿/并发开销表现
- 约束：
  - 目标参数的可用性与解释方式依 JVM 实现与版本变化。

## 常用构造/操作（仅列出接口与符号）
- GC 参数：见 [../../tuning/parameters/GCParameters.md](../../tuning/parameters/GCParameters.md)

## 关系：上级/下级/等价/特例/推广
- 上级：垃圾收集器（见 [GarbageCollector.md](GarbageCollector.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → collector → ParallelScavenge

