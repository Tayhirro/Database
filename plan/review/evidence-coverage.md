# 2.1 Generative Refinement 证据覆盖检查

| Claim | Evidence | Coverage | Remaining risk |
| --- | --- | --- | --- |
| 父级先给出四类二级机制比较，2.1 只定义 Generative Refinement | TAX-LOCAL | 已覆盖 | 需通过标题区间检查防止总表再次落入 2.1 |
| Generative Refinement 属于单链串行生成状态更新 | TAX-LOCAL, DD, DR | 已覆盖 | 属于本文操作性定义，不是外部统一术语 |
| 与 Rethinking 的主要区别是更新对象 | REASON-LOCAL, DD, DR | 已覆盖于父级总表 | 2.2 已补入 ECoT 与 CoA-VLA，方法级组合标签另行说明 |
| 与 Evaluation-Guided 的主要区别是是否存在候选级评价反馈 | DV, SC, DD | 已覆盖于父级总表 | 反馈可直接构造，也可经世界模型后果推演构造 |
| 使用 diffusion 不足以单独决定分类 | TAX-LOCAL | 已覆盖 | 需在正文中补充组合机制边界 |
| Rethinking 迭代的是推理状态而不是动作级生成状态 | REASON-LOCAL, ECoT, CoA | 已覆盖于 2.2 定义与案例 | CoA-VLA 同时包含 diffusion action state，需保留组合标签 |
| Direct Verifier Feedback 必须让候选级评价控制 learned correction 路径 | DV | 已覆盖于 2.3.1 定义 | 同一 backbone 也可能承担 actor 与 critic 两种功能 |
| World-Model-Mediated Outcome Feedback 通过预测后果形成内部反馈 | SC | 已覆盖于 2.3.1 定义 | world model 只预测，`E∘W` 才形成后果评价；不要求完整像素级 rollout |
| 真实执行后获得的新观测不属于内部 world-model-mediated feedback | SC, TAX-LOCAL | 已覆盖于 2.3.1 边界 | 与第 5 节闭环重规划保持一致 |

## 2.3 层级重构覆盖检查

| Claim | Evidence | Coverage | Remaining risk |
| --- | --- | --- | --- |
| Evaluation-Guided 是评价信息回流的父类，同时覆盖 learned correction 与 objective-guided update | TAX-EVAL, TAX-OBJ | 已覆盖 | 完整标签仍需给出具体更新子链 |
| 直接 verifier 与 world-model-mediated path 共享评价反馈—learned refiner 更新机制 | TAX-EVAL, DV, SC | 已覆盖 | 反馈来源仍需作为独立字段保留 |
| world model 本身不自动等于 verifier | TAX-EVAL, SC | 已覆盖 | 需在正文明确区分 `W` 的预测与 `E` 的评价 |
| 固定候选 argmax 仍属于 Selection | TAX-SEL | 已覆盖 | 不能因存在 world rollout 就归入 2.3 |
| 梯度或局部求解直接更新轨迹属于 2.3.2 Objective-Guided | TAX-OBJ | 已覆盖 | learned/world-model objective 只作为来源属性 |

# 2.2 Rethinking / Iterative Reasoning 文献补充证据覆盖

| Claim | Evidence | Coverage | Remaining risk |
| --- | --- | --- | --- |
| ECoT 在动作前生成多个具身推理阶段 | ECoT | 已覆盖 | “多步推理”不等于显式错误纠正，正文需避免 self-correction 表述 |
| ECoT 的推理内容包含计划、子任务、运动和视觉落地状态 | ECoT | 已覆盖 | 不必列出所有训练数据生成细节 |
| CoA-VLA 的四类 affordance 构成有序语义链 | CoA | 已覆盖 | 部分 affordance 会根据任务进度动态选择，不应写成每一步固定生成全部四类 |
| CoA-VLA 的 affordance 中间量条件化低层动作 | CoA | 已覆盖 | 动作由 diffusion head 生成，必须保留 Generative Refinement 组合标签 |
| ISR-LLM 使用 validator 评价并修正初始计划 | ISR | 已覆盖 | 作为排除边界即可，不扩写成 2.3 完整案例 |
| 论文命名不能替代本文近端更新机制判据 | ECoT, CoA, ISR, REASON-LOCAL | 已覆盖 | 仍需在跨维度索引中保持方法级组合标签 |
| ECoT 的语义阶段与动作之间构成一条自回归数据流 | ECoT Figure 3 | 已覆盖 | 需说明它不是候选评价后的 self-correction |
| ECoT 的场景描述、检测与 Gemini 组件用于生成训练标注 | ECoT Figure 4 | 已覆盖 | 不得误写成部署时每一步都调用的在线模块 |
| CoA-VLA 同时包含 affordance reasoning 与 diffusion action head | CoA-VLA Figure 1 | 已覆盖 | 必须保留组合机制标签 |
| CoA-VLA 根据任务进度动态选择 affordance | CoA-VLA Figure 2 | 已覆盖 | 图中真实执行时间轴不能机械计为同一次决策的 refinement rounds |

