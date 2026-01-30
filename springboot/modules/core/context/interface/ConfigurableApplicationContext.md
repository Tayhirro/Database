# ConfigurableApplicationContext（可配置应用上下文）

> **类型**：接口（Interface）

## 一句话
`ConfigurableApplicationContext` 是 `ApplicationContext` 的可配置运行时视图：在容器能力之上补充 `refresh()`、监听器/后处理器注册、关闭与 active 状态查询等启动驱动能力。

## 严格定义
在 Spring Framework 中，`org.springframework.context.ConfigurableApplicationContext` 继承 `ApplicationContext` 并扩展生命周期与关闭语义（`Lifecycle`/`Closeable`），其 `refresh()` 定义了将上下文从“可配置态”推进到“可用态”的刷新入口；Spring Boot 的 `SpringApplication.run(...)` 的返回类型即为 `ConfigurableApplicationContext`。

## 继承链（接口链 / 实现链）
- 接口链：
  - `ApplicationContext`（容器能力组合体）：见 [ApplicationContext.md](ApplicationContext.md)
  - `Lifecycle`（启动/停止运行态语义）
  - `Closeable`（关闭语义）
  - 以上组合为 `ConfigurableApplicationContext`（补充 refresh/注册/关闭等驱动入口）
- 常见实现链：`AbstractApplicationContext` →（非 Web）`AnnotationConfigApplicationContext`；（Servlet Web）`ServletWebServerApplicationContext`；（Reactive Web）`ReactiveWebServerApplicationContext`（实现类名随版本/栈而异）。

## 接口：数据 + 约束
- 输入（可配置入口）：
  - `setId(...)` / `setParent(...)` / `setEnvironment(...)`
  - `addBeanFactoryPostProcessor(...)`
  - `addApplicationListener(...)`
  - `setClassLoader(...)`
- 输出（生命周期驱动）：
  - `refresh()`：推进 refresh 模板流程（见 [../mechanism/ContextRefresh.md](../mechanism/ContextRefresh.md)）
  - `close()` / `registerShutdownHook()`
  - `isActive()`
- 约束：
  - `refresh()` 的阶段化顺序由 Framework 定义；实现类仅能在 hook 点插入行为（见 [../mechanism/ContextRefresh.md](../mechanism/ContextRefresh.md)）。
  - Web 场景下的“对外服务就绪”通常依赖 `refresh()` 内部启动 WebServer（具体位置见 Web context 的 `onRefresh()` 覆写）。

## 常用构造/操作（仅列出接口与符号）
- 驱动：`refresh()` / `close()` / `isActive()`
- 早期定制：`addBeanFactoryPostProcessor(...)` / `addApplicationListener(...)`
- 环境：`getEnvironment()` / `setEnvironment(...)`

## 关系：上级/下级/等价/特例/推广
- 上级：`ApplicationContext`。
- 相关：
  - Boot 启动入口：`SpringApplication.run(...)`（返回该接口）：见 [../../bootstrap/class/SpringApplication.md](../../bootstrap/class/SpringApplication.md)
  - Context 选择工厂：`ApplicationContextFactory`（创建该接口的实现）：见 [../../bootstrap/interface/ApplicationContextFactory.md](../../bootstrap/interface/ApplicationContextFactory.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → core → context → interface → ConfigurableApplicationContext → flows/启动流程。

