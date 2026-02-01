---
type: algorithm
tags:
  - java/jvm
  - jvm
  - gc
  - algorithm
---

# MarkCompact（标记-整理）

## 一句话
标记-整理（Mark-Compact）是一类 GC 算法：先标记存活对象，再通过搬迁/压缩将存活对象整理到一端，释放连续空间。

## 严格定义
标记阶段计算存活集合 $L$；整理阶段为 $L$ 中对象计算新位置并搬迁对象，最终使得空闲空间在堆中形成更连续的区间。与 Mark-Sweep 相比，Mark-Compact 通过搬迁降低碎片化，但引入对象移动与引用更新成本。

## 接口：数据 + 约束
- 数据：
  - 标记信息（存活集合）
  - 搬迁计划/转发表（实现定义）
- 输入：
  - Roots 与引用图
- 输出：
  - 更连续的可用空间与更新后的引用
- 约束：
  - 整理阶段通常需要全局一致性边界或等价机制以保证引用更新的安全性（机制依收集器实现）。

## 常用构造/操作（仅列出接口与符号）
- Mark-Sweep（对比）：见 [MarkSweep.md](MarkSweep.md)

## 关系：上级/下级/等价/特例/推广
- 上级：GC 概述（见 [../mechanism/GCOverview.md](../mechanism/GCOverview.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → algorithm → MarkCompact

