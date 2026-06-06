---
title: 度量学习（Metric Learning）
date: "2026-03-25"
categories:
  - 神经网络
description: 学习一个任务相关的距离/相似度，使同类样本在表征空间中更近、异类样本更远，是检索、人脸识别、行人重识别、few-shot 等任务的常见底座。
---
# 度量学习（Metric Learning）

## 1. 一句话
- 度量学习的核心目标是：**让模型学会“什么叫相似”**，使同类样本在表征空间里更近、异类样本更远。

## 2. 基本概念（固定距离 vs 学习距离）
- 传统方法通常直接用固定距离：
  - 欧氏距离 `||x_i-x_j||_2`
  - 余弦相似度 `cos(x_i, x_j)`
- 但固定距离不一定适合任务本身。例如在人脸识别里，像素空间的欧氏距离通常没什么语义意义。
- 度量学习的做法是：先学一个映射，再在新空间里计算距离。

### 2.1 线性形式（Mahalanobis / 线性变换）
- 常见写法是学习一个线性变换 `L`：

$$
d(x_i, x_j)=\|L(x_i-x_j)\|_2^2
$$

- 等价地，也可以写成 Mahalanobis 距离：

$$
d_M(x_i, x_j)=(x_i-x_j)^T M (x_i-x_j), \quad M=L^TL \succeq 0
$$

- 这里 `M` 是一个半正定矩阵，表示“哪些方向更重要，哪些方向该被压缩”。

### 2.2 深度形式（神经网络 embedding）
- 在深度学习里，更常见的是直接学习非线性映射 `f_\theta(x)`：

$$
h_i=f_\theta(x_i), \quad d(x_i,x_j)=\|h_i-h_j\|_2^2
$$

- 也常见 cosine 相似度：

$$
s(x_i,x_j)=\frac{h_i^T h_j}{\|h_i\|_2\|h_j\|_2}
$$

- 本质没变：不是手工定义“什么叫近”，而是让模型自己把样本排布到更适合任务的空间里。

## 3. 核心思想（同类近、异类远、留 margin）

| 原则 | 说明 |
| --- | --- |
| 同类相近 | 同一类别或同一语义实例的样本，在 embedding 空间里尽量靠近 |
| 异类相远 | 不同类别或语义不一致的样本，尽量拉开 |
| 边界约束 | 往往不只要求“正例比负例近”，还要求至少近/远一个 margin |

- 所以度量学习经常不是直接学类别概率，而是在学一种**相对排序关系**：

$$
d(a,p) + m < d(a,n)
$$

- 这里：
  - `a` 是 anchor
  - `p` 是 positive
  - `n` 是 negative
  - `m` 是 margin

## 4. 训练单位（pair / triplet / center / class prototype）
- 度量学习不一定只有一种监督形式，常见训练单位有四类：
- **pair**：一对样本，告诉模型“像 / 不像”
- **triplet**：三元组 `(anchor, positive, negative)`，告诉模型“正例必须比负例更近”
- **center / prototype**：告诉模型“样本要靠近本类中心，远离他类中心”
- **classification with margin**：还是分类框架，但在角度或余弦空间上强制类间分离

## 5. 常见方法

### 5.1 Siamese Network（孪生网络）
- 两个输入共享同一个编码器 `f_\theta`，分别得到 embedding，再计算距离或相似度。
- 它本身更像一种**网络结构**；真正决定训练行为的是损失函数。
- 最经典的是 contrastive loss：

$$
L
=
y \cdot d(h_i,h_j)^2
+
(1-y)\cdot \max(0, m-d(h_i,h_j))^2
$$

- 其中：
  - `y=1` 表示正样本对，希望距离小
  - `y=0` 表示负样本对，希望距离至少大于 `m`

### 5.2 Triplet Network（Triplet Loss）
- 输入三元组：
  - anchor `a`
  - positive `p`
  - negative `n`
- 目标是让：

$$
d(a,p) + m < d(a,n)
$$

- 常见损失：

$$
L = \max(0, d(a,p)-d(a,n)+m)
$$

- 它很直观，因为优化目标直接对应“排序正确”。
- 缺点是对采样策略很敏感：
  - triplet 太容易，loss 常常是 0
  - triplet 太难，训练不稳定

### 5.3 Center Loss
- 给每个类别维护一个中心向量 `c_k`，让样本靠近自己的类中心：

$$
L_{center}=\frac{1}{2}\sum_i \|h_i-c_{y_i}\|_2^2
$$

- 实际中常与 softmax 交叉熵一起用：

$$
L = L_{ce} + \lambda L_{center}
$$

- 直觉：
  - softmax 主要做“分开不同类”
  - center loss 主要做“压紧类内分布”

### 5.4 ArcFace / CosFace（角度边际）
- 这类方法在人脸识别里很常见。
- 核心不是只看“分错/分对”，而是进一步在角度空间里加入 margin，增强类间可分性。
- 典型思想：
  - 特征 `h` 和分类器权重 `W` 都做归一化
  - logit 由余弦相似度决定
  - 对真实类别额外减去一个角度边际或余弦边际
