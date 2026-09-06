# 项目概览

## 当前任务

- 文件：`Database/任务-new.md`
- 内容：轨迹规划与 VLA 测试时扩展的分类笔记
- 当前阶段：S5 Diffusion taxonomy 逐篇文献扩写完成
- 目标：保留 diffusion-specific 分类骨架，把每篇代表论文扩成与前文 test-time scaling 条目同等细度的技术卡片，并补齐原论文图解

## 结构原则

- `#### 2. 非耦合迭代精化` 负责容纳 $R>1,C=0$ 的单链和独立多链，并说明每条链内部的二级更新机制。
- `##### 2.1 Generative Refinement` 只定义 Generative Refinement，并承接本类案例。
- 不在 2.1 内放置与 2.2、2.3 的总分类表。
- 2.1—2.3 及其子标签均使用“迭代状态—近端更新机制—控制信息—分类边界”的介绍结构。
- 2.3 是评价信息回流的父类；完整机制标注还要区分 2.3.1 learned correction 与 2.3.2 numerical objective update。
- 2.1—2.3 及其子标签只描述一条精化链内部怎样更新；$N=1$ 时是 Sequential Depth，$N>1$ 且链间无反馈时是 Independent Multi-Chain。
- $N$ 统计持久候选解，不把只用于梯度、熵或矩估计的瞬时样本自动计为并行候选。

## 当前增量：Diffusion 采样与引导的独立分类体系

- 新章独立编号为 D0—D4，不复用前文 1—5 类或 $N/R/C$ 标签。
- 核心判别对象是一个 reverse diffusion transition 中实际改变状态的算子：独立转移、单粒子 guidance、连续粒子耦合、离散重采样或二者混合。
- `reward`、Feynman–Kac `potential`、归一化采样权重与 resampling 分别定义，不把它们合并成“评分”。
- Best-of-$N$ 作为终局后处理基线，显式树搜索作为边界项，不并入 FK/SMC 粒子重采样。
- 本轮不改变 D0—D4 分类，只扩写 DDPM、DDIM、classifier guidance、CFG、SafeBimanual、Particle Guidance、SPELL、EDDY、FK Steering、DAS 与 IMPFM 的问题、方法、图解、归类理由和边界。
- 11 篇论文均已按统一卡片落盘，每篇各有一幅官方原论文图；DAS 用于说明 guided proposal + SMC 的 D3 判定，IMPFM 用于说明 continuous interaction + resampling 的 D4 判定。
