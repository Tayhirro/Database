# 章节蓝图：Diffusion 采样与引导的独立分类体系

## 章节功能

在既有规划 / VLA 测试时扩展 taxonomy 之外，给出一套只解释 diffusion sampler 内部机制的分类。读者应能据此区分“同一粒子被连续引导”“多个粒子彼此作用”“粒子按权重复制 / 淘汰”以及二者同时发生。

## 核心主张

1. 分类单位是一个 reverse transition 中实际作用的更新算子，而不是整篇方法的应用领域。
2. 是否使用 reward 不能决定类别；reward 可以进入单链 gradient guidance，也可以转成 FK potential 后驱动 population resampling。
3. FK Steering 的 `reward`、`potential`、normalized sampling weight 和 resampling 是四个不同对象。
4. Continuous particle interaction 改变的是 drift / score；FK resampling 改变的是 ancestry / offspring count。
5. Best-of-$N$ 只在终局选择，不能替代 intermediate FK resampling。

## 叙述顺序

1. 独立性声明与统一状态表达。
2. D0—D4 判别总表。
3. 术语表。
4. D0 independent reverse sampling。
5. D1 pathwise guided denoising，以 SafeBimanual 说明“梯度影响生成结果但不更新模型参数”。
6. D2 continuous coupling：Particle Guidance、SPELL 配置边界、EDDY marginal-preserving 目标。
7. D3 FK/SMC：target tilt、potential product constraint、概率重采样算法、数值例子、Difference Potential、interval schedule、确定性 transition caveat。
8. D4 hybrid：IMPFM。
9. 对照表与快速判别问题。

## 公式槽位

- 基础 transition：$x_{t-1}^{i}\sim p_\theta(\cdot\mid x_t^i,c)$。
- 单粒子 guidance：$x_{t-1}^{i}=\mu_\theta(x_t^i,t,c)+u_t^i+\sigma_t\epsilon_t^i$。
- joint potential：$\nabla_{x_t^i}\log\Phi_t(x_t^1,\ldots,x_t^K)$。
- terminal tilt：$p_{\mathrm{target}}(x_0)\propto p_\theta(x_0)e^{\lambda r(x_0)}$。
- FK product constraint：$\prod_tG_t=e^{\lambda r(x_0)}$。
- normalized resampling weight：$w_t^i=\widetilde G_t^i/\sum_j\widetilde G_t^j$。
- Difference Potential 与 telescoping。

## 边界与限定

- `D0—D4` 是本文为说明机制建立的 taxonomy，不宣称论文共同使用这些类别名。
- D2 与 D3 不是“是否有多个样本”的区别，而是 continuous state coupling 与 discrete ancestry update 的区别。
- 同一完整方法可含多个算子；D4 专指两者在 sampler 内同时工作。
- 树搜索维护显式父子节点、访问统计或价值回传，作为边界项而非粒子重采样子类。
- 2026 年 EDDY 与 IMPFM 均标为 preprint。

## 证据分配

- D0：DIFF-LOCAL、PG、FK-BOUNDARY。
- D1：SAFE-DIFF。
- D2：PG、SPELL、EDDY。
- D3：FK、FK-BOUNDARY。
- D4：IMPFM。

## 完成标准

- 新章可脱离前文 taxonomy 独立阅读。
- FK 数据流、公式、数值例子与三个边界均齐全。
- 所有论文链接指向 arXiv 或 PMLR 一手页面。
- 原主 taxonomy 文本保持不变。
