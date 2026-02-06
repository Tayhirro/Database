# LLM 知识库

导航：[agent/README.md](../README.md) | [索引.md](索引.md) | [概念图.md](概念图.md)

本目录系统组织大语言模型（LLM）相关知识。

---

## 目录结构

| 分支 | 说明 | 入口 |
|------|------|------|
| architecture/ | 模型架构（Transformer、SSM） | [README](architecture/README.md) |
| training/ | 训练方法（预训练、微调、优化） | [README](training/README.md) |
| inference/ | 推理技术（解码、加速、量化） | [README](inference/README.md) |
| tokenization/ | 分词方法 | [README](tokenization/README.md) |
| alignment/ | 对齐技术（RLHF、DPO） | [README](alignment/README.md) |
| prompting/ | 提示工程 | [README](prompting/README.md) |
| scaling/ | 扩展规律 | [README](scaling/README.md) |
| context/ | 长上下文 | [README](context/README.md) |
| evaluation/ | 评估基准 | [README](evaluation/README.md) |

---

## 快速导航

### 架构
- [Transformer](architecture/Transformer/Transformer.md) | [SSM](architecture/SSM/SSM.md) | [Mamba](architecture/SSM/Mamba.md)
- 组件：[SelfAttention](architecture/Transformer/structure/SelfAttention.md) | [FFN](architecture/Transformer/structure/FFN.md) | [RoPE](architecture/Transformer/mechanics/RoPE.md)
- 变体：[GQA](architecture/Transformer/variants/GQA.md) | [SwiGLU](architecture/Transformer/variants/SwiGLU.md)
- 层级架构：[MoE](architecture/Transformer/layers/MoE.md)

### 训练
- [Pretraining](training/pretraining/Pretraining.md) | [SFT](training/finetuning/SFT.md) | [LoRA](training/finetuning/LoRA.md)

### 推理
- [KVCache](inference/acceleration/KVCache.md) | [FlashAttention](inference/acceleration/FlashAttention.md) | [Quantization](inference/quantization/Quantization.md)

### 对齐
- [RLHF](alignment/RLHF.md) | [DPO](alignment/DPO.md)

### 提示
- [CoT](prompting/CoT.md) | [ReAct](prompting/ReAct.md)
