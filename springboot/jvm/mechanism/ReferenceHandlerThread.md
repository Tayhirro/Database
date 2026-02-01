---
type: mechanism
tags:
  - springboot/jvm
  - jvm
  - reference
  - threading
---

# ReferenceHandlerThread（Reference Handler 线程）

## 一句话
Reference Handler 线程是 JVM 在运行态用于处理待处理引用（reference processing）并将其入队到 `ReferenceQueue` 的内部服务线程（以 HotSpot/OpenJDK 为例）。

## 严格定义
在 HotSpot/OpenJDK 的实现模型中，JVM 维护待处理引用集合（例如在 GC 或运行态处理过程中产生），并由专用线程负责将这些引用对象推进到 Java 层可观测的队列语义（例如入队到 `ReferenceQueue`），从而使引用消费者能够在应用层拉取并处理这些引用事件。

## 接口：数据 + 约束
- 数据（语义级别）：
  - 待处理引用集合（pending references）
  - `ReferenceQueue`
- 输入：
  - “引用需要入队”的运行态事件
- 输出：
  - 引用对象入队到 `ReferenceQueue` 的效果（副作用）
- 约束：
  - 引用处理的具体触发时机与并发性依赖 GC 与 JVM 实现；本页只描述线程职责边界。

## 常用构造/操作（仅列出接口与符号）
- 观测：`jstack <pid>`

## 关系：上级/下级/等价/特例/推广
- 上级：JVM Runtime Threads（见 [JvmRuntimeThreads.md](JvmRuntimeThreads.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → jvm → mechanism → ReferenceHandlerThread。

