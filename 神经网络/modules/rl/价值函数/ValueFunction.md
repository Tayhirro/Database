---
title: 价值函数（Value Function）
date: "2026-05-18"
categories:
  - 神经网络
description: 状态价值函数 V、动作价值函数 Q、优势函数 A 的定义与关系。
---
# 价值函数（Value Function）

## 1. 一句话
价值函数回答：从现在开始，按某个策略走，未来期望能拿多少回报。

## 2. V、Q、A 定义

### 状态价值函数 V(s)
从状态 s 出发，按策略 pi 行动，期望累积回报：

$$
V^{\pi}(s) = \mathbb{E}_{\pi}[G_t \mid s_t = s]
$$

### 动作价值函数 Q(s,a)
在状态 s 选动作 a，之后按策略 pi 行动，期望累积回报：

$$
Q^{\pi}(s,a) = \mathbb{E}_{\pi}[G_t \mid s_t = s, a_t = a]
$$

### 优势函数 A(s,a)
当前动作比平均水平好多少：

$$
A^{\pi}(s,a) = Q^{\pi}(s,a) - V^{\pi}(s)
$$

### 三者关系
- V 是对所有动作的 Q 按策略平均：$V(s) = \sum_a \pi(a|s) Q(s,a)$
- A(s,a) > 0：这个动作比平均好
- A(s,a) < 0：这个动作比平均差

## 3. 为什么用 A 而不是 Q

策略梯度直接用 Q 也行，但可以减 baseline 降方差：

$$
\nabla_\theta J(\theta) = \mathbb{E}_\pi[\nabla_\theta \log \pi_\theta(a|s) \cdot (Q(s,a) - b(s))]
$$

取 b(s) = V(s) 就是 A(s,a)。减去与动作无关的 baseline 不改变梯度期望，但方差更小。

## 4. 相关文件
- [BellmanEquation.md](BellmanEquation.md)：Bellman 期望方程与最优方程

## 5. 速查
- 关键词：V、Q、A、baseline、advantage
- 常见坑：
  - V 和 Q 都依赖策略 pi，换了策略要重新估计
  - A 能直接告诉"这个动作相对好不好"，比原始回报更稳
