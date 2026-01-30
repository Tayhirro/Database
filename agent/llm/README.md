# LLM 架构（Large Language Model Architecture）

导航：[agent/README.md](../README.md) | 本分支：[索引.md](索引.md) | [概念图.md](概念图.md)

本目录按三条主线组织 LLM 架构知识：
1. **骨架**：Transformer（Encoder / Decoder / Decoder-only）
2. **模块**：Attention、FFN、Normalization、Position Encoding
3. **扩展**：MoE（稀疏化）、Tokenizer

---

## 目录结构

- [索引.md](索引.md)：术语索引（中英 | 一句话 | 链接）
- [概念图.md](概念图.md)：概念关系图（依赖链 / 变体演化）

模块页（modules/）：
- [modules/Transformer.md](modules/Transformer.md)：Transformer 骨架
- [modules/SelfAttention.md](modules/SelfAttention.md)：自注意力机制
- [modules/GQA.md](modules/GQA.md)：Grouped Query Attention
- [modules/RoPE.md](modules/RoPE.md)：Rotary Position Embedding
- [modules/FFN.md](modules/FFN.md)：前馈网络（含 SwiGLU）
- [modules/RMSNorm.md](modules/RMSNorm.md)：RMS 归一化
- [modules/PreNorm.md](modules/PreNorm.md)：Pre-Norm 归一化位置
- [modules/QKNorm.md](modules/QKNorm.md)：QK-Norm
- [modules/MoE.md](modules/MoE.md)：Mixture of Experts

---

## 阅读路线

基础路径：Transformer → Self-Attention → FFN → LayerNorm/RMSNorm

变体演化：
- Attention 变体：MHA → MQA → GQA
- 位置编码变体：Sinusoidal → Learned → RoPE → ALiBi
- FFN 变体：ReLU → GELU → SwiGLU
- 归一化变体：LayerNorm → RMSNorm；Post-Norm → Pre-Norm
- 稀疏化：Dense → MoE
