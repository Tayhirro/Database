---
title: RNN 变体（LSTM / GRU / BiRNN）
date: "2026-01-26"
categories:
  - 神经网络
description: modules/ 里放“序列建模的共用理论”；这里放可复用的 RNN 家族变体（更像模型卡片/结构说明）。
---
# RNN 变体（LSTM / GRU / BiRNN）

> `modules/` 里放“序列建模的共用理论”；这里放可复用的 **RNN 家族变体**（更像模型卡片/结构说明）。

## 入口
- LSTM：[LSTM.md](LSTM.md)
- GRU：[GRU.md](GRU.md)
- 双向 RNN（BiRNN / BiLSTM / BiGRU）：[BiRNN.md](BiRNN.md)

## 速查：怎么选
- **默认**：数据不大/序列不太长时，优先试 `GRU`（省参数，通常够用）
- **更长依赖/更难任务**：试 `LSTM`（门更多，更稳但更重）
- **离线编码（能看到全序列）**：用 `BiRNN` 系（`BiLSTM`/`BiGRU`）

