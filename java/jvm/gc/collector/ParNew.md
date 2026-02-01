---
type: collector
tags:
  - java/jvm
  - jvm
  - gc
  - collector
---

# ParNew（收集器）

## 一句话
ParNew 是一种以多线程并行执行回收工作为特征的收集器实现（名称、代际定位与可用性依 JVM 版本与实现而定）。

## 严格定义
在收集器分类中，ParNew 描述并行度特征：在关键回收阶段使用多个 GC 工作线程并行完成标记、复制或整理等工作（阶段组合依实现）。其语义可被视为“并行化的回收工作组织方式”的实现实体；是否并发于应用线程、停顿边界与阶段划分依实现决定。

## 接口：数据 + 约束
- 数据：
  - GC 工作线程池（并行度参数依实现）
- 输入：
  - 分配压力与触发条件
- 输出：
  - 回收后的堆空间变化
- 约束：
  - ParNew 与其他收集器的组合关系（例如年轻代/老年代搭配）属于实现与版本范围内的配置空间。

## 常用构造/操作（仅列出接口与符号）
- 上级收集器抽象：见 [GarbageCollector.md](GarbageCollector.md)

## 关系：上级/下级/等价/特例/推广
- 上级：垃圾收集器（见 [GarbageCollector.md](GarbageCollector.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → collector → ParNew

