---
title: "原型学习（Prototypical Networks / Prototype-based Few-shot）"
date: ""
categories:
  - 神经网络
description: "在表征空间里用 support（K-shot） 样本为每个类计算一个（或多个）“原型”向量，query 样本按“离哪个原型最近”来分类（或输出条件分布）。"
---
# 原型学习（Prototypical Networks / Prototype-based Few-shot）

## 1. 一句话
- 在表征空间里用 **support（K-shot）** 样本为每个类计算一个（或多个）“原型”向量，**query** 样本按“离哪个原型最近”来分类（或输出条件分布）。

## 2. 定义 / 公式（最常用写法）
- 编码器：`f_θ(x) -> h`（把输入映射到 embedding）
- 一个 episode（N-way K-shot）：
  - support set `S = {(x_i, y_i)}`：每类 `K` 个带标签样本（用来算原型）
  - query set `Q = {(x_j, y_j)}`：用来做预测并计算 loss 的样本
- 第 `k` 类原型（类中心）：
  - `c_k = mean_{(x_i,y_i)=k} f_θ(x_i)`
- 对 query `x` 的分类（用距离/相似度做 softmax）：
  - `p(y=k | x, S) = softmax_k( - d(f_θ(x), c_k) )`
  - 常见 `d`：平方欧氏距离；或 cosine 相似度（再配温度 `τ`）
- 训练：对 query 做交叉熵 `CE(y, p(y|x,S))`，反向更新 `θ`

## 3. `N-way / K-shot / support / query` 是什么（举例）
- `3-way 2-shot 3-query`：
  - 3 个类别（way=3）：猫/狗/马
  - support（2-shot）：每类给 2 张带标签图片，用它们的 embedding 均值当原型 `c_cat/c_dog/c_horse`
  - query（3-query）：每类再给 3 张（训练时也有 GT），用原型去分类它们并算 loss

## 4. 直觉（为什么有效）
- 它本质上是“最近类中心（Nearest Class Mean）”分类器，但把“embedding 空间”学到对 few-shot 更友好：同类更紧、不同类更分离。
- episodic 训练让“训练时的任务形式”与“推理时的 few-shot 形式”对齐（train/infer mismatch 更小）。

## 5. 常用变体 / 记号差异
- 距离 vs 相似度：`-||h-c||^2`（欧氏）或 `cos(h,c)/τ`（cosine + 温度）
- 单原型 vs 多原型：每类一个中心（简单）或每类多个中心（多模态类、更复杂决策边界）
- transductive：利用 query 的分布（不看 label）来细化原型/边界（更像“带监督的聚类 + 原型分类”）

## 6. 和对比学习 / 聚类的关系（你问到的“算不算一类”“区别”）
- 对比学习：更像训练目标/范式（InfoNCE/Triplet…），原型学习：更像“用原型做推断”的 few-shot 结构；两者都属于度量/表示学习范畴，且可以互相改写（把本类原型当正例、其他类原型当负例）。
- 聚类：通常无标签，目标是自动分组；原型学习通常有标签（support 的 GT），原型对应“语义类别代表”，不是“数据自己分出来的簇”。

## 7. 速查
- 关键词：`N-way K-shot`、support/query、episode、prototype、nearest centroid、few-shot、metric learning
- 常见坑：
  - support/query 泄漏（把 query 误用来算原型，或训练/测试类混淆）
  - 距离度量与归一化不一致（cosine 记得 L2 normalize / 温度）
  - 类内方差很大时单原型不足（考虑多原型/更强 encoder）

相关页：
- 度量学习：`modules/representation/MetricLearning.md`
- 对比学习：`modules/representation/ContrastiveLearning.md`

