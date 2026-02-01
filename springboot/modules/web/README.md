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
- Tomcat 组件委托链：`Connector` → `ProtocolHandler` → `AbstractProtocol` → `AbstractEndpoint`
  - `Connector`：见 [server/tomcat/class/Connector.md](server/tomcat/class/Connector.md)
  - `ProtocolHandler`：见 [server/tomcat/interface/ProtocolHandler.md](server/tomcat/interface/ProtocolHandler.md)
  - `AbstractProtocol`：见 [server/tomcat/class/AbstractProtocol.md](server/tomcat/class/AbstractProtocol.md)
  - `AbstractEndpoint`：见 [server/tomcat/class/AbstractEndpoint.md](server/tomcat/class/AbstractEndpoint.md)

## 运行态线程角色（Tomcat / NIO）
- Tomcat 线程与执行器模型：见 [server/tomcat/mechanism/TomcatThreadingAndExecutors.md](server/tomcat/mechanism/TomcatThreadingAndExecutors.md)
- 端点线程角色（Acceptor/Poller/Executor）：见 [server/tomcat/class/threading/README.md](server/tomcat/class/threading/README.md)

## Web 作用域（WebScopes）
- Web 作用域（request/session）：见 [mechanism/WebScopes.md](mechanism/WebScopes.md)

## 前端控制器（可选）
- `DispatcherServlet`（Spring MVC）：见 [class/DispatcherServlet.md](class/DispatcherServlet.md)
- `DispatcherHandler`（Spring WebFlux）：见 [class/DispatcherHandler.md](class/DispatcherHandler.md)

