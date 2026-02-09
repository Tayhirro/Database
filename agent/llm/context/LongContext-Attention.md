# 长上下文注意力优化

导航：[context/README.md](../../README.md) | [SelfAttention](../structure/SelfAttention.md)

**核心问题**：标准自注意力复杂度为O(L²)，当序列长度L达到100k+时，计算量和显存消耗爆炸。如何在**不重新训练**的情况下处理超长序列？

---

## 核心挑战：O(L²)复杂度瓶颈

### 计算量爆炸

| 序列长度 | 注意力计算量 | 显存占用（batch=1） |
|---------|-------------|-------------------|
| 2k | 4M | ~16MB |
| 8k | 64M | ~256MB |
| 32k | 1B | ~4GB |
| 128k | 16B | ~64GB |
| 1M | 1T | ~4TB |

**症状**：
1. 显存溢出（OOM）
2. 计算速度极慢
3. 无法处理整篇文档、长视频、长对话

---

## 解决方案：分块注意力（Chunked Attention）

### 核心思想

**将长序列切分为块（chunk），在块内和块间分别处理**

```
原始序列：[t1][t2][t3][t4][t5][t6][t7][t8]...
分块后：  |  Chunk 0   |  Chunk 1   |  Chunk 2   |
          [t1-t4]      [t5-t8]      [t9-t12]...
```

### 两类注意力

| 类型 | 范围 | 目的 | 计算方式 |
|------|------|------|---------|
| **Intra-chunk**（块内） | 每个chunk内部 | 捕获局部细节 | 标准注意力 |
| **Inter-chunk**（块间） | chunk之间 | 建立全局联系 | 压缩/稀疏机制 |

---

## 具体方法：DCA（Dual Chunk Attention）

### 核心机制

**训练无关（training-free）**：直接修改推理代码，无需继续训练模型

**双流注意力**：

1. **局部流（Local Stream）**：
   - 每个token只关注所在chunk内的其他token
   - 使用标准滑动窗口或固定chunk大小
   - O(L×C)，C为chunk大小

2. **全局流（Global Stream）**：
   - 每个chunk提取代表性信息（如mean/max pooling）
   - token可以关注其他chunk的代表性信息
   - 建立跨chunk长距离依赖

### 信息融合

```
对于位置i的token：
- 局部上下文：与同一chunk内token的注意力
- 全局上下文：与其他chunk代表性向量的注意力
- 最终表示：局部 + 全局的融合
```

### 复杂度分析

| 方法 | 时间复杂度 | 空间复杂度 | 有效上下文 |
|------|-----------|-----------|-----------|
| 标准注意力 | O(L²) | O(L²) | L |
| DCA | O(L×C + L×N) | O(L×C + N²) | L |

其中：
- C：chunk大小（如1k）
- N：chunk数量（如L/C = 100）

当L=100k，C=1k时：
- 标准：O(10B)
- DCA：O(100M)，**降低100倍**

---

## 其他分块注意力方法

### 1. StreamingLLM

**核心**：保留初始token（sink tokens）+ 滑动窗口
- 初始几个token作为注意力"汇聚点"
- 近期token用滑动窗口保持局部性
- 中间token被压缩/丢弃

**特点**：
- 极简实现，几乎无损
- 适合流式生成场景

### 2. LongLoRA

**核心**：稀疏注意力 + 短训微调
- 训练时只计算局部注意力（节省显存）
- 推理时可扩展为更长上下文
- 需要少量微调训练

### 3. Ring Attention

**核心**：分布式块间通信
- 多块GPU协同，每块处理一个chunk
- 通过环形通信聚合全局信息
- 支持百万级token

---

## 方法对比

| 方法 | 是否需要训练 | 核心思想 | 适用场景 |
|------|-------------|---------|---------|
| **DCA** | 否 | 块内+块间双流 | 100k+推理加速 |
| **StreamingLLM** | 否 | 关键token保留 | 流式生成 |
| **LongLoRA** | 是（少量） | 稀疏注意力 | 资源有限训练 |
| **Ring Attention** | 否 | 分布式块处理 | 超大规模（1M+） |

---

## 实现要点

### DCA伪代码

```python
def dca_attention(q, k, v, chunk_size=1024):
    """
    q, k, v: [batch, seq_len, dim]
    """
    seq_len = q.shape[1]
    num_chunks = (seq_len + chunk_size - 1) // chunk_size
    
    # 分块
    q_chunks = q.chunk(num_chunks, dim=1)
    k_chunks = k.chunk(num_chunks, dim=1)
    v_chunks = v.chunk(num_chunks, dim=1)
    
    outputs = []
    
    for i, q_i in enumerate(q_chunks):
        # 1. Intra-chunk: 当前chunk内注意力
        k_i, v_i = k_chunks[i], v_chunks[i]
        local_out = standard_attention(q_i, k_i, v_i)
        
        # 2. Inter-chunk: 跨chunk全局信息
        # 提取其他chunk的representative keys
        global_k = [k_j.mean(dim=1, keepdim=True) for j in range(num_chunks) if j != i]
        global_v = [v_j.mean(dim=1, keepdim=True) for j in range(num_chunks) if j != i]
        global_out = standard_attention(q_i, torch.cat(global_k, dim=1), torch.cat(global_v, dim=1))
        
        # 3. 融合局部和全局
        out = combine(local_out, global_out)
        outputs.append(out)
    
    return torch.cat(outputs, dim=1)
```

---

## 与RoPE扩展的配合

**组合使用效果最佳**：

1. **RoPE扩展**（ABF/YaRN）：解决位置编码外推问题，让模型"看得懂"长距离位置
2. **DCA**：解决注意力计算复杂度问题，让模型"算得起"长距离交互

```
长上下文建模 = RoPE扩展（外推能力） + DCA（计算效率）
```

---

## 参考文献

- DCA: arXiv:2402.17463
- StreamingLLM: arXiv:2309.17453
- LongLoRA: arXiv:2309.12307
- Ring Attention: arXiv:2310.01889
