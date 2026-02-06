---
aliases: [Swish Gated Linear Unit, SwiGLU激活]
tags: [activation, glu, ffn, llama, efficiency]
---

# SwiGLU

## 一句话

在 Feed Forward Network (FFN) 中使用 Swish 激活函数的门控线性单元 (GLU)，通过门控机制选择性激活信息，提高模型表达能力并减少参数。

## 严格定义

对输入 $x \in \mathbb{R}^d$，SwiGLU 定义为：

$$
\text{SwiGLU}(x, W, V, b, c) = \text{Swish}(xW + b) \otimes (xV + c)
$$

其中：
- $\text{Swish}(x) = x \cdot \sigma(\beta x)$（通常 $\beta=1$，即 $x \cdot \text{sigmoid}(x)$）
- $\otimes$ 表示逐元素相乘（Hadamard 积）
- $W, V \in \mathbb{R}^{d \times d_{\text{ffn}}}$：两个独立的线性投影
- $b, c \in \mathbb{R}^{d_{\text{ffn}}}$：偏置项（常省略）

### 简化形式（无偏置）

$$
\text{SwiGLU}(x) = \text{Swish}(xW) \otimes (xV)
$$

## 公式拆解

### 完整计算流程

```
输入 x ∈ R^d
   │
   ├─────────────┬─────────────┐
   │             │             │
   ↓             ↓             ↓
  xW            xV           (可选：门控值)
   │             │             
   ↓             ↓             
 Swish(xW)      xV            
   │             │             
   └──────→ ⊗ ←──┘             (逐元素相乘)
          ↓
       输出 ∈ R^{d_ffn}
```

### Swish 激活函数详解

$$
\text{Swish}(x) = x \cdot \sigma(x) = x \cdot \frac{1}{1 + e^{-x}}
$$

**特性**：
- 平滑、非单调
- 在 $x<0$ 时接近 0（类似 ReLU）
- 在 $x>0$ 时接近线性（类似恒等函数）
- 梯度更平滑，训练更稳定

**图示**（数值例子）：
```
x = -2  →  Swish(-2) = -2 × sigmoid(-2) ≈ -2 × 0.12 = -0.24
x =  0  →  Swish(0)  = 0 × sigmoid(0)  = 0 × 0.5  = 0
x =  2  →  Swish(2)  = 2 × sigmoid(2)  ≈ 2 × 0.88 = 1.76
x =  5  →  Swish(5)  = 5 × sigmoid(5)  ≈ 5 × 0.99 = 4.95
```

## 与其他激活函数对比

### 1. GLU（Gated Linear Unit）

$$
\text{GLU}(x) = (xW) \otimes \sigma(xV)
$$

**区别**：
- GLU 对第二个分支用 sigmoid 门控
- SwiGLU 对第一个分支用 Swish，第二个分支是线性的

### 2. GEGLU（Gaussian Error GLU）

$$
\text{GEGLU}(x) = \text{GELU}(xW) \otimes (xV)
$$

**区别**：
- 用 GELU 代替 Swish
- GELU = $x \cdot \Phi(x)$（$\Phi$ 是标准正态分布的 CDF）

### 3. ReLU（传统 FFN）

$$
\text{FFN}(x) = \text{ReLU}(xW_1) W_2
$$

**区别**：
- ReLU 是单分支，SwiGLU 是双分支（门控）
- ReLU 在 0 处不可导，Swish 平滑

### 对比表

| 激活函数 | 公式 | 平滑性 | 门控 | 性能 |
|---------|------|--------|------|------|
| ReLU | $\max(0, x)$ | ❌ 不平滑 | ❌ 无 | 中等 |
| GELU | $x \cdot \Phi(x)$ | ✅ 平滑 | ❌ 无 | 好 |
| GLU | $(xW) \otimes \sigma(xV)$ | ✅ 平滑 | ✅ 有 | 好 |
| GEGLU | $\text{GELU}(xW) \otimes (xV)$ | ✅ 平滑 | ✅ 有 | 很好 |
| **SwiGLU** | $\text{Swish}(xW) \otimes (xV)$ | ✅ 平滑 | ✅ 有 | **最好** |

## 在 Transformer FFN 中的应用

### 传统 FFN（使用 ReLU）

```python
class FFN(nn.Module):
    def __init__(self, d_model, d_ffn):
        self.w1 = nn.Linear(d_model, d_ffn)
        self.w2 = nn.Linear(d_ffn, d_model)
    
    def forward(self, x):
        return self.w2(F.relu(self.w1(x)))
```

**维度变化**：
```
x: [batch, seq_len, d_model]
   ↓ w1
[batch, seq_len, d_ffn]  (通常 d_ffn = 4 × d_model)
   ↓ ReLU
[batch, seq_len, d_ffn]
   ↓ w2
[batch, seq_len, d_model]
```

