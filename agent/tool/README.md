---
title: 工具使用（Tool）
date: "2026-02-02"
categories:
  - agent
description: "导航：agent/README.md | 索引.md"
---
# 工具使用（Tool）

导航：[agent/README.md](../README.md) | [索引.md](索引.md)

Agent 的工具使用模块，扩展 LLM 的能力边界。

---

## 定义

Tool Use：Agent 调用外部工具（API、代码执行器、数据库等）完成特定任务的能力。

---

## 子目录

| 目录 | 说明 |
|------|------|
| [api/](api/) | API 调用 |
| [code/](code/) | 代码执行 |
| [frameworks/](frameworks/) | 工具使用框架 |

---

## 条目列表

### API 调用
- [WebSearch](api/WebSearch.md)
- [Calculator](api/Calculator.md)
- [ExternalAPI](api/ExternalAPI.md)

### 代码执行
- [CodeInterpreter](code/CodeInterpreter.md)
- [Sandbox](code/Sandbox.md)
- [REPL](code/REPL.md)

### 框架
- [FunctionCalling](frameworks/FunctionCalling.md)
- [Toolformer](frameworks/Toolformer.md)
- [MCP](frameworks/MCP.md)（Model Context Protocol）

---

## 关系

- 上级：[Agent](../README.md)
- 扩展：[LLM](../llm/README.md) 能力
- 相关：[Action](../action/README.md)

---

## 交叉引用：工具相关训练

如何训练模型使用工具，见 LLM 训练模块：

- [Agentic Training](../llm/training/post-training/agentic/)：Agent 能力训练
  - [Search-R1](../llm/training/post-training/agentic/Search-R1.md)：搜索工具 + RL
  - [Toolformer](../llm/training/post-training/agentic/Toolformer.md)：自监督工具学习
  - [WebGPT](../llm/training/post-training/agentic/WebGPT.md)：网页浏览 + RLHF
