---
type: concept
tags:
  - java/jvm
  - jvm
  - runtime
  - structure
  - pc
---

# ProgramCounter（程序计数器）

## 一句话
程序计数器（Program Counter）是线程私有的执行位置指示：指向当前线程将要执行的字节码指令（或等价表示）。

## 严格定义
对每个线程，JVM 需要维护一个“下一条要执行的指令位置”的状态，以支持解释执行与异常处理等机制；该状态可用程序计数器抽象表示。其具体表示形式与在 JIT 编译等情况下的对应关系由 JVM 实现决定（实现相关）。

## 接口：数据 + 约束
- 数据：
  - 当前执行位置（字节码索引/机器码位置映射等，实现相关）
- 输入：
  - 指令执行推进、分支跳转、异常转移
- 输出：
  - 调试/异常栈追踪等机制所需的定位信息（实现相关）
- 约束：
  - 程序计数器是线程私有状态；不承载对象实例存储。

## 常用构造/操作（仅列出接口与符号）
- 线程与执行：见 [../threading/JvmRuntimeThreads.md](../threading/JvmRuntimeThreads.md)

## 关系：上级/下级/等价/特例/推广
- 上级：Runtime / structure（运行时数据区结构）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → runtime → structure → ProgramCounter

