# 后训练（Post-training）

导航：[training/README.md](../README.md)

预训练之后的所有训练阶段。

---

## 定义

Post-training：在预训练完成后，通过额外训练使模型获得特定能力（指令遵循、对齐、推理、Agent能力）的阶段。

---

## 子目录

| 目录 | 说明 | 典型方法 |
|------|------|----------|
| [sft/](sft/) | 监督微调 | SFT, LoRA, QLoRA |
| [alignment/](alignment/) | 人类偏好对齐 | RLHF, DPO, PPO |
| [reasoning/](reasoning/) | 推理能力增强 | RL for Reasoning, PRM |
| [agentic/](agentic/) | Agent能力训练 | Search-R1, Toolformer |

---

## 阶段关系

```
Base Model (from Pretraining)
      ↓
    SFT        → Instruct Model
      ↓
  Alignment    → Aligned Model
      ↓
  Reasoning    → Reasoning Model (可选)
      ↓
  Agentic      → Agent Model (可选)
```

---

## 关系

- 上级：[Training](../README.md)
- 前置：[Pretraining](../pretraining/)
