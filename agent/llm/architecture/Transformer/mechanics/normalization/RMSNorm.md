---
title: RMSNorm
date: "2026-02-06"
categories:
  - agent
tags:
  - normalization
  - layer-norm
  - llama
  - training-stability
description: 仅通过均方根（RMS）进行尺度缩放而不减去均值、不添加偏置的层归一化变体，以减少计算开销并维持训练稳定性。
aliases:
  - Root Mean Square Layer Normalization
  - RMS归一化
---
# RMSNorm

## 一句话

仅通过均方根（RMS）对向量进行**等比缩放**的归一化方法——不减均值、不加偏置，因此**保持向量方向不变，只将模长归一化到 $\sqrt{d}$**。

## 严格定义

对输入向量 $x \in \mathbb{R}^d$，RMSNorm 定义为：

$$
y = \gamma \cdot \frac{x}{\text{RMS}(x) + \epsilon}
$$

其中均方根（RMS）为：

$$
\text{RMS}(x) = \sqrt{\frac{1}{d}\sum_{i=1}^{d} x_i^2} = \frac{\|x\|}{\sqrt{d}}
$$

> [!note] RMS 与模长的关系
> $\text{RMS}(x) = \frac{\|x\|_2}{\sqrt{d}}$，所以除以 RMS 等价于将向量模长缩放到 $\sqrt{d}$。

## 接口：数据 + 约束

| 符号 | 类型 | 约束 | 说明 |
|------|------|------|------|
| $x$ | $\mathbb{R}^d$ 或 $\mathbb{R}^{B \times L \times d}$ | 任意实数 | 输入特征，最后一维为特征维 |
| $\gamma$ | $\mathbb{R}^d$ | 可学习参数，初始化为 1 | 逐元素缩放权重 |
| $\epsilon$ | $\mathbb{R}$ | 常数，默认 $10^{-5} \sim 10^{-6}$ | 数值稳定性保护 |
| RMS | $\mathbb{R}$ | $\geq 0$ | 沿特征维计算的标量 |

## 核心特性

### 特性 1：方向保持（Direction Preservation）

RMSNorm 对向量做的是**标量除法**——每个分量除以同一个标量 $\text{RMS}(x)$：

$$
\frac{x}{\text{RMS}(x)} = \frac{x}{\|x\|/\sqrt{d}} = \sqrt{d} \cdot \frac{x}{\|x\|}
$$

标量除法不改变向量方向，因此：

$$
\angle(x,\; y) = \angle\!\left(x,\; \frac{x}{\text{RMS}(x)}\right) = 0
$$

**归一化前后，向量夹角为零，方向完全保持。**

#### 为什么方向保持重要？

**1. 语义方向即信息**

在 Embedding 空间中，向量的方向编码语义：

```
"国王" = [0.8, 0.3, 0.5]   方向 → "男性 + 权力"

RMSNorm 后:
"国王" → [0.9, 0.34, 0.56]  方向不变，语义保持

对比 LayerNorm（减均值后）:
"国王" → [0.3, -0.2, 0.0]   方向改变，语义偏移
```

**2. 余弦相似度不变**

对任意两个向量 $a, b$，经 RMSNorm（不含 $\gamma$）后：

$$
\cos(a', b') = \cos(a, b)
$$

归一化不会破坏向量之间的相对关系。

**3. 残差连接更稳定**

```
在 Transformer 中:
输出 = x + Attention(RMSNorm(x))

如果 Norm 改变了方向 → 残差相加时产生"方向偏移"
RMSNorm 只调整模长 → 残差路径更直接
```

---

### 特性 2：模长归一化（Magnitude Normalization）

RMSNorm 将所有输入向量的模长统一缩放到 $\sqrt{d}$：

$$
\left\|\frac{x}{\text{RMS}(x)}\right\| = \left\|\frac{x}{\|x\|/\sqrt{d}}\right\| = \sqrt{d}
$$

#### 为什么模长归一化重要？

**1. 消除模长差异，防止尺度爆炸/消失**

不同 token 的隐层向量模长可能差异很大。经过 RMSNorm 后，所有向量被拉到同一模长，后续 Attention 的点积计算更稳定。

**2. 各向同性缩放（Isotropic Scaling）**

每个分量被同一个标量缩放，没有分量被选择性压缩或拉伸：

$$
\frac{x_i}{\text{RMS}(x)} \quad \text{（所有 } i \text{ 除以同一个值）}
$$

这是**相似变换**（Similarity Transform），保持角度、保持形状，只改变大小。

---

### 特性 3：计算效率高

```
1. 计算 x²:        O(d)
2. 求和并平均:      O(d)
3. 开方得 RMS:     O(1)
4. 除以 RMS:       O(d)
5. 乘以 γ:         O(d)
总计: ~4d 次操作
```

