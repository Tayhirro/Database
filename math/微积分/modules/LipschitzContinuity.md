---
title: "Lipschitz 连续性（Lipschitz Continuity）"
date: "2026-03-30"
categories:
  - math
description: Lipschitz 连续要求函数变化速度被一个线性常数统一控制；它比连续更强，常用于 ODE 唯一性、优化稳定性和神经网络中的 Lipschitz 约束。
---
# Lipschitz 连续性（Lipschitz Continuity）

## 1. 一句话
- Lipschitz 连续要求“输入改一点，输出最多按某个固定倍数改动”。

## 2. 定义
- 设 `f : D -> R^m`，若存在常数 `L >= 0`，使得对任意 `x,y in D` 都有
  - `||f(x)-f(y)|| <= L ||x-y||`
- 则称 `f` 在 `D` 上是 `L`-Lipschitz 的，或称 `f` 在 `D` 上 Lipschitz 连续。
- 这个 `L` 可以理解为“全局变化率上界”。

## 3. 与连续、可导的关系
- `Lipschitz => 一致连续 => 连续`
- 反过来一般不成立：连续函数未必 Lipschitz。
- 一元情形下，若 `f` 可导且在区间上满足 `|f'(x)| <= M`，则 `f` 是 `M`-Lipschitz。
- 多元情形下，若 `f` 可微且在凸域上满足 `||∇f(x)|| <= M`（或更一般地 Jacobian 算子范数有界），则 `f` 是 `M`-Lipschitz。

## 4. 常见例子
- `f(x)=ax+b` 是 `|a|`-Lipschitz。
- `f(x)=sin x` 是 `1`-Lipschitz，因为 `|cos x| <= 1`。
- `f(x)=x^2` 在有界区间 `[-R,R]` 上是 `2R`-Lipschitz，但在整个 `R` 上不是 Lipschitz。
- `f(x)=sqrt(x)` 在 `[0,1]` 上连续，但不是 Lipschitz（因为在 `0` 附近变化太陡）。

## 5. 为什么重要
- ODE：Picard-Lindelof 唯一性定理常要求右端项对状态变量满足 Lipschitz 条件。
- 优化：若 `∇f` 是 Lipschitz 连续的，常称 `f` 是 smooth 的，这能给出步长与收敛分析。
- 神经网络：WGAN 里的 critic 需要满足 `1`-Lipschitz 约束，因此会用 weight clipping、gradient penalty 或谱归一化来近似控制。

## 6. 常见坑
- 把“连续”误当成“Lipschitz 连续”。
- 只看局部斜率上界，却直接下结论说“全局 Lipschitz”。
- 忽略定义域：有些函数只在有界区间上 Lipschitz，离开该区间就不成立。

## 7. 相关入口
- 极限与连续：见 [math/微积分/modules/Limits.md](Limits.md)
- 导数与微分：见 [math/微积分/modules/Differentiation.md](Differentiation.md)
- 常微分方程：见 [math/微积分/modules/ODE.md](ODE.md)
