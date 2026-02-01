---
type: tool
tags:
  - java/jvm
  - jvm
  - tuning
  - tools
---

# jmap（堆与内存相关诊断工具）

## 一句话
`jmap` 是 JDK 提供的命令行工具，用于获取目标 JVM 进程的堆与内存相关信息，并可触发生成堆转储（heap dump）（可用功能依实现与权限而定）。

## 严格定义
对一个目标 JVM 进程（PID），`jmap` 在给定命令与参数下请求 JVM 输出实现定义的内存视图信息，或执行某些诊断动作（例如生成 heap dump）。命令集合与输出格式依 JVM 实现与版本变化，本页只描述其作为“堆相关诊断入口”的语义边界。

## 接口：数据 + 约束
- 输入：
  - PID（目标 JVM 进程）
  - 命令与参数（实现定义）
- 输出：
  - 文本信息或转储文件（副作用）
- 约束：
  - 功能可用性与权限要求依平台与 JVM 实现而定；部分动作可能带来停顿或额外开销。

## 常用构造/操作（仅列出接口与符号）
- 生成堆转储：见 [../analysis/HeapDump.md](../analysis/HeapDump.md)

## 关系：上级/下级/等价/特例/推广
- 上级：Tuning / tools（诊断工具）。
- 相关：堆（见 [../../runtime/structure/Heap.md](../../runtime/structure/Heap.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → tuning → tools → jmap

