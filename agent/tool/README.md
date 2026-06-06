---
title: 工具使用（Tool Use）
date: "2026-02-02"
categories:
  - agent
description: "导航：agent/README.md | 索引.md"
---

# 工具使用（Tool Use）

导航：[agent/README.md](../README.md) | [索引.md](索引.md)

Tool Use 是 Agent 系统扩展 LLM 能力边界的核心机制。它使模型能够在生成文本之外，调用外部 API、代码执行器、数据库、浏览器、文件系统、搜索系统或专用运行环境，以完成需要外部状态、外部计算或真实环境交互的任务。

---

## 定义

**Tool Use** 指 Agent 在任务执行过程中，根据当前上下文选择并调用外部工具，将工具返回结果纳入后续推理与决策，从而完成单纯语言生成无法可靠完成的操作。

一个完整的工具使用系统通常需要回答以下问题：

1. **工具发现**：模型或 Agent 如何知道当前有哪些工具可用。
2. **工具描述**：工具的名称、用途、参数、约束和返回值如何表示。
3. **调用表达**：模型如何表达一次工具调用，包括工具名与参数。
4. **调用解析**：Agent runtime 如何解析模型输出，并将其转换为可执行请求。
5. **调度执行**：工具调用由哪个组件执行，执行环境在哪里，权限边界如何定义。
6. **结果回写**：工具结果如何作为 observation、tool result 或 message 回写到上下文。
7. **状态管理**：工具执行产生的环境状态、会话状态、文件状态和中间结果如何保持。
8. **失败处理**：解析失败、执行失败、超时、权限错误和不安全调用如何处理。
9. **标准化**：工具、资源、权限和状态是否需要跨应用、跨 runtime 或跨模型统一描述。

---

## 分层模型

工具使用可以分为七个相互关联但边界不同的层级。

| 层级        | 核心问题                     | 典型对象                                  | 代表方向                                    |
| --------- | ------------------------ | ------------------------------------- | --------------------------------------- |
| 行动范式层     | 模型如何在思考、行动、观察之间循环        | `action`、`observation`                | WebGPT、ReAct                            |
| 工具学习层     | 模型如何学习何时调用工具、如何传参、如何利用结果 | tool-use annotation、API call          | Toolformer、Agentic Training             |
| 调用格式层     | 模型如何结构化表达工具调用            | `tool_call`、function call、JSON schema | OpenAI function calling、Claude tool use |
| Agent 编排层 | 维护循环、状态、重试、终止和历史         | `step`、trajectory、runtime state       | LangChain、AutoGen、SWE-agent             |
| 交互界面层     | 工具如何设计得适合 Agent 使用       | ACI command、编辑器、浏览器接口                 | SWE-agent ACI                           |
| 执行环境层     | 调用在哪里执行，环境状态如何隔离和保持      | sandbox、shell session、runtime         | Code Interpreter、Sandbox、SWE-ReX        |
| 工具协议层     | 工具和资源如何跨应用标准化暴露          | MCP server、tool、resource              | MCP                                     |

这些层级经常同时出现在同一个 Agent 系统中，但它们解决的问题不同。分析工具系统时，应避免将模型调用格式、工具协议和执行 runtime 混为同一概念。

---

## 核心区分

### Function Calling

Function Calling 属于**调用格式层**。它解决的问题是：模型如何以结构化方式表达“调用哪个工具，以及传入哪些参数”。

典型结构包括：

```text
tool name
argument schema
argument values
tool call id
```

Function Calling 本身不负责执行工具。工具是否执行、在哪里执行、执行结果如何回写，由 Agent runtime 或工具服务决定。

### MCP

MCP（Model Context Protocol）属于**工具协议层**。它解决的问题是：AI 应用如何以统一协议连接外部工具、资源和提示模板。

MCP 的核心对象包括：

```text
host
client
server
tool
resource
prompt
tools/list
tools/call
resources/read
```

MCP 标准化的是 Agent 应用与外部工具服务之间的连接方式，而不是某个具体工具的内部执行机制。

需要注意，MCP server 可以运行在本地，也可以运行在远程。因此，MCP 与本地工具系统的差异不在于“本地还是远程”，而在于是否存在标准化的 client-server 协议边界：

```text
本地内嵌工具：应用自己定义、自己加载、自己执行。
本地 MCP server：工具仍可在本机运行，但通过 MCP 协议对 host 暴露。
远程 MCP server：工具运行在远程服务中，同样通过 MCP 协议暴露。
```

