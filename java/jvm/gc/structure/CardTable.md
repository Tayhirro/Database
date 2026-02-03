---
type: structure
kind: class
tags:
  - java/jvm
  - jvm
  - gc
  - structure
---

# CardTable（卡表）

## 一句话
卡表（Card Table）是记忆集（Remembered Set）的一种**具体实现**，采用字节数组将堆内存映射为固定大小的逻辑块（Card），并通过标记“脏卡”来记录跨区引用。

## 严格定义
卡表是一个字节数组，其中每个元素（Entry）对应堆内存中一块连续的内存区域（称为“卡页”，Card Page，通常为 512 字节）。
- **映射关系**：$Address \rightarrow CardIndex = (Address - BaseAddress) \gg Shift$。
- **脏卡（Dirty Card）**：当卡页内的对象发生引用字段赋值时，写屏障将对应的卡表元素标记为 Dirty。
  - **示例（老年代引用新生代）**：当**老年代**中的对象 $A$（位于卡页 $P_A$）的字段被赋值为**新生代**对象 $B$ 时（即 $A.field = B$），写屏障会将 $P_A$ 在卡表中对应的索引标记为 Dirty。
- **扫描逻辑**：GC（通常是 Minor GC）时仅扫描状态为 Dirty 的卡页（即老年代中发生了修改的区域），找出指向新生代的引用，从而避免扫描整个老年代。

## 接口：数据 + 约束
- **数据结构**：
  - `byte[] card_table`：全局数组。
- **状态值**：
  - `0` (Clean)：无跨区引用写入。
  - `1` (Dirty)：存在跨区引用写入（具体数值依实现而定，如 CMS 用 0 表示脏）。
- **操作**：
  - `mark_dirty(address)`：计算索引并标记。
  - `is_dirty(index)`：查询状态。
- **约束**：
  - **伪共享（False Sharing）**：高并发写入相邻卡片可能导致缓存行失效，需填充处理（如 `-XX:+UseCondCardMark`）。

## 常用构造/操作（仅列出接口与符号）
- **无条件写屏障**：直接标记脏。
- **条件写屏障**：先读后写，减少总线流量。

## 关系：上级/下级/等价/特例/推广
- **上级/接口**：[RememberedSet.md](RememberedSet.md)（记忆集）。
- **对应实体**：[Region.md](Region.md) / Generation（被映射的内存区）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → structure → CardTable