- 结果是：
  - 同类不只是“被分类对”
  - 而是被压到更紧、更稳定的角度簇里

### 5.5 Prototypical Networks（原型学习）
- 原型学习可以看作度量学习在 few-shot 里的一个非常自然的落地：
  - 先算每个类的 prototype / 类中心
  - 再按离哪个原型最近来分类
- 详细见：[PrototypicalLearning.md](PrototypicalLearning.md)

## 6. 和对比学习（Contrastive Learning）的关系
- 两者很近，但不完全一样。

### 6.1 共同点
- 都在学 embedding 空间
- 都强调“语义一致的更近，不一致的更远”
- 都常使用 positive / negative、相似度、温度、margin 等概念

### 6.2 区别
- **度量学习** 更像一个大范畴：
  - 关注“学什么距离 / 表征空间”
  - 往往更监督、更任务导向（检索、人脸、ReID、few-shot）
- **对比学习** 更像一种训练范式 / 目标族：
  - 典型目标是 InfoNCE、NT-Xent、SupCon
  - 可以是监督，也可以是自监督
  - 常强调大 batch、负样本、数据增强、多视图一致性

一句话记：
- **Metric Learning 更强调距离结构**
- **Contrastive Learning 更强调训练目标和构造正负样本的方式**

相关页：[ContrastiveLearning.md](ContrastiveLearning.md)

## 7. 应用场景

| 领域 | 典型用途 |
| --- | --- |
| 人脸识别 | 学人脸 embedding，同人更近、不同人更远 |
| 图像检索 | 给查询图找最近邻结果 |
| 行人重识别 | 跨摄像头匹配同一个人 |
| 推荐系统 | 学用户-物品 embedding，匹配偏好与语义相似度 |
| few-shot 学习 | 用距离代替大样本分类器 |
| 因果表示学习 | 用额外关系标注约束 latent 空间结构 |

## 8. 和你之前提到的“因果标注正则 latent”有什么关系

> [!note]
> 你之前那句 “introduce a metric learning approach that regularizes latent representations with causal annotations” 的意思，基本可以理解成：**把因果标注变成 metric learning 的监督信号**，去约束潜在空间的几何结构。

- 一个常见做法是：
  - 如果两个智能体 / 状态 / 事件之间存在因果关系，就把它们视为“正例对”
  - 如果不存在因果关系，或只是统计相关但非因果，就把它们视为“负例对”或 harder negative
- 然后在 latent space 上加一个度量学习损失，例如：

$$
L = L_{task} + \lambda L_{metric}
$$

- 其中 `L_metric` 可以是：
  - pairwise contrastive loss
  - triplet loss
  - prototype / center-based regularization

- 直觉上它在做的事是：
  - 不让模型只记住“谁经常一起出现”
  - 而是逼它把“真正存在因果依赖的结构”编码进表示空间

- 这样带来的好处通常是：
  - 对分布变化更稳
  - 对伪相关（spurious correlation）依赖更少
  - 更容易迁移到新的环境或新的交互组合

## 9. 直观比喻
- 想象你在整理书架：
  - 固定距离：按封面颜色排
  - 度量学习：学会按主题内容排
  - 加因果标注的度量学习：不只看“经常一起被借”，而是看“内容上真的有因果或依赖关系”

## 10. 常见坑 & Debug 清单
- 正负样本构造太随意：模型学到的只是数据采样偏差，不是语义距离
- 难样本挖掘（hard negative mining）不合理：太简单没信号，太难会炸训练
- 训练时用欧氏，推理时用 cosine：度量不一致，检索/分类效果会飘
- embedding 没归一化：尤其在人脸识别、检索里经常导致距离尺度不稳
- margin 设得不合适：
  - 太小：拉不开
  - 太大：训练经常无解或不稳定
- 类内多模态很强却只用单中心：容易把不同子模式硬挤在一起

## 11. 和相关页面怎么分工
- 如果你想看“InfoNCE / NT-Xent / MoCo / SimCLR / CLIP”，去看：[ContrastiveLearning.md](ContrastiveLearning.md)
- 如果你想看“few-shot 下用类原型做推断”，去看：[PrototypicalLearning.md](PrototypicalLearning.md)
- 如果你想搞清楚这里说的“因果关系”在 Pearl 体系里到底是怎么定义的，先看：[../../../因果推断/structures/StructuralCausalModel.md](../../../因果推断/structures/StructuralCausalModel.md)
- 如果你想看“度量学习到底在学什么距离，以及 Siamese / Triplet / Center / ArcFace 是怎么回事”，这页就是总入口

## 12. 参考
- Chopra et al., 2005. *Learning a Similarity Metric Discriminatively, with Application to Face Verification*
- Hadsell et al., 2006. *Dimensionality Reduction by Learning an Invariant Mapping*
- Schroff et al., 2015. *FaceNet: A Unified Embedding for Face Recognition and Clustering*
- Wen et al., 2016. *A Discriminative Feature Learning Approach for Deep Face Recognition* (Center Loss)
- Deng et al., 2019. *ArcFace: Additive Angular Margin Loss for Deep Face Recognition*
