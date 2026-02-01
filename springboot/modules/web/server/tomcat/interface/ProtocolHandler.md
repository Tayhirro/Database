---
type: interface
tags:
  - springboot/web
  - tomcat
---

# ProtocolHandler（Tomcat 协议处理器接口）

> **类型**：接口（Interface）

## 一句话
`ProtocolHandler` 是 Tomcat 用于抽象“某种协议栈（HTTP/1.1、AJP 等）如何启动/停止并处理连接”的接口，作为 `Connector` 与具体协议实现之间的边界。

## 严格定义
在 Tomcat 中，`org.apache.coyote.ProtocolHandler` 是由 `Connector` 持有并在 `Connector.startInternal()` 中触发其 `start()` 的协议处理器接口；其典型实现会在 `start()` 中启动对应的 `Endpoint`（网络端点）来完成端口监听与 I/O 管理，并将收到的请求推进到后续处理链路。

## 继承链（接口链 / 实现链）
- 接口链：`ProtocolHandler`（无上级接口）。
- 常见实现基类：`AbstractProtocol`（见 [../class/AbstractProtocol.md](../class/AbstractProtocol.md)）。

## 接口：数据 + 约束
- 输入：
  - `start()`：启动协议处理链路（副作用）
  - `stop()`：停止协议处理链路（副作用）
- 输出：
  - 无（以副作用表达生命周期迁移）
- 约束：
  - 协议解析、线程模型与连接管理属于实现细节；接口只表达生命周期边界与协议处理职责的存在性。

## 典型实现的组成（语义级）
在 HTTP/1.1（NIO）等常见实现中，`ProtocolHandler` 往往由以下组件组合表达其内部职责分层（组件名与字段名随版本变化）：

- `Endpoint`：端口监听、连接接入、I/O 就绪轮询与任务投递（见 [../class/AbstractEndpoint.md](../class/AbstractEndpoint.md)）。
- `Processor`：协议解析与连接级状态机（HTTP/1.1 等），将字节流解析并写入 Coyote 请求对象（见 [Processor.md](Processor.md)、[../class/Http11Processor.md](../class/Http11Processor.md)）。
- `CoyoteAdapter`：将 Coyote 请求/响应适配为 Servlet 容器链路输入输出（见 [../class/CoyoteAdapter.md](../class/CoyoteAdapter.md)）。

在这一组合中，`Connector` 负责端口入口与生命周期委派；`ProtocolHandler` 承载“协议栈本体”的组织与运行态推进（见 [../mechanism/TomcatComponentModel.md](../mechanism/TomcatComponentModel.md)）。

## 常用构造/操作（仅列出接口与符号）
- 生命周期：`start()` / `stop()` / `destroy()`

## 代码示例
### 通过 Connector 获取 ProtocolHandler 并按实现分支处理
前提：可获得 Tomcat 的 `Connector` 实例。

```java
import org.apache.catalina.connector.Connector;
import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;

void inspectProtocol(Connector connector) {
  ProtocolHandler handler = connector.getProtocolHandler();
  if (handler instanceof AbstractProtocol<?> protocol) {
    int maxThreads = protocol.getMaxThreads();
    long connectionCount = protocol.getConnectionCount();
    String id = protocol.getId();
  }
}
```

## 关系：上级/下级/等价/特例/推广
- 上级：Tomcat 组件模型（见 [../mechanism/TomcatComponentModel.md](../mechanism/TomcatComponentModel.md)）。
- 被持有者：`Connector`（见 [../class/Connector.md](../class/Connector.md)）。
- 相关：`AbstractEndpoint`（网络端点，见 [../class/AbstractEndpoint.md](../class/AbstractEndpoint.md)）。
- 下级（实现级组成）：`Processor`（见 [Processor.md](Processor.md)）、`CoyoteAdapter`（见 [../class/CoyoteAdapter.md](../class/CoyoteAdapter.md)）。
- 特例：HTTP/1.1 NIO 协议处理器 `Http11NioProtocol`（见 [../class/Http11NioProtocol.md](../class/Http11NioProtocol.md)）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → interface → ProtocolHandler。
