---
title: 广义优势估计（GAE）
date: "2026-03-07"
categories:
  - 神经网络
description: 在偏差与方差间折中地估计优势函数，是 PPO 中最常见的优势估计方式。
---
# 广义优势估计（GAE）

## 1. 一句话
- GAE 用 `\lambda` 在“低偏差高方差”和“高偏差低方差”之间做平衡，得到更稳的优势估计。

## 2. 定义 / 公式（最常用那版）
- TD 残差：

$$
\delta_t = r_t + \gamma V(s_{t+1}) - V(s_t)
$$

- GAE：

$$
\hat A_t^{GAE(\gamma,\lambda)} = \sum_{l=0}^{\infty}(\gamma\lambda)^l\delta_{t+l}
$$

- 当 `\lambda=0` 接近 1-step TD；`\lambda \to 1` 更接近 Monte Carlo。

## 3. 直觉（为什么这么设计）
- 你可以把 GAE 看成“对未来 TD 残差做指数加权求和”。
- `\lambda` 越大，看的步数越长，方差也通常更大。

## 4. 常用变体 / 记号差异
- 实现时通常会从后往前递推，避免显式无穷和。
- 工程上常搭配 advantage normalization。

## 5. 在哪些模型里出现
- PPO 基本标配；A2C/A3C 也常用其思想。

## 6. 速查
- 关键词：TD residual、bias-variance tradeoff、`\lambda`。
- 常见坑：
  - 回报截断处理不当（episode done 时未正确 bootstrap）
  - `gamma/lambda` 选择不当导致学习过慢或不稳
  - 未处理 value 估计偏移，GAE 会系统性带偏
