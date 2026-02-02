# 自监督学习（Self-Supervised Learning）

导航：[paradigms/README.md](README.md)

---

## 一句话

从数据自身结构中构造监督信号的学习范式。

---

## 严格定义

自监督学习 (SSL)：通过设计预测任务（如预测被遮蔽部分、预测下一 token），从无标注数据中学习表示。

---

## 在 LLM 中的应用

| 任务 | 输入 | 预测目标 | 代表模型 |
|------|------|----------|----------|
| Next Token Prediction | $x_{<t}$ | $x_t$ | GPT 系列 |
| Masked LM | $x$ with [MASK] | 被遮蔽 token | BERT |
| Span Corruption | $x$ with spans | 被遮蔽 span | T5 |

---

## 关系

- 上级：[Paradigms](README.md)
- 应用于：[Pretraining](../pretraining/)
