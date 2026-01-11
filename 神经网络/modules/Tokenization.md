# Tokenization（分词与子词）

## 1. 一句话
- 把自然语言变成 token 序列，是 BERT/LLM 输入管线的第一步。

## 2. 常见方法（够用版）
- WordPiece（BERT 常见）：用子词词表把 OOV 拆开
- BPE（很多 LLM/CLIP 文本编码器常见）：基于合并规则构造子词

## 3. 训练/推理时的关键点
- 特殊 token：`[CLS]`、`[SEP]`、`[PAD]` 等（以具体模型为准）
- 最大长度、padding、attention mask 的处理要一致

相关页：
- BERT：`models/BERT.md`