### 使用 SwiGLU 的 FFN

```python
class SwiGLU_FFN(nn.Module):
    def __init__(self, d_model, d_ffn):
        # 注意：需要两个投影矩阵 W 和 V
        self.w = nn.Linear(d_model, d_ffn, bias=False)  # 用于 Swish
        self.v = nn.Linear(d_model, d_ffn, bias=False)  # 用于门控
        self.w2 = nn.Linear(d_ffn, d_model, bias=False)
    
    def forward(self, x):
        # SwiGLU(x) = Swish(xW) ⊗ (xV)
        swish_output = F.silu(self.w(x))  # silu = Swish
        gate_output = self.v(x)
        return self.w2(swish_output * gate_output)
```

**维度变化**：
```
x: [batch, seq_len, d_model]
   ├─ w ──→ Swish ──┐
   │                 ↓ (元素相乘)
   └─ v ────────────→ [batch, seq_len, d_ffn]
                      ↓ w2
                   [batch, seq_len, d_model]
```

### LLaMA 的实现（带参数优化）

```python
class LLaMA_FFN(nn.Module):
    def __init__(self, d_model, hidden_dim):
        # LLaMA 使用 2/3 × 4d 作为隐藏层维度
        self.hidden_dim = int(2 * hidden_dim / 3)
        self.hidden_dim = find_multiple(self.hidden_dim, 256)  # 对齐到256
        
        self.w1 = nn.Linear(d_model, self.hidden_dim, bias=False)  # gate
        self.w2 = nn.Linear(self.hidden_dim, d_model, bias=False)  # down_proj
        self.w3 = nn.Linear(d_model, self.hidden_dim, bias=False)  # up_proj
    
    def forward(self, x):
        # SwiGLU: Swish(w1(x)) ⊗ w3(x)
        return self.w2(F.silu(self.w1(x)) * self.w3(x))
```

## 为什么 SwiGLU 好？

### 优势 1：门控机制增强表达能力

**传统 FFN（单分支）**：
```
x → 线性 → 激活 → 线性 → 输出
    (强制所有信息通过同一个激活函数)
```

**SwiGLU（双分支门控）**：
```
x → 分支1: Swish 激活（非线性变换）
  → 分支2: 线性（保留原始信息）
  → 逐元素相乘（门控：分支2 决定保留分支1 的多少）
```

**类比理解**：
- 分支1（Swish）：提取特征
- 分支2（线性）：决定哪些特征重要（门控）
- 相乘：选择性保留信息

### 优势 2：平滑梯度，训练更稳定

**ReLU 的问题**：
```
ReLU(x) = max(0, x)
梯度: ∂ReLU/∂x = {1 if x>0, 0 if x≤0}  ← 不连续！
```

**Swish 的优势**：
```
Swish(x) = x · sigmoid(x)
梯度: ∂Swish/∂x = sigmoid(x) + x·sigmoid(x)·(1-sigmoid(x))  ← 平滑！
```

**影响**：
- 梯度平滑 → 训练更稳定
- 没有"死亡 ReLU"问题（ReLU 在 x<0 时梯度为 0）

### 优势 3：性能更好（实验验证）

**GLU 变体性能对比**（在 WMT 翻译任务上）：

| 激活函数 | BLEU 分数 | 相对提升 |
|---------|-----------|---------|
| ReLU | 25.8 | 基准 |
| GELU | 26.4 | +0.6 |
| GLU | 26.9 | +1.1 |
| GEGLU | 27.2 | +1.4 |
| **SwiGLU** | **27.5** | **+1.7** ✅ |

**在 LLM 上的应用**：
- **LLaMA**: 使用 SwiGLU，性能优于 GPT-3（同等参数量）
- **PaLM**: 使用 SwiGLU，540B 参数达到 SOTA
- **Mistral**: 7B 模型超越 13B Llama2

### 优势 4：参数效率（需要调整维度）

**问题**：SwiGLU 需要两个投影矩阵（W 和 V），参数量增加

**解决方案**：减少隐藏层维度

```
传统 FFN:
  d_model → 4d_model (W1) → d_model (W2)
  参数量: d × 4d + 4d × d = 8d²

SwiGLU FFN (LLaMA 方案):
  d_model → 2.67d_model (W, V) → d_model (W2)
  参数量: d × 2.67d × 2 + 2.67d × d ≈ 8d²
  
保持参数量相同，但性能更好！
```

## 数值示例

### 输入
```
x = [1.0, -0.5, 2.0]  (d_model = 3)
```

