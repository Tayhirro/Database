---
type: concept
tags:
  - java/jvm
  - jvm
  - tuning
  - parameters
  - gc
---

# GCParameters（GC 参数）

## 一句话
GC 参数是 JVM 启动参数集合中用于选择垃圾收集器与控制其行为边界（堆大小、并发/并行度、停顿目标、日志输出等）的参数子集。

## 严格定义
对一个 JVM 进程，设启动参数集合为 $P$。GC 参数子集 $P_{gc} \subseteq P$ 作用于 GC 相关模块：收集器选择、堆与代/区的布局、触发阈值、并行/并发线程数、停顿目标与节流策略，以及 GC 日志/事件输出。由于参数集合与语义依 JDK 版本与 JVM 实现变化，本页以“参数类别”描述其统一接口，并将具体选项视为实现细节。

## 接口：数据 + 约束
- 输入：
  - JVM 启动参数（命令行）
- 输出：
  - GC 行为边界与观测输出的变化（副作用）
- 约束：
  - 参数的存在性、默认值与交互依赖 JDK 版本（例如 JDK 8 与 JDK 9+ 的日志参数体系不同）。

## 常用构造/操作（仅列出接口与符号）

### A. 收集器选择（Collector selection）
- 语义：选择某个收集器实现作为本进程的 GC 策略（选项名依 JDK/JVM 实现）。

### B. 堆与代/区边界（Heap sizing / layout）
- 语义：控制堆大小与布局，从而影响分配与回收的边界条件（例如 `-Xms/-Xmx` 等属于同类边界；见 [HeapParameters.md](HeapParameters.md)）。

### C. 并行/并发线程组织（Parallelism / concurrency）
- 语义：控制 GC 阶段的并行线程与并发阶段线程组织方式（参数名依收集器）。

### D. 目标与约束（Goals）
- 语义：以目标形式表达停顿、吞吐或其他约束（参数名依收集器）。

### E. 日志与事件输出（Logging）
- 语义：输出 GC 事件与阶段信息（见 [../analysis/GCLog.md](../analysis/GCLog.md)）。
  - JDK 8 常见口径：`-XX:+PrintGC*`、`-Xloggc:<file>`
  - JDK 9+ 常见口径：`-Xlog:gc*`（统一日志）

## 关系：上级/下级/等价/特例/推广
- 上级：GC（见 [../../gc/README.md](../../gc/README.md)）。
- 相关：
  - GC 日志（见 [../analysis/GCLog.md](../analysis/GCLog.md)）
  - 垃圾收集器（见 [../../gc/collector/GarbageCollector.md](../../gc/collector/GarbageCollector.md)）

## 把新概念挂回框架（多级索引轨迹）
java → jvm → tuning → parameters → GCParameters

