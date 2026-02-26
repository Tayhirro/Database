---
title: FinalizerThread（Finalizer 线程）
date: "2026-02-01"
categories:
  - java
tags:
  - springboot/jvm
  - jvm
  - finalization
  - threading
description: Finalizer 线程是 JVM 在运行态用于执行终结（finalization）相关工作的内部服务线程（以 HotSpot/OpenJDK 为例）。
type: mechanism
---
# FinalizerThread（Finalizer 线程）

## 一句话
Finalizer 线程是 JVM 在运行态用于执行终结（finalization）相关工作的内部服务线程（以 HotSpot/OpenJDK 为例）。

## 严格定义
在 HotSpot/OpenJDK 的实现模型中，若存在“需要终结处理”的对象集合，JVM 会通过专用线程按其内部的队列/调度机制执行终结相关工作（例如与 `finalize()` 相关的处理路径）。线程转储工具通常以固定名称模式展示该线程（具体名称依实现而变）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - 待终结对象集合（概念级）
  - 终结任务队列（概念级）
- 输入：
  - 终结任务产生事件
- 输出：
  - 终结任务被执行的效果（副作用）
- 约束：
  - 终结机制的具体语义与可用性依赖 JDK 版本与实现；本页仅描述“存在专用线程承担终结相关工作”的运行态结构。

## 常用构造/操作（仅列出接口与符号）
- 观测：`jstack <pid>`

## 关系：上级/下级/等价/特例/推广
- 上级：JVM Runtime Threads（见 [JvmRuntimeThreads.md](JvmRuntimeThreads.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → jvm → mechanism → FinalizerThread。

