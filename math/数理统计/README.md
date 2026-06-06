---
title: "数理统计（Mathematical Statistics）笔记组织说明（估计 / 推断）"
date: "2026-03-30"
categories:
  - math
description: 导航：math/README.md ｜math/数学索引.md ｜本分支：math/数理统计/索引.md ｜math/数理统计/概念图.md
---
# 数理统计（Mathematical Statistics）笔记组织说明（估计 / 推断）

导航：[math/README.md](../README.md) ｜[math/数学索引.md](../数学索引.md) ｜本分支：[math/数理统计/索引.md](索引.md) ｜[math/数理统计/概念图.md](概念图.md)

这部分关心的是：给你一批样本 `X_1,...,X_n`，怎样去估计总体里的参数（例如 `μ`、`σ^2`），以及这些估计本身有多不确定。

- 概率论更像“已知分布/机制，推出数据会怎样”。
- 数理统计更像“已知数据样本，反推分布参数/泛化误差会怎样”。

## 目录结构（入口 → 索引 → 概念图 → 模块）
- [math/数理统计/README.md](README.md)：入口与组织方式（本页）
- [math/数理统计/索引.md](索引.md)：术语索引（中文｜英文｜一句话｜链接）
- [math/数理统计/概念图.md](概念图.md)：概念关系图（样本 → 估计量 → 不确定度）

模块（先补最常用的两块）：
- [math/数理统计/modules/PointEstimation.md](modules/PointEstimation.md)：点估计、标准误、置信区间、`μ/σ^2` 的 batch 估计
- [math/数理统计/modules/Resampling.md](modules/Resampling.md)：Jackknife、Bootstrap、留一交叉验证（LOOCV）的区别

## 建议学习路线
- 先把一个 batch 看成总体分布抽出来的一个样本：`X_1,...,X_n`
- 再区分两种“波动”：
  - 数据本身的离散程度：`Var(X)`
  - 估计量本身的波动：例如 `Var(\bar X)`、标准误 `SE`
- 对简单统计量（样本均值、样本方差），优先记解析公式
- 对复杂统计量（分位数、黑盒模型指标、难推导统计量），优先想到 bootstrap
- 看到“留一法”时先问清楚：是在做参数不确定度估计，还是在做模型泛化误差评估
