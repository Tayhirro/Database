# SpringApplication（Spring Boot 启动编排入口）

## 一句话
`SpringApplication` 是 Spring Boot 的启动编排器：从 `primarySources` 与启动参数出发，构造 `Environment`、创建 `ApplicationContext`、触发 `refresh()` 并发布启动生命周期事件。

## 严格定义
在 Spring Boot 中，`org.springframework.boot.SpringApplication` 是应用启动入口 `SpringApplication.run(...)` 的承载对象；其目标是产生一个可用的 `ConfigurableApplicationContext`（成功时）或抛出启动失败异常（失败时）。

## 接口：数据 + 约束
- 输入：
  - `primarySources`：启动源（通常包含 `@SpringBootApplication` 标注的配置类）
  - `args`：命令行参数
- 输出：
  - `ConfigurableApplicationContext`
- 可配置项（示例，非穷举）：
  - `WebApplicationType`（servlet/reactive/none）
  - listeners/initializers 集合
  - banner、启动日志等
- 约束：
  - 启动阶段事件与扩展点加载列表在不同 Boot 版本间存在差异。

## 常用构造/操作（仅列出接口与符号）
- 启动入口：`SpringApplication.run(...)`
- 启动阶段化过程（概览）：见 [springboot/flows/启动流程.md](../flows/启动流程.md)
- 扩展点发现：见 [springboot/modules/SpringFactories.md](SpringFactories.md)
- 生命周期事件：见 [springboot/modules/ApplicationLifecycleEvents.md](ApplicationLifecycleEvents.md)

## 关系：上级/下级/等价/特例/推广
- 上级：应用生命周期（启动/运行/停止）。
- 下级：外部化配置（`Environment`）、`ApplicationContext`、自动配置、生命周期事件。
- 特例：Web 应用会创建并绑定嵌入式 WebServer（见 [springboot/modules/EmbeddedWebServer.md](EmbeddedWebServer.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → SpringApplication → flows/启动流程。

