---
type: algorithm
tags:
  - java/jvm
  - jvm
  - gc
  - algorithm
---

# GenerationalGC（分代收集）

## 一句话
分代收集（Generational GC）是基于对象“短命更常见”的经验假设，将堆划分为不同代并对各代采用不同回收策略的 GC 组织方式。

## 严格定义
设堆被划分为若干代集合 $\{G_0, G_1, \dots\}$（常见二代划分为 Young/Old）。分代收集在回收某一代时，利用跨代引用跟踪结构（例如记忆集/卡表）限制扫描范围，使得对年轻代的回收可以在不全堆扫描的前提下完成，从而改变一次回收的工作范围边界（语义级）。

分代是组织方式而非单一算法：各代内部可使用 Mark-Sweep、Copying、Mark-Compact 等不同算法组合（见 [MarkSweep.md](MarkSweep.md)、[Copying.md](Copying.md)、[MarkCompact.md](MarkCompact.md)）。

## 接口：数据 + 约束
- 数据：
  - 代划分与边界（实现定义）
  - 跨代引⽤跟踪结构（见 [../mechanism/CardTable.md](../mechanism/CardTable.md)、[../mechanism/RememberedSet.md](../mechanism/RememberedSet.md)）
- 输入：
  - 分配与晋升（promotion）导致的对象代际变化（实现定义）
- 输出：
  - 分代回收后的空间变化与对象移动结果（若存在）
- 约束：
  - “代”的具体数量、边界与晋升策略依收集器实现与参数而定。

## 常用构造/操作（仅列出接口与符号）
- 卡表：见 [../mechanism/CardTable.md](../mechanism/CardTable.md)
- 记忆集：见 [../mechanism/RememberedSet.md](../mechanism/RememberedSet.md)

## 关系：上级/下级/等价/特例/推广
- 上级：GC 概述（见 [../mechanism/GCOverview.md](../mechanism/GCOverview.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → algorithm → GenerationalGC

