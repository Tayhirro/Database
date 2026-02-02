# Agent 知识库

导航：[索引.md](索引.md) | [概念图.md](概念图.md)

基于大语言模型的智能体（LLM-based Agent）系统知识库。

---

## 定义

Agent：能够感知环境、进行推理规划、调用工具、执行行动并从反馈中学习的自主系统。

$$
\text{Agent} = \text{LLM} + \text{Memory} + \text{Planning} + \text{Tool Use} + \text{Action}
$$

---

## 模块结构

| 模块 | 说明 | 入口 |
|------|------|------|
| llm/ | 大语言模型基座 | [README](llm/README.md) |
| memory/ | 记忆系统（短期/长期/外部） | [README](memory/README.md) |
| planning/ | 规划与推理（任务分解、反思） | [README](planning/README.md) |
| tool/ | 工具使用（API、代码执行） | [README](tool/README.md) |
| action/ | 行动执行与环境交互 | [README](action/README.md) |
| perception/ | 感知（多模态输入处理） | [README](perception/README.md) |
| multi-agent/ | 多智能体协作与通信 | [README](multi-agent/README.md) |
| evaluation/ | Agent 评估基准与方法 | [README](evaluation/README.md) |
| safety/ | 安全、对齐与可控性 | [README](safety/README.md) |

---

## Agent 系统架构

```
                    ┌─────────────────────────────────────────┐
                    │              Environment                │
                    └─────────────────────────────────────────┘
                              ↑                    ↓
                         [Action]            [Perception]
                              ↑                    ↓
┌─────────────────────────────────────────────────────────────────┐
│                           Agent                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐         │
│  │  Memory  │←→│   LLM    │←→│ Planning │←→│   Tool   │         │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘         │
│       ↑                                          ↓              │
│  [Retrieve]                                 [Execute]           │
└─────────────────────────────────────────────────────────────────┘
```

---

## 典型 Agent 框架

| 框架 | 特点 |
|------|------|
| ReAct | Reasoning + Acting 交替 |
| AutoGPT | 自主任务规划与执行 |
| BabyAGI | 任务驱动的自主 Agent |
| LangChain Agents | 模块化工具链 |
| MetaGPT | 多角色软件开发 |
| CrewAI | 多智能体协作 |

---

## 参考文献

- Lilian Weng. *LLM Powered Autonomous Agents*. 2023.
- Significant Gravitas. *AutoGPT*. 2023.
- Yohei Nakajima. *BabyAGI*. 2023.
