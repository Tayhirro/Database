---
title: 双向 RNN（BiRNN / BiLSTM / BiGRU）
date: "2026-01-26"
categories:
  - 神经网络
description: 同时用“正向 + 反向”两条递推链编码序列，把未来上下文也纳入每个时间步的表征里（适合离线场景）。
---
# 双向 RNN（BiRNN / BiLSTM / BiGRU）

## 1. 一句话
- 同时用“正向 + 反向”两条递推链编码序列，把未来上下文也纳入每个时间步的表征里（适合离线场景）。

## 2. 目标（解决什么问题）
- 让表示同时包含过去与未来的信息：对标注/编码类任务（NER、语音、轨迹离线编码等）通常更有利。

## 3. 核心结构（数据流）
- 正向：$$\overrightarrow{h}_t = \text{RNN}(x_t,\overrightarrow{h}_{t-1})$$
- 反向：$$\overleftarrow{h}_t = \text{RNN}(x_t,\overleftarrow{h}_{t+1})$$
- 融合（常见）：$$h_t = [\overrightarrow{h}_t;\overleftarrow{h}_t] \ \text{或}\  h_t = \overrightarrow{h}_t + \overleftarrow{h}_t$$
- 其中 `RNN` 可以是 Vanilla RNN / LSTM / GRU，对应 BiRNN / BiLSTM / BiGRU。

## 4. 损失 / 训练目标
- 由任务决定；BiRNN 只是编码方式的改变。

## 5. 训练流程（关键细节）
- 变长序列：同样需要 `padding/mask`
- 输出方式：
  - many-to-many：每步用 `h_t` 做预测（序列标注）
  - many-to-one：对 `h_{1:T}` 做 pooling（mean/max/attention）或取两端状态拼接

## 6. 推理
- 需要拿到全序列才能跑反向链，因此是 **离线** 编码器。

## 7. 常见坑 & Debug 清单
- **不适合严格在线预测**：在线场景用不了反向信息（除非允许延迟）
- **维度翻倍**：concat 融合会把 hidden size 翻倍，后续层参数也跟着涨
- **pad 的反向影响**：反向链如果没 mask，padding 会污染早期步的表示

## 8. 扩展与对比（相关模型）
- 对比：单向 RNN（在线/自回归更合适）
- 常见组合：BiLSTM + CRF（序列标注传统强基线）

## 9. 参考
- Schuster & Paliwal, 1997

