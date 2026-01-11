# BERT（Bidirectional Encoder Representations from Transformers）

## 1. 一句话
- 用 Transformer Encoder 做双向上下文建模的预训练语言模型，经典目标是 MLM。

## 2. 输入与结构
- Tokenization：`modules/Tokenization.md`
- Encoder 堆叠：Multi-Head Self-Attention + FFN + 残差 + LayerNorm（注意力见 `modules/Attention.md`）

## 3. 预训练目标（经典版）
- MLM（Masked Language Modeling）：随机 mask 一部分 token，让模型预测
- NSP（Next Sentence Prediction）：原论文包含，后续实践常不使用/替换

## 4. 下游用法（速查）
- 句子级任务：用 `[CLS]` 的表示接分类头
- 序列标注：取每个 token 的表示

## 5. 常见坑
- tokenizer/特殊 token/attention mask 不一致导致效果异常
- 微调学习率与 batch size 很敏感

