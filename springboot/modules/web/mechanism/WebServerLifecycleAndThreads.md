# WebServerLifecycleAndThreads（WebServer 生命周期与线程保活）

> **类型**：机制（Mechanism）

## 一句话
WebServer 生命周期与线程保活机制描述了 `WebServer.start()` 启动底层容器后如何通过非守护线程维持进程存活，以及 `stop()/close()` 如何触发资源释放与线程退出。

## 严格定义
设 `WebServer` 实例为 $W$，应用线程集合为 $T$。当执行 $W.start()$ 后，底层容器会创建线程子集 $T_w\subseteq T$ 来完成端口监听与请求处理；若 $T_w$ 中存在非守护线程，则在 `main` 线程结束后 JVM 仍保持存活。执行 $W.stop()$ 或应用关闭触发关闭阶段后，容器停止接受新连接并释放资源，使 $T_w$ 退出；当 JVM 中不再存在非守护线程时进程自然终止。

## 接口：数据 + 约束
- 数据：
  - `WebServer`（生命周期入口）：见 [../interface/WebServer.md](../interface/WebServer.md)
  - `ServletWebServerFactory` / `ReactiveWebServerFactory`（创建入口）：见 [../interface/ServletWebServerFactory.md](../interface/ServletWebServerFactory.md)、[../interface/ReactiveWebServerFactory.md](../interface/ReactiveWebServerFactory.md)
- 约束：
  - 线程创建、命名、数量与调度策略属于底层容器实现细节（Tomcat/Jetty/Undertow/Netty 等）。
  - 是否为守护线程（daemon/non-daemon）影响 JVM 的存活语义。

## 常用构造/操作（仅列出接口与符号）
- 启动：`WebServer.start()`
- 停止：`WebServer.stop()`
- 端口：`WebServer.getPort()`
- 关闭触发：`ApplicationContext.close()` / shutdown hook

## 具体形态（实现级别示例）
### Tomcat（Servlet）
- `TomcatServletWebServerFactory` 创建 `TomcatWebServer`（见 [../class/TomcatServletWebServerFactory.md](../class/TomcatServletWebServerFactory.md)、[../class/TomcatWebServer.md](../class/TomcatWebServer.md)）。
- `TomcatWebServer.start()` 触发 Tomcat 启动并绑定端口；端口监听与请求处理由 Tomcat 的连接器/线程池承担。
- 线程命名与分类属于实现细节；在常见配置下可观察到 acceptor/poller/worker 等类别（名称形式与数量与协议实现有关）。
- 机制细化：
  - `start/stop` 触发链：见 [../server/tomcat/mechanism/TomcatWebServerStartStop.md](../server/tomcat/mechanism/TomcatWebServerStartStop.md)
  - 线程与执行器角色：见 [../server/tomcat/mechanism/TomcatThreadingAndExecutors.md](../server/tomcat/mechanism/TomcatThreadingAndExecutors.md)

### Netty（Reactive）
- `NettyReactiveWebServerFactory` 创建 `NettyWebServer`（见 [../class/NettyReactiveWebServerFactory.md](../class/NettyReactiveWebServerFactory.md)、[../class/NettyWebServer.md](../class/NettyWebServer.md)）。
- `NettyWebServer.start()` 启动 event loop 以处理连接与 I/O；event loop 的线程模型由 Reactor Netty 决定。
- 机制细化：
  - `start/stop` 触发链：见 [../server/netty/mechanism/NettyWebServerStartStop.md](../server/netty/mechanism/NettyWebServerStartStop.md)
  - 线程与 event loop 角色：见 [../server/netty/mechanism/NettyThreadingAndExecutors.md](../server/netty/mechanism/NettyThreadingAndExecutors.md)

## 关系：上级/下级/等价/特例/推广
- 上级：嵌入式 WebServer 机制（见 [EmbeddedWebServer.md](EmbeddedWebServer.md)）。
- 相关：Servlet Web 应用持续运行机制（见 [springboot/flows/ServletWeb应用持续运行机制.md](../../../flows/ServletWeb应用持续运行机制.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → mechanism → WebServerLifecycleAndThreads → flows/ServletWeb应用持续运行机制。
