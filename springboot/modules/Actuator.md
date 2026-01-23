# Actuator（运行态端点与可观测性入口）

> **类型**：模块（Module）

## 一句话
Actuator 提供运行态端点（endpoints）以暴露应用的健康、信息与管理操作，并作为与度量/追踪等可观测性系统集成的入口之一。

## 严格定义
在 Spring Boot 中，Actuator 是一组自动配置与端点实现的组合：端点以统一的标识符与访问协议暴露，端点的启用、暴露范围与访问控制由外部化配置与安全配置共同决定。

## 接口：数据 + 约束
- 输入：
  - `ApplicationContext`（端点 Bean 与依赖）
  - `Environment`（端点启用/暴露/路径等配置）
- 输出：
  - endpoints（HTTP/JMX 等形式，取决于配置）
- 约束：
  - 暴露方式与端点集合依赖所启用的模块与依赖（Web、JMX、安全等）。

## 常用构造/操作（仅列出接口与符号）
- endpoints：`health` / `info` / `metrics` 等（具体集合以版本与依赖为准）
- 配置入口：`management.*` 命名空间（示例）

## 关系：上级/下级/等价/特例/推广
- 上级：运行态管理（runtime management）。
- 相关：外部化配置（端点开关/暴露）、自动配置（端点 Bean 的导入）、WebServer（HTTP 暴露时）。

## 把新概念挂回框架（多级索引轨迹）
springboot → modules → Actuator →（运行态端点/可观测性）。

