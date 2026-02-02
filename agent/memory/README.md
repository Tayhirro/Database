# 记忆系统（Memory）

导航：[agent/README.md](../README.md) | [索引.md](索引.md)

Agent 的记忆系统，负责信息的存储、检索与管理。

---

## 定义

Memory：使 Agent 能够存储、检索和利用历史信息的系统，扩展 LLM 有限的上下文窗口。

---

## 子目录

| 目录 | 说明 |
|------|------|
| [short-term/](short-term/) | 短期记忆（会话级） |
| [long-term/](long-term/) | 长期记忆（跨会话持久化） |
| [external/](external/) | 外部存储（向量库、知识图谱） |
| [systems/](systems/) | 记忆管理系统（MemGPT、EverMemOS） |

---

## 记忆类型

```
Memory
├── Short-term Memory（短期记忆）
│   ├── 存储：当前会话上下文
│   ├── 容量：受 context window 限制
│   └── 策略：滑动窗口、摘要压缩
│
├── Long-term Memory（长期记忆）
│   ├── Episodic Memory（事件记忆）：具体事件、对话历史
│   ├── Semantic Memory（语义记忆）：事实、知识
│   └── Procedural Memory（程序记忆）：技能、方法
│
└── External Memory（外部记忆）
    ├── Vector Store：向量相似度检索
    ├── Knowledge Graph：结构化知识
    └── Database：结构化数据存储
```

---

## 条目列表

### 短期记忆
- [ConversationBuffer](short-term/ConversationBuffer.md)
- [SlidingWindow](short-term/SlidingWindow.md)
- [SummaryMemory](short-term/SummaryMemory.md)

### 长期记忆
- [EpisodicMemory](long-term/EpisodicMemory.md)
- [SemanticMemory](long-term/SemanticMemory.md)
- [ProceduralMemory](long-term/ProceduralMemory.md)

### 外部存储
- [VectorStore](external/VectorStore.md)
- [KnowledgeGraph](external/KnowledgeGraph.md)
- [RAG](external/RAG.md)

### 记忆系统
- [MemGPT](systems/MemGPT.md)
- [EverMemOS](systems/EverMemOS.md)
- [Letta](systems/Letta.md)

---

## 关系

- 上级：[Agent](../README.md)
- 依赖：[LLM](../llm/README.md)（记忆需要 LLM 进行摘要、检索判断）
- 相关：[Planning](../planning/README.md)（规划依赖历史记忆）
