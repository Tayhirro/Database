# BeanFactory（Bean 工厂）

> **类型**：接口（Interface）

## 一句话
`BeanFactory` 是 Spring 容器的最小 Bean 获取与依赖解析契约，定义了按名称/类型获取 Bean 的入口与相关异常语义。

## 严格定义
在 Spring Framework 中，`org.springframework.beans.factory.BeanFactory` 定义了 Bean 的检索与依赖解析的最小接口集合；`ApplicationContext` 在接口层面扩展了 `ListableBeanFactory` 等能力，但常见实现将具体创建与缓存委托给内部 `BeanFactory` 实例完成。

## 继承链（接口链 / 实现链）
- 接口链：
  - `BeanFactory`（按名称/类型获取 Bean 的最小契约）
  - `HierarchicalBeanFactory`（extends `BeanFactory`：补充父子容器层级访问）
  - `ListableBeanFactory`（extends `BeanFactory`：补充按类型/注解枚举 Bean 的能力）
- 常见实现链：`AbstractBeanFactory` → `DefaultListableBeanFactory`（见 [../class/AbstractBeanFactory.md](../class/AbstractBeanFactory.md)、[../class/DefaultListableBeanFactory.md](../class/DefaultListableBeanFactory.md)）。

## 接口：数据 + 约束
- 输入：
  - BeanName（字符串）或类型（Class/ResolvableType）
- 输出：
  - Bean 实例（按 scope 语义返回）
- 约束：
  - 是否存在、是否唯一、是否允许提前引用等语义由具体实现与 BeanDefinition 决定。
  - scope 的解析依赖 BeanFactory 中已注册的 `Scope`（见 [../mechanism/ScopeResolution.md](../mechanism/ScopeResolution.md)）。

## 常用构造/操作（仅列出接口与符号）
- 获取：`getBean(name)` / `getBean(name, type)` / `getBean(type)`
- 查询：`containsBean(name)` / `isSingleton(name)` / `isPrototype(name)`

## 关系：上级/下级/等价/特例/推广
- 上级：依赖注入容器（DI container）的 Bean 检索契约。
- 相关：
  - `ApplicationContext`（组合并扩展 BeanFactory 能力）：见 [../../context/interface/ApplicationContext.md](../../context/interface/ApplicationContext.md)
  - `BeanDefinition`（描述 Bean 的元数据）：见 [BeanDefinition.md](BeanDefinition.md)
  - `Scope`（决定实例存取上下文）：见 [Scope.md](Scope.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → beans → interface → BeanFactory → flows/Bean 注册与创建流程。
