---
title: 长上下文（Context）
date: "2026-01-31"
categories:
  - agent
description: "导航：llm/README.md | 索引.md"
---
# 长上下文（Context）

导航：[llm/README.md](../README.md) | [索引.md](索引.md)

本目录包含扩展和利用 LLM 上下文窗口的技术。

---

## 条目列表

### 上下文扩展技术
- [ContextWindow](ContextWindow.md)：上下文窗口基础概念
- [LongContext-Attention](LongContext-Attention.md)：长上下文注意力优化（DCA、StreamingLLM等分块注意力方法）
- [PositionInterpolation](PositionInterpolation.md)：位置插值
- [StreamingLLM](StreamingLLM.md)：流式 LLM

### 上下文利用技术
- [RAG](RAG.md)：Retrieval-Augmented Generation

---

## 关系

- 相关：[RoPE](../architecture/Transformer/mechanics/RoPE.md)
- 相关：[KVCache](../inference/acceleration/KVCache.md)
