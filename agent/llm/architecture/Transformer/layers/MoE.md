# Mixture of Experts (MoE)

导航：[layers/README.md](./README.md) | [Transformer.md](../Transformer.md) | [architecture/README.md](../../README.md)

**论文**：Outrageously Large Neural Networks: The Sparsely-Gated Mixture-of-Experts Layer (Shazeer et al., 2017)

---

## 核心思想

Mixture of Experts (MoE) 是一种**条件计算（Conditional Computation）**架构，通过**稀疏激活**大幅提升模型容量而不成比例增加计算量。

### 基本原理

MoE层由以下组件构成：
- **专家网络（Experts）**：多个独立的子网络（通常是前馈网络FFN）
- **门控网络（Gating Network）**：决定每个样本应该激活哪些专家

对于输入 `x`，输出为：
```
y = Σ G(x)ᵢ · Eᵢ(x)
```

其中：
- `G(x)` 是门控网络的稀疏输出（大部分为0）
- `Eᵢ(x)` 是第i个专家的输出
- 只计算 `G(x)ᵢ ≠ 0` 的专家，节省计算

### 关键特性

| 特性 | 说明 |
|------|------|
| **容量扩展** | 专家数量可达数千至数万，参数量可达千亿级 |
| **稀疏激活** | 每个样本只激活k个专家（通常k=1-4） |
| **可扩展性** | 通过增加专家数量和设备数量实现线性扩展 |
| **专家特化** | 不同专家自动学习处理不同类型的输入 |

---

## 架构设计

### 1. 门控机制（Gating Mechanism）

#### Noisy Top-K Gating

```
G(x) = Softmax(KeepTopK(H(x), k))
H(x)ᵢ = (x·Wg)ᵢ + StandardNormal()·Softplus((x·Wnoise)ᵢ)
```

**关键组件**：
- **Softmax基础**：`x·Wg` 计算每个专家的权重
- **噪声注入**：帮助负载均衡，避免专家利用率不平衡
- **Top-K稀疏化**：只保留权重最大的k个专家，其余置为0

### 2. 专家网络（Expert Networks）

典型配置：
```
输入维度：512
隐藏层：1024（ReLU激活）
输出维度：512
参数量：~1M per expert
```

### 3. 层次化MoE（Hierarchical MoE）

当专家数量非常大时（如>1000），使用两级结构：
```
y = Σᵢ Σⱼ Gprimary(x)ᵢ · Gᵢ(x)ⱼ · Eᵢ,ⱼ(x)
```

**优势**：
- 减少分支因子
- 更好的负载均衡
- 适配分布式硬件拓扑

---

## 性能挑战与解决方案

详见 [MoE-性能挑战.md](./MoE-性能挑战.md)

MoE面临四个核心挑战：

### 1. 计算架构效率（Computational Efficiency）
**问题**：GPU擅长算术但不擅长分支
**解决**：大块网络开关（稀疏计算）

### 2. 批次收缩问题（Shrinking Batch Problem）
**问题**：每个专家收到的批次大小 ≈ kb/n（k个专家，b批次大小，n总专家数）
**解决**：混合数据并行+模型并行

### 3. 网络带宽瓶颈（Network Bandwidth）
**问题**：专家输入输出需要跨设备传输
**解决**：增大专家计算密度（更大隐藏层）

### 4. 负载均衡（Load Balancing）
**问题**：门控网络倾向于总选择相同专家
**解决**：辅助损失函数（Importance Loss + Load Loss）

---

## 训练技巧

### 损失函数

**1. Importance Loss** - 确保专家重要性相等
```
Importance(X) = Σₓ G(x)
Limportance = wimportance · CV(Importance(X))²
```

**2. Load Loss** - 确保样本数量均衡
```
Load(X)ᵢ = Σₓ P(x,i)
Lload = wload · CV(Load(X))²
```

其中 CV 是变异系数（标准差/均值）

### 初始化策略

- 将 `Wg` 和 `Wnoise` 初始化为全零
- 保证初始状态下负载近似均衡
- 避免训练初期的内存溢出

---

## 实验结果

### 语言建模（1B Word Benchmark）

| 模型 | 参数量 | 测试困惑度 | 计算量 |
|------|--------|-----------|--------|
| LSTM-2048-512 | 151M | 43.7 | 151M ops/step |
| MoE-4096-h | 4.3B | 34.1 | 8.9M ops/step |

**提升**：容量提升28倍，困惑度降低22%，计算量仅6%

### 机器翻译（WMT'14 En→Fr）

| 模型 | BLEU | 参数量 | 训练时间 |
|------|------|--------|---------|
| GNMT | 39.22 | 278M | 6天/96 K80s |
| MoE-2048 | 40.56 | 8.7B | 6天/64 K40s |

**提升**：BLEU +1.34，参数量31倍，GPU需求更少

---

## 应用场景

### 适用场景
1. **超大规模数据集**：训练数据量足以支撑海量参数
2. **多样化任务**：输入具有明显的子类别或子问题
3. **分布式集群**：有足够的硬件资源支持模型并行

### 典型应用
- **语言建模**：不同专家处理不同语法/语义模式
- **多语言翻译**：不同专家专门处理特定语言对
- **多模态模型**：不同专家处理不同模态

---

## 与其他技术的关系

| 技术 | 关系 | 区别 |
|------|------|------|
| **Embedding层** | 都是条件计算 | Embedding是稀疏查找，MoE是稀疏计算 |
| **Dropout** | 都引入稀疏性 | Dropout随机，MoE学习选择 |
| **集成学习** | 都组合多个模型 | MoE端到端训练，专家自动特化 |
| **知识蒸馏** | 可结合使用 | MoE可蒸馏到密集模型 |

详见 [稀疏计算vs稀疏查找.md](./稀疏计算vs稀疏查找.md)

---

## 现代变体

### Switch Transformer (2021)
- 简化为每个token只路由到1个专家（k=1）
- 更激进的稀疏性，更高的扩展性

### GLaM (2021)
- 将MoE应用于每个Transformer层
- 1.2T参数，超越GPT-3

### Mixtral 8x7B (2024)
- 开源MoE模型
- 8个专家，每次激活2个
- 总参数47B，激活参数13B

---

## 相关资源

- 论文：[Outrageously Large Neural Networks](../../论文/MOE.pdf)
- 代码实现：TensorFlow, PyTorch均有官方示例
- 相关技术：[GQA](./GQA.md) | [Flash Attention](../../inference/acceleration/FlashAttention.md)

---

## 参考文献

```bibtex
@article{shazeer2017outrageously,
  title={Outrageously large neural networks: The sparsely-gated mixture-of-experts layer},
  author={Shazeer, Noam and Mirhoseini, Azalia and Maziarz, Krzysztof and Davis, Andy and Le, Quoc and Hinton, Geoffrey and Dean, Jeff},
  journal={arXiv preprint arXiv:1701.06538},
  year={2017}
}
```
