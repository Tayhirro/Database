# 当前章节结构

## 2. 非耦合迭代精化：Uncoupled Iterative Refinement

- 总体定义：$R>1,C=0$
- 两种拓扑子形态：单链 Sequential Depth 与独立多链 Width × Depth
- 完整方法描述：计算拓扑 × 近端更新机制 × 候选 / 采样状态 × 评价或反馈来源
- 二级机制统一判据：下一迭代状态的近端更新机制
- Generative、Reasoning 与 Evaluation-Guided 三个机制家族，以及 Evaluation-Guided 下的 learned correction 和 objective-guided optimization 两条更新子链
- 单标签判定优先级与混合机制组合标注规则
- 瞬时评价样本与持久候选的边界

### 2.1 Generative Refinement

- 操作性定义
- 形式化表达
- 本类判别条件
- DiffusionDrive 与 DiffRefiner 案例

### 2.2 Rethinking / Iterative Reasoning

- 操作性定义
- 推理状态迭代公式
- 更新对象与自生成上下文
- 与显式候选评价的边界
- 经一手来源验证的代表性论文案例及其拓扑判定

### 2.3 Evaluation-Guided Refinement

- 上位定义：评价当前解，并让评价信息回流产生下一候选
- 统一的 `Assess → Update` 表达
- learned refiner 与数学局部求解器两条更新子链
- 与固定候选选择、真实环境闭环反馈的边界

#### 2.3.1 Feedback-Conditioned Learned Refinement

- `评价反馈 → learned refiner → 新候选`
- Direct Verifier Feedback：DriveVer、CriticVLA
- World-Model-Mediated Outcome Feedback：ReflectVLM、SC-VLA
- world model 与 outcome evaluator 的功能分工
- 与 World-Feedback Selection、真实环境反馈的边界

#### 2.3.2 Objective-Guided Continuous Refinement

- `显式目标或约束 → 数学局部求解器 → 下一迭代点`
- 局部优化更新公式
- 与 learned refiner、world feedback、guided diffusion 的边界
- 独立多起点与反馈耦合群体搜索的边界

## Diffusion 采样与引导的独立分类体系

- 适用范围与判别单位：一个去噪时刻上的 sampler state 和更新算子
- 统一分解：base reverse transition、pathwise guidance、continuous coupling、population reproduction
- 术语表：denoising state、particle、reward、potential、normalized weight、resampling

### D0 Independent Reverse Sampling

- 单条基础去噪链
- 多条相互独立的 diffusion trajectories
- terminal Best-of-$N$ 只作为完成采样后的 selection overlay
- DDPM 逐篇卡片：随机 Markov reverse chain 与 Figure 2
- DDIM 逐篇卡片：非 Markov forward family、可确定性采样、加速与 Figure 1

### D1 Pathwise Guided Denoising

- 外部条件、能量、代价或约束直接改写当前粒子的 transition
- classifier guidance：独立 noisy classifier gradient
- classifier-free guidance：conditional / unconditional score extrapolation
- SafeBimanual：对 action denoising state 注入 safety-cost gradient
- 与参数更新、粒子交互和重采样的边界

### D2 Continuously Coupled Particle Diffusion

- Particle Guidance：joint potential 与 pairwise repulsion
- SPELL：固定 reference-set 与 current-batch 动态 shielding 两种配置
- EDDY：以反对称、近似 divergence-free drift 改变 joint dynamics，并以保持单粒子 marginal 为目标

### D3 Feynman–Kac / SMC Resampling

- terminal reward tilt 与 path potentials 的乘积约束
- `reward → positive potential → normalized probability → parent resampling → diffusion transition`
- Difference Potential 的 telescoping 解释
- interval resampling、随机复制与 stochastic/deterministic transition 下的后代分化边界
- FK Steering 完整论文卡片与 Figure 1
- DAS：tempered targets、guided proposal、importance weights、resampling 与 Figure 1

### D4 Hybrid Interaction-and-Resampling

- 连续 interaction drift 与离散 reweighting/resampling 同时存在
- IMPFM 作为 2026 preprint 示例，并展开 flow-map posterior sharing、双力 drift 与 interaction-aware FKC
- 与 D2、D3 的判别表

### 对照边界

- FK resampling vs terminal Best-of-$N$
- FK resampling vs deterministic top-$k$
- particle guidance vs FK reproduction
- explicit diffusion tree search 不属于无显式树的 particle population
