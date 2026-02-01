---
type: class
tags:
  - springboot/web
  - tomcat
---

# Host（Tomcat Catalina Host 组件）

> **类型**：类（Class）

## 一句话
`Host` 表示一个虚拟主机：按主机名组织一组 `Context`（Web 应用），并作为容器链路中的一级分发边界。

## 严格定义
在 Tomcat 中，`org.apache.catalina.Host` 是 `Engine` 的子容器：它以主机名为键组织其子级 `Context`（Web 应用），并在请求分发时把请求推进到匹配的 `Context` 继续处理；常见实现为 `org.apache.catalina.core.StandardHost`。

## 接口：数据 + 约束
- 数据（语义级别）：
  - `contexts: Context[]`：子容器集合
  - `name: String`：虚拟主机名
  - `appBase: String`：应用基目录（实现相关，部署模式相关）
- 字段与状态（常见实现；字段名可能随 Tomcat 版本变化）：
  - `name: String`：虚拟主机名
  - `appBase: String`：应用基目录（部署相关）
  - `children: Map<String, Context>`：子容器集合（按 context path/name 索引，实现相关）
  - `aliases: String[]`：别名主机名集合（可选，实现相关）
  - `pipeline`：容器调用链（Valve 链，Tomcat Pipeline 体系）
  - `state`：生命周期状态（Tomcat `Lifecycle` 体系）
- 输入：
  - 子容器管理：`addChild(context)` / `findChild(pathOrName)`
  - 配置：`setName(name)` / `setAppBase(path)`
- 输出：
  - 将请求分派到某个 `Context` 的处理链路（运行态行为）
- 约束：
  - `Host` 与 `Context` 的组织方式同时适用于“外置部署”与“嵌入式部署”；嵌入式模式下 `appBase` 等部署配置可能不参与目录扫描式部署（实现相关）。

## 常用构造/操作（仅列出接口与符号）
- `addChild(Context)` / `findChild(String)`
- `setName(String)` / `setAppBase(String)`

## 关系：上级/下级/等价/特例/推广
- 上级：
  - `Engine`：见 [Engine.md](Engine.md)
- 下级：
  - `Host` → `Context`：见 [Context.md](Context.md)

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → web → server → tomcat → class → Host。
