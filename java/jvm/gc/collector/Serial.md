---
type: collector
tags:
  - java/jvm
  - jvm
  - gc
  - collector
---

# Serial（收集器）

## 一句话
Serial 是一种以单线程执行回收阶段为特征的垃圾收集器实现（收集器名称与可用性依 JVM 版本与实现而定）。

## 严格定义
在收集器分类中，Serial 表示回收过程的并行度约束：在其关键回收阶段使用单个 GC 线程执行标记、复制或整理等工作（阶段组合依实现）。其停顿与阶段划分由实现决定；本页以“串行执行回收工作”的边界定义其分类语义。

## 接口：数据 + 约束
- 数据：
  - 堆与代/区布局（若采用分代）
  - GC 线程组织（通常为单工作线程；见 [../../runtime/threading/GCThreads.md](../../runtime/threading/GCThreads.md)）
- 输入：
  - 分配压力与触发条件
- 输出：
  - 回收后的堆空间变化
- 约束：
  - Serial 的具体算法组合与适用范围依 JVM 实现与版本变化；本页不将某版本默认选择视为稳定规则。

## 常用构造/操作（仅列出接口与符号）
- 上级收集器抽象：见 [GarbageCollector.md](GarbageCollector.md)

## 关系：上级/下级/等价/特例/推广
- 上级：垃圾收集器（见 [GarbageCollector.md](GarbageCollector.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → collector → Serial

