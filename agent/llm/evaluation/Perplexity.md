---
title: "困惑度（Perplexity, PPL）"
date: "2026-02-08"
categories:
  - agent
description: "导航：evaluation/README.md | CrossEntropy.md"
---
# 困惑度（Perplexity, PPL）

导航：[evaluation/README.md](../README.md) | [CrossEntropy.md](./CrossEntropy.md)

**困惑度**是语言模型评估的核心指标，直观表示模型有多"困惑"。

---

## 定义

$$\text{PPL} = \exp\left(-\frac{1}{N} \sum_{i=1}^{N} \log P(w_i | w_{<i})\right) = \exp(\text{CrossEntropy})$$

其中：
- $N$：测试集中的token总数
- $P(w_i | w_{<i})$：模型预测第 $i$ 个token的条件概率
- CrossEntropy：平均交叉熵损失

---

## 直观理解

### 等效分支因子

**困惑度 = 模型每次预测时面临的选择数量**。

```
PPL = 100  ⟺  相当于每次从100个等概率选项中猜
PPL = 2    ⟺  相当于从2个选项中猜（如抛硬币）
PPL = 1    ⟺  完全确定，没有选择（理论最优）
```

### 具体例子

```python
# 模型A：PPL = 50
# 相当于面对一个50面的骰子
# 模型有1/50的概率猜中下一个词

# 模型B：PPL = 500  
# 相当于面对一个500面的骰子
# 模型A比模型B好10倍

# 人类水平：PPL ≈ 10-20（视任务而定）
# GPT-3：PPL ≈ 10-30（不同数据集）
```

---

## 为什么用指数？

### 线性化解释

```python
# 交叉熵是 log 尺度，困惑度是线性尺度
CE = 2.3   →  PPL = exp(2.3) ≈ 10
CE = 4.6   →  PPL = exp(4.6) ≈ 100

# 差异：
# CE从2.3→4.6（增加2.3）
# PPL从10→100（增加90，直观展示10倍差距）
```

### 几何平均解释

```python
# PPL = 几何平均的倒数
# 如果每个token的概率是 [p1, p2, p3, ...]
# PPL = 1 / (p1 * p2 * p3 * ...)^(1/N)

# 例子：
# 3个token，概率都是0.5
# PPL = 1 / (0.5 * 0.5 * 0.5)^(1/3) = 1 / 0.5 = 2
```

---

## 计算示例

### 简单句子

```python
句子："我爱北京"
模型概率：
P("我"|<BOS>) = 0.1
P("爱"|"我") = 0.3  
P("北京"|"我爱") = 0.05

平均对数概率 = (log(0.1) + log(0.3) + log(0.05)) / 3
            = (-2.3 - 1.2 - 3.0) / 3
            = -2.17

PPL = exp(2.17) ≈ 8.8
```

### 实际代码

```python
import torch
import torch.nn.functional as F

def calculate_perplexity(model, dataloader, device):
    model.eval()
    total_loss = 0
    total_tokens = 0
    
    with torch.no_grad():
        for batch in dataloader:
            input_ids = batch['input_ids'].to(device)
            attention_mask = batch['attention_mask'].to(device)
            
            # 前向传播
            outputs = model(input_ids, attention_mask=attention_mask)
            logits = outputs.logits
            
            # 计算交叉熵
            shift_logits = logits[..., :-1, :].contiguous()
            shift_labels = input_ids[..., 1:].contiguous()
            
            loss = F.cross_entropy(
                shift_logits.view(-1, shift_logits.size(-1)),
                shift_labels.view(-1),
                reduction='sum'
            )
            
            # 统计
            total_loss += loss.item()
            total_tokens += attention_mask.sum().item()
    
    # 计算困惑度
    avg_loss = total_loss / total_tokens
    perplexity = torch.exp(torch.tensor(avg_loss))
    
    return perplexity.item()
```

---

## 不同规模模型的PPL参考

| 模型 | 参数量 | WikiText-103 PPL | 1B Word PPL |
|------|--------|------------------|-------------|
| LSTM | 151M | 45.0 | 43.7 |
| GPT-1 | 117M | 18.4 | - |
| GPT-2 | 1.5B | 10.8 | - |
| GPT-3 | 175B | ~8 | ~10 |
| GPT-4 | ? | ~6 | ~8 |
| MoE-137B | 137B | - | 24% lower |

**趋势**：参数量越大，PPL越低（但不代表实际能力越强！）

---

## 困惑度的局限性

### 1. 不能反映生成质量

```python
# 低PPL不等于好生成
# 模型可能学会了"安全"的平凡预测

高PPL文本："量子纠缠是量子力学中的现象..."（专业但PPL高）
低PPL文本："的的是了了"（重复、无意义但PPL低）
```

### 2. 无法衡量指令遵循能力

PPL只衡量"预测下一个词"，不衡量：
- 指令理解
- 逻辑推理
- 多轮对话
- 安全性

### 3. 与下游任务性能不线性相关

```
PPL降低50% ≠ 下游任务提升50%

例如：
- 基座模型PPL = 10
- 微调后PPL = 12（略增）
- 但下游任务准确率从60%→90%（大幅提升）
```

---

## 使用建议

### 何时使用PPL？

✅ **适合**：
- 预训练阶段监控收敛
- 对比同架构不同规模模型
- 语言建模基准测试

❌ **不适合**：
- 评估对话质量
- 衡量指令遵循能力
- 比较不同架构（如GPT vs BERT）

### 结合其他指标

```python
# 完整评估应包含：
metrics = {
    'PPL': calculate_ppl(model, test_set),  # 语言建模能力
    'BLEU': calculate_bleu(generations, references),  # 生成质量
    'Accuracy': evaluate_benchmark(model),  # 任务性能
    'HumanEval': evaluate_humaneval(model),  # 代码能力
}
```

---

## 相关概念对比

| 指标 | 计算公式 | 使用场景 |
|------|---------|---------|
| **CrossEntropy** | $-\frac{1}{N}\sum \log P(w_i)$ | 训练损失 |
| **Perplexity** | $\exp(\text{CE})$ | 评估报告 |
| **Bits Per Char** | $\text{CE} / \ln(2)$ | 压缩效率 |
| **Token Accuracy** | $\frac{\text{正确预测数}}{\text{总数}}$ | 分类评估 |

---

## 参考

- [CrossEntropy.md](./CrossEntropy.md) - 困惑度的对数形式
- [MMLU.md](./benchmarks/MMLU.md) - 综合能力评估基准
- [HumanEval.md](./benchmarks/HumanEval.md) - 代码能力评估
