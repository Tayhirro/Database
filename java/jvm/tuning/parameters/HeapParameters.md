---
type: concept
tags:
  - java/jvm
  - jvm
  - tuning
  - parameters
  - heap
---

# HeapParameters（堆参数）

## 一句话
堆参数（Heap Parameters）是 JVM 启动参数中用于限定堆大小与相关布局边界（例如初始/最大堆）的参数子集。

## 严格定义
对一个 JVM 进程，设启动参数集合为 $P$。堆参数子集 $P_{heap} \subseteq P$ 通过实现定义的方式约束堆（见 [../../runtime/structure/Heap.md](../../runtime/structure/Heap.md)）的容量上下界与部分布局边界，从而影响对象分配与 GC 的触发条件空间。常见语义包括：
- 初始堆容量与最大堆容量边界；
- 年轻代与老年代（或等价区域集合）的容量边界（若采用分代/分区模型）。

参数名与解释方式随 JVM 实现与版本变化，本页只描述参数类别语义，不将具体默认值视为稳定规则。

## 接口：数据 + 约束
- 输入：
  - JVM 启动参数（命令行）
- 输出：
  - 堆容量边界与布局边界的变化（副作用）
- 约束：
  - 与 GC 选择参数、目标参数存在交互（见 [GCParameters.md](GCParameters.md)）。

## 常用构造/操作（仅列出接口与符号）
- 容量边界（示例）：
  - `-Xms`：初始堆大小
  - `-Xmx`：最大堆大小
- 与 GC 参数关系：见 [GCParameters.md](GCParameters.md)

## 关系：上级/下级/等价/特例/推广
- 上级：调优参数（Tuning / parameters）。
- 相关：堆（见 [../../runtime/structure/Heap.md](../../runtime/structure/Heap.md)）。
- 相关：GC 参数（见 [GCParameters.md](GCParameters.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → tuning → parameters → HeapParameters

