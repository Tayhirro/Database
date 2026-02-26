---
title: "潜变量→数据映射（Latent-to-Data Mapping）/ 推前分布（Pushforward）"
date: "2026-01-13"
categories:
  - 神经网络
description: 你问的“低维 z 经过（通常非线性）变换变成高维 x，但 x 仍落在低维结构附近”，在不同语境里常见名字是： VAE/生成模型里：decoder mapping / generator（解码器/生成器映射）g_θ 概率/测度论里：pushforward（推前）/ induced distrib...
---
# 潜变量→数据映射（Latent-to-Data Mapping）/ 推前分布（Pushforward）

## 1. 这件事“叫什么”
你问的“低维 `z` 经过（通常非线性）变换变成高维 `x`，但 `x` 仍落在低维结构附近”，在不同语境里常见名字是：
- VAE/生成模型里：decoder mapping / generator（解码器/生成器映射）`g_θ`
- 概率/测度论里：pushforward（推前）/ induced distribution（诱导分布）
- 几何/流形视角里：parameterization（参数化），更强条件下叫 immersion（浸入）/ embedding（嵌入）

## 2. 最核心的一句话（为什么“简单正态”也能变复杂）
设 `z ~ p(z)`（VAE 里常取 `N(0,I)`），定义确定性映射 `x = g_θ(z)`。那么 `x` 的分布就是：
- `p_x = (g_θ)_# p_z`（读作：`p_z` 在 `g_θ` 下的推前）

这句话精确表达了：**一个简单的高斯，经过非线性映射的 pushforward，就能变成很复杂的分布形状**。

## 3. “高维但非满秩/在流形上”是什么意思
把 `g: R^k -> R^D` 看成用 `z` 参数化高维点 `x`：
- `M = { g(z) : z ∈ R^k }`

如果 `g` 足够光滑，且其 Jacobian `J_g(z)` 在（几乎）处处满秩 `k`，那么 `M` 在局部就是一张 `k` 维的“曲面/流形”（嵌在 `R^D` 里）。  
因此看起来 `x` 的各维高度相关、整体“非满秩”，本质上是：**自由度来自 `z` 的 `k` 维参数**。

## 4. 为什么它不一定是“零厚度流形”（VAE vs GAN 的关键差异）
如果是纯确定映射 `x=g(z)`（更像 GAN 的生成路径），分布严格落在一张 `k` 维集合上（在 `R^D` 里是“很薄”的）。

但 VAE 通常是：
- `x ~ p_θ(x|z)`，例如 `p_θ(x|z)=N(μ_θ(z), Σ_θ(z))`

这意味着：
- 均值 `μ_θ(z)` 可以理解为“流形中心线/中心面”
- `Σ_θ(z)` 让分布在该结构周围有“厚度”（像细管/薄壳），不再是严格零厚度

## 5. 另一个视角：VAE 的边缘分布是“连续混合”
VAE 的边缘似然：
- `p_θ(x) = ∫ p_θ(x|z) p(z) dz`

即使 `p(z)` 很简单，只要 `p_θ(x|z)` 的参数（`μ_θ(z), Σ_θ(z)`）随 `z` 非线性变化，这个积分就相当于一个非常灵活的“无限混合模型”。

## 6. 10 秒玩具例子（1D 正态 → 3D 曲线）
取 `z ~ N(0,1)`，定义
- `g(z) = (z, z^2, z^3) ∈ R^3`

那么 `x=g(z)` 落在 `R^3` 里的一条弯曲曲线（1 维结构）上；如果再加噪声
- `x ~ N(g(z), σ^2 I)`

它就从“细线”变成“细管”。

## 7. 相关页
- 降维/潜变量总览：[modules/DimensionalityReduction.md](DimensionalityReduction.md)
- VAE：[models/VAE.md](../models/VAE.md)
- ELBO（为什么要 KL、为什么要对齐先验）：[modules/ELBO.md](ELBO.md)