### SWE-ReX

SWE-ReX 属于**执行环境层**。它解决的问题是：命令如何在隔离、持久、可控的代码运行环境中执行。

在 SWE-agent 中，SWE-ReX 负责：

- 启动 Docker 或远程运行环境
- 创建并维护 shell session
- 执行 `BashAction`
- 读取和写入文件
- 中断长时间运行的命令
- 保持 repo working tree 状态
- 返回 stdout、exit code 和文件变化

SWE-ReX 不是 MCP，也不是模型的 function calling 格式。它是 Agent 执行动作时使用的 runtime backend。

### ACI

ACI（Agent-Computer Interface）属于**交互界面层**。它关注的问题是：应当为 Agent 暴露怎样的计算机交互界面。

对于 coding agent，裸 shell 并不总是最合适的接口。ACI 会关注：

- 文件查看粒度
- 编辑命令形式
- 错误反馈结构
- 返回内容长度控制
- 状态摘要
- 提交语义

ACI 的目标是降低模型使用计算机环境的认知负担，提高工具调用的稳定性。

---

## 典型调用路径

一个通用工具调用路径可以表示为：

```text
context
  -> model output
  -> tool call representation
  -> parser / dispatcher
  -> tool executor
  -> external environment
  -> observation / result
  -> updated context
```

以 SWE-agent 为例，其工具调用路径更接近：

```text
model output
  -> ToolHandler.parse_actions(output)
  -> action string
  -> DefaultAgent.handle_action(step)
  -> SWEEnv.communicate(action)
  -> SWE-ReX BashAction
  -> Docker / remote shell session
  -> observation
  -> history / trajectory
```

其中：

- `tool_calls` 或 XML / JSON / code block 只描述模型输出的调用形式。
- `ToolHandler` 负责将模型输出解析为 Agent 内部的 `action`。
- `SWEEnv` 负责把 `action` 交给环境执行。
- SWE-ReX 负责真实执行与环境状态维护。
- observation 回写后，下一轮 step 才能继续使用新的环境反馈。

### MCP 调用路径

MCP 场景下，模型通常并不直接连接 MCP server。连接、发现、调用和结果回填由 host / Agent runtime 完成。

```text
Host / Agent runtime
  -> connect MCP server
  -> initialize / capability negotiation
  -> tools/list
  -> merge tool registry
  -> expose tool schema to model
  -> model emits tool call
  -> host routes to MCP tools/call
  -> MCP server executes
  -> tool result returns to host
  -> host writes result back to model context
```

因此，MCP 不是替代模型 function calling 的输出格式。更准确地说，MCP 位于模型工具调用格式之后、具体工具执行之前：

```text
model tool_call
  -> host / client routing
  -> MCP tools/call
  -> MCP server execution
  -> tool result
```

---

## 高阶抽象：外部能力接入系统

Tool Use 是 Agent 系统中的**外部能力接入系统**：模型或 Agent 需要把语言推理连接到搜索、数据库、代码执行器、浏览器、文件系统、业务 API、桌面环境、其他 Agent 或真实世界环境。
工具使用是多个问题域的并行分化：

```text
Agent system
  -> cognitive control: reasoning / planning / memory
  -> external capability access: tools / APIs / computers / environments
  -> execution substrate: sandbox / shell session / browser / robot / remote runtime
  -> interoperability: plugins / MCP / A2A / enterprise connectors
  -> governance: permission / confirmation / audit / policy / evaluation
```
### 1. 行动范式线

这一线回答的问题是：模型如何从“一次性回答”变成“观察环境、采取行动、再根据反馈继续行动”。

代表方向包括 WebGPT、MRKL、ReAct：

- WebGPT 将模型放入文本浏览器环境，让模型通过搜索、导航和引用回答问题。
- MRKL 将 LLM 与外部知识源、计算模块和符号推理模块组合起来。
- ReAct 将 reasoning trace 与 action 交替生成，使模型能够在推理过程中调用外部环境。

这一线的核心对象是
```text
thought
action
observation
trajectory
```
### 2. 模型工具能力线
这一线回答的问题是：模型本身如何学习何时调用工具、调用哪个工具、如何传参、如何利用返回结果。
代表方向包括 Toolformer、API-Bank、Gorilla：
- Toolformer 关注模型如何通过自监督数据学习 API 调用。
- API-Bank 关注工具增强 LLM 的任务、数据集和评测。
- Gorilla 关注大规模 API 选择与调用生成能力。
这一线的核心对象是
```text
tool-use annotation
API call
argument generation
tool-use benchmark
tool-augmented model
```

