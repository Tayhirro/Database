---
title: BeanFactoryPostProcessorExecution（BFPP/BDRPP 执行与断环）
date: "2026-01-29"
categories:
  - springboot
description: 类型：机制（Mechanism）
---
# BeanFactoryPostProcessorExecution（BFPP/BDRPP 执行与断环）

> **类型**：机制（Mechanism）

## 一句话
`BeanFactoryPostProcessor`（BFPP）与 `BeanDefinitionRegistryPostProcessor`（BDRPP）的执行机制是在 `refresh()` 早期以“先实例化后回调”的方式对 BeanDefinition 注册表与 BeanFactory 进行变换，并通过阶段顺序将其与 `BeanPostProcessor`（BPP）链和业务 Bean 的大规模实例化解耦。

## 严格定义
给定 BeanDefinition 注册表 $R$ 与 BeanFactory $F$，以及后处理器实现集合：
- $P_R$：所有 BDRPP 实例（可由 BeanDefinition 指定或由外部编程式注册）
- $P_F$：所有 BFPP 实例（含 BDRPP 作为 BFPP 的子集语义）

在一次 `ApplicationContext.refresh()` 中，存在一段阶段化流程使得：
1) 在任何非基础设施的单例 Bean 大规模创建之前，按确定顺序实例化并回调 $P_R$ 与 $P_F$，以允许对 $R$ 中的 BeanDefinition 做增删改与派生注册；
2) 在 BPP 列表被完整注册之前允许以最小创建能力实例化 $P_R/P_F$；
3) 在 $P_R/P_F$ 回调完成后，才进入 BPP 注册与普通单例实例化阶段。

该流程使“后处理器本身也是 Bean”与“后处理器需要在 Bean 大规模创建前生效”同时成立。

## 接口：数据 + 约束
- 数据：
  - BeanDefinition 注册表 $R$（`beanName -> BeanDefinition`）
  - 后处理器集合（BDRPP/BFPP/BPP）
- 输入：
  - `BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry(registry)`
  - `BeanFactoryPostProcessor#postProcessBeanFactory(beanFactory)`
  - `BeanPostProcessor` 的注册（`addBeanPostProcessor`）
- 输出：
  - $R$ 的变换（新增/移除/替换 BeanDefinition）
  - $F$ 的变换（属性占位符、解析策略、可解析依赖等实现语义范围内的变更）
- 约束：
  - BDRPP/BFPP 必须在普通单例 Bean 大规模实例化之前完成回调，否则对 BeanDefinition 的修改无法影响已创建实例。
  - BPP 的注册必须在普通单例 Bean 大规模实例化之前完成，否则实例创建不会经过完整的 BPP 链。

## 常用构造/操作（仅列出接口与符号）
- BDRPP：`postProcessBeanDefinitionRegistry(registry)` / `postProcessBeanFactory(beanFactory)`
- BFPP：`postProcessBeanFactory(beanFactory)`
- BPP：`postProcessBeforeInitialization` / `postProcessAfterInitialization`

## 阶段化执行（refresh 视角）
以 `AbstractApplicationContext.refresh()` 的阶段顺序表达：
- BFPP/BDRPP：发生于 `invokeBeanFactoryPostProcessors(beanFactory)`
  - 执行目标：对 BeanDefinition 注册表与 BeanFactory 做变换
  - 运行位置：在 `registerBeanPostProcessors(beanFactory)` 之前
- BPP：发生于 `registerBeanPostProcessors(beanFactory)`
  - 执行目标：向 BeanFactory 注册实例级后处理链
- 普通单例创建：发生于 `finishBeanFactoryInitialization(beanFactory)`（或其内部等价步骤）

## 断环条件（Bootstrap window）
在 BDRPP/BFPP 被实例化并回调的窗口期内：
- BPP 链尚未被完整注册；
- 后处理器实例的创建使用 BeanFactory 的最小创建能力，以便获取“可调用的后处理器对象”；
- 该窗口期内创建的其他 Bean 可能不经过完整 BPP 链，从而在日志中出现“未被所有 BeanPostProcessors 处理”的提示（具体文案取决于版本与触发条件）。

## 关系：上级/下级/等价/特例/推广
- 上级：`ApplicationContext.refresh()` 的阶段化模板流程（见 [../../context/mechanism/ContextRefresh.md](../../context/mechanism/ContextRefresh.md)）。
- 相关：
  - Bean 注册与创建流程（见 [springboot/flows/Bean注册与创建流程.md](../../../../flows/Bean注册与创建流程.md)）
  - `DefaultListableBeanFactory`（注册表与实例化承载体之一）：见 [../class/DefaultListableBeanFactory.md](../class/DefaultListableBeanFactory.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → beans → mechanism → BeanFactoryPostProcessorExecution → flows/Bean注册与创建流程。

