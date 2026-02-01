---
type: concept
tags:
  - java/jvm
  - jvm
  - runtime
  - structure
  - direct-memory
---

# DirectMemory（直接内存）

## 一句话
直接内存（Direct Memory）是 Java 进程可使用的堆外内存形态之一，常用于 NIO 的 direct buffer 等场景。

## 严格定义
直接内存用于描述不在 Java 堆内分配的内存区域（off-heap）：其分配与释放由具体 API 与 JVM 实现协作完成，通常不受堆 GC 的直接管理，但其可达性与回收时机可能与 Java 对象的可达性相关联（实现相关）。

## 接口：数据 + 约束
- 数据：
  - 堆外内存块与其 Java 侧引用对象（例如 direct buffer，对象类型实现相关）
- 输入：
  - 堆外分配请求（例如 NIO direct buffer 创建）
- 输出：
  - 堆外内存块的生命周期变化（分配/释放）
- 约束：
  - 直接内存不属于堆的语义边界（见 [Heap.md](Heap.md)）；上限与回收机制由实现与参数决定（实现相关）。

## 常用构造/操作（仅列出接口与符号）
- 直接内存上限参数：`-XX:MaxDirectMemorySize`（实现相关）

## 关系：上级/下级/等价/特例/推广
- 上级：Runtime / structure（运行时数据区结构）。
- 相关：Heap（堆，见 [Heap.md](Heap.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → runtime → structure → DirectMemory

