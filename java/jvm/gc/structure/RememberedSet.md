---
type: structure
tags:
  - java/jvm
  - jvm
  - gc
  - structure
---

# RememberedSet（记忆集）

## 一句话
记忆集（Remembered Set, RSet）是一种用于记录**从非收集区域指向收集区域**的指针集合的抽象数据结构，旨在避免在部分收集（Partial GC）时扫描整个堆。

## 严格定义
在分代或分区（Region-based）垃圾收集器中，为了独立回收特定区域（Collection Set, CSet），需要识别所有指向该区域的活动引用。记忆集是维护这些**跨代/跨区引用**（Cross-Region Reference）的数据结构。它记录了“谁引用了我”，使得 GC Root 扫描可以包含这些跨区引用，而无需遍历整个老年代或非收集区域。

## 接口：数据 + 约束
- **数据模型**：
  - 集合 $S = \{ (src, target) \mid src \in \text{Non-CSet}, target \in \text{CSet} \}$ 的一种紧凑表示。
  - 通常按目标区域（Region/Generation）索引。
- **输入**：
  - 引用赋值操作（$obj.field = value$），通过**写屏障（Write Barrier）**捕获。
- **输出**：
  - 指向特定区域的所有源对象/卡片/内存块的地址集合。
- **约束**：
  - **准确性**：必须包含所有活跃的跨区引用（允许包含少量非活跃引用，即“浮动垃圾”）。
  - **性能**：写入开销（写屏障）与空间占用需平衡。

## 常用构造/操作（仅列出接口与符号）
- **Points-in RSet**：记录“谁引用了我”（G1 使用）。
- **Points-out RSet**：记录“我引用了谁”（较少用）。
- **具体实现**：
  - **卡表（Card Table）**：见 [CardTable.md](CardTable.md)（最常见的实现）。
  - **粗粒度位图（Coarse-grained Bitmap）**：按 Region 记录。
  - **细粒度哈希表（Fine-grained Per-Region Table）**：记录具体引用地址。

## 关系：上级/下级/等价/特例/推广
- **上级**：GC 数据结构（GC Structure）。
- **下级/实现**：[CardTable.md](CardTable.md)（卡表）。
- **关联**：
  - 服务对象：[Region.md](Region.md)（内存区域）。
  - 维护机制：写屏障（Write Barrier）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → structure → RememberedSet
