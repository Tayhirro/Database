---
type: mechanism
tags:
  - java/jvm
  - jvm
  - gc
  - mechanism
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

