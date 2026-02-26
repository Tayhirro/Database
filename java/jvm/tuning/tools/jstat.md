---
title: jstat（JVM 统计监控工具）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - tuning
  - tools
description: jstat 是 JDK 提供的命令行工具，用于按采样周期输出某个 JVM 进程的统计信息（包括类加载、GC、编译等维度），以便进行运行态观测。
type: tool
---
# jstat（JVM 统计监控工具）

## 一句话
`jstat` 是 JDK 提供的命令行工具，用于按采样周期输出某个 JVM 进程的统计信息（包括类加载、GC、编译等维度），以便进行运行态观测。

## 严格定义
对一个目标 JVM 进程（以进程标识符 PID 表示），`jstat` 在给定选项 $opt$ 与采样参数（周期/次数）下输出一个统计序列 $\{s_i\}$。每个样本 $s_i$ 为一组实现定义字段的值（字段集合取决于 $opt$ 与 JVM 实现），用于描述目标进程在采样时刻的运行态统计量。

## 接口：数据 + 约束
- 输入：
  - PID（目标 JVM 进程）
  - 选项（决定字段集合）
  - 采样周期与次数
- 输出：
  - 文本形式的统计表（标准输出/重定向）
- 约束：
  - 可用选项与字段解释依 JDK/JVM 版本变化；本页不将某选项的字段集合视为稳定规则。

## 常用构造/操作（仅列出接口与符号）
- 观测 GC：与 GC 日志互补（见 [../analysis/GCLog.md](../analysis/GCLog.md)）

## 关系：上级/下级/等价/特例/推广
- 上级：Tuning / tools（诊断工具）。
- 相关：GC（见 [../../gc/README.md](../../gc/README.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → tuning → tools → jstat

