---
title: 参数初始化（Initialization / Weight Initialization）
date: "2026-01-16"
categories:
  - 神经网络
description: 用“合适尺度”的随机权重初始化，使前向信号与反向梯度在层间传播时不过度爆炸/消失，从而更容易训练。
---
# 参数初始化（Initialization / Weight Initialization）

## 1. 一句话
- 用“合适尺度”的随机权重初始化，使前向信号与反向梯度在层间传播时不过度爆炸/消失，从而更容易训练。

## 2. 定义 / 公式（最常用：Xavier / He）
- 线性层：`y = W x + b`，其中 `W ∈ R^{fan_out × fan_in}`。
- 常见假设（用于推导尺度）：`x_i` 独立同分布、`E[x_i]=0`、`Var(x_i)=v`；`W_{ji}` 独立同分布、`E[W_{ji}]=0`、`Var(W_{ji})=σ^2`，且与 `x` 独立。

**前向尺度（只看线性部分）**
- `y_j = Σ_i W_{j i} x_i`
- `Var(y_j) = Σ_i Var(W_{j i} x_i) = fan_in · Var(W) · Var(x) = fan_in · σ^2 · v`
- 为了让 `Var(y_j) ≈ Var(x_i)`（即 `fan_in·σ^2·v ≈ v`），可取 `σ^2 ≈ 1/fan_in`。

**反向尺度（梯度传播）**
- `dL/dx = W^T (dL/dy)`，同理可得 `Var(dL/dx_i) ≈ fan_out · σ^2 · Var(dL/dy_j)`。
- 为了让梯度方差不随层数系统性放大/缩小，可取 `σ^2 ≈ 1/fan_out`。

**Xavier/Glorot 初始化（兼顾前向与反向的折中）**
- 取 `Var(W) = gain^2 · 2/(fan_in + fan_out)`。
- 若用正态分布（PyTorch `xavier_normal_`）：`W ~ N(0, std^2)`，其中 `std = gain · sqrt(2/(fan_in + fan_out))`。
- 若用均匀分布（`xavier_uniform_`）：`W ~ U(-a, a)`，其中 `a = gain · sqrt(6/(fan_in + fan_out))`（因为 `Var(U(-a,a))=a^2/3`）。

**He/Kaiming 初始化（ReLU 家族常用）**
- ReLU 会“砍掉一半”信号（粗略近似使方差乘以 `1/2`），因此常取 `Var(W) ≈ 2/fan_in` 来补偿。
- PyTorch `kaiming_*` 用 `gain` 来适配激活函数：例如 ReLU 常用 `gain = sqrt(2)`（`nn.init.calculate_gain('relu')`）。

## 3. 直觉（背后数学是什么）
- 本质是一个“方差守恒/尺度守恒”的问题：每层都是很多独立随机变量的加权和，方差会按 `fan_in` 线性累积；如果 `Var(W)` 选得太大，层数一深就爆炸；太小就消失。
- Xavier 的 `2/(fan_in+fan_out)` 可以看成同时让前向与反向都“尽量不漂移”的折中；`gain` 则是在把“非线性激活造成的尺度变化”吸收到初始化里。

## 4. 常用变体 / 记号差异
- `fan_in / fan_out`：线性层分别是输入/输出维度；卷积层一般是 `fan_in = in_channels·k_h·k_w`，`fan_out = out_channels·k_h·k_w`。
- `gain`：与激活函数有关（ReLU≈`sqrt(2)`；tanh 常见 `5/3`；leaky ReLU 依负半轴斜率而变），不同库可能默认不同。
- Bias 初始化：把 `b` 置 0 很常见；“对称性破缺”主要靠随机权重完成（如果所有权重都一样才会严重对称）。
- 其他：Orthogonal 初始化、对 LayerNorm/Embedding 的专用初始化策略等（通常按论文/框架惯例）。

## 5. 在哪些模型里出现
- 基本上所有深度网络（MLP/CNN/Transformer）都有初始化；差异主要在：线性/卷积的 `fan_*` 定义、以及是否需要为特定模块（LN/Embedding/Residual）采用特殊规则。

## 6. 速查
- 关键词：`fan_in`、`fan_out`、`Var`、`gain`、Xavier/Glorot、He/Kaiming
- PyTorch：`nn.init.xavier_normal_`、`nn.init.xavier_uniform_`、`nn.init.kaiming_normal_`、`nn.init.calculate_gain`
- 小算例：`Linear(3,2)` 用 `xavier_normal_` 且 `gain=sqrt(2)` 时，`std = sqrt(2)·sqrt(2/5)=sqrt(4/5)≈0.894`（量级对，但每次采样不同）
