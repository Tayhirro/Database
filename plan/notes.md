# 写作决策记录

## 2.1 Generative Refinement

- 将 Generative Refinement 明确写成本文分类框架中的操作性标签，不声称它是已有文献的统一术语。
- 分类判据采用“直接更新对象 + 更新信号来源”，不用“是否迭代”或“是否使用 diffusion”代替机制判断。
- 场景、语言和 BEV 特征属于生成条件；只有针对当前候选产生的分数、critique、修正向量或预测后果才作为显式反馈。
- 四类机制的总对照表属于 `#### 2. 串行精化` 的父级说明，不属于 `##### 2.1 Generative Refinement`。
- `2.1` 只保留本机制的定义、公式、判别条件和案例过渡。
- `2.2—2.4` 已按与 2.1 相同的五项要素补齐：定义、形式化过程、直接更新对象、反馈来源和分类边界。
- Verifier-Guided 的判断依据是评价信号是否控制候选修正，而不是模块是否命名为 verifier；仅对固定候选排序仍属于 Selection。
- World-Feedback 的判断依据是反馈是否经由预测未来产生；真实执行后的新观测归入 Environment-Feedback Replanning。

## 2026-08-19｜2.1—2.5 统一分类轴

- 五类统一按“产生下一迭代状态的近端更新机制”判定；迭代状态和信息来源改为解释属性，不再分别充当分类轴。
- Generative 对应学习到的生成变换，Rethinking 对应语义推理变换，Verifier-Guided 对应 `评价 → feedback-conditioned refiner`，World-Feedback 对应 `未来推演 → 后果反馈 → refiner`，Objective-Guided 对应梯度、凸化、投影等数学局部优化步骤。
- loss、reward 或 cost 的存在不是 Objective-Guided 的充分条件；只有局部求解器在推理时直接计算下一迭代点才归入该类。
- 可微 verifier 或 world model 若被直接求导并用于局部优化，主机制归入 Objective-Guided，来源分别注明为 learned verifier 或 world-model-derived objective。
- 同一更新式含生成变换与目标梯度等可独立辨认的更新项时使用组合标注；训练损失、初始化、静态条件和最终选择不产生组合标签。
- guided diffusion 保留 `Generative Refinement + Objective Guidance`，不强制压入单一类别。

## 2026-08-19｜分层标签与采样边界

- 完整方法采用 `计算拓扑 × 内层更新机制 × 候选 / 采样状态 × 评价或反馈来源`；规划操作和训练—部署方式继续作为补充字段。
- 2.1—2.5 是可复用的单链更新机制词汇，不再被当作覆盖方法全部性质的互斥标签。
- $N$ 统计候选解，不统计所有底层随机调用。只为一个当前解估计梯度、熵或统计量的样本属于瞬时评价采样。
- 一次性 $N$ 候选为 Parallel Width；$N$ 条独立迭代链为 `Width × Depth, C=0`；多个候选共同改变下一轮 proposal 或 population state 时才是 `Adaptive Population, C=1`。
- 组合标签保留机制叠加，候选 / 采样字段保留 stochastic denoising、Gaussian population、particles、tree frontier 等实现形态，两者互不替代。
- AAC 的多样本仅用于估计 entropy 并决定执行长度，因此其一级拓扑保留 Closed Loop，采样性质移入“候选 / 采样状态”字段。

## 2026-08-19｜非耦合迭代边界修正

- 原第 2 节把纯 Sequential Depth 与跨拓扑对照放在一起，造成 Width 和 Adaptive Population 在第 2 节重复出现；该组织方式已弃用。
- 非分支单次决策搜索改按 $R$、$C$ 判定：$R=1$ 对应一次并行候选，$R>1,C=0$ 对应非耦合迭代，$R>1,C=1$ 对应反馈耦合群体。
- 第 2 类的统一条件是 $R>1,C=0$。$N=1$ 时标注 Sequential Depth，$N>1$ 时标注 Independent Multi-Chain / Width × Depth。
- 2.1—2.5 描述每条链的近端更新机制，可同时用于单链和多条独立链，但不再承担一级拓扑判定。
- 第 2 节只保留单链、独立多链和瞬时局部采样的边界；Parallel Width 与 Adaptive Population 分别由第 1、3 节定义。

## 2026-08-19｜2.2 代表论文与命名边界

- ECoT 被用作较纯的 `Rethinking / Iterative Reasoning` 正例：动作前依次生成任务、计划、子任务、运动与视觉落地状态，既有推理上下文直接条件化后续语义状态和动作。
- 语义阶段而非任意底层 token 才对应公式中的 $r$；本文不把“输出文本更长”本身视为 refinement。
- CoA-VLA 的 affordance reasoning 子链归入 Rethinking，但其低层动作由 diffusion head 生成，因此完整方法采用 `Rethinking / Iterative Reasoning + Generative Refinement` 组合标注。
- ISR-LLM 虽在标题中使用 Iterative Self-Refined，但初始计划由 validator 评价并据反馈修正，按本文近端数据流应归 Verifier-Guided Refinement。
- 论文作者没有使用本文 taxonomy；正文只说明本文如何依据其推理时数据流进行归类。
- CoA-VLA 已正式发表于 ICCV 2025；正文引用优先指向 IEEE Xplore，同时提供 CVF Open Access 和 DOI，不再标为仅 arXiv 预印本。

## 2026-08-19｜2.2 论文图选择

