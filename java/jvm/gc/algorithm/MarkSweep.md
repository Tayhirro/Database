---
type: algorithm
tags:
  - java/jvm
  - jvm
  - gc
  - algorithm
---

# MarkSweep（标记-清除）

## 一句话
标记-清除（Mark-Sweep）是一类 GC 算法：先标记存活对象，再回收未标记对象占用的空间。

## 严格定义
在对象引用图上，标记阶段计算存活集合 $L \subseteq O$（通常由可达性分析得到），清除阶段回收 $O \setminus L$ 对应的堆空间。该算法不要求在回收后移动存活对象，因此可能导致堆空间碎片化（fragmentation），后续分配可能依赖空闲链表等结构。

## 接口：数据 + 约束
- 数据：
  - 标记位/标记集合（实现定义）
  - 空闲空间管理结构（实现定义）
- 输入：
  - Roots 与引用图（见 [../mechanism/ReachabilityAnalysis.md](../mechanism/ReachabilityAnalysis.md)）
- 输出：
  - 回收后的空闲空间集合（可能非连续）
- 约束：
  - 是否并行/并发、是否增量化、以及标记位存储位置等属于收集器实现细节。

## 常用构造/操作（仅列出接口与符号）
- 存活判定：见 [../mechanism/ReachabilityAnalysis.md](../mechanism/ReachabilityAnalysis.md)

## 关系：上级/下级/等价/特例/推广
- 上级：GC 概述（见 [../mechanism/GCOverview.md](../mechanism/GCOverview.md)）。
- 对比：MarkCompact（见 [MarkCompact.md](MarkCompact.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → algorithm → MarkSweep

