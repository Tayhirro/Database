# 稀疏计算 vs 稀疏查找

导航：[layers/README.md](./README.md) | [MoE.md](./MoE.md) | [MoE-性能挑战.md](./MoE-性能挑战.md)

**核心问题**：为什么Embedding层（稀疏查找）和MoE层（稀疏计算）虽然都是"条件计算"，但面临完全不同的性能瓶颈？

---

## 概念定义

### 稀疏查找（Sparse Lookup）

**定义**：根据索引从参数表中**直接读取**预存储的值，无需计算。

**典型代表**：Embedding层

```python
# Embedding查找示例
vocab_size = 50000
embedding_dim = 512
embedding_table = np.random.randn(vocab_size, embedding_dim)  # 预存储

# 查找操作
token_id = 42
embedding = embedding_table[token_id]  # 纯内存读取，0计算
```

### 稀疏计算（Sparse Computation）

**定义**：根据条件**选择性执行**部分计算模块，每个模块包含大量运算。

**典型代表**：MoE专家层

```python
# MoE专家计算示例
def expert_network(x):
    # 大量矩阵运算
    hidden = ReLU(x @ W1)  # [512, 1024] 矩阵乘法
    output = hidden @ W2   # [1024, 512] 矩阵乘法
    return output

# 条件计算
selected_experts = gating_network(x)  # 选择k个专家
output = sum(expert_network(x) for expert in selected_experts)
```

---

## 核心区别对比

| 维度 | 稀疏查找<br>(Embedding) | 稀疏计算<br>(MoE) |
|------|-------------------------|-------------------|
| **操作类型** | 内存读取 | 算术运算 |
| **计算量** | 0 FLOPs | 百万级 FLOPs per expert |
| **通信量** | embedding_dim | input_dim + output_dim |
| **计算/通信比** | 0:1 | 100-1000:1 |
| **瓶颈类型** | 带宽密集型 | 计算密集型 |
| **GPU利用率** | 低（内存等待） | 高（矩阵运算） |
| **可调节性** | 固定（由embedding_dim决定） | 灵活（可增大隐藏层） |
| **分布式效率** | 差（通信主导） | 好（计算主导） |

---

## 详细分析

### 1. 计算特征

#### Embedding层（稀疏查找）

**操作流程**：
```
输入：token_id = 42
     ↓
参数表查找：embedding_table[42]
     ↓
输出：[512维向量]
```

**计算分析**：
- **算术运算数**：0（只是数组索引）
- **内存操作数**：512次读取（float32）
- **操作类型**：纯内存带宽操作

**硬件视角**：
```
CPU/GPU执行：
1. 计算内存地址：base_addr + 42 * 512 * 4
2. 发起内存读取请求
3. 等待内存响应 ← 主要时间消耗
4. 返回数据
```

#### MoE专家层（稀疏计算）

**操作流程**：
```
输入：x = [512维向量]
     ↓
专家选择：gating_network(x) → Expert_3, Expert_7
     ↓
专家计算：
  Expert_3:
    h = ReLU(x @ W1_3)  # 512×1024 = 524,288 ops
    y = h @ W2_3        # 1024×512 = 524,288 ops
  Expert_7:
    h = ReLU(x @ W1_7)  # 524,288 ops
    y = h @ W2_7        # 524,288 ops
     ↓
输出：加权求和
```

**计算分析**：
- **算术运算数**：2 × 2 × 524,288 = 2,097,152 ops
- **内存操作数**：512(输入) + 512(输出) = 1,024 floats
- **操作类型**：密集矩阵运算

**硬件视角**：
```
GPU执行（以Expert_3为例）：
1. 读取输入向量 x [512] → 2KB
2. 读取权重 W1_3 [512×1024] → 2MB
3. 执行矩阵乘法 → 524,288次乘加运算
4. ReLU激活 → 1,024次比较和条件置零
5. 读取权重 W2_3 [1024×512] → 2MB
6. 执行矩阵乘法 → 524,288次乘加运算
7. 写回输出 [512] → 2KB

GPU大部分时间在计算，内存等待时间可忽略
```

---

### 2. 计算/通信比分析

这是决定分布式性能的关键指标。

#### Embedding层

**分布式场景**：
```
场景：词表分布在4台机器上
词表大小：50,000
Embedding维度：512

机器1：token 0-12,499
机器2：token 12,500-24,999
机器3：token 25,000-37,499
机器4：token 37,500-49,999
```

**单次查询分析**：
```
输入：token_id = 42（在机器1上）

本地操作：
- 计算量：0 FLOPs
- 内存读取：512 floats = 2KB

跨机器传输：
- 发送token_id到机器1：4 bytes
- 机器1返回embedding：512 floats = 2KB

计算/通信比 = 0 / 2KB = 0
```

**瓶颈**：
- 完全受限于网络延迟和带宽
- GPU计算能力完全浪费
- 无法通过增加计算来改善

#### MoE专家层

