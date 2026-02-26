---
title: ConfigurableListableBeanFactory（可配置可枚举 BeanFactory）
date: "2026-01-29"
categories:
  - springboot
description: 类型：接口（Interface）
---
# ConfigurableListableBeanFactory（可配置可枚举 BeanFactory）

> **类型**：接口（Interface）

## 一句话
`ConfigurableListableBeanFactory` 是 refresh 流程中最常用的 BeanFactory 视图：在可配置能力之上提供 BeanDefinition 的可枚举访问与 Bean 实例化控制入口。

## 严格定义
在 Spring Framework 中，`org.springframework.beans.factory.config.ConfigurableListableBeanFactory` 聚合了 listable（按类型枚举）、autowire 解析、以及对 BeanDefinition 与 Bean 创建过程的可配置操作；`AbstractApplicationContext.refresh()` 的多个阶段以该接口为参数类型对 BeanFactory 进行准备、后处理与实例化推进。

## 接口：数据 + 约束
- 输入：
  - BeanDefinition 注册表与依赖解析配置
- 输出：
  - 对 BeanDefinition 枚举与 Bean 创建阶段的控制入口
- 约束：
  - 在 refresh 的阶段边界内，部分操作的可用性与语义受限（例如 Bean 创建后再改 BeanDefinition 的语义由实现与调用点决定）。

## 常用构造/操作（仅列出接口与符号）
- 枚举：`getBeanDefinitionNames()` / `getBeansOfType(type)`
- 访问：`getBeanDefinition(name)`
- 阶段：`preInstantiateSingletons()`（由具体实现提供，默认处理非 lazy 的 singleton Bean）

## 关系：上级/下级/等价/特例/推广
- 上级：`BeanFactory`（见 [BeanFactory.md](BeanFactory.md)）。
- 相关：`BeanDefinitionRegistry`（注册表写入侧）：见 [BeanDefinitionRegistry.md](BeanDefinitionRegistry.md)。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → beans → interface → ConfigurableListableBeanFactory → flows/Bean 注册与创建流程。

