---
type: mechanism
tags:
  - java/jvm
  - jvm
  - gc
  - mechanism
---

# CardTable（卡表）

## 一句话
卡表（Card Table）是一种按固定粒度将堆划分为“卡（card）”并记录其脏状态的结构，用于支持跨代/跨区引用的增量跟踪。

## 严格定义
设堆地址空间按固定大小划分为若干卡片区间 $\{c_i\}$。卡表为每个 $c_i$ 维护一个标记位/状态值，表示该区间内对象引用字段是否发生过可能影响跨代/跨区引用关系的写入。写入时由写屏障（write barrier，具体实现定义）更新卡表状态，使得 GC 在扫描时能够跳过未被标记的卡片，从而减少扫描范围。

## 接口：数据 + 约束
- 数据：
  - 卡粒度（card size，依实现）
  - 卡表状态数组/位图
- 输入：
  - 对对象引用字段的写入事件（由写屏障观察）
- 输出：
  - “脏卡”集合（用于缩小扫描范围）
- 约束：
  - 卡表的语义与 remembered set 的组织方式依收集器而定（见 [RememberedSet.md](RememberedSet.md)）。

## 常用构造/操作（仅列出接口与符号）
- 跨代/跨区引用跟踪：见 [RememberedSet.md](RememberedSet.md)

## 关系：上级/下级/等价/特例/推广
- 上级：GC 概述（见 [GCOverview.md](GCOverview.md)）。
- 相关：分代收集（见 [../algorithm/GenerationalGC.md](../algorithm/GenerationalGC.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → mechanism → CardTable

