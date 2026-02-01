---
type: mechanism
tags:
  - java/jvm
  - jvm
  - jit
  - threading
---

# CompilerThreads（编译器线程 / CompilerThread (C1/C2)）

## 一句话
编译器线程是 JVM 运行态用于执行 JIT 编译的线程集合：把热点方法从字节码编译为机器码并进行优化（分层编译时可区分 C1/C2）。

## 严格定义
在具体 JVM 实现（例如 HotSpot/OpenJDK）中，JIT 编译由一个或多个编译器线程执行；它们从编译队列中取出待编译的方法，生成并安装对应的机器码版本，使后续调用可转入已编译代码路径。线程转储常以包含 `CompilerThread`、`C1`、`C2` 等标识的名称模式呈现（依实现与参数而变）。

## 接口：数据 + 约束
- 数据（观测级）：
  - 线程名与状态（可反映是否在编译/等待队列）
  - 栈（可反映编译队列消费与安装路径）
- 输入：
  - 编译请求（热点方法识别/触发产生）
- 输出：
  - 已编译代码的生成与安装（副作用）
- 约束：
  - 编译策略与线程数量依赖 JVM 参数与实现；本页不绑定到某一套参数组合。

## 常用构造/操作（仅列出接口与符号）
- 观测：`jstack <pid>`

## 关系：上级/下级/等价/特例/推广
- 上级：[JvmRuntimeThreads](JvmRuntimeThreads.md)
- 相关：[JIT](../../execution/jit/JIT.md)（即时编译机制）

## 把新概念挂回框架（多级索引轨迹）
java → jvm → runtime → threading → CompilerThreads
