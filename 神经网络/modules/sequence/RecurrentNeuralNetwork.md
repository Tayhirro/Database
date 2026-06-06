---
title: RNN（Recurrent Neural Network）
date: "2026-01-26"
categories:
  - 神经网络
description: "处理序列 x_{1:T} 的神经网络：用共享参数在时间上递推隐藏状态 h_t，把“历史信息”压进一个可更新的记忆里。"
---
# RNN（Recurrent Neural Network）

## 1. 一句话
- 处理序列 `x_{1:T}` 的神经网络：用共享参数在时间上递推隐藏状态 `h_t`，把“历史信息”压进一个可更新的记忆里。

## 2. 定义 / 公式（Vanilla RNN）

- 给定输入 `x_t`、上一时刻隐藏状态 `h_{t-1}`：
  - $$h_t = \phi(W_x x_t + W_h h_{t-1} + b)$$
  - $$y_t = g(W_y h_t + b_y)\quad (\text{可选})$$
- 训练通常用 BPTT（Backprop Through Time）。长序列容易出现梯度消失/爆炸（常配 gradient clipping）。

## 3. 直觉（为什么能做序列）
- `h_t` 是“可学习的状态变量”：每步读入 `x_t`，用同一套参数更新一次，相当于在时间上做递归的特征提取。
- LSTM/GRU 的门控本质是“让网络学会什么时候该忘、什么时候该记”，从而更稳地建模长期依赖。

## 4. 输出方式（常见任务接口）
- many-to-one：用 `h_T`（或 pooling）当序列 embedding（分类/意图识别/轨迹编码常见）
- many-to-many：输出每步 `y_t`（序列标注/逐步预测）

## 5. 在哪些模型里出现
- 经典 NLP：语言模型、Seq2Seq（Transformer 之前的主力）。
- 时间序列：预测/插补/异常检测。
- 轨迹预测：Social-LSTM、edgeRNN/nodeRNN（把历史轨迹编码成运动特征），以及“先用 GRU/LSTM 把时间压成向量，再做图/注意力交互”的 summary-level 方案。

## 6. 速查
- 关键词：BPTT、vanishing/exploding gradients、gradient clipping、teacher forcing、hidden state init、padding/mask、BiLSTM。
- 常见坑：
  - 用绝对坐标直接喂 RNN 容易学到坐标系偏置：很多任务更偏好输入位移 `Δx, Δy`/速度等相对量。
  - rollout 预测的 exposure bias：训练时喂 GT、推理时喂预测会有分布漂移（可用 scheduled sampling/非自回归方案缓解）。

## 7. 常用变体（放到 models/rnn）
- 入口：[models/rnn/README.md](../../models/rnn/README.md)
- LSTM：[models/rnn/LSTM.md](../../models/rnn/LSTM.md)
- GRU：[models/rnn/GRU.md](../../models/rnn/GRU.md)
- 双向 RNN（BiRNN / BiLSTM / BiGRU）：[models/rnn/BiRNN.md](../../models/rnn/BiRNN.md)
