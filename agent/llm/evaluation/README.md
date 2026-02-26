---
title: 评估（Evaluation）
date: "2026-01-31"
categories:
  - agent
description: "导航：llm/README.md | 索引.md"
---
# 评估（Evaluation）

导航：[llm/README.md](../README.md) | [索引.md](索引.md)

本目录包含 LLM 能力评估的指标与基准。

---

## 子目录

| 目录 | 说明 |
|------|------|
| [benchmarks/](benchmarks/) | 评测基准 |

---

## 条目列表

### 指标
- [CrossEntropy](CrossEntropy.md)：交叉熵损失，语言模型训练的核心目标函数
- [Perplexity](Perplexity.md)：困惑度，交叉熵的指数形式

### 基准
- [MMLU](benchmarks/MMLU.md)：多任务语言理解
- [HumanEval](benchmarks/HumanEval.md)：代码生成
- [GSM8K](benchmarks/GSM8K.md)：数学推理
- [MTBench](benchmarks/MTBench.md)：多轮对话

### 安全
- [SafetyEval](SafetyEval.md)：安全性评估
