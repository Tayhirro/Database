---
type: mechanism
tags:
  - java/jvm
  - jvm
  - gc
---

# GCOverview（GC 概述）

## 一句话
垃圾回收（GC）是 JVM 在运行态自动管理堆内存的一组机制：识别不可达对象并回收其占用的内存，并在必要时整理/搬迁存活对象以维持分配与访问效率。

## 严格定义
在 JVM 的内存管理语义中，GC 作用于堆（Heap）上的对象图。设对象集合为 $O$，对象间引用关系为有向边集合 $E \subseteq O \times O$，GC Roots 集合为 $R \subseteq O$。可达性定义为：
$$
Reachable(x) \iff \exists r \in R,\; r \leadsto x
$$
其中 $r \leadsto x$ 表示在引用图中从 $r$ 到 $x$ 存在一条路径。GC 的“存活判定”以可达性为主流口径：不可达对象可被回收；对引用类型（软/弱/虚）会引入额外规则（见 [ReferenceTypes.md](ReferenceTypes.md)）。

GC 的实现包含若干阶段化动作（阶段集合与顺序依收集器而定），常见阶段语义包括：根扫描、标记（mark）、清理（sweep）、复制/整理（copy/compact）、更新引用（update references）、以及与应用线程的同步/并发协作（STW/并发）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - 堆（Heap）：GC 的主要作用对象（见 [../../runtime/structure/Heap.md](../../runtime/structure/Heap.md)）
  - GC Roots：可达性分析的起点集合（见 [GCRoots.md](GCRoots.md)）
  - 安全点（Safepoint）：线程可被一致性暂停的点（见 [SafePoint.md](SafePoint.md)）
  - 写屏障与记忆结构：CardTable/RememberedSet（见 [CardTable.md](CardTable.md)、[RememberedSet.md](RememberedSet.md)）
- 输入：
  - 内存分配压力（allocation pressure）
  - 收集器触发条件（阈值、周期性、显式触发等；依实现与参数）
- 输出：
  - 堆空间回收结果（释放/整理后可分配空间的变化）
  - 运行态停顿/并发开销（表现为暂停时间与后台 CPU 负载）
- 约束：
  - 不同收集器对“停顿 vs 并发”的权衡不同；本页只给统一语义，不把某个收集器的阶段图固定为唯一形态。

## 常用构造/操作（仅列出接口与符号）
- 术语：
  - STW（Stop-The-World）：应用线程在某些阶段被暂停以满足一致性要求
  - Young/Old（分代语义）：以对象年龄/区域划分堆的管理策略（见 [../algorithm/GenerationalGC.md](../algorithm/GenerationalGC.md)）
- 观测：
  - GC 线程：见 [../../runtime/threading/GCThreads.md](../../runtime/threading/GCThreads.md)
  - GC 日志：见 [../../tuning/analysis/GCLog.md](../../tuning/analysis/GCLog.md)
  - GC 参数：见 [../../tuning/parameters/GCParameters.md](../../tuning/parameters/GCParameters.md)

## 关系：上级/下级/等价/特例/推广
- 上级：Heap（见 [../../runtime/structure/Heap.md](../../runtime/structure/Heap.md)）。
- 下级：
  - 可达性分析（见 [ReachabilityAnalysis.md](ReachabilityAnalysis.md)）
  - GC Roots（见 [GCRoots.md](GCRoots.md)）
  - 引用类型（见 [ReferenceTypes.md](ReferenceTypes.md)）
  - 安全点（见 [SafePoint.md](SafePoint.md)）
- 相关：
  - 垃圾收集器（Collector）：见 [../collector/GarbageCollector.md](../collector/GarbageCollector.md)

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → mechanism → GCOverview

