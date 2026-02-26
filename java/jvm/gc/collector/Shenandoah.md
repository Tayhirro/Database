---
title: Shenandoah（收集器）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - gc
  - collector
description: Shenandoah 是一种以“并发回收与并发压缩/搬迁”为特征、并以缩短停顿窗口为目标之一的收集器实现（语义与可用性依 JVM 版本与实现而定）。
type: collector
---
# Shenandoah（收集器）

## 一句话
Shenandoah 是一种以“并发回收与并发压缩/搬迁”为特征、并以缩短停顿窗口为目标之一的收集器实现（语义与可用性依 JVM 版本与实现而定）。

## 严格定义
在收集器分类语义上，Shenandoah 的边界在于：其标记、清理与对象搬迁/整理等工作被设计为尽可能与应用线程并发执行，并通过实现定义的屏障与引用更新机制保证对象访问安全。具体阶段划分与实现细节随版本变化，本页仅保留“并发搬迁/整理”的分类语义。

## 接口：数据 + 约束
- 数据：
  - 并发阶段线程组织（实现定义）
  - 对象搬迁与引用更新机制（实现定义）
- 输入：
  - 分配压力与触发条件
- 输出：
  - 回收后堆空间变化与对象搬迁结果（若存在）
- 约束：
  - 目标停顿与后台开销之间的关系依实现与参数变化。

## 常用构造/操作（仅列出接口与符号）
- GC 参数：见 [../../tuning/parameters/GCParameters.md](../../tuning/parameters/GCParameters.md)
- GC 日志：见 [../../tuning/analysis/GCLog.md](../../tuning/analysis/GCLog.md)

## 关系：上级/下级/等价/特例/推广
- 上级：垃圾收集器（见 [GarbageCollector.md](GarbageCollector.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → collector → Shenandoah

