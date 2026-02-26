---
title: 行动执行（Action）
date: "2026-02-02"
categories:
  - agent
description: "导航：agent/README.md | 索引.md"
---
# 行动执行（Action）

导航：[agent/README.md](../README.md) | [索引.md](索引.md)

Agent 与环境交互的执行层。

---

## 定义

Action：Agent 将规划结果转化为实际操作并作用于环境的过程。

---

## 条目列表

- [ActorInterface](ActorInterface.md)
- [EnvironmentInteraction](EnvironmentInteraction.md)
- [FeedbackLoop](FeedbackLoop.md)
- [ActionSpace](ActionSpace.md)
- [ExecutionEngine](ExecutionEngine.md)

---

## 关系

- 上级：[Agent](../README.md)
- 输入：[Planning](../planning/README.md) 输出
- 依赖：[Tool](../tool/README.md)
- 输出：作用于 Environment
