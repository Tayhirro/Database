---
title: Runtime（java.lang.Runtime）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - runtime
  - class
description: java.lang.Runtime 是 Java 进程运行时环境的 Java 层访问入口，其 getRuntime() 暴露一个进程内共享的 Runtime 实例。
type: class
---
# Runtime（java.lang.Runtime）

## 一句话
`java.lang.Runtime` 是 Java 进程运行时环境的 Java 层访问入口，其 `getRuntime()` 暴露一个进程内共享的 `Runtime` 实例。

## 严格定义
在单个 JVM 进程内，`Runtime.getRuntime()` 返回一个 `Runtime` 实例引用，该引用在同一进程内可被重复获取并用于调用运行时相关能力（例如创建子进程、注册 shutdown hook 等）。

以 OpenJDK/HotSpot 的 `java.lang.Runtime` 实现为例，该共享实例通常由 `Runtime` 类初始化时创建并保存在 `Runtime` 类的静态字段中；`getRuntime()` 返回该静态字段的值（实现相关，字段名可能随版本变化）。

## 接口：数据 + 约束
- 数据：
  - 共享实例 `r: Runtime`
- 输入：
  - `Runtime.getRuntime(): Runtime`
- 输出：
  - 对共享实例 `r` 的引用
- 约束：
  - `r` 的可达性通常由“类静态字段引用”提供；当 `Runtime` 类由引导类加载器加载且类保持已加载状态时，该静态引用可作为 GC Roots 的一部分参与可达性判定（见 [../../gc/mechanism/GCRoots.md](../../gc/mechanism/GCRoots.md)）。

## 常用构造/操作（仅列出接口与符号）
- 获取共享实例：`Runtime.getRuntime()`
- 进程能力：`exec(...)`
- 关闭钩子：`addShutdownHook(Thread)` / `removeShutdownHook(Thread)`

## 关系：上级/下级/等价/特例/推广
- 上级：JVM 运行态（运行时数据区与运行态线程的抽象集合，见 [../README.md](../README.md)）。
- 相关：GC Roots（静态引用作为根来源之一，见 [../../gc/mechanism/GCRoots.md](../../gc/mechanism/GCRoots.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → runtime → class → Runtime

