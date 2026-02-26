---
title: JITParameters（JIT 参数）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - tuning
  - parameters
  - jit
description: JIT 参数是 JVM 启动参数集合中用于控制即时编译（JIT）行为边界（编译策略、阈值、编译线程等）的参数子集。
type: concept
---
# JITParameters（JIT 参数）

## 一句话
JIT 参数是 JVM 启动参数集合中用于控制即时编译（JIT）行为边界（编译策略、阈值、编译线程等）的参数子集。

## 严格定义
对一个 JVM 进程，设启动参数集合为 $P$。JIT 参数子集 $P_{jit} \subseteq P$ 作用于执行引擎的 JIT 编译模块：影响解释执行与编译执行的切换条件、编译器选择与分层编译策略，以及与编译相关的线程组织方式。参数集合、默认值与语义依 JVM 实现与版本变化，本页以“参数类别”描述其统一接口。

## 接口：数据 + 约束
- 输入：
  - JVM 启动参数（命令行）
- 输出：
  - JIT 编译行为边界变化（副作用）
- 约束：
  - 参数语义与可用性依 JVM 实现与版本变化；本页不固定某实现的默认策略。

## 常用构造/操作（仅列出接口与符号）
- 观测与对照：编译器线程（见 [../../runtime/threading/CompilerThreads.md](../../runtime/threading/CompilerThreads.md)）

## 关系：上级/下级/等价/特例/推广
- 上级：Tuning / parameters（调优参数）。
- 相关：执行引擎 / JIT（分支入口见 [../../execution/README.md](../../execution/README.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → tuning → parameters → JITParameters

