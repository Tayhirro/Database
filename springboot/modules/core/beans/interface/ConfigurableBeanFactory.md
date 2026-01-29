# ConfigurableBeanFactory（可配置 BeanFactory）

> **类型**：接口（Interface）

## 一句话
`ConfigurableBeanFactory` 在 `BeanFactory` 的基础上补充容器内部设施的可配置能力，包括 Scope 注册、别名/解析策略与 BeanPostProcessor 等扩展点的接入。

## 严格定义
在 Spring Framework 中，`org.springframework.beans.factory.config.ConfigurableBeanFactory` 定义了 BeanFactory 的可配置扩展面；其中 `registerScope(String, Scope)` 用于将 scopeName 绑定到 `Scope` 实现，使 BeanDefinition 上的 scopeName 在运行时可被解析为具体的实例存取策略。

## 继承链（接口链 / 实现链）
- 接口链：`BeanFactory` → `ConfigurableBeanFactory`。
- 常见实现链：`AbstractBeanFactory`（implements）→ `DefaultListableBeanFactory`。

## 接口：数据 + 约束
- 数据（语义级别）：
  - Scope 注册表：`Map<String, Scope>`（由实现维护）
- 输入：
  - `registerScope(scopeName, scope)`：scope 名称与实现
- 输出：
  - 无返回值（副作用为更新 scope 注册表）
- 约束：
  - `scopeName` 必须与 BeanDefinition 中声明的 scopeName 一致，才能在解析阶段命中（见 [../mechanism/ScopeResolution.md](../mechanism/ScopeResolution.md)）。

## 常用构造/操作（仅列出接口与符号）
- scope：`registerScope(name, scope)` / `getRegisteredScope(name)`
- 处理链：`addBeanPostProcessor(bpp)`（实例创建链路扩展）

## 关系：上级/下级/等价/特例/推广
- 下级：Scope 解析机制（见 [../mechanism/ScopeResolution.md](../mechanism/ScopeResolution.md)）。
- 相关：Web scopes 的注册（见 [../../../web/mechanism/WebScopes.md](../../../web/mechanism/WebScopes.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → beans → interface → ConfigurableBeanFactory → flows/Bean 注册与创建流程。

