---
type: concept
tags:
  - network
  - tcp
  - transport
---

# TCP连接标识（五元组）

## 一句话
TCP 连接在网络栈中常以五元组 $\{协议, 源IP, 源端口, 目的IP, 目的端口\}$ 作为标识，从而在同一主机上区分不同连接。

## 严格定义
在传输层语义下，一个 TCP 连接可以用两端点（本地端点与远端端点）与协议类型来描述；五元组是端点对与协议的组合表示形式，用于刻画“同一主机上多条 TCP 连接之间的区分维度”。在具体操作系统实现中，连接还包含运行态状态（例如连接状态机、窗口、重传等），五元组是连接识别与路由的一部分信息。

## 接口：数据 + 约束
- 数据：
  - 协议：TCP
  - 端点对：$(srcIP, srcPort)$ 与 $(dstIP, dstPort)$
- 约束：
  - “唯一性”讨论具有作用域：通常指同一主机的网络栈在处理到达分组与套接字查找时使用的区分维度；在存在 NAT、端口复用或不同 network namespace 等场景下，需要额外指定作用域。

## 常用构造/操作（仅列出接口与符号）
- 五元组：$\{TCP, srcIP, srcPort, dstIP, dstPort\}$
- 端点对（socket pair）：$\{(srcIP, srcPort), (dstIP, dstPort)\}$

## 关系：上级/下级/等价/特例/推广
- 相关：socket 与 accept（见 [../../os/accept（监听socket与已连接socket）.md](../../os/accept（监听socket与已连接socket）.md)）
- 相关：文件描述符（FD）（见 [../../os/文件描述符（FD）.md](../../os/文件描述符（FD）.md)）

## 把新概念挂回框架（多级索引轨迹）
网络 → transport → tcp → TCP连接标识（五元组）。

