---
title: GarbageCollector（垃圾收集器）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - gc
  - collector
description: 垃圾收集器（Garbage Collector）是 JVM 中实现 GC 的具体算法与执行策略组合，用于在运行态完成标记、回收、整理/搬迁以及与应用线程的同步/并发协作。
type: concept
---
# GarbageCollector（垃圾收集器）

## 一句话
垃圾收集器（Garbage Collector）是 JVM 中实现 GC 的具体算法与执行策略组合，用于在运行态完成标记、回收、整理/搬迁以及与应用线程的同步/并发协作。

## 严格定义
在 JVM 的实现层面，“GC”是机制与语义，“Collector”是对该机制的具体实现：它定义了对象存活判定的实现方式、回收与整理策略、并行/并发线程组织方式、以及停顿点与安全性保证方式。不同收集器的差异通常体现在：
- 是否分代/分区（generational/region-based）；
- 标记/回收/整理阶段的组合与是否并发；
- 停顿（pause）与后台开销（concurrent overhead）的权衡目标。

## 接口：数据 + 约束
- 数据（语义级别）：
  - 堆布局与代/区（依收集器）
  - 线程组织：GC 线程与并发线程（见 [../../runtime/threading/GCThreads.md](../../runtime/threading/GCThreads.md)）
  - 写屏障与记忆结构（跨代/跨区引用的维护结构）
- 输入：
  - GC 触发条件与回收目标（由实现与参数决定）
- 输出：
  - 堆回收结果与对象移动结果（依收集器）
  - 应用线程停顿与并发阶段开销
- 约束：
  - “收集器名称、可用性与默认选择”与 JDK 版本、JVM 实现、平台相关；本页不将某版本默认值视为稳定规则。

## 常用构造/操作（仅列出接口与符号）
- 选择与配置（参数维度）：见 [../../tuning/parameters/GCParameters.md](../../tuning/parameters/GCParameters.md)
- 观测（日志维度）：见 [../../tuning/analysis/GCLog.md](../../tuning/analysis/GCLog.md)

## 关系：上级/下级/等价/特例/推广
- 上级：GC 概述（见 [../mechanism/GCOverview.md](../mechanism/GCOverview.md)）。
- 下级：具体收集器（目录 `collector/` 内的条目，依实现与版本范围）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → collector → GarbageCollector

