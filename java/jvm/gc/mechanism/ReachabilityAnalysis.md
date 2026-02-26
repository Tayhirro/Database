---
title: ReachabilityAnalysis（可达性分析）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - gc
  - mechanism
description: 可达性分析（Reachability Analysis）是在对象引用图上以 GC Roots 为起点判定对象存活性的过程与判定规则。
type: mechanism
---
# ReachabilityAnalysis（可达性分析）

## 一句话
可达性分析（Reachability Analysis）是在对象引用图上以 GC Roots 为起点判定对象存活性的过程与判定规则。

## 严格定义
设对象集合为 $O$，引用关系为有向边集合 $E \subseteq O \times O$，GC Roots 集合为 $R \subseteq O$。定义可达性：
$$
Reachable(x) \iff \exists r \in R,\; r \leadsto x
$$
其中 $r \leadsto x$ 表示从 $r$ 到 $x$ 存在一条引用路径。可达性分析的输出是对象的可达集合 $\{x \in O \mid Reachable(x)\}$，其补集（在实现允许的前提下）对应可回收对象集合。

对软/弱/虚引用等引用类型，存活性判定可能引入额外规则（见 [ReferenceTypes.md](ReferenceTypes.md)）。

## 可达性级别（GC 周期第 1 阶段）

在可达性分析阶段，JVM 根据引用类型将对象分为不同可达性级别：

```
可达性分析（标记阶段）
├── 强可达（Strongly Reachable）
│   └── 存在从 GC Roots 出发、不经过任何 Reference 对象的引用路径
│   └── 结果：保留（存活）
│
├── 软可达（Softly Reachable）
│   └── 存在从 GC Roots 出发、经过 SoftReference 的路径，但无强引用路径
│   └── 结果：标记为"候选回收"（依内存压力决定是否回收）
│
├── 弱可达（Weakly Reachable）
│   └── 存在从 GC Roots 出发、经过 WeakReference 的路径，但无强/软引用路径
│   └── 结果：标记为"候选回收"（下次 GC 回收）
│
├── 虚可达（Phantom Reachable）
│   └── 对象无强/软/弱引用路径，但存在 PhantomReference 指向它
│   └── 结果：标记为"待回收"（用于回收前回调）
│
└── 不可达（Unreachable）
    └── 不存在从 GC Roots 出发的任何引用路径
    └── 结果：标记为"待回收"（可直接回收）
```

### 可达性判定规则

| 可达性级别 | 判定条件 | 回收策略 | 典型场景 |
|------------|----------|----------|----------|
| **强可达** | 存在不经过 Reference 的引用链 | 不回收 | 正常引用 |
| **软可达** | 仅通过 SoftReference 可达 | 内存不足时回收 | 缓存 |
| **弱可达** | 仅通过 WeakReference 可达 | 下次 GC 回收 | 规范化映射 |
| **虚可达** | 仅通过 PhantomReference 可达 | 已决定回收，等待回调 | 清理操作 |
| **不可达** | 无任何引用链 | 立即回收 | 垃圾对象 |

### 可达性分析流程

1. **初始标记**：从 GC Roots 开始遍历，标记所有强可达对象
2. **引用链追踪**：识别通过 SoftReference/WeakReference/PhantomReference 的引用路径
3. **级别判定**：根据引用链类型判定对象可达性级别
4. **标记输出**：
   - 强可达对象：标记为存活
   - 软/弱/虚可达对象：标记为候选回收（具体处理见引用处理阶段）
   - 不可达对象：标记为待回收

见 [ReferenceTypes.md](ReferenceTypes.md) 了解引用处理阶段的详细逻辑。

## 接口：数据 + 约束
- 数据：
  - 对象引用图（对象字段形成的有向图）
  - Roots 集合（见 [GCRoots.md](GCRoots.md)）
- 输入：
  - Roots 的枚举结果
- 输出：
  - 存活集合/不可达集合（语义级）
- 约束：
  - Roots 的枚举范围与线程一致性由安全点/停顿机制保证（见 [SafePoint.md](SafePoint.md)）。

## 常用构造/操作（仅列出接口与符号）
- Roots：见 [GCRoots.md](GCRoots.md)
- 引用类型规则：见 [ReferenceTypes.md](ReferenceTypes.md)

## 关系：上级/下级/等价/特例/推广
- 上级：GC 概述（见 [GCOverview.md](GCOverview.md)）。
- 相关：堆（见 [../../runtime/structure/Heap.md](../../runtime/structure/Heap.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → mechanism → ReachabilityAnalysis

