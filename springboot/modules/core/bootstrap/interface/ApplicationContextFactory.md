---
title: ApplicationContextFactory（上下文创建工厂）
date: "2026-01-31"
categories:
  - springboot
description: 类型：接口（Interface）/ 扩展点（SPI）
---
# ApplicationContextFactory（上下文创建工厂）

> **类型**：接口（Interface）/ 扩展点（SPI）

## 一句话
`ApplicationContextFactory` 是 Spring Boot 启动期用于创建 `ConfigurableApplicationContext` 的工厂接口：按 `WebApplicationType` 选择并返回合适的上下文实现（或返回 `null` 表示不支持）。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.ApplicationContextFactory` 是一个 `@FunctionalInterface`，其单一抽象方法为 `create(WebApplicationType)`；Boot 可通过 `SpringFactoriesLoader` 发现该接口的实现集合，并在创建 ApplicationContext 时遍历候选工厂，选择首个返回非空 context 的实现作为本次 `run(...)` 的上下文实例。

## 继承链（接口链 / 实现链）
- 接口链：`ApplicationContextFactory`（无上级接口；函数式接口）。
- 常见实现来源：
  - `ApplicationContextFactory.DEFAULT`：以 lambda 表达式提供的默认实现（见“常用构造/操作”）。
  - `SpringFactoriesLoader.loadFactories(ApplicationContextFactory.class, ...)` 发现的第三方/框架实现（Boot 扩展点）。

## 接口：数据 + 约束
- 输入：
  - `webApplicationType: WebApplicationType`（servlet/reactive/none）
- 输出：
  - `ConfigurableApplicationContext | null`
- 约束：
  - `@FunctionalInterface`：该接口仅包含一个抽象方法，因此方法本体由具体实现提供；接口声明处不会包含方法实现体。
  - “是否支持”的表达方式：实现可用“返回 `null`”表示不支持该 `webApplicationType`，由调用方继续遍历其他候选工厂。
  - 执行模型：分派点选择（遍历候选 → 选择匹配者），见 [../../../extension/mechanism/ExtensionExecutionModels.md](../../../extension/mechanism/ExtensionExecutionModels.md)。

## 常用构造/操作（仅列出接口与符号）
- 抽象方法：`create(WebApplicationType webApplicationType): ConfigurableApplicationContext | null`
- 默认实现（语义级描述）：
  - 收集：`SpringFactoriesLoader.loadFactories(ApplicationContextFactory.class, classLoader)`
  - 遍历：对每个 `candidate` 调用 `candidate.create(webApplicationType)`
  - 选择：返回首个非空 `context`
  - 回退：若无候选支持，则返回 `new AnnotationConfigApplicationContext()`

## 关系：上级/下级/等价/特例/推广
- 上级：扩展点发现（见 [../../../extension/mechanism/SpringFactoriesLoader.md](../../../extension/mechanism/SpringFactoriesLoader.md)）。
- 被使用：
  - `SpringApplication.createApplicationContext()`（启动期创建 context 的分派点）：见 [../class/SpringApplication.md](../class/SpringApplication.md)
- 产物：
  - `ConfigurableApplicationContext`：见 [../../context/interface/ConfigurableApplicationContext.md](../../context/interface/ConfigurableApplicationContext.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → bootstrap → interface → ApplicationContextFactory → flows/启动流程。
