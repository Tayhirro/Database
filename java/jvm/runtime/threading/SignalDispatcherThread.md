---
type: mechanism
tags:
  - springboot/jvm
  - jvm
  - signal
  - threading
---

# SignalDispatcherThread（Signal Dispatcher 线程）

## 一句话
Signal Dispatcher 线程是 JVM 在运行态用于接收与分发进程信号相关事件的内部服务线程（以 HotSpot/OpenJDK 为例）。

## 严格定义
在 HotSpot/OpenJDK 的实现模型中，JVM 会处理来自操作系统的进程信号（例如中断、退出、诊断等类别）；这些信号事件会由 JVM 的信号处理与分发机制推进到内部处理路径，并可能触发 Java 层或 JVM 内部的相应行为。线程转储工具通常以 `Signal Dispatcher` 等名称模式展示该线程（依实现而变）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - 进程信号事件流（概念级）
  - 信号到处理路径的映射（概念级）
- 输入：
  - OS 信号事件
- 输出：
  - 内部处理路径被触发的效果（副作用）
- 约束：
  - 不同平台/不同 JVM 实现对信号的处理策略可能不同；本页只描述“存在专用线程承担信号分发职责”的结构边界。

## 常用构造/操作（仅列出接口与符号）
- 观测：`jstack <pid>`

## 关系：上级/下级/等价/特例/推广
- 上级：JVM Runtime Threads（见 [JvmRuntimeThreads.md](JvmRuntimeThreads.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → jvm → mechanism → SignalDispatcherThread。