它关注的是模型能力。一个模型可以很会写 API call，但仍然不知道你的本地工具在哪里执行。
### 3. 调用表达线
这一线回答的问题是：模型输出如何被程序稳定消费。
代表方向包括 OpenAI function calling、Claude tool use、JSON schema tool calling。它们将早期依赖 prompt 约定和正则解析的 action，变成结构化对象：

```text
tool name
tool call id
arguments schema
arguments value
tool result
```

这一线解决的是“模型如何表达调用”，不解决“工具在哪里发现、谁执行、状态如何保持、权限如何管理”。
### 4. Agent 编排线

这一线回答的问题是：谁维护循环、状态、重试、历史、终止条件和错误恢复。

LangChain、AutoGen、SWE-agent、OpenAI Agents SDK 等框架处在这一层。它们通常会维护：
```text
agent loop
step
state
memory
tool registry
dispatcher
retry policy
trajectory
```

这一线是 runtime 是否拥有主循环。没有主循环，工具调用只是一次函数调用；有了主循环，工具调用才成为 Agent 行动链的一部分。

### 5. 交互界面线

这一线回答的问题是：工具接口是否适合 Agent 使用，SWE-agent ACI 属于这一线。它重新设计 coding agent 与计算机交互的命令界面，包括文件查看、编辑、测试、错误反馈和上下文长度控制，Anthropic Computer Use、OpenAI computer use 等方向也可以放在这一线：它们将工具接口从结构化 API 扩展到屏幕、鼠标、键盘和 GUI 操作。
这一线的核心对象是：
```text
computer interface
browser interface
editor command
screen observation
mouse / keyboard action
agent-computer interface
```

### 6. 执行环境线

这一线回答的问题是：调用在哪里真实执行，执行状态如何隔离、保持和观察。
代表方向包括 Code Interpreter、sandbox、SWE-ReX、远程 shell runtime。它们关注：

- 文件系统状态
- shell session
- 运行超时与中断
- stdout / stderr / exit code
- repo working tree
- 容器隔离
- 资源限制

SWE-ReX 属于这一线。它是把 action 放进 Docker 或远程 runtime 中执行的环境层。
### 7. 工具与资源协议线

这一线回答的问题是：外部工具、资源和提示模板如何被不同 AI 应用发现和调用。

ChatGPT Plugins、OpenAPI manifest、MCP 都属于这一方向，但抽象程度不同：

```text
host-specific plugin
  -> API manifest / OpenAPI
  -> protocol-level tool and resource server
```
MCP 的意义在于把工具与资源从某个 Agent 应用内部抽离为协议对象：
```text
MCP server
  -> tools/list
  -> tools/call
  -> resources/list
  -> resources/read
  -> prompts/list
```

### 8. Agent 互操作线

这一线回答的问题是：一个 Agent 如何发现、委托、调用或协作另一个 Agent。

A2A 属于这一方向。它和 MCP 相邻，但不是同一类问题：

```text
MCP: Agent / host -> tool or resource server
A2A: Agent -> Agent
```

在复杂系统里，一个远程 Agent 可以被包装成 tool。
### 9. 治理与运维线
当工具调用会产生真实副作用时，系统还必须回答：

- 谁授权工具调用。
- 哪些动作需要用户确认。
- 调用日志如何审计。
- 凭证如何隔离。
- 工具输出是否可信。
- prompt injection 如何防护。
- 执行失败如何恢复。
- 工具能力如何评测。

这一线往往不在论文原型中充分展开，但在生产系统中会决定工具系统能否上线。

### 对 SWE-agent 的重新定位

SWE-agent 不应被理解为“没有 MCP 的旧式工具系统”。更准确的定位是：

```text
SWE-agent
  -> Agent 编排线：step / trajectory / parse_actions / handle_action
  -> 交互界面线：ACI command / file editing / test feedback
  -> 执行环境线：SWE-ReX / BashAction / Docker or remote runtime
  -> 调用表达线：XML / JSON / function calling parser
```

它目前没有走“工具协议线”的 MCP 路径。原因不是能力低一级，而是设计目标不同：SWE-agent 优先服务 coding-agent 闭环，需要稳定的仓库状态、shell session、编辑命令和测试反馈；MCP 优先解决跨应用的工具与资源暴露。
如果把 SWE-agent 工具 MCP 化，应该理解为新增一个协议适配层：
```text
MCP client / host
  -> MCP server adapter
  -> SWE-agent command or SWE-ReX request
  -> Docker / remote runtime
  -> MCP tool result
```

