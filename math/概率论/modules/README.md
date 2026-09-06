---
title: modules（工具箱）
date: "2026-01-15"
categories:
  - math
description: 建议放：期望/方差、不等式、条件概率/条件期望、独立性、常用收敛与极限定理（按“可复用证明套路/结论清单”写）。
---
# modules（工具箱）

- 建议放：期望/方差、不等式、条件概率/条件期望、独立性、常用收敛与极限定理（按“可复用证明套路/结论清单”写）。

## 已有模块

- [期望（Expectation）](Expectation.md)：随机变量的加权平均，含离散、连续和测度论层次。
- [矩与数值特征（Moments and Numerical Measures）](MomentsMeasures.md)：方差、条件方差、协方差、相关系数、高阶矩和常用不等式。
- [条件概率与链式法则](ChainRule.md)：条件概率、链式法则和 Bayes 公式。
- [条件期望](ConditionalExpectation.md)：条件期望作为随机变量和最优预测。
- [嵌套期望与全期望公式](IteratedExpectation.md)：Tower property、Fubini 和嵌套采样。
- [独立性与条件独立](Independence.md)：独立、条件独立和常用等价形式。

## 连续时间随机过程主线

- [随机微分方程（SDE）主线](SDE.md)：主入口。按“单粒子 SDE → 转移核 → Fokker-Planck → PF-ODE”的顺序串起来。
- [随机积分附录](StochasticIntegral.md)：只在需要细看 `∫dW`、`∫g(s)dW_s` 和简单 SDE 积分形式时打开。
- [SDE 到 Fokker-Planck 附录](SDEToFokkerPlanck.md)：只在需要转移核、Dirac delta、测试函数/弱形式的不跳步推导时打开。
