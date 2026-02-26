---
title: ZGC（Z Garbage Collector）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - gc
  - collector
description: ZGC 是一种以“尽量缩短停顿窗口”为目标并将主要回收工作并发化的收集器实现（语义与可用性依 JVM 版本与实现而定）。
type: collector
---
# ZGC（Z Garbage Collector）

## 一句话
ZGC 是一种以“尽量缩短停顿窗口”为目标并将主要回收工作并发化的收集器实现（语义与可用性依 JVM 版本与实现而定）。

## 严格定义
在收集器分类语义上，ZGC 的边界在于：其标记、回收与搬迁等阶段被设计为尽可能与应用线程并发执行，并通过实现定义的读/写路径机制保证对象访问与引用更新的安全性。其具体实现机制（例如指针标记/屏障形式）属于实现细节，本页不将其固定为唯一口径。

## 接口：数据 + 约束
- 数据：
  - 并发阶段线程组织（实现定义）
  - 对象搬迁与引用更新机制（实现定义）
- 输入：
  - 分配压力与触发条件
- 输出：
  - 回收后堆空间变化与对象搬迁结果（若存在）
- 约束：
  - 目标停顿与后台开销之间的关系依实现与参数变化；本页只描述分类边界。

## 常用构造/操作（仅列出接口与符号）
- GC 参数：见 [../../tuning/parameters/GCParameters.md](../../tuning/parameters/GCParameters.md)
- GC 日志：见 [../../tuning/analysis/GCLog.md](../../tuning/analysis/GCLog.md)

## 关系：上级/下级/等价/特例/推广
- 上级：垃圾收集器（见 [GarbageCollector.md](GarbageCollector.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → collector → ZGC

