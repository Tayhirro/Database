# 架构（Architecture）

导航：[llm/README.md](../README.md) | [索引.md](索引.md)

本目录包含 LLM 的模型架构设计。

---

## 子目录

| 目录 | 说明 |
|------|------|
| [Transformer/](Transformer/Transformer.md) | Transformer 架构及其组件 |
| [SSM/](SSM/SSM.md) | 状态空间模型（Mamba、RWKV 等） |

---

## 条目列表

### Transformer 家族
- [Transformer](Transformer/Transformer.md)
  - structure/：[SelfAttention](Transformer/structure/SelfAttention.md)、[CrossAttention](Transformer/structure/CrossAttention.md)、[FFN](Transformer/structure/FFN.md)、[ResidualConnection](Transformer/structure/ResidualConnection.md)
  - mechanics/：[RoPE](Transformer/mechanics/RoPE.md)、[ALiBi](Transformer/mechanics/ALiBi.md)、[RMSNorm](Transformer/mechanics/RMSNorm.md)、[PreNorm](Transformer/mechanics/PreNorm.md)、[QKNorm](Transformer/mechanics/QKNorm.md)
  - variants/：[GQA](Transformer/variants/GQA.md)、[MQA](Transformer/variants/MQA.md)、[MoE](Transformer/variants/MoE.md)、[SwiGLU](Transformer/variants/SwiGLU.md)

### 状态空间模型
- [SSM](SSM/SSM.md)
- [Mamba](SSM/Mamba.md)
- [RWKV](SSM/RWKV.md)
