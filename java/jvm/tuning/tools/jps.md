---
type: tool
tags:
  - java/jvm
  - jvm
  - tuning
  - tools
---

# jps（JVM 进程查看工具）

## 一句话
`jps` 是 JDK 提供的命令行工具，用于列出本机可见的 Java 进程及其主类/启动参数摘要（输出字段依实现与权限而定）。

## 严格定义
在给定运行环境与权限条件下，`jps` 输出一个进程集合 $\{(pid_i, info_i)\}$，其中 $pid_i$ 为 Java 进程标识，$info_i$ 为实现定义的摘要信息。该输出作为其他诊断工具（如 `jstat`/`jcmd`/`jstack`/`jmap`）定位目标 PID 的入口之一。

## 接口：数据 + 约束
- 输入：
  - 可选参数（决定输出字段集合）
- 输出：
  - PID 列表与摘要信息
- 约束：
  - 可见进程集合依操作系统权限、容器隔离与 JVM 实现而定。

## 常用构造/操作（仅列出接口与符号）
- PID → 其他工具：`jstat`（见 [jstat.md](jstat.md)）、`jcmd`（见 [jcmd.md](jcmd.md)）

## 关系：上级/下级/等价/特例/推广
- 上级：Tuning / tools（诊断工具）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → tuning → tools → jps

