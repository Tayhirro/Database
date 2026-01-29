# ScopeResolution（Scope 注册与解析）

> **类型**：机制（Mechanism）

## 一句话
Scope 注册与解析机制描述了 BeanDefinition 的 `scopeName` 如何与 BeanFactory 内部的 scope 注册表匹配，并在创建/获取 Bean 时委托给对应的 `Scope` 完成实例存取。

## 严格定义
给定 BeanDefinition $d$ 与其 `scopeName` $s$，以及 BeanFactory 中的 scope 注册表 $M: String\\to Scope$，当容器需要解析 beanName $n$ 的实例时：
1) 若 $s$ 为空或为 `singleton`，按单例语义从单例缓存取/建；
2) 若 $s$ 为 `prototype`，每次创建新实例；
3) 否则取 $M[s]$，若不存在则失败；若存在则调用 `M[s].get(n, factory)` 获取或创建实例。

## 接口：数据 + 约束
- 数据：
  - BeanDefinition.scopeName（见 [../interface/BeanDefinition.md](../interface/BeanDefinition.md)）
  - BeanFactory.scopes（见 [../class/AbstractBeanFactory.md](../class/AbstractBeanFactory.md)）
- 约束：
  - `scopeName` 与注册表 key 必须一致；未注册的自定义 scope 在解析阶段失败。
  - scope 的上下文来源由 `Scope` 实现决定；Web scope 依赖请求上下文（见 [../../../web/mechanism/WebScopes.md](../../../web/mechanism/WebScopes.md)）。

## 常用构造/操作（仅列出接口与符号）
- 注册：`ConfigurableBeanFactory.registerScope(scopeName, scope)`（见 [../interface/ConfigurableBeanFactory.md](../interface/ConfigurableBeanFactory.md)）
- 解析：`Scope.get(beanName, objectFactory)`（见 [../interface/Scope.md](../interface/Scope.md)）

## 关系：上级/下级/等价/特例/推广
- 上级：两阶段初始化中的“运行态按上下文取实例”策略（与具体框架无关）。
- 特例：Web scopes（request/session）：见 [../../../web/mechanism/WebScopes.md](../../../web/mechanism/WebScopes.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → beans → mechanism → ScopeResolution → flows/Bean 注册与创建流程。

