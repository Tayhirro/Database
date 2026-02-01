---
type: collector
tags:
  - java/jvm
  - jvm
  - gc
  - collector
---

# CMS（Concurrent Mark Sweep）

## 一句话
CMS（Concurrent Mark Sweep）是一种以“并发标记/并发清理”为特征的收集器实现，其目标之一是减少某些回收阶段的停顿时间（名称与可用性依 JVM 版本与实现而定）。

## 严格定义
CMS 的分类边界在于：其标记与清理阶段（或其子阶段）可以与应用线程并发执行，从而把部分回收工作移出停顿窗口；但其语义仍需在某些一致性边界内完成必要的同步（阶段划分为实现定义）。

CMS 通常与标记-清除类算法语义相关（见 [../algorithm/MarkSweep.md](../algorithm/MarkSweep.md)），并可能引入碎片化与并发开销的权衡（表现依实现与参数）。

## 接口：数据 + 约束
- 数据：
  - 并发阶段的线程组织（实现定义）
  - 写屏障与记忆结构（若存在，依实现）
- 输入：
  - 分配压力与触发条件
- 输出：
  - 回收后的堆空间变化（可能碎片化）
- 约束：
  - CMS 的可用性与实现细节在不同 JVM 版本中可能变化；本页不将任何版本默认行为视为稳定规则。

## 常用构造/操作（仅列出接口与符号）
- 上级收集器抽象：见 [GarbageCollector.md](GarbageCollector.md)
- GC 日志：见 [../../tuning/analysis/GCLog.md](../../tuning/analysis/GCLog.md)

## 关系：上级/下级/等价/特例/推广
- 上级：垃圾收集器（见 [GarbageCollector.md](GarbageCollector.md)）。
- 相关：Mark-Sweep（见 [../algorithm/MarkSweep.md](../algorithm/MarkSweep.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → collector → CMS