# Diffusion 独立 taxonomy 证据覆盖

| Claim | Evidence | Coverage | Remaining risk |
| --- | --- | --- | --- |
| particle 是同一模型下的中间去噪状态，不是多个 diffusion 模型 | DIFF-LOCAL, PG, FK | 已覆盖 | 正文需统一用“粒子/轨迹”而非“多个分布” |
| SafeBimanual 在测试时更新 action state 而不是模型参数 | SAFE-DIFF | 已覆盖 | 必须写明 learned denoising 与 cost-gradient guidance 是同一步的两个项 |
| Particle Guidance 通过 joint potential 连续耦合粒子 | PG | 已覆盖 | 不得写成离散淘汰或复制 |
| SPELL 可在静态 reference 与 current-batch dynamic reference 两种配置间切换 | SPELL | 已覆盖 | 仅动态 current-batch 配置属于当前粒子间耦合 |
| EDDY 的理论目标是改变 joint dynamics 并保持 single-particle marginal | EDDY | 已覆盖 | 特征空间近似不再精确保证，正文需保留限定词 |
| FK potential 是正的路径权重，而非另一个网络 | FK | 已覆盖 | reward estimator 本身可以是 learned model，不能与 $G_t$ 混同 |
| FK Algorithm 1 采用按权重的 multinomial parent resampling | FK, FK-BOUNDARY | 已覆盖 | 正文不得简化成 top-$k$ |
| Difference Potential 通过 telescoping 将 terminal tilt 分摊到中间阶段 | FK | 已覆盖 | 需要保留 endpoint compensation 或 $r_T=0$ 条件 |
| terminal Best-of-$N$ 不修改中间 diffusion population | FK-BOUNDARY | 已覆盖 | 如果方法在终局后还重启采样，则需另行判定 |
| 复制粒子随后是否分化取决于 transition 随机性 | FK-BOUNDARY | 已覆盖 | 确定性 DDIM 下完全相同 parent 不会自动分叉 |
| IMPFM 同时含 continuous interaction 与 reweighting/resampling | IMPFM | 已覆盖 | 仅作为 2026 preprint 的混合示例，不写成成熟共识类别 |
| DDPM 与 DDIM 的 reverse steps 在没有额外控制时都属于 D0 | DDPM, DDIM | 已覆盖 | 不能把“多步”或“随机”本身当作粒子搜索 |
| DDIM 改变采样路径与步数，但不自动引入 guidance 或跨样本反馈 | DDIM | 已覆盖 | 确定性 sampler 会影响 FK 复制后的分化能力 |
| classifier guidance 与 CFG 都逐路径修改 score，故属于 D1 | CG, CFG | 已覆盖 | 二者的 guidance 来源不同，不能抹去独立 classifier 与 score combination 的差别 |
| SafeBimanual 的 cost gradient 与 diffusion mean 同时作用于 action denoising state | SAFE-DIFF | 已覆盖 | 完整标签应保留 learned denoising + objective guidance 两项 |
| DAS 同时有 guided proposal 与 SMC resampling，但没有 continuous cross-particle drift | DAS | 已覆盖 | 主类 D3；D1 只作为 proposal 子算子说明，不能升级为 D4 |
| 逐篇卡片必须由原论文问题、公式、图和分类边界共同支撑 | DDPM, DDIM, CG, CFG, SAFE-DIFF, PG, SPELL, EDDY, FK, DAS, IMPFM | 已覆盖：11 张完整卡片与 11 幅原 Figure | 2026 工作保持 preprint 限定；后续版本需重新核验 |

## SPELL 结构化重写覆盖检查

| Claim | Evidence | Coverage | Remaining risk |
| --- | --- | --- | --- |
| SPELL 同时处理同 prompt 重复和对保护图像的近复制 | SPELL | 已覆盖于“前序工作问题” | 两种应用共享 shielding 机制，但风险场景不同 |
| SPELL 先与多个 base models 的原始 sampler 做同种子对照 | SPELL, SPELL-BASELINES | 已覆盖于“基础模型对照” | 不同模型上的 precision / density 变化并不完全一致 |
| PG、IG 与 CADS 是 Section 5.3 的并列 baselines | SPELL-BASELINES | 已覆盖于“Training-free baselines” | baseline 重实现仍可能与原作者实现存在差异 |
| SPELL 的 Pareto 优势只按论文具体实验范围陈述 | SPELL-BASELINES | 已覆盖并限定 Latent Diffusion / CC12M | 不外推至所有模型和应用域 |
| 运行效率与 1.2M ImageNet 静态保护集属于 SPELL 自身实验贡献 | SPELL-BASELINES | 已覆盖于“效率与规模” | 百万级检索开销依赖近邻索引实现 |
| static / historical 与 dynamic intra-batch reference 分别形成 D1 与 D2 | SPELL, DIFF-LOCAL | 已覆盖于“配置归类” | mixed 配置按存在 contemporaneous coupling 判为 D2 |
