# BeanDefinitionRegistry（BeanDefinition 注册表）

> **类型**：接口（Interface）

## 一句话
`BeanDefinitionRegistry` 定义了 BeanDefinition 的注册与查询契约，用于在 refresh 早期将配置来源解析结果写入容器注册表。

## 严格定义
在 Spring Framework 中，`org.springframework.beans.factory.support.BeanDefinitionRegistry` 提供 `registerBeanDefinition(beanName, beanDefinition)` 等操作，使 BeanDefinition 的产生（解析/扫描/导入）与 Bean 的创建（由 BeanFactory 执行）解耦；`DefaultListableBeanFactory` 同时实现该接口并作为常见注册表承载体。

## 继承链（接口链 / 实现链）
- 接口链：`BeanDefinitionRegistry`（无上级接口）。
- 常见实现：`DefaultListableBeanFactory`。

## 接口：数据 + 约束
- 输入：
  - `beanName: String`
  - `beanDefinition: BeanDefinition`
- 输出：
  - 无返回值（副作用为写入注册表）
- 约束：
  - 同名覆盖、别名与冲突处理语义由实现与配置决定（例如是否允许覆盖）。

## 常用构造/操作（仅列出接口与符号）
- 注册：`registerBeanDefinition(name, bd)`
- 查询：`containsBeanDefinition(name)` / `getBeanDefinition(name)` / `getBeanDefinitionCount()`

## 关系：上级/下级/等价/特例/推广
- 下游：`BeanFactory` 在创建 Bean 时读取 BeanDefinition（见 [BeanFactory.md](BeanFactory.md)、[BeanDefinition.md](BeanDefinition.md)）。
- 相关：refresh 中 BeanDefinition 的增删改（BeanFactoryPostProcessor）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → beans → interface → BeanDefinitionRegistry → flows/Bean 注册与创建流程。

