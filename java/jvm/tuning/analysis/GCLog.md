---
title: GCLog（GC 日志）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - tuning
  - analysis
  - gc
description: GC 日志是 JVM 在运行态输出的 GC 事件记录流，用于描述回收触发原因、阶段耗时、停顿时间与堆使用量变化等可观测信息。
type: concept
---
# GCLog（GC 日志）

## 一句话
GC 日志是 JVM 在运行态输出的 GC 事件记录流，用于描述回收触发原因、阶段耗时、停顿时间与堆使用量变化等可观测信息。

## 严格定义
对一个 JVM 进程，GC 日志是由 GC 子系统生成的事件序列 $\{e_i\}$。每个事件 $e_i$ 以实现定义的格式记录一组字段（并非所有字段在所有收集器与版本中都存在），典型字段集合包含：
- 时间信息：时间戳、相对时间；
- 事件类型：young/mixed/full/remark/cleanup 等（依收集器）；
- 原因（cause）：触发原因（分配失败、阈值触发、显式触发等）；
- 耗时：停顿时间与各阶段耗时；
- 内存变化：回收前/后堆与代/区使用量。

GC 日志的结构与开启方式与 JDK 版本相关：JDK 8 常见为 PrintGC 系列参数；JDK 9+ 采用统一日志（unified logging）。

## 接口：数据 + 约束
- 输入：
  - 运行态 GC 事件与阶段
  - 日志开关参数（见 [../parameters/GCParameters.md](../parameters/GCParameters.md)）
- 输出：
  - 文本日志或统一日志输出流（副作用）
- 约束：
  - 日志字段集合、事件命名与阶段划分依赖收集器与版本；本页只描述可观测字段的通用口径。

## 常用构造/操作（仅列出接口与符号）
- 开启日志（语义级）：
  - JDK 8：`-XX:+PrintGCDetails` / `-XX:+PrintGCDateStamps` / `-Xloggc:<file>`（选项组合依需求）
  - JDK 9+：`-Xlog:gc*`（统一日志口径）
- 关联工具（分析与对照）：
  - `jstat`（统计视图）：见 [../tools/jstat.md](../tools/jstat.md)
  - `jcmd`（事件打印/诊断）：见 [../tools/jcmd.md](../tools/jcmd.md)

## 关系：上级/下级/等价/特例/推广
- 上级：GC（见 [../../gc/README.md](../../gc/README.md)）。
- 相关：
  - GC 参数（见 [../parameters/GCParameters.md](../parameters/GCParameters.md)）
  - GC 线程（见 [../../runtime/threading/GCThreads.md](../../runtime/threading/GCThreads.md)）

## 把新概念挂回框架（多级索引轨迹）
java → jvm → tuning → analysis → GCLog

