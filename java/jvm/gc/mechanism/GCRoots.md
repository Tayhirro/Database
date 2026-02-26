---
title: GCRoots（GC Roots）
date: "2026-02-02"
categories:
  - java
tags:
  - java/jvm
  - jvm
  - gc
  - mechanism
description: GC Roots 是可达性分析的起点集合：从这些根对象出发可达的对象被判定为存活（在引用类型规则之外）。
type: mechanism
---
# GCRoots（GC Roots）

## 一句话
GC Roots 是可达性分析的起点集合：从这些根对象出发可达的对象被判定为存活（在引用类型规则之外）。

## 严格定义
对一次 GC，设根集合为 $R \subseteq O$（对象集合）。$R$ 的元素由 JVM 在特定一致性条件下枚举得到，用于定义可达性关系并作为存活判定的起点（见 [ReachabilityAnalysis.md](ReachabilityAnalysis.md)）。

根集合的构成是实现定义的；常见根来源包括：
- 线程栈上的局部变量与参数引用（栈帧引用）；
- 方法区/元数据中的静态引用；
- JNI（本地代码）持有的引用；
- 运行时内部数据结构持有的引用（集合与枚举方式依实现）。

## 接口：数据 + 约束
- 数据：
  - 根集合 $R$
- 输入：
  - 线程与运行时结构的枚举视图（需要一致性保障）
- 输出：
  - Roots 枚举结果（对象引用集合）
- 约束：
  - Roots 枚举通常要求在安全点或等价的一致性边界内进行（见 [SafePoint.md](SafePoint.md)）。

## 常用构造/操作（仅列出接口与符号）
- 可达性判定：见 [ReachabilityAnalysis.md](ReachabilityAnalysis.md)

## 关系：上级/下级/等价/特例/推广
- 上级：可达性分析（见 [ReachabilityAnalysis.md](ReachabilityAnalysis.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → mechanism → GCRoots