### 传统 FFN（ReLU）
```
xW1 = [0.8, -0.3, 1.5, 0.2]  (假设线性投影结果)
ReLU(xW1) = [0.8, 0, 1.5, 0.2]  ← 负值被裁剪！
输出W2 = ...
```

### SwiGLU FFN
```
xW = [0.8, -0.3, 1.5, 0.2]  (分支1)
Swish(xW) = [0.71, -0.11, 1.48, 0.19]  ← 负值保留！

xV = [0.5, 0.8, -0.2, 1.0]  (分支2，门控值)

Swish(xW) ⊗ xV = [0.36, -0.09, -0.30, 0.19]  ← 门控选择
输出W2 = ...
```

**关键差异**：
- ReLU 直接裁剪负值（信息丢失）
- SwiGLU 通过门控选择性保留（信息保留）

## 实现细节

### PyTorch 实现

```python
import torch
import torch.nn as nn
import torch.nn.functional as F

class SwiGLU(nn.Module):
    """
    SwiGLU: Swish Gated Linear Unit
    
    Args:
        d_model: 输入/输出维度
        d_ffn: 隐藏层维度（通常是 d_model 的 2.67-4 倍）
        bias: 是否使用偏置（LLaMA 不用）
    """
    def __init__(self, d_model, d_ffn, bias=False):
        super().__init__()
        self.w = nn.Linear(d_model, d_ffn, bias=bias)   # Swish 分支
        self.v = nn.Linear(d_model, d_ffn, bias=bias)   # 门控分支
        self.w2 = nn.Linear(d_ffn, d_model, bias=bias)  # 输出投影
    
    def forward(self, x):
        """
        Args:
            x: [batch_size, seq_len, d_model]
        Returns:
            output: [batch_size, seq_len, d_model]
        """
        # SwiGLU(x) = Swish(xW) ⊗ (xV)
        swish_out = F.silu(self.w(x))  # silu 就是 Swish
        gate_out = self.v(x)
        hidden = swish_out * gate_out   # 逐元素相乘
        return self.w2(hidden)
```

### 使用示例

```python
# 创建 SwiGLU 层
d_model = 512
d_ffn = 2048  # 通常是 d_model 的 4 倍，或 LLaMA 的 2.67 倍
ffn = SwiGLU(d_model, d_ffn)

# 输入
x = torch.randn(2, 10, d_model)  # [batch=2, seq_len=10, d_model=512]

# 前向传播
output = ffn(x)  # [2, 10, 512]

print(f"输入形状: {x.shape}")
print(f"输出形状: {output.shape}")
```

## 常见变体

### 1. GeGLU（GPT-3、T5）

```python
def geglu(x, w, v):
    return F.gelu(x @ w) * (x @ v)
```

### 2. ReGLU（更简单）

```python
def reglu(x, w, v):
    return F.relu(x @ w) * (x @ v)
```

### 3. SwiGLU（LLaMA、PaLM 首选）

```python
def swiglu(x, w, v):
    return F.silu(x @ w) * (x @ v)  # silu = Swish
```

## 何时使用 SwiGLU？

| 场景 | 推荐 | 原因 |
|------|------|------|
| **训练大型 LLM** | **SwiGLU** ✅ | 性能最好，已在 LLaMA/PaLM 验证 |
| **追求最佳性能** | **SwiGLU** ✅ | 实验表明优于 GELU/ReLU |
| **计算资源有限** | GeGLU 或 ReLU | SwiGLU 需要两个投影矩阵 |
| **小模型/快速原型** | ReLU | 简单高效 |
| **BERT 类模型** | GELU | 已成标配 |

## 关键要点总结

1. **双分支门控**：一个分支激活，一个分支门控
2. **Swish 激活**：平滑、非单调、梯度友好
3. **性能优异**：在多个任务上优于 GELU、ReLU
4. **现代 LLM 标配**：LLaMA、PaLM、Mistral 都用
5. **参数调整**：需要减少隐藏层维度以保持参数量

## 关系

- **上级**: [[Feed Forward Network]] → [[Transformer]] 中的核心组件
- **对比**: [[ReLU]], [[GELU]], [[GLU]] —— 其他激活函数
- **组合**: 常与 [[RMSNorm]] 搭配使用（现代 LLM 架构）
- **应用**: [[LLaMA]], [[PaLM]], [[Mistral]], [[Falcon]] 等模型

## 参考文献

- [GLU Variants Improve Transformer](https://arxiv.org/abs/2002.05202) - 原始论文
- [LLaMA: Open and Efficient Foundation Language Models](https://arxiv.org/abs/2302.13971)
- [PaLM: Scaling Language Modeling with Pathways](https://arxiv.org/abs/2204.02311)

## 挂载路径

[[深度学习]] → [[神经网络组件]] → [[激活函数]] → [[SwiGLU]]
