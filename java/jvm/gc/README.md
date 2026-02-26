---
title: 垃圾回收（Garbage Collection）
date: "2026-02-01"
categories:
  - java
description: "导航：jvm/README.md | 索引.md | 概念图.md"
---
# 垃圾回收（Garbage Collection）

导航：[jvm/README.md](../README.md) | [索引.md](索引.md) | [概念图.md](概念图.md)

本目录包含 JVM 垃圾回收机制、算法与收集器。

---

## 子目录

| 目录 | 说明 |
|------|------|
| [mechanism/](mechanism/) | GC 机制与概念 |
| [algorithm/](algorithm/) | GC 算法 |
| [collector/](collector/) | 垃圾收集器 |

---

## 条目列表

### 机制（mechanism/）
- [GCOverview](mechanism/GCOverview.md)：GC 概述
- [ReachabilityAnalysis](mechanism/ReachabilityAnalysis.md)：可达性分析
- [GCRoots](mechanism/GCRoots.md)：GC Roots
- [ReferenceTypes](mechanism/ReferenceTypes.md)：引用类型（强/软/弱/虚）
- [SafePoint](mechanism/SafePoint.md)：安全点
- [CardTable](mechanism/CardTable.md)：卡表
- [RememberedSet](mechanism/RememberedSet.md)：记忆集

### 算法（algorithm/）
- [MarkSweep](algorithm/MarkSweep.md)：标记-清除
- [Copying](algorithm/Copying.md)：复制算法
- [MarkCompact](algorithm/MarkCompact.md)：标记-整理
- [GenerationalGC](algorithm/GenerationalGC.md)：分代收集

### 收集器（collector/）
- [GarbageCollector](collector/GarbageCollector.md)：垃圾收集器（概念与边界）
- [Serial](collector/Serial.md)：Serial 收集器
- [ParNew](collector/ParNew.md)：ParNew 收集器
- [ParallelScavenge](collector/ParallelScavenge.md)：Parallel Scavenge
- [CMS](collector/CMS.md)：CMS 收集器
- [G1](collector/G1.md)：G1 收集器
- [ZGC](collector/ZGC.md)：ZGC
- [Shenandoah](collector/Shenandoah.md)：Shenandoah

---

## 关系

- 上级：[JVM](../README.md)
- 相关：[Heap](../runtime/structure/Heap.md)（GC 作用于堆）
- 相关：[GCThreads](../runtime/threading/GCThreads.md)（GC 执行线程）
