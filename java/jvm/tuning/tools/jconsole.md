---
type: tool
tags:
  - java/jvm
  - jvm
  - tuning
  - tools
  - jmx
---

# jconsole（JMX 监控工具）

## 一句话
`jconsole` 是 JDK 提供的可视化监控工具，通过 JMX 连接目标 JVM 进程以查看运行态指标与管理信息（可见项依 MBean 与权限而定）。

## 严格定义
对一个 JVM 进程，在其暴露 JMX 管理接口的前提下，`jconsole` 作为 JMX 客户端连接到目标并读取 MBean 树上的属性与操作接口，从而获得运行态信息与可执行的管理操作入口。MBean 集合与可见性依应用与平台配置而定。

## 接口：数据 + 约束
- 输入：
  - JMX 连接信息（本地或远程）
- 输出：
  - 可视化界面中的指标与管理视图
- 约束：
  - 需要 JMX 可用且权限允许；远程连接涉及额外安全配置。

## 常用构造/操作（仅列出接口与符号）
- 观测 GC/内存：与 GC 日志互补（见 [../analysis/GCLog.md](../analysis/GCLog.md)）

## 关系：上级/下级/等价/特例/推广
- 上级：Tuning / tools（诊断工具）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → tuning → tools → jconsole

