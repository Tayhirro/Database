---
title: GRPO（Group Relative Policy Optimization）
date: "2026-03-07"
categories:
  - 神经网络
description: 基于同题多样本组内相对优势的策略优化方法，常见于大模型后训练场景；核心是“相对比较”而非绝对分数。
---
# GRPO（Group Relative Policy Optimization）

## 1. 一句话
- GRPO 的核心想法是：同一个 prompt 采样一组候选回答，按组内相对表现给优势，再做策略更新。

## 2. 背景（为什么会有它）
- 在大模型后训练里，单样本奖励噪声大、尺度漂移明显。
- 绝对 reward 难以稳定比较；组内相对排序更稳。

## 3. 典型流程（抽象）
1. 对同一输入 `x` 采样 `K` 个回答 `y_1,...,y_K`。
2. 用 reward model 或规则打分 `r_i`。
3. 在组内做标准化/中心化得到相对优势 `\hat A_i`（如减均值再除标准差）。
4. 用 `\log \pi_\theta(y_i|x)` 与 `\hat A_i` 做策略梯度更新。

## 4. 直觉
- “和同题队友比”，比“和全局历史分布比”更稳定。
- 组内标准化后，训练更关注相对优劣，减弱奖励尺度抖动。

## 5. 与 PPO 的关系（实用视角）
- 相同点：都属于策略优化范式，目标是提高高优势行为概率。
- 不同点：
  - PPO 常依赖 value function + GAE（经典 RL 设定）。
  - GRPO 常在 LLM 后训练里直接用组内相对优势，不一定显式训练 value 网络。

## 6. 常见坑
- 组大小 `K` 太小：相对排名不稳定。
- 奖励模型偏置：会系统性误导策略。
- 只看组内排名，忽略跨任务校准，可能出现“局部最优答案风格”。

## 7. 什么时候用
- 你在做 LLM 后训练（SFT 之后的 RL 阶段），且 reward 噪声较大、任务以生成质量比较为主时。

## 8. 关联模块
- [../../modules/PolicyGradient.md](../../modules/PolicyGradient.md)
- [../../modules/ValueFunction.md](../../modules/ValueFunction.md)
- [PPO.md](PPO.md)
