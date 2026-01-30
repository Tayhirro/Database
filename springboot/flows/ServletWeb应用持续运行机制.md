---
type: flow
tags:
  - springboot/flow
  - web
---

# ServletWeb应用持续运行机制（AnnotationConfigServletWebServerApplicationContext）

## 一句话
在 Servlet Web 应用形态下，`SpringApplication.run(...)` 本身不会阻塞；应用在 `context.refresh()` 过程中启动 `WebServer`，由 WebServer 启动的非守护线程（non-daemon threads）保持 JVM 进程持续运行，直到 `ApplicationContext.close()` 停止 WebServer 并释放线程。

## 严格定义
给定 `WebApplicationType = SERVLET` 的一次启动调用，`SpringApplication.createApplicationContext()` 会创建一个 `ConfigurableApplicationContext` 的 Servlet Web 实现（常见为 `AnnotationConfigServletWebServerApplicationContext`）。在 `SpringApplication.refreshContext(context)` 调用 `context.refresh()` 时，该上下文在 refresh 的 hook 阶段创建并启动 `WebServer`；`WebServer.start()` 将启动端口监听与请求处理相关线程，且这些线程通常为非守护线程，从而使 JVM 在 `main` 线程结束后仍保持存活。关闭时，由 `context.close()`（或 shutdown hook）触发 WebServer 停止，线程退出后 JVM 才会自然结束。

## 接口：数据 + 约束
- 输入：
  - `SpringApplication.run(primarySources, args)`
  - `WebApplicationType` 的判定结果（servlet）
  - `ApplicationContext` 内部可用的 `ServletWebServerFactory` Bean（Tomcat/Jetty/Undertow 等）
- 输出：
  - `ConfigurableApplicationContext` 返回给调用方（run 返回）
  - `WebServer` 已启动并绑定端口（对外提供 HTTP 服务）
- 约束：
  - `run()` 的返回不等同于“进程退出/继续运行”的控制；进程存活依赖是否存在非守护线程。
  - 若 WebServer 未能创建或未启动（例如缺少 `ServletWebServerFactory`），则不会形成保活线程，进程可能在 `main` 线程结束后退出（除非存在其他非守护线程）。

## 时间线（从 run 到 JVM 保活）

### Phase 1：启动入口与上下文选择
- `main(String[] args)` 调用 `SpringApplication.run(...)`
- `createApplicationContext()` 通过 `ApplicationContextFactory` 按 `WebApplicationType` 选择并创建上下文实现：
  - `ApplicationContextFactory`：见 [../modules/core/bootstrap/interface/ApplicationContextFactory.md](../modules/core/bootstrap/interface/ApplicationContextFactory.md)
  - `ConfigurableApplicationContext`：见 [../modules/core/context/interface/ConfigurableApplicationContext.md](../modules/core/context/interface/ConfigurableApplicationContext.md)

### Phase 2：refresh 触发 WebServer 启动
- `refreshContext(context)` → `context.refresh()`
- 在 `ContextRefresh` 的 hook 阶段，Servlet Web 上下文覆写 `onRefresh()` 并触发 WebServer 创建与启动：
  - refresh 模板流程：见 [../modules/core/context/mechanism/ContextRefresh.md](../modules/core/context/mechanism/ContextRefresh.md)
- WebServer 机制总览：见 [../modules/web/mechanism/EmbeddedWebServer.md](../modules/web/mechanism/EmbeddedWebServer.md)
- WebServer 生命周期与线程保活：见 [../modules/web/mechanism/WebServerLifecycleAndThreads.md](../modules/web/mechanism/WebServerLifecycleAndThreads.md)

概念级调用链（Servlet Web）：
1) `SpringApplication.refreshContext(context)`  
2) `AnnotationConfigServletWebServerApplicationContext.refresh()`（在该实例上调用，方法实现来自 Framework 的 `AbstractApplicationContext.refresh()`）  
3) `AbstractApplicationContext.refresh()`（模板方法）  
4) `ServletWebServerApplicationContext.onRefresh()`（Boot 的 Servlet Web 上下文覆写 hook）  
5) `ServletWebServerApplicationContext.createWebServer()`（创建并写入 `webServer` 字段）  
6) `ServletWebServerFactory.getWebServer(...)` → `WebServer.start()`  
7) 容器实现启动端口监听与请求处理线程（non-daemon）

### Phase 3：run 返回但进程仍存活
- `SpringApplication.run(...)` 返回 `ApplicationContext`
- `main` 线程可以结束
- JVM 继续运行的条件是：仍存在非守护线程（典型来自 WebServer 线程池与监听线程）

### Phase 4：退出路径（关闭与线程释放）
- 触发来源：
  - 显式：`context.close()`
  - 隐式：JVM shutdown hook（若注册）
- 典型效果：
  - `ApplicationContext` 关闭阶段停止 `WebServer`，释放端口与线程资源
  - 非守护线程退出后 JVM 结束

## 关系：上级/下级/等价/特例/推广
- 上级：启动流程（Boot）：见 [启动流程.md](启动流程.md)
- 相关：
  - `DispatcherServlet`（请求进入点）：见 [../modules/web/class/DispatcherServlet.md](../modules/web/class/DispatcherServlet.md)
- 特例：Reactive Web 应用以 `ReactiveWebServerApplicationContext` 形成类似“server threads 保活”的语义，但底层容器与线程模型不同。

## 把新概念挂回框架（多级索引轨迹）
springboot → flows → ServletWeb应用持续运行机制 →（SpringApplication.run / ContextRefresh.onRefresh / EmbeddedWebServer / WebServer.start）。
