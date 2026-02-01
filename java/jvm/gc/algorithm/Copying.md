---
type: algorithm
tags:
  - java/jvm
  - jvm
  - gc
  - algorithm
---

# Copying（复制算法）

## 一句话
复制算法（Copying）是一类 GC 算法：将存活对象从一个空间复制/搬迁到另一个空间，并在复制完成后整体回收原空间。

## 严格定义
设堆空间被划分为源空间 $S$ 与目标空间 $T$（或更一般的多个分配区）。在一次回收中，算法将存活集合 $L$ 中的对象从 $S$ 搬迁到 $T$，并更新引用使其指向新位置。完成后，$S$ 可整体视为可用空间。复制算法的结果通常具有“空间连续性更强”的分配性质，但需要额外的目标空间或转移缓冲。

## 接口：数据 + 约束
- 数据：
  - From/To 空间（或等价的分配区）
  - 转发表/转移标记（用于避免重复复制；实现定义）
- 输入：
  - 存活对象集合与对象图
- 输出：
  - 搬迁后的对象布局与更新后的引用
- 约束：
  - 搬迁需要更新引用（指针修正）；实现可使用不同屏障/读写路径完成该语义。

## 常用构造/操作（仅列出接口与符号）
- 存活判定：见 [../mechanism/ReachabilityAnalysis.md](../mechanism/ReachabilityAnalysis.md)

## 关系：上级/下级/等价/特例/推广
- 上级：GC 概述（见 [../mechanism/GCOverview.md](../mechanism/GCOverview.md)）。
- 相关：分代收集（见 [GenerationalGC.md](GenerationalGC.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → algorithm → Copying

