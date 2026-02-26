---
title: 规划与推理（Planning）
date: "2026-02-02"
categories:
  - agent
description: "导航：agent/README.md | 索引.md"
---
# 规划与推理（Planning）

导航：[agent/README.md](../README.md) | [索引.md](索引.md)

Agent 的规划模块，负责任务分解、推理与决策。

---

## 定义

Planning：将复杂目标分解为可执行步骤，并通过推理选择最优行动序列的过程。

---

## 子目录

| 目录 | 说明 |
|------|------|
| [decomposition/](decomposition/) | 任务分解方法 |
| [reflection/](reflection/) | 反思与自我改进 |
| [search/](search/) | 搜索算法 |

---

## 条目列表

### 任务分解
- [ChainOfThought](decomposition/ChainOfThought.md)（CoT）
- [TreeOfThoughts](decomposition/TreeOfThoughts.md)（ToT）
- [GraphOfThoughts](decomposition/GraphOfThoughts.md)（GoT）
- [LeastToMost](decomposition/LeastToMost.md)

### 反思
- [Reflexion](reflection/Reflexion.md)
- [SelfRefine](reflection/SelfRefine.md)
- [SelfCritique](reflection/SelfCritique.md)

### 搜索
- [MCTS](search/MCTS.md)
- [BeamSearch](search/BeamSearch.md)
- [BestFirstSearch](search/BestFirstSearch.md)

---

## 关系

- 上级：[Agent](../README.md)
- 依赖：[LLM](../llm/README.md)、[Memory](../memory/README.md)
- 下游：[Action](../action/README.md)
