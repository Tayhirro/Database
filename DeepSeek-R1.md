---
title: DeepSeek-R1
date: "2026-01-12"
categories:
  - agent
tags:
  - 大模型
  - 推理模型
  - DeepSeek
description: DeepSeek 推出的开源推理大模型系列，通过大规模强化学习训练，具备强大的数学推理和代码生成能力。
---

# DeepSeek-R1

> DeepSeek 推理模型系列

---

## 概述

DeepSeek-R1 是 DeepSeek AI 推出的开源推理大模型，于 2025 年初发布。该模型系列包括多个版本：

- **DeepSeek-R1-Zero** — 直接通过大规模强化学习（RL）训练的原始版本
- **DeepSeek-R1** — 经过冷启动数据优化后的最终版本
- **DeepSeek-R1-Distill** — 基于 Llama/Qwen 等底座模型蒸馏的小型版本

---

## 技术特点

### 1. 强化学习训练

DeepSeek-R1-Zero 采用了纯强化学习方法（GRPO），不经过 SFT 阶段，直接通过 RL 训练涌现出强大的推理能力。

### 2. 思维链（CoT）

模型通过长思维链（Long Chain-of-Thought）展示推理过程，能够：

- 数学推理与证明
- 代码编写与调试
- 复杂问题分析
- 逻辑推理

### 3. 开源可商用

DeepSeek-R1 采用 MIT 许可证开源，支持商业使用，推动了 AI 技术的民主化。

---

## 模型版本

| 模型 | 参数 | 特点 |
|------|------|------|
| DeepSeek-R1 | 671B (37B active) | MoE 架构，完整版 |
| DeepSeek-R1-Zero | 671B | 纯 RL 训练版本 |
| R1-Distill-Llama-8B | 8B | Llama 3.1 蒸馏 |
| R1-Distill-Qwen-7B | 7B | Qwen2.5 蒸馏 |

---

## 与 OpenAI-o1 对比

DeepSeek-R1 与 OpenAI-o1 都是推理模型，但在以下方面有所不同：

- **开源 vs 闭源**：DeepSeek-R1 完全开源
- **价格**：DeepSeek-R1 API 成本远低于 o1
- **蒸馏版本**：DeepSeek 提供多种小型蒸馏版本

---

## 参考资料

- [DeepSeek 官方仓库](https://github.com/deepseek-ai/DeepSeek-R1)
- [HuggingFace 模型](https://huggingface.co/deepseek-ai/DeepSeek-R1)
