# 训练（Training）

导航：[llm/README.md](../README.md) | [索引.md](索引.md)

本目录包含 LLM 的训练方法与优化技术。

---

## 子目录

| 目录 | 说明 |
|------|------|
| [pretraining/](pretraining/) | 预训练目标与方法 |
| [finetuning/](finetuning/) | 微调技术 |
| [optimization/](optimization/) | 优化器与训练技巧 |

---

## 条目列表

### 预训练
- [Pretraining](pretraining/Pretraining.md)
- [NextTokenPrediction](pretraining/NextTokenPrediction.md)
- [MaskedLM](pretraining/MaskedLM.md)

### 微调
- [SFT](finetuning/SFT.md)（Supervised Fine-Tuning）
- [LoRA](finetuning/LoRA.md)
- [QLoRA](finetuning/QLoRA.md)
- [AdapterTuning](finetuning/AdapterTuning.md)

### 优化
- [AdamW](optimization/AdamW.md)
- [LearningRateSchedule](optimization/LearningRateSchedule.md)
- [GradientCheckpointing](optimization/GradientCheckpointing.md)
- [MixedPrecision](optimization/MixedPrecision.md)