这个适配可以提高跨应用复用能力，但不会替代 SWE-agent 自身的编排、ACI 和执行环境。
---

## 阅读顺序：按问题而非按单线阶段

如果把上述分支写成严格时间线，会误导读者以为后出现的方向必然替代先出现的方向。更合适的读法是按问题进入：

| 问题             | 应看的方向                                         | 典型对象                                         |
| -------------- | --------------------------------------------- | -------------------------------------------- |
| 模型如何多步行动       | WebGPT、MRKL、ReAct                             | `thought`、`action`、`observation`             |
| 模型如何学会用工具      | Toolformer、API-Bank、Gorilla                   | API call、tool-use data、benchmark             |
| 模型如何稳定表达调用     | OpenAI function calling、Claude tool use       | `tool_call`、JSON schema、arguments            |
| 谁维护 Agent 主循环  | LangChain、AutoGen、SWE-agent、OpenAI Agents SDK | step、state、trajectory、retry                  |
| 工具界面如何适配 Agent | SWE-agent ACI、Computer Use                    | editor command、screen observation、GUI action |
| 调用在哪里执行        | Code Interpreter、sandbox、SWE-ReX              | runtime、shell session、filesystem             |
| 工具如何跨应用暴露      | Plugins、OpenAPI manifest、MCP                  | server、resource、`tools/list`、`tools/call`    |
| Agent 如何互相协作   | A2A、multi-agent orchestration                 | agent card、task、message                      |
| 真实副作用如何治理      | policy、permission、audit、eval                  | approval、credential、log、guardrail            |

这个表的作用是定位问题域，而不是描述一个技术替代序列。MCP 只覆盖“工具如何跨应用暴露”这一格；SWE-ReX 只覆盖“调用在哪里执行”这一格；SWE-agent 则横跨 Agent 编排、ACI、调用表达和执行环境多个格子。

---

## 简化时间线

| 时间 | 节点 | 意义 |
|---|---|---|
| 2021 | WebGPT | 将模型放入浏览环境，通过搜索、导航和引用回答问题。 |
| 2022 | MRKL / ReAct | 建立模块化工具路由与推理-行动交替范式。 |
| 2023 | Toolformer | 将工具调用作为模型可学习能力进行探索。 |
| 2023 | API-Bank / Gorilla | 将工具增强 LLM 的评测、API 选择和 API 调用生成系统化。 |
| 2023 | ChatGPT Plugins / OpenAI function calling | 推动工具调用从自然语言约定转向结构化 schema。 |
| 2024 | SWE-agent ACI | 强调为 coding agent 设计专用计算机交互界面。 |
| 2024 | SWE-ReX | 将 sandboxed shell execution 抽象为独立 runtime 层。 |
| 2024-11 | MCP | 将工具、资源和提示模板连接方式标准化为跨应用协议。 |
| 2024-2025 | Computer Use / CUA | 将工具接口扩展到屏幕、鼠标、键盘和 GUI 操作循环。 |
| 2025 | A2A | 将互操作问题从 Agent-Tool 扩展到 Agent-Agent 协作。 |

---

## 撰写依据与充分性评估

### 撰写依据

本文采用分层归纳法，而不是单纯编年史写法。资料依据包括六类：

1. **工具行动范式论文**：WebGPT、ReAct、MRKL，用于梳理工具使用从“模型回答”走向“环境交互”的早期脉络。
2. **模型工具能力论文与评测**：Toolformer、API-Bank、Gorilla，用于区分模型自身的工具学习能力与 runtime 工具系统。
3. **模型厂商工具调用文档**：OpenAI function calling、Anthropic tool use，用于区分模型输出层的结构化调用格式。
4. **计算机交互与执行环境文档**：Anthropic Computer Use、OpenAI Computer Use、SWE-ReX 文档，用于区分 GUI / shell / sandbox / runtime 等执行载体。
5. **协议与互操作文档**：MCP 官方架构文档、Google A2A 发布说明，用于区分 Agent-Tool 协议和 Agent-Agent 协议。
6. **本地源码观察**：SWE-agent 的 `ToolConfig`、`Command`、`parse_function`、`SWEEnv.communicate()`、SWE-ReX `BashAction` 调用链，用于定位 SWE-agent 在分层模型中的位置。