- ECoT 采用 Figure 3 与 Figure 4：前者直接展示动作前语义推理阶段，后者用于区分离线训练标注管线与部署时推理拓扑。
- CoA-VLA 采用 Figure 1 与 Figure 2：前者展示 affordance reasoning 和 diffusion action head 的组合数据流，后者展示任务执行中按进度动态选择 affordance。
- CoA-VLA Figure 2 的横轴包含真实动作执行，不能直接把图中所有阶段计为同一次决策的 $R$；2.2 标签仅作用于每次动作前的 affordance reasoning 子链。
- ISR-LLM 仍只承担 Verifier-Guided 排除边界，不增加图片或扩写成 2.2 正例。

## 2026-08-19｜评价引导机制由并列改为层级

- 原 2.3 Verifier-Guided 与原 2.4 World-Feedback 不再作为平级近端机制；两者共同归入 2.3 Evaluation-Guided Refinement。
- 2.3.1 Direct Verifier-Guided 直接计算 `V(o,a)`；2.3.2 World-Model-Mediated Verification 先计算 `W(o,a)`，再由 `E` 将预测后果转为候选级反馈。
- world model 只承担后果预测，`E∘W` 复合路径才承担 outcome verifier 功能，避免把预测器和评价器概念混用。
- 两个子类最终都由 feedback-conditioned refiner 产生新候选，因此共享近端更新机制；direct verifier 与 imagined future 继续在反馈来源层保留。
- 原 2.5 Objective-Guided 顺延为 2.4。若评价信号进入梯度、投影或局部求解器而不是 learned refiner，仍归 Objective-Guided。

## 2026-08-20｜评价引导父类覆盖 Objective-Guided

- 上一轮把 Evaluation-Guided 限定成 `评价反馈 → learned refiner`，却让语义更窄的 Objective-Guided 与它平级，标题范围与层级判据不一致；该结构由本次决定取代。
- 2.3 现作为评价信息回流的上位机制家族：评价只用于固定候选排序时仍属于第 1 类；评价实际生成下一候选时才进入 2.3。
- 2.3.1 是 `评价反馈 → feedback-conditioned learned refiner → 新候选`；Direct Verifier 与 World-Model-Mediated Outcome Feedback 是它的两个反馈来源变体。
- 2.3.2 是 `显式目标或约束 → gradient / projection / local solver → 下一迭代点`；解析代价、learned verifier 和 differentiable world model 只记录为目标来源。
- Objective-Guided 被收入 2.3，不等于任意 cost 或 world model 都是 verifier。二者的共同上位概念是评价信息参与更新，差别仍由 learned update 与 numerical update 两条子链保留。

## 2026-08-20｜Diffusion sampler taxonomy

- 新增 D0—D4 独立机制体系，不套用前文规划 taxonomy：D0 independent reverse sampling、D1 pathwise guidance、D2 continuous particle coupling、D3 FK/SMC resampling、D4 interaction + resampling hybrid。
- diffusion particle 是同一生成模型下持久演化的中间状态，不是多个 diffusion 网络或多个独立训练分布。
- SafeBimanual 的 safety gradient 更新 action denoising state，因此属于 D1；梯度会影响生成结果，但这不等于 population resampling。
- Particle Guidance 在 drift/score 上使用 joint potential 连续耦合当前 particles，不执行 parent replication。
- SPELL 的固定 reference 配置属于 reference-guided transition；只有 reference 随当前 batch expected outputs 动态更新时才形成 D2 型 contemporaneous coupling。
- FK Steering 区分 intermediate reward、positive potential、normalized parent probability 与 resampling；完整 SMC 权重还可能包含 $p_\theta/\tau$ proposal correction。
- FK 是有放回概率繁殖，不是 top-$k$；$K$ 次 categorical parent sampling 与 offspring-count 视角的 Multinomial sampling 等价。
- Difference Potential 通过 telescoping 将 terminal reward tilt 分摊到中间阶段，但需处理噪声端 reward 或 endpoint compensation。
- 同一 parent 的后代只有在后续 transition 有随机性或额外扰动时才会分叉；完全确定性 DDIM/ODE 不会自动恢复 diversity。
- EDDY 与 IMPFM 截至本次核验均按 2026 arXiv preprint 处理，不写成正式录用结论。

## 2026-08-20｜Diffusion 代表论文扩写决策

- D0—D4 分类骨架得到用户认可，本轮不新增类别、不合并类别，只提升每篇论文的说明密度。
- 每篇统一采用“前序工作问题—动机与方法—核心创新—原图解读—分类理由—边界与局限”，与前文 test-time scaling 条目对齐。
- D0 增加 DDPM 与 DDIM，避免基础 reverse sampling 只剩概念描述；D1 增加 classifier guidance 与 CFG，说明 learned guidance 和解析 cost guidance 的共同点与差别。
- D3 增加 DAS，用它说明“pathwise guided proposal + SMC resampling”仍以 ancestry update 为主类，并与 D4 的 continuous cross-particle interaction 拉开边界。
- 论文图只采用官方 PDF / arXiv 原 Figure；DAS 与 IMPFM 的网页缩略图已判定不合格，改由官方 PDF 精确裁取 Figure 1。
- classifier guidance 后续补入 NeurIPS 正式论文 Figure 2；DDIM 的网页子图也改为官方 PDF 的完整 Figure 1，避免图解文字超过图片实际内容。
- 逐篇对照表保留主类和组合信息：DAS 主类为 D3 并附 D1-style proposal；SPELL 按 static / dynamic reference 分别落入 D1 / D2；IMPFM 同时满足 D2 与 D3 条件而使用 D4。
