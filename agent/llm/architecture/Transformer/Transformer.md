# Transformer

基于自注意力机制的序列到序列建模架构。

---

## 严格定义

Transformer 是一种神经网络架构，由堆叠的层组成，每层包含：
1. 多头自注意力子层（Multi-Head Self-Attention）
2. 位置前馈网络子层（Position-wise Feed-Forward Network）
3. 残差连接与归一化

原始架构分为 Encoder 和 Decoder 两部分；现代 LLM 多采用 Decoder-only 架构。

---

## 接口

**输入**：
- $X \in \mathbb{R}^{n \times d}$：序列长度 $n$，嵌入维度 $d$

**输出**：
- $Y \in \mathbb{R}^{n \times d}$：同维度的变换表示

**参数**：
- $L$：层数
- $d$：模型维度
- $h$：注意力头数
- $d_{ff}$：FFN 中间维度

---

## 常用构造

| 变体 | 说明 |
|------|------|
| Encoder-only | 双向注意力（BERT） |
| Decoder-only | 因果掩码、自回归（GPT） |
| Encoder-Decoder | 编码 + 交叉注意力解码（T5） |

---

## 关系

- 上级：神经网络架构
- 组件：[SelfAttention](structure/SelfAttention.md)、[FFN](structure/FFN.md)、[ResidualConnection](structure/ResidualConnection.md)
- 机制：[RoPE](mechanics/RoPE.md)、[RMSNorm](mechanics/RMSNorm.md)、[PreNorm](mechanics/PreNorm.md)
- 变体：[GQA](variants/GQA.md)、[MoE](variants/MoE.md)
- 替代架构：[SSM](../SSM/SSM.md)
