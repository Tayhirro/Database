---
title: TemplateMethod（模板方法）
date: "2026-01-28"
categories:
  - springboot
description: 类型：模式（Pattern）
---
# TemplateMethod（模板方法）

> **类型**：模式（Pattern）

## 一句话
Template Method 是一种将算法主流程固定在基类中，并将若干可变步骤以 hook/抽象方法形式交给子类或扩展点实现的组织方式。

## 严格定义
给定一个流程 $F$，若存在一个“模板方法” $T$ 在稳定位置上调用步骤集合 $\{s_i\}$，并且其中至少一个步骤由子类覆写或由外部策略实现，则称该设计满足 Template Method；其中 $T$ 决定步骤调用顺序与阶段边界，$\{s_i\}$ 的实现决定各阶段的具体行为。

## 接口：数据 + 约束
- 数据（参与者/要素）：
  - 模板方法 `T()`：固定的阶段顺序与调用点
  - hook/抽象步骤集合 `s1..sn`：可变实现点
  - 扩展载体：子类覆写、回调接口、注册表/容器扩展点（取其一或组合）
- 约束：
  - 变更应落在步骤实现中；模板方法只负责流程编排与阶段边界。
  - hook 的可见性与调用时机由模板方法定义；扩展点应满足“在调用点之前可被发现/注册”。

## 常用构造/操作（仅列出接口与符号）
- 固定主流程：`T() { pre(); s1(); s2(); post(); }`
- 可变步骤：
  - 子类覆写：`protected void s1()` / `protected abstract void s2()`
  - 回调扩展：`List<Callback>` / `invokeCallbacks(stage)`

## 关系：上级/下级/等价/特例/推广
- 上级：框架主控流（Framework Control Flow）。
- 相关：
  - 两阶段初始化（Two-Phase Initialization）：见 [TwoPhaseInitialization.md](TwoPhaseInitialization.md)
- 例化：
  - `AbstractApplicationContext.refresh()`：见 [../../core/context/mechanism/ContextRefresh.md](../../core/context/mechanism/ContextRefresh.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → patterns → pattern → TemplateMethod → core/context/mechanism/ContextRefresh。
