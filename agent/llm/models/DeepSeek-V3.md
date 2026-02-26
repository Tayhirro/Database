---
title: DeepSeek-V3
date: "2026-01-12"
categories:
  - agent
tags:
  - 大模型
  - MoE
  - DeepSeek
description: DeepSeek-V3 技术摘要：MoE 架构、训练流水线与通信优化
---

# DeepSeek-V3 技术摘要

> DeepSeek-V3: Strong MoE Language Model

---

## 1. 架构概述

DeepSeek-V3 是 DeepSeek 推出的 **大规模 MoE（Mixture of Experts）语言模型**，采用 **Multi-head Latent Attention (MLA)**  + **DeepSeekMoE** 架构。

### 核心特性

| 特性 | 描述 |
|------|------|
| **总参数** | 671B |
| **激活参数** | 37B |
| **架构** | MoE (16 个 experts，其中 2 个共享) |
| **上下文长度** | 64K / 128K |
| **训练方式** | FP8 训练 + 强化学习 |

---

## 2. MoE 通信流水线

### 2.1 核心概念

在 MoE 架构中，每个 **MoE Block** 包含四个步骤：

1. **ATTENTION** — 计算 token 的 self-attention
2. **DISPATCH** — all-to-all 通信，将 token 按 expert 分发
3. **MLP (Experts)** — 各个 GPU 上的 expert 分别计算
4. **COMBINE** — all-to-all 通信，将 expert 输出发回原位置

### 2.2 流水线编排（DualPipe 风格）

通过 **计算-通信 overlap** 隐藏通信延迟：

| 时间片 | Compute Stream (计算) | Comm Stream (通信) |
|:------:|---------------------|-------------------|
| t0 | ATTENTION(m0) | — |
| t1 | ATTENTION(m1) | DISPATCH(m0) |
| t2 | Expert MLP(m0) | DISPATCH(m1) |
| t3 | Expert MLP(m1) | COMBINE(m0) |
| t4 | 下一层计算 | COMBINE(m1) |

**核心思想**：利用不同 CUDA stream 并行执行通信和计算，让 `dispatch/combine` 的 all-to-all 通信隐藏在 `attention/MLP` 的计算时间下。

---

## 3. 4 GPU / 4 Expert 示例

### 硬件拓扑

| GPU | Expert |
|-----|--------|
| GPU1 | Expert1 |
| GPU2 | Expert2 |
| GPU3 | Expert3 |
| GPU4 | Expert4 |

### Top-1 路由示例

假设 GPU1 上有 token 集合 {a, b, c}，router 决策：

| Token | 目标 Expert | 目标 GPU |
|-------|------------|----------|
| a | Expert3 | GPU3 |
| b | Expert1 | GPU1 (本地) |
| c | Expert4 | GPU4 |

**DISPATCH 阶段**：
- GPU1 打包：发 GPU3 → a，发 GPU4 → c，留在 GPU1 → b

**COMBINE 阶段**：
- 各 GPU 收到的 expert 输出通过 all-to-all 传回原 token 所在 GPU，恢复顺序

---

## 4. 关键技术点

### 4.1 计算-通信 Overlap

- 使用 **独立 CUDA Stream** 分别跑通信 kernel 和计算 kernel
- 动态调配 **SM 资源** 给通信 vs 计算
- 目标：让 wall-time 接近 `max(计算, 通信)` 而非 `计算 + 通信`

### 4.2 DualPipe 双向流水线

- **Forward/Backward 交错**：将 chunk 拆成 4 段（ATTN / DISPATCH / MLP / COMBINE）
- **Backward 再拆分**：对输入的反传 + 对权重的反传
- 让 all-to-all 和 PP 通信都被计算隐藏

---

## 5. 与 DeepSeek-R1 的关系

| 模型 | 架构 | 定位 |
|------|------|------|
| **DeepSeek-V3** | MoE (671B) | 基座模型 |
| **DeepSeek-R1** | Dense / MoE | 推理模型 |

R1 通常基于 V3 做后训练（SFT + RL）得到。

---

## 6. 参考资料

- [DeepSeek-V3 Technical Report](https://github.com/deepseek-ai/DeepSeek-V3)
- [DeepSeek-V3 HuggingFace](https://huggingface.co/deepseek-ai/DeepSeek-V3)
- [DualPipe Paper](https://arxiv.org/abs/2405.00916)