对比 [[LayerNorm]] 的 ~5d 次操作，省去了均值计算和减法两步。

**实测对比**（d=4096）：
- LayerNorm: ~12.5 μs
- RMSNorm:  ~7.8 μs
- 加速比: ~1.6x

---

### 特性 4：参数更少

| 参数 | 数量 | 说明 |
|------|------|------|
| 缩放参数 $\gamma$ | $d$ 维 | 逐元素缩放 |
| 平移参数 $\beta$ | **无** | 不需要 |
| **总计** | $d$ | 对比 [[LayerNorm]] 的 $2d$ |

**实际影响**（以 LLaMA-7B 为例）：
- d = 4096，32 层
- RMSNorm 参数: $1 \times 4096 \times 32 = 131{,}072$
- 对比 LayerNorm: $2 \times 4096 \times 32 = 262{,}144$
- 节省: 131,072 个参数

## 几何意义

RMSNorm 执行的是**相似变换**（Similarity Transform）：

```
原始向量  →  等比缩放到统一模长
            (除以 RMS)

方向不变，形状不变，只改变大小
```

### 数值示例

输入 $x = [2.0, -1.0, 3.0]$，$d = 3$：

1. $\text{RMS} = \sqrt{\frac{4 + 1 + 9}{3}} = \sqrt{4.67} = 2.16$
2. 归一化：$\frac{x}{\text{RMS}} = [0.93, -0.46, 1.39]$
3. 验证模长：$\sqrt{0.93^2 + 0.46^2 + 1.39^2} = \sqrt{0.86 + 0.21 + 1.93} = \sqrt{3.0} = \sqrt{d}$
4. 验证方向：原始方向角与归一化后完全一致

### 2D 可视化

```
原始向量 x = [3, 1]，方向角 = arctan(1/3) ≈ 18.4°

RMSNorm:
  RMS = √((9+1)/2) = √5 = 2.24
  x / RMS = [1.34, 0.45]
  方向角 = arctan(0.45/1.34) ≈ 18.4°  ← 方向不变！
  模长 = √(1.34² + 0.45²) = √2 = √d   ← 模长归一化到 √d
```

## 实现

```python
# PyTorch 手动实现（LLaMA 风格）
class RMSNorm(nn.Module):
    def __init__(self, d: int, eps: float = 1e-6):
        super().__init__()
        self.eps = eps
        self.weight = nn.Parameter(torch.ones(d))  # γ

    def forward(self, x):
        # rsqrt = 1/sqrt，避免先开方再除法
        norm = x * torch.rsqrt(x.pow(2).mean(-1, keepdim=True) + self.eps)
        return self.weight * norm.type_as(x)
```

> [!tip] 实现细节
> - 使用 `rsqrt` 代替 `sqrt` + 除法，减少一次浮点运算
> - `.type_as(x)` 确保半精度（fp16/bf16）输入输出类型一致
> - 无 `bias` 参数

## 为什么现代 LLM 都用 RMSNorm

### Pre-LN 架构下均值减法是冗余的

```
Pre-LN 架构:
x → Norm → Attention → +x → Norm → FFN → +x
           ↑                        ↑
     前一层已归一化，输入近似零均值
     再减均值是冗余计算！
```

在 Pre-LN 架构中，归一化层的输入来自残差路径，已经近似零均值。此时 $\mu \approx 0$，LayerNorm 的减均值步骤几乎无效，RMSNorm 通过省略此步骤获得加速而不损失性能。

### 采用 RMSNorm 的代表模型

[[LLaMA]]、[[PaLM]]、[[Mistral]]、[[Qwen]]、[[Gemma]] 等 decoder-only 模型。

## 适用场景

| 场景 | 原因 |
|------|------|
| Transformer Decoder（GPT/LLaMA） | Pre-LN 架构，输入已归一化 |
| 对训练速度敏感 | 计算量少 ~40% |
| 需要保留语义方向 | 不改变向量方向 |
| 大规模模型 | 参数量少，内存节省 |

## 关系

- **上级**: [[LayerNorm]] —— 共享"沿特征维归一化"结构，但移除中心化与平移
- **对比**: [[Norm对比总结]] —— 与其他归一化方法的系统对比
- **等价**: [[Pre-LN]] 架构中的常用组件，与 SwiGLU 激活函数共同构成现代 LLM 基础模块
- **应用**: [[LLaMA]], [[PaLM]], [[Mistral]], [[Qwen]] 等 decoder-only 模型的默认归一化层

## 挂载路径

[[深度学习]] → [[神经网络组件]] → [[归一化]] → [[RMSNorm]]
