
产品调研

1：目标用户
2：痛点
求职信息碎片化  --单agent，llm效果不佳 不够深入
reasoning：
- 评价策略    
	- 可信度评分   -- 论坛信息评价过滤，结构设计
- 证据抽取策略
- 冲突消解策略

act：
- 搜索策略  
	- A 公司 后端 实习 转正  
	A 公司 base 年终 绩效 总包  
	A 公司 offer 组成  
	A 公司 部门 避雷  
	A 公司 某城市 某岗位 面经  
	A 公司 mentor 老板 leader  
	A 公司 加班 WLB  
	A 公司 PIP 裁员 组织调整  
	A 公司 实习生 项目 代码权限  
	A 公司 秋招 HC 冻结  
	A 公司 外包 驻场 正编  
	A 公司 业务线 技术栈  
	A 公司 牛客 / 脉脉 / 小红书 / 知乎 / 掘金 / GitHub
	- 岗位真实性搜索 +  成长性搜索（目标是判断这个岗位对简历有没有用）+ 风险搜索 + 薪资/待遇搜索 + 公司类型搜索（可判定发展逻辑）


高要求，可转化执行路径 
- 参考JIT去 设计
- 项目准备策略
- 行动转化策略 ---可以将他转换为具体的行动
	- 如面试提问，
3：方法设计
- 针对深入信息如何构建算法，结构，让agent去深入探索
	- 利用多agent并行搜索




功能层面
- 搜索功能
	- 岗位搜索：按方向搜索实习岗位，例如后端开发、大模型应用、算法工程、Agent 工程等。  
	- JD 检索：收集并解析典型岗位 JD，提取高频技能、岗位任务、项目要求和隐含能力门槛。  
	- 技能趋势检索：统计不同方向中反复出现的技术关键词，帮助用户判断学习优先级。  
	- 项目案例检索：根据目标方向推荐可用于补强简历的项目类型。  
	- 经验内容检索：整理求职经验、面试反馈和岗位准备路径，辅助用户理解真实招聘要求。

- 分析功能
规划类
- 输入：用户可以输入自己的年级、专业、研究方向、论文情况、项目经历、技能栈和目标实习类型。
	- 用户画像解析：识别用户的学历阶段、技术基础、研究方向、项目经历和求职目标。  
	- 方向推荐：判断用户更适合优先尝试哪些实习方向。  
	- 差距分析：指出用户与目标岗位之间的主要差距。  
	- 项目补强建议：推荐能够提升简历竞争力的项目路线。
调研类
- 市场现状调研
- 机会版图调研
- 岗位要求调研
- 具体目标尽调
- 对比调研
- 风险信号调研

stage1： 反问补充阶段


stage2：具体子任务派发阶段






- 科研idea功能



架构层面

- 状态，追踪，评估，回滚
- 会话管理
- 日志系统




- 错误处理

- agent自进化功能
	- skill-prompt进化
		- 信息字段发现
	- 架构进化





## 数据标注公司
Mercor









## WM视频生成部分
**InfiniVerse**


## WM辅助规划部分：
**World4drive**

- **动机**
	- **现有问题**
		- VADv2 Hydra-MDP  依赖标注
		- VaVAM ，LAW [18]  无多模态
	- **解决问题**
		- 多模态创新
		- 潜空间无监督


**DLWM**
- **动机**