### 充分性结论

本文目前足以支持以下目标：

- 区分 function calling、MCP、SWE-ReX、ACI 的抽象层级。
- 解释为什么“都有 tool schema”并不意味着它们是同一类机制。
- 说明 Tool Use 不是“本地工具升级到 MCP”的单线发展，而是行动范式、模型能力、调用格式、Agent 编排、交互界面、执行环境、协议互操作和治理运维的多线并行发展。
- 用 SWE-agent 作为案例说明“内部工具系统 + 执行环境 runtime”的组合形态。

本文尚不覆盖以下内容：

- MCP 的完整协议字段、生命周期状态机和安全模型。
- OpenAI、Anthropic、Google 等厂商工具调用接口的逐字段差异。
- LangChain、AutoGen、CrewAI 等框架的完整实现对比。
- SWE-agent 每个 tool bundle 的具体 schema、安装方式和执行细节。
- 工具安全、权限委托、用户确认、审计日志和 prompt injection 防护的系统化分析。

因此，本文适合作为 Tool Use 的结构总览和概念边界说明；若用于实现 MCP server、设计生产级工具权限系统或深入分析 SWE-agent 工具执行链，还需要拆分为更细的专题文档。

---

## 判断问题

分析一个工具系统时，可以使用以下问题定位其抽象层：

1. 工具 schema 在哪里定义。
2. 模型输出的工具调用是什么格式。
3. 哪个组件解析模型输出。
4. 哪个组件执行工具调用。
5. 执行环境是否具有持久状态。
6. 工具结果如何回写到上下文。
7. 是否存在跨应用工具发现与调用协议。
8. 是否存在权限、隔离、审计和恢复机制。

判断示例：

| 调用链 | 所属重点 |
|---|---|
| `tool_calls -> parser -> action -> Docker shell` | 执行环境线 |
| `tools/list -> tools/call -> MCP server` | 工具协议线 |
| `thought -> action -> observation` | Agent 行动范式线 |
| `schema -> structured arguments -> tool result` | 调用格式线 |

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
- SWE-ReX：执行环境 runtime，不是 MCP 同层工具协议
- SWE-agent ACI：面向 coding agent 的计算机交互界面设计

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

---

## 参考资料

- [WebGPT: Browser-assisted question-answering with human feedback](https://arxiv.org/abs/2112.09332)
- [MRKL Systems: A modular, neuro-symbolic architecture](https://arxiv.org/abs/2205.00445)
- [ReAct: Synergizing Reasoning and Acting in Language Models](https://arxiv.org/abs/2210.03629)
- [Toolformer: Language Models Can Teach Themselves to Use Tools](https://arxiv.org/abs/2302.04761)
- [API-Bank: A Comprehensive Benchmark for Tool-Augmented LLMs](https://arxiv.org/abs/2304.08244)
- [Gorilla: Large Language Model Connected with Massive APIs](https://arxiv.org/abs/2305.15334)
- [OpenAI: ChatGPT plugins](https://openai.com/blog/chatgpt-plugins)
- [OpenAI plugins quickstart](https://github.com/openai/plugins-quickstart)
- [OpenAI: Function calling and other API updates](https://openai.com/index/function-calling-and-other-api-updates/)
- [OpenAI Agents SDK: Tools](https://openai.github.io/openai-agents-python/tools/)
- [OpenAI: Computer use](https://developers.openai.com/api/docs/guides/tools-computer-use)
- [Anthropic: Tool use with Claude](https://docs.anthropic.com/en/docs/agents-and-tools/tool-use/overview)
- [Anthropic: Computer use tool](https://platform.claude.com/docs/en/agents-and-tools/tool-use/computer-use-tool)
- [Anthropic: Developing a computer use model](https://www.anthropic.com/research/developing-computer-use)
- [Google: Announcing the Agent2Agent Protocol](https://developers.googleblog.com/en/a2a-a-new-era-of-agent-interoperability/)
- [SWE-agent: Agent-Computer Interfaces Enable Automated Software Engineering](https://arxiv.org/abs/2405.15793)
- [SWE-ReX documentation](https://swe-rex.com/latest/)
- [SWE-ReX PyPI release history](https://pypi.org/project/swe-rex/)
- [Model Context Protocol: What is MCP?](https://modelcontextprotocol.io/docs/getting-started/intro)
- [Model Context Protocol: Architecture overview](https://modelcontextprotocol.io/docs/learn/architecture)
