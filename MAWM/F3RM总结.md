---
title: F3RM 原理总结
tags:
  - 论文笔记
  - 跨视角学习
  - 三维表征
  - 跨视角对应
---

# F3RM 原理总结

论文：**Distilled Feature Fields Enable Few-Shot Language-Guided Manipulation**（F3RM = Feature Fields for Robotic Manipulation），William Shen、Ge Yang、Alan Yu、Jansen Wong、Leslie Pack Kaelbling、Phillip Isola，CoRL 2023 Oral（Best Paper），arXiv:2308.07931。[论文信息](https://arxiv.org/abs/2308.07931) ｜ [GitHub](https://github.com/f3rm/f3rm) ｜ [项目主页](https://f3rm.csail.mit.edu/)

## 做了什么

F3RM 把多张带位姿（posed）的 RGB 视图，通过体渲染（volume rendering）把 2D 基础模型（DINO、CLIP）提取的密集特征蒸馏进同一个 3D feature field，再用这个 3D 场同时提供几何与语义，做 few-shot 的 6-DOF 抓取/放置与开放文本语言引导操作。它不直接预测未来，而是把"不同视角中属于同一物体的像素"通过 3D 场对齐到同一组空间-语义特征——这正是"用场对齐不同视角中的同一东西"。[原文摘要、§1](https://arxiv.org/abs/2308.07931)

## 多视角怎样蒸馏进同一个 3D 场

2D 视觉特征提取：对每个视图的 RGB 图 $\mathbf{I}$，用基础模型得到 dense 特征图 $\mathbf{I}^{f}=\mathbf{f}_{\mathrm{vis}}(\mathbf{I})$。CLIP 分支用 MaskCLIP 重参数化技巧提取 patch 级密集特征且不做微调；DINO 用 `dino_vits8` 的最后一层 key/value 嵌入作为密集对应描述符。[§3.1、Appendix A.4]

3D feature field 用基于哈希网格（hash grid）的 NeRF 参数化，沿相机射线做体渲染，把各视角特征积分到空间点上。渲染得到的 2D 特征图定义为（原文 Eq.1）：

$$
\mathbf{F}(\mathbf{r})=\int_{t_{n}}^{t_{f}}T(t)\,\sigma(\mathbf{r}_{t})\,\mathbf{f}(\mathbf{r}_{t})\,\mathrm{d}t,
$$

其中 $T(t)=\exp\!\big(-\int_{t_{n}}^{t}\sigma(\mathbf{r}_{s})\,\mathrm{d}s\big)$ 为累积透射率，$\sigma$ 是密度场，$\mathbf{f}(\cdot)$ 是假设与视角无关的特征场。

蒸馏损失让渲染特征逼近各视角真实 2D 特征（§3.1 "Feature Distillation"）：

$$
\mathcal{L}_{\mathrm{feat}}=\sum_{\mathbf{r}\in\mathcal{R}}\big\|\hat{\mathbf{F}}(\mathbf{r})-\mathbf{I}^{f}(\mathbf{r})\big\|_{2}^{2}.
$$

注意监督联系的是"同一 3D 点在不同位姿视图下的 2D 特征投影"，因此多视角不是各自独立自监督，而是通过 $\mathbf{f}(\cdot)$ 这个共享场被显式对齐。论文还训练一个普通 NeRF 密度/颜色场，二者共用哈希网格结构（Appendix A.3）。

## 语言引导与 6-DOF 姿态优化

few-shot 抓取（无语言）优化目标为最大化演示姿态特征的平均相似度（Eq.3）：

$$
\mathcal{J}_{\mathrm{pose}}(\mathbf{T})=-\cos\big(\mathbf{z}_{\mathbf{T}},\,\mathbf{Z}_{M}\big),
$$

其中 $\mathbf{Z}_{M}$ 是演示姿态特征的平均。语言引导时（Eq.4），用 CLIP 文本嵌入 $\mathbf{q}$ 与 $\alpha$-加权查询点特征 $\mathbf{f}_{\alpha}(\mathbf{x})$ 的归一化余弦相似度作为权重，乘以姿态代价：

$$
\mathcal{J}_{\mathrm{lang}}(\mathbf{T})=\underset{\mathbf{x}\in\mathbf{T}\mathcal{X}}{\mathrm{mean}}\big[\mathbf{q}\otimes\mathbf{f}_{\alpha}(\mathbf{x})\big]\cdot\mathcal{J}_{\mathrm{pose}}(\mathbf{T}).
$$

§3.3 还用"成对 softmax"对每个 voxel 计算其与正/负文本嵌入集合的余弦相似度，剔除更靠近负查询的 voxel。原文未给出带温度的显式 softmax 公式，温度项原文未报告。

## 关键实验与对照

**Few-shot 抓取**（§4.1, Table 1）：5 个任务、每任务 2 演示、50 次试验。总成功率：MIRA 基线 15/50，Density(NeRF) 27/50，RGB 21/50，**DINO ViT 31/50，CLIP ViT 34/50，CLIP ResNet 39/50**。蒸馏特征显著优于纯几何/纯 RGB 与从零训练的 MIRA。但对照是"不同特征类型"与"不同 NeRF 输出"，不是严格的"去掉多视角"消融。

**语言引导操作**（§4.2, Table 2）：13 场景、10 演示、50 个自由文本查询，总成功 **31/50**；OOD 新类别 9/10，关系型查询仅 4/10（CLIP 词袋缺陷）。

**多视角数量消融**（Appendix A.5, Fig A11）：从 50 张均匀子采样，定性显示约 **20 张**是质量骤降（floater 增多）前的下限——证明对多视角有依赖但可减负。原文未报告去掉多视角后的定量成功率。

**架构消融**（Appendix A.3.1）：哈希网格 vs MLP head（类 NDF），哈希网格在特征蒸馏 MSE 上更低、语义边界更清晰（减少"特征溢出"）。

因果结论边界：这些对照支持"把基础模型特征蒸进 3D 场优于纯几何/纯 RGB/从零训练"，但缺少"用单视角重建代替多视角对齐"的受控消融，不能确定全部收益都来自跨视角对应关系本身；语言任务也暴露了 CLIP 关系理解弱。

## 与单车世界模型的关系及边界

它支持：把多个 posed 视角的互补观测通过共享 3D 场对齐到同一组空间-语义特征，从而使单视角推理（查询任一 3D 点）能调用多视角聚合得到的语义与几何。它没有证明：跨视角特征对齐能改善**未来预测/世界模型 rollout**；F3RM 是静态 3D 重建 + few-shot 抓取优化，没有动作条件动力学，也没有时序。它最接近"跨视角对应 → 训练监督 → 单车可用性"的表征层证据，但落脚点是抓取/放置而非预测。

**一句话：F3RM 用体渲染把 DINO/CLIP 的密集特征从带位姿的多视角蒸馏进同一个 3D feature field，使不同视角中同一物体对齐到一致的空间-语义特征，但停在静态表征与 few-shot 抓取，未触及未来预测。**

整体结论与拟议实验见 [[跨Agent视角对应的训练价值与单车推理结论]]。相关：[[GNFactor总结]]、[[MV-MWM总结]]、[[NeuralDescriptorFields总结]]、[[GARField总结]]、[[RSRD总结]]。