**[DriveLaW](https://arxiv.org/abs/2512.23421)** 2025
- **动机**
	- **现有问题**
		- 预测潜空间应该反哺于规划
			- 例子DriveVLA-W0 ，VaVAM


**ForgeDrive**
- 动机 
	- 研究action，未来依赖关系

## VLA部分
**unidrive-VLA**
- 动机





## VLA-WM部分
**[LCDrive / Latent-CoT-Drive](https://arxiv.org/abs/2512.10226)** 2026
- 借助WM辅助监督提升VLA
- **动机**
	- **现有问题**
		- 自回归的预测会导致时延开销
		- 应该理解语义丰富的表示学习，而不是强调纹理
		- 预测的潜空间应该反哺于规划 
			- 反例：world4drive
			- 例子：UniDrive-WM，FutureSightDrive（隐式）DriveWorld-VLA 特征被压缩成标量

**uniworld-vla** 2026



VLA-World


**CoWorld-VLA**

![[Pasted image 20260724164614.png]]



**VL-JEPA**

**unidrive-wm** 2026




## 端到端驾驶

**SparseDrive**
**VAD**







## 思考
### 状态建模
1：目标要构建从WM中提取的信息的之间的关系，且我们认为这种关系的显式建模是对单纯的bev隐式建模更有效，以及传统的Bev建模会有什么样的问题
- 连接结构主要由隐特征相关性决定，没有显式利用候选未来轨迹之间的空间接近关系，来决定哪些节点应该连接

当环境能够被分解为实体，未来由实体之间稀疏、类型化、可变化的关系所决定时，图是一种特别合适的世界状态表示  
- 当问题由实体、实体间关系以及可组合规则构成时，将这些结构显式写入模型可能有利于学习和泛化，**可查询，组合**
	- 对空间时间：
		- 在对象可分解、交互较明确的环境中，图动力学可能比整体潜变量动力学更适合 （局部性利用）
			- Neural Relational Inference 推断潜在交互图，并根据此预测
			- GraphAD 构建交互图
	- 对语义属性：
		- 环境语义图
		- 环境动力学或因果交互图
		- 推理过程图



### 状态压缩
latent-world 
sparse-world AAAI 



基于语义的latent空间压缩
latent能恢复语言，图片，action



假设：
【基于video/latent的loss覆盖问题】
基于videos的稠密监督可能导致与规划无关的监督损失可能会覆盖action的优化任务，而latent只是基于语义编码器提取的latent/bev预测未来，而latent本身仍然更多服务于视觉/bev重建任务
【隐式学习结构，没有显示利用驾驶环境中广泛存在的图结构】
传统世界模型擅长处理视频、图像、文本等序列或张量，但没有显式利用世界中已有的实体关系和结构，模型需要自己隐式学出：
- 节点关系
- 信息传播关系
- 已存在的图中的节点间影响

对世界模型state的结构化设计，利用图结构对多模态未来进行展开，
1：对于无法显式提取agent的实体如何识别
2：对未来ego-action的多模态输出如何利用构建的图结构
- 图：t时刻图 （树内）--- 0-t时刻树状展开（树-世界模型） ？
- 图：时空-模态 图 （联合）
- 需要确认的是，我们利用图结构辅助diffusion进行扩散生成，增强自车action的控制效果   
	- 不比较particle-diffusion，而是利用他的思路为我们提供帮助
	- 

状态压缩与非压缩
HyWorldVLA
MaskGWM

直接非压缩做像素级别扩散 【不太好】



### 轨迹规划与 VLA 的测试时扩展

一般测试时扩展流程为：

`Action Proposal -> Evaluation / Feedback -> Planning Operation`

当评价反馈由世界模型提供时，流程进一步具体化为：

`Action Proposal -> World Rollout -> Outcome Evaluation -> Selection / Refinement / Optimization / Branching`

本节不以一个类别标签压缩方法的全部性质，而采用分层描述：

`完整方法描述 = 计算拓扑 × 内层更新机制 × 候选 / 采样状态 × 评价或反馈来源`

**计算拓扑**描述推理阶段额外计算怎样组织。对于不维护显式分支的单次决策搜索，第 1—3 类按 $R$ 与 $C$ 形成互斥划分：$N>1,R=1$ 是一次并行候选；$R>1,C=0$ 是非耦合迭代，其中 $N=1$ 为单链串行精化，$N>1$ 为独立多链；$N>1,R>1,C=1$ 是反馈耦合群体。显式维护条件分支的搜索归入第 4 类。第 5 类描述动作执行后由真实环境反馈触发的重规划，它属于跨决策时刻的执行方式，可以与前述单次决策拓扑组合。

**内层更新机制**说明一条精化链怎样从当前状态得到下一状态。第 2 节的 2.1—2.3 及其子标签只标注这一层，不改变方法在 $N$、$R$ 和 $C$ 上的拓扑判定。

这里的 $R$ 表示在一次真实动作执行之前，规划器基于当前观测进行的候选生成或更新轮数，初始 proposal 计为第一轮；执行动作并获得新观测后开始新的决策，不计入同一个 $R$。$N$ 统计作为候选解参与选择、更新或跨轮传递的轨迹 / 动作序列。仅用于估计单个当前解的梯度、熵或局部统计量，随后即被丢弃的扰动样本，应记录为**瞬时评价采样**，不能据此增加 $N$ 或把单解优化改判为群体搜索。

#### 1. 并行候选扩展：Parallel Width Scaling

**符号：** $N$ 是候选数，$R$ 是单次决策内的迭代轮数，$C$ 表示候选之间是否互相反馈。**本类：** $N>1, R=1$，即一次生成多个候选。

一次生成多个彼此基本独立的候选，再进行选择或聚合；本轮评价结果不会改变后续候选的生成分布：

`a₁:ₙ ~ π(a | o) -> Aggregate(a₁:ₙ)`

##### 1.1 Verifier-based Selection
生成多个候选，再由外部或学习得到的评价器选择。
- **[V-GPS / Steering Your Generalists](https://arxiv.org/abs/2410.13816)** 2024-10-17｜CoRL 2024
	- **前序工作问题**
		- **Abstract / Introduction：** 大规模、多来源示范赋予 generalist robot policy 较强的语义与任务泛化能力，但数据质量混杂，策略在精确抓取、释放时机和环境分布偏移下仍容易失败。直接微调 foundation policy 不仅需要访问权重、代价高，还可能破坏原有的通用能力。
		- **Related Work：** 普通 offline RL 通常用价值函数更新或抽取一个新 actor，最终部署的是 $\pi_{\mathrm{RL}}$；这类 actor 受离线数据支持域和策略参数化限制，难以复用已有 VLA 的大规模预训练能力，也不便与黑盒或架构各异的 generalist policy 组合。
	- **动机与方法**
		- 不让 offline RL actor 替代 VLA，而是拆分“生成”与“评价”：冻结的 generalist policy 负责产生候选动作，language-conditioned value function 只负责判断候选的长期价值。论文主要用 Cal-QL 在 Bridge 与 Fractal 数据上训练 $Q_\theta(s,a,l)$，并说明 IQL 等其他 offline RL / policy-evaluation 方法也可用于拟合该价值函数。
		- 部署时无需微调、甚至无需访问 policy 权重；每个时间步从任意 base policy 采样 $K$ 个动作，再按 Q 值贪心选择或通过温度为 $\beta$ 的 softmax 重采样：

			$$
			a_{1:K}\sim\pi_{\mathrm{gen}}(a\mid s_t,l),\qquad
			a_t\sim\operatorname{Softmax}\!\left(\frac{Q_\theta(s_t,a_1,l)}{\beta},\ldots,\frac{Q_\theta(s_t,a_K,l)}{\beta}\right).
			$$
	- **核心创新点（一句话）**
		- V-GPS 将 offline RL 的价值优化能力变成可外挂的测试时 steering signal，以“generalist policy 生成、Q-function 评价”的组合保留 foundation policy 的泛化能力，同时改善其动作精度与鲁棒性。
	- **为什么不直接部署 Cal-QL / IQL actor**
		- RL actor 必须独自学习从视觉、语言到连续控制的完整生成映射，而 Q 只需对 generalist policy 已生成的候选进行相对评价；前者更容易被有限离线数据和简单分布参数化限制。
		- 论文 Appendix I 的直接对比中，Cal-QL 与 IQL actor 在两项 SIMPLER 任务上的成功率均为 0，常见失败是无法学会夹爪开合并持续输出 open-gripper；而将同一类 value function 与表达力更强的预训练策略结合则能获得收益。因此 V-GPS 在 offline RL 阶段虽然会训练 actor，但最终“带走”的是 Q，而不是 $\pi_{\mathrm{RL}}$。
	- **可复用性与边界**
		- 同一个 value function 被用于 Octo-small、Octo-base、Octo-small-1.5、RT1-X 和 OpenVLA，并跨 WidowX 与 Google Robot 工作，体现的是 **policy-agnostic / plug-and-play**：Q 可以外挂到训练数据和架构不同的黑盒策略上。
		- 但“base policy 是 generalist”并不推出“离线数据训练出的 Q 也是 general evaluator”。Q 仍可能在 OOD 状态或动作上误评分，候选数增大还可能放大对 value error 的利用；这正是后续 MG-Select 质疑外部 verifier 泛化、转向模型内部置信度选择的切入点。
	- 规划操作：`Selection`。只在当前时间步的 $K$ 个候选中重排并选择，不修改候选，也不把评分反馈给 policy 生成下一轮候选。
	- 评价器：Language-conditioned offline-RL Q-function。它估计在状态 $s_t$、语言指令 $l$ 下执行候选动作 $a_i$ 的长期折扣回报。
	- 反馈来源：离线机器人轨迹中的任务完成奖励；部署时不做 simulator / world-model rollout，只查询已经训练好的 Q-value。
	- 训练—部署：训练阶段用 Cal-QL（主方法，也验证 IQL）学习 value function；部署阶段冻结并黑盒调用 generalist policy，多次采样后由 Q 重排。额外测试时开销来自候选宽度和批量 Q 评估。
	- 参数更新：不更新 base policy，但会单独训练 Q 网络；因此应表述为 **Frozen Generalist Policy + Offline-RL Value Training + Online Width Selection**，而不是“无需训练”。
- **[RoboMonkey](https://arxiv.org/abs/2506.17811)** 2025-06-21｜CoRL 2025
	- **前序工作问题**
		- **Abstract / Introduction：** VLA 在结构化训练场景中表现较好，但面对遮挡、杂乱和 OOD 物体时，一次错误动作就可能导致任务失败；pass@k 又表明冻结策略的分布中往往已经存在正确动作。
		- **Related Work：** 继续扩大或微调 policy 成本高且可能损害通用能力；单次贪心解码没有利用动作分布的宽度，通用 VLM 也没有被训练成可靠的机器人动作 verifier。
	- **动机与方法**
		- 与其重新训练策略，不如从它已有的候选中识别好动作；因此对冻结 VLA 采样并扰动多个动作，用合成失败数据训练 VLM verifier，再结合多数 proposal 分布选出最可靠动作。
	- **核心创新点（一句话）**
		- RoboMonkey 用合成动作偏好训练专用 verifier，将冻结 VLA 中“能采到但不会挑”的正确动作转化为可随候选数量扩展的测试时收益，无需重新训练原策略。
	- **论文图解**
		- **Figure 1｜经验动机：测试时增加候选是否真的有用**
			![[assets/trajectory-planning-vla-tts/robomonkey-fig1-scaling-law.png]]
			- **读图：** 横轴是候选数量，纵轴是 oracle verifier 选中动作的误差。多种采样方式的误差都随候选数增加而下降，说明冻结 VLA 的分布里并非没有好动作，瓶颈之一是没有可靠评价器把它挑出来。
			- **边界：** 这里使用 oracle verifier，只证明“存在可利用的 width scaling 空间”，还不能证明实际学习到的 verifier 一定同样有效。
		- **Figure 2｜训练 verifier 与部署时扩展的完整流程**
			![[assets/trajectory-planning-vla-tts/robomonkey-fig2-framework.png]]
			- **读图：** Stage 1 从机器人数据中采样动作、聚类并按与真值的 RMSE 构造偏好对，用于训练 VLM action verifier；Stage 2 冻结 policy，通过重复采样、Gaussian perturbation 和 majority proposal 扩大候选，再由 verifier 做最终选择。
			- **关键结构：** 动作生成器与评价器分离，所以新增测试时计算主要增加候选宽度，不需要更新原策略。
	- 规划操作：`Selection`。只从已经生成的候选动作中选择最优动作，不修改候选本身。
	- 评价器：VLM verifier。它根据当前视觉观测和任务语义判断每个候选动作是否合理。
	- 反馈来源：候选动作评价。评价结果只用于本轮候选排序，不会反馈给 VLA 继续生成新候选。
	- 训练—部署：冻结 VLA；部署时采样并扰动多个动作，再由 verifier 选择。这样可以增加测试时计算量而不改变原始策略参数。
- **[VeGAS](https://arxiv.org/abs/2605.12620)** 2026-05-12｜CVPR 2026 Findings
	- **前序工作问题**
		- **Abstract / Introduction：** MLLM embodied agent 在 OOD 环境中较脆弱，单样本推理无法利用模型潜在的多种动作假设；直接使用现成 MLLM 验证动作也不能稳定提升结果。
		- **Related Work：** Best-of-N 依赖可信评分器，但 policy likelihood 不等于动作正确性；off-the-shelf verifier 没见过细粒度执行失败，容易只判断候选是否“看起来合理”。
	- **动机与方法**
		- Verifier 必须专门学习各种失败模式；因此先采样多个动作，再合成多类型失败样本构造训练 curriculum，单独训练 generative verifier，部署时只增加候选采样和选择。
	- **核心创新点（一句话）**
		- VeGAS 通过 LLM 注入多样且逼真的执行错误来训练 generative verifier，使多候选推理能够依据任务语义和历史状态识别 OOD 动作失败，而不是依赖 policy 概率或通用 MLLM 的表面判断。
	- **论文图解**
		- **Figure 1｜为什么最高概率动作仍需要验证**
			![[assets/trajectory-planning-vla-tts/vegas-fig1-overview.png]]
			- **读图：** 对“寻找运动用品”的指令，policy 的最高概率动作是 `Pick(sponge)`，但候选集中存在正确的 `Pick(ball)`；generative verifier 结合指令、场景和历史动作重新判断后选择后者。
			- **问题定位：** Policy probability 表示模型偏好，不等于任务正确性；Width Sampling 只有配合能识别语义错误的 verifier 才有意义。
		- **Figure 3｜Verifier 的合成训练数据如何产生**
			![[assets/trajectory-planning-vla-tts/vegas-fig3-verifier-training.png]]
			- **读图：** 从成功轨迹生成逐动作 CoT，再由 LLM 注入顺序、对象、前置条件等逼真失败；正确与错误轨迹分别交给 generative verifier 产生验证结论，形成监督数据。
			- **解决关系：** 这张图对应论文最核心的判断——通用 MLLM 没受过动作失败辨识训练，因此必须用系统化 failure curriculum 专门训练 verifier。
	- 规划操作：`Selection`。从同一轮生成的多个候选动作中选出最可靠的一个，不对候选进行后续修正。
	- 评价器：Generative verifier。它通过生成式判断评价候选动作，而不是只依赖策略本身的动作概率。
	- 反馈来源：候选动作评价。评价结果用于比较候选的可靠性，但不会更新下一轮候选分布。
	- 训练—部署：单独训练 verifier；部署时进行 Width Sampling，不修改底层 policy。额外能力主要来自测试时多候选生成与验证。
- **[SVA](https://arxiv.org/abs/2607.03751)** 2026-07-04｜arXiv 预印本，暂无公开刊会录用信息
	- **前序工作问题**
		- **Abstract / Introduction：** 继续 post-training 可能使通用 VLA 的能力变窄，而 pass@k 表明冻结模型经常能采到成功动作，只是无法识别哪个动作具有更高长期价值。
		- **Related Work：** 在线 MCTS 能获得高质量动作但实时开销过大；普通 value model 缺少策略分布上的探索数据，单纯多采样又无法判断动作的长期回报。
	- **动机与方法**
		- 把昂贵搜索放到离线训练，把价值判断留到线上；因此在模拟器中对冻结 VLA 执行 MCTS 和 rollout，将 empirical return 蒸馏到轻量 Q evaluator，部署时只需多采样并按 Q 值选择。
	- **核心创新点（一句话）**
		- SVA 将模拟器中的离线 MCTS 长期回报蒸馏为轻量 Q evaluator，把昂贵的搜索能力压缩成部署时对冻结 VLA 候选的一次价值选择，从而同时保留通用性与实时性。
	- **论文图解**
		- **Figure 1｜从“继续训练策略”转向“补足动作评价”**
			![[assets/trajectory-planning-vla-tts/sva-fig1-scaling-comparison.png]]
			- **读图：** 上部对比 post-training 与 test-time scaling：前者昂贵且可能缩窄通用性，后者冻结 policy；中部说明普通并行/串行扩展仍依赖 verifier；下部给出搜索蒸馏路线，用离线搜索教会 Q model 评价动作。
			- **问题定位：** 论文把失败归因于 evaluation bottleneck，而不只是 action generation bottleneck。
		- **Figure 3｜Search–Value–Act 三阶段**
			![[assets/trajectory-planning-vla-tts/sva-fig3-framework.png]]
			- **读图：** Search 阶段在模拟器中对冻结 VLA 做 MCTS 并获得经验回报；Value 阶段把搜索结果蒸馏到轻量 Q head；Act 阶段线上只采样 N 个动作并用 Q 值选择，不再访问模拟器。
			- **关键取舍：** 昂贵的分支搜索只发生在离线阶段，部署阶段退化为 Width Selection，因此不能把 SVA 写成“线上 MCTS”。
	- 规划操作：`Selection`。部署时只对 VLA 生成的候选动作进行价值排序，不再展开搜索树。
	- 评价器：Learned Q-value evaluator。它估计当前状态下执行某个候选动作能够获得的预期未来回报。
	- 反馈来源：部署时使用 Q-value；训练 Q 模型时使用模拟器中的 empirical returns。这些 returns 是训练阶段通过搜索和环境 rollout 得到的动作结果。
	- 训练方式：在模拟器中使用 MCTS 探索冻结 VLA 的动作分布，并将搜索结果蒸馏到轻量 Q-value model。这里的蒸馏是让 Q 模型学习复现昂贵搜索得到的价值判断。
	- 部署方式：冻结 VLA 生成多个候选，由 Q evaluator 选择，不再执行 MCTS 或 simulator rollout。因此部署开销主要来自候选采样和轻量价值评估。
	- 综合标注：**Offline MCTS Search Distillation + Online Width Selection**。前半部分描述训练机制，后半部分描述部署时的计算拓扑。
	- 参数更新：冻结 VLA backbone，但会训练新的 Q-value evaluator；不能表述为“整个系统不更新参数”。冻结主策略用于保留其原有能力，新评价器则负责吸收搜索经验。
- **[Beyond Success / JITI](https://arxiv.org/abs/2511.22555)** 2025-11-27｜CVPR 2026
	- **前序工作问题**
		- **Abstract / Introduction：** 人类示范只保证“任务完成”却不总满足稳定抓取、避免碰撞和正确释放等隐式质量约束，VLA 因而会复现 mixed-quality execution；每一步都做 Best-of-$N$ 又浪费计算。
		- **Related Work：** 普通 value steering 只优化成功回报，固定频率的多候选选择没有区分真正影响整段轨迹质量的 decision-critical moments。
	- **动机与方法**
		- 用显式 Success / Elegance Criteria 构造 LIBERO-Elegant，并以离线 Cal-QL 训练 Elegance Critic。部署时先评价默认动作的 Q 值及其相对滑动平均的波动；只有波动超过阈值时，JITI 才采样 $N$ 个 action chunks 并选择最高 Q 候选，否则直接执行默认动作。
	- **核心创新点（一句话）**
		- JITI 把测试时宽度从“每步固定 Best-of-$N$”改成由 critic 波动触发的稀疏干预，只在关键时刻为冻结 VLA 增加多候选计算。
	- 规划操作：`Adaptive Selection`。干预时仍只从原始候选中选择，并不连续修改候选，因此论文虽使用 refinement 一词，拓扑上仍属于 verifier-based selection。
	- 评价器：Offline Cal-QL Elegance Critic；评价目标是动作是否满足 Implicit Task Constraints，而不只是最终成功。
	- 训练—部署：冻结 base VLA，单独训练 critic；部署时用 Q-value fluctuation 门控是否启用 Width Scaling。
- **[VERITAS](https://arxiv.org/abs/2606.18247)** 2026-06-16｜arXiv 预印本，暂无公开刊会录用信息
	- **前序工作问题**
		- **Abstract / Introduction：** 冻结 generalist policy 能采到多种可行动作，但缺少部署时判断任务对齐与物理可行性的机制；一次性的 inference boost 也不会沉淀为长期能力。
		- **Related Work：** 人类干预和 DAgger 式重标注难扩展，已有 test-time verification 多依赖任务专用 verifier，且通常不把验证后的真实 rollout 再利用。
	- **动机与方法**
		- 每个决策点从预训练策略采样多个短 action chunks，由 gradient-free visual verifier 评分并执行最优候选；真实执行产生的 verified trajectories 还能在部署后离线蒸馏回策略。
	- **核心创新点（一句话）**
		- VERITAS 将视觉验证同时用于在线 Best-of-$N$ steering 与离线自生成数据改进，把“本轮选择收益”和“后续策略学习”接成闭环。
	- 规划操作：部署当下是 `Selection`；后续 fine-tuning 是 policy improvement，不能把二者混写成在线参数更新。
	- 训练—部署：在线选择阶段不更新 base policy；离线阶段可用 verified rollouts 微调策略，属于额外的跨 episode 自改进路径。

##### 1.2 World-Feedback Selection

并行产生多个动作或轨迹候选，世界模型分别预测每个候选导致的未来状态，再根据预测未来的合理性、回报或风险进行评分和选择：

`a₁:ₖ -> ŝ₁:ₖ = Wφ(o, a₁:ₖ) -> s₁:ₖ = E(ŝ₁:ₖ) -> j = argmaxₖ sₖ -> a* = aⱼ`

归入本类需要同时满足三个条件：世界模型在推理阶段对候选产生 action-conditioned future prediction；预测未来实际参与候选评分；最终结果仍是原候选集合中的一个元素，而不是依据反馈生成的新轨迹。如果世界预测形成的后果评价被 learned refiner 用来修改候选坐标或动作分量，则归入 2.3.1，并把反馈来源标为 World-Model-Mediated；若评价通过梯度或局部求解器直接更新轨迹，则归入 2.3.2 Objective-Guided Continuous Refinement。

- **[World4Drive](https://arxiv.org/abs/2507.00603)** 2025-07-01｜ICCV 2025
	- **前序工作问题**
		- **Abstract / Introduction：** 端到端驾驶通常依赖昂贵的感知标注；普通模仿学习只拟合专家轨迹，也难以表达不同驾驶意图对应的多种合理未来。
		- **Related Work：** LAW 等潜空间世界模型能够提供自监督未来预测，但对空间—语义先验、驾驶意图以及多模态候选的联合利用不足。
	- **动机与方法**
		- 用视觉基础模型构造包含空间与语义先验的 physical world latent；从轨迹词表提取多种 intention queries，一次生成 $K$ 条多模态轨迹，并对每条轨迹预测 intention-conditioned future latent。World Model Selector 根据这些未来 latent 的分数选择对应轨迹。
		- 训练时，以预测 future latent 与真实 future latent 的距离确定目标模态，并用该模态索引监督 ScoreNet；推理时没有真实未来观测，直接选择 ScoreNet 最高分对应的原始候选：

			`T₁:ₖ -> L_pred¹:ᵏ -> S₁:ₖ -> j = argmaxₖ Sₖ -> T_final = Tⱼ`
	- **核心创新点（一句话）**
		- World4Drive 用意图条件潜世界预测评价多模态轨迹，使未来世界表征成为候选选择依据，但不根据该反馈重新生成或残差修正轨迹。
	- 规划操作：`Selection`。默认对每个驾驶指令生成 6 条轨迹，最终输出其中世界模型分数最高的一条。
	- 评价器：World Model Selector / ScoreNet。它读取各候选对应的预测 future latent 并输出模态分数。
	- 反馈来源：Action-conditioned latent world prediction。世界模型回答不同轨迹意图可能对应怎样的未来潜状态。
	- 训练—部署：联合训练 trajectory generator、latent world model 与 ScoreNet；部署时执行多候选生成、未来 latent 预测和一次 argmax 选择。
	- 分类边界：其计算结构属于 **World-Feedback Selection**，不是 `Evaluation-Guided / Learned Correction / World-Model-Mediated`，因为预测未来只进入评分器，未回流到轨迹解码器修改 $T^k$。论文使用固定 $K=6$，因此更准确地说是 width-style inference topology；它没有单独证明随测试时增大 $K$ 而持续提升的 scaling law。
- **[DreamTrajectory](https://arxiv.org/abs/2608.01381)** 2026-08-02｜arXiv 预印本，暂无公开刊会录用信息
	- **前序工作问题**
		- **Abstract / Introduction：** 移动操作需要联合协调底盘和机械臂，直接在高维 whole-body action space 中生成 action chunk 缺少显式任务空间意图；开环执行又无法检查控制误差和接触是否让实际运动偏离计划。
		- **Related Work：** 普通 VLA 将 task-space trajectory 隐藏在动作分布里，已有世界模型常生成完整视频，代价高且没有直接针对“候选动作能否实现本次意图轨迹”进行对齐。
	- **动机与方法**
		- Action expert 联合输出 intention-level end-effector trajectory 与 whole-body action chunk；部署时采样多个动作候选，轻量 trajectory world model 分别预测它们会诱导的末端轨迹，再选出与计划轨迹最一致者。
	- **核心创新点（一句话）**
		- DreamTrajectory 把世界模型压缩到与控制直接相关的轨迹空间，用 `search -> predict -> score` 在多候选中检查“动作是否真正实现意图”，而不必生成像素级未来。
	- 规划操作：`Selection`。最终动作仍来自原候选集，世界预测只用于评分，没有反向修改 action chunk。
	- 反馈来源：Action-conditioned predicted end-effector trajectory 与 intention trajectory 的 alignment score。
	- 训练—部署：训练 action expert 与 lightweight trajectory world model；部署时执行多候选预测和一次 world-feedback selection。

##### 1.3 Verifier-free Selection
不引入另行训练的外部评价器，而是使用模型内部统计量、候选间几何关系或解析控制目标选择候选。
- **[MG-Select](https://arxiv.org/abs/2510.05681)** 2025-10-07｜ICLR 2026
	- **前序工作问题**
		- **Abstract / Introduction：** 单次 VLA 推理精度有限，但额外 verifier 需要训练且可能无法泛化（如 V-GPS）；直接按照模型 likelihood 选择也会因策略分布过度集中而失效。
		- **Related Work：** 外部评分器增加系统复杂度，自一致性只能寻找高频模式；原始条件概率还混合了动作先验与当前视觉、语言条件真正贡献的信息。
	- **动机与方法**
		- 好候选应当对当前条件高度敏感，而不是仅仅在无条件情况下常见；因此构造 condition-masked reference distribution，用候选相对参考分布的 KL divergence 作为模型内部置信度。
	- **核心创新点（一句话）**
		- MG-Select 用条件分布相对 condition-masked reference distribution 的 KL 增量衡量动作对当前观测与指令的依赖程度，由此在不训练外部 verifier 的情况下完成可信的 Best-of-N 选择。
	- **论文图解**
		- **Figure 1｜无需外部 verifier 的内部打分流程**
			![[assets/trajectory-planning-vla-tts/mg-select-fig1-overview.png]]
			- **读图：** VLA 并行采样 N 组 action tokens，同时把状态、语言等条件遮蔽得到 reference distribution；逐 token 计算条件分布相对参考分布的 KL，聚合成每个候选的 condition-masking confidence，再执行 Best-of-N。
			- **为什么只有这一张：** 论文没有单独的问题示意图；Figure 1 已经完整表达“likelihood 不可靠 → 用条件信息增量作为置信度”的核心机制，其余图主要是实验、延迟和失败案例。
	- 规划操作：`Selection`。从多个候选动作中选择一个，但不使用独立的外部评价模型。
	- 评价信号：候选动作分布相对于 condition-masked reference distribution 的 KL divergence。该差异度被用作候选动作与当前条件关联程度的内部置信度信号。
	- 反馈来源：模型内部置信度统计。选择依据来自 VLA 自身的分布变化，而不是另一个 verifier 的判断。
	- 训练—部署：不额外训练 verifier；部署时进行 Width Sampling 和内部打分。因此额外开销主要来自多次采样和分布统计。
- **[Bidirectional Decoding (BID)](https://arxiv.org/abs/2408.17355)** 2024-08-30｜ICLR 2025
	- **前序工作问题**
		- **Abstract / Introduction：** 长 action chunk 能保留示范中的长期策略和时间依赖，却因较少读取新观测而反应迟缓；每步重采样虽更灵敏，却可能在多模态策略之间跳变并产生抖动。
		- **Related Work：** Temporal ensembling 会平均不相容的动作模式，独立采样不保持跨 chunk 一致性，另训 value / reward model 又增加数据和泛化负担。
	- **动机与方法**
		- 每个时间步并行采样 $N$ 个 action chunks，以 backward coherence 衡量候选与上一轮已选 chunk 的重叠一致性，再以 forward contrast 奖励接近强策略样本、远离弱 checkpoint 样本的候选，最后最小化二者之和。
	- **核心创新点（一句话）**
		- BID 不训练外部 verifier，而用“向后保持已选策略、向前偏向强策略”的双向几何准则完成测试时选择，在一致性与对新观测的反应性之间折中。
	- 规划操作：`Selection`。候选一次并行产生，评分不会更新本轮的生成分布。
	- 评价信号：跨 chunk 的动作距离与 strong/weak policy 样本对比；它是 sample-relative decoding objective，不是预测长期回报的 critic。
	- 扩展证据：论文显式增加 batch size，并观察到默认规模尚未饱和；因此 BID 比“固定 $N$ 的多模态解码”更直接支持 width scaling。
	- 训练—部署：复用 strong policy 及其较早的 weak checkpoint，部署时批量采样和比较，不额外拟合 verifier。
- **[RACE](https://openreview.net/forum?id=INsLvSCJ4z)** 2025-09-10｜ICLR 2026
	- **前序工作问题**
		- **Abstract / Introduction：** 将 action chunk 简单提频会改变控制动力学并违反短时可达性；异步推理时新 chunk 基于过期状态生成，直接切换还会造成状态不连续。
		- **Related Work：** 固定速率 chunk execution 继承慢速示范，单纯丢弃延迟期间的旧动作不能保证新计划从机器人当前状态平滑可控地接入。
	- **动机与方法**
		- 先把监督目标从 commanded actions 改为实际 reached states，再用 reachability-aware time-optimal path parameterization 在速度、力矩等物理限制下重定时；部署时对未来 action chunks 做批量搜索，选择从当前状态接入后最平滑、最可控的一条。
	- **核心创新点（一句话）**
		- RACE 将“更快执行”分解为可达状态预测、动力学可行的时间重参数化和延迟感知的 test-time chunk search，使策略能够快于示范而不靠盲目提频。
	- 规划操作：其中 test-time search 是 `Selection`，解析目标来自当前状态的 controllability / smoothness，而非另训 verifier；时间重参数化则是同一系统中的局部轨迹优化。
	- 训练—部署：reached-state target 需要重新训练或转换 policy；测试时搜索本身不更新网络参数。因此它是训练改造与在线解析搜索的组合，不是纯黑盒 wrapper。

##### 1.4 Self-Consistency Aggregation

- 规划操作：`Selection / Aggregation`。根据多个候选之间的共识选出代表性结果，而不是逐个预测绝对价值。
- 评价信号：候选之间的多数关系、聚类中心、动作一致性或语义共识。这些信号衡量某个候选是否接近模型反复产生的主流答案。
- 训练—部署：通常不需要外部 verifier，部署时对并行候选进行聚合。测试时增加的是候选采样次数和共识计算。
- 局限：候选高度相关时，共识只代表模型最常输出的模式，不一定代表最安全或价值最高的动作。共同出现的系统性错误也可能被一致性规则选中。

#### 2. 非耦合迭代精化：Uncoupled Iterative Refinement

**符号：** $N$ 是候选数，$R$ 是一次真实动作执行前的候选生成或更新轮数，$C$ 表示不同候选是否通过评分、统计量或其他搜索状态相互影响。**本类：** $R>1,C=0$，即至少进行两轮生成或更新，且不同候选链之间不交换信息。

每条链内部都沿推理、生成或修正步骤串行推进：

`aᵢ⁽¹⁾ -> aᵢ⁽²⁾ -> ... -> aᵢ⁽ᴿ⁾,  i=1,...,N`

本类包含两个互斥的拓扑子形态：

| 子形态 | 条件 | 当前决策内维护的状态 | 拓扑标签 |
| --- | --- | --- | --- |
| 单链串行精化 | $N=1,R>1,C=0$ | 一个当前解连续更新 | `Sequential Depth` |
| 独立多链精化 | $N>1,R>1,C=0$ | 多个当前解各自更新，链间不交换信息 | `Width × Depth` / `Independent Multi-Chain` |

独立多链与第 1 类并不重复：第 1 类只生成一轮候选，满足 $R=1$；独立多链中的每个候选都要经历多轮更新，满足 $R>1$。它也不同于第 3 类，因为任何一条链的下一状态都不依赖其他链的评分、精英统计、全局最优或 proposal parameters。


| 机制家族 | 更新子链 | 迭代状态 | 产生下一状态的近端机制 | 核心判别 |
| --- | --- | --- | --- | --- |
| Generative Refinement | Generative Refinement | 动作或轨迹的中间生成状态 | 训练得到的 generator、denoiser 或 refiner 直接执行生成变换 | 下一状态主要由学习到的生成动力学产生，不经过独立评价或局部求解 |
| Rethinking / Iterative Reasoning | Rethinking / Iterative Reasoning | 推理链、语言化方案或语义状态 | reasoning / rewriting operator 延续、检查或改写当前推理状态 | 串行计算主要推进语义推理，动作由更新后的推理状态产生 |
| Evaluation-Guided Refinement | Feedback-Conditioned Learned Refinement | 已生成的动作或轨迹 | 候选评价形成反馈，feedback-conditioned learned refiner 再修改当前解 | 评价反馈由学习到的修正算子解释并转化为下一状态 |
| Evaluation-Guided Refinement | Objective-Guided Continuous Refinement | 连续动作或轨迹变量 | gradient、序列凸优化、投影等局部优化算子直接求出下一迭代点 | 评价信息被写成显式目标或约束，并由数学求解步骤直接转化为更新量 |

分类时只检查推理或规划阶段真实执行的数据流。训练损失、初始 proposal、静态条件和最终候选选择都不单独构成 refinement 类型。`Evaluation-Guided Refinement` 是评价信息回流的上位标签，不能单独说明下一状态由 learned refiner 还是数学求解器产生；完整标注还应带上 2.3.1 或 2.3.2 的更新子链。若同一步更新包含可独立辨认的多个项，则采用组合标注。例如，普通条件去噪属于 Generative Refinement，在去噪均值之外注入 cost gradient 时标注为 `Generative Refinement + Objective Guidance (2.3.2)`。direct verifier、world model、解析代价或真实约束说明评价从哪里来；它们只有通过具体更新算子回流到下一候选时，才构成对应的 refinement 子链。

采样本身不改变上述归类。围绕一个当前解生成、并在估计梯度或更新方向后立即丢弃的局部扰动，不增加候选链数量；该方法仍是 `Sequential Depth`，只需附记“瞬时评价采样”。例如，单条 guided diffusion 可标为 `Sequential Depth × Generative Refinement + Objective Guidance × Stochastic Denoising`；同时运行多条互不通信的 guided diffusion 链则标为 `Width × Depth × Generative Refinement + Objective Guidance`。只有跨链信息共同改变下一轮候选或 proposal state 时，才转入第 3 类。

##### 2.1 Generative Refinement

Generative Refinement 的近端更新机制是训练得到的生成变换。系统在同一个规划时刻维护动作或轨迹的中间生成状态，并沿单条生成链反复调用 generator、denoiser 或 refiner，直至得到最终方案：

`z⁽ʳ⁺¹⁾ = Gθ(z⁽ʳ⁾, c, r),  a = Dec(z⁽ᴿ⁾)`

其中，`z⁽ʳ⁾` 可以是噪声轨迹、anchor 附近的扰动轨迹，也可以是判别式模型给出的粗 proposal；`c` 表示场景观测、语言指令或 BEV 特征等生成条件。只要 `c` 不由当前候选的独立评价结果在迭代间更新，它仍只是生成条件。若系统另行产生候选分数、critique 或预测后果，并由 learned refiner 解释这些反馈，则组合标注 2.3.1 Feedback-Conditioned Learned Refinement，并记录 direct-verifier 或 world-model-mediated feedback；若生成更新中显式加入由目标函数计算的梯度或局部求解项，则组合标注 `Generative Refinement + Objective Guidance (2.3.2)`。

DiffusionDrive 和 DiffRefiner 的共同点正是直接细化动作级生成状态：前者从 anchor-conditioned distribution 开始截断去噪，后者从 scene-adaptive coarse proposal 开始条件去噪。两者的归类依据是生成链的更新机制，而不是方法名称中是否出现 diffusion 或 refiner。

- **[DiffusionDrive](https://arxiv.org/abs/2411.15139)** 2024-11-22｜CVPR 2025 Highlight
	- **前序工作问题**
		- **Abstract / Introduction：** 标准扩散规划需要大量去噪步，难以满足实时驾驶；单一轨迹回归又会平均多个合理驾驶意图，无法表达交通行为的多模态性。
		- **Related Work：** 传统 anchor planner 的候选覆盖有限，vanilla diffusion 灵活但推理慢，直接减少去噪步数则可能明显损害轨迹质量。
	- **动机与方法**
		- 先用驾驶先验缩小生成空间，就不必从纯噪声进行长链生成；因此以多模态轨迹 anchors 为起点，结合 truncated diffusion schedule 和 cascade decoder，用约两步去噪生成多样轨迹。
	- **核心创新点（一句话）**
		- DiffusionDrive 把多模态 anchors 变成 anchored Gaussian 生成先验，并以截断扩散和级联场景交互解码器取代从纯噪声开始的长链采样，使 diffusion 首次兼顾开放场景中的轨迹多样性与实时规划速度。
	- **论文图解**
		- **Figure 1｜四种端到端轨迹生成范式的差别**
			![[assets/trajectory-planning-vla-tts/diffusiondrive-fig1-paradigms.png]]
			- **读图：** (a) 单模态回归只给一条轨迹；(b) 轨迹词表通过离散 anchors 获得多模态但受覆盖范围限制；(c) vanilla diffusion 从宽高斯分布开始，灵活但去噪重；(d) DiffusionDrive 从 anchor-conditioned distribution 开始截断去噪。
			- **问题—方法对应：** 它不是放弃 anchors，而是把 anchors 从“最终离散答案”改成“生成分布的先验中心”，兼顾多模态覆盖和较短采样链。
		- **Figure 3｜Truncated diffusion 的核心机制**
			![[assets/trajectory-planning-vla-tts/diffusiondrive-fig3-truncated-diffusion.png]]
			- **读图：** 上方 vanilla diffusion 把真值扩散到近似纯噪声后再完整逆推；下方只在 anchors 周围加入较小噪声，并从 anchored Gaussian distribution 开始反向过程，因此训练和推理都只保留截断区间。
			- **本质：** 推理加速来自更好的初始分布和更短的 diffusion horizon，不是 verifier 根据错误进行反馈修正。
		- **Figure 4｜整体架构与 cascade decoder**
			![[assets/trajectory-planning-vla-tts/diffusiondrive-fig4-architecture.png]]
			- **读图：** 感知模块产生场景和 agent 条件，anchor 分布采样出 noisy trajectories；cascade diffusion decoder 在每层重新与条件场景交互并逐步去噪，最后输出多条轨迹及其分数。
	- 规划操作：`Refinement`。轨迹通过连续去噪从粗糙状态逐步变得完整，而不是只在固定候选中进行一次选择。
	- 更新对象：单条轨迹或多模态轨迹 anchors。Anchors 是预先给出的粗轨迹模式，扩散过程在其附近生成更具体的轨迹。
	- 反馈来源：固定的扩散去噪动力学，不依赖显式错误评价。每一步更新由训练得到的去噪器决定，而不是由 verifier 指出当前轨迹的错误。
	- 训练—部署：训练扩散式轨迹生成器；部署时以约两步截断去噪生成轨迹。截断去噪用于减少标准多步扩散的推理延迟。
	- 分类说明：去噪过程不应直接称为 Self-Correction，因为它未必包含“评价错误—定向修改”的显式反馈环。
- **[DiffRefiner](https://arxiv.org/abs/2511.17150)** 2025-11-21｜AAAI 2026
	- **前序工作问题**
		- **Abstract / Introduction：** 单次回归虽然高效，但会对多种驾驶行为求平均，在复杂路口产生次优轨迹；基于大量离散 anchors 的分类方法又会随候选规模增加显著提高计算量。
		- **Related Work：** Anchor-based discriminative planner 的上限受固定候选覆盖范围限制；DiffusionDrive 等生成方法能表达多模态未来，但从无结构高斯噪声或固定轨迹 anchors 开始去噪，初始化缺少场景自适应，偏离可行运动分布时需要更多迭代。既有感知辅助规划还多依赖隐式特征交互，缺少轨迹与可行驶区域、障碍物等语义的细粒度对齐。
	- **动机与方法**
		- 判别式 proposal 擅长快速给出正确的大致运动趋势，diffusion 擅长刻画其附近的复杂分布和局部细节；因此采用 coarse-to-fine 两阶段结构：Proposal Decoder 先依据场景调整预定义 anchors 生成粗轨迹，Diffusion Refiner 再对这些 scene-adaptive proposals 迭代去噪。
		- 为避免 refinement 只在轨迹空间盲目修形，Fine-Grained Semantic Interaction Module 先用 cross-attention 建立轨迹与全局 BEV 语义的对应，再用 deformable attention 对齐轨迹端点与局部关键区域，最后通过 adaptive gate 融合全局和局部约束。
	- **核心创新点（一句话）**
		- DiffRefiner 将判别式 decoder 产生的 scene-adaptive 粗轨迹作为**扩散初值**，再用 FGSIM **把道路和交通参与者语义显式注入去噪过程**，以“**强初值 + 语义约束细化**”同时突破固定 anchors 的覆盖上限和纯 diffusion 的低效盲目采样。
	- **论文图解**
		- **Figure 1｜判别式、纯扩散与粗到细范式对比**
			![[assets/trajectory-planning-vla-tts/diffrefiner-fig1-paradigms.png]]
			- **读图：** (a) 判别式 planning head 直接从 BEV 输出固定候选；(b) 单阶段 diffusion 从 anchor-conditioned distribution 或 Gaussian noise 直接去噪；(c) DiffRefiner 先产生 scene-adaptive proposal，再引入 semantic BEV feature 做 refinement。
			- **问题—方法对应：** 论文要解决是它的初始化不随场景调整、去噪过程与道路和障碍语义交互不够细。
		- **Figure 2｜完整 coarse-to-fine 数据流**
			![[assets/trajectory-planning-vla-tts/diffrefiner-fig2-overview.png]]
			- **读图：** Camera 经 BEV encoder 后进入检测、分割和 planning token；Proposal Decoder 先调整预定义 anchors，训练时对 proposal 加噪，Diffusion Refiner 再结合 BEV/semantic BEV 产生最终 refinement。
			- **两阶段分工：** Proposal 负责大致运动趋势和可行初值，Refiner 负责连续分布、局部几何与环境一致性。
		- **Figure 3｜FGSIM 如何注入细粒度场景约束**
			![[assets/trajectory-planning-vla-tts/diffrefiner-fig3-fgsim.png]]
			- **读图：** Refiner 同时读取 proposal、spatial BEV、可行驶区域语义、交通参与者语义、agent token 和 planning token；FGSIM 将 projected semantic BEV 与 refiner query 做全局 cross-attention、局部对齐和 gate fusion，再输出更新后的 query。
			- **核心价值：** 这一步让去噪不再只是轨迹坐标上的平滑，而是显式服从道路、障碍物与交通参与者约束。
	- 规划操作：`Coarse Proposal + Generative Refinement`。第一阶段生成可行初值，第二阶段修改候选本身，不只是从固定候选中选择。
	- 更新对象：Proposal Decoder 调整后的多模态粗轨迹；扩散过程在这些 scene-adaptive proposals 附近细化几何与环境一致性。
	- 反馈来源：训练得到的条件去噪动力学和显式 BEV semantic interaction，不包含独立 verifier、真实执行反馈或测试时 reward。
	- 训练—部署：联合训练感知、proposal 与 diffusion refiner；部署时顺序执行粗轨迹生成和条件去噪，因此属于 Sequential Depth / Generative Refinement。
	- 与 DiffusionDrive 的关键区别：DiffusionDrive 直接从固定 anchor 参数化分布进行截断去噪；DiffRefiner 先用判别式 decoder 把 anchors 调整为当前场景的粗 proposal，再执行带显式语义交互的扩散细化。
- **[SGAC](https://arxiv.org/abs/2510.12392)** 2025-10-14｜NeurIPS 2025 Main Conference
	- **前序工作问题**
		- **Abstract / Introduction：** Diffusion Policy 的随机采样偶尔产生低保真 action chunk；长开环执行对动态变化反应慢，而每步闭环重采样又会在多模态动作间跳变。
		- **Related Work：** BID 通过多候选搜索保持跨 chunk 一致性，但计算随采样数增加；普通 classifier-free guidance 需要额外无条件分支，也没有直接利用上一时刻的观测与动作语境。
	- **动机与方法**
		- Self-Guidance 将上一时刻条件下的 score / noise prediction 作为 negative guidance，在每个去噪步强化当前观测相对过去观测带来的分布变化；Adaptive Chunking 再比较上一计划与新计划的动作相似性，只在反应收益超过一致性损失时替换执行队列。
	- **核心创新点（一句话）**
		- SGAC 用过去条件构造无需外部 verifier 的扩散负引导，并以动作相似性决定何时重规划，同时处理采样保真度与开环—闭环一致性冲突。
	- 规划操作：`Generative Guidance + Adaptive Chunk Execution`。前者直接改变每步去噪方向，属于 Depth；后者跨环境时间决定是否接纳新 chunk，属于闭环执行门控。
	- 反馈来源：当前与过去条件下的模型自身 score / feature，以及新旧 action chunks 的相似性；不产生独立 reward 或世界 rollout。
	- 训练—部署：原始 diffusion self-guidance 可直接用于冻结策略；论文还给出对非 diffusion VLA 的 feature-space activation steering 扩展。其改进来自特定推理规则，不应表述为任意增加去噪步都单调变好。
- **[General Policy Composition (GPC)](https://arxiv.org/abs/2510.01068)** 2025-10-01｜ICLR 2026
	- **前序工作问题**
		- **Abstract / Introduction：** 不同 VA/VLA、视觉模态和 diffusion/flow policies 各有互补强项，但通常只能单独部署；为每个组合重新收集机器人数据和训练新策略成本很高。
		- **Related Work：** 输出动作平均会落到多模态分布之间的低密度区域，普通 ensemble 也没有在生成过程中组合各 policy 的 distributional score。
	- **动机与方法**
		- 冻结多个预训练生成策略，在每个 denoising / flow integration step 对它们的 score 做凸组合，再用组合 score 更新同一 noisy trajectory；外层枚举 composition weights，并依据任务 rollout 的 success-rate reward pool 选择权重。
	- **核心创新点（一句话）**
		- GPC 在生成分布层而非最终动作层组合异构 robot policies，使多个冻结模型能通过测试时 score composition 产生任一 parent policy 都未直接输出的新轨迹。
	- 规划操作：内部是 `Multi-Policy Guided Generative Refinement`；外层是对 composition coefficient 的 task-level search，形成 `Width over Weights × Denoising Depth`。
	- 分类边界：权重搜索通过 rollout success rate 选定任务级组合，并不按每个决策时刻的候选反馈反复拟合 proposal distribution，所以不能归为 TOAD/CEM 式 Adaptive Population Scaling。
	- 训练—部署：parent policies 全部冻结、无需额外训练；部署计算随参与 policy 数、权重候选数和生成深度增长。

##### 2.2 Rethinking / Iterative Reasoning

Rethinking / Iterative Reasoning 的近端更新机制是 reasoning 或 rewriting operator。模型在同一个任务和同一组观测条件下，将上一轮产生的推理状态或语义方案重新作为输入，继续推导、检查或改写，并依据更新后的推理状态生成动作：

`h⁽ʳ⁺¹⁾ = Rθ(o, h⁽ʳ⁾),  a = πθ(o, h⁽ᴿ⁾)`

其中，`h⁽ʳ⁾` 可以是语言推理链、结构化计划或模型内部的语义状态。这里被反复推进的是“如何理解问题和组织方案”，而不是噪声轨迹、anchor 或粗 proposal 等动作级生成状态。更新信息主要来自已有推理上下文，不包含动作真实执行后的新观测，也不以独立候选评价或数值局部求解作为下一状态的直接生成机制。

这一机制的判别依据是继续推理本身产生下一语义状态。如果某个阶段专门针对当前候选输出正确性分数、critique 或修正方向，并由反馈条件化的 refiner 产生下一候选，即使评价阶段与 actor 使用同一个 backbone，近端机制也属于 2.3.1 Feedback-Conditioned Learned Refinement，反馈来源标为 Direct Verifier；若 reasoning transition 与独立评价反馈在同一步中均直接参与更新，则使用组合标注。缺少可检查中间状态或外部反馈时，增加推理轮次并不保证方案持续改善，后续推理仍可能重复或放大原有错误。

这里的 $r$ 应对应可辨认的语义阶段，例如从任务理解推进到子任务、运动约束和动作，而不是把每个底层 token 都机械地计作一轮 refinement。本类也不要求模型先输出一份完整方案再“反省”；只要前序推理状态直接条件化下一语义状态，且动作在这条推理链之后产生，就满足其近端机制判据。

- **[Embodied Chain-of-Thought（ECoT）](https://proceedings.mlr.press/v270/zawalski25a.html)** 2024-07-11｜CoRL 2024，PMLR 270（2025）
	- **前序工作问题：** 标准 VLA 通常从观测和指令直接预测动作；普通语言 CoT 又多停留在高层语义分解，缺少物体位置、末端执行器位置和具体运动等视觉—控制落地信息。
	- **动机与方法：** ECoT 为机器人数据合成具身推理监督，使 VLA 在动作 token 之前依次生成任务、计划、当前子任务、运动和视觉落地状态。前序推理 token 进入后续自回归上下文，最终动作由整条推理状态条件化产生。
	- **核心创新点（一句话）：** 把语言式任务推理扩展为包含计划、子任务、运动和视觉 grounding 的动作前推理链，使中间语义状态能够直接连接高层任务与低层控制。
	- **论文图解**
		- **Figure 3｜具身推理状态怎样逐步过渡到动作**
			![[assets/trajectory-planning-vla-tts/ecot-fig3-reasoning-steps.png]]
			- **读图：** 同一图像与指令先进入 `TASK` 和 `PLAN`，再推进到当前 `SUBTASK`、低层 `MOVE`、gripper position 与 visible objects，最后才产生 robot action。绿色部分组织高层任务，紫色部分把推理落到机器人状态和视觉坐标。
			- **分类含义：** 前序语义阶段直接成为后续阶段的自回归上下文，整条链只维护一个 reasoning state，没有并行候选、独立评价器或候选间反馈，因此对应 `Sequential Depth × Rethinking / Iterative Reasoning`。
		- **Figure 4｜大规模合成 ECoT 训练数据的管线**
			![[assets/trajectory-planning-vla-tts/ecot-fig4-data-pipeline.png]]
			- **读图：** 论文从已有机器人轨迹提取任务、观测和 proprioception，再用 Prismatic-VLM 生成场景描述、Grounding DINO 提取物体框、运动学状态计算 motion primitives、OWL 与 SAM 定位 gripper，最后交给 Gemini 生成计划和子任务标注。
			- **训练—部署边界：** 这些视觉模型和 LLM 组成的是离线标注生成管线，不代表部署时每个动作都依次调用全部模块。部署拓扑应按训练后的 VLA 如何自回归生成 reasoning tokens 与 action tokens 判定，不能把训练数据处理步骤计入测试时 $R$。
	- 规划操作：`Reasoning → Action`。它增加的是动作前的语义推理深度，不维护待比较的候选群体。
	- 更新对象：单条自回归具身推理状态；每个语义阶段读取当前观测、任务和已有推理上下文，再产生下一阶段或动作。
	- 反馈来源：模型已有推理上下文与视觉观测，不包含独立 verifier、候选分数或 imagined rollout。
	- 训练—部署：用合成 ECoT 标注微调 VLA；部署时先生成多阶段具身推理，再生成动作，因此额外计算表现为 `Sequential Depth`。
	- 分类说明：这里归入 Rethinking / Iterative Reasoning，是因为 reasoning transition 直接产生下一语义状态；ECoT 并不是“完整动作生成后再评价并纠错”的 Self-Correction。
- **[CoA-VLA](https://ieeexplore.ieee.org/document/11445407)** 2024-12-29｜ICCV 2025，pp. 9759–9769｜[CVF Open Access](https://openaccess.thecvf.com/content/ICCV2025/html/Li_CoA-VLA_Improving_Vision-Language-Action_Models_via_Visual-Text_Chain-of-Affordance_ICCV_2025_paper.html)｜[DOI: 10.1109/ICCV51701.2025.00910](https://doi.org/10.1109/ICCV51701.2025.00910)
	- **前序工作问题：** 直接动作预测缺少对“操作什么、在哪里抓、放到哪里、怎样避障移动”的显式组织；只做高层任务分解又难以落到低层连续控制。
	- **动机与方法：** CoA-VLA 把 object、grasp、spatial 和 movement affordance 组织为有序的视觉—文本 reasoning chain，并根据任务进度动态生成当前需要的 affordance。该中间语义状态随后通过视觉—语言 co-injection 条件化 diffusion action head。
	- **核心创新点（一句话）：** 用渐进式 Chain-of-Affordance 把物体、抓取、放置空间和运动路径串成可落地的中间推理状态，再以这些状态约束连续动作生成。
	- **论文图解**
		- **Figure 1｜Chain-of-Affordance 与 diffusion action head 的整体数据流**
			![[assets/trajectory-planning-vla-tts/coa-vla-fig1-framework.png]]
			- **读图：** Observation 和 instruction 进入 LLM 后，系统动态选择当前需要的 textual / visual affordance，将二者经 co-injection 投影到 diffusion model；右侧把 object、grasp、spatial、movement 四类 affordance 串成语义链，下方给出各类视觉—文本 grounding 的实例。
			- **分类含义：** 图中存在两种可独立辨认的近端机制：LLM 推进 affordance reasoning state，diffusion model 再更新连续动作生成状态。因此完整方法不能只写成纯 Rethinking，应采用 `Rethinking / Iterative Reasoning + Generative Refinement`。
		- **Figure 2｜PourTea 任务中的动态 affordance chain**
			![[assets/trajectory-planning-vla-tts/coa-vla-fig2-affordance-chain.png]]
			- **读图：** 机器人先定位并抓取 cup，再推理放置位置与移动路径，随后定位并抓取 teapot，最后完成倾倒；每个阶段只生成当前必要的 object、grasp、spatial 或 movement affordance，避免在所有时刻重复输出全部四类状态。
			- **时间轴边界：** 该图横轴包含动作真实执行后的任务推进，不应把整条时间轴机械视为同一次决策内的 $R$ 轮 refinement。2.2 标签针对的是每次动作生成前的 affordance reasoning 子链；跨真实环境时刻的状态变化仍属于执行层时间轴。
	- 规划操作：`Affordance Reasoning → Generative Action Decoding`。
	- 更新对象：前半段是逐步展开的视觉—文本 affordance state，后半段是 diffusion head 中的连续动作生成状态。
	- 反馈来源：当前观测、任务描述、已有 affordance 上下文和 proprioception；没有独立候选 evaluator。
	- 训练—部署：训练时自动构造并注入 affordance 监督；部署时动态选择并生成必要的 affordance，再据此生成动作。
	- 分类说明：affordance reasoning 子链属于 Rethinking / Iterative Reasoning，但完整方法还包含 diffusion action head，因此方法级标签应写成 `Rethinking / Iterative Reasoning + Generative Refinement`，不能把后者抹去。

名称中出现 iterative 或 self-refinement 也不自动属于本类。例如，[ISR-LLM](https://arxiv.org/abs/2308.13724) 先生成完整计划，再由 validator 评价并把验证结果用于下一轮修正；按本文的数据流判据，它属于 `Feedback-Conditioned Learned Refinement / Direct Verifier Feedback`，而不是纯 Rethinking / Iterative Reasoning。

##### 2.3 评价信息引导的精化：Evaluation-Guided Refinement

Evaluation-Guided Refinement 是评价信息回流的上位机制家族。系统先对当前候选的质量、风险、任务进度或约束违反程度形成评价，再让该评价实际改变下一候选，而不是只在计算结束时从固定集合中选出一个结果：

`q⁽ʳ⁾ = Assess(o, x⁽ʳ⁾),  x⁽ʳ⁺¹⁾ = Update(x⁽ʳ⁾, q⁽ʳ⁾)`

这一上位标签只说明评价信息进入更新闭环，不把所有评价模块都叫作 verifier，也不预先规定 `Update` 的实现。按照评价怎样转化为下一状态，2.3 分成两条更新子链：

| 更新子链 | 评价表示 | 评价到更新的路径 | 子类标签 |
| --- | --- | --- | --- |
| 评价反馈条件化的学习式精化 | 分数、错误类型、critique、修正向量或预测后果 | `f⁽ʳ⁾ = Assess(o,a⁽ʳ⁾) -> a⁽ʳ⁺¹⁾ = Uθ(o,a⁽ʳ⁾,f⁽ʳ⁾)` | `Feedback-Conditioned Learned Refinement` |
| 目标引导的连续精化 | 显式目标、代价或约束 | `d⁽ʳ⁾ = LocalSolve(τ⁽ʳ⁾;J,𝒞) -> τ⁽ʳ⁺¹⁾ = Π𝒞(τ⁽ʳ⁾+d⁽ʳ⁾)` | `Objective-Guided Continuous Refinement` |

若评价结果只对固定候选排序而不产生新候选，方法仍属于第 1 类的 verifier-based 或 world-feedback selection。direct verifier 和 world model 可以为两条更新子链提供信息，但二者只是评价来源：反馈由 learned refiner 解释时归入 2.3.1，评价被写成目标或约束并由梯度、投影或局部求解器直接转成更新量时归入 2.3.2。动作真实执行后才获得的新观测不属于这两条内部评价子链，而属于 Environment-Feedback Replanning。

###### 2.3.1 评价反馈条件化的学习式精化：Feedback-Conditioned Learned Refinement

本子类先针对当前候选形成评价反馈，再由学习到的 `Uθ` 解释反馈并产生下一候选：

`f⁽ʳ⁾ = Assess(o, a⁽ʳ⁾),  a⁽ʳ⁺¹⁾ = Uθ(o, a⁽ʳ⁾, f⁽ʳ⁾)`

`Assess` 可以直接读取候选，也可以先借助世界模型取得候选后果。两种反馈来源共享同一个 learned-update 判据，差别只在候选证据怎样构造：

| 反馈来源变体 | 评价路径 | 反馈内容 | 来源标签 |
| --- | --- | --- | --- |
| 直接候选验证 | `f⁽ʳ⁾ = Vφ(o,a⁽ʳ⁾)` | 安全分数、错误类型、critique 或修正向量 | `Direct Verifier Feedback` |
| 世界模型介导的后果评价 | `ŝ⁽ʳ⁾ = Wφ(o,a⁽ʳ⁾), f⁽ʳ⁾ = E(ŝ⁽ʳ⁾)` | imagined outcome、任务进度、风险或未来状态偏差 | `World-Model-Mediated Outcome Feedback` |

世界模型本身只负责回答“执行该候选后可能发生什么”，并不天然等同于 verifier；只有预测结果经过 outcome evaluator 转化为候选级判断时，复合模块 `E∘W` 才形成可供 `Uθ` 使用的后果评价。

**反馈来源 A｜直接验证器反馈（Direct Verifier Feedback）**

本子类直接读取当前候选并输出评价反馈，不先生成该候选对应的未来。verifier 与 refiner 可以使用不同模型，也可以共享 backbone；判别依据是候选级评价是否控制保留、重写或定向修改路径。

- **[DriveVer](https://arxiv.org/abs/2607.00399)** 2026-07-01｜IROS 2026
	- **前序工作问题**
		- **Abstract / Introduction：** 持续扩大训练数据和模型规模成本很高且收益递减；one-shot planner 输出轨迹后没有验证和修正环节，小的危险偏差也可能被直接执行。
		- **Related Work：** 普通 trajectory scorer 只能排序，rule-based 后处理不理解场景语义；通用大模型 verifier 较重，而且通常不能输出精确的几何修改量。
	- **动机与方法**
		- 轻量 verifier 应同时回答“轨迹是否安全”和“应该向哪里修正”；因此训练双头评价器输出 safety score 与 absolute refinement vector，作为底层 planner 的测试时后处理。【从哪里修正】
	- **核心创新点（一句话）**
		- DriveVer 用一个可插拔的轻量双头 verifier 同时预测干预置信度和绝对几何修正方向，使测试时验证从“只判断或排序”升级为“按需直接修复任意 planner 的初始轨迹”。
	- **论文图解**
		- **Figure 1｜One-shot planning 与 test-time verification 的范式差别**
			![[assets/trajectory-planning-vla-tts/drivever-fig1-paradigms.png]]
			- **读图：** 上方 base planner 输出一条轨迹后直接执行，错误没有第二道检查；下方 DriveVer 接收同一场景、ego state 和初始轨迹，先给安全分数，再在需要时输出修正轨迹。
			- **问题定位：** 这里新增的是“执行前验证—修正”层，不要求替换或重新训练原 planner。
		- **Figure 2｜双头轻量 verifier**
			![[assets/trajectory-planning-vla-tts/drivever-fig2-architecture.png]]
			- **读图：** Image、ego state 和 initial trajectory 分别编码为 scene/state/trajectory tokens；共享输入进入 confidence branch 和 refinement branch，前者决定是否干预，后者给出几何 refinement direction，阈值门控决定保留原轨迹还是执行修正轨迹。
			- **区别于 scorer：** Confidence head 不是最终功能；真正突破“只能排序”的是 refinement head 能够生成原候选之外的几何修改量。
	- 规划操作：`Refinement`。评价器不仅判断粗轨迹是否可靠，还直接给出用于修改轨迹的几何修正量。
	- 评价器：Trajectory verifier。它面向轨迹输出安全置信度和 refinement vector，同时承担验证与修正功能。
	- 反馈来源：轨迹安全置信度与绝对几何 refinement vector。前者表示轨迹是否可信，后者表示轨迹应向哪个方向、移动多少距离。
	- 训练—部署：训练轻量轨迹评价器；部署时验证并修正底层 planner 的候选。底层规划器负责提出粗轨迹，DriveVer 负责后处理。
- **[CriticVLA](https://arxiv.org/abs/2604.27366)** 2026-04-30｜arXiv 预印本，暂无公开刊会录用信息
	- **前序工作问题**
		- **Abstract / Introduction：** 现有 VLA 通常只被当作动作生成器（actor），将视觉与语言输入直接映射为控制信号；其联合理解场景、指令和动作并据此判断决策质量的能力，很少被用于检查和修正自身轨迹。
		- **Related Work：** 多候选选择只能在已有结果中挑选，无法创造修正后的轨迹；语言式 critique 难落到连续控制，反复迭代优化又不符合实时要求。
	- **动机与方法**
		- 同一个多模态模型不只可以“提出动作”，还可以充当评价者：读取当前场景、驾驶指令和粗轨迹，识别碰撞、速度、方向等风险，并给出可执行的修改建议。CriticVLA 因而先由 VLA 生成 rough trajectory，再复用 VLA backbone 产生结构化 critique，并据此完成一次连续轨迹修正，以单步 refinement 兼顾修正能力与实时性。  
	- **术语澄清**
		- 这里的 critic 指多模态的评价、验证与修正角色，不等同于经典强化学习 Actor–Critic 中必须单独学习的 `V(s)` 或 `Q(s,a)` 网络。论文在理论分析中用抽象的 `Q(V,L,A)` 表示动作质量，但实际 critic 并非输出标量价值分数，而是生成结构化风险判断和动作建议，再由 refinement 分支生成修正轨迹。
	- **核心创新点（一句话）**
		- CriticVLA 将同一 VLA/LLM backbone 从**动作生成器扩展为兼具轨迹评价与修正能力**的模型：先生成粗轨迹，再联合场景、指令和粗动作输出结构化风险判断与动作建议，并通过一次 refinement 得到修正后的连续轨迹。
	- **论文图解**
		- **Figure 1｜从 actor-only 到 critic-centric 两阶段 VLA**
			![[assets/trajectory-planning-vla-tts/criticvla-fig1-paradigms.png]]
			- **读图：** 旧范式把视觉和语言输入一次映射成动作；CriticVLA 的 Stage 1 先生成 rough action，Stage 2 复用同一 LLM backbone，并通过独立的 critic 参数读取原输入和粗动作，先作判断，再生成 refined action。
			- **问题—方法对应：** 它利用 VLA 原本被忽略的评价能力，但不是让模型无限反思，而是增加一次显式 `generate → critique → refine`。
		- **Figure 2｜Critique 如何参与连续轨迹修正**
			![[assets/trajectory-planning-vla-tts/criticvla-fig2-framework.png]]
			- **读图：** Stage 1 用 image、language 和 rough trajectory queries 产生粗轨迹；Stage 2 把粗轨迹重新编码，输出碰撞、速度、行人等结构化 critique，再结合 refinement trajectory queries 生成最终轨迹。
			- **关键点：** Critique 不是只展示给人看的解释文本，而是作为 refinement 的中间条件进入动作生成链。
		- **Figure 4｜实际修正类型**
			![[assets/trajectory-planning-vla-tts/criticvla-fig4-refinement-cases.png]]
			- **读图：** (a)(b) 根据道路入口修正 route waypoints，(c)(d) 根据碰撞风险修正 speed waypoints；红/绿轨迹相对粗轨迹发生方向或速度变化，说明它执行的是几何修改而非候选排序。
	- 规划操作：`Refinement`。先生成粗轨迹，再根据 critic 的判断对该轨迹进行一次定向修改。
	- 评价器：VLA critic。这里的“评价器”是由同一 VLA backbone 承担的功能角色：它联合视觉、语言和粗动作定位风险并提出修改建议，而不是单独输出 `V(s)` 或 `Q(s,a)` 的价值网络。
	- 反馈来源：针对当前粗轨迹的结构化多模态 critique，包括风险判断和动作建议；反馈不是候选间的投票结果，也不是单一标量价值分数。
	- 训练—部署：在 Stage-1 动作生成器基础上训练 Stage-2 的 critic/refinement 能力；部署时只增加一次多模态评价和一次轨迹修正，不运行传统 Actor–Critic 的时序差分更新或多轮在线优化。

**反馈来源 B｜世界模型介导的后果反馈（World-Model-Mediated Outcome Feedback）**

本子类先显式推演候选后果，再把预测未来转化为评价反馈并交给 refiner。系统在当前动作尚未真实执行时，利用世界模型或未来预测模块生成 action-conditioned future，由 outcome evaluator 提取任务进度、回报、风险或其他候选级判断：

`ŝ⁽ʳ⁾ₜ₊₁:ₜ₊ᴴ = Wφ(oₜ, a⁽ʳ⁾) -> f⁽ʳ⁾ = E(ŝ⁽ʳ⁾) -> a⁽ʳ⁺¹⁾ = Uθ(a⁽ʳ⁾, f⁽ʳ⁾)`

其中，`ŝ⁽ʳ⁾` 表示候选动作对应的预测未来，可以是完整视觉状态，也可以只是未来轨迹、任务进度或与控制相关的稀疏状态；`f⁽ʳ⁾` 表示 outcome evaluator 从预测未来中得到的内部反馈。本子类不要求生成高成本的完整像素世界。它与直接验证器反馈共享“评价反馈 $\rightarrow$ learned refiner”的更新机制，只是候选证据由 `V(o,a)` 的直接判断改为 `E(W(o,a))` 的后果判断。

归入本子类还要求 `a⁽ʳ⁺¹⁾` 是依据反馈产生的新候选，例如轨迹坐标、速度、方向或动作分量发生更新。若系统只计算 `j = argmaxₖ sₖ, a* = aⱼ`，且 `a*` 仍是原候选集合中的元素，则无论评分是否来自世界模型，都应归入 **World-Feedback Selection**，不能仅因存在 world rollout 就称为 refinement。

若系统不把后果评价交给 learned refiner，而是对穿过可微世界模型得到的目标直接执行梯度或局部优化，则更新子链归入 2.3.2 Objective-Guided Continuous Refinement，目标来源注明为 world-model-derived。这里的未来是推理阶段的内部想象；如果动作已经真实执行，并根据下一时刻获得的新观测重新规划，则属于 Environment-Feedback Replanning。

- **[Reflective Planning / ReflectVLM](https://arxiv.org/abs/2502.16707)** 2025-02-24｜CoRL 2025
	- **前序工作问题**
		- **Abstract / Introduction：** 互联网预训练 VLM 能理解物体和语言，却缺少精细物理交互知识；在多阶段装配中，短视决策的误差会沿长时域累积。
		- **Related Work：** 纯 VLM reasoning 没有动作后果证据，传统 TAMP 依赖显式符号和状态估计，MCTS 在高成本视觉动力学模型上还需大量分支 rollout。
	- **动机与方法**
		- VLM 先提出动作，diffusion dynamics model 逐步生成 $H$ 步 imagined future；最终 future image 与整段 imagined plan 一同送回同一个 VLM 的 reflection prompt，输出真正执行的修订动作。
	- **核心创新点（一句话）**
		- ReflectVLM 让 VLM 先在生成式视觉动力学中“看完后果”再反思当前动作，把互联网语义知识与任务内物理预测组合成测试时 look-ahead refinement。
	- 规划操作：`Evaluation-Guided / Learned Correction / World-Model-Mediated`。输出动作可以不同于初始 proposal，因此不是从多个固定候选中做 argmax。
	- 反馈来源：Action-conditioned imagined future images；proposal 与 reflection 由同一 VLM 通过不同 prompt 完成。
	- 训练—部署：需要交互式后训练 VLM 并训练 diffusion dynamics model；部署时冻结参数，执行 $H$ 步内部想象和一次 reflection。
	- 分类边界：论文文字称 iterative reflection，但其推理算法是“$H$ 步 proposal/world rollout + 一次最终反思”，不能写成任意多轮 `critique -> revise` 循环。
- **[SC-VLA](https://arxiv.org/abs/2602.21633)** 2026-02-25｜arXiv 预印本，暂无公开刊会录用信息
	- **前序工作问题**
		- **Abstract / Introduction：** 标准 VLA 主要拟合数据统计先验，对物理动态的理解有限；现有 RL-VLA 依赖外部奖励，奖励设计成本高且难以提供细粒度物理引导；完整 world-model rollout 又过于昂贵。
		- **Related Work：** Action-only policy 不预测动作造成的未来变化；WAM 虽然统一建模动作生成和未来演化，可从 future prediction 获得 intrinsic guidance，但现有方法多依赖隐式上下文约束，缺少把预测未来显式转化为动作修正信号的机制。
	- **动机与方法**
		- SC-VLA 结合 RL 的在线优化与 WAM 的内部未来预测：VLA 先预测 task progress 和 future trajectory trend，再将预测结果重塑为 progress-dependent endogenous dense reward，驱动 residual RL 在线调整轨迹方向。
	- **核心创新点（一句话）**
		- SC-VLA 以任务进度和未来状态变化构成 Sparse World Imagination，再将模型自身的未来预测转化为稠密内生奖励来训练 residual RL，使 VLA 能用内部 world imagination 直接指导在线动作修正。
	- **论文图解**
		- **Figure 1｜稀疏世界想象如何形成自我修正闭环**
			![[assets/trajectory-planning-vla-tts/sc-vla-fig1-overview.png]]
			- **整体定位：** 这是一张概念总览图，不是具体网络结构图。上半部分说明动作如何从“直接执行”变成“先预测短期未来，再用预测结果修正”；下半部分依次展示输入数据、Stage I 的输出、Stage II 的在线优化以及实验结果。
			- **Stage I｜Base Action + Sparse World Imagination：** 图像、语言指令和机器人状态进入 SC-VLA。模型同时输出 base action、当前任务进度 `Progress` 和短期状态变化 `ΔState`；后两者描述“任务做到哪一步”以及“按照当前意图，末端执行器接下来应向哪里变化”，不需要生成完整未来图像或视频。
			- **Imagination → Reward：** `ΔState` 给出一个短期目标方向。执行动作后，系统比较机械臂的实际位移方向与该预测方向是否一致，并把方向一致性转成稠密引导奖励 `r_guide`；`Progress` 用于动态调节其权重，任务前期强化预测方向的引导，接近精细接触阶段时逐渐减弱该先验。
			- **Stage II｜Online Action Refinement：** 训练时冻结 Stage I 的 base policy，Residual RL Module 只学习一个局部修正量 `a_res`，最终动作满足 `a = a_base + λa_res`。图中的蓝线表示原始路径，黄色线表示修正后的路径，红色标记表示 residual adjustment；修正目标是让动作沿模型想象的物理演化方向前进，同时避开原始路径的失败点。
			- **奖励边界：** Stage II 的最终奖励由内部方向引导、稀疏环境奖励和步数惩罚共同组成。SC-VLA 省去的是人工设计的外部稠密 reward model，不是完全脱离环境反馈。
			- **训练—执行边界：** `Direction as Reward` 用于 Stage II 与环境交互训练 residual policy。部署时固定模型参数，由 base policy 和训练好的 residual policy 前向生成最终动作，不在执行过程中继续更新网络。
			- **底部柱状图：** 这部分是结果摘要，不参与算法计算；它表示 SC-VLA 在实验中用更少的成功步数获得更高的平均成功率，对应图上方的 accuracy/throughput 提升。
			- **问题—方法对应：** 只预测与任务有关的进度和局部状态变化，降低世界预测成本；再把预测结果从辅助训练目标变成 Residual RL 可直接使用的方向性奖励，从而形成 `未来想象 → 内生奖励 → 动作残差修正` 的闭环。
		- **Figure 2｜SPI 与 OAR 的具体连接方式**
			![[assets/trajectory-planning-vla-tts/sc-vla-fig2-architecture.png]]
			- **读图：** Stage I 从 DiT 中间层解码 progress/ΔState，从末层输出 base action；Stage II 用 imagined progress 与 state 构造 dynamic weight 和 endogenous dense reward，Residual RL Module 输出 residual，与 base action 相加后执行。
			- **反馈性质：** 用于动作精化的 dense guidance 来自模型预测的 progress/ΔState。Residual RL 仍在环境中在线优化，但不依赖额外设计的外部 dense reward model；这一机制区别于 DriveVer 的独立轨迹 verifier，也区别于完整像素 rollout。
	- 规划操作：`Refinement`。根据预测的动作后果修改当前动作，而不是只给多个现成候选排序。
	- 反馈构造器：Sparse world imagination module 负责预测与任务有关的稀疏未来状态；后续 reward construction 将其转换为 residual policy 可用的方向性评价，二者共同构成 world-model-mediated verification path。
	- 反馈来源：模型预测的任务进度和未来轨迹趋势，属于 imagined feedback。该反馈来自模型内部想象，并非动作真实执行后的观测。
	- 训练—部署：Stage I 训练 base action 与 sparse world imagination，Stage II 冻结 base policy 并通过环境交互训练 residual policy；部署时固定两者参数，根据当前状态及预测的 progress/ΔState 前向生成 residual correction，不进行在线梯度更新。
	- 分类说明：模型预测的未来属于推理时内部反馈；动作真实执行后获得的新观测应归入环境闭环重规划。

###### 2.3.2 目标引导的连续精化：Objective-Guided Continuous Refinement

Objective-Guided Continuous Refinement 属于 2.3 的第二条更新子链，因为显式目标或约束同样在评价当前解，并且评价结果会回流到下一迭代点。它与 2.3.1 的区别不在于“有没有评价”，而在于评价怎样变成更新：本子类由数学局部优化算子直接求出更新量，不经过 feedback-conditioned learned refiner 的解释：

`d⁽ʳ⁾ = LocalSolve(τ⁽ʳ⁾; J, 𝒞),  τ⁽ʳ⁺¹⁾ = Π𝒞(τ⁽ʳ⁾ + d⁽ʳ⁾)`

其中，`𝒞` 表示可行域；若 `J` 是待最小化的可微代价，一个典型更新为 `d⁽ʳ⁾ = -ηᵣMᵣ⁻¹∇τJ(τ⁽ʳ⁾)`。归入本类的必要条件不是系统中存在 loss、reward 或 cost，而是 gradient、序列凸优化、投影或其他局部求解器在推理时直接计算 `τ⁽ʳ⁺¹⁾`。`J` 可以由解析几何、安全约束、learned verifier 或 differentiable world model 给出，目标来源只作为附加属性，不改变近端更新机制。对于 guided diffusion，更新还包含学习到的去噪先验与随机噪声，因此采用 `Generative Refinement + Objective Guidance` 的组合标注，也不要求 `J` 在每一步严格单调改善。若同时运行多条相互独立的精化链，其结构是 `Width × Depth / multi-start refinement`；只有当下一轮候选的生成或更新依赖多个当前候选之间的信息，例如 elite statistics、global best、pairwise differences、recombination 或 proposal-distribution parameters，才属于反馈耦合的群体搜索。

- **[SafeBimanual](https://arxiv.org/abs/2508.18268)** 2025-08-25｜CoRL 2025
	- **前序工作问题**
		- **Abstract / Introduction：** 预训练 bimanual diffusion policies 能生成协调动作，却没有显式防止双臂碰撞、物体碰撞、戳刺或撕扯等物理危险。
		- **Related Work：** 训练期安全数据增强难覆盖任务阶段变化，固定单一 cost 也无法适配搬运、插入、倾倒等不同双臂关系。
	- **动机与方法**
		- 先将主要危险交互写成可微 keypoint costs，VLM 根据当前任务阶段和双臂—物体关系调度 cost；在每个 diffusion step 估计 clean action chunk，并把所选 safety cost 的梯度注入去噪更新：

			$$A_{t}^{k-1}=\mu(A_{t}^{k},O_t,k)-\rho_k\nabla_A\mathcal C_{\mathrm{sched}}(A_{0\mid k},\mathcal P,s_t)+\sigma_k\epsilon.$$
	- **核心创新点（一句话）**
		- SafeBimanual 用 VLM 动态选择阶段相关的可微安全约束，并在冻结 diffusion policy 的去噪过程中直接做 action-space gradient guidance。
	- 规划操作：`Generative Refinement + Objective Guidance (2.3.2)`。梯度连续改变当前去噪轨迹，能够生成原始 sample 之外的新解；不是 scorer 排序，也不是 CEM 的分布矩更新。
	- 反馈来源：基于前向运动学和双臂关键点关系的 differentiable safety costs；VLM 只负责选择 cost，不直接评价每条轨迹的长期回报。
	- 训练—部署：复用预训练 diffusion policy，部署时进行 guided denoising；因此它正是“直接在 diffusion 生成链上更新轨迹”的代表，与 TOAD 在外部初始化 CEM 分布后更新均值/方差不同。
- **经典梯度与局部轨迹优化：** [CHOMP](https://publications.ri.cmu.edu/chomp-gradient-optimization-techniques-for-efficient-motion-planning) 使用协变梯度连续改善路径；[TrajOpt](https://rll.berkeley.edu/~sachin/papers/Schulman-IJRR2014.pdf) 通过序列凸优化处理碰撞与轨迹约束；[Universal Planning Networks](https://arxiv.org/abs/1804.00645) 则在学习到的潜在动力学中对动作序列执行梯度下降。三者更新的核心对象都是当前轨迹，而不是由一组候选拟合出的下一轮采样分布。
- **学习式规划中的测试时后处理：** [UniAD](https://openaccess.thecvf.com/content/CVPR2023/html/Hu_Planning-Oriented_Autonomous_Driving_CVPR_2023_paper.html) 根据预测 occupancy 对输出轨迹进行碰撞规避后优化；[LAGO Policy](https://arxiv.org/abs/2606.17982) 对 diffusion policy 或 goal-directed planner 产生的轨迹执行时空优化，以降低 jerk 并满足可行性约束。这些方法即使内部运行多步求解，也仍属于单解顺序精化。
- **与 diffusion guidance 的关系：** [Diffuser](https://proceedings.mlr.press/v162/janner22a.html) 和 [Motion Planning Diffusion](https://arxiv.org/abs/2412.19948) 在去噪过程中注入 reward/cost gradient，可标注为 **Generative Refinement + Objective Guidance**。若批量生成多条彼此独立的引导去噪链，可进一步标注为 `Width of Depth Chains`；在本文拓扑中，仅存在多个 particles 而没有跨链反馈耦合时，不归入第 3 类的 Adaptive Population Scaling。
- **多起点梯度优化：** 若从 $N$ 个初值分别运行梯度下降且各链之间不交换信息，其结构是 $N$ 条独立 depth chains。只有下一轮候选的生成或更新使用了多个当前候选之间的信息，例如精英统计、全局最优、候选差分、重组关系或分布参数更新时，才进入第 3 类。

#### 3. 反馈耦合的群体搜索：Adaptive Population Scaling

**符号：** $N$ 是候选数，$R$ 是单次决策内的迭代轮数，$C$ 表示候选之间是否互相反馈。**本类：** $N>1, R>1, C=1$，即多候选经过多轮搜索，且下一轮候选的生成或更新依赖多个当前候选之间的信息；若 $C=0$，则只是多条独立精化链。

系统在每轮维护候选集合 $X^{(r)}=\{\tau_i^{(r)}\}_{i=1}^{N}$，并将本轮的跨候选信息反馈给下一轮候选生成或更新机制：

`X⁽ʳ⁾ -> s⁽ʳ⁾ = Evaluate(X⁽ʳ⁾) -> H⁽ʳ⁾ = Couple(X⁽ʳ⁾, s⁽ʳ⁾) -> X⁽ʳ⁺¹⁾ = Update(X⁽ʳ⁾, H⁽ʳ⁾)`

其独特性来自 **population feedback coupling**：额外计算既有每轮候选宽度，也有跨轮迭代深度，而且第 $r$ 轮多个候选的信息会共同影响第 $r+1$ 轮怎样生成或更新。这里的 $H^{(r)}$ 是跨候选耦合状态，可以是 elite statistics、reward-weighted moments、global/personal best、pairwise differences、recombination relationships，也可以是显式 proposal distribution 的参数或由 particles 隐式表示的搜索状态。显式拟合 proposal distribution 是一种实现方式，但不是群体搜索的必要条件。$N$ 在此表示共同参与搜索更新的候选，而不是为单个当前解估计梯度或局部统计量的瞬时 probes；后一种情形仍是单解精化。若 $R=1$，流程退化为 Width Selection；若 $N=1$ 且只更新单条轨迹，则退化为 Sequential Refinement；若 $N>1,R>1$ 但候选之间始终没有信息交换，则是 `Width × Depth, C=0`，而不是本类。

##### 3.1 参数化分布更新：Parametric Distribution Update

本类显式维护 proposal distribution 的参数，并利用 elite statistics 或 reward-weighted moments 更新其均值、方差或协方差。

###### 3.1.1 Elite-based Distribution Fitting

- **[TOAD](https://arxiv.org/abs/2606.07170)** 2026-06-05｜arXiv 预印本，暂无公开刊会录用信息
	- **前序工作问题**
		- **Abstract / Introduction：** 学到的 trajectory scorer 通常只为固定 proposals 排序；如果候选集没有覆盖好轨迹，评分器再准确也无法突破 proposal 的质量上限。
		- **Related Work：** One-shot planner 的搜索宽度固定，Best-of-N 不会根据评分更新下一轮候选；传统在线优化又缺少可学习的场景目标或可靠初值。
	- **动机与方法**
		- 应把 scorer 从一次性排序器变成测试时优化目标；因此用原 planner proposals warm-start CEM，反复采样、评分、保留精英并更新候选分布，搜索原候选集之外的轨迹。
	- **核心创新点（一句话）**
		- TOAD 将原本只负责固定候选排序的 learned scorer 重新解释为 CEM 的测试时 reward，并以 base planner proposals 作为受信任的搜索初值，使额外计算能够生成原候选集之外的更优轨迹。
	- **论文图解**
		- **Figure 1｜把固定 scorer 变成 CEM 的测试时 reward**
			![[assets/trajectory-planning-vla-tts/toad-fig1-overview.png]]
			- **读图：** Base planner 提供 proposals 和初始 anchor；inverse bicycle model 将轨迹转为控制量，在其附近初始化 CEM Gaussian；每轮采样控制、用 bicycle model rollout、经 `Scorer + regularization` 评分，保留 elites 后更新 Gaussian。
			- **关键变化：** Scorer 不再只给原有候选排名，而是通过多轮分布更新指导产生新轨迹；regularization/信赖域负责避免搜索离开 scorer 熟悉的区域。
		- **Figure 5｜CEM 迭代过程中轨迹分布怎样移动**
			![[assets/trajectory-planning-vla-tts/toad-fig5-cem-iterations.png]]
			- **读图：** 从 iteration 0 到 100，候选和 elite 均值逐步向更合理的车道位置与前向进度移动；这正是 `Score → Update distribution → Resample`，不是一次 Best-of-N。
	- 规划操作：`Optimization`。系统会根据评分重复生成和更新候选，而不是评分一次后立即结束。
	- 搜索方法：CEM，以原 planner proposals 作为 warm start。CEM 每轮保留高分候选，并根据这些候选重新估计下一轮采样分布。
	- 评价器：Learned trajectory scorer，被作为测试时优化的 reward/objective。它把候选轨迹映射为分数，为 CEM 提供优化方向。
	- 更新对象：候选轨迹分布，而不是单条轨迹。本轮高分候选会改变下一轮更可能采样到哪些轨迹。
	- 训练—部署：训练 trajectory scorer；部署时反复采样、评价并更新候选分布。因此主要额外开销发生在测试时的多轮搜索。
	- 综合标注：**Feedback-Coupled Population Search / Elite-based Gaussian Update**。Width 表示每轮存在多个候选，Depth 表示搜索跨多轮推进，二者通过 elite statistics 更新下一轮 Gaussian。
- **[World–Value–Action Model / WAV](https://arxiv.org/abs/2604.14732)** 2026-04-16｜arXiv 预印本，暂无公开刊会录用信息
	- **前序工作问题**
		- **Abstract / Introduction：** 现有 VLA 多直接预测短时动作，缺少对长时未来的显式推演与价值判断；直接在高维 action space 采样长轨迹时，可行轨迹所占比例会随规划时域增加而迅速下降。
		- **Related Work：** 世界模型能够预测未来但未必知道哪些未来更有价值；一次性 latent sampling 虽提高了采到可行轨迹的概率，仍不能在有限样本预算下保证找到高价值轨迹。
	- **动机与方法**
		- 将未来生成、轨迹价值和动作解码统一在 latent planning 中。部署时分别维护 video-flow latent 与 value-flow latent 的 Gaussian；每轮采样未来视频 latent，对每个未来再采样多组 value latent，以 value prediction 的 SNR 评价候选，选择 top-$K$ elites 后重新估计两个 Gaussian 的均值与方差，最后从优化后的 latent distributions 解码动作。
	- **核心创新点（一句话）**
		- WAV 将 VLA 的世界预测与长期价值估计组织成双层 latent population search，使预测未来的价值评价能够跨轮重塑 video/value latent 的采样分布，再由收敛后的高价值未来生成可执行动作。
	- **论文图解**
		- **Figure 1｜WAV 的 World–Value–Action 统一架构**
			![[assets/trajectory-planning-vla-tts/wav-fig1-overview.png]]
			- **读图：** 左侧语言指令和多视角视觉状态进入 Video Generation Module，产生 future-video latent；Trajectory Value Module 对预测未来做长期价值估计，并通过 `Trajectory Evaluation → Distribution Update` 反复更新 video/value 两组 noise distributions；优化后的 latent 最后进入 Action Prediction Module 解码动作。
			- **关键结构：** 世界模型负责提出可行未来，value module 不只从固定候选里选一个，而是用评价结果改变下一轮 latent sampling distribution，因此这里优化的是测试时 latent 分布参数，不是网络权重。
		- **Figure 4｜测试时预算 $K$、$M$、$N$ 如何影响成功率**
			![[assets/trajectory-planning-vla-tts/wav-fig4-scaling.png]]
			- **读图：** 三幅图分别固定迭代轮数 $K=1,5,10$；横轴 $M$ 是每轮 video latent 候选数，纵轴 $N$ 是每个未来对应的 value latent 采样数，曲面高度与颜色表示成功率。
			- **结论边界：** 增加 $K$、$M$、$N$ 会先提高成功率，随后进入收益饱和区。WAV 的测试时扩展来自“更多未来候选 × 更稳健的价值估计 × 更多分布更新轮次”，并非直接微调 diffusion/flow 网络参数。
	- 规划操作：`Latent-space Inference / Distribution Refinement`。评价结果不只选择某个现成未来，而会更新后续 future/value latent 的生成分布；它不是在 action space 中直接执行显式几何轨迹优化。
	- 评价器：Trajectory value module。它针对预测的 future-video features 生成多组价值估计，并以均值与不确定性构成的 SNR 评价未来质量。
	- 反馈来源：Language-conditioned predicted futures + learned trajectory value，属于 world prediction 与 value evaluation 的组合反馈。
	- 更新对象：两组参数化 latent distributions，即 $f_{vid}^{(r)}$ 与 $f_{val}^{(r)}$ 的均值和方差。
	- 训练—部署：训练阶段以 flow matching 分别学习 future video、trajectory value 与 action generation；部署时固定网络参数，只对当前任务实例的 latent noise distributions 进行多轮 elite update。
	- 综合标注：**World-Feedback + Value-Guided Adaptive Population Scaling**。
- **机制谱系：CEM / CMA-ES / iCEM / Model-based MPC**
	- CEM 与 CMA-ES 根据 elite samples 更新参数化分布；[iCEM](https://proceedings.mlr.press/v155/pinneri21a.html) 通过跨轮记忆、时间相关动作噪声和 warm start 降低实时规划的样本需求。
	- [PETS](https://proceedings.neurips.cc/paper_files/paper/2018/hash/3de568f8597b94bda53149c7d7f5958c-Abstract.html) 与 [PlaNet](https://proceedings.mlr.press/v97/hafner19a.html) 展示了在学习动力学或潜在世界模型上用 CEM 进行在线 action-sequence search 的经典范式；它们是 TOAD、WAV 之前的重要机制谱系，但不应因此被称为 VLA 方法。

###### 3.1.2 Reward-weighted Moment Update

- **Iterative MPPI / STOMP / PISTO / TD-MPC**
	- 这类方法不只保留 top-$K$ elites，而是按照轨迹代价或回报给样本加权，再用加权矩更新 nominal trajectory 或 proposal mean。[STOMP](https://whiteoak.umd.edu/roswiki/attachments/Papers%282f%29ICRA2011_Kalakrishnan/kalakrishnan_icra2011.pdf) 使用随机扰动处理不可微代价；[PISTO](https://arxiv.org/abs/2605.07215) 进一步在相邻 Gaussian proposals 之间加入 KL proximal regularization；[TD-MPC](https://proceedings.mlr.press/v162/hansen22a.html) 则在学习到的潜在动力学和终端价值上执行局部轨迹优化。
	- **MPPI 边界：** 不能把所有 MPPI 实现都自动归为本类。经典 MPPI 在一个真实控制周期内可能只完成一次采样加权更新，此时内层更接近一次 Width aggregation，外层依靠下一时刻真实观测形成 closed-loop replanning；只有在同一决策时刻反复 `sample -> weight -> update -> resample` 的 multi-step MPPI 才构成 Adaptive Population Scaling。
	- **机制交叉：** MPPI 的样本加权更新也可被解释为 sampling-distribution 参数上的预条件梯度步；因此 `gradient-based` 与 `population-based` 不是互斥的一级拓扑标签。分类时应观察实现维护的是单条轨迹，还是由多个样本共同更新的 proposal state。

##### 3.2 非参数群体演化：Non-parametric Population Evolution

本类不要求把下一轮 proposal 压缩成单个 Gaussian，而是直接以 elite particles、重采样权重和生成式 mutation 表示新的搜索群体。

- **[Diffusion-ES](https://arxiv.org/abs/2402.06559)** 2024-02-09｜CVPR 2024
	- **前序工作问题**
		- **Abstract / Introduction：** Reward-gradient-guided diffusion 要求 reward 对带噪轨迹可微，并常需针对 noisy inputs 重新训练；纯 CEM、MPPI 等采样优化若每次 mutation 都完整运行 diffusion，又会产生过高的搜索成本。
		- **Related Work：** 普通 diffusion planner 的多条样本彼此独立，增加样本数只扩大覆盖范围；高分样本不会反过来影响其他样本的下一轮生成，因此不能主动把预算集中到高回报区域。
	- **动机与方法**
		- 用 diffusion model 表达可行轨迹流形，用 evolutionary search 优化任意 black-box reward。初始 population 由完整 diffusion sampling 产生；每轮按 reward 对候选重采样，再对高分 trajectories 执行截断的加噪—去噪 mutation，并随搜索轮数逐渐减小 mutation strength。
	- **核心创新点（一句话）**
		- Diffusion-ES 以截断扩散作为数据流形上的生成式 mutation，将不可微 reward 的群体进化搜索与 diffusion trajectory prior 结合，使高分轨迹能够跨轮繁殖并探索邻近的新行为。
	- 规划操作：`Evolutionary Optimization`。最终轨迹可以由高分样本变异产生，不受初始候选集合限制。
	- 评价器：可为安全、进度、舒适度等组成的 black-box driving reward，也可由 LLM 把自然语言指令转写成不可微 reward program。
	- 更新对象：由 elite resampling 与 truncated diffusion mutation 隐式表示的非参数 population，而不是单个显式 Gaussian。
	- 训练—部署：训练 trajectory diffusion prior；部署时固定 diffusion 参数，通过多轮评分、重采样和变异完成轨迹优化。
	- 综合标注：**Non-parametric Population Search + Generative Mutation**。

##### 3.3 反馈条件的候选再生成：Feedback-Conditioned Candidate Regeneration

- **[E-TTS](https://arxiv.org/abs/2606.27268)** 2026-06-25｜arXiv 预印本，暂无公开刊会录用信息
	- **前序工作问题**
		- **Abstract / Introduction：** 早期 embodied TTS 多只扩动作候选，忽略 reasoning 如何扩展；逐时刻只看当前观测还会丢掉长任务中的历史失败与阶段信息。
		- **Related Work：** 固定 Best-of-$N$ 的评分不会改变下一轮生成，单纯 self-reflection 又缺少专门评价 reasoning/action 的 grounded verifier。
	- **动机与方法**
		- 每轮联合采样 reasoning–action pairs，用 history-aware reasoning verifier 与 action verifier 分别评分；再把评价转成反馈写回下一轮采样，并由 history buffer 保存此前观测、推理、动作与反馈，从而迭代产生新候选。
	- **核心创新点（一句话）**
		- E-TTS 把 reasoning width、action width 与 feedback-driven depth 组合成模块化闭环，使 verifier 结果不只决定本轮胜者，还条件化下一轮候选生成。
	- 规划操作：`Joint Sampling + Verification + Feedback-Conditioned Regeneration`。其 $N>1,R>1,C=1$，因此比固定 Best-of-$N$ 更接近反馈耦合的候选群体搜索。
	- 更新对象：不是 CEM 的显式均值/方差，也不是 Diffusion-ES particles，而是由语言/视觉反馈与历史上下文隐式改变的下一轮 reasoning–action proposal distribution。
	- 训练—部署：框架面向冻结 base VLA 和可组合 verifiers，论文报告无需重新训练或新增专家数据；当前仍是预印本，venue 状态不能写成已录用。

与其他拓扑的边界：

- **与 Width Selection：** Best-of-N、RoboMonkey、TACO、MG-Select 只在固定候选集上选择或聚合；RoVer 虽沿预测方向扩展候选，但仍是一次扩展后统一评分，不存在跨轮 proposal update。
- **与 Sequential Refinement：** 单条轨迹的 critique-revise、残差修正、梯度下降或 diffusion denoising 都只维护一个当前解；即使内部执行多步，也应归入 Depth。
- **与独立多链：** $N$ 条互不通信的去噪链或多起点梯度链只是 `Parallel Width × Sequential Depth`。第 3 类要求下一轮候选的生成或更新依赖多个当前候选之间的信息，而不限定必须聚合评分或拟合显式分布。
- **与 Branching Search：** 群体优化通常把候选压缩为分布参数、加权矩或 empirical particles，不保留严格的父子前缀和价值回传路径；MCTS、beam search、MCTD 则显式维护 frontier、ancestry、expansion 与 pruning/backpropagation。
- **与闭环重规划：** CEM、MPPI 等可以作为 MPC 每个环境时间步的内层求解器。内层是否属于 Adaptive Population Scaling 取决于同一决策时刻是否存在多轮跨候选耦合更新；外层根据真实新观测再次规划则另标为 Environment-Feedback Replanning。
- **与参数适应：** TT-VLA、EVOLVE-VLA 等更新的是模型权重或 policy parameters，属于 test-time training/adaptation；本类默认模型参数冻结，只更新当前任务实例的轨迹、latent 或 proposal state。

#### 4. 分支式条件规划：Branching / Contingent-Policy Scaling

沿物理时间或生成状态建立多个分支，并根据不同的预测环境响应形成条件后续策略：

`aₜ -> ŝₜ₊₁ -> {aₜ₊₁⁽¹⁾, aₜ₊₁⁽²⁾, ...}`

##### 4.1 Tree-Structured Policy Planning

- **[TPP / DTPP](https://arxiv.org/abs/2310.05885)** 2023-10-09｜ICRA 2024
	- **前序工作问题**
		- **Abstract / Introduction：** 运动预测和规划代价都影响安全决策，但传统系统往往将二者分别训练；单阶段轨迹规划也难表达“自车动作改变环境响应，再影响后续动作”的条件关系。
		- **Related Work：** Prediction-first pipeline 不以 ego action 为条件，手工 cost 泛化有限；已有学习式 planner 通常只输出一段固定轨迹，不维护未来条件分支。
	- **动机与方法**
		- 规划应沿物理时间展开，并在每个 ego 分支下重新预测环境响应；因此构造 trajectory tree，以 query-centric ego-conditioned prediction 更新分支，再用 context-aware learned cost 评价和剪枝，并联合训练预测与规划。
	- **核心创新点（一句话）**
		- DTPP 在可微轨迹树中联合学习 ego-conditioned 多智能体预测与 context-aware cost，使每个自车分支都产生对应的环境响应并据此剪枝，从根本上连接了交互预测和多阶段决策。
	- **论文图解**
		- **Figure 1｜Trajectory tree 与 scenario tree 的条件闭环**
			![[assets/trajectory-planning-vla-tts/dtpp-fig1-overview.png]]
			- **读图：** Tree-structured planner 先展开 ego trajectory tree；conditional prediction 针对不同 ego 分支生成不同的环境 scenario tree；cost evaluation 联合两棵树选出最优计划，并通过可微训练让预测和代价相互对齐。
			- **问题—方法对应：** 普通 prediction-first 方法只提供一份与 ego 无关的未来；DTPP 明确建模“我选择不同动作，其他参与者的响应也不同”。
		- **Figure 2｜剪枝、条件预测和可学习代价如何联合**
			![[assets/trajectory-planning-vla-tts/dtpp-fig2-framework.png]]
			- **读图：** 上部沿时间反复 `Prune & Expand`，并把树编码为 branch×time tensor；中部 Transformer 用 ego tree query 场景上下文，输出 scenario tree；下部 feature extractor 与 weight decoder 形成 context-aware cost，Max-Ent IRL 和 prediction loss 共同反传。
			- **核心结构：** 宽度来自每阶段分支，深度来自物理时间展开，评价反馈来自 ego-conditioned prediction 与 learned cost；因此它不是普通 MCTS，也不是简单多候选选择。
	- 规划操作：`Branching + Pruning`。规划器在每个时间阶段展开多个自车动作分支，并删除代价较高或价值较低的分支。
	- 评价器：Context-aware cost evaluator。它结合场景上下文、预测的环境响应和候选轨迹计算各分支的规划代价。
	- 反馈来源：Ego-conditioned environment prediction。不同自车动作会触发不同的周围交通参与者预测，从而影响后续分支评价。
	- 训练—部署：联合训练条件运动预测与代价评价；部署时执行树状条件规划。预测模块和代价模块共同决定搜索树如何展开和剪枝。
	- 综合标注：**Physical-time Depth + Branching Width + Conditional Feedback**。Depth 对应未来时间推进，Width 对应每个节点的多个动作分支，Feedback 对应动作条件下的环境响应。
	- 分类说明：DTPP 是 tree-structured policy planning，不应直接等同于 MCTS。标准 MCTS 还包含 selection、expansion、simulation 和 value backup 等过程。
	- 结构说明：普通树只共享相同前缀，不会自动合并已经分开的相似状态；“动态合并”需要额外的 DAG、state aggregation 或 transposition 机制。

##### 4.2 Tree Search over Generative States

- **[MCTD](https://arxiv.org/abs/2502.07202)** 2025-02-11｜ICML 2025 Spotlight
	- **前序工作问题**
		- **Abstract / Introduction：** 标准 diffusion planner 在测试时通常只增加去噪步数或完整样本数，无法主动把计算预算集中到更有希望的中间方案。
		- **Related Work：** 单链 diffusion 没有分支探索，Best-of-N 只能在完整样本生成后选择；传统树搜索又缺少适合连续高维生成状态的节点表示。
	- **动机与方法**
		- 部分去噪状态天然可以作为能够继续展开的搜索节点；因此把 denoising process 组织成树，在中间状态上分支、评价、剪枝和继续精化，动态平衡探索与利用。
	- **核心创新点（一句话）**
		- MCTD 将部分去噪轨迹定义为 MCTS 节点、将 guidance level 定义为可搜索的 meta-action，使 diffusion planner 能在生成中间态上分支和回传价值，并把测试时算力自适应集中到高潜力计划。
	- **论文图解**
		- **Figure 1｜同一算法的 MCTS 视角与 diffusion 视角**
			![[assets/trajectory-planning-vla-tts/mctd-fig1-tree-diffusion.png]]
			- **读图：** 左侧把部分去噪轨迹作为节点，执行 Selection、Expansion、快速 Simulation 和 value Backpropagation；右侧把相同过程画在 denoising depth×planning horizon 网格中，说明分支发生在生成中间态而不是完整轨迹之后。
			- **关键机制：** Guidance level 被当作 meta-action，树搜索决定某个中间态采用何种引导继续去噪，从而把额外算力集中给更有前景的分支。
		- **Figure 3｜树搜索如何从失败分支转向高回报分支**
			![[assets/trajectory-planning-vla-tts/mctd-fig3-search-process.png]]
			- **读图：** 根节点的不同 guidance 分支通过快速 rollout 得到 reward；低价值节点停止扩展，高价值路径继续分支，最终选择红框中的较高回报计划。这比增加一条固定去噪链多了显式探索—利用分配。
	- 一级归属：`Branching Search`。它在推理时保留多个可继续扩展的生成分支，而不是只维护一条去噪链。
	- 组合机制：`Denoising Depth + Tree Search`。去噪步骤提供纵向生成深度，树搜索在部分去噪状态之间提供横向分支选择。
	- 分类依据：推理时显式维护、选择并扩展多个去噪分支。只要不同中间状态能够被独立评价和继续展开，就形成了搜索树。
	- 边界说明：如果具体实现只沿单条生成链增加去噪步骤，而不维护多个搜索节点，则应改归入 Generative Refinement。判断重点是是否存在并行分支，而不是是否使用 diffusion。
- **[VLA-Reasoner](https://arxiv.org/abs/2509.22643)** 2025-09-26｜arXiv 预印本，暂无公开刊会录用信息
	- **前序工作问题**
		- **Abstract / Introduction：** Off-the-shelf VLA 擅长短时动作预测，却无法显式检查当前动作对长时任务的影响，局部偏差会持续累积。
		- **Related Work：** 枚举完整动作序列开销大，普通 VLA sampling 重复查询相近动作，稀疏终局奖励又难以给 MCTS 的中间节点稳定反馈。
	- **动机与方法**
		- VLA proposal 为根节点提供动作先验，KDE confidence sampling 减少冗余 VLA query；world model rollout 生成未来状态，offline reward shaping 评价中间节点，再由在线 MCTS selection、expansion 与 backup 选择当前动作。
	- **核心创新点（一句话）**
		- VLA-Reasoner 将冻结 VLA 的局部动作先验、学习式未来预测与中间状态 reward 组织进在线 MCTS，使测试时算力能沿高价值物理未来自适应展开。
	- 规划操作：`Branching Search`。它保留父子状态、执行探索—利用选择和价值回传，不能简化为“world model 给多个候选打分”。
	- 反馈来源：Predicted future states + offline-shaped intermediate reward；KDE 主要控制探索分布而不是最终价值。
	- 训练—部署：需要预训练 world model 与 reward-shaping components；部署时冻结 base VLA 并在线展开 MCTS。

##### 4.3 Graph Search over Dataset States

- **[Test-Time Graph Search (TTGS)](https://arxiv.org/abs/2510.07257)** 2025-10-08｜ICML 2026
	- **前序工作问题**
		- **Abstract / Introduction：** Offline goal-conditioned RL 的 value 在局部状态间往往可靠，但跨长距离直接执行会因 value error 和动作误差累积而失败。
		- **Related Work：** 专门训练 hierarchical controller 或 landmark model 增加监督；对远距离边设置硬阈值又容易让状态图断裂。
	- **动机与方法**
		- 从离线数据抽取状态作为图节点，以冻结 GCRL value 映射出的局部距离作为边权；对超过 trust region 的长边施加 superlinear soft penalty，再用 Dijkstra 计算 guide path，执行中选择当前可达的最远 subgoal。
	- **核心创新点（一句话）**
		- TTGS 证明标准 goal-conditioned value 已包含足以支持局部几何规划的结构，可在不改训练的情况下通过数据状态图和最短路把远目标拆成可信短跳。
	- 规划操作：`Graph Search + Adaptive Subgoal Selection`。图可复用，guide path 每个 episode 计算一次，真实执行时依据当前状态沿路径推进。
	- 反馈来源：Value-derived state distance 或领域距离；soft long-edge penalty 用于抑制 value 产生的“wormhole”捷径。
	- 训练—部署：base policy 与 value 均冻结，不新增监督或参数；范围是 offline GCRL 而非 VLA，但它补齐了 2026 年顶会中纯测试时图搜索这一规划拓扑。

#### 5. 环境闭环重规划：Online Closed-Loop Replanning

动作真实执行后，利用下一时刻的新观测重新规划：

`oₜ -> aₜ -> 真实执行 -> oₜ₊₁ -> aₜ₊₁`

##### 5.1 Environment-Feedback Replanning

- **[Inner Monologue](https://arxiv.org/abs/2207.05608)** 2022-07-12｜CoRL 2022
	- **前序工作问题**
		- **Abstract / Introduction：** 具身 LLM 不仅要知道做什么，还必须根据执行是否成功和场景变化决定何时修改计划；开环语言计划无法获知真实动作后果。
		- **Related Work：** 静态 prompt 只利用初始观测，传统 closed-loop policy 缺少高层语言推理；纯语言 self-reflection 没有环境证据，可能反复合理化同一个错误。
	- **动机与方法**
		- 把真实反馈持续写回语言上下文，便可在不重新训练 LLM 的情况下闭环重规划；因此将成功检测、场景描述和人类反馈反复注入 prompt，执行一步后根据新观测更新后续计划。
	- **核心创新点（一句话）**
		- Inner Monologue 把成功检测、被动/主动场景描述和人类反馈统一转成可持续写回 prompt 的 grounded context，使冻结 LLM 能依据真实执行结果跨环境时间闭环重规划。
	- **论文图解**
		- **Figure 1｜语言计划如何接入真实环境反馈**
			![[assets/trajectory-planning-vla-tts/inner-monologue-fig1-closed-loop.png]]
			- **读图：** LLM 先把任务拆成可执行技能；机器人执行后，scene descriptor 和 success detector 把场景与成败转成文本重新送回 planner，失败时修改动作，成功后继续下一步。
			- **时间尺度：** 反馈来自真实动作执行后的新观测，所以这是 environment-time replanning，而不是一次决策内部的 imagined refinement。
		- **Figure 2｜三类可写入 prompt 的反馈**
			![[assets/trajectory-planning-vla-tts/inner-monologue-fig2-feedback-types.png]]
			- **读图：** Success Detection 回答任务是否完成；Passive Scene Description 每步自动提供结构化场景；Active Scene Description 只在 LLM 主动查询时返回开放式信息。三者提供不同粒度和触发方式的 grounded feedback。
			- **方法本质：** Inner Monologue 本身不训练新的大语言模型，而是设计反馈接口，让已有 LLM 在环境闭环中持续更新上下文和计划。
	- 规划操作：`Replanning`。系统执行一个动作后重新制定后续计划，而不是在同一控制时刻内部反复修改候选。
	- 评价或反馈来源：真实成功检测、场景描述或人类反馈。这些信息反映动作执行后的实际结果。
	- 反馈性质：Environment-time feedback。反馈跨越真实环境时间产生，与世界模型预测的 imagined feedback 不同。
	- 训练—部署：动作执行后，根据真实新观测更新语言推理和后续计划。重新规划发生在机器人与环境的交互循环中。
	- 分类说明：该方法不属于单次决策内部的 Depth Scaling；DriveVer、CriticVLA 等 inference-time internal refinement 与它处于不同反馈时间尺度。

##### 5.2 Adaptive Execution Scheduling and Replanning Horizon

- **[SAIL](https://arxiv.org/abs/2506.11948)** 2025-06-13｜CoRL 2025 Oral
	- **前序工作问题**
		- **Abstract / Introduction：** Imitation policy 会继承遥操作示范的慢速；直接提高执行频率会改变控制动力学、放大 tracking error，并让下一轮观测落到训练分布外。
		- **Related Work：** 固定 action horizon 与简单 temporal smoothing 没有考虑控制器误差、精细操作阶段和真实系统延迟的共同约束。
	- **动机与方法**
		- 用 reached pose 代替 commanded pose 作为控制器无关监督目标；部署时根据 tracking error 开关跨 chunk diffusion guidance，根据动作复杂度和 gripper event 自适应调节速度，并在异步推理中丢弃过期动作、保证 action queue 不耗尽。
	- **核心创新点（一句话）**
		- SAIL 将测试时生成一致性、控制器可达性、阶段化速度与系统延迟联合处理，使 imitation policy 在保持成功率的前提下快于示范执行。
	- 规划操作：`Adaptive Guidance + Speed Scheduling + Closed-Loop Execution`。tracking error 来自真实执行，因而跨 environment time 调节下一轮生成与执行。
	- 分类边界：SAIL 的重点是吞吐量和动态可达性，不是通过不断增加 $N$ 或 $R$ 获得单调精度收益；它属于相关的 inference-time execution scaling，而非标准 Best-of-$N$ compute scaling。
- **[Adaptive Action Chunking (AAC)](https://arxiv.org/abs/2604.04161)** 2026-04-06｜CVPR 2026
	- **前序工作问题**
		- **Abstract / Introduction：** 固定长 chunk 稳定且省推理，却对新观测反应慢；固定短 chunk 反应快，却更容易 mode jump、抖动并增加推理频率，不同任务和阶段不存在统一最优长度。
		- **Related Work：** BID 选择完整 action chunk，SGAC 决定是否替换动作队列，但它们仍使用固定 chunk length。
	- **动机与方法**
		- 每个观测并行采样 $N$ 个长度为 $H$ 的 action chunks，以样本协方差和 gripper 概率估计各未来位置的连续/离散 action entropy；选择平均熵曲线最大差分点作为 $h^*$，执行前 $h^*$ 步后再观察并规划。
	- **核心创新点（一句话）**
		- AAC 不选“哪条轨迹”，而是用候选分布的不确定性决定“这条计划执行多远再重规划”，把测试时宽度转化为动态闭环频率。
	- **论文图解**
		- **Figure 2｜固定 chunk 与 AAC 自适应执行的对比**
			![[assets/trajectory-planning-vla-tts/aac-fig2-overview.png]]
			- **读图：** 上半部分的冻结 VLM 按固定 $H=16$ 执行完整 action chunk，长时间不重新观察导致任务失败；下半部分的冻结 VLM 与 diffusion head 并行采样 $N$ 个长度为 $H$ 的 chunks，AAC 根据逐未来位置的 action entropy 截取最优前缀，得到随状态变化的 $h^*=4,3,14,11$，执行后再观察并重规划。
			- **分类含义：** 多候选只用于估计分布不确定性，不做 Best-of-$N$ 轨迹选择；AAC 输出的是本轮承诺执行的 horizon，因此应归入自适应闭环重规划，而不是 verifier-based selection。
	- 规划操作：`Uncertainty-Gated Replanning Horizon`。高熵时缩短 chunk、提高反应性，低熵时延长 chunk、保持一致性和效率。
	- 反馈来源：多候选 action distribution 的 entropy；没有外部 verifier，也不利用世界模型预测未来。
	- 训练—部署：不改架构、不额外训练；部署时批量采样用于熵估计。论文也报告 sample 数增加后的收益趋于饱和，因此它是自适应计算分配，不应被写成无限 width scaling。


A Path-Space Formulation of Prediction in World Models: From a Single Action to Prediction, Planning, and Irreversibility
DUST：diffusion-denoising 与 wm 双向 ICML2026
EPONA
BrainWAM


diffusion
- tts
- training时
	- DDPO etc   强化学习做diffusion的score场塑造



解决问题：单未来

统一速度场建模的**多不确定性**世界模型
利用世界模型，对diffusion刻画多不确定性场有帮助
- 世界模型可以rollout
- 

- 需要与diffusion各个拉开区别
	- 单路径优化
	- 多粒子优化
	- 筛选演化
	- 混合演化 （当然可以基于此基础）


**DTPP**
- DTPP基于TPP进行采样树展开，受限于采样target states
	- 离散问题
	- Branch数量和树深度存在指数级矛盾 存在规划精度和复杂度矛盾
- 
**Scaling World-Model Reinforcement Learning Through Diffusion Policy Optimization（MBDPO，2026）**
- 设定Reward-model 用于体现原数据分布 
- 防止真实采样数据偏离 训练数据分布
- diffusion内部去噪步

**Flex-forcing**
- 另一条路线

早期的latent过于模糊，直接去估计未来效果不好
- 现有基于分支，树状搜索可以，但是局限于分支数量和分支anchor，同时计算开销大
- 但是目前有文章说明diffusion在早期就已经确定未来多模态分支走向，再去噪只是细化
	- 1：未来本身是链式结构，zhis会影响 future ： flex-forcing 联合训练解决









#### 跨维度索引

下表把完整方法拆成可并列检查的字段，仅用于横向查阅，不构成新的分类层级。其中，“候选 / 采样状态”专门保留一次性候选、单链随机更新、独立多链、参数化分布与 particles 等原本容易被机制标签遮蔽的性质：

| 方法 | 一级拓扑 | 二级机制 | 候选 / 采样状态 | 规划操作 | 评价或反馈来源 | 训练—部署方式 |
| --- | --- | --- | --- | --- | --- | --- |
| World4Drive | Width | World-Feedback Selection | 固定 6 候选；一次生成与选择 | Selection | Intention-conditioned future latent + ScoreNet | 联合训练；推理时生成 6 个候选、预测未来 latent 并按分数选择 |
| RoboMonkey | Width | Verifier-based Selection | 批量候选与局部扰动；一次选择 | Selection | VLM verifier | 冻结 VLA；部署时采样、扰动并选择 |
| VeGAS | Width | Verifier-based Selection | 并行独立候选；一次选择 | Selection | Generative verifier | 单独训练 verifier；部署时并行采样与选择 |
| SVA | Width | Q-guided Selection | 在线候选集；一次选择 | Selection | Q-value / empirical returns | 离线 MCTS 蒸馏；在线 Width Selection |
| Beyond Success / JITI | Width | Event-gated Verifier Selection | 事件触发的候选批次 | Adaptive Selection | Elegance Critic / Q fluctuation | 训练 Cal-QL critic；关键时刻才启用多候选选择 |
| VERITAS | Width | Visual Verifier Selection | 视觉候选集；一次选择 | Selection | Gradient-free visual verifier | 在线选择不更新策略；verified rollouts 可用于后续离线微调 |
| DreamTrajectory | Width | World-Feedback Selection | 搜索得到的固定候选集 | Selection | Predicted trajectory / intention alignment | 训练轻量 trajectory world model；部署时 search–predict–score |
| MG-Select | Width | Verifier-free Selection | 固定多样本集合 | Selection | KL divergence | 无外部 verifier；在线采样与内部打分 |
| BID | Width | Sample-relative Temporal Selection | strong / weak chunk 候选批次 | Selection | Backward coherence + forward contrast | 复用 strong/weak policies；部署时批量采样和选择 |
| RACE | Width + Local Optimization | Reachability-aware Chunk Search | chunk 候选批次 + 局部重定时 | Selection + Retiming | Smoothness / controllability / kinodynamic limits | 训练 reached-state policy；部署时重定时并搜索可接入 chunk |
| ECoT | Depth | Rethinking / Iterative Reasoning | 单条自回归具身推理—动作序列 | Reasoning → Action | 当前观测、任务与已有推理上下文 | 合成具身推理监督；部署时先生成多阶段推理 token，再生成动作 |
| CoA-VLA | Depth | Rethinking / Iterative Reasoning + Generative Refinement | 渐进 affordance chain + diffusion action state | Affordance Reasoning + Action Generation | 视觉—文本 affordance、任务进度与 proprioception | 训练 affordance co-injection 与 diffusion action head；部署时动态生成 affordance 并条件化动作 |
| DiffusionDrive | Depth | Generative Refinement | anchor-conditioned 截断去噪链，可批量独立运行 | Refinement | 扩散去噪动力学 | 训练扩散生成器；部署时截断去噪 |
| DiffRefiner | Depth | Coarse-to-Fine Generative Refinement | coarse proposal 对应的去噪状态 | Proposal + Refinement | 条件去噪与 BEV 语义交互 | 联合训练 proposal/refiner；部署时串行粗到细生成 |
| SGAC | Depth + Closed Loop | Self-Guidance / Adaptive Chunking | 当前去噪链 + 跨时刻动作队列 | Guided Denoising + Queue Update | Past-vs-current score / action similarity | 无外部 verifier；部署时负引导并按相似性更新 chunk |
| GPC | Depth + Task-level Width | Multi-Policy Score Composition | 单条联合去噪链 + 任务级权重候选 | Generative Refinement + Weight Search | Convexly composed policy scores / rollout SR | 冻结 parent policies；搜索组合权重并执行联合去噪 |
| DriveVer | Depth | Evaluation-Guided / Learned Correction / Direct Verifier | 单个当前轨迹持续修正 | Refinement | 轨迹安全与几何修正信号 | 训练 verifier；部署时验证并修正 |
| CriticVLA | Depth | Evaluation-Guided / Learned Correction / Direct Verifier | 单个粗轨迹 + 一次 critique 修正 | Refinement | 结构化多模态 critique（非显式 V/Q 网络） | 训练 critic/refinement；部署时单步修正 |
| ReflectVLM | Depth | Evaluation-Guided / Learned Correction / World-Model-Mediated | 单个 proposal + imagined rollout | Look-ahead + Reflection | Diffusion dynamics imagined future | 训练 VLM/DDM；部署时 H 步想象后一次反思 |
| SC-VLA | Depth | Evaluation-Guided / Learned Correction / World-Model-Mediated | 基础动作 + residual correction | Refinement | Sparse world imagination | 训练想象与精化模块；部署时内部反馈修正 |
| SafeBimanual | Depth | Generative Refinement + Evaluation-Guided / Objective Guidance | 单条随机去噪链；每步注入目标梯度 | Guided Denoising | VLM-scheduled differentiable safety costs | 冻结 diffusion policy；部署时注入 cost gradient |
| TOAD | Adaptive Population | Parametric / Elite-based Distribution Fitting | Gaussian population；跨轮 elite 重采样 | Optimization | Trajectory scorer | 训练 scorer；部署时运行多轮 CEM |
| WAV | Adaptive Population | Parametric / Latent Elite Update | 双层 latent Gaussian populations | Latent-space Inference | Predicted future + trajectory value | 训练 world/value/action flow；部署时更新 latent distributions |
| Diffusion-ES | Adaptive Population | Non-parametric Population Evolution | empirical particles；重采样与生成式变异 | Evolutionary Optimization | Black-box reward | 训练 diffusion prior；部署时重采样并进行截断扩散变异 |
| E-TTS | Adaptive Population | Feedback-Conditioned Candidate Regeneration | reasoning–action 候选群体；跨轮再生成 | Joint Sampling + Iterative Refinement | History-aware reasoning/action verifiers | 冻结 base VLA；评价反馈条件化下一轮 reasoning–action 采样 |
| TPP/DTPP | Branching | Tree-Structured Policy Planning | 显式轨迹树 frontier | Branching / Pruning | 条件环境预测与 cost | 联合训练；部署时树状条件规划 |
| MCTD | Branching | Tree Search over Generative States | diffusion-state tree frontier | Branching / Refinement | 树搜索评价信号 | 部署时组合去噪深度与树搜索 |
| VLA-Reasoner | Branching | World-Model MCTS | MCTS tree nodes | Branching / Backup | Predicted futures + intermediate reward | 冻结 VLA；部署时在线 MCTS |
| TTGS | Graph Search | Dataset-State Shortest Path | dataset-state graph nodes | Subgoal Planning | Value-derived state distance | 无新增训练；每 episode 图搜索并闭环选择 subgoal |
| Inner Monologue | Closed Loop | Environment-Feedback Replanning | 每个环境时刻维护一个待执行动作 | Replanning | 真实环境或人类反馈 | 动作执行后跨时刻重新规划 |
| SAIL | Closed Loop | Adaptive Guidance / Speed Scheduling | 当前 action chunk + 执行状态 | Execution Adaptation | Tracking error + motion complexity | 训练 reached-state target；部署时自适应 guidance 与速度 |
| AAC | Closed Loop | Uncertainty-Gated Replanning Horizon | $N$ 个长度为 $H$ 的 action chunks；仅用于逐位置熵估计 | Horizon Selection | Multi-sample action entropy | 无额外训练；部署时采样估计熵并选择执行长度 |

`World Rollout -> Outcome Evaluation -> Selection` 只描述“世界模型生成未来并从已有候选中选择”的一种用法。世界模型还可以支持单轨迹精化、候选分布优化和条件分支规划，因此不能用 selection 代表全部世界模型反馈机制。




第一性原理
- 世界模型能带来监督
	- 未来表征监督
	- 完整轨迹的结果监督
	- 动作之间的相对偏好监督
	- 时间过程监督-相对普通静态 scorer
	- 局部动作敏感性监督
		- 通过世界模型比较J(τ+δτk​)−J(τ).可以近似得到 J场
	- 环境响应监督
	- 负样本rollout监督
		- RL如果训练时几乎从未访问，过那么 actor gradient 中基本没有这些状态
			- 专家日志上的 offline AC 则不会出现此rollout
	- 模型不确定性监督，同一action引发的未来监督
- 






**VL-JEPA**
- 动机
	- 所有视觉理解都通过文本生成完成 
		- 意味着模型同时承担两个任务，需要理解世界 + 组织语言
- **结果**
	- 生成速度加快
	- 效率提升
	- 选择性解码提升效率
- **利用**
	- 在潜空间能更好抽象状态
		- 如 前车急刹，ego 应减速，由于前方车辆速度下降，需要制动 ->前向间距缩小，碰撞风险上升，减速动作有利 学习到某个状态
		- “如果我将这个灯开关往下拨，这里会发生什么？”，“灯被关闭” 和 “房间会变暗” 都是有效的回答

**V-JEPA-2**
- 动机
	- 生成action-based next frame
- **结果**
	- ![[Pasted image 20260725174312.png]]




基于jepa的双向对齐机制
- 传统问题：
	- 三大问题
	- 交替问题：锚定点局部，没有基于未来结果进行全局修正能力


基于动态交互图的世界模型





**[SparseWorld（AAAI 2026，4D occupancy 版）](https://ojs.aaai.org/index.php/AAAI/article/view/37347)**
- **动机**
	- 语义占据：把车辆周围的三维空间切成许多小体素，并判断每个体素具体内容
	- 占用预测部分去做比较
- **解决**
	- 稀疏查询
	- 连续预测模块  查出来的稀疏query做连续预测


**[SparseWorld（IROS 2026，端到端规划版）](https://arxiv.org/abs/2605.24354)**
action<->predict 先pred，然后sparse-pred ->轨迹细化 
无action-based E2EAD 
- 动机
	- 稀疏状态



**GraphWorld**
- **动机**
	- 现有问题
		- 大多数现有的端到端自动驾驶方法仍局限于短时程规划，缺乏对长期时间依赖关系的建模能力
			- 类MPC式决策 缺少长程建模能力可能导致 不连续，跳变问题
			- 利用流匹配建模Wnow到未来Wgt的过渡
		- 世界模型费时间
- **方法**
	- 以ego为中心的图+world-latent构建
	- FM运输到遥远时刻


GraphAD
- other-plan->ego的规划
![[Pasted image 20260719074531.png]]






**GWM**
- **动机**
	- 因此，本文旨在引起对此紧迫研究问题的关注：我们能否将世界模型扩展以处理跨广泛任务的图结构


**GWM-调研**
图的基本概念+ 特殊结构
- 节点、边、邻接、关联、度、邻域、路径、环、连通性​
图操作
- graph coarsening：图粗化； graph contraction：边收缩；node aggregation：节点聚合； sparsification：图稀疏化；subgraph extraction：子图提取；graph pruning：图剪枝；graph partition：图划分。
图上的随机过程
-  随机游走；Markov chain；PageRank；diffusion process；hitting time；commute time。
图的矩阵和谱性质
- 拉普拉斯特征值； 图频率；谱聚类；连通性与特征值关系；图扩散； 随机游走；图信号平滑。
图上的经典组合优化问题
- 最大流，最小生成树 。。。
- **动机**
	- 显式状态
		- 邻接关系：用关系代替连续坐标
			- 精确度量地图并非必要， 连续几何地图→离散邻接结构
		- 路径与可达性：局部连接组合成长程关系
			- 长程任务可以由局部可达关系组合而成，长程规划和稀疏奖励下的信用分配困难​
		- 加权最短路：将规划目标转换为图上的路径优化
		- **稀疏性与图粗化**：压缩状态空间
			- 节点的完全图最多有n2,而拓扑导航图通常要求E远小于N2
			- 图负责承载抽象后的连接结构 
- 显式/隐式空间
	- 利用图的离散拓扑、稀疏连接和路径可组合性，把高维连续空间中的长程规划，转换成“关键节点之间的可达性搜索
- 图作为




**Diffusion Transformer World-Action Model for AV Scene Prediction**
- **动机**
	- **现有问题**
		- 在哪个潜在空间中进行预测
			- CLIP latent：语义、文本对齐信息  -->知道“这是道路和汽车”，但细致几何较弱
			- DINOv2 latent：视觉结构、物体和几何信息 -->有较强静态场景表征
			- V-JEPA2 latent：视频时序和运动规律 ->更容易保留 ego-motion、动态变化
			- VQ-VAE/SD-VAE latent 
			- BEV latent
		- 在该空间中生成式 Transformer 相较于确定性回归器是否具有额外价值
			- 确定性回归器
			- 生成式diffusion的概率分布学习
		- 没有建模真正运动信息，当前的运动学信息并未学入模型
			- 运动加权训练 DWS 根据帧间变化给动态区域更高权重，避免静态背景主导损失
			- 运动—外观解耦 
			- 显式几何和轨迹约束 加入深度一致性和三维轨迹一致性，使视频从“纯视觉合成”转向“几何与动态一致的状态建模”
			- 将物理结构直接写入状态转移 LaWM 不让网络自由预测下一状态，而是学习 latent Lagrangian
实验
- 问题1：如果一个 latent 能很好地恢复车辆动作，那么它应当包含较多与驾驶动力学相关的信息
	- V-JEPA2 的时序表示优于所有单帧编码器，说明带视频上下文的 latent 更容易保留 ego-motion 和车道曲率等动态信息

- 问题2：我们通过一种受控的、假设驱动的诊断方法来回答第二个问题，该方法明确了 DiT 起作用的时机：关键因素包括预测目标（𝑥0 与 𝜖 之比）、空间结构、残差锚定，以及采样与目标不确定性之间的匹配程度。
	- 如果这个分布接近单峰、方差很小  确定性回归更好
	- 如果这个分布高度多模态，那么一个点预测无法表达多个合理未来，Diffusion 才更有价值
- 数据
	- 更长时的时域不利于DIT，更长时更加接近单峰
	- 加入动作序列后，DIT会效果比MLP更好 ，比较cosine相似度  发现delta更大


- **仍然存在的问题**
	- 对时间保真度进行实验：由于diffusion锚定当前zt+未来action序列提供的影响，导致不能很好捕捉世界变化特征，跳跃预测仍然粗糙，不能生成高保真效果图	
	- 贡献
		- 识别出空间 token、𝑥0 目标、残差锚定以及与目标不确定性匹配的采样






**DTPP RaMP**
对段进行回归 和直接diffusion进行因果加噪回归  TPP展开
- 问题（利用WM辅助规划）->结构+利用WM->和DTPP结构相同  >>>  问题改变 这样展开连贯性不强，不适合规划 
	- **问题**：转换为DTPP/RaMP的问题
		- 展开不连续的问题 ，由于是采样 
		- DTPP的本身的场景信息编码不够 <弱>
	- **DTPP的本身动机**  **动机**
		- 每个 ego 分支都要重新预测 太重，需要减枝+动态奖励评估
	- **我们想解决DTPP的未解决问题**
		- 弥补DTPP条件预测的单模态或有限概率表达 <RaMP已经解决>
		- 减少手工轨迹采样的覆盖盲区  diffusion
	- **结论**
		- 仅用diffusion对TPP进行改造，创新度不够：本质上是diffusion带来的能力



**PhysisForcing**



## 发展

### world-model 辅助规划
- vidar，driveworld，occworld


### action-based-world model 辅助规划

#### 分类1：
**串行**
- image-> act->image->act
	- 

**image and act**
- 统一进行额外监督
	- DriveVLA-W0
	- 
- **缺点**：
	- 统一了网络，不等于规划真正使用了动力学
	- 弱耦合
- 进化：
	- 少量交互
**image then act**
- frame-t,action-t
- 没有另一个网络把这种动力学知识“吸收成 policy”。对于纯评价模型：把推理时的 WM 删掉以后，无法评价动作，整个动作选择机制也没了 ，
- 策略学习到了一个cost-model中
- **缺点**：
	-  未来场景生成本身更容易受模糊、漂移和局部幻觉影响。
	- 动作恢复更被动：动作只能在已经生成出的未来之上事后恢复，信息瓶颈很强
	- 误差会级联：一旦未来画面有偏差，后续动作和下一步画面都会继续在偏差上滚动
- 进化：
	- Metis
	- ForgeDrive
	- Uni-World VLA 时间逐步交互 action-frame-action-frame

**act then image**  
- **缺点** :
	- 动作仍然没有被未来验证
	- 可能产生“自我证实的未来”
	- 误差级联
- raw-action->frame -> 选择action
	- 例子
		- Drive-WM
			- arXiv 首次提交：2023-11-29（[arXiv:2311.17918](https://arxiv.org/abs/2311.17918)）
			- 录用会议：[CVPR 2024](https://openaccess.thecvf.com/content/CVPR2024/html/Wang_Driving_into_the_Future_Multiview_Visual_Forecasting_and_Planning_with_CVPR_2024_paper.html)
		- Drive-OccWorld
			- arXiv 首次提交：2024-08-26（[arXiv:2408.14197](https://arxiv.org/abs/2408.14197)）
			- 录用会议：[AAAI 2025](https://ojs.aaai.org/index.php/AAAI/article/view/33010)
		- WoTE 候选排序
			- arXiv 首次提交：2025-04-02（[arXiv:2504.01941](https://arxiv.org/abs/2504.01941)）
			- 录用会议：[ICCV 2025](https://openaccess.thecvf.com/content/ICCV2025/html/Li_End-to-End_Driving_with_Online_Trajectory_Evaluation_via_BEV_World_Model_ICCV_2025_paper.html)
		- UniDrive-WM
	- 进化：
	- World4Drive：多模态轨迹 proposals -> intention-conditioned future latent -> world-model scoring -> 选择原候选
			- arXiv 首次提交：2025-07-01（[arXiv:2507.00603](https://arxiv.org/abs/2507.00603)）
			- 录用会议：[ICCV 2025](https://openaccess.thecvf.com/content/ICCV2025/html/Zheng_World4Drive_End-to-End_Autonomous_Driving_via_Intention-aware_Physical_Latent_World_Model_ICCV_2025_paper.html)
		-  forgedrive：action->frame->actioni->fram 细化到逐步生成
			- arXiv 首次提交：2026-06-30（[arXiv:2606.31226](https://arxiv.org/abs/2606.31226)）
			- 录用会议：暂无公开信息（截至 2026-07-22）
			- 
#### 分类2
##### **基于流的世界模型（需要按 flow 的实际作用位置细分）**
- **分类结论**
	- Epona、LiDAR FWM、DynFlowDrive 都使用了 flow matching，但不是同一类任务：
		- Epona：世界模拟 + 端到端轨迹规划；flow 是图像/轨迹生成头的训练目标，并不是显式的世界状态转移动力学。
		- LiDAR FWM：LiDAR/占据预测 + 跨域预训练；不包含规划器，不能直接归为“世界模型辅助规划”。
		- DynFlowDrive：世界模型辅助规划；flow 直接建模“候选动作条件下的潜状态转移”，但只在训练期参与轨迹评分监督。
	- 因此，“基于流”更适合看成**实现机制分类**，不能替代“生成、预测、预训练、辅助规划”等任务分类。
- **例子**
	- **Epona: Autoregressive Diffusion World Model for Autonomous Driving**
		- **时间**
			- arXiv 首次提交：2025-06-30（[arXiv:2506.24113](https://arxiv.org/abs/2506.24113)）
			- 录用会议：[ICCV 2025](https://openaccess.thecvf.com/content/ICCV2025/html/Zhang_Epona_Autoregressive_Diffusion_World_Model_for_Autonomous_Driving_ICCV_2025_paper.html)
		- **任务类别**
			- 主任务：长时驾驶视频生成 + 直接端到端轨迹规划。
			- 属于“世界模型辅助规划”，但辅助发生在**共享潜表示和联合训练**层面；规划推理时可关闭视频生成头，不是先在线 rollout 多个未来再用 cost/reward 选动作。
		- **对应分类1**
			- 最接近 **act then image**：历史状态先由 TrajDiT 一次生成未来 3 秒连续轨迹，VisDiT 再以该轨迹或外部轨迹为条件生成下一帧。
			- 但它不是严格的 `action-t -> frame-t -> action-t+1` 逐步交替，而是轨迹头与下一帧头共享 MST 潜状态、异步/模块化生成，应单列为“trajectory-first + next-frame generation”。
		- **宣称问题**
			- 固定长度的视频扩散联合建模整段视频，**缺少逐时刻的因果分解**，难以灵活生成长序列，也不方便加入多模态轨迹规划。
			- GPT 式世界模型虽然可自回归扩展时长，但图像和动作离散 token 会损失视觉细节与轨迹精度，而且常只预测下一动作。【**完全不需要**】
			- 自回归训练使用真实历史帧、推理依赖自身生成帧，存在 teacher-forcing 分布偏移和长时误差累积。
		- **实际解决方法**
			- MST 用因果时间注意力压缩历史图像和动作；TrajDiT 用 rectified flow 一次生成连续多步轨迹；VisDiT 用 rectified flow 生成动作条件下的下一帧。
			- Chain-of-Forward 周期性地把模型自己的一步预测送回历史上下文，使训练阶段提前暴露于推理误差。
			- Temporal-aware DCAE 在解码前增加跨帧交互，缓解逐帧解码闪烁。
	- **Towards Foundational LiDAR World Models with Efficient Latent Flow Matching**
		- **时间**
			- arXiv 首次提交：2025-06-30（[arXiv:2506.23434](https://arxiv.org/abs/2506.23434)）
			- 录用会议：[NeurIPS 2025](https://neurips.cc/virtual/2025/poster/118252)
		- **任务类别**
			- 主任务：LiDAR/4D occupancy forecasting、跨传感器/场景/语义任务预训练与小样本迁移。
			- 属于“世界状态预测/基础世界模型预训练”，**不属于世界模型辅助规划**；论文结论明确把接入 planning/control 留作未来工作。
		- **对应分类1**
			- 主预训练流程是 `历史 LiDAR/occupancy -> 未来 occupancy`，不生成动作，因此不属于分类1的 image/action 闭环。
			- 在使用未来 ego trajectory 作为条件的分支中，可近似映射为 **act then world-state**（轨迹 -> 未来 occupancy），即 act then image 的 LiDAR/occupancy 版本；但它只预测动作后果，不回推出下一动作。
		- **作者宣称的原有问题**
			- Lidar 专属，数据域的偏差问题
			- 现有方法对 LiDAR 表示压缩不足，latent 过大；扩散/DiT 结构训练目标和时间建模也较重，造成参数与计算浪费。
			- 从无语义 LiDAR 迁移到语义 occupancy 时，新旧 VAE 潜空间不对齐，使预训练的动态模型难以复用。
		- **实际解决方法**
			- 用连续编码的 Swin-VAE 将 occupancy 压到较小潜空间；用 conditional rectified flow 从历史潜状态生成未来潜状态，并可加入历史或未来 ego trajectory 条件。
			- 用 U-Net 式时空 DiT、时间模块后的 3D 卷积和 classifier-free guidance 提升时间依赖建模与采样效率。
			- 先在无标注 nuScenes LiDAR 上预训练 VAE+CFM，再对高线束、室内和语义 occupancy 三类任务微调；通过 cosine latent alignment 保留预训练潜空间结构。
	- **DynFlowDrive: Flow-Based Dynamic World Modeling for Autonomous Driving**
		- **时间**
			- arXiv 首次提交：2026-03-20（[arXiv:2603.19675](https://arxiv.org/abs/2603.19675)）
			- 录用会议：暂无公开信息（截至 2026-07-22，作者主页仍标为 arXiv preprint）
		- **任务类别**
			- 主任务：训练期世界模型辅助规划 / 多候选轨迹评价与评分学习。
			- 世界模型在训练期模拟每个候选动作对应的潜状态演化，并把评价结果蒸馏进轨迹 score head；推理时移除世界模型，直接按 score 选轨迹。
		- **对应分类1**
			- 属于 **act then latent-world-state**：`候选轨迹(action) -> flow latent rollout -> 稳定性/重建评价 -> score监督`，是 act then image 的潜空间版本。
			- 它不在推理期形成 `action -> world -> action` 在线闭环，而是训练期把世界模型的评价能力吸收到 policy/score head。
		- **作者宣称的原有问题**
			- 显式图像/occupancy 生成过度消耗计算去还原纹理和光照，不一定增强面向动作的几何与动力学推理。
			- LAW/World4Drive 等潜空间方法把当前 latent 直接回归到下一时刻 latent，只对齐端点，没有描述中间的状态转移路径；不同动作即使终点相近，其制动过程和风险也可能不同。
			- 仅按轨迹 L2 或终点 latent 重建误差挑选模式，不能判断动作诱导的世界演化是否平滑稳定。
		- **实际解决方法**
			- 用预训练 VAE/基础编码器提取较稳定的多视角 world latent；以候选轨迹为条件，用 rectified flow 学习从当前 latent 到下一时刻 latent 的速度场，并用 Euler 多步积分近似转移过程。
			- 用相邻积分步速度方向的平均角度变化定义 flow stability，再与轨迹误差、未来 latent 重建误差共同决定训练时的最佳轨迹模式，监督 score head。
			- 推理时不运行 flow 世界模型，所以保持与原规划器近似相同的速度；这本质上是训练期的 world-model-to-policy 蒸馏。
		- **效果核对**
			- nuScenes：LAW 从平均 L2 0.61 m/碰撞率 0.30% 改善到 0.57 m/0.22%。论文正文写“降低 0.4 m”，但表格实际是 **0.04 m**，属于明显小数点错误/夸大表述。
			- SSR 官方复现基线为 0.39 m/0.15%，换成 DynFlowDrive 后为 0.35 m/0.14%；加入 ego status 后才达到 0.31 m/0.11%，因此“降低 0.08 m”不能全部归因于 flow 世界模型。
			- NAVSIM：PDMS 88.7，相比 WoTE 88.3 仅提高 0.4，但确实在该表中最高；输入为 Camera+LiDAR，不能与纯 Camera 方法直接解释为同等条件的模型优势。
			- 核心消融：Static WM 为 0.61 m/0.30%，单独换成 Flow WM 为 0.59 m/0.26%，再加入 world-feature 设计才到 0.57 m/0.22%，说明 flow 有稳定的小幅增益，但完整提升来自多个组件。
			- mode selection 中，加入重建项已达到 0.58 m/0.22%；再加入 flow stability 只把 L2 改到 0.57 m，碰撞率仍为 0.22%。因此“flow stability 明显提升安全性”的直接证据偏弱，更准确的说法是它略微改善轨迹精度且没有损害安全指标。
		- **结论**
			- DynFlowDrive 是三篇中最符合“flow-based world dynamics + 世界模型辅助规划”的论文，也最适合放在分类2核心位置。
			- 但其连续“动力学路径”是从当前 latent 到单个下一时刻 latent 的生成积分路径，不等同于经过真实时间标定或物理约束的连续车辆—环境动力学；所谓“物理稳定”主要由潜速度方向平滑这一代理指标定义。
			- 实验支持它作为可插拔训练监督带来一致但中等幅度的收益，尚不足以证明它已真正学到可解释、可泛化的物理动力学。
- **横向结论**
	- 如果研究目标是“世界模型如何直接改善规划”，优先看 DynFlowDrive；Epona 是联合生成监督增强 planner；LiDAR FWM 当前只提供可迁移的预测底座。
	- 如果研究目标是“flow 是否比确定性回归更适合多未来”，三篇都没有完全证明：Epona 没把 flow 与回归规划头做纯净对照；LiDAR FWM 承认采样未来接近固定；DynFlowDrive 的主要指标增益较小且 flow-stability 的安全增益不独立。
	- 更准确的进化链应写为：`固定终点回归 -> 动作条件的概率生成 -> 显式建模潜空间转移路径 -> 将转移稳定性蒸馏到轨迹评分`，而不是简单地把所有用了 rectified flow loss 的方法视为同一种世界模型。
##### 基于JEPA的自动驾驶世界模型
**Drive-JEPA**   xpeng
- 多模态轨迹训练
![[Pasted image 20260724170801.png]]

- 问题
	- image then action ：动作只能在生成的视频上进行恢复，一旦视频稍有偏移，则动作会出错，之后会更加滚动；
	- 

**V-JEPA 2**


![[Pasted image 20260724180438.png]]
- 问题
	- action then image：
		- 动作没有被未来验证
	- 动作仍然通过求解函数的方式获取





vla
- 联合
	- unidrive-vla ： MOT




### WorldModel用于预测未来

## 逐篇论文原文补充：前作问题与动机—方法链

> [!note] 阅读口径
> - **Abstract / Introduction**：论文作者直接提出的总问题；**Related Work 定位**：作者相对已有方法强调的缺口，均为压缩转述，不是原文直译。
> - **动机 → 方法**：按“旧方法为什么不够 → 需要什么能力 → 论文如何实现 → 为什么能缓解问题”整理，避免只罗列模块名。
> - 同一论文在原笔记多处出现时只在这里总结一次；MPPI、CMA-ES、Self-Consistency、Gradient-based Optimization 是方法族，不冒充单篇论文。

### A. 世界模型、VLA 与联合规划

#### [InfiniVerse](https://arxiv.org/abs/2606.31109)

- **Abstract / Introduction：** 驾驶场景生成仍难同时满足可控性、跨视角三维一致性、时间连贯性和无限长度扩展；短视频生成好看，并不等于能沿任意自车路线持续构造世界。
- **Related Work 定位：** 视频生成方法擅长外观但缺少显式三维约束，三维/占据生成方法几何可控却不够逼真；已有自回归长视频还会不断累积几何漂移。
- **动机 → 方法：** 需要一个既可扩展又能保持 2D–3D 对齐的生成闭环，因此先从单帧多视角重建 occupancy，再沿轨迹自回归扩展三维场景，以视频扩散渲染外观，并把生成视频反投影回 occupancy 做 sketch-and-refine 校正。

#### [World4Drive](https://arxiv.org/abs/2507.00603)

- **Abstract / Introduction：** 端到端驾驶依赖昂贵的感知标注，而且普通模仿学习只拟合单条专家轨迹，难以理解不同意图对应的多种合理未来。
- **Related Work 定位：** VADv2、Hydra-MDP 等通过额外感知或规划监督增强模型但标注成本高；LAW 类潜空间世界模型提供自监督，却对驾驶意图和多模态候选的利用不足。
- **动机 → 方法：** 若未来潜状态能够评价“这个意图下会发生什么”，它就能反过来选择轨迹；论文用基础视觉先验构造 intention-aware latent，联合多模态轨迹生成、意图条件未来预测和 world-model selector，并用真实未来 latent 自监督对齐预测结果。

#### [DLWM](https://arxiv.org/abs/2604.00969)

- **Abstract / Introduction：** 稠密 BEV/occupancy 世界状态计算昂贵，纯稀疏 query 又可能丢失场景细节；同时，现有 3D Gaussian 表征缺少同时服务占据、预测和规划的统一预训练方式。
- **Related Work 定位：** 稠密栅格方法完整但冗余，query-based 方法高效但状态不够完备，Gaussian 方法多集中在重建或单任务，尚未形成面向驾驶全链路的潜世界模型。
- **动机 → 方法：** 需要兼顾几何完整性和稀疏计算，因此先用语义与深度视图自监督学习 3D Gaussian 世界表示，再分别以 Gaussian flow 和 ego planning 为条件学习两类 latent dynamics，让同一底座覆盖感知、预测与规划。

#### [DriveLaW](https://arxiv.org/abs/2512.23421)

- **Abstract / Introduction：** 很多所谓“统一”的 world-action 模型仍把未来视频预测和动作规划放在两条弱耦合支路中，预测出来的未来 latent 并没有真正成为决策依据。
- **Related Work 定位：** 纯视频世界模型只学环境演化，纯 action policy 不验证动作后果；先生成视频再恢复动作的方法又容易受到生成误差和延迟影响。
- **动机 → 方法：** 关键不是多加一个预测损失，而是让世界表征进入规划计算；DriveLaW 将 Video expert 的未来 latent 注入 Act diffusion planner，并采用分阶段训练先稳住视觉动态，再学习动作，最后联合对齐两者。

#### [ForgeDrive](https://arxiv.org/abs/2606.31226)

- **Abstract / Introduction：** `imagine-then-act` 把动作建立在已生成画面上，画面中的局部幻觉会直接传给动作并在后续滚动中放大。
- **Related Work 定位：** 分离的视频模型与动作模型存在表征断层；严格串行的 frame→action 或 action→frame 只建立单向依赖，不能在同一时刻共同约束视觉和控制。
- **动机 → 方法：** 要减少级联误差，就应显式建模 frame 与 action 的双向条件关系；论文以统一自回归扩散模型生成逐时刻 frame-action pair，用双向 cross-conditioning、解耦扩散时间和非对称噪声日程支持 `act-then-imagine`，并额外预测 ego status 稳定控制。

#### [UniDriveVLA](https://arxiv.org/abs/2604.02190)

- **Abstract / Introduction：** 驾驶 VLA 面临空间感知与语义推理的冲突：原生 2D VLM 几何能力不足，而直接加入 3D 模块并共享参数又可能破坏语言模型已有的推理能力。
- **Related Work 定位：** 纯 VLM 路线缺少精确几何，BEV/3D 增强路线常把理解、感知、动作揉进同一参数空间，造成梯度干扰和能力此消彼长。
- **动机 → 方法：** 需要共享信息但隔离专长，因而采用 Mixture-of-Transformers，为理解、空间感知和动作分别设置 expert，通过 masked joint attention 交换必要信息，再用稀疏感知监督和三阶段训练逐步联合。

#### [LCDrive / Latent-CoT-Drive](https://arxiv.org/abs/2512.10226)

- **Abstract / Introduction：** 文本 CoT 在实时驾驶中解码慢，而且语言符号会压缩掉连续几何、速度和时间关系；只预测动作则缺少“动作将导致什么”的中间推演。
- **Related Work 定位：** 文本推理可解释但与低层控制不对齐，视觉 CoT 信息丰富却生成成本高，普通 latent world model 又可能只做辅助损失而不参与动作形成。
- **动机 → 方法：** 推理表示必须既紧凑又与动作因果对齐；论文交替生成 action-proposal token 与预测其后果的 latent world token，先用真实未来 rollout 冷启动，再以闭环强化学习让潜推演真正改善最终动作。

#### [Uni-World VLA](https://arxiv.org/abs/2603.27287)

- **Abstract / Introduction：** 先一次性想象整段未来、再据此规划属于开环推演，早期生成偏差会污染后续全部动作，而且纯 RGB latent 的几何约束偏弱。
- **Related Work 定位：** 现有 world-VLA 多采用 video-first 或两个任务共享骨干但独立输出，未来生成与动作之间缺少逐时间步交互。
- **动机 → 方法：** 要让每个动作只依赖最近、可校正的未来，应把想象和行动细粒度交替；论文逐步生成 future frame 与 ego action，并注入深度特征，使 actionₜ 由最新 imagined stateₜ 条件化，缩短误差传播链。

#### [CoWorld-VLA](https://arxiv.org/abs/2605.10426)

- **Abstract / Introduction：** 单一文本或单一视觉 latent 很难同时保存驾驶中的语义交互、三维几何、动态演化和自车意图；世界推理即使学到了，也未必能被 action head 有效读取。
- **Related Work 定位：** 文本 CoT 丢连续时空细节，单一 latent CoT 缺少可分工结构，普通 VLA 的感知、预测和规划监督之间仍然竞争。
- **动机 → 方法：** 需要让不同类型的世界知识分工建模后再面向动作融合，因此设计语义交互、几何结构、动态演化、自车轨迹四类 expert token，以多源监督训练，最终由 diffusion planner 分层融合这些 expert 生成动作。

#### [UniDrive-WM](https://arxiv.org/abs/2601.04453)

- **Abstract / Introduction：** 场景理解、轨迹规划和未来预测常被分开训练，导致模型虽能生成未来，却不能用生成结果持续改进当前轨迹。
- **Related Work 定位：** 模块化系统误差逐级传递，联合模型常停留在共享 backbone 或辅助损失；像素未来又比 latent 未来更重，不同未来表征是否适合规划缺少系统比较。
- **动机 → 方法：** 若未来预测被轨迹显式条件化并回到轨迹优化环，就能提供结果监督；论文统一 VLM 场景理解、规划和 trajectory-conditioned future generation，并用预测未来迭代细化轨迹，同时比较离散与连续未来表示。

#### [DriveVLA-W0](https://arxiv.org/abs/2510.12796)

- **Abstract / Introduction：** 大容量 VLA 只由稀疏、低维动作标签监督，形成“supervision deficit”，模型很容易记动作相关性而没有学到完整世界动态。
- **Related Work 定位：** 增加人工感知标签不可扩展；单独训练世界模型再接 planner 存在接口错位；在线视频 rollout 又会增加延迟。
- **动机 → 方法：** 应用免费而稠密的未来观测作为训练信号、但保持部署轻量，因此用自监督 future-image prediction 预训练/联合训练世界表征，并提供自回归离散和扩散连续两种实现，推理时只保留轻量 action expert。

#### [Metis](https://arxiv.org/abs/2606.15869)

- **Abstract / Introduction：** World-action 模型在测试时生成未来视频会带来高延迟；视觉生成和动作控制紧密绑在同一表示中，还会造成任务错配和泛化下降。
- **Related Work 定位：** video-first WAM 让动作依赖昂贵像素生成，完全共享的联合网络又让纹理重建梯度干扰控制学习。
- **动机 → 方法：** 世界预测应在训练时帮助动作、而不是成为推理必经路径；论文用 MoT 将 video/action expert 解耦，以非对称注意力允许联合学习，但部署时 action expert 可绕过视频生成直接输出。

#### [VaViM / VaVAM](https://arxiv.org/abs/2502.15672)

- **Abstract / Introduction：** 通用视频生成规模化后是否真的能迁移到真实驾驶控制并不明确，生成质量和行动能力之间缺少直接证据链。
- **Related Work 定位：** 既有视频模型主要评估视觉预测，端到端驾驶则直接从图像学动作，二者很少在同一数据和闭环控制任务上系统连接。
- **动机 → 方法：** 如果视频预训练学到了道路动态，其表示应能降低动作学习难度；论文先训练自回归驾驶视频模型 VaViM，再以其视觉表示构建模仿学习轨迹策略 VaVAM，检验生成式预训练向控制的迁移。

#### [FutureSightDrive](https://arxiv.org/abs/2505.17685)

- **Abstract / Introduction：** 文本 CoT 是对场景的抽象符号压缩，容易产生时空关系歧义并丢失小目标、距离和未来运动细节。
- **Related Work 定位：** action-only VLA 缺少显式未来推演，文本 CoT 可解释但不够几何化，独立视频世界模型又未必与最终轨迹对齐。
- **动机 → 方法：** 应把未来视觉本身作为可供规划读取的 CoT；论文生成融合感知标注与未来画面的统一 future sight，再把 VLM 当作 inverse dynamics model，由这个可视化未来反推轨迹，实现视觉生成与理解共用一套推理链。

#### [DriveWorld-VLA](https://arxiv.org/abs/2602.06521)

- **Abstract / Introduction：** 世界模型与 VLA 之间的 latent 共享不足，视觉想象常只是旁路辅助任务，对实际动作影响很弱。
- **Related Work 定位：** 像素 rollout 成本高且易漂移，独立 world model 与 policy 接口不一致，联合训练但各自输出也不能保证 action 真正使用未来信息。
- **动机 → 方法：** 应让 world latent 成为 planner 的核心状态；论文在统一 latent 空间中进行 action-conditioned feature imagination，并直接用想象后的特征规划，避开高成本像素生成。

#### [LAW](https://arxiv.org/abs/2406.08481)

- **Abstract / Introduction：** 端到端驾驶通常依赖昂贵的 3D 检测、地图和运动标注，而单纯轨迹模仿提供的监督过于稀疏。
- **Related Work 定位：** 多任务感知监督能改善表示但不可规模化；像素级未来生成包含大量与规划无关的纹理，计算代价也高。
- **动机 → 方法：** 未来观测天然提供免费监督，因此以当前 latent 和预测 ego action 生成未来 latent，再与真实未来观测的编码对齐，使动作学习和驾驶表征通过潜世界预测共同训练。

#### [LaWAM](https://arxiv.org/abs/2606.15768)

- **Abstract / Introduction：** VLA 缺少动作后果预见，而现有 world-action model 往往生成冗余像素视频，导致时延高、控制信息密度低。
- **Related Work 定位：** action-only policy 没有 foresight，video-based WAM 又把大量容量用于外观；仅把未来预测作为 auxiliary loss 不能保证 action expert 读取它。
- **动机 → 方法：** 需要在紧凑 latent 中预测与动作直接相关的视觉子目标；论文用 action-conditioned latent world model 生成 latent visual subgoal，再把该子目标显式条件化到 action expert，从而用低成本的未来表征指导动作。

#### [VL-JEPA](https://arxiv.org/abs/2512.10942)

- **Abstract / Introduction：** 经典 VLM 无论任务是否需要语言，都自回归生成文本，模型被迫同时学习语义和表面措辞，推理还要承担逐 token 解码成本。
- **Related Work 定位：** 对比式视觉语言表征适合检索但不直接预测目标语义，生成式 VLM 通用却低效，并容易把同义表达差异当成学习目标。
- **动机 → 方法：** 若直接预测目标文本的连续语义 embedding，就能把“理解”与“说出来”分离；VL-JEPA 在潜空间做视觉到语言表征预测，仅在确实需要开放文本时接轻量 decoder，从而支持选择性解码、分类、检索和 VQA。

#### [V-JEPA 2](https://arxiv.org/abs/2506.09985)

- **Abstract / Introduction：** 世界理解和行动学习通常需要大量任务专用交互数据，而现实机器人数据昂贵；单帧自监督也难学到运动与物理变化。
- **Related Work 定位：** 像素生成世界模型把容量花在不可预测纹理上，纯视觉表征不建模动作条件后果，端到端机器人策略又依赖大规模示范。
- **动机 → 方法：** 先从海量无动作视频学抽象时空规律，再用少量动作数据建立可控 dynamics；论文先做 100 万小时 action-free video JEPA 预训练，再用不足 62 小时 DROID 数据训练 action-conditioned world model，部署时采样动作后果并以 MPC 朝图像目标规划。

### B. 端到端驾驶、稀疏状态与结构化世界模型

#### [SparseDrive](https://arxiv.org/abs/2405.19620)

- **Abstract / Introduction：** 模块化驾驶会在感知、预测、规划接口处丢信息并累积误差；稠密 BEV 端到端模型计算重，且预测和规划的交互设计仍较粗糙。
- **Related Work 定位：** object-centric 方法通常只稀疏化感知，后续运动预测和规划仍不对称；已有 planner 对多模态轨迹的选择与碰撞安全处理不足。
- **动机 → 方法：** 需要从感知到规划都保持稀疏、对称的对象表示，因此构建 symmetric sparse perception、并行 motion planner，以及分层轨迹选择与 collision-aware rescoring，在避免稠密 BEV 的同时保留实例级交互。

#### [VAD](https://arxiv.org/abs/2303.12077)

- **Abstract / Introduction：** 稠密 raster/BEV 表示计算量大，还把车辆、车道等本来具有结构的实体混成像素，规划器难以直接读取实例约束。
- **Related Work 定位：** 传统模块化方案误差传递，已有端到端方法又依赖稠密语义图、复杂后处理或手工代价，结构和效率仍不理想。
- **动机 → 方法：** 驾驶决策真正关心的是 agent 和地图向量，因此把环境完整表示成 vectorized agents/maps，并把这些向量作为显式规划约束，去掉稠密 raster 和额外后处理。

#### [ViDAR](https://openaccess.thecvf.com/content/CVPR2024/html/Yang_Visual_Point_Cloud_Forecasting_enables_Scalable_Autonomous_Driving_CVPR_2024_paper.html)

- **Abstract / Introduction：** 通用图像预训练没有同时对齐驾驶所需的语义、三维几何和时间动态，依赖人工 3D 标注又难以随数据规模扩展。
- **Related Work 定位：** 图像重建偏外观，BEV 感知预训练依赖标签或只建模当前帧，视频预测常在像素空间学习而缺少明确三维监督。
- **动机 → 方法：** 未来 LiDAR 点云天然同时携带几何和动态监督，论文从历史多视角图像预测未来点云，并用 latent rendering 把图像特征投射到三维，使大规模无标注驾驶数据可用于表征预训练。

#### [DriveWorld](https://arxiv.org/abs/2405.04390)

- **Abstract / Introduction：** 现有驾驶预训练多采用 2D/3D 静态 pretext，忽略真实驾驶是持续演化的 4D 时空过程。
- **Related Work 定位：** 图像或点云 masked modeling 学到单帧结构，常规时序模型又难同时保存长期动态记忆和稳定静态场景信息。
- **动机 → 方法：** 世界状态需要分别处理会变化的动态信息和可跨帧复用的静态信息；论文提出 Memory State-Space Model，以 Dynamic Memory Bank 维护时序，以 Static Scene Propagation 保留环境，并用 task prompt 适配下游任务。

#### [Drive-OccWorld](https://arxiv.org/abs/2408.14197)

- **Abstract / Introduction：** 很多驾驶世界模型主要用于视频生成或预训练，没有把动作条件的未来状态直接用于端到端规划。
- **Related Work 定位：** 视频世界模型外观强但几何弱，occupancy 预测通常不考虑自车行为，传统 planner 又在当前状态上评分而不比较候选动作诱导的未来。
- **动机 → 方法：** 规划需要统一可占用性、语义和运动的 4D 未来；论文预测 action-conditioned occupancy 与 flow，通过语义/运动条件归一化建模动态，再用基于未来占据的 cost 选择轨迹。

#### [Drive-WM](https://arxiv.org/abs/2311.17918)

- **Abstract / Introduction：** 规划器若只看当前场景，无法判断不同机动动作未来会产生何种风险；现有端到端方法缺少可控的多视角未来想象。
- **Related Work 定位：** 单视角视频预测不能覆盖自动驾驶环视几何，普通视频生成不可由候选驾驶动作精确控制，行为克隆也没有显式结果评价。
- **动机 → 方法：** 应对多个 maneuver 分别 rollout 一致的环视未来，再按结果选动作；论文以 view-factorized 时空建模生成 action-conditioned multiview video，并用 image-based reward 对不同未来及其轨迹打分。

#### [WoTE](https://arxiv.org/abs/2504.01941)

- **Abstract / Introduction：** 现有轨迹评价主要依赖当前状态、规则或静态 cost，无法看到候选轨迹真正诱导的未来，而且容易受当前感知误差影响。
- **Related Work 定位：** trajectory scorer 只在现有特征上排序，视频 world model 又太重；已有 BEV 预测通常与具体候选轨迹解耦。
- **动机 → 方法：** 每条候选都应有自己的未来证据，因此用候选轨迹条件化 BEV world model，分别生成其未来 BEV，再由 reward model 联合当前和预测未来选择最优轨迹。

#### [Epona](https://arxiv.org/abs/2506.24113)

- **Abstract / Introduction：** 固定长度的全局视频扩散不便生成长时驾驶序列，也难自然接入多模态轨迹；自回归模型则有训练—推理分布偏移和误差累积。
- **Related Work 定位：** 整段视频联合生成缺少逐时因果分解，离散图像/动作 token 损失细节与轨迹精度，teacher forcing 使模型没见过自身错误历史。
- **动机 → 方法：** 需要可扩展且能在训练中承受滚动误差的生成过程；论文将历史编码、轨迹生成和下一帧生成模块化，以 rectified flow 学轨迹与视觉头，并用 Chain-of-Forward 把自生成历史周期性送回训练上下文。

#### [Towards Foundational LiDAR World Models with Efficient Latent Flow Matching](https://arxiv.org/abs/2506.23434)

- **Abstract / Introduction：** LiDAR 世界模型多面向单数据集或单任务，潜表示压缩不足、生成目标计算重，难成为可跨传感器和场景迁移的基础模型。
- **Related Work 定位：** 像素/体素扩散的 latent 仍过大，从无语义 LiDAR 迁移到语义 occupancy 时潜空间错位，导致预训练 dynamics 难复用。
- **动机 → 方法：** 需要小而稳定、跨域可对齐的 LiDAR latent；论文以高压缩 Swin-VAE 编码 occupancy，用 conditional rectified flow 预测未来，并通过 latent alignment 把无标注预训练迁移到高线束、室内和语义任务。它当前不含规划器，不能写成“已辅助规划”。

#### [DynFlowDrive](https://arxiv.org/abs/2603.19675)

- **Abstract / Introduction：** 显式图像/occupancy 生成浪费容量在纹理上，确定性 latent endpoint 回归又只对齐起终点，不能描述候选动作引起的中间演化与稳定性。
- **Related Work 定位：** LAW、World4Drive 类 latent WM 提供未来监督，但通常以重建误差选模式；相近终点可能对应截然不同的制动过程和风险，单一端点误差无法区分。
- **动机 → 方法：** 轨迹评分应感知动作条件下潜状态转移是否平稳；论文用 rectified flow 学当前到未来 latent 的速度场，以候选轨迹为条件多步积分，并把重建误差和 flow stability 蒸馏给 score head，部署时移除世界模型以避免额外延迟。

#### [SparseWorld（AAAI 2026，4D occupancy 版）](https://ojs.aaai.org/index.php/AAAI/article/view/37347)

- **Abstract / Introduction：** 稠密 4D occupancy world model 在空旷区域也执行等量计算，状态分辨率和预测范围固定，难兼顾效率、灵活性与长时预测。
- **Related Work 定位：** dense voxel/BEV 方法计算随空间体积增长，固定 query 方法虽稀疏却不能随场景内容动态分配容量，离散逐帧预测还容易造成时间不连续。
- **动机 → 方法：** 世界状态应只在有信息的位置分配表示并可连续查询，因此以 sparse dynamic queries 表示场景，通过自适应 query 更新和连续预测模块生成 4D occupancy，降低冗余计算。

#### [SparseWorld（IROS 2026，端到端规划版）](https://arxiv.org/abs/2605.24354)

- **Abstract / Introduction：** 稠密场景世界模型计算高且包含大量与规划无关的背景，因而很难以低延迟真正反哺端到端驾驶。
- **Related Work 定位：** occupancy/video rollout 细节丰富但昂贵，稀疏端到端方法通常只预测对象运动，没有把未来稀疏状态作为规划校正信号。
- **动机 → 方法：** 应只想象地图与动态 agent 等规划关键实体；Sparse Dreamer 自回归预测未来 sparse map/agent 表征，用未来特征细化 motion 与 planning，并通过自适应轨迹选择把稀疏 rollout 变成决策依据。

#### [GraphAD](https://arxiv.org/abs/2403.19098)

- **Abstract / Introduction：** 全注意力交互把所有 agent、地图和自车两两连接，计算昂贵，也忽略了驾驶交互具有局部、异构和几何先验。
- **Related Work 定位：** 稠密 attention 隐式学习关系但难解释；普通图方法常只建 agent-agent 关系，没有统一 risky agent、地图约束和 ego planning。
- **动机 → 方法：** 只传播对当前决策有意义的信息即可，因此构造 Interaction Scene Graph，将 ego、风险 agent 和相关地图元素按类型连接，剪掉无关交互后进行感知—预测—规划信息传递。

#### [GraphWorld](https://arxiv.org/abs/2606.16274)

- **Abstract / Introduction：** 多数端到端驾驶只优化短时轨迹，缺少长期时间依赖和持续交互建模，容易出现不连续决策并降低安全与泛化。
- **Related Work 定位：** 短时 BEV planner 类似局部反应器，视频 world model 长程但代价高，普通交互图又只描述当前关系而不滚动未来世界状态。
- **动机 → 方法：** 长时规划需要紧凑、可递推的交互状态；论文以 ego-centric graph 表示关键参与者关系，用 latent world dynamics 推进交互状态，再以 world-state-conditioned planner 生成长期、安全感知轨迹。

#### [Graph World Model (GWM)](https://arxiv.org/abs/2507.10539)

- **Abstract / Introduction：** 主流世界模型围绕图像、视频、文本等规则张量设计，难原生处理实体数和边结构变化的图世界；图基础模型又通常局限于节点/边预测等单一任务。
- **Related Work 定位：** world model 缺图结构，graph foundation model 缺少动作条件的环境演化和跨模态任务接口，两条研究线尚未统一。
- **动机 → 方法：** 如果把任务和动作也表示为图的一部分，同一 dynamics 就能覆盖不同图任务；论文以通用 message passing 处理图与非结构化多模态状态，并用 action node 表示要执行的任务或干预。

#### [Diffusion Transformer World-Action Model for AV Scene Prediction](https://arxiv.org/abs/2606.12987)

- **Abstract / Introduction：** 自动驾驶未来具有歧义，但常用失真指标偏好模糊均值；同时尚不清楚应在哪种 latent 中预测，以及 DiT 相对确定性回归器何时真正有价值。
- **Related Work 定位：** 单帧 CLIP/DINO latent 分别偏语义或静态结构，像素/VAE latent 重外观，传统回归不能表达多峰未来；但盲目上 diffusion 在低方差目标上反而浪费计算。
- **动机 → 方法：** 应先诊断 latent 是否保留驾驶动态、目标是否真的多模态，再选择生成器；论文受控比较编码器，采用 V-JEPA 2 时序 latent，并通过 spatial tokens、x0 目标、residual anchor 和不确定性匹配采样构建 DiT，以 FID/KID 而非单一失真评价分布质量。

#### [Drive-JEPA](https://arxiv.org/abs/2601.22032)

- **Abstract / Introduction：** 通用视频 world-model 预训练给规划带来的收益有限；每个场景只有一条人类轨迹，无法覆盖同样合理的其他行为，造成监督歧义和多模态缺失。
- **Related Work 定位：** 像素视频生成与规划目标错位，普通行为克隆把未记录的安全轨迹当负样本，单一轨迹回归还会平均多个可能行为。
- **动机 → 方法：** 需要面向驾驶的抽象动态表征和更丰富的可行轨迹监督；论文适配 V-JEPA 表征，并用 proposal-centric planner 蒸馏模拟器产生的多轨迹，再通过 momentum-aware selection 在多候选中稳定选择。

#### [PhysisForcing](https://arxiv.org/abs/2606.28128)

- **Abstract / Introduction：** 视频生成器仍会产生轨迹不连续、物体变形和接触关系不合理等物理错误；全局或纯像素损失容易被静态背景主导。
- **Related Work 定位：** 光流/轨迹监督只约束像素移动，不能保证对象关系与接触语义；通用语义对齐又缺少深度和关键运动区域定位。
- **动机 → 方法：** 物理监督应集中于真正发生交互的区域，并同时约束运动与关系；论文用深度感知 motion mask 找到关键区域，在 DiT feature 上加入 pixel trajectory alignment 和 semantic relational alignment，辅助模型训练后可移除，不增加推理成本。

#### [Neural Relational Inference](https://arxiv.org/abs/1802.04687)

- **Abstract / Introduction：** 多实体动力学中的交互图往往不可观测，手工指定边既昂贵又可能错误，纯黑盒序列模型则难复用局部交互规律。
- **Related Work 定位：** 传统图模型假定已知结构，连续潜变量模型可建模不确定性但没有显式对象关系，导致解释与组合泛化受限。
- **动机 → 方法：** 关系结构本身也应由数据推断；论文用 encoder 从轨迹推断离散潜在边类型，再由 graph neural decoder 按这些边预测动力学，从而把“识别谁影响谁”和“预测如何变化”联合学习。

### C. 轨迹规划与 VLA 的测试时扩展

#### [V-GPS / Steering Your Generalists](https://arxiv.org/abs/2410.13816)

- **Abstract / Introduction：** Generalist robot policy 从大规模、多来源数据中获得了任务与语义泛化能力，但混合质量示范会留下抓取不准、释放过早等低层控制缺陷；直接微调或用 offline RL actor 替换它，又可能丢失既有通用能力。
- **Related Work 定位：** 传统 offline RL 的终点通常是从 Q 更新或抽取 $\pi_{\mathrm{RL}}$，但该 actor 受离线数据支持域和策略参数化限制；论文附录中 Cal-QL / IQL actor 在两项 SIMPLER 任务上均为 0 成功率，说明“学到可用 value”不等于“能抽出可泛化 policy”。
- **动机 → 方法：** 将动作生成与价值评价解耦：冻结且可黑盒调用的 VLA 生成 $K$ 个候选，Cal-QL / IQL 学到的 language-conditioned Q-function 只在部署时重排候选。这样保留 generalist policy 的生成能力，并把 offline RL 的价值优化作为 policy-agnostic steering signal；但 Q 自身的 OOD 泛化仍是后续 MG-Select 所质疑的边界。

#### [RoboMonkey](https://arxiv.org/abs/2506.17811)

- **Abstract / Introduction：** VLA 在结构化训练场景中表现良好，但面对遮挡、杂乱和 OOD 物体时容易因一次错误动作直接失败；同时 pass@k 表明冻结策略的分布里往往已经存在正确动作。
- **Related Work 定位：** 继续扩大或微调 policy 成本高且可能破坏通用能力，单次贪心解码没有利用动作分布宽度，通用 VLM 也未被训练成可靠的机器人动作 verifier。
- **动机 → 方法：** 与其改写策略，不如把已有好动作找出来；论文对冻结 VLA 采样并扰动多个候选，用合成失败数据训练 VLM verifier，再以多数 proposal 分布和 verifier 选择最可靠动作。

#### [VeGAS](https://arxiv.org/abs/2605.12620)

- **Abstract / Introduction：** MLLM embodied agent 在 OOD 环境中脆弱，单样本推理无法利用模型潜在的多种动作假设；直接拿现成 MLLM 做判断并不能稳定带来收益。
- **Related Work 定位：** best-of-N 需要可信评分器，策略 likelihood 不是动作正确性，off-the-shelf verifier 又没见过细粒度执行失败，容易给出表面合理评价。
- **动机 → 方法：** verifier 必须专门学习“失败长什么样”；论文采样多候选，并通过合成多类型失败构造 curriculum，单独训练 generative verifier，在部署时只增加并行采样和选择。

#### [SVA](https://arxiv.org/abs/2607.03751)

- **Abstract / Introduction：** 继续 post-training 可能让通用 VLA 能力变窄，而 pass@k 显示冻结模型常能采到成功动作，只是不会识别哪个更好。
- **Related Work 定位：** 在线 MCTS 质量高但实时成本太大，普通 value model 缺少冻结策略分布上的探索数据，单纯多采样又无法评估长期回报。
- **动机 → 方法：** 将昂贵搜索只留在离线训练，再把判断能力带到线上；论文在模拟器中对冻结 VLA 做 MCTS 和 rollout，蒸馏 empirical return 到轻量 Q evaluator，部署时多采样并用不确定性正则化 Q 值选择，不再运行搜索树。

#### [MG-Select](https://arxiv.org/abs/2510.05681)

- **Abstract / Introduction：** 单次 VLA 推理精度不足，但额外 verifier 需要训练且可能无法泛化；直接按模型 likelihood 选候选也会因策略分布过度集中而失效。
- **Related Work 定位：** 外部评分增加系统复杂度，自一致性只找高频模式，原始条件概率还混合了动作先验与视觉/语言条件真正提供的信息。
- **动机 → 方法：** 好候选应对当前条件高度敏感，而非仅在无条件下常见；论文构造 condition-masked reference distribution，用候选相对参考分布的 KL divergence 作为内部置信度，并可通过 joint dropout 改善参考分布。

#### [DiffusionDrive](https://arxiv.org/abs/2411.15139)

- **Abstract / Introduction：** 标准扩散规划需要大量去噪步，难满足实时驾驶；同时交通行为本身多模态，单一回归轨迹会平均不同驾驶意图。
- **Related Work 定位：** 传统 anchor planner 覆盖有限，vanilla diffusion 灵活但慢，直接缩短步数又会损失轨迹质量。
- **动机 → 方法：** 用驾驶先验缩小生成搜索空间后，扩散就无需从纯噪声长链生成；论文以多模态轨迹 anchors 为先验，配合 truncated diffusion schedule 和 cascade decoder，用约两步去噪得到多样轨迹。这里是生成式 refinement，不等于有错误反馈的 self-correction。

#### [DiffRefiner](https://arxiv.org/abs/2511.17150)

- **Abstract / Introduction：** 单次回归会平均多模态行为，固定候选分类的计算随 anchor 数量增加；扩散方法虽然能表达复杂运动分布，但从随机噪声或固定 anchors 初始化时缺少场景适应性，可能需要更多去噪迭代。
- **Related Work 定位：** Discriminative planner 受 anchor coverage 限制，DiffusionDrive 等 generative planner 的初始化仍不够 scene-aware；已有感知—规划联合方法多为隐式特征交互，无法细粒度约束轨迹遵守道路与避障语义。
- **动机 → 方法：** 将判别式方法的强初值和生成式方法的分布细化结合：Proposal Decoder 先从传感器特征调整 anchors 得到 scene-adaptive coarse proposals，Diffusion Refiner 再条件去噪；FGSIM 通过全局 cross-attention、局部 deformable attention 和 adaptive gate 显式对齐轨迹与 BEV 语义。

#### [DriveVer](https://arxiv.org/abs/2607.00399)

- **Abstract / Introduction：** 扩大训练数据和模型规模成本高且收益递减，一次性 planner 输出后没有验证与修正环节，危险小偏差会直接执行。
- **Related Work 定位：** 普通 trajectory scorer 只能排序，rule-based 后处理不理解场景语义，通用大模型 verifier 太重且通常不能输出精确几何修正。
- **动机 → 方法：** 轻量 verifier 应同时回答“是否安全”和“该往哪里改”；论文训练约 34M 的双头模型，输出 safety score 与 absolute refinement vector，并用 condition-balanced NAVSIM 数据覆盖典型错误，作为任意底层 planner 的测试时后处理。

#### [CriticVLA](https://arxiv.org/abs/2604.27366)

- **Abstract / Introduction：** 现有 VLA 通常只被当作 actor，将多模态输入直接映射为动作，很少进一步利用同一模型的视觉—语言理解能力评价并修正自身轨迹。
- **Related Work 定位：** 多候选选择不能创造新轨迹，通用语言 critique 难落到连续控制，反复优化又不满足实时性。
- **动机 → 方法：** CriticVLA 让同一 VLA 先作为 actor 生成 rough trajectory，再作为多模态评价者联合场景、指令和粗动作生成结构化风险判断与动作建议，并通过 refinement 分支执行一次连续轨迹修正；论文用约 1290 万条合成标注轨迹训练这一评价与修正能力。
- **术语边界：** critic 在这里主要表示“判断并指导修正”的功能角色。论文虽以抽象的 `Q(V,L,A)` 分析动作质量与改进幅度，但实现中没有把 critic 设计成单独输出 `V(s)` 或 `Q(s,a)` 的传统价值网络。

#### [SC-VLA](https://arxiv.org/abs/2602.21633)

- **Abstract / Introduction：** 标准 VLA 主要拟合数据统计先验，对物理动态的理解有限；现有 RL-VLA 依赖外部奖励，奖励设计成本高且难以提供细粒度物理引导；完整 world-model rollout 又过于昂贵。
- **Related Work 定位：** WAM 将动作生成与未来演化纳入统一模型，可由 future prediction 提供 intrinsic guidance，但现有方法多以隐式上下文约束动作，缺少把预测未来显式转化为自我修正信号的机制。
- **动机 → 方法：** SC-VLA 预测 task progress 和 future trajectory trend，将模型自身的 future imagination 重塑为 progress-dependent endogenous dense reward，再用该奖励驱动 residual RL 在线修正动作。

#### [TOAD](https://arxiv.org/abs/2606.07170)

- **Abstract / Introduction：** 学到的 trajectory scorer 通常只给固定 proposal 排序，若候选集根本没覆盖好轨迹，评分再准也无法突破 proposal 上限。
- **Related Work 定位：** one-shot planner 搜索宽度固定，best-of-N 不更新候选分布，传统在线优化又缺少可学习的场景目标或良好初值。
- **动机 → 方法：** scorer 应从“排序器”升级为测试时 objective；论文以原 planner proposals warm start CEM，反复采样、评分、保留精英并更新分布，让新增计算真正探索原候选集之外的轨迹。

#### [DTPP](https://arxiv.org/abs/2310.05885)

- **Abstract / Introduction：** 运动预测和规划代价对安全决策都重要，但传统系统分别训练；单阶段轨迹规划也难表达“当前动作改变环境响应、进而影响后续动作”的条件关系。
- **Related Work 定位：** prediction-first pipeline 不以 ego action 为条件，手工 cost 泛化有限，已有学习式 planner 往往只生成一段固定轨迹而不维护未来分支。
- **动机 → 方法：** 规划应沿物理时间展开，并在每个自车分支下重新预测环境响应；论文构造 trajectory tree，以 query-centric ego-conditioned prediction 更新各分支，用 context-aware learned cost 评价和剪枝，并对预测与规划端到端可微联合训练。

> [!warning] 关于原笔记中的 “DTPP RaMP”
> 未检索到与该名称和描述相符的公开论文；现有段落更像你基于 DTPP 加 diffusion/RaMP 的方案草案。因此保留为研究构想，不能把“前作问题”和“实验结论”写成论文作者已经证明的事实。

#### [Monte Carlo Tree Diffusion (MCTD)](https://arxiv.org/abs/2502.07202)

- **Abstract / Introduction：** 标准 diffusion planner 在测试时通常只增加去噪步数或样本数，计算虽增加，却不能主动把预算集中到更有希望的中间方案。
- **Related Work 定位：** 单链 diffusion 没有分支探索，best-of-N 只在完整样本后选择，传统树搜索又缺少适合连续高维生成状态的节点表示。
- **动机 → 方法：** 部分去噪状态天然可以作为可继续展开的搜索节点；论文把 denoising process 组织成树，在中间状态上分支、评价、剪枝和继续精化，以探索—利用策略动态分配测试时计算。

#### [Inner Monologue](https://arxiv.org/abs/2207.05608)

- **Abstract / Introduction：** 具身 LLM 不仅要知道“做什么”，还要根据执行是否成功、场景是否变化决定“何时改计划”；开环语言计划无法获知真实动作后果。
- **Related Work 定位：** 静态 prompt 只利用初始观测，传统 closed-loop policy 缺少高层语言推理，纯语言 self-reflection 又没有环境证据，可能反复合理化同一错误。
- **动机 → 方法：** 把真实反馈持续写回语言上下文，就能在不重新训练 LLM 的情况下闭环重规划；论文将成功检测、场景描述和人类反馈反复注入 prompt，执行一步后依据新观测更新后续计划。

#### [Beyond Success / JITI](https://arxiv.org/abs/2511.22555)

- **Abstract / Introduction：** Mixed-quality demonstrations 让 VLA 即使完成任务，也可能出现不稳抓取、碰撞或错误释放；每一步固定做多候选选择又浪费计算。
- **Related Work 定位：** 普通 value steering 主要优化成功回报，固定 Best-of-$N$ 没有识别真正影响整段执行质量的少数关键决策。
- **动机 → 方法：** 用明确的 elegance constraints 和离线 Cal-QL 训练 Elegance Critic；部署时以 Q-value fluctuation 触发 JITI，只在 decision-critical moments 采样多个 action chunks 并选最高 Q 候选。

#### [VERITAS](https://arxiv.org/abs/2606.18247)

- **Abstract / Introduction：** Generalist policy 能采到多样可行动作，却缺少部署时验证和从自身真实经验持续改进的机制。
- **Related Work 定位：** 人类干预式重标注难扩展，已有 inference-time verification 多被当作一次性性能增益。
- **动机 → 方法：** 冻结策略每步生成多个短 action chunks，gradient-free visual verifier 选择最优项；执行得到的 verified rollouts 还可在部署后离线微调回 base policy。

#### [DreamTrajectory](https://arxiv.org/abs/2608.01381)

- **Abstract / Introduction：** 移动操作的底盘—机械臂联合动作空间大，直接生成 whole-body chunk 缺少显式任务空间意图，开环执行也无法检查实际运动是否偏离计划。
- **Related Work 定位：** 像素级 world rollout 成本高，普通 VLA 又没有以 action-conditioned motion 检验“候选能否实现意图轨迹”。
- **动机 → 方法：** Action expert 联合预测 intention trajectory 与 action chunk；测试时对多个 chunk 用轻量 trajectory world model 预测其诱导轨迹，并选择与 intention 最一致的候选。

#### [Bidirectional Decoding (BID)](https://arxiv.org/abs/2408.17355)

- **Abstract / Introduction：** 长 chunk 一致但反应慢，逐步重采样反应快却会在多模态策略间跳变。
- **Related Work 定位：** Temporal ensembling 可能平均不相容动作，外部 value/verifier 又需要额外训练。
- **动机 → 方法：** 每步批量采样 chunks，以 backward coherence 对齐上一决策，并用 forward contrast 偏向 strong policy、远离 weak checkpoint；候选数增加时论文观察到明确 width-scaling 收益。

#### [RACE](https://openreview.net/forum?id=INsLvSCJ4z)

- **Abstract / Introduction：** 盲目提高 action execution frequency 会违反物理可达性，异步推理还使新 chunk 基于过期状态。
- **Related Work 定位：** 固定频率 action chunking 和简单丢弃旧动作不能保证从机器人当前状态平滑接入新计划。
- **动机 → 方法：** 预测 reached states，使用 reachability-aware time-optimal retiming，并在测试时批量搜索与当前状态最平滑、最可控的未来 chunk；这是训练改造、局部优化和 Width Selection 的组合。

#### [SGAC](https://arxiv.org/abs/2510.12392)

- **Abstract / Introduction：** Diffusion Policy 有随机低保真样本；开环 chunk 反应慢，完全闭环重采样又抖动。
- **Related Work 定位：** BID 的多候选搜索成本较高，普通 guidance 没有直接利用上一时刻条件作为反例。
- **动机 → 方法：** 将过去条件下的 score prediction 作为 negative guidance 改变当前去噪，并按新旧 action chunk 相似性决定是否更新执行队列，组合 generative depth 与 closed-loop gating。

#### [Reflective Planning / ReflectVLM](https://arxiv.org/abs/2502.16707)

- **Abstract / Introduction：** VLM 有语义知识但缺少精细物理推理，长时装配中的局部错误会持续累积。
- **Related Work 定位：** 纯语言反思没有动作后果证据，传统符号规划依赖已知状态模型。
- **动机 → 方法：** VLM 与 diffusion dynamics model 交替产生 $H$ 步 imagined plan/future，最后把未来图像与方案送回 VLM 做一次 reflection 并输出修订动作；属于 world-feedback refinement，而不是固定候选选择。

#### [SafeBimanual](https://arxiv.org/abs/2508.18268)

- **Abstract / Introduction：** 预训练双臂 diffusion policy 缺少碰撞、撕扯和行为错位等显式安全约束。
- **Related Work 定位：** 固定安全 cost 无法覆盖任务阶段变化，训练期数据增强也难穷尽危险交互。
- **动机 → 方法：** VLM 按阶段选择可微 keypoint safety costs，并把 cost gradient 直接注入 clean-action estimate 的 diffusion denoising；这是动作生成链上的梯度 guidance，不是 TOAD 式 CEM 参数分布更新。

#### [E-TTS](https://arxiv.org/abs/2606.27268)

- **Abstract / Introduction：** 早期 embodied TTS 多只扩 action samples，缺少 reasoning scaling 与长时历史利用。
- **Related Work 定位：** 固定 Best-of-$N$ 的评分不会影响后续生成，纯 self-reflection 又缺少 grounded verifier。
- **动机 → 方法：** 联合采样 reasoning–action pairs，用 history-aware 双 verifier 评分并生成反馈，再把反馈和历史条件化下一轮采样，形成 $N>1,R>1,C=1$ 的反馈耦合候选再生成。

#### [VLA-Reasoner](https://arxiv.org/abs/2509.22643)

- **Abstract / Introduction：** VLA 的短视动作预测无法显式检查长时后果，局部偏差会在任务中累积。
- **Related Work 定位：** 完整枚举动作序列昂贵，稀疏终局奖励也难稳定评价搜索树中间节点。
- **动机 → 方法：** 以 VLA proposal 和 KDE action prior 展开 MCTS，以 world model rollout 预测未来、offline-shaped reward 评价中间状态并回传价值，属于真正在线 branching search。

#### [Test-Time Graph Search (TTGS)](https://arxiv.org/abs/2510.07257)

- **Abstract / Introduction：** Offline GCRL value 在短距离上可靠，直接面对远目标却会累积 value 和控制误差。
- **Related Work 定位：** 专门 hierarchical training 增加监督，硬截断远边又容易让状态图断裂。
- **动机 → 方法：** 从离线数据构图，用冻结 value 导出的局部距离与超线性长边惩罚做 Dijkstra 最短路，执行时沿路径选择当前可达的最远 subgoal；无额外训练，但范围是 GCRL 而非 VLA。

#### [SAIL](https://arxiv.org/abs/2506.11948)

- **Abstract / Introduction：** Imitation policy 继承慢速示范，简单提频会放大 tracking error、改变控制动力学并造成 OOD 观测。
- **Related Work 定位：** 固定 chunk 和事后平滑没有联合考虑控制器误差、精细任务阶段与真实系统延迟。
- **动机 → 方法：** 以 reached pose 训练控制器无关目标，部署时依据 tracking error 开关 diffusion guidance，并按动作复杂度调速、调度异步 action queue；它属于 inference-time execution scaling，不是随 $N/R$ 单调增长的 compute scaling。

#### [Adaptive Action Chunking (AAC)](https://arxiv.org/abs/2604.04161)

- **Abstract / Introduction：** 固定长 chunk 反应慢，固定短 chunk 易抖动且计算频繁，不同任务阶段没有统一最优执行长度。
- **Related Work 定位：** BID 选择完整候选，SGAC 判断是否替换队列，但仍预设固定 chunk length。
- **动机 → 方法：** 批量采样 action chunks 估计每个未来位置的连续/离散 action entropy，以平均熵曲线的最大差分点选 $h^*$；它决定“执行多远再重规划”，而不是“选哪条轨迹”。

#### [General Policy Composition (GPC)](https://arxiv.org/abs/2510.01068)

- **Abstract / Introduction：** 多个 VA/VLA 或 diffusion/flow policies 具有互补能力，但重新采集数据训练统一策略代价高。
- **Related Work 定位：** 最终动作平均容易落到低密度区，普通 ensemble 没有组合生成分布的 score field。
- **动机 → 方法：** 冻结 parent policies，在每个生成步凸组合它们的 scores，并在外层按任务 rollout success rate 搜索组合权重；它是 `Weight Width × Denoising Depth`，不是依据每个候选的 elite 反复更新 Gaussian 的 CEM。

## Diffusion 采样与引导的独立分类体系

> [!important] 与前文 taxonomy 相互独立
> 本节只描述 diffusion / flow sampler 在一次生成过程中怎样维护和更新去噪状态，不复用前文规划 taxonomy 的 1—5 类、$N/R/C$ 或 2.x 标签，也不改变 SafeBimanual、MCTD 等方法在前文中的归属。这里的 D0—D4 是为了比较 diffusion sampling mechanism 建立的操作性分类，不是这些论文共同采用的标准类别名。

### 1. 分类对象与统一表达

设同一生成模型在噪声时刻 $t$ 维护 $K$ 个状态：

$$
\mathcal X_t=\{x_t^1,\ldots,x_t^K\}.
$$

$x_t^i$ 称为第 $i$ 个 **particle**，表示同一个 diffusion / flow model 下的一条中间生成轨迹；它不是第 $i$ 个 diffusion 网络，也不表示系统训练了 $K$ 个不同分布。单个 particle 的基础 reverse transition 写为：

$$
x_{t-1}^i\sim p_\theta(\,\cdot\mid x_t^i,c).
$$

在这个基础转移外，测试时 sampler 可能再加入三种算子：

1. **Pathwise guidance $u_t^i$：** 根据当前 particle 自身的条件、代价或梯度修改它的下一步转移。
2. **Continuous coupling $\Psi_t(\mathcal X_t)$：** 让一个 particle 的 drift / score 连续依赖其他 particles 或动态 reference set。
3. **Population reproduction $\mathcal R_t$：** 根据权重重新抽取 parent index，使部分 particles 被复制、部分被淘汰。

| 类别 | diffusion step 中被改变的对象 | particle 之间的关系 | 代表方法 | 核心判别 |
| --- | --- | --- | --- | --- |
| **D0 Independent Reverse Sampling** | 只有基础 reverse transition | 单条链，或多条彼此独立的链 | 普通 DDPM / DDIM 多次采样、terminal Best-of-$N$ 的生成阶段 | 每个 $x_{t-1}^i$ 只依赖自己的 $x_t^i$ 与公共条件 |
| **D1 Pathwise Guided Denoising** | 单个 particle 的 mean、drift、score、条件输入或局部 update | 多条链即使并行运行，也不交换当前状态 | classifier / CFG、Interval Guidance、CADS、SafeBimanual、static SPELL | 路径自身的引导信号或调度直接改变本粒子的 transition，不改变 ancestry |
| **D2 Continuously Coupled Particle Diffusion** | 整个 particle set 的 joint drift / joint score | 当前粒子通过势函数、核或 reference 动态相互作用 | Particle Guidance、动态 SPELL、EDDY | $x_{t-1}^i$ 的更新连续依赖 $x_t^j$ 或其预测终局，$j\neq i$ |
| **D3 Feynman–Kac / SMC Resampling** | parent index、offspring count 与 population composition | 通过权重发生离散复制和淘汰 | FK Steering | 中间 potential 被归一化成概率，并据此有放回抽取 parents |
| **D4 Hybrid Interaction-and-Resampling** | joint drift 与 population composition 同时改变 | 连续信息共享和离散繁殖同时存在 | IMPFM | 同一 sampler 同时包含 D2 型 interaction 与 D3 型 reweighting / resampling |

上述类别描述的是 **sampler 的近端更新机制**。同一应用系统仍可在 sampler 外叠加终局选择、真实环境重规划或树搜索，但这些外层操作不反过来改变 D0—D4 的定义。

### 2. 术语表：reward、potential 和 weight 不是一回事

| 术语                    | 记号                                                | 含义                                                                    |
| --------------------- | ------------------------------------------------- | --------------------------------------------------------------------- |
| Denoising state       | $x_t^i$                                           | 第 $i$ 条生成轨迹在噪声时刻 $t$ 的当前状态                                            |
| Particle              | 第 $i$ 条 $x_T^i\rightarrow\cdots\rightarrow x_0^i$ | 一条会持续演化的生成轨迹；多个 particles 通常共享同一模型参数 $\theta$                         |
| Intermediate reward   | $r_\phi(x_t^i,c)$                                 | 对当前状态最终可能产生何种结果的评价；可来自网络、解析函数或其他估计器                                   |
| Feynman–Kac potential | $G_t^i>0$                                         | 对路径这一阶段施加的正权重，用来表达该 particle 在当前阶段应被上调或下调多少                           |
| Normalized weight     | $w_t^i$                                           | 将 potential 或完整 importance weight 归一化后得到的 parent sampling probability |
| Resampling            | $a_t^i\sim\operatorname{Cat}(w_t)$                | 有放回抽 parent index；高权重 parent 可产生多个后代，低权重 parent 可能没有后代                |

`reward` 回答“当前结果看起来有多好”，`potential` 回答“这段路径应被乘上多大的增量权重”，`normalized weight` 才是实际用于抽 parent 的概率。$G_t$ 本身不必是一个网络；即使 $r_\phi$ 由 learned reward model 产生，$G_t$ 也只是对该评价进行正值变换并满足路径重加权约束后的量。

### D0. Independent Reverse Sampling

本类只执行模型原生 reverse transition。链可以随机，也可以在给定初始噪声后近似确定；可以只跑一条，也可以从 $K$ 份噪声并行跑 $K$ 条。只要第 $i$ 条链在中间时刻不读取其他链的状态、分数或统计量，就仍是 independent sampling：

$$
x_T^i\rightarrow x_{T-1}^i\rightarrow\cdots\rightarrow x_0^i,
\qquad
x_{t-1}^i\sim p_\theta(\cdot\mid x_t^i,c).
$$

#### D0.1 DDPM：随机 Markov reverse chain

- **[Denoising Diffusion Probabilistic Models](https://arxiv.org/abs/2006.11239)** 2020-06-19｜NeurIPS 2020
  - **前序工作问题**
    - **Abstract / Introduction：** 生成模型长期在 likelihood、sample quality 与稳定训练之间权衡。GAN 在图像质量上强，但训练容易不稳定并可能牺牲覆盖；早期 diffusion / score-based 方法具备稳健的概率建模基础，却尚未证明可以达到同等级别的高质量图像合成。
    - **方法背景：** 既有 diffusion probabilistic model 的正向扩散与反向生成具有清楚的概率结构，但训练目标、denoising score matching 和 Langevin dynamics 之间的关系需要进一步统一，才能得到可扩展的实现。
  - **动机与方法**
    - 正向过程按固定噪声日程逐步破坏数据：

      $$
      q(x_t\mid x_{t-1})
      =\mathcal N\!\left(\sqrt{1-\beta_t}\,x_{t-1},\beta_tI\right).
      $$

    - 训练网络学习反向条件分布：

      $$
      p_\theta(x_{t-1}\mid x_t)
      =\mathcal N\!\left(\mu_\theta(x_t,t),\Sigma_\theta(x_t,t)\right),
      $$

      部署时从 $x_T\sim\mathcal N(0,I)$ 出发，逐步抽样到 $x_0$。论文用加权 variational bound 训练，并给出与 denoising score matching 的联系。
  - **核心创新点（一句话）**
    - 把高质量图像生成落实为一条可训练的随机反向 Markov 链，使“逐步去噪”成为后续各种 diffusion guidance 与 inference-time sampler 的基础状态机。
  - **论文图解**

    ![[assets/trajectory-planning-vla-tts/ddpm-fig2-graphical-model.png]]

    - **读图：** 原论文 Figure 2 从左向右展示 $x_T\rightarrow\cdots\rightarrow x_0$ 的生成链；虚线 $q(x_t\mid x_{t-1})$ 是训练时可用的正向扩散关系，实线 $p_\theta(x_{t-1}\mid x_t)$ 是部署时反复调用的 learned reverse transition。
    - **分类含义：** 图中始终只有当前路径的相邻时刻转移，没有外部梯度、其他 particle、parent selection 或 offspring replication，因此是 D0 的标准原型。
  - **状态与更新：** 持久状态是单个 noisy sample $x_t$；随机噪声只决定本链下一状态，不构成跨候选搜索。
  - **训练—部署关系：** 训练更新参数 $\theta$；部署阶段固定 $\theta$，只更新 $x_t$。
  - **边界与局限：** DDPM 的去噪步数可以很多，但“链很深”不等于 D2/D3；只有加入跨粒子 interaction 或 ancestry resampling，类别才会改变。

#### D0.2 DDIM：改变采样路径，不增加 population control

- **[Denoising Diffusion Implicit Models](https://arxiv.org/abs/2010.02502)** 2020-10-06｜ICLR 2021
  - **前序工作问题**
    - **Abstract / Introduction：** DDPM 生成质量高，但需要模拟很多步 Markov chain，单个样本的 wall-clock sampling 较慢。
    - **关键缺口：** 既有训练目标似乎把模型绑定到特定的随机 Markov reverse process；如果每次减少步数都要重新训练模型，采样加速的代价很高。
  - **动机与方法**
    - DDIM 构造与 DDPM 具有相同训练目标的 non-Markovian forward family，并令 reverse update 在选定的 timestep 子序列上运行。常见写法为：

      $$
      x_{t-1}
      =\sqrt{\bar\alpha_{t-1}}\,\hat x_0(x_t)
      +\sqrt{1-\bar\alpha_{t-1}-\sigma_t^2}\,\epsilon_\theta(x_t,t)
      +\sigma_t\epsilon.
      $$

    - 当 $\sigma_t=0$ 时，给定 $x_T$ 与条件后，反向路径成为确定性映射；选择更稀疏的 timestep 子序列可以减少模型调用。论文报告在其实验设置中达到约 $10\times$—$50\times$ wall-clock 加速。
  - **核心创新点（一句话）**
    - 在不改变 DDPM 训练目标的情况下，重构可随机也可确定的快速 reverse sampler，把“训练出的 denoiser”和“部署时采用哪条采样路径”解耦。
  - **论文图解**

    ![[assets/trajectory-planning-vla-tts/ddim-fig1-graphical-models.png]]

    - **读图：** 原论文 Figure 1 对比 DDPM 的 Markov 图模型与 DDIM 的 non-Markovian 构造；二者最终都从噪声状态顺序得到 $x_0$，区别在 transition family，而不是多维护了一组会互相竞争的候选。
    - **分类含义：** DDIM 改的是基础 transition 的随机性和时间离散化。没有额外 guidance term 时，它仍是 D0，不会因为“确定性生成”或“跳步”自动升级成 D1。
  - **状态与更新：** 单链只维护一个 $x_t$；多次从不同 $x_T^i$ 运行时是 $K$ 条独立 D0 chains。
  - **边界与局限：** 完全确定性的 DDIM / ODE sampler 会影响 D3 中“复制后能否重新分叉”：两个完全相同的 parent 若后续条件与算子也相同，将保持相同。这个性质影响混合使用方式，但不改变 DDIM 自身的 D0 归类。

#### D0.3 独立多样本与 terminal Best-of-$N$

从 $K$ 份初始噪声独立运行 DDPM、DDIM 或其他 base sampler，是同一模型的 independent ensemble，不是“$K$ 个 diffusion 模型”。[Particle Guidance](https://openreview.net/forum?id=KqbCvIFBY7) 正是以 I.I.D. 多次采样为基线，再讨论 joint potential 如何打破粒子独立性。

如果所有样本完成后才执行

$$
\hat x_0=\arg\max_i r(x_0^i),
$$

生成阶段仍属于 D0，`argmax` 只是 **terminal Best-of-$N$ overlay**。终局选择没有把任何分数反馈到中间去噪 population；只有发生中间引导、跨粒子耦合或重采样时，才进入 D1—D4。

### D1. Pathwise Guided Denoising

D1 在每个 particle 自己的 reverse transition 上添加控制项：

$$
x_{t-1}^i
=
\mu_\theta(x_t^i,t,c)
+u_t(x_t^i,c)
+\sigma_t\epsilon_t^i.
$$

$u_t$ 可以是 classifier score、能量梯度、显式代价梯度、约束投影或其他局部控制。判别重点不是它最终是否改善生成效果——所有有效 guidance 都会影响结果——而是控制量是否直接作用于 **当前去噪状态的 transition**。

#### D1.1 Classifier Guidance：独立分类器梯度改写 reverse mean

- **[Diffusion Models Beat GANs on Image Synthesis](https://papers.nips.cc/paper_files/paper/2021/hash/49ad23d1ec9fa4bd8d77d02681df5cfa-Abstract.html)** 2021｜NeurIPS 2021
  - **前序工作问题**
    - **Introduction：** diffusion model 具有稳定训练和较好 distribution coverage，但在困难的 ImageNet 合成上仍落后于当时的 GAN；GAN 还可以用 truncation 等方式牺牲 diversity 换取 fidelity，而 diffusion 缺少同样直接的采样时旋钮。
    - **方法缺口：** 仅把类别标签作为生成网络条件，未必能在采样时持续把 noisy state 推向分类器认为更符合目标类别的区域。
  - **动机与方法**
    - 额外训练一个能处理各噪声等级的 classifier $p_\phi(y\mid x_t,t)$，在每个 reverse step 对当前 $x_t$ 求类别对数概率梯度。对 Gaussian reverse transition，Algorithm 1 的关键更新为：

      $$
      x_{t-1}
      \sim
      \mathcal N\!\left(
      \mu_\theta(x_t,t)
      +s\Sigma_\theta(x_t,t)\nabla_{x_t}\log p_\phi(y\mid x_t,t),
      \Sigma_\theta(x_t,t)
      \right).
      $$

    - guidance scale $s$ 控制条件梯度强度。$s$ 增大时，采样更集中于 classifier 的高概率区域，通常提高 class fidelity，同时可能降低覆盖和多样性。
  - **核心创新点（一句话）**
    - 把独立 noisy-image classifier 的输入梯度直接注入每一步 reverse diffusion，使条件强度成为部署时可调的 pathwise control。
  - **论文图解**

    ![[assets/trajectory-planning-vla-tts/classifier-guidance-fig2-corgi.png]]

    - **读图：** 原论文 Figure 2 比较 classifier scale $1.0$ 与 $10.0$ 的 corgi 条件样本；较强 guidance 让生成更符合目标类别，也直观展示了论文讨论的 fidelity—diversity 调节。
    - **分类含义：** 每张图仍由自己的 $x_t$ 和同一个 classifier 产生梯度；图中没有当前 batch 内其他样本参与本路径更新，因此是 D1，不是 D2。
  - **训练—部署关系：** diffusion model 与 noisy-state classifier 在部署前训练；推理时固定 $\theta,\phi$，更新的是 $x_t$。
  - **边界与局限：** 需要额外 classifier，并且 classifier 在高噪声状态上的梯度质量会直接影响路径；无论 $s$ 多大，只要没有跨路径交互或重采样，仍不属于 population search。

#### D1.2 Classifier-Free Guidance：条件与无条件 score 的路径内组合

- **[Classifier-Free Diffusion Guidance](https://arxiv.org/abs/2207.12598)** 2022-07-26｜NeurIPS 2021 Workshop 短版；arXiv 扩展版
  - **前序工作问题**
    - **Abstract：** classifier guidance 能在训练后调节 fidelity 与 diversity，但需要单独训练 classifier，并把生成质量交给另一个网络在 noisy inputs 上的梯度。
    - **核心疑问：** 如果生成模型自身已经学到 conditional 与 unconditional distributions，是否可以不用外部 classifier 也构造 guidance direction？
  - **动机与方法**
    - 训练时以一定概率丢弃条件，使同一个 denoiser 同时学到 $\epsilon_\theta(x_t,t,c)$ 与 $\epsilon_\theta(x_t,t,\varnothing)$。采样时采用常见组合约定：

      $$
      \epsilon_{\mathrm{cfg}}
      =\epsilon_\theta(x_t,t,\varnothing)
      +w\Bigl[
      \epsilon_\theta(x_t,t,c)-
      \epsilon_\theta(x_t,t,\varnothing)
      \Bigr].
      $$

    - 方括号中的差值提供“朝向条件分布、远离无条件分布”的更新方向；不同论文对 $w$ 的基准写法略有差异，判别时应看实际 score combination，不只看符号大小。
  - **核心创新点（一句话）**
    - 让同一个 denoiser 同时学习 conditional 与 unconditional 去噪方向，并在采样时放大二者的差值，将生成模型自身学到的“条件增量”用作 guidance，从而无需独立 classifier，仍可调节条件一致性与 fidelity—diversity 权衡。
  - **论文图解**

    ![[assets/trajectory-planning-vla-tts/cfg-fig2-guidance-mixture.png]]

    - **读图：** 原论文 Figure 2 在二维 Gaussian mixture 上逐步提高 guidance weight；样本从覆盖多个 mode 逐渐集中到条件偏好的 mode，显示 guidance strength 对质量—多样性权衡的连续影响。
    - **分类含义：** conditional 与 unconditional prediction 都针对同一个 $x_t$ 计算并合成为本路径的 score；并行样本之间不交换状态，故仍是 D1。
  - **训练—部署关系：** 条件 dropout 属于训练设计，score 线性组合属于部署时 sampler。它不是测试时更新网络参数，也不是从多个完成候选中选择。
  - **边界与局限：** CFG 是 learned pathwise guidance；它和显式 cost gradient 来源不同，但二者都修改单条 transition。较大 guidance weight 可加强条件一致性，也可能损害 diversity 或出现过饱和。

#### D1.3 Interval Guidance：只在中间噪声区间启用 CFG 外推

- **[Applying Guidance in a Limited Interval Improves Sample and Distribution Quality in Diffusion Models](https://arxiv.org/abs/2404.07724)** 2024-04-11｜NeurIPS 2024；[会议论文](https://papers.neurips.cc/paper_files/paper/2024/hash/dd540e1c8d26687d56d296e64d35949f-Abstract-Conference.html)
  - **前序工作问题**
    - **Introduction：** 标准 CFG 通常在整条 sampling chain 上使用同一个 guidance weight，但反向扩散早、中、晚三个阶段承担的生成作用并不相同，用同一强度全程外推并非中性设计。
    - **阶段性问题：** 高噪声阶段的强 guidance 会过早压缩全局构图和 mode coverage，使同一条件下的结果趋向少数“模板”；中等噪声阶段的 guidance 有助于更明确地选择特征并提高感知清晰度；低噪声阶段继续计算 guidance 通常收益很小。
    - **方法缺口：** 降低全程 guidance scale 虽能缓解 mode concentration，却也会削弱真正有用的中段引导；需要把“引导多强”和“在哪些噪声阶段引导”拆成两个采样超参数。
  - **动机与方法**
    - 论文不改变 CFG 的 conditional / unconditional 方向，只把固定权重改成关于噪声水平的分段函数：

      $$
      D_{mathrm{IG}}(x;sigma,c)
      =w(\sigma)D_\theta(x\mid c;\sigma)
      +\bigl(1-w(\sigma)\bigr)D_\theta(x;\sigma),
      $$

      $$
      w(\sigma)=
      \begin{cases}
      w,&\sigma\in(\sigma_{\mathrm{lo}},\sigma_{\mathrm{hi}}],\\
      1,&\text{otherwise}.
      \end{cases}
      $$

    - 反向采样从高噪声走向低噪声：到达 $\sigma_{\mathrm{hi}}$ 时开启 CFG 外推，到达 $\sigma_{\mathrm{lo}}$ 时关闭。区间外的 $w(\sigma)=1$ 表示继续使用 **clean conditional denoiser**，不是转成 unconditional generation，也不是停止去噪。
    - $\sigma_{\mathrm{lo}}=0,\sigma_{\mathrm{hi}}=\infty$ 时退化为传统全程 CFG。论文把两个端点放在 sampler step boundary 上，避免一个数值积分步内部跨越不连续的权重切换。
  - **核心创新点（一句话）**
    - IG 把 CFG 从“全程固定开启”改成“只在中等噪声区间执行条件外推”，保留中段的清晰度收益，同时避开高噪声阶段的 mode collapse 倾向和低噪声阶段的冗余计算。
  - **论文图解**

    ![[assets/trajectory-planning-vla-tts/interval-guidance-fig2-window.png]]

    - **读图：** 原论文 Figure 2 的一维 toy distribution 中，全程 guidance 会在高噪声端把轨迹推离平滑数据分布并丢掉一个 mode；关闭高噪声 guidance 后两个 mode 恢复，再关闭低噪声 guidance 对终局分布影响很小。
    - **分类含义：** 图中每条轨迹仍只根据自身 $x_t$、公共条件和预设区间更新；没有读取其他当前 samples，也没有复制或淘汰 parent，因此属于 D1 pathwise schedule。
  - **实验结果**
    - 在论文的 ImageNet-512 / EDM2-XXL 设置中，limited interval 将 FID 从全程 CFG 的 $1.81$ 降至 $1.40$；对应最佳 FID 的配置只在 32 个采样步中的 6 步使用 guidance。论文还在 EDM2-S、DiT-XL/2 与 Stable Diffusion XL 上报告了相同方向的收益。
    - 计算节省来自区间外不再执行 CFG 所需的额外 unconditional evaluation；具体速度收益取决于 conditional / unconditional branch 是否共享计算以及 sampler 实现。
  - **训练—部署关系：** IG 不重新训练 diffusion model，只在部署时调度已有 CFG 的调用区间和权重。
  - **边界与局限：** IG 是预先调好的确定性时间表，不根据当前样本质量、reward 或搜索树状态在线决定开关，因此不是 MCTD 的 meta-action search，也不是 adaptive controller。最优 $\sigma_{\mathrm{lo}},\sigma_{\mathrm{hi}},w$ 会随模型、数据和评价指标变化。

#### D1.4 CADS：对条件信号做退火式扰动

- **[CADS: Unleashing the Diversity of Diffusion Models through Condition-Annealed Sampling](https://arxiv.org/abs/2310.17347)** 2023-10-26｜ICLR 2024 Spotlight；[OpenReview](https://openreview.net/forum?id=zMoNrajk2X)
  - **前序工作问题**
    - **Abstract / Introduction：** conditional diffusion 虽然具有较好的整体 distribution coverage，但在高 CFG scale 或较小训练集下，同一 condition 对不同初始噪声的生成结果仍可能高度相似。
    - **既有处理问题：** 直接减小 CFG scale 可以恢复一部分多样性，却往往同时降低 perceptual quality 或 condition alignment；单纯缩短采样步数主要解决速度问题，也不直接解除条件信号造成的 mode concentration。
    - **核心疑问：** 能否在不重训模型的情况下，让早期采样保留更大探索空间，同时在后期逐渐恢复完整条件，以兼顾多样性和条件一致性？
  - **动机与方法**
    - CADS 不直接修改样本间关系，而是在每个 reverse step 把送入 conditional denoiser 的干净条件 $y$ 换成退火条件：

      $$
      \hat y_t
      =\sqrt{\gamma(t)}y
      +s\sqrt{1-\gamma(t)}n,
      \qquad n\sim\mathcal N(0,I),
      $$

      其中 $s$ 控制条件噪声尺度，$\gamma(t)$ 使用分段退火日程：

      $$
      \gamma(t)=
      \begin{cases}
      1,&t\le\tau_1,\\
      \dfrac{\tau_2-t}{\tau_2-\tau_1},&\tau_1<t<\tau_2,\\
      0,&t\ge\tau_2.
      \end{cases}
      $$

    - 由于反向扩散从 $t=1$ 走向 $t=0$，早期 $\gamma(t)\approx0$，模型接收到的条件接近噪声，条件约束较弱，允许不同随机路径先探索不同 mode；随后条件噪声单调减小，晚期恢复 $\hat y_t=y$，再把结果拉回目标类别、文本、身份或姿态条件。
    - 论文还提供可选 rescaling：把扰动后条件的均值和标准差拉回原条件统计量，再用 mixture factor $\psi$ 混合原始扰动条件与 rescaled condition，以权衡采样稳定性和多样性。
  - **核心创新点（一句话）**
    - CADS 把“条件在采样早期压得过紧”转化为条件输入的退火问题：先随机弱化 condition 以扩大探索，后逐步去除条件噪声以恢复对齐，而不需要改动或重训预训练 diffusion model。
  - **论文图解**

    ![[assets/trajectory-planning-vla-tts/cads-fig1-diversity.png]]

    - **读图：** 原论文 Figure 1 在相同类别条件和高 guidance scale 下比较标准 DDPM 与 CADS；标准采样常重复相近构图，而 CADS 在汉堡、鸟、油菜花和金毛等类别中产生更明显的姿态、视角与背景变化。
    - **分类含义：** 每个样本只改变自己路径所读取的 $\hat y_t$；其他当前 samples 不参与该条件的计算，ancestry 也不改变，所以 CADS 属于 D1，而不是 Particle Guidance 的 D2 或 FK resampling 的 D3。
  - **实验结果**
    - 论文在 class-conditional ImageNet、pose-to-image、identity-conditioned face synthesis 与 Stable Diffusion text-to-image 上比较标准 sampler 与 CADS，并报告 diversity / recall 提升而 condition alignment 基本保持。按论文当时的 ImageNet 实验，CADS 在已有模型上得到 $256^2$ 的 FID $1.70$ 和 $512^2$ 的 FID $2.31$；这些是特定 backbone、sampler 与评测设置下的结果，不代表所有任务上的固定收益。
    - CADS 可叠加到 DDIM、DPM++、SDE-DPM++、PNDM、UniPC 等 sampler；它改变的是 conditioning path，而不是重新定义某一种 base solver。
  - **训练—部署关系：** CADS 是 training-free inference-time sampler modification。模型参数冻结，部署时变化的是条件噪声、退火阈值与可选 rescaling。
  - **与 IG 的关键区别**
    - **IG 改 guidance coefficient：** condition 始终保持干净，只在预设噪声区间把 conditional 与 unconditional prediction 的差值放大，区间外取消外推。
    - **CADS 改 conditional input：** CFG 可以继续使用较高 scale，但传给 conditional branch 的条件在早期被随机扰动，随后连续恢复；它不是简单的 CFG 开关。
    - CADS 论文同时实验了 Dynamic CFG，但“随时间改变 CFG weight”只是对照机制，不是 CADS 本体。两者都属于单路径时间调度，都没有 reward feedback、跨 particle coupling 或树搜索。
  - **边界与局限：** 条件噪声过小则多样性改善有限，过大或持续过久会损害样本质量和条件一致性；有效超参数依赖 condition representation，直接扰动 class embedding、text embedding、pose image 等不同条件载体的行为并不完全相同。

#### D1.5 SafeBimanual：解析安全代价引导动作 diffusion

- **[SafeBimanual](https://arxiv.org/abs/2508.18268)** 2025-08-25｜CoRL 2025 Poster；[OpenReview](https://openreview.net/forum?id=Q0H9xlNdVm)
  - **前序工作问题**
    - **Abstract / Introduction：** bimanual diffusion policy 能生成协调动作，但复杂双臂操作包含碰撞、戳刺、撕扯、错误握持姿态等阶段相关风险；单一固定安全代价难以覆盖不同任务阶段与交互模式。
    - **既有方法问题：** 重新收集安全演示或重新训练策略成本高；统一的 rule/cost 又可能在某些阶段约束不足、在另一些阶段过度限制动作。
  - **动机与方法**
    - 系统先由视觉基础模型与 6D pose estimator 得到场景 keypoints，再让 VLM 根据当前 manipulation stage 选择 unsafe interaction mode、相关 keypoints 和 safety cost，形成 $\mathcal C_{\mathrm{sched}}$。
    - 测试时在预训练 diffusion policy 的 denoising mean 外直接注入 action-space cost gradient：

      $$
      A_t^{k-1}
      =
      \mu(A_t^k,O_t,k)
      -\rho_k\nabla_{A_k}\mathcal C_{\mathrm{sched}}(A_{0\mid k},\mathcal P,s_t)
      +\sigma_k\varepsilon.
      $$

    - 论文将 guidance 集中在最后若干去噪步，以利用更可靠的 clean-action estimate，并通过 scheduler 避免所有 safety costs 始终同时激活。
  - **核心创新点（一句话）**
    - 把 VLM 选择的阶段化安全约束转成可微 action cost，并在部署时直接修正预训练 bimanual diffusion policy 的去噪路径。
  - **论文图解**

    ![[assets/trajectory-planning-vla-tts/safebimanual-fig2-pipeline.png]]

    - **读图：** 原论文 Figure 2 左侧生成 keypoints / poses，中部 VLM 根据 manipulation stage 调度安全代价，右侧把 optimal cost scheduling 接到 frozen diffusion model，底部列出 collision、alignment、poking、tearing 等具体 cost。
    - **分类含义：** 图中的红紫路径最终形成当前 action trajectory 的 gradient correction；没有“评完多个 particles 后复制高分 parent”的环节，所以它是 D1 pathwise guidance。
  - **更新分解：** $\mu(\cdot)$ 是 learned denoising，$-\rho_k\nabla\mathcal C$ 是 objective / constraint guidance；二者共同决定下一 action state，但只更新 $A_k$，不在线更新参数 $\theta$。
  - **与 D2/D3 的边界：** 同时启动多条 SafeBimanual chains，只要各链只计算自己的 cost gradient，仍是多条独立 D1 trajectories。只有 cost 或 drift 显式读取其他当前 particles 才进入 D2；只有权重改变 offspring count 才进入 D3。
  - **局限：** 安全效果依赖感知 keypoints、阶段判断、cost 设计及 clean-action estimate；“梯度最终影响 diffusion 结果”不能据此把它写成 verifier resampling 或模型再训练。

### D1/D2 边界方法：Sparse Reference Shielding（SPELL）

SPELL 不能仅凭方法名固定归入 D1 或 D2，因为决定类别的是 reference set 的运行时来源：只读取静态或历史 reference 时是 D1；读取当前 batch 其他 trajectories 的预测终局时是 D2。下面先统一说明它们共享的 shielding 机制，再按配置判定类别。

- **[Shielded Diffusion: Generating Novel and Diverse Images using Sparse Repellency](https://proceedings.mlr.press/v267/kirchhof25a.html)** 2024-10-08｜ICML 2025；[arXiv](https://arxiv.org/abs/2410.06025)
  - **前序工作问题**
    - **Abstract / Introduction：** text-to-image diffusion 在同一 prompt 下反复采样时，常会生成少量典型构图的轻微变体；模型还可能生成与训练图像或其他受保护图像过度接近的结果。前者浪费有限的生成 batch，后者带来 memorization 与近复制风险。
    - **既有处理问题：** 完整生成后再做 nearest-neighbor 检查并丢弃近重复样本，会浪费整条 reverse trajectory 的计算；持续增大通用排斥力虽然可能提高 diversity，也会不断扰动基础 diffusion dynamics，损害 precision 或 fidelity。
    - **既有 diversity 方法的不足：** 已有 training-free 方法会通过限制 CFG 生效区间、扰动条件或耦合当前 batch 的生成轨迹来改善 diversity–quality trade-off，但这些机制并不直接提供一个面向任意 reference set 的统一 shielding 接口。SPELL 关注的是另一种控制需求：无论 reference 来自保护数据、历史输出还是当前 batch，都用同一个最小距离规则判断是否需要干预。
    - **核心疑问：** 能否在不重训 diffusion model 的情况下，提前预测当前轨迹的最终落点，并且只在该落点即将进入某个 reference 的保护区时施加最小修正？
  - **动机与方法**
    - SPELL 将 reference $z_k$ 解释为不希望最终输出过度靠近的 **保护样本或已占用位置**，而不是应当模仿的正确答案。reference 可以来自固定保护数据集、以前生成的 batches，也可以来自当前 batch 其他轨迹的预测终局。
    - 对每个 reference 定义半径为 $r$ 的 shield，并把所有 shields 的并集记为禁入集合：

      $$
      B_k=\{x:\|x-z_k\|\le r\},
      \qquad
      S=\bigcup_k B_k.
      $$

      sampler 的目标是在尽量保留原数据分布 $p_0$ 的同时，生成满足 $X_0\notin S$ 的样本。
    - 在每个 reverse step，denoiser 根据当前 noisy state 预测最终 clean output：

      $$
      \hat x_0=D_\theta(t,x_t)\approx\mathbb E[X_0\mid x_t].
      $$

      因此，SPELL 检查的不是仍含大量噪声的 $x_t$ 是否相近，而是轨迹照当前方向继续去噪后预计会落到哪里。
    - 若 $\hat x_0$ 落入 $z_k$ 的 shield，则沿二者连线计算把预测终局移到半径边界所需的 correction：

      $$
      \delta_k(\hat x_0)
      =\frac{r(\hat x_0-z_k)}{\|\hat x_0-z_k\|}-(\hat x_0-z_k),
      \qquad
      \Delta(\hat x_0)=\sum_k\mathbf 1_{B_k}(\hat x_0)\delta_k(\hat x_0).
      $$

      对单个且不重叠的 shield，修正后满足 $\|\hat x_0+\delta_k-z_k\|=r$。它只完成“离 reference 至少为 $r$”所需的局部修正，而不把输出无限推远；若没有命中任何 shield，则 $\Delta=0$，基础 sampler 不受影响。该 clean-output correction 随后被转换为当前 reverse transition 的 repellency term。
    - 这种边界修正体现了 SPELL 的设计取舍：目标是满足最低距离约束，而不是最大化样本之间的距离。干预一旦不再必要便归零，从而给基础 diffusion 留出恢复图像质量的后续去噪步骤。
  - **实验结果与基线**
    - **基础模型对照：** Section 5.2 先在多个 text-to-image 与 class-to-image diffusion models 上比较同一基础模型加入 SPELL 前后的结果，并使用相同随机种子控制差异。论文报告 SPELL 普遍提高 recall 与 Vendi Score，而 precision / density 的变化通常较小，说明稀疏干预能够提高多样性，但仍存在随 shield radius 增大的 diversity–quality trade-off。
    - **Training-free baselines：** Section 5.3 并列比较三类近期方法：Interval Guidance 只在部分反向扩散区间启用 CFG；CADS 对条件注入随时间退火的噪声；Particle Guidance 用 joint potential 增加当前 batch 内的粒子排斥。PG 在这里仅是三个实验基线之一，不是 SPELL 全文的唯一比较对象。
    - **Trade-off comparison：** 作者重新实现并调参上述 baselines。Figure 4 在 Latent Diffusion / CC12M 设置下比较 recall–precision、coverage–density、Vendi–CLIP 和 CLIP–$FD_{\mathrm{DINOv2}}$ Pareto fronts，并报告 SPELL 在该设置下获得更有利的综合权衡；Appendix Table 4 给出各方法不同超参数下的完整指标。这个结果受模型、数据集、baseline 实现与评价指标限制，不能写成 SPELL 在所有场景中都优于其他方法。
    - **效率与规模：** Appendix Table 5 显示 SPELL、Particle Guidance、Interval Guidance 与 CADS 相对于 diffusion backbone 的额外运行时间都较小；Section 5.6 进一步把 ImageNet-1k 的 1.2M 张训练图像作为保护集合，展示了静态 reference shielding 的大规模应用。
  - **核心创新点（一句话）**
    - SPELL 把“最终输出不得进入 reference 的保护半径”写成 predicted clean output 上的局部约束，并以按需归零的稀疏 correction 提前修正 reverse trajectory，从而统一支持静态图像保护、跨 batch novelty 与当前 batch diversity。
  - **论文图解**

    ![[assets/trajectory-planning-vla-tts/spell-fig2-mechanism.svg]]

    - **读图：** 原论文 Figure 2 展示 reverse trajectory 先预测终局，再检查是否落入 reference 周围的 shield；只有命中 shield 的路径才获得指向边界的 correction，其他路径继续原生去噪。静态 reference 用来保护指定图像，当前 batch 的动态 reference 用来避免并行结果相互重复，两者也可以混合。
  - **配置归类**
    - **Static / historical SPELL → D1：** reference 是固定保护数据集、历史样本或过去 batch 时，当前 particle 只读取外部 memory；每条轨迹可以单独应用 correction，不存在当前 particles 之间的 contemporaneous coupling。
    - **Dynamic intra-batch SPELL → D2：** reference 在当前时刻由同一 batch 其他 trajectories 的 expected outputs 动态更新时，$x_t^j$ 会改变 $x_t^i$ 是否触发 shield 以及 correction 的方向。
    - **Mixed SPELL → D2（附外部 reference guidance）：** 混合集合只要包含当前 batch 的动态预测结果，就已经存在 D2 型跨粒子耦合；静态部分同时提供 D1-style external guidance。方法名 SPELL 本身不能决定分类。
  - **训练—部署关系：** SPELL 是 training-free inference-time correction，不要求重训 base diffusion model。模型参数保持冻结；部署时变化的是 reference set、shield radius 与当前 reverse trajectory。
  - **边界与局限：** shield radius、距离表征和 $\hat x_0$ 的预测误差共同决定触发质量。多个 shields 重叠时，逐项 correction 不再保证得到禁入集合之外的全局最小投影；高维特征空间中“距离多近算复制”也依赖表征与阈值。过大的 $r$ 或 overcompensation 仍会明显扰动原分布并损害 fidelity。

### D2. Continuously Coupled Particle Diffusion

D2 不改变 parent 数量，而是在 reverse dynamics 中增加依赖整个集合的连续控制项。一般形式可写为：

$$
\mathrm d x_t^i
=
\bigl[b_\theta(x_t^i,t,c)
+\Psi_t^i(x_t^1,\ldots,x_t^K)\bigr]\mathrm dt
+g(t)\mathrm dW_t^i.
$$

一个 particle 的下一状态会随着其他 particles 的位置、特征或预测终局而改变，但整个过程没有“抽 parent—复制—淘汰”的离散 population update。

Dynamic intra-batch SPELL 也是这一机制的实例，但由于 SPELL 还存在 D1 型 static / historical reference 配置，其完整方法统一放在前面的 D1/D2 边界小节说明。

#### D2.1 Joint-potential interaction：Particle Guidance

- **[Particle Guidance: Non-I.I.D. Diverse Sampling with Diffusion Models](https://arxiv.org/abs/2310.13102)** 2023-10-19｜ICLR 2024；[OpenReview](https://openreview.net/forum?id=KqbCvIFBY7)
  - **前序工作问题**
    - **Abstract / Introduction：** 从同一个 diffusion model 独立采样得到的是 I.I.D. outputs；即使单样本分布质量高，一小批样本仍可能彼此相似，浪费有限的 batch budget。
    - **既有 diversity 手段问题：** terminal rejection 或事后挑选不能回收已经花掉的生成计算；直接把每个样本推离高密度区域又可能损害单样本质量。
  - **动机与方法**
    - 论文把 $K$ 个 reverse processes 提升为一个 joint process，并在每个 particle 的 base score 外加入同一个 permutation-invariant joint potential 对该 particle 的梯度：

      $$
      \mathrm d x_t^i
      =\left[
      -f(x_t^i,t)
      +g(t)^2\nabla_{x_t^i}\log p_t(x_t^i)
      +g(t)^2\nabla_{x_t^i}\log\Phi_t(x_t^1,\ldots,x_t^K)
      \right]\mathrm dt
      +g(t)\mathrm dW_t^i.
      $$

    - fixed-potential 版本可写成 pairwise similarity kernel 的负和：

      $$
      \log\Phi_t(\mathcal X_t)
      =-\frac{\alpha_t}{2}\sum_{i,j}k_t(x_t^i,x_t^j),
      $$

      使相似 particles 在连续去噪过程中产生排斥；论文也讨论 learned potential，用任务数据学习整组样本的 joint preference。
  - **核心创新点（一句话）**
    - 不再把 diffusion batch 当作互不相干的 I.I.D. samples，而是在 joint score 中加入集合势，使整批样本边去噪边协调多样性。
  - **论文图解**

    ![[assets/trajectory-planning-vla-tts/particle-guidance-fig1-overview.png]]

    - **读图：** 原论文 Figure 1 左侧是 I.I.D. sampling：每个黄色 particle 只受自身蓝色 score field；右侧加入红色 Particle Guidance 后，更新方向同时包含其他 particles 形成的集合势，终局样本覆盖更多不同模式。
    - **分类含义：** 红色项直接进入每个 particle 的连续 drift，说明粒子之间会在同一 reverse step 交换状态信息；图中没有 parent index 抽样，故是 D2 而非 D3。
  - **population 状态：** $K$ 条轨迹从头到尾都存在；“好粒子”不会被复制，“差粒子”也不会在某个时刻突然死亡，改变的是位置和方向而非 offspring count。
  - **训练—部署关系：** fixed potential 可直接用于 inference；learned potential 需要额外训练，但部署时仍作为 joint guidance term 作用于当前 states。
  - **边界与局限：** 一般 joint repulsion 优化的是 joint distribution，不自动保证每个 particle 的 marginal 与原模型完全一致；这个问题正是 EDDY 进一步处理的重点。

#### D2.2 Marginal-preserving interaction：EDDY

- **[Diverse Sampling in Diffusion Models with Marginal Preserving Particle Guidance](https://arxiv.org/abs/2605.06553)** 2026-05-07｜arXiv preprint
  - **前序工作问题**
    - **Abstract / Introduction：** Particle Guidance 一类 repulsive joint potential 可以提升 batch diversity，但一般会改变每个 particle 的 marginal law；换言之，整组样本更分散的同时，单个样本可能偏离原 diffusion model 的生成分布。
    - **理论缺口：** “让 particles 相互作用”和“保持每个 particle 的原始 marginal”看似冲突，需要找出不改变 Fokker–Planck marginal 演化的额外 drift 自由度。
  - **动机与方法**
    - EDDY 利用 Fokker–Planck equation 的 symmetry，在 multi-particle dynamics 中构造 anti-symmetric / divergence-cancelling drift perturbation。它让联合状态 $\mathcal X_t$ 中的粒子沿互相协调的 transport field 运动，而理论构造下每个 $x_t^i$ 的 marginal 仍遵循 base diffusion 的目标演化。
    - 从分类数据流看，更新仍具有

      $$
      \mathrm d x_t^i
      =\bigl[b_\theta(x_t^i,t)+\Psi_{\mathrm{EDDY}}^i(\mathcal X_t,t)\bigr]\mathrm dt
      +g(t)\mathrm dW_t^i,
      $$

      且 $\Psi_{\mathrm{EDDY}}^i$ 读取其他 particles；区别在于它受 marginal-preservation 条件约束，而不是任意 pairwise repulsion。
  - **核心创新点（一句话）**
    - 用 Fokker–Planck symmetry 设计跨粒子 transport，在提高集合多样性的同时，把单粒子 marginal fidelity 作为理论约束而非经验副作用。
  - **论文图解**

    ![[assets/trajectory-planning-vla-tts/eddy-fig2-transport-fields.png]]

    - **读图：** 原论文 Figure 2 左侧 PG 的矢量场主要从其他粒子径向排斥，容易把质量整体推离原分布；右侧 EDDY 的场呈现绕行、旋转式协同 transport，利用反对称结构重新分配 joint arrangement。
    - **分类含义：** 两边都持续改变 particle drift，均属 D2；EDDY 的新增维度是 marginal-preserving 约束，不是 discrete resampling。
  - **population 状态：** 轨迹数量和 parent identity 不变，所有 particles 持续演化。
  - **理论—实现边界：** 精确结论依赖论文的理论 drift 构造；面向高维图像的 feature-space / finite-difference approximation 不再保证严格 marginal preservation。更准确的表述是“以精确保持为理论设计目标，实用近似经验上维持较强 distributional fidelity”。
  - **发表状态：** 截至本文核验日期为 2026 arXiv preprint，后续版本可能调整定理条件、算法细节或实验结论。

### D3. Feynman–Kac / SMC Resampling

D3 的决定性动作是 **根据中间权重改变 parent identity 和 offspring count**。某个 particle 可以被复制成多个后代，也可以不再获得后续计算；这与 D2 中“所有粒子保留、只连续改 drift”是两种不同的 population control。

#### D3.1 FK Steering：potential 驱动的中间概率繁殖

- **[A General Framework for Inference-time Scaling and Steering of Diffusion Models](https://proceedings.mlr.press/v267/singhal25b.html)** 2025-01-12｜ICML 2025 / PMLR 267；[arXiv](https://arxiv.org/abs/2501.06848)
  - **前序工作问题**
    - **Abstract / Introduction：** 增加 diffusion inference compute 的常见做法是独立生成多个完整样本再做 Best-of-$N$。低潜力轨迹即使早期已经显露问题，仍会消耗到 $x_0$，计算预算不能在生成途中转移给更有希望的分支。
    - **既有 steering 问题：** classifier / reward gradient 往往要求可微 evaluator，并依赖 noisy-state 或 predicted-clean reward 的局部近似；它持续改写单条 drift，却没有统一解释 non-differentiable reward、intermediate selection 与 proposal correction。
  - **动机与方法**
    - FK Steering 把 reverse diffusion 视为一组可在中间时刻重新分配计算预算的 particles。它针对 reward-tilted target：

      $$
      p_{\mathrm{target}}(x_0)
      \propto
      p_\theta(x_0)\exp\bigl(\lambda r(x_0)\bigr).
      $$

    - 方法不要求 reward 可微，因此不必用 $\nabla r$ 改写每条轨迹的 drift。其核心是选择一系列正 potentials，使整条路径上的乘积对应 terminal reward tilt：

      $$
      \prod_{t=T}^{0}G_t(x_T,\ldots,x_t,c)
      =
      \exp\bigl(\lambda r(x_0,c)\bigr).
      $$

  - **核心创新点（一句话）**
    - 用 Feynman–Kac path potentials 把终局 reward tilt 分配到中间去噪阶段，并通过概率重采样把后续算力转向 promising ancestry。
    - 解决非可微分reward + 依赖noisy-state/predicted-clean reward 局部近似问题
	    - 1：非可微分：用粒子particle过滤筛除
	    - 2：noisy-state
  - **论文图解**

    ![[assets/trajectory-planning-vla-tts/fk-steering-fig1-overview.png]]

    - **读图：** 原论文 Figure 1 左侧只运行一条 inference path；中部 Best-of-$N$ 让多条 path 全部跑到终局再选；右侧 FK Steering 在多个中间时刻按 potential 复制高潜力分支、淘汰低潜力分支，黄色方块的 ancestry 在生成途中已经改变。
    - **分类含义：** 右图中的红叉不是连续排斥，蓝色虚线圈也不是显式 tree backup；它们表示当前 population 经过权重重采样后重新组成，因此是 D3。

##### FK-A. 概率重采样的数据流

在最容易理解的情形中，proposal transition 就是基础 diffusion transition。第 $t$ 个重采样时刻执行：

$$
\text{intermediate reward}
\rightarrow
G_t^i>0
\rightarrow
w_t^i=\frac{G_t^i}{\sum_jG_t^j}
\rightarrow
a_t^\ell\sim\operatorname{Cat}(w_t)
\rightarrow
\widetilde x_t^\ell=x_t^{a_t^\ell}.
$$

然后每个复制后的 parent 继续 proposal transition：

$$
x_{t-1}^\ell\sim\tau(x_{t-1}\mid\widetilde x_t^\ell,c).
$$

完整 SMC 形式还要把 proposal mismatch 纳入 importance weight：

$$
\widetilde G_{t-1}^i
=
\frac{p_\theta(x_{t-1}^i\mid x_t^i,c)}
{\tau(x_{t-1}^i\mid x_t^i,c)}
G_{t-1}(x_T^i,\ldots,x_{t-1}^i,c),
$$

再将 $\widetilde G^i$ 归一化用于下一次 resampling。当 $\tau=p_\theta$ 时，importance ratio 为 $1$，才退化为只归一化 $G_t^i$ 的直观版本。

##### FK-B. 四个 particles 的数值例子

假设中间 reward 为：

```text
particle      r_t
p1            0.2
p2            0.9
p3            0.1
p4            0.8
```

仅为解释概率抽样，先取示意变换 $G_t^i=\exp(r_t^i)$：

```text
particle      G_t       normalized w_t
p1            1.22          17%
p2            2.46          35%
p3            1.11          16%
p4            2.23          32%
```

系统不是保留 top-2，也不是只留下 $p2$，而是按 $w_t$ **有放回抽四次**。某一次随机结果可能为：

```text
p2, p4, p2, p1
```

于是 $p2$ 有两个后代，$p3$ 没有后代。这里的 offspring count 是随机变量；换一次随机种子，即使权重相同也可能得到不同 population。

逐个抽取 $K$ 个 parent index 可写成 $K$ 次 categorical sampling；把各 parent 最终获得的 offspring 数量合在一起看，则等价于一次 $\operatorname{Multinomial}(K,w_t)$ 抽样。

上面的 $G_t=\exp(r_t)$ 只是教学示意，不是 FK Steering 的唯一或普遍正确 potential。真实 potential 必须与完整路径重加权和 proposal correction 一起满足目标分布约束。

##### FK-C. Difference Potential 为什么看“这一段有没有进步”

论文给出的一种选择是：

$$
G_t^i
=
\exp\!\left[
\lambda\bigl(
r_\phi(x_t^i,c)-r_\phi(x_{t+1}^i,c)
\bigr)
\right].
$$

它不是只奖励“当前绝对分数高”，而是提高刚刚这一段去噪中 reward 上升较多的 particle 的繁殖概率。沿路径相乘时，中间项 telescoping：

$$
\prod_{t=0}^{T-1}G_t
=
\exp\!\left[
\lambda\bigl(r_\phi(x_0)-r_\phi(x_T)\bigr)
\right].
$$

若噪声端 reward 取零，或在 endpoint potential 中补偿 $r_\phi(x_T)$ 并令 $r_\phi(x_0)=r(x_0)$，就得到所需的 $\exp(\lambda r(x_0))$ terminal tilt。也就是说，Difference Potential 把一次终局偏好拆成多个中间增量权重，而不是在每一步重复乘同一个终局分数。

##### FK-D. 为什么复制以后还能分叉

若两个 offspring 复制了同一个 $x_t^2$，之后分别执行随机 reverse transition：

$$
x_{t-1}^{2a},x_{t-1}^{2b}
\overset{\mathrm{ind.}}{\sim}
p_\theta(x_{t-1}\mid x_t^2,c),
$$

独立噪声会让它们再次分化。因此，probabilistic resampling 会集中计算于 promising ancestry，但不必立即丢失全部 diversity。论文也实验了 interval resampling，例如只在 $\{80,60,40,20,0\}$ 等若干去噪时刻使用非均匀 $G_t$，其余时刻令 $G_t=1$ 并正常传播。

这个结论依赖后续 transition 具有随机性或额外扰动。若使用完全确定性的 DDIM / ODE transition，并让两个复制状态接受完全相同条件和算子，那么它们不会自行分叉；此时需要保留不同 parent、注入噪声或使用其他 diversity mechanism。

##### FK-E. FK 不是 top-$k$，也不是 terminal Best-of-$N$

| 方法 | 评价发生时刻 | population 怎样改变 | 低分样本消耗的后续计算 | 是否允许同一 parent 多个后代 |
| --- | --- | --- | --- | --- |
| Terminal Best-of-$N$ | 所有样本生成到 $x_0$ 后 | 不改变生成过程，只在终局选一个或若干个 | 已经完整消耗 | 否 |
| Deterministic top-$k$ pruning | 中间或终局 | 固定保留最高分集合，通常截断其余项 | 视剪枝时刻而定 | 只有显式复制时才有 |
| FK / SMC resampling | 一个或多个中间去噪时刻 | 按归一化权重有放回随机抽 parents | 低权重 ancestry 可能提前停止 | 是 |
| Particle Guidance | 每个受控 reverse step | 不改 ancestry；用 joint potential 连续改 drift | 所有 particles 通常继续存在 | 否 |

因此，FK 的本质不是“评分器更强”，而是 **评分经过 potential 后改变了下一阶段的 population ancestry distribution**。

- **状态与更新：** 持久状态包括 $K$ 个 particles、normalized weights 及必要的 ancestry；重采样是有放回的 categorical / multinomial reproduction，不是 deterministic top-$k$。
- **训练—部署关系：** base diffusion 与可选的 reward estimator 可预先训练；FK population update 本身发生在推理时，不更新 base model 参数。
- **局限：** intermediate reward 的预测质量、potential schedule 与 resampling 频率会影响 weight degeneracy 和 diversity；有限 $K$ 下仍有 Monte Carlo error。复制后能否重新分叉取决于后续 transition 的随机性。

#### D3.2 DAS：guided proposal 与 tempered SMC 的组合

- **[Test-time Alignment of Diffusion Models without Reward Over-optimization](https://proceedings.iclr.cc/paper_files/paper/2025/hash/d9042abf40782fbce28901c1c9c0e8d8-Abstract-Conference.html)** 2025｜ICLR 2025
  - **前序工作问题**
    - **Abstract / Introduction：** reward fine-tuning 可以提高目标分数，但 reverse-KL 式优化容易 mode-seeking，出现 reward over-optimization、diversity 下降和对未优化 reward 的泛化变差。
    - **Related Work / Section 3.2：** training-free approximate guidance 往往用 Tweedie clean estimate $\hat x_0(x_t)$ 近似 noisy-state posterior reward。早期噪声大时该估计不准，单纯梯度 guidance 容易 reward under-optimization；直接套用 SMC 又可能需要很多 particles，并出现 weight degeneracy。
  - **动机与目标分布**
    - DAS 把 alignment 写成 KL-regularized distribution optimization，其闭式 target 为：

      $$
      p_{\mathrm{tar}}(x)
      =\frac{1}{Z}p_{\mathrm{pre}}(x)
      \exp\!\left(\frac{r(x)}{\alpha}\right).
      $$

    - 它不是把 reward 最大的单个样本当作答案，而是用 SMC 逼近整个位于 pretrained prior 与高 reward 之间的 target distribution。
  - **三步 sampler 设计**
    - **Tempered intermediate targets：** 随噪声减小逐步打开 reward：

      $$
      \pi_t(x_t)
      \propto
      p_t(x_t)
      \exp\!\left(\frac{\lambda_t}{\alpha}\hat r(x_t)\right),
      \qquad
      0=\lambda_T\le\cdots\le\lambda_0=1.
      $$

    - **Guided proposal：** 用 reward gradient 构造 locally optimal proposal 的 Gaussian approximation：

      $$
      m_{t-1}(x_{t-1}\mid x_t)
      =\mathcal N\!\left(
      \mu_\theta(x_t,t)
      +\sigma_t^2\frac{\lambda_{t-1}}{\alpha}
      \nabla_{x_t}\hat r(x_t),
      \sigma_t^2I
      \right).
      $$

    - **Importance correction 与 resampling：** proposal 后计算

      $$
      w_{t-1}
      =
      \frac{\widetilde\gamma_{t-1}(x_{t-1})}
      {\widetilde\gamma_t(x_t)}
      \frac{L_t(x_t\mid x_{t-1})}
      {m_{t-1}(x_{t-1}\mid x_t)},
      $$

      再把归一化 $w_{t-1}^{1:K}$ 用于 multinomial resampling。梯度负责提出较好的局部 move，importance weight 负责修正 proposal 偏差，resampling 负责重新分配 ancestry。
  - **核心创新点（一句话）**
    - 用 tempering 降低早期 reward approximation 的破坏性，并把 guided proposal、importance correction 与 SMC reproduction 组合成 training-free diffusion alignment sampler。
  - **论文图解**

    ![[assets/trajectory-planning-vla-tts/das-fig1-target-sampling.png]]

    - **读图：** 原论文 Figure 1 左侧给出 pretrained 与 reward-aligned multimodal target；RL / direct backprop 只覆盖部分 mode，approximate guidance 偏离目标，DAS 在两个 toy targets 上同时覆盖所有目标 modes，并取得较低 EMD。
    - **分类含义：** 图的重点不是“DAS 有 reward gradient”，而是最终用多粒子加权与重采样逼近整个 target distribution。只看 proposal 会漏掉决定 population composition 的 SMC 层。
  - **为什么主类是 D3：** DAS 确实含 D1 型 pathwise guided proposal，但每轮还以 importance weights 离散抽取 parents，offspring count 会改变，因此完整 sampler 的主 population mechanism 是 D3。
  - **为什么不是 D4：** 在 proposal gradient 之外，$x_t^i$ 的连续 drift 不读取 $x_t^j$ 的位置或 feature；其他 particles 通过归一化权重和 multinomial ancestry update 发生离散耦合，而不是 D2 型 continuous joint drift。
  - **训练—部署关系与局限：** 方法不微调 diffusion model，但需要运行 reward / clean estimate 与多粒子 SMC。论文的 asymptotic exactness 建立在正则条件和 $K\rightarrow\infty$ 的估计意义上，不代表有限 particles、近似 reward 与近似 proposal 下没有误差。

### D4. Hybrid Interaction-and-Resampling

D4 同时使用两种 population control：

$$
\underbrace{\Psi_t^i(\mathcal X_t)}_{\text{continuous interaction / drift correction}}
\quad+\quad
\underbrace{a_t^i\sim\mathcal R_t(w_t)}_{\text{discrete reweighting and resampling}}.
$$

#### D4.1 IMPFM：交互式 flow-map drift 与 Feynman–Kac corrector

- **[Sequentially-Controlled Interactive Multi-Particle Flow-Maps for Online Feedback-Driven Search](https://arxiv.org/abs/2607.01144)** 2026-07-01｜arXiv preprint
  - **前序工作问题**
    - **Abstract / Introduction：** 当偏好事先未知、只能通过昂贵的顺序反馈逐步显露时，普通 training-free reward alignment 擅长在当前高分附近做 local exploitation，却不擅长用有限 query 覆盖多个可能的高效用区域。
    - **既有 SMC / tree 方法问题：** 标准 SMC 容易 weight degeneracy、proposal collapse；tree-based sampler 可能绕开一部分终局 reward 稀疏性，但仍受固定 generative dynamics 限制。标准 Feynman–Kac corrector 主要按粒子自身价值重加权，粒子之间缺少可复用的 posterior information sharing。
  - **目标与 look-ahead value**
    - IMPFM 以 pretrained process 的 marginal $p_t$ 为 prior，定义 reward-tilted intermediate law：

      $$
      p_t^*(x)
      \propto p_t(x)e^{V_t(x)},
      \qquad
      V_t(x)=\log\mathbb E\!\left[e^{r(X_1)}\mid X_t=x\right].
      $$

    - $V_t$ 不是只评价当前观测到的 reward，而是把从当前状态继续 transport 后可能获得的终局效用折算成 look-ahead value。
  - **连续 interaction：posterior sample sharing**
    - flow map 为每个 particle 提供条件 posterior samples；系统不仅利用 particle 自己的 value gradient，还通过 kernel 复用其他 particles 的 posterior / value 信息，并加入 repulsion。其数据流可概括为：

      $$
      \text{own value exploitation}
      +\text{kernel-weighted peer value transfer}
      +\text{cross-particle repulsion}
      \longrightarrow
      \Psi_t^i(\mathcal X_t).
      $$

    - attraction 把 particle 拉向 ensemble 已发现的高效用区域，repulsion 阻止所有 paths 聚到同一个 mode；其他 particles 的信息在 transition 前就连续改变当前 drift。
  - **离散 corrector：interaction-aware Feynman–Kac update**
    - 论文从 flow map 构造 diffusion-style stochastic transition，并在反馈到来时计算兼顾 exploitation 与 exploration 的 interaction-aware weights。归一化权重随后触发 Feynman–Kac resampling，改变 parent identity 和 offspring count。
    - 因此一次完整更新同时包含：

      ```text
      ensemble posterior sharing
          → interactive drift correction
          → stochastic propagation
          → feedback / value update
          → interaction-aware reweighting
          → resampling
      ```
  - **核心创新点（一句话）**
    - 把跨粒子的连续 posterior sharing 与 interaction-aware FK reproduction 合并，使在线反馈既能修正每条 path 的方向，也能重新分配整组 particles 的生存预算。
  - **论文图解**

    ![[assets/trajectory-planning-vla-tts/impfm-fig1-overview.png]]

    - **读图：** 原论文 Figure 1 左侧 valued-based drift correction 只沿各自 value 向同一 mode 聚集，出现 mode collapse / reward over-optimization；右侧 IMPFM 同时画出 own value、来自其他 particles 的 kernel-weighted value transfer 和 repulsion，多个 particles 因而覆盖不同高效用 modes。
    - **分类含义：** 图中跨粒子的箭头说明 D2 型 continuous interaction；论文 sampler 另有 interaction-aware Feynman–Kac reweighting / resampling，说明还存在 D3 型 discrete reproduction。两者同时存在才构成 D4。
  - **为什么不能只标 D2：** ancestry 会在 corrector step 被重采样，population composition 不是固定的。
  - **为什么不能只标 D3：** 在 resampling 之外，peer posterior 与 repulsion 已经连续进入每个 particle 的 drift；这不是只通过 normalized weights 发生的离散耦合。
  - **训练—部署关系与局限：** 该方法建立在 flow matching / flow-map posterior sampling 上，再构造 diffusion-style stochastic transitions；它不是传统 DDPM 的简单插件。论文截至核验时为 2026 preprint，理论与实验结论应保留版本限定。

D4 不是任何“多模块 diffusion”都自动满足的类别。只有 **跨粒子连续耦合** 与 **基于权重的离散 reproduction** 在同一 sampler 中都真实执行，才使用 D4。

### 3. 跨论文对照索引

| 论文 | 基础持久状态 | 路径内 guidance | 当前 particles 连续互读 | 中间 ancestry 重采样 | 本文主类 |
| --- | --- | --- | --- | --- | --- |
| DDPM | 单个 noisy sample $x_t$ | 无 | 否 | 否 | D0 |
| DDIM | 单个 $x_t$ 与选定 timestep schedule | 无 | 否 | 否 | D0 |
| Classifier Guidance | $x_t$ | noisy classifier gradient | 否 | 否 | D1 |
| Classifier-Free Guidance | $x_t$ | conditional / unconditional score difference | 否 | 否 | D1 |
| Interval Guidance | $x_t$ 与预设 noise interval | 仅在中等噪声区间执行 CFG extrapolation | 否 | 否 | D1 |
| CADS | $x_t$ 与退火条件 $\hat y_t$ | 对路径自身的 conditional input 加逐步消失的噪声 | 否 | 否 | D1 |
| SafeBimanual | noisy action trajectory $A_k$ | scheduled safety-cost gradient | 否 | 否 | D1 |
| Particle Guidance | $K$ 个 $x_t^i$ | joint-potential gradient | 是 | 否 | D2 |
| SPELL | $x_t$ 与 predicted clean output | sparse shield correction | 视 reference 配置而定 | 否 | static: D1；dynamic: D2 |
| EDDY | $K$ 个 $x_t^i$ | marginal-constrained interaction drift | 是 | 否 | D2 |
| FK Steering | particles、weights、ancestry | potential 可不依赖梯度 | 仅通过归一化权重离散耦合 | 是 | D3 |
| DAS | particles、tempered targets、weights | reward-gradient proposal | 否 | 是 | D3，附 D1-style proposal |
| IMPFM | interactive particles、flow-map posterior、weights | own / peer value 与 repulsion | 是 | 是 | D4 |

这张表只汇总 sampler 内部机制，不抹去每篇论文的训练方式、reward 来源或应用领域。尤其是 DAS：它有 pathwise gradient，不代表主类是 D1；决定性区别是完整算法随后用 importance weights 改变 ancestry。SPELL 则必须按 reference set 的实际来源判定，不能只看方法名。

### 4. 与显式 diffusion tree search 的边界

Particle population 即使发生复制，也不必维护显式树。FK / SMC 通常只需保存当前 particles、weights 与必要的 ancestry 信息；过去未选中的分支可以被丢弃。相比之下，[Monte Carlo Tree Diffusion](https://arxiv.org/abs/2502.07202) 一类方法把部分去噪状态保存为可重复访问的节点，并执行显式分支扩展、节点选择、价值统计或回传。

因此，本节把 tree search 作为边界项：

- 只按权重复制当前 population、随后向前传播：D3 或 D4；
- 保留可回访的父子节点并在树上分配 rollout budget：显式 diffusion tree search，不并入 D3。

### 5. 快速判别顺序

分析一个 diffusion sampler 时，可以依次问：

1. **中间阶段是否根据权重重新抽 parent index，使 offspring count 改变？** 若是，至少属于 D3。
2. **在重采样之外，一个 particle 的 drift / score 是否连续读取其他 particles？** 若是，D3 升为 D4；若否，保持 D3。
3. **若没有重采样，一个 particle 的更新是否读取当前 batch 其他 trajectories 或动态 expected outputs？** 若是，属于 D2。
4. **若没有跨粒子耦合，是否有额外代价、约束或 classifier gradient 修改本粒子的 transition？** 若是，属于 D1。
5. **若以上都没有，**就是 D0；生成完成后的 Best-of-$N$ 只另记为 terminal selection。

这套顺序保留了 diffusion 方法原本的重要差别：单个状态怎样被引导、多个 particles 是否持续交互、计算预算是否通过概率繁殖重新分配，以及二者是否组合，而不会把所有“使用 reward 的 diffusion”都压成同一类。