**分布式场景**：
```
场景：512个专家分布在4台机器上

机器1：Expert 0-127
机器2：Expert 128-255
机器3：Expert 256-383
机器4：Expert 384-511
```

**单个样本分析**：
```
输入：x = [512维]，门控选择Expert_3和Expert_130

机器1处理Expert_3：
  接收数据：x [512] = 2KB
  计算量：2 × 524,288 = 1,048,576 FLOPs
  返回数据：output [512] = 2KB
  
  计算/通信比 = 1,048,576 FLOPs / 4KB = 262,144 FLOPs/KB

机器2处理Expert_130：
  接收数据：x [512] = 2KB
  计算量：1,048,576 FLOPs
  返回数据：output [512] = 2KB
  
  计算/通信比 = 1,048,576 FLOPs / 4KB = 262,144 FLOPs/KB
```

**关键优势**：
```
假设网络带宽：10 GB/s
假设计算能力：100 TFLOPS

网络可支持：10 GB/s = 2.5G float/s 的传输
计算可支持：100 TFLOPS 的运算

MoE的计算/通信比 = 262,144 FLOPs/KB
                  = 262,144 FLOPs / (1024 bytes)
                  = 256 FLOPs/float
                  
这意味着每传输1个float，可以执行256次运算
与硬件比值（100T / 10G = 10,000）相比仍有优化空间
但已经远好于Embedding的0比值
```

---

### 3. 可调节性

#### Embedding层：固定比值

**问题**：Embedding的计算/通信比**无法改善**

```python
# 尝试改善Embedding
vocab_size = 50000
embedding_dim = 512  # 固定，由模型设计决定

# 计算/通信比永远是 0
# 无论如何优化都无法增加计算量
```

**根本原因**：
- Embedding维度由下游任务决定
- 无法为了提高计算/通信比而增加embedding维度
- 增大embedding_dim只会增加通信量，不增加计算量

#### MoE层：灵活可调

**优势**：可以通过调整隐藏层大小来改善比值

```python
# 方案1：小隐藏层
hidden_size = 512
计算量 = 2 × 512 × 512 = 524,288 FLOPs
通信量 = 512 + 512 = 1,024 floats
比值 = 524,288 / 1,024 = 512

# 方案2：中隐藏层
hidden_size = 1024
计算量 = 2 × 512 × 1024 = 1,048,576 FLOPs
通信量 = 512 + 512 = 1,024 floats（不变！）
比值 = 1,048,576 / 1,024 = 1024

# 方案3：大隐藏层
hidden_size = 2048
计算量 = 2 × 512 × 2048 = 2,097,152 FLOPs
通信量 = 512 + 512 = 1,024 floats（不变！）
比值 = 2,097,152 / 1,024 = 2048
```

**关键洞察**：
- 增大隐藏层只增加**内部计算**，不增加**跨机器通信**
- 可以轻松将计算/通信比提高到1000+
- 匹配GPU集群的硬件特性

---

### 4. GPU利用率

#### Embedding层

**GPU执行特征**：
```
时间线：
[内存读取请求] → [等待...] → [数据返回] → [继续]
 0.1 μs         99.8 μs       0.1 μs

GPU利用率：0.2 / 100 = 0.2%
```

**问题**：
- GPU的计算单元**完全闲置**
- 只有内存控制器在工作
- 典型的内存带宽瓶颈（Memory-bound）

#### MoE专家层

**GPU执行特征**：
```
时间线：
[读权重] → [矩阵乘法] → [激活函数] → [矩阵乘法] → [写回]
 1 μs      100 μs        10 μs         100 μs       1 μs

GPU利用率：210 / 212 = 99%
```

**优势**：
- GPU的CUDA核心**高速运转**
- Tensor Core（如有）充分利用
- 典型的计算密集型（Compute-bound）

---

## 分布式性能对比

### 硬件假设

```
GPU集群配置：
- 单GPU计算能力：100 TFLOPS (FP16)
- GPU间网络带宽：10 GB/s
- 硬件比值：100 TFLOPS / 10 GB/s = 10,000

要求：算法的计算/通信比 > 10,000 才能充分利用计算能力
```

### Embedding层性能

**批次处理**：
```
批次大小：1024 tokens
Embedding维度：512

总通信量：1024 × 512 × 4 bytes = 2MB
总计算量：0 FLOPs

网络传输时间：2MB / 10GB/s = 0.2ms
计算时间：0ms
总时间：0.2ms（受网络限制）

GPU利用率：0%
瓶颈：100%网络带宽
```

**结论**：Embedding分布式扩展受限于网络，无法利用GPU计算能力

### MoE层性能

