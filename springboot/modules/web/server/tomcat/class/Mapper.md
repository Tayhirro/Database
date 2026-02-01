---
type: class
tags:
  - springboot/web
  - tomcat
---

# Mapper（Tomcat 请求映射器）

> **类型**：类（Class）

## 一句话
`Mapper` 是 Tomcat 在运行态将“目标主机名 + URI 路径”映射到容器对象（`Host`/`Context`/`Wrapper`）的一层索引与查询组件。

## 严格定义
在 Tomcat 中，`org.apache.catalina.mapper.Mapper` 维护与容器树一致的映射表（例如虚拟主机、上下文路径与 servlet 映射），并在请求处理过程中将请求的 serverName、URI 等输入映射为目标 `Host`、`Context` 与 `Wrapper`（以及可选的派生路径匹配信息）；映射表通常由容器事件驱动更新（实现相关）。

## 接口：数据 + 约束
- 数据（语义级别）：
  - Host 映射：`hostName -> Host`
  - Context 映射：`(host, contextPath) -> Context`
  - Wrapper 映射：`(context, urlPattern) -> Wrapper`
- 字段与状态（常见实现；字段名可能随 Tomcat 版本变化）：
  - `hosts`：host 映射表（实现相关）
  - `defaultHostName: String`：默认 host 名称（实现相关）
  - `state`：可见性/并发控制与缓存（实现相关）
- 输入：
  - `map(...)`：以主机名、URI、协议等为输入进行映射（方法签名实现相关）
- 输出：
  - 目标容器对象：`Host` / `Context` / `Wrapper`（以及派生的路径匹配信息，实现相关）
- 约束：
  - `Mapper` 仅描述“查询与映射”边界；映射表的构建与增量更新由容器注册/事件机制提供（例如通过监听 `Host/Context/Wrapper` 的增删与映射变更，实现相关）。

## 常用构造/操作（仅列出接口与符号）
- `addHost(...)` / `removeHost(...)`（实现相关）
- `addContext(...)` / `removeContext(...)`（实现相关）
- `addWrapper(...)` / `removeWrapper(...)`（实现相关）
- `map(...)`（将请求输入映射为目标容器对象）

## 关系：上级/下级/等价/特例/推广
- 上级：
  - Tomcat 组件模型：见 [../mechanism/TomcatComponentModel.md](../mechanism/TomcatComponentModel.md)
- 下级：
  - `Mapper` → `Host`：见 [Host.md](Host.md)
  - `Mapper` → `Context`：见 [Context.md](Context.md)
  - `Mapper` → `Wrapper`：见 [Wrapper.md](Wrapper.md)
- 相关：
  - `CoyoteAdapter`：见 [CoyoteAdapter.md](CoyoteAdapter.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → Mapper。

