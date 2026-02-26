---
title: Layers（层级架构）
date: "2026-02-06"
categories:
  - agent
description: "导航：architecture/README.md | Transformer.md"
---
# Layers（层级架构）

导航：[architecture/README.md](../../README.md) | [Transformer.md](../Transformer.md)

本目录包含可以插入Transformer中的**层级架构组件**，这些组件通常替代或增强标准的FFN层或Attention层。

---

## 与其他目录的区别

| 目录 | 含义 | 示例 |
|------|------|------|
| **structure/** | 基础结构组件 | SelfAttention, FFN, ResidualConnection |
| **mechanics/** | 底层机制 | RoPE, RMSNorm, Bias |
| **variants/** | 组件的优化变体 | GQA（Attention变体）, SwiGLU（激活函数） |
| **layers/** | 独立的层级架构 | MoE（专家混合层） |

**区别**：
- `structure/` 是所有Transformer都需要的核心组件
- `mechanics/` 是实现细节和技术机制
- `variants/` 是对某个具体组件的优化版本
- `layers/` 是**独立的、可选的、复杂的**层级架构

---

## 条目列表

### 稀疏计算层

- **[MoE](./MoE.md)**（Mixture of Experts）：稀疏激活的专家混合层
  - **背景**：条件计算面临的五大挑战及MoE的解决方案
  - **架构**：Noisy Top-K门控 + 专家网络设计
  - **关键洞察**：稀疏计算如何克服稀疏查找（如Embedding）的带宽瓶颈
  - **应用**：语言建模、机器翻译等超大规模场景

---

## 层的使用模式

### 标准Transformer层
```
Input
  ↓
LayerNorm → SelfAttention → Residual
  ↓
LayerNorm → FFN → Residual
  ↓
Output
```

### 使用MoE层
```
Input
  ↓
LayerNorm → SelfAttention → Residual
  ↓
LayerNorm → MoE → Residual
  ↓
Output
```

**替代关系**：
- MoE通常**替换FFN层**的位置
- 保持Attention层不变（也有MoE-Attention的研究）
- MoE也可以与标准FFN交替使用

---

## 设计原则

层级架构（layers/）应满足：

1. **独立性**：可以独立作为一个层插入模型
2. **复杂性**：内部包含多个子组件或子网络
3. **可替代性**：通常替代FFN或Attention
4. **非必需性**：不是所有Transformer都必须使用

**反例**：
- GQA不应放在这里，因为它只是Attention的优化，不是独立层
- SwiGLU不应放在这里，因为它只是激活函数的变体

---

## 未来扩展

可能添加的层级架构：

### 1. Switch Transformer
- 简化版MoE，每个token只路由到1个专家
- 更激进的稀疏性

### 2. GShard
- Google的大规模MoE实现
- 针对TPU优化的分片策略

### 3. Expert Choice Routing
- 由专家选择token，而非token选择专家
- 更好的负载均衡

### 4. Retrieval-Augmented Layer
- 结合检索机制的层
- 动态访问外部知识库

### 5. Adapter Layers
- 参数高效的微调层
- 插入到预训练模型中

---

## 相关资源

- 上级目录：[Transformer](../Transformer.md)
- 基础组件：[structure/](../structure/)
- 底层机制：[mechanics/](../mechanics/)
- 优化变体：[variants/](../variants/)
