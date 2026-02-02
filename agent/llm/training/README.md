# 训练（Training）

导航：[llm/README.md](../README.md) | [索引.md](索引.md)

LLM 的训练方法、流程与优化技术。

---

## 训练流程总览

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        LLM Training Pipeline                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Stage 1: Pretraining                                                   │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  大规模无标注语料 → Next Token Prediction → Base Model          │   │
│  │  范式：Self-Supervised                                          │   │
│  │  产出：GPT、LLaMA-base                                          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↓                                          │
│  Stage 2: Post-training                                                 │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                                                                  │   │
│  │  2.1 SFT（监督微调）                                             │   │
│  │      指令数据 → Supervised Learning → Instruct Model            │   │
│  │      产出：Alpaca、Vicuna                                        │   │
│  │                              ↓                                   │   │
│  │  2.2 Alignment（对齐）                                           │   │
│  │      人类偏好 → RLHF / DPO → Aligned Model                      │   │
│  │      产出：ChatGPT、Claude                                       │   │
│  │                              ↓                                   │   │
│  │  2.3 Reasoning（推理增强）[可选]                                  │   │
│  │      推理数据/RL → Reasoning Model                              │   │
│  │      产出：o1、DeepSeek-R1                                       │   │
│  │                              ↓                                   │   │
│  │  2.4 Agentic（Agent能力）[可选]                                   │   │
│  │      工具交互/环境反馈 → Agent-capable Model                     │   │
│  │      产出：Toolformer、Search-R1                                 │   │
│  │                                                                  │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                              ↓                                          │
│  Production Model                                                       │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 两个维度

### 维度1：训练阶段（Stage）

按时间顺序，模型经过的训练阶段。

| 阶段 | 目标 | 数据 | 产出 |
|------|------|------|------|
| Pretraining | 语言建模能力 | 大规模无标注文本 | Base Model |
| SFT | 指令遵循能力 | 指令-回复对 | Instruct Model |
| Alignment | 人类偏好对齐 | 偏好数据 | Aligned Model |
| Reasoning | 推理能力 | 推理轨迹/RL | Reasoning Model |
| Agentic | Agent能力 | 工具交互/环境 | Agent Model |

### 维度2：训练范式（Paradigm）

训练所使用的学习方法。

| 范式 | 信号来源 | 适用阶段 |
|------|----------|----------|
| Self-Supervised | 数据自身结构 | Pretraining |
| Supervised | 标注数据 | SFT |
| Reinforcement Learning | 环境/奖励信号 | Alignment, Reasoning, Agentic |
| Preference Optimization | 偏好对比 | Alignment |

---

## 目录结构

| 目录 | 说明 |
|------|------|
| [pretraining/](pretraining/) | 预训练方法 |
| [post-training/](post-training/) | 后训练（SFT、对齐、推理、Agentic） |
| [paradigms/](paradigms/) | 训练范式（SL、RL、PO） |
| [optimization/](optimization/) | 优化器与训练技巧 |

---

## 常见训练流程变体

| 变体 | 流程 | 例子 |
|------|------|------|
| 标准流程 | Pretrain → SFT → RLHF | ChatGPT |
| DPO 替代 | Pretrain → SFT → DPO | Zephyr |
| 纯 SFT | Pretrain → SFT | Alpaca |
| 推理增强 | Pretrain → SFT → RL (Reasoning) | DeepSeek-R1 |
| Agent 训练 | Pretrain → SFT → RL (Tool) | Search-R1 |
| 持续预训练 | Pretrain → Continue Pretrain → SFT | Domain LLM |

---

## 关系

- 上级：[LLM](../README.md)
- 下游：[Inference](../inference/README.md)、[Evaluation](../evaluation/README.md)
