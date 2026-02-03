---
type: structure
kind: class
tags:
  - java/jvm
  - jvm
  - gc
  - structure
---

# Region（内存区域）

## 一句话
Region（内存区域）是堆内存的**逻辑或物理划分单元**，是垃圾收集器（如 G1, Shenandoah, ZGC）进行资源管理和回收调度的基本粒度。

## 严格定义
在分区（Region-based）堆模型中，连续的 Java 堆空间被划分为多个大小相等的独立块（Region）。每个 Region 在任意时刻可以扮演特定的逻辑角色（如 Eden, Survivor, Old, Humongous）。
与传统分代模型（连续的大块物理内存作为 Generation）不同，Region 允许逻辑上的分代在物理上不连续。

## 接口：数据 + 约束
- **属性**：
  - `Bottom` / `Top` / `End`：地址边界。
  - `Type`：角色类型（Eden/Survivor/Old/Humongous/Free）。
  - `RSet`：关联的记忆集（[RememberedSet.md](RememberedSet.md)）。
    - **注**：为性能考虑，**Young Region 通常不维护 RSet**（如 G1），因为其对象更新频繁且总是被整体回收。
- **操作**：
  - `Allocation`：在 Region 内 TLAB 或直接分配。
  - `Evacuation`：将存活对象复制到另一 Region。
- **约束**：
  - **大小**：通常为 2 的幂次（1MB - 32MB，由 `-XX:G1HeapRegionSize` 指定）。
  - **Humongous 对象**：超过 Region 容量一半的对象需分配在连续的 Humongous Regions 中。

## 常用构造/操作（仅列出接口与符号）
- **G1 Region**：包含 RSet。
- **ZGC Region (Page)**：分为 Small/Medium/Large，支持动态大小。
- **Shenandoah Region**：类似 G1，但不维护重型 RSet（使用连接矩阵或卡表）。

## 关系：上级/下级/等价/特例/推广
- **上级**：[../../runtime/structure/Heap.md](../../runtime/structure/Heap.md)（堆）。
- **特例**：
  - Generation（传统分代，物理连续的大 Region）。
  - G1 Heap Region。
- **关联**：
  - 辅助结构：[RememberedSet.md](RememberedSet.md)（每个 Region 可能拥有一个 RSet）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → structure → Region
