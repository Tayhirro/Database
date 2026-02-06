# Transformer 核心机制 (Mechanics)

本目录包含 Transformer 架构中的核心机制和组件。

## 目录结构

```
mechanics/
├── normalization/           # 归一化机制
│   ├── LayerNorm.md        # 层归一化
│   ├── RMSNorm.md          # 均方根归一化（LLaMA 使用）
│   └── Norm对比总结.md      # LayerNorm vs RMSNorm 详细对比
│
├── position-encoding/       # 位置编码
│   ├── 正弦位置编码.md      # 原始 Transformer 位置编码
│   └── RoPE.md             # 旋转位置编码（现代 LLM 使用）
│
├── activation/             # 激活函数
│   └── SwiGLU.md           # Swish 门控线性单元（LLaMA 使用）
│
└── components/             # 其他组件
    └── Bias.md             # 偏置项讨论
```

---

## 快速导航

### 归一化 (Normalization)

**问题**：为什么需要归一化？
- 稳定训练
- 加速收敛
- 减少梯度爆炸/消失

**常用方法**：
| 方法 | 使用场景 | 特点 |
|------|----------|------|
| [LayerNorm](normalization/LayerNorm.md) | BERT、原始 Transformer | 减均值 + 除标准差 |
| [RMSNorm](normalization/RMSNorm.md) | LLaMA、Mistral、GPT-3 | 只除 RMS，省 40% 计算 |

**推荐阅读顺序**：
1. [LayerNorm.md](normalization/LayerNorm.md) - 理解基本原理
2. [RMSNorm.md](normalization/RMSNorm.md) - 了解优化版本
3. [Norm对比总结.md](normalization/Norm对比总结.md) - 快速对比

---

### 位置编码 (Position Encoding)

**问题**：Self-Attention 无序，如何让模型知道词的位置？

**常用方法**：
| 方法 | 使用场景 | 特点 |
|------|----------|------|
| [正弦位置编码](position-encoding/正弦位置编码.md) | 原始 Transformer、BERT | 固定编码，不可学习 |
| [RoPE](position-encoding/RoPE.md) | LLaMA、GPT-NeoX、Qwen | 旋转编码，支持长文本 |

**核心区别**：
- 正弦编码：加到 Embedding 上
- RoPE：旋转 Q、K 向量

---

### 激活函数 (Activation)

**问题**：如何引入非线性？

**常用方法**：
| 方法 | 使用场景 | 特点 |
|------|----------|------|
| ReLU | 早期 Transformer | 简单，但梯度不平滑 |
| GELU | BERT、GPT-2 | 平滑，性能好 |
| [SwiGLU](activation/SwiGLU.md) | LLaMA、PaLM、Mistral | 门控机制，性能最好 |

**推荐**：现代 LLM 使用 **SwiGLU**

---

### 其他组件 (Components)

- [Bias](components/Bias.md) - 偏置项的作用和取舍

---

## 现代 LLM 标配组合

### LLaMA / Mistral / Qwen 架构

```
输入
  ↓
RMSNorm           ← 归一化（省计算）
  ↓
Multi-Head Attention (RoPE)  ← 位置编码
  ↓
残差连接
  ↓
RMSNorm
  ↓
SwiGLU FFN        ← 激活函数（门控）
  ↓
残差连接
  ↓
输出
```

**关键组件**：
1. **RMSNorm** - 省 40% 计算，保持向量方向
2. **RoPE** - 支持长文本，相对位置编码
3. **SwiGLU** - 门控激活，性能最好

---

## 学习路径

### 初学者路线
1. **归一化**：[LayerNorm](normalization/LayerNorm.md) → 理解基本概念
2. **位置编码**：[正弦位置编码](position-encoding/正弦位置编码.md) → 了解位置信息
3. **激活函数**：先了解 ReLU/GELU，再看 [SwiGLU](activation/SwiGLU.md)

### 进阶路线（现代 LLM）
1. **RMSNorm vs LayerNorm**：[Norm对比总结.md](normalization/Norm对比总结.md)
2. **RoPE 原理**：[RoPE.md](position-encoding/RoPE.md)
3. **SwiGLU 优势**：[SwiGLU.md](activation/SwiGLU.md)

---

## 对比总结

### LayerNorm vs RMSNorm

| 特性 | LayerNorm | RMSNorm |
|------|-----------|---------|
| 减均值 | ✓ | ✗ |
| 参数量 | 2d | d |
| 计算量 | 100% | 60% |
| 向量方向 | 改变 | 保持 |
| 使用场景 | BERT | LLaMA |

**结论**：现代 LLM 用 RMSNorm

---

### 正弦位置编码 vs RoPE

| 特性 | 正弦编码 | RoPE |
|------|----------|------|
| 位置表示 | 绝对位置 | 相对位置 |
| 长文本 | 外推性差 | 外推性好 |
| 实现方式 | 加到 Embedding | 旋转 Q、K |
| 使用场景 | BERT | LLaMA |

**结论**：现代 LLM 用 RoPE

---

### 激活函数对比

| 激活函数 | 平滑性 | 门控 | 性能 | 使用场景 |
|---------|--------|------|------|---------|
| ReLU | ✗ | ✗ | 中等 | 早期 Transformer |
| GELU | ✓ | ✗ | 好 | BERT、GPT-2 |
| SwiGLU | ✓ | ✓ | 最好 | LLaMA、PaLM |

**结论**：现代 LLM 用 SwiGLU

---

## 核心记忆卡片

```
┌─────────────────────────────────────────┐
│ 现代 LLM 三大核心优化                    │
│                                         │
│ 1. RMSNorm  → 省 40% 计算，保持方向     │
│ 2. RoPE     → 相对位置，支持长文本       │
│ 3. SwiGLU   → 门控激活，性能最好         │
│                                         │
│ 代表模型: LLaMA, Mistral, Qwen         │
└─────────────────────────────────────────┘
```

---

**最后更新**: 2024-02-06
