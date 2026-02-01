---
type: concept
tags:
  - java/jvm
  - jvm
  - tuning
  - analysis
  - threading
---

# ThreadDump（线程转储）

## 一句话
线程转储（Thread Dump）是某一时刻 JVM 进程内线程集合及其栈帧状态的快照，用于分析阻塞、死锁与运行态调用路径。

## 严格定义
对一个 JVM 进程在时间点 $t$，线程转储是一个包含线程集合 $T$ 的状态快照：每个线程记录其实现定义的状态字段（例如 runnable/blocked/waiting 等）与一段栈帧序列（方法调用链）。线程转储的输出格式与字段集合依 JVM 实现与工具而定；本页仅描述其作为“线程状态快照”的语义边界。

## 接口：数据 + 约束
- 输入：
  - 目标 JVM 进程（PID）
  - 触发转储的工具命令与参数
- 输出：
  - 文本/文件形式的线程转储（副作用）
- 约束：
  - 获取栈帧通常需要一致性边界；开销与停顿特征依实现与工具而定。

## 常用构造/操作（仅列出接口与符号）
- 触发工具（示例）：`jstack`（见 [../tools/jstack.md](../tools/jstack.md)）

## 关系：上级/下级/等价/特例/推广
- 上级：Tuning / analysis（分析方法）。
- 相关：JVM 运行态线程（见 [../../runtime/threading/JvmRuntimeThreads.md](../../runtime/threading/JvmRuntimeThreads.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → tuning → analysis → ThreadDump

