# 章节蓝图：Diffusion taxonomy 逐篇文献扩写

## 章节功能

保持 D0—D4 作为稳定的 sampler 机制骨架，把每类下的代表论文从“点名举例”扩成可独立阅读的技术卡片。每张卡片既回答论文做了什么，也回答其测试时状态、控制信号与 population update 为什么落入该类。

## 统一论文卡片

1. 书目信息：标题、官方链接、首次公开日期、正式 venue 或 preprint 状态。
2. 前序工作问题：分别从 Abstract / Introduction 与 Related Work 提取问题，不用本文分类替代论文动机。
3. 动机与方法：写出状态变量、控制信号、近端更新式和逐步数据流。
4. 核心创新点：一句话概括论文相对先前 sampler 的关键变化。
5. 论文图解：嵌入原论文 Figure，按“读图—分类含义—边界”解释。
6. 分类结论：记录 sampler state、是否跨粒子耦合、是否改变 ancestry、训练—部署关系和限制。

## 论文分配

- D0：DDPM（随机 Markov reverse chain）、DDIM（同训练目标下的非 Markov / 可确定性快速 sampler）。
- D1：classifier guidance（独立 noisy classifier 梯度）、classifier-free guidance（conditional/unconditional score 组合）、SafeBimanual（解析 safety cost gradient 注入动作去噪）。
- D2：Particle Guidance（joint potential）、SPELL（static reference 与 dynamic batch 两种边界）、EDDY（marginal-preserving interaction 目标）。
- D3：FK Steering（path potential 与概率繁殖）、DAS（tempered SMC、guided proposal、importance correction 与 resampling）。
- D4：IMPFM（flow-map posterior sharing、连续 attraction/repulsion、interaction-aware FK corrector）。

## 图像槽位

- DDPM Figure 2；DDIM Figure 1；classifier guidance Figure 2；CFG Figure 2；SafeBimanual Figure 2。
- Particle Guidance Figure 1；SPELL Figure 2；EDDY Figure 2。
- FK Steering Figure 1；DAS Figure 1；IMPFM Figure 1。

## 关键边界

- D0 与 D1：是否存在基础 reverse transition 之外、直接作用于当前 path 的 guidance term。
- D1 与 D2：guidance 是否读取当前 batch 中其他 particles 的状态或预测终局。
- D2 与 D3：连续改变 drift/score，还是离散改变 parent index 与 offspring count。
- D3 与 D4：是否在 resampling 外还存在显式跨粒子的连续 drift correction。
- terminal Best-of-N、top-k 与 tree search 保留为外层或边界机制，不吸收进 D0—D4 论文卡片。

## 完成标准

- 11 篇论文均有完整卡片，不再只有一句代表方法说明。
- 11 幅原论文图完成视觉核验，图解直接服务分类判据。
- 所有 2025/2026 venue 与 preprint 状态由官方页面核验。
- 原 D0—D4 骨架、术语表和 FK 数值例子保留。
