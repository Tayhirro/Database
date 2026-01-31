---
type: class
tags:
  - springboot/web
  - tomcat
---

# Connector（Tomcat 连接器）

> **类型**：类（Class）

## 一句话
`Connector` 是 Tomcat 的网络入口组件：绑定端口并将连接处理委托给 `ProtocolHandler`，从而把底层 I/O 与上层 Servlet 容器处理链路连接起来。

## 严格定义
在 Tomcat 中，`org.apache.catalina.connector.Connector` 是 `Service` 持有的连接器实现；其生命周期启动时会调用其内部 `ProtocolHandler.start()`，从而启动端口监听与协议处理链路。停止时会调用 `ProtocolHandler.stop()` 以释放相关资源。

## 继承链（接口链 / 实现链）
- 继承链：`java.lang.Object` → `Connector`。
- 关联接口（语义层）：组件生命周期（Tomcat 的 `Lifecycle` 体系）与 `ProtocolHandler` 委托点。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `protocolHandler: ProtocolHandler`（协议处理器委托点）
  - `port: int`（配置端口；实际绑定端口可在运行态确定）
- 字段与状态（面向“线程/执行器”理解；字段名可能随 Tomcat 版本变化）：
  - `protocolHandler`：线程模型与 executor 组织的入口委托点；`Connector` 本身不定义 accept/worker 的线程模型（见 [../interface/ProtocolHandler.md](../interface/ProtocolHandler.md)）
  - 连接器级配置项（例如端口、协议名称等）通常用于构造/选择具体 `ProtocolHandler`，并在启动时触发生命周期迁移
- 输入：
  - `startInternal()`：触发 `protocolHandler.start()`
  - `stopInternal()`：触发 `protocolHandler.stop()`
- 输出：
  - 端口监听与协议处理链路的启动/停止（副作用）
- 约束：
  - `Connector` 只表达“端口入口 + 委托协议处理器”的边界；连接处理线程模型由 `ProtocolHandler/Endpoint` 决定。

## 常用构造/操作（仅列出接口与符号）
- 生命周期：`startInternal()` / `stopInternal()`
- 观测：`getPort()` / `getLocalPort()` / `getScheme()`

## 代码示例
### 在运行态读取 Connector → ProtocolHandler（Servlet/Tomcat 场景）
前提：应用为 Servlet Web 形态，且使用 embedded Tomcat；运行时可取得 `TomcatWebServer`。

```java
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.ApplicationRunner;

@Bean
ApplicationRunner dumpConnectors(ApplicationContext context) {
  return args -> {
    if (context instanceof ServletWebServerApplicationContext webContext) {
      WebServer webServer = webContext.getWebServer();
      if (webServer instanceof TomcatWebServer tomcatWebServer) {
        Service service = tomcatWebServer.getTomcat().getService();
        for (Connector connector : service.findConnectors()) {
          ProtocolHandler protocolHandler = connector.getProtocolHandler();
          String protocolHandlerClassName = connector.getProtocolHandlerClassName();
          int configuredPort = connector.getPort();
          int localPort = connector.getLocalPort();
        }
      }
    }
  };
}
```

## 关系：上级/下级/等价/特例/推广
- 上级：Tomcat 组件模型（见 [../mechanism/TomcatComponentModel.md](../mechanism/TomcatComponentModel.md)）。
- 下级：`ProtocolHandler`（见 [../interface/ProtocolHandler.md](../interface/ProtocolHandler.md)）。
- 相关：Boot 的 `TomcatWebServer` 在启动时会校验 `Connector` 的生命周期状态（见 [../../../class/TomcatWebServer.md](../../../class/TomcatWebServer.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → Connector。
