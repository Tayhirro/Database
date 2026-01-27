# ApplicationContext（应用上下文）

> **类型**：接口（Interface）

## 一句话
`ApplicationContext` 是 Spring 应用的运行时容器：提供 Bean 装配、资源访问、事件发布、环境（`Environment`）与生命周期管理等能力的统一入口。

## 严格定义
在 Spring Framework 中，`ApplicationContext` 是一组容器接口的组合体；在 Spring Boot 启动过程中，`SpringApplication.run(...)` 的目标是构建一个 `ConfigurableApplicationContext` 并完成 `refresh()`，使其进入可用状态。

## 数据（语义级别；与启动主线相关）
`ApplicationContext` 是接口，具体“数据承载”取决于实现；在典型的 `ConfigurableApplicationContext`（如 `AbstractApplicationContext` 一系）中，可抽象为一组被持有/可访问的组件：

- `BeanFactory`（通常为 `ConfigurableListableBeanFactory`）：BeanDefinition 注册、Bean 实例化与依赖注入的底层容器。
- `Environment`（通常为 `ConfigurableEnvironment`）：profiles 与属性源的聚合视图，供条件判断与属性绑定查询。
- `ApplicationEventMulticaster`（默认 bean 名为 `applicationEventMulticaster`）：事件发布到监听器的分发组件。
- 监听器注册表：`ApplicationListener` 对象集合与 listener beanName 集合（在 refresh 的注册步骤中装配到多播器）。
- `ResourceLoader`：`classpath:` 等资源路径解析与资源访问。
- `MessageSource`：消息解析与国际化资源访问（常用于 `getMessage(...)`）。
- 生命周期处理器（常见为 `LifecycleProcessor`）：对实现了 `Lifecycle/SmartLifecycle` 的 Bean 执行 start/stop 协调。
- 上下文标识与层级信息：`id` / `displayName` / `parent`（若存在父子上下文）。



## 接口：数据 + 约束
- 输入（容器构建的常见输入形态）：
  - 配置类/组件扫描结果（作为 BeanDefinition 来源）
  - `Environment`（属性与 profiles）
  - 各类后处理器与监听器（post-processors / listeners）
- 输出：
  - Bean 获取与注入（DI）
  - 事件发布/监听
  - 资源加载（resource loading）
  - 生命周期：`refresh()` / `close()`
- 约束：
  - `ApplicationContext` 是接口；具体行为取决于实现类与其组合的底层容器（例如 `BeanFactory`、事件多播器等）。

## 常用构造/操作（仅列出接口与符号）
- `refresh()`：将上下文从“已配置”推进到“可用”状态（refresh 流程见 [springboot/modules/core/context/mechanism/ContextRefresh.md](../mechanism/ContextRefresh.md)，启动阶段位置见 [springboot/flows/启动流程.md](../../../../flows/启动流程.md)）。
- `close()`：触发容器关闭与资源回收（关闭段见 [springboot/flows/运行全链路.md](../../../../flows/运行全链路.md)）。
- `getBean(...)`：按类型/名称获取 Bean（抽象接口）。

## 关系：上级/下级/等价/特例/推广
- 上级：依赖注入容器（DI container）。
- 相关：
  - `SpringApplication`（创建并驱动 refresh）：见 [springboot/modules/core/bootstrap/class/SpringApplication.md](../../bootstrap/class/SpringApplication.md)
  - 外部化配置（`Environment`）：见 [springboot/modules/config/mechanism/ExternalizedConfiguration.md](../../../config/mechanism/ExternalizedConfiguration.md)
  - 自动配置：见 [springboot/modules/config/mechanism/AutoConfiguration.md](../../../config/mechanism/AutoConfiguration.md)
- 特例：Web 应用会使用 Web 相关的 `ApplicationContext` 实现并绑定 WebServer（见 [springboot/modules/web/mechanism/EmbeddedWebServer.md](../../../web/mechanism/EmbeddedWebServer.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → ApplicationContext → flows/启动流程。
