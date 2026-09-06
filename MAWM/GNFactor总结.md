---
title: GNFactor 原理总结
tags:
  - 论文笔记
  - 跨视角学习
  - 三维表征
  - 跨视角对应
---

# GNFactor 原理总结

论文：**GNFactor: Multi-Task Real Robot Learning with Generalizable Neural Feature Fields**，Yanjie Ze、Ge Yan、Yueh-Hua Wu、Annabella Macaluso、Yuying Ge、Jianglong Ye、Nicklas Hansen、Li Erran Li、Xiaolong Wang，CoRL 2023 Oral，PMLR 229:284-301。[论文信息](https://proceedings.mlr.press/v229/ze23a.html) ｜ [arXiv:2308.16891](https://arxiv.org/abs/2308.16891) ｜ [项目主页](https://yanjieze.com/GNFactor/)

## 做了什么

GNFactor 是一个视觉行为克隆（behavior cloning）智能体，用于多任务真实机器人操作。它用 Generalizable Neural Feature Field（GNF）作为重建模块、Perceiver Transformer 作为决策模块，二者共享同一个 deep 3D voxel 表征；并通过一个视觉-语言基础模型（Stable Diffusion）把丰富的语义蒸馏进这个共享 3D voxel。作者主张：共享的 3D 体素表征 + 神经渲染重建，能让同一份多视角观测形成一个统一、可泛化的 3D 表征，并直接服务于操作策略（输出 3D Q-function）。[原文摘要、§1、§3](https://proceedings.mlr.press/v229/ze23a/ze23a.pdf)

与 F3RM 不同，GNFactor 明确把"统一 3D field representation"作为策略的输入后端，并给出了"去掉 field / 去掉语义蒸馏"的量化消融。

## 多视角怎样进入统一 3D voxel

GNFactor 被构造成两个共享同一 deep volumetric representation 的模块。

- **体渲染模块（volumetric rendering module）**：学一个 Generalizable Neural Feature Field，从相机重建 RGB 图像，以及从视觉-语言基础模型（Stable Diffusion）得到的 embedding。
- **3D 策略模块（3D policy module）**：一个 Perceiver Transformer，输入来自单 RGB-D 相机的 deep volumetric representation，输出 3D Q-function。任务描述用 CLIP 编码成 task embedding。

体素表征为 $100^3\times 128$ 的特征体 $v$。沿相机射线 $\mathbf{r}(t)=\mathbf{o}+t\mathbf{d}$，颜色与嵌入的体渲染为（原文 Eq.1）：

$$
\begin{aligned}
\hat{\mathbf{C}}(\mathbf{r},v)&=\int_{t_{n}}^{t_{f}}T(t)\,\sigma(\mathbf{r}(t),v_{\mathbf{x}(t)})\,\mathbf{c}(\mathbf{r}(t),\mathbf{d},v_{\mathbf{x}(t)})\,\mathrm{d}t,\\
\hat{\mathbf{F}}(\mathbf{r},v)&=\int_{t_{n}}^{t_{f}}T(t)\,\sigma(\mathbf{r}(t),v_{\mathbf{x}(t)})\,\mathbf{f}(\mathbf{r}(t),\mathbf{d},v_{\mathbf{x}(t)})\,\mathrm{d}t,
\end{aligned}
$$

其中 $T(t)=\exp\!\big(-\int_{t_{n}}^{t}\sigma(s)\,\mathrm{d}s\big)$，$\mathbf{F}(\mathbf{r})$ 是由 Stable Diffusion 提取的真实视觉-语言嵌入，即语义蒸馏的"监督真值"。

重建与语义蒸馏的联合目标（Eq.2）：

$$
\mathcal{L}_{\mathrm{recon}}=\sum_{\mathbf{r}\in\mathcal{R}}\big\|\mathbf{C}(\mathbf{r})-\hat{\mathbf{C}}(\mathbf{r})\big\|_{2}^{2}+\lambda_{\mathrm{feat}}\big\|\mathbf{F}(\mathbf{r})-\hat{\mathbf{F}}(\mathbf{r})\big\|_{2}^{2}.
$$

整体训练目标（Eq.4）把重建与行为克隆交叉熵 $\mathcal{L}_{\mathrm{action}}$ 联合：

$$
\mathcal{L}_{\mathrm{GNFactor}}=\mathcal{L}_{\mathrm{action}}+\lambda_{\mathrm{recon}}\,\mathcal{L}_{\mathrm{recon}}.
$$

关键点：多视角的互补观测通过共享体素 $v$ 与体渲染被对齐到同一 3D 表征；Stable Diffusion 的 2D 特征则作为监督把语义"蒸"进该 3D 表征，使策略在 3D 空间内直接做 Grad-CAM 可视化。[§3.2、§3.3]

## 关键实验与对照

全部以 **PerAct** 为主要基线（原文仅报告 PerAct 及其 4-camera 变体；RVT/Voltron 等未出现在报告表格中）。数字来自 arXiv v3 HTML 与 PMLR 版。

**RLBench 可见任务**（§4.2, Table 1）：10 个任务、每任务 25 episode，平均成功率 PerAct **20.4%**、PerAct(4 Cam) **22.7%**、**GNFactor 31.7%**（约 1.55×）。

**RLBench 未见（泛化）任务**（Table 2）：6 个任务、每任务 20 episode，平均 PerAct **18.0%** vs **GNFactor 28.3%**（约 1.57×）。

**真实机器人**（Table 3）：2 厨房、3 任务（含干扰物），每任务 10 episode，平均 PerAct **22.5%** vs **GNFactor 43.3%**；最具挑战的 teapot 任务 PerAct 两厨房均 0%，GNFactor 达 40%。

**消融**（Table 4，10 个 RLBench 任务平均）：完整 GNFactor **36.8%**；
- **w/o. GNF objective 24.2%**（移除神经特征场重建约束，退化为仅体素编码器 + 策略）；
- **w/o. Diffusion 30.0%**（移除 Stable Diffusion 语义蒸馏）；
- Diffusion→DINO 30.4%，Diffusion→CLIP 32.0%（替换为其它特征仍不及原版）；
- **k=19→9 33.2%**（减少多视角数量）。

因果结论：消融中"w/o. GNF objective"掉 12.6 个点、"w/o. Diffusion"掉 6.8 个点、"k=19→9"掉 3.6 个点，共同支持"统一 3D field 表征 + 语义蒸馏 + 多视角"三者都对策略有独立正向贡献；但 PerAct 本身就是单一 RGB-D 前视椎体素 + Perceiver 的"普通 3D 表征"基线（无 GNF 渲染模块），因此"field 优于普通 voxel"的对照已隐含在 PerAct vs GNFactor 之间，而非一个干净的组件替换。各消融均在同一多任务行为克隆设置内，不涉及未来预测。

## 与单车世界模型的关系及边界

它支持：把多视角观测通过一个共享、可泛化的 3D 神经特征场对齐成统一表征，并证明这种统一 3D field representation 可以直接服务操作策略——比"辅助视角表征 → 单视角控制"的 [[MV-MWM总结|MV-MWM]] 更进了一步，因为它把 field 本身就作为策略输入。它没有证明：跨视角统一场能改善**未来预测 / 世界模型 rollout / 动力学**；GNFactor 是行为克隆（看当前观测出动作），没有动作条件的时序模型，也不预测下一帧或未来状态。其"跨视角"作用在表征与重建层，而非动力学层。

**一句话：GNFactor 用 Generalizable Neural Feature Field 把多视角观测与视觉-语言语义蒸馏进同一个共享 3D voxel，并证明这种统一 3D field 表征能直接喂给操作策略、显著优于 PerAct，但停在行为克隆，未触及未来预测。**

整体结论与拟议实验见 [[跨Agent视角对应的训练价值与单车推理结论]]。相关：[[F3RM总结]]、[[MV-MWM总结]]、[[NeuralDescriptorFields总结]]、[[GARField总结]]、[[RSRD总结]]。
