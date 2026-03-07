---
title: 优化器（Optimizer）
date: "2026-03-06"
categories:
  - 神经网络
description: 优化器是“如何用梯度更新参数”的规则。核心问题是：在噪声梯度下，如何兼顾收敛速度、稳定性和泛化；常见路线包括 SGD 家族、自适应学习率家族和解耦正则化（AdamW）。
---
# 优化器（Optimizer）

## 1. 一句话
- 优化器是参数更新算法：给定当前参数 $\theta_t$ 和梯度 $g_t=\nabla_\theta L(\theta_t)$，计算下一步参数 $\theta_{t+1}$。

## 2. 定义 / 公式（最常用那版）
- 统一写法：

$$
\theta_{t+1} = \theta_t + \Delta_t
$$

- 不同优化器的核心区别是 $\Delta_t$ 如何由历史梯度构造。

**(1) SGD / Mini-batch SGD**

$$
\theta_{t+1} = \theta_t - \eta g_t
$$

- 其中 $\eta$ 是学习率（step size）。

**(2) Momentum / Nesterov**
- Momentum：

$$
v_t = \beta v_{t-1} + (1-\beta)g_t
$$

$$
\theta_{t+1} = \theta_t - \eta v_t
$$

- Nesterov（常见实现口径）：先按动量方向“预看”，再算梯度，通常比普通 Momentum 更稳。

**(3) AdaGrad / RMSProp（按坐标自适应学习率）**
- AdaGrad：

$$
s_t = s_{t-1} + g_t^2,
\quad
\theta_{t+1} = \theta_t - \eta \frac{g_t}{\sqrt{s_t}+\epsilon}
$$

- RMSProp：

$$
s_t = \rho s_{t-1} + (1-\rho)g_t^2,
\quad
\theta_{t+1} = \theta_t - \eta \frac{g_t}{\sqrt{s_t}+\epsilon}
$$

- RMSProp 的重点是用滑动平均代替累加，避免 AdaGrad 后期学习率过小。

**(4) Adam / AdamW（一阶+二阶矩估计）**

$$
m_t = \beta_1 m_{t-1} + (1-\beta_1)g_t
$$

$$
v_t = \beta_2 v_{t-1} + (1-\beta_2)g_t^2
$$

$$
\hat m_t = \frac{m_t}{1-\beta_1^t},
\quad
\hat v_t = \frac{v_t}{1-\beta_2^t}
$$

$$
\text{Adam:}\quad
\theta_{t+1} = \theta_t - \eta \frac{\hat m_t}{\sqrt{\hat v_t}+\epsilon}
$$

$$
\text{AdamW:}\quad
\theta_{t+1} = \theta_t - \eta \frac{\hat m_t}{\sqrt{\hat v_t}+\epsilon} - \eta\lambda\theta_t
$$

## 3. 直觉（为什么这么设计）
- SGD：方向由当前梯度决定，简单且常有较好泛化，但对学习率敏感。
- Momentum：对梯度做指数平滑，减少小批次噪声影响，让参数沿“长期一致方向”加速。
- RMSProp/AdaGrad：给每个参数单独步长；“梯度经常大”的坐标走小步，“梯度稀疏或小”的坐标走大步。
- Adam：把 Momentum（方向平滑）和 RMSProp（尺度归一）合在一起，通常更快起步、调参更省心。
- AdamW：把权重衰减从自适应梯度缩放里解耦，实践中更稳定，尤其在 Transformer 训练中常作为默认。

## 4. 具体分类（实用视角）
- 一阶、固定学习率家族：`SGD`、`Momentum`、`Nesterov`
- 一阶、自适应学习率家族：`AdaGrad`、`RMSProp`、`Adam`、`AdamW`
- 按“是否解耦权重衰减”：`Adam`（未解耦） vs `AdamW`（解耦）
- 按“工程搭配”：优化器通常与 `learning rate scheduler`（warmup / cosine / step）一起决定最终训练动态。

## 5. 在哪些模型里出现
- 几乎所有深度模型都要选优化器（CNN/RNN/Transformer/Diffusion/多模态）。
- 常见经验：
  - 视觉/经典任务：`SGD+Momentum` 常见且泛化强。
  - 大模型/NLP/多模态：`AdamW + warmup + cosine decay` 很常见。
  - 小数据或快速原型：`Adam/AdamW` 往往更快得到可用结果。

## 6. 速查
- 关键词：学习率 `lr`、动量 $\beta$、二阶矩 $v_t$、偏差校正、权重衰减、解耦正则化
- 常见坑：
  - `weight_decay` 与 `L2` 正则混用概念（Adam 与 AdamW 行为不一样）
  - 只改优化器不改学习率/调度器，导致对比不公平
  - 忽略 warmup，前期梯度不稳导致 loss 抖动
  - 把 `eps` 设得过大或过小，出现数值不稳或更新过慢
- PyTorch 常用：`torch.optim.SGD`、`torch.optim.Adam`、`torch.optim.AdamW`、`torch.optim.RMSprop`
