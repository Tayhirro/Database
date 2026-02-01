---
type: index
tags:
  - springboot/web
  - moc
---

# web（模块总览）

> 本页用于汇总 `modules/web/` 下的机制与入口，并用关系边表达“启动/运行态”的对象连接方式。

导航：[springboot/README.md](../../README.md) | [springboot/索引.md](../../索引.md) | [springboot/概念图.md](../../概念图.md)

## 目录结构（模块内）
- `modules/web/class/`：类条目
- `modules/web/interface/`：接口条目
- `modules/web/mechanism/`：机制条目
- `modules/web/server/`：具体服务器实现（Tomcat/Netty 等）

## WebServer 抽象（接口层）
- `WebServer`：见 [interface/WebServer.md](interface/WebServer.md)
- `WebServerFactory`：见 [interface/WebServerFactory.md](interface/WebServerFactory.md)
- `ServletWebServerFactory`：见 [interface/ServletWebServerFactory.md](interface/ServletWebServerFactory.md)
- `ReactiveWebServerFactory`：见 [interface/ReactiveWebServerFactory.md](interface/ReactiveWebServerFactory.md)

## 启动链（Servlet / embedded Tomcat）

### Boot → WebServer（概念级）
- 嵌入式 WebServer 创建与绑定：见 [mechanism/EmbeddedWebServer.md](mechanism/EmbeddedWebServer.md)
- WebServer 生命周期与线程保活：见 [mechanism/WebServerLifecycleAndThreads.md](mechanism/WebServerLifecycleAndThreads.md)
- `ServletWebServerFactory` → `TomcatServletWebServerFactory`：见 [class/TomcatServletWebServerFactory.md](class/TomcatServletWebServerFactory.md)
- `TomcatServletWebServerFactory` → `TomcatWebServer`：见 [class/TomcatWebServer.md](class/TomcatWebServer.md)

### TomcatWebServer → ProtocolHandler → Endpoint（概念级）
- Tomcat start/stop 触发链：见 [server/tomcat/mechanism/TomcatWebServerStartStop.md](server/tomcat/mechanism/TomcatWebServerStartStop.md)
- Tomcat 组件模型：见 [server/tomcat/mechanism/TomcatComponentModel.md](server/tomcat/mechanism/TomcatComponentModel.md)
- Tomcat 组件委托链：`Connector`（端口入口 + 生命周期委派）→ `ProtocolHandler`（协议处理边界）→ `AbstractProtocol`（常见实现骨架）→ `AbstractEndpoint`（I/O 端点 + executor）
  - `Connector`：见 [server/tomcat/class/Connector.md](server/tomcat/class/Connector.md)
  - `ProtocolHandler`：见 [server/tomcat/interface/ProtocolHandler.md](server/tomcat/interface/ProtocolHandler.md)
  - `AbstractProtocol`：见 [server/tomcat/class/AbstractProtocol.md](server/tomcat/class/AbstractProtocol.md)
  - `AbstractEndpoint`：见 [server/tomcat/class/AbstractEndpoint.md](server/tomcat/class/AbstractEndpoint.md)

#### 组件用途与注意事项（语义级）

| 组件                 | 用途（做什么）                                                                                | 注意事项（不做什么 / 边界）                                                                                                                     |
| ------------------ | -------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| `Connector`        | Tomcat 的网络入口组件：绑定端口并把连接处理委派给其持有的 `ProtocolHandler`，作为 Catalina（容器）侧与 Coyote（协议栈）侧的连接点。 | 不定义 accept/poller/worker 的线程模型；线程与执行器的组织在 `ProtocolHandler/Endpoint` 侧。连接器级配置通常用于选择/构造具体协议实现与触发生命周期迁移。                              |
| `ProtocolHandler`  | 协议处理器接口：表达“某种协议栈如何 start/stop 并处理连接”的生命周期边界，作为 `Connector` 与具体协议实现之间的接口。               | 只表达生命周期与职责边界，不固定线程模型与连接管理细节；协议解析与 I/O 组织由具体实现决定。                                                                                    |
| `AbstractProtocol` | `ProtocolHandler` 的常见抽象基类：提供协议处理器的通用骨架，并在 `start()` 中调用 `endpoint.start()` 启动端点。       | 其职责偏“协议处理器层”的组织点；worker executor 与 accept/poll 线程角色通常由 endpoint 组织。除 endpoint 外，协议层可能还持有辅助调度器（例如 `utilityExecutor`），与请求处理线程池属于不同角色。 |
| `AbstractEndpoint` | 网络端点抽象：端口监听、连接管理、I/O 轮询/事件分发，并把请求处理任务投递到 `Executor`（内部创建或外部注入）。                        | 端点的具体实现随 I/O 模型变化（NIO/NIO2/APR 等）；线程命名、线程数量、以及 executor 的具体类型属于实现细节。                                                                |

#### 为什么分层（Connector vs ProtocolHandler vs Endpoint）
该链路把“端口入口与容器侧生命周期”（`Connector`）与“协议处理边界”（`ProtocolHandler`）分离，并把“底层 I/O 与任务投递”（`AbstractEndpoint`）独立成端点层，使协议实现与 I/O 模型可以在相同的委派框架下替换与组合。

## 运行态线程角色（Tomcat / NIO）
- Tomcat 线程与执行器模型：见 [server/tomcat/mechanism/TomcatThreadingAndExecutors.md](server/tomcat/mechanism/TomcatThreadingAndExecutors.md)
- 端点线程角色（Acceptor/Poller/Executor）：见 [server/tomcat/class/threading/README.md](server/tomcat/class/threading/README.md)

## Web 作用域（WebScopes）
- Web 作用域（request/session）：见 [mechanism/WebScopes.md](mechanism/WebScopes.md)

## 前端控制器（可选）
- `DispatcherServlet`（Spring MVC）：见 [class/DispatcherServlet.md](class/DispatcherServlet.md)
- `DispatcherHandler`（Spring WebFlux）：见 [class/DispatcherHandler.md](class/DispatcherHandler.md)
