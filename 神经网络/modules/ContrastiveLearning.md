# 对比学习（Contrastive Learning / InfoNCE）

## 1. 一句话
- 通过“拉近正样本、推远负样本”来学习表征，CLIP 训练的核心目标之一。

## 2. 基本设定（anchor / positive / negative）
典型对比学习的最小要素：
- 编码器 `f(·)`：把输入映射到表征向量 `h`
- （可选）投影头 `g(·)`：把 `h` 映射到对比空间 `z`（很多方法用 `z` 做对比、用 `h` 做下游）
- anchor `a`：当前要“对齐”的样本
- positive `p`：和 anchor 语义一致的样本（同一实例的另一种增强、同一图文对、同类样本等）
- negative `n`：和 anchor 不一致的样本（通常来自同 batch 的其他样本）

## 3. 相似度、归一化与温度（temperature）
常见做法：
- 相似度 `s(u,v)`：dot-product 或 cosine
- L2 归一化：`u ← u / ||u||`、`v ← v / ||v||`（此时 dot-product 等价 cosine）
- 温度 `τ`：控制 softmax “尖锐程度”；CLIP 里常写成可学习的 `logit_scale = 1/τ`

直觉：
- `τ` 小 → 分布更尖锐、梯度更集中（更“挑剔”），但数值不稳/过拟合风险更大
- `τ` 大 → 分布更平滑，训练更稳但可能不够区分

## 4. InfoNCE / NT-Xent（batch 内负样本）
单向（以 anchor `i` 的正例是 `i` 为例）：
- `L_i = -log ( exp(s(i,i)/τ) / Σ_j exp(s(i,j)/τ) )`

常见的对称形式（双向平均）：
- 图文对齐（CLIP）：image→text + text→image
- 视图对齐（SimCLR）：view1→view2 + view2→view1

实现等价视角：
- 把“在一堆候选里找正例”看成一个 `N` 类分类问题，直接用交叉熵（logits 就是相似度矩阵/温度缩放）

## 5. 负样本来源（in-batch / memory bank / queue）
### 5.1 In-batch negatives
- 最常见：一个 batch 里除了正例之外的样本都当负例
- 分布式训练时要确认：是否把“其他 GPU 的样本”也纳入负样本（all-gather）

### 5.2 Memory bank / Queue（MoCo 思路）
- 用队列缓存历史 batch 的表征，扩大负样本数量
- 常配一个 momentum encoder（动量更新的编码器）来稳定队列特征

### 5.3 Hard negatives 与 false negatives
- hard negative：与 anchor 很像但其实不同类（更有训练信号）
- false negative：在无监督/弱监督场景里“其实应该算正例却被当成负例”的样本（会伤害表示）
  - 常见缓解：更强数据增强、多正例（multi-positive）、去偏/重加权（debiased）等

## 6. 常见方法谱系（你可以在索引里直接挂名）
- SimCLR：强增强 + 大 batch（in-batch negatives）+ projection head
- MoCo：queue + momentum encoder（解决大 batch 成本）
- SupCon（Supervised Contrastive）：同一类别的样本都可作正例（多正例）
- CLIP：跨模态双塔（image/text）+ 相似度矩阵 + 双向交叉熵

## 7. 相关概念：Metric Learning / Triplet / 非对比自监督
### 7.1 Triplet loss（度量学习常见）
- 用距离 `d(·,·)` 与 margin `m`：
  - `L = max(0, d(a,p) - d(a,n) + m)`
- 更像“排序/间隔约束”，而 InfoNCE 更像“带温度的多类分类”（softmax over candidates）。

### 7.2 非对比（no-negatives）的自监督（相关但不同路子）
- BYOL / SimSiam 等方法不显式用负样本，通常用 stop-grad、predictor、动量分支等机制避免塌缩（representation collapse）。

## 8. 常见坑 & Debug 清单（对比学习专用）
- 表征未做 L2 normalize（或训练/推理不一致）
- 温度/`logit_scale` 数值异常（过大导致过尖、过小导致区分不够）
- 分布式未 all-gather，等价于负样本数量骤减
- 正例配对错位（尤其是 dataloader shuffle、batch 拼接、多卡数据切分）
- 对比用的是 `h` 还是 `z` 搞混（projection head 前后不一致）

## 9. 在 CLIP 里怎么用
- 对一个 batch 的图文对，构造相似度矩阵，然后做交叉熵分类（正例是对角线）。

相关页：
- CLIP：`models/CLIP.md`
