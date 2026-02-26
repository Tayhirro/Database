---
title: Transformer 学习笔记
date: "2026-02-06"
categories:
  - agent
description: "整体架构流程 核心模块详解（Self-Attention、Multi-Head、FFN、残差连接） 完整的 Encoder/Decoder 流程 维度变化详解 直观案例理解"
---
# Transformer 学习笔记

## 📚 文档目录

### 1. [Transformer-基础结构.md](./Transformer-基础结构.md) ⭐ 完整版
- 整体架构流程
- 核心模块详解（Self-Attention、Multi-Head、FFN、残差连接）
- 完整的 Encoder/Decoder 流程
- 维度变化详解
- 直观案例理解

**适合**：想全面了解 Transformer 的同学

---

### 2. [Transformer-简化流程.md](./Transformer-简化流程.md) 🚀 简化版
- Self-Attention 5步流程（超详细图解）
- 残差连接流程图
- Multi-Head 流程图
- 关键公式总结
- 快速记忆口诀

**适合**：想快速上手的同学，图解多，易理解

---

### 3. [Transformer-FAQ.md](./Transformer-FAQ.md) ❓ 问题解答
- Q、K、V 是什么？怎么来的？
- 为什么要 Q·K^T？
- 为什么要除以 √dk？
- 残差连接有什么用？
- LayerNorm vs BatchNorm
- Multi-Head 为什么好？
- 计算复杂度
- 位置编码
- Encoder vs Decoder
- Transformer 为什么强？

**适合**：有疑问时查阅

---

## 🎯 学习路径推荐

### 初学者路线
1. 先看 **简化流程.md** - 快速建立框架
2. 看不懂就查 **FAQ.md** - 解决具体问题
3. 最后看 **基础结构.md** - 深入理解细节

### 已有基础路线
1. 直接看 **基础结构.md** - 系统学习
2. 不懂的概念查 **FAQ.md**

---

## 🔑 核心要点速查

### Self-Attention 流程（5步）
```
1. X → Q, K, V（通过矩阵乘法）
2. Q·K^T（计算相关性分数）
3. / √dk（缩放）
4. Softmax（转为概率）
5. × V（加权求和）
```

### 残差连接
```
输出 = LayerNorm(X + Sublayer(X))
```

### 完整 Encoder Layer
```
X → Multi-Head Attention → +X → LayerNorm
  → FFN → +X → LayerNorm → 输出
```

---

## 📖 参考资料

### 原论文
- [Attention Is All You Need](https://arxiv.org/abs/1706.03762) - Vaswani et al., 2017

### 可视化讲解
- [The Illustrated Transformer](https://jalammar.github.io/illustrated-transformer/) - Jay Alammar（英文，强烈推荐）
- [图解 Transformer](https://zhuanlan.zhihu.com/p/338817680) - 知乎（中文）

### 视频讲解
- [李宏毅 - Transformer](https://www.youtube.com/watch?v=ugWDIIOHtPA) - 中文，讲得很清楚
- [3Blue1Brown - Attention](https://www.youtube.com/watch?v=eMlx5fFNoYc) - 英文，数学角度

### 代码实现
- [The Annotated Transformer](http://nlp.seas.harvard.edu/2018/04/03/attention.html) - 哈佛，带详细注释的 PyTorch 实现
- [Hugging Face Transformers](https://github.com/huggingface/transformers) - 生产级实现

---

## 🛠️ 待补充内容

- [ ] Transformer 变种（BERT、GPT、T5）
- [ ] 优化版 Transformer（Flash Attention、Linear Attention）
- [ ] 代码实现示例
- [ ] 训练技巧

---

**最后更新**: 2024-02-06
