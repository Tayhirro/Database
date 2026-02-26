---
title: jcmd（JVM 命令行诊断工具）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - tuning
  - tools
description: jcmd 是 JDK 提供的命令行诊断工具，用于向目标 JVM 进程发送诊断命令以获取运行态信息或触发某些诊断动作（命令集合依 JVM 实现而定）。
type: tool
---
# jcmd（JVM 命令行诊断工具）

## 一句话
`jcmd` 是 JDK 提供的命令行诊断工具，用于向目标 JVM 进程发送诊断命令以获取运行态信息或触发某些诊断动作（命令集合依 JVM 实现而定）。

## 严格定义
对一个目标 JVM 进程（PID）与一个诊断命令 $c$，`jcmd` 请求 JVM 执行 $c$ 并返回命令输出。命令集合与每个命令的语义属于实现定义，常用于获取线程/内存/GC 等维度的运行态信息。

## 接口：数据 + 约束
- 输入：
  - PID（目标 JVM 进程）
  - 命令名与参数（实现定义）
- 输出：
  - 命令执行结果文本（标准输出/重定向）
- 约束：
  - 可用命令与输出格式依 JDK/JVM 版本变化；本页不将命令集合视为稳定规则。

## 常用构造/操作（仅列出接口与符号）
- 与 GC 观测相关：见 [../analysis/GCLog.md](../analysis/GCLog.md)

## 关系：上级/下级/等价/特例/推广
- 上级：Tuning / tools（诊断工具）。
- 相关：GC（见 [../../gc/README.md](../../gc/README.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → tuning → tools → jcmd

