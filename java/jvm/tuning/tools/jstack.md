---
title: jstack（线程转储工具）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - tuning
  - tools
description: jstack 是 JDK 提供的命令行工具，用于获取目标 JVM 进程的线程转储（thread dump），以便分析线程状态与栈帧调用链。
type: tool
---
# jstack（线程转储工具）

## 一句话
`jstack` 是 JDK 提供的命令行工具，用于获取目标 JVM 进程的线程转储（thread dump），以便分析线程状态与栈帧调用链。

## 严格定义
对一个目标 JVM 进程（PID），`jstack` 请求 JVM 输出线程集合的栈帧快照与实现定义状态字段，形成线程转储结果（见 [../analysis/ThreadDump.md](../analysis/ThreadDump.md)）。输出格式与字段集合依 JVM 实现与版本变化，本页只保留其“线程快照获取入口”的语义边界。

## 接口：数据 + 约束
- 输入：
  - PID（目标 JVM 进程）
  - 可选参数（影响输出内容）
- 输出：
  - 线程转储文本（标准输出/重定向）
- 约束：
  - 可用性与输出字段依 JVM 实现与版本变化；获取过程可能有开销或停顿。

## 常用构造/操作（仅列出接口与符号）
- 线程转储：见 [../analysis/ThreadDump.md](../analysis/ThreadDump.md)

## 关系：上级/下级/等价/特例/推广
- 上级：Tuning / tools（诊断工具）。
- 相关：JVM 运行态线程（见 [../../runtime/threading/JvmRuntimeThreads.md](../../runtime/threading/JvmRuntimeThreads.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → tuning → tools → jstack

