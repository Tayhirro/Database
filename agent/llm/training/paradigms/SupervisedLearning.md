# 监督学习（Supervised Learning）

导航：[paradigms/README.md](README.md)

---

## 一句话

从标注数据中学习输入到输出映射的学习范式。

---

## 严格定义

监督学习 (SL)：给定标注数据集 $\{(x_i, y_i)\}_{i=1}^N$，优化模型参数使预测 $\hat{y} = f_\theta(x)$ 与真实标签 $y$ 的差异最小。

$$
\min_\theta \frac{1}{N} \sum_{i=1}^{N} \mathcal{L}(f_\theta(x_i), y_i)
$$

---

## 接口

**输入**：
- 输入数据 $x \in \mathcal{X}$
- 标签 $y \in \mathcal{Y}$

**输出**：
- 模型 $f_\theta: \mathcal{X} \to \mathcal{Y}$

---

## 在 LLM 中的应用

| 应用 | 输入 $x$ | 标签 $y$ | 损失函数 |
|------|----------|----------|----------|
| SFT | 指令 | 回复 | Cross-Entropy |
| Instruction Tuning | 指令模板 | 标准回复 | Cross-Entropy |

---

## 关系

- 上级：[Paradigms](README.md)
- 应用于：[SFT](../post-training/sft/)
