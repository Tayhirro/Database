# 注意力（Attention）

## 1. 一句话
- 用相似度在一组向量上做加权汇聚，是 Transformer 的核心算子。

## 2. Scaled Dot-Product Attention
给定 `Q,K,V`：
- `Attention(Q,K,V) = softmax(QK^T / sqrt(d_k)) V`

## 3. 常见形态
- Self-Attention：`Q=K=V`
- Cross-Attention：`Q` 来自一侧，`K,V` 来自另一侧
- Multi-Head：多组线性投影并行，最后拼接

相关页：
- BERT：`models/BERT.md`

