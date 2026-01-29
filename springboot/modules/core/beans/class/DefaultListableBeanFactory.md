# DefaultListableBeanFactory（默认可枚举 BeanFactory）

> **类型**：类（Class）

## 一句话
`DefaultListableBeanFactory` 是 Spring Framework 中常见的 BeanFactory 实现，兼具 BeanDefinitionRegistry 与可枚举能力，并作为多数 ApplicationContext 的内部 BeanFactory 承载体。

## 严格定义
在 Spring Framework 中，`org.springframework.beans.factory.support.DefaultListableBeanFactory` 提供 BeanDefinition 的注册/查询、按类型枚举 Bean、以及单例预实例化等能力；在典型的 `ApplicationContext.refresh()` 流程中，它作为 `ConfigurableListableBeanFactory` 的具体实现参与后处理器执行、作用域注册与 Bean 实例创建。

## 继承链（接口链 / 实现链）
- 继承链：`AbstractBeanFactory` → `DefaultListableBeanFactory`。
- 实现接口（语义级别）：`ConfigurableListableBeanFactory` / `BeanDefinitionRegistry`。

## 接口：数据 + 约束
- 数据（语义级别）：
  - BeanDefinition 注册表（`beanName -> BeanDefinition`）
  - singleton 缓存与 scope 注册表（继承自 `AbstractBeanFactory`）
- 约束：
  - 覆盖策略、依赖解析与类型匹配语义与容器配置相关（例如是否允许 BeanDefinition 覆盖）。

## 常用构造/操作（仅列出接口与符号）
- 注册：`registerBeanDefinition(name, bd)`
- 枚举：`getBeanDefinitionNames()` / `getBeansOfType(type)`
- 实例化推进：`preInstantiateSingletons()`

## 关系：上级/下级/等价/特例/推广
- 上级：`AbstractBeanFactory`（见 [AbstractBeanFactory.md](AbstractBeanFactory.md)）。
- 相关：
  - `ApplicationContext.refresh()`：见 [../../context/mechanism/ContextRefresh.md](../../context/mechanism/ContextRefresh.md)
  - scope 解析机制：见 [../mechanism/ScopeResolution.md](../mechanism/ScopeResolution.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → beans → class → DefaultListableBeanFactory → flows/Bean 注册与创建流程。

