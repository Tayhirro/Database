---
type: concept
tags:
  - java/jvm
  - jvm
  - tuning
  - analysis
  - heap
---

# HeapDump（堆转储）

## 一句话
堆转储（Heap Dump）是对某一时刻 JVM 堆（Heap）对象图与相关元数据的快照，用于离线分析对象占用与引用关系。

## 严格定义
对一个 JVM 进程在时间点 $t$，堆转储是一个包含堆对象集合、对象字段引用关系、以及实现定义附加信息（如类元数据标识、对象大小、线程/根信息的部分视图）的数据文件。堆转储的格式与字段集合依 JVM 实现与工具而定；本页仅描述其作为“堆快照”的语义边界。

## 接口：数据 + 约束
- 输入：
  - 目标 JVM 进程（PID）
  - 触发转储的工具命令与参数（实现/工具定义）
- 输出：
  - 堆转储文件（副作用）
- 约束：
  - 转储过程可能需要一致性边界，并可能导致停顿或额外开销（幅度依实现与工具选择而定）。

## 常用构造/操作（仅列出接口与符号）
- 触发工具（示例）：`jmap`（见 [../tools/jmap.md](../tools/jmap.md)）

## 关系：上级/下级/等价/特例/推广
- 上级：Tuning / analysis（分析方法）。
- 相关：堆（见 [../../runtime/structure/Heap.md](../../runtime/structure/Heap.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → tuning → analysis → HeapDump

