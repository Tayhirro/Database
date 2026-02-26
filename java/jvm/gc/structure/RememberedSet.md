---
title: RememberedSet（记忆集）
date: "2026-02-04"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - gc
  - structure
description: "记忆集（Remembered Set, RSet）是一种用于记录从非收集区域指向收集区域的指针集合的抽象数据结构，旨在避免在部分收集（Partial GC）时扫描整个堆。"
type: structure
kind: interface
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
- **Points-out RSet（我引用了谁）**：
  - **定义**：记录**来源区域**（Source）指向了哪些外部区域。
  - **典型实现**：**卡表（Card Table）**。
    - **例子**：老年代（Source）的卡页被标记为 Dirty，隐含表示“我（老年代）引用了新生代（Target）”。
  - **场景**：适用于 Source 变动频繁但 Target 固定的场景（如传统分代 GC，Target 总是新生代）。
- **Points-in RSet（谁引用了我）**：
  - **定义**：记录**目标区域**（Target）被哪些外部区域引用。
  - **典型实现**：**G1 的 RSet**（哈希表）。
    - **例子**：Region A（Target）内部维护一个列表 `{Region B, Region C}`，明确记录“Region B 和 C 引用了我”。
  - **场景**：适用于需要独立回收任意 Region 的场景（如 G1, Shenandoah），回收 Region A 时直接查 RSet 即可知 GC Roots。
    - **特例**：**Young Region 通常不维护**。因新生代引用变更频繁且总是整体回收，维护 RSet 开销大于收益（通常改用扫描全局 Dirty Cards）。
  - **核心价值**：解决**部分老年代回收**（Mixed GC）的效率问题。
    - 若仅用卡表（Points-out），要回收特定的老年代区域 $X$，必须遍历全堆的卡表来寻找指向 $X$ 的引用（反向查询成本 $O(\text{Heap})$）。
    - 使用 Points-in，区域 $X$ 直接记录了引用来源，扫描成本仅为 $O(\text{IncomingRefs})$。
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
