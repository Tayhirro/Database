# 偏好优化（Preference Optimization）

导航：[paradigms/README.md](README.md)

---

## 一句话

直接从偏好对比数据优化模型，无需显式奖励模型。

---

## 严格定义

偏好优化 (PO)：给定偏好数据 $(x, y_w, y_l)$（$y_w$ 优于 $y_l$），优化模型使偏好样本概率更高。

DPO 目标函数：

$$
\mathcal{L}_{\text{DPO}} = -\mathbb{E} \left[ \log \sigma \left( \beta \log \frac{\pi_\theta(y_w|x)}{\pi_{\text{ref}}(y_w|x)} - \beta \log \frac{\pi_\theta(y_l|x)}{\pi_{\text{ref}}(y_l|x)} \right) \right]
$$

---

## 接口

**输入**：
- Prompt $x$
- 偏好对 $(y_w, y_l)$：$y_w \succ y_l$
- 参考模型 $\pi_{\text{ref}}$

**输出**：
- 优化后策略 $\pi_\theta$

---

## 方法对比

| 方法 | 特点 |
|------|------|
| DPO | 直接优化，无需 RM |
| IPO | 防止过拟合偏好 |
| KTO | 只需二元反馈（好/坏） |
| ORPO | 结合 SFT 与偏好 |

---

## 与 RLHF 的对比

| | RLHF | Preference Optimization |
|---|------|------------------------|
| 需要 RM | 是 | 否 |
| 训练稳定性 | 较低 | 较高 |
| 计算成本 | 较高 | 较低 |

---

## 关系

- 上级：[Paradigms](README.md)
- 应用于：[Alignment](../post-training/alignment/)
- 替代：[RLHF](../post-training/alignment/RLHF.md)
