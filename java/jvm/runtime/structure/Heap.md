---
type: concept
tags:
  - java/jvm
  - jvm
  - runtime
  - structure
  - heap
---

# Heap（堆）

## 一句话
堆（Heap）是 JVM 运行时数据区中用于分配对象实例的内存区域，也是垃圾回收（GC）的主要管理对象。

## 严格定义
对一个 JVM 进程，堆是由 JVM 管理的一段（或多段）虚拟地址空间，用于承载对象实例及其引用关系形成的对象图。堆的管理语义由垃圾收集（GC）子系统定义：对象的分配、存活判定、回收、以及（若存在）整理/搬迁都发生在堆的语义边界内。

“堆”与“堆外内存（off-heap/direct memory）”在语义上区分：后者不属于堆的管理边界，通常不由堆 GC 直接回收。

## 接口：数据 + 约束
- 数据：
  - 对象实例与引用图（对象字段形成的引用关系）
  - 代/区（若收集器采用分代或分区/region 模型）
- 输入：
  - 分配请求（对象创建导致的内存分配）
  - GC 触发与回收目标（由收集器与参数决定）
- 输出：
  - 分配结果（对象在堆中的位置/引用可达）
  - 回收结果（释放空间、整理/搬迁后的布局变化）
- 约束：
  - 堆的大小边界由参数与实现限制决定（见 [../../tuning/parameters/HeapParameters.md](../../tuning/parameters/HeapParameters.md)）。

## 常用构造/操作（仅列出接口与符号）
- 堆边界参数：见 [../../tuning/parameters/HeapParameters.md](../../tuning/parameters/HeapParameters.md)
- 垃圾回收：见 [../../gc/README.md](../../gc/README.md)

## 关系：上级/下级/等价/特例/推广
- 上级：Runtime / structure（运行时数据区结构）。
- 相关：GC（见 [../../gc/mechanism/GCOverview.md](../../gc/mechanism/GCOverview.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → runtime → structure → Heap

