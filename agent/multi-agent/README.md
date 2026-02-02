# 多智能体系统（Multi-Agent）

导航：[agent/README.md](../README.md) | [索引.md](索引.md)

多个 Agent 协作完成任务的系统。

---

## 定义

Multi-Agent System：多个自主 Agent 通过通信与协调机制共同完成复杂任务的系统。

---

## 子目录

| 目录 | 说明 |
|------|------|
| [communication/](communication/) | 通信机制 |
| [coordination/](coordination/) | 协调策略 |
| [frameworks/](frameworks/) | 多智能体框架 |

---

## 条目列表

### 通信
- [MessagePassing](communication/MessagePassing.md)
- [SharedMemory](communication/SharedMemory.md)
- [Blackboard](communication/Blackboard.md)

### 协调
- [Hierarchical](coordination/Hierarchical.md)（层级式）
- [Democratic](coordination/Democratic.md)（民主式）
- [MarketBased](coordination/MarketBased.md)（市场式）

### 框架
- [MetaGPT](frameworks/MetaGPT.md)
- [CrewAI](frameworks/CrewAI.md)
- [AutoGen](frameworks/AutoGen.md)
- [CAMEL](frameworks/CAMEL.md)
- [ChatDev](frameworks/ChatDev.md)

---

## 关系

- 上级：[Agent](../README.md)
- 组合：多个 [Agent](../README.md)
