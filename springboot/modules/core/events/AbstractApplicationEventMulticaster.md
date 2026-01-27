# AbstractApplicationEventMulticaster（多播器抽象基类）

> **类型**：类（Class）

## 一句话
`AbstractApplicationEventMulticaster` 是 `ApplicationEventMulticaster` 的抽象基类：提供监听器注册、事件类型匹配与缓存的通用实现，将具体的分发执行策略留给子类完成。

## 严格定义
在 Spring Framework 中，`org.springframework.context.event.AbstractApplicationEventMulticaster` 是事件多播器的抽象实现类，实现了 `ApplicationEventMulticaster` 的监听器注册/移除相关方法，并维护监听器检索器与缓存；它通过 `BeanFactoryAware` 接入 `ConfigurableBeanFactory`，以支持按 beanName 注册的监听器在分发时被解析与匹配。

## 接口：数据 + 约束
- 数据（类内字段，语义级别）：
  - `defaultRetriever`：监听器注册表（对象监听器 + listener beanName）
  - `retrieverCache`：按事件类型/源类型缓存“匹配后的监听器集合”
  - `beanClassLoader`：用于监听器类型解析
  - `beanFactory`：用于解析 listener beanName 与判断事件支持性
- 输入：
  - 监听器注册：`ApplicationListener<?>` / `String beanName`
  - 事件检索：`ApplicationEvent event` + 可选 `ResolvableType eventType`
- 输出：
  - 注册表变更（副作用）
  - 匹配监听器集合：`getApplicationListeners(event, eventType)`（供子类分发循环使用）
- 约束：
  - 事件匹配依赖 `ResolvableType` 与监听器类型/泛型信息；当监听器以 beanName 形式注册时，匹配过程可依赖 `beanFactory` 的类型信息与实例解析。
  - 缓存命中以“事件类型 + 源类型”等维度为 key；缓存策略属于实现细节，影响重复事件发布的检索成本。

## 常用构造/操作（仅列出接口与符号）
- 注册：`addApplicationListener(listener)` / `addApplicationListenerBean(beanName)`
- 取消：`removeApplicationListener(listener)` / `removeApplicationListenerBean(beanName)` / `removeAllListeners()`
- 检索：`getApplicationListeners(event, eventType)`

## 关系：上级/下级/等价/特例/推广
- 实现：`ApplicationEventMulticaster`（见 [ApplicationEventMulticaster.md](ApplicationEventMulticaster.md)）。
- 子类：`SimpleApplicationEventMulticaster`（见 [SimpleApplicationEventMulticaster.md](SimpleApplicationEventMulticaster.md)）。
- 运行时接入：`ApplicationContext` 通过名为 `applicationEventMulticaster` 的组件持有并使用多播器（见 [../context/ApplicationContext.md](../context/ApplicationContext.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → events → AbstractApplicationEventMulticaster →（Listener Registry / Matching / Cache）。

