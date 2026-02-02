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