**批次处理**：
```
批次大小：1024 samples
专家配置：512 experts, 每样本激活2个
专家隐藏层：1024
设备数：16

每个设备收到的样本：1024 × 2 / 16 ≈ 128 samples

每设备通信量：
  输入：128 × 512 × 4 bytes = 256KB
  输出：128 × 512 × 4 bytes = 256KB
  总计：512KB

每设备计算量：
  128 samples × 2 × 512 × 1024 FLOPs
  = 134,217,728 FLOPs = 134M FLOPs

计算/通信比：134M FLOPs / 512KB = 261,632 FLOPs/KB

网络传输时间：512KB / (10GB/s) = 0.05ms
计算时间：134M FLOPs / 100 TFLOPS = 1.34ms
总时间：1.39ms（受计算限制）

GPU利用率：1.34 / 1.39 = 96%
瓶颈：96%计算，4%网络带宽
```

**结论**：MoE成功将瓶颈从网络转移到计算，充分利用GPU

---

## 论文原文引用

从MOE论文的描述：

> **Network bandwidth can be a bottleneck.** A cluster of GPUs may have computational power thousands of times greater than the aggregate inter-device network bandwidth. To be computationally efficient, the relative computational versus network demands of an algorithm must exceed this ratio. 
> 
> **Embedding layers, which can be seen as a form of conditional computation, are handicapped by this very problem.** Since the embeddings generally need to be sent across the network, the number of (example, parameter) interactions is limited by network bandwidth instead of computational capacity.

**翻译与解读**：
- Embedding层被视为一种条件计算形式
- **但它受限于网络带宽瓶颈**
- (样本,参数)交互数量受限于网络而非计算能力
- 这正是稀疏**查找**的问题

> **We use experts with one hidden layer containing thousands of RELU-activated units.** Since the weight matrices in the expert have sizes input_size×hidden_size and hidden_size×output_size, **the ratio of computation to input and output is equal to the size of the hidden layer.** 
> 
> **Conveniently, we can increase computational efficiency simply by using a larger hidden layer, or more hidden layers.**

**翻译与解读**：
- 专家使用包含数千单元的隐藏层
- 计算/IO比等于隐藏层大小
- 可以通过增大隐藏层轻松提高计算效率
- 这正是稀疏**计算**的优势

---

## 实际影响

### 案例1：大规模词表的NMT

**场景**：神经机器翻译，100万词表

**Embedding层问题**：
```
词表大小：1,000,000
Embedding维度：1024
参数量：1,000,000 × 1024 = 1B参数

分布到16台机器：
- 每台机器：62,500个词
- 每次查询可能需要跨机器通信
- 完全受限于网络延迟

无法改善：
- 不能减小embedding_dim（损害模型质量）
- 不能增加计算（本质上是查表）
- 只能忍受网络瓶颈
```

**MoE层解决方案**：
```
专家数量：512
每样本激活：2
专家隐藏层：2048

计算/通信比：2048（可调节）
可以匹配网络/计算硬件比
充分利用GPU计算能力
```

### 案例2：超大规模语言模型

**100B Word数据集实验**（论文实验）：

**如果用Embedding扩展**：
- 增大词表和embedding维度
- 线性增加通信量
- GPU计算能力浪费
- 扩展性差

**使用MoE扩展**：
- 65,536个专家
- 68B参数
- 99.994%稀疏度
- **仍保持0.72 TFLOPS/GPU效率**

---

## 设计启示

### 1. 选择条件计算类型

**使用稀疏查找（Embedding）**：
- 参数是离散的预定义值
- 不需要复杂计算
- 单机或小规模分布式
- 对延迟不敏感

**使用稀疏计算（MoE）**：
- 需要复杂的参数化变换
- 有足够的计算空间
- 大规模分布式集群
- 需要充分利用GPU

### 2. 优化策略

**Embedding层优化**：
```
✓ 减少跨设备查询（局部性）
✓ 缓存常用embedding
✓ 批量查询合并
✗ 增加计算量（无法做到）
```

**MoE层优化**：
```
✓ 增大隐藏层大小
✓ 增加专家网络深度
✓ 优化批次大小
✓ 混合并行策略
```

### 3. 混合架构

实际系统中两者常常共存：

```
输入序列 → Embedding层（稀疏查找）
          ↓
        Transformer层
          ↓
        MoE层（稀疏计算）
          ↓
        Transformer层
          ↓
        输出层
```

**设计原则**：
- Embedding层：尽量本地化，减少通信
- MoE层：充分利用计算，调节计算/通信比
- 整体：平衡两种瓶颈

---

## 总结表

| 对比维度 | 稀疏查找 (Embedding) | 稀疏计算 (MoE) |
|---------|---------------------|----------------|
| **本质** | 参数表查询 | 条件执行计算 |
| **操作** | 内存读取 | 矩阵运算 |
| **FLOPs** | 0 | 百万-千万级 |
| **瓶颈** | 网络带宽 | GPU计算 |
| **计算/通信** | 0 | 100-10,000+ |
| **可调性** | 不可调 | 灵活可调 |
| **GPU利用** | <1% | 70-99% |
| **扩展性** | 差 | 优秀 |
| **适用场景** | 离散表示 | 连续变换 |

---

## 参考文档

- [MoE主文档](./MoE.md)
- [MoE性能挑战](./MoE-性能挑战.md)
- [论文原文](../../../../论文/MOE.pdf)
