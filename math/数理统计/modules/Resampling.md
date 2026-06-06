---
title: 重抽样：Jackknife / Bootstrap / 留一交叉验证（Resampling）
date: "2026-03-30"
categories:
  - math
description: Jackknife、Bootstrap 和 LOOCV 都会“反复重做计算”，但目标完全不同：前两者主要估计统计量不确定度，后者主要评估模型泛化误差。
---
# 重抽样：Jackknife / Bootstrap / 留一交叉验证（Resampling）

## 1. 一句话
- 它们都长得像“重复抽子样本再重算一次”，但 Jackknife / Bootstrap 主要服务于统计推断，LOOCV 主要服务于模型评估。

## 2. 三个名字先分清
- Jackknife：
  - 典型做法是 delete-1，即每次删掉 1 个样本，重算一次估计量
  - 目标通常是近似 estimator 的 bias / variance
- Bootstrap：
  - 从原样本中“有放回”重抽大小仍为 `n` 的样本，重复很多次
  - 目标通常是近似 estimator 的抽样分布、标准误、置信区间
- LOOCV（留一交叉验证）：
  - 每次留 1 个样本做验证，其余 `n-1` 个做训练
  - 目标通常是估计模型的泛化误差，不是总体参数 `μ`、`σ` 的标准误

## 3. Jackknife（删一法）是什么
- 设原始估计量为：
  - `T = T(X_1,...,X_n)`
- 删除第 `i` 个样本后重算：
  - `T_(i) = T(X_1,...,X_{i-1},X_{i+1},...,X_n)`
- 定义删一平均：
  - `bar T_(.) = (1/n) sum_{i=1}^n T_(i)`

常见的 Jackknife 方差估计：
- `hat Var_jack(T) = ((n-1)/n) sum_{i=1}^n (T_(i) - bar T_(.))^2`

常见的 Jackknife bias 估计：
- `hat Bias_jack(T) = (n-1)(bar T_(.) - T)`

## 4. Jackknife 适合什么
- 估计量比较“平滑”时，Jackknife 往往很好用
- 你想低成本看一个 estimator 对单个样本有多敏感
- 你不想做很多次 bootstrap，只想先要一个快速近似

## 5. Jackknife 的限制
- 对不平滑统计量（如某些分位数、极值类统计量）可能不稳定
- 它本质上是局部近似，不如 bootstrap 通用
- 它和“模型训练时留一验证”长得像，但用途不同

## 6. Bootstrap（自助法）是什么
- 重复 `B` 次：
  - 从原样本 `{X_1,...,X_n}` 中有放回抽取一个 bootstrap 样本 `{X_1^*,...,X_n^*}`
  - 计算对应统计量 `T^{*(b)}`
- 得到一组 bootstrap 复制值：
  - `T^{*(1)},...,T^{*(B)}`

Bootstrap 标准误常写为：
- `hat SE_boot(T) = sqrt((1/(B-1)) sum_{b=1}^B (T^{*(b)} - bar T^*)^2)`

其中：
- `bar T^* = (1/B) sum_{b=1}^B T^{*(b)}`

Bootstrap 百分位数置信区间常直接取：
- `[(α/2)-quantile of {T^{*(b)}}, (1-α/2)-quantile of {T^{*(b)}}]`

## 7. Bootstrap 适合什么
- 统计量复杂，难以手推标准误或区间
- 想估计中位数、分位数、相关系数、复杂模型指标
- 想直接用“样本自己近似总体”来构造统计量分布

## 8. Bootstrap 的限制
- 小样本下可能不稳，尤其样本代表性差时
- 对强依赖数据（时间序列、空间数据）不能直接用普通 iid bootstrap
- 计算成本比 Jackknife 高得多

## 9. LOOCV 为什么不一样
- LOOCV 的输出通常是平均预测误差、平均验证损失、分类错误率等
- 它回答的是：
  - “这个模型如果见到新样本，预测会怎样？”
- 它不直接回答：
  - “我对 `μ` 的估计有多不确定？”
  - “我对 `σ` 的估计标准误是多少？”
- 所以看到“留一法”时，必须先分清上下文

## 10. 你这个问题里该怎么选
- 如果你只是想知道一个 batch 估计 `μ` 的不确定度：
  - 先看 `SE(bar X) = S / sqrt(n)`
- 如果你想给 `μ`、`σ^2` 一个区间：
  - 先看解析公式；正态假设下尤其方便
- 如果你估计的是复杂统计量，或者想少依赖分布假设：
  - 用 bootstrap
- 如果你想快速看 estimator 对单个样本是否敏感：
  - 用 jackknife
- 如果你是在比较模型效果：
  - 用交叉验证；LOOCV 属于这一类

## 11. 常见混淆
- 把统计学里的 bootstrap 和强化学习里的 bootstrapping 混为一谈
- 把 Jackknife 直接叫成 LOO，却没说清是在做 variance estimation 还是 validation
- 以为“多做几次子样本”就都在估计同一种东西

## 12. 相关入口
- 点估计与标准误：见 [math/数理统计/modules/PointEstimation.md](PointEstimation.md)
- 概率基础：见 [math/概率论/README.md](../../概率论/README.md)
