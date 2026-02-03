---
type: mechanism
tags:
  - java/jvm
  - jvm
  - gc
  - mechanism
---

# RememberedSet（记忆集）

## 一句话
记忆集（Remembered Set, RSet）是记录“跨代/跨区引用来源”的数据结构，用于在收集某一代/区域时限制扫描范围。



## 严格定义
对分代或分区的堆布局，设堆被划分为多个区域集合 $\{A, B, \dots\}$。当收集目标区域为 $T$ 时，需要识别从其他区域指向 $T$ 的引用边集合 $E_{in}(T)$。记忆集以实现定义的组织方式（例如按区域维护引用来源摘要）存储 $E_{in}(T)$ 的可查询近似，使得 GC 能在不全堆扫描的情况下定位进入目标区域的引用边。

记忆集的维护通常依赖写屏障，并可与卡表等结构协同（见 [CardTable.md](CardTable.md)）。

## 接口：数据 + 约束
- 数据：
  - 目标区域到“引用来源摘要”的映射（实现定义）
  - 可选：与卡表/位图等辅助结构的组合
- 输入：
  - 引用字段写入事件（由写屏障观察）
- 输出：
  - 进入目标区域的引用来源集合的可查询视图（近似/摘要）
- 约束：
  - 记忆集精确度与维护开销由收集器设计决定；本页不将某实现的粒度或编码方式视为稳定规则。

## 常用构造/操作（仅列出接口与符号）
- 卡表：见 [CardTable.md](CardTable.md)

## 关系：上级/下级/等价/特例/推广
- 上级：GC 概述（见 [GCOverview.md](GCOverview.md)）。
- 相关：分代收集（见 [../algorithm/GenerationalGC.md](../algorithm/GenerationalGC.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → mechanism → RememberedSet

