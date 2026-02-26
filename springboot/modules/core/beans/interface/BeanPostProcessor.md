---
title: BeanPostProcessor（Bean 后处理器）
date: "2026-01-31"
categories:
  - springboot
description: 类型：接口（Interface）
---
# BeanPostProcessor（Bean 后处理器）

> **类型**：接口（Interface）

## 一句话
`BeanPostProcessor` 定义了 Bean 实例创建过程中的拦截点：允许在初始化前后对 Bean 实例进行变换（例如包装代理、注入增强或替换返回对象）。

## 严格定义
在 Spring Framework 中，`org.springframework.beans.factory.config.BeanPostProcessor` 是实例级后处理接口；当 BeanFactory 创建 Bean 实例时，会在初始化回调前后分别调用 `postProcessBeforeInitialization` 与 `postProcessAfterInitialization`，以便对实例进行装配期变换。该接口在 `ApplicationContext.refresh()` 的 `registerBeanPostProcessors(beanFactory)` 阶段被收集并注册进 BeanFactory 的处理链。

## 继承链（接口链 / 实现链）
- 接口链：`BeanPostProcessor`（无上级接口）。
- 常见扩展接口：`InstantiationAwareBeanPostProcessor` / `SmartInstantiationAwareBeanPostProcessor` 等（对实例化阶段暴露更细粒度拦截点）。

## 接口：数据 + 约束
- 输入：
  - `Object bean`
  - `String beanName`
- 输出：
  - `Object`（可返回原对象或替换后的对象）
- 约束：
  - 执行次序：多个 BPP 的调用顺序受 `Ordered/@Order` 与注册顺序影响。
  - 注册时机：需要在普通单例大规模实例化之前完成注册，否则部分 Bean 可能不会经过完整 BPP 链（见 [../mechanism/BeanFactoryPostProcessorExecution.md](../mechanism/BeanFactoryPostProcessorExecution.md)）。

## 常用构造/操作（仅列出接口与符号）
- 初始化前：`postProcessBeforeInitialization(bean, beanName)`
- 初始化后：`postProcessAfterInitialization(bean, beanName)`

## 关系：上级/下级/等价/特例/推广
- 上级：Bean 生命周期（创建阶段的实例级拦截点）：见 [../mechanism/BeanLifecycle.md](../mechanism/BeanLifecycle.md)
- 相关：refresh 中注册阶段（见 [springboot/modules/core/context/mechanism/ContextRefresh.md](../../context/mechanism/ContextRefresh.md)）

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → beans → interface → BeanPostProcessor → flows/Bean注册与创建流程。

