# AbstractBeanFactory（BeanFactory 抽象基类）

> **类型**：类（Class）

## 一句话
`AbstractBeanFactory` 是 BeanFactory 的抽象基类，提供 doGetBean 与 scope 解析等通用创建/缓存骨架，并维护 scopeName 到 `Scope` 的注册表。

## 严格定义
在 Spring Framework 中，`org.springframework.beans.factory.support.AbstractBeanFactory` 作为 BeanFactory 的基础实现，维护 singleton 缓存与 `scopes` 注册表；当 BeanDefinition 的 scopeName 非空且非内置 scope 时，它通过 `scopes.get(scopeName)` 获取 `Scope` 实现并委托 `Scope.get(beanName, factory)` 完成实例获取/创建。

## 继承链（接口链 / 实现链）
- 继承链：`java.lang.Object` → `AbstractBeanFactory` → `DefaultListableBeanFactory`（沿着支持层次向下扩展）。
- 实现接口：`ConfigurableBeanFactory`（以及其上游的 `BeanFactory`）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - singleton 缓存（`beanName -> instance`）
  - scope 注册表（`scopeName -> Scope`）
- 约束：
  - 当 `scopeName` 非空且在注册表中不存在同名 `Scope` 时，实例解析失败（见 [../mechanism/ScopeResolution.md](../mechanism/ScopeResolution.md)）。

## 常用构造/操作（仅列出接口与符号）
- 获取：`doGetBean(name, requiredType, args, typeCheckOnly)`（内部骨架）
- scope：`registerScope(name, scope)`

## 关系：上级/下级/等价/特例/推广
- 上级：`BeanFactory` / `ConfigurableBeanFactory`（契约接口）。
- 下级：`DefaultListableBeanFactory`（常见 concrete 实现）：见 [DefaultListableBeanFactory.md](DefaultListableBeanFactory.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → beans → class → AbstractBeanFactory → flows/Bean 注册与创建流程。

