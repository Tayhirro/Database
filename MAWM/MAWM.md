1：single-agent WM 往往把其他 Agent 的影响吸收到非平稳性或随机性中；MAWM 显式分解不同 Agent 对状态变化的贡献    

2：多智能体 credit assignment 同时包含时间维度和 Agent 维度，WM 可以做跨 Agent 的反事实贡献估计     


3：MAWM 的 transition model 以所有 Agent 的联合动作作为条件，预测 latent variables 的演化  在CTDE的情况下，反馈给各个观测，独自做训练，联合的state转移使得 observation更全面，状态转移可能更优，给到的DE 更好

4. MAWM 可通过 Projective Objectives 学习不同视角、多模态或部分／掩码输入下的一致表征，使相关信息在观测变化时保持稳定。（若强调多 Agent 的特色，应收窄为“通过显式关联不同 Agent 的观测，学习跨 Agent 视角的一致表征并整合局部信息”，因为单个 Agent 也能使用多视角、多模态或 mask 输入，普通的一致性目标本身并非多 Agent 独有。）
   - **区别在于是否显式利用对应关系**：将不同视角分别作为样本训练同一个模型，可以学到共性，但不自动建立同一场景的跨视角对应；配对预测或一致性 loss 则显式利用这种关系，而模型数量或样本分开输入不能显式利用这种loss
	   - 显式关联不同视角数据 == 显式考虑单视角外的数据建模 




对应原文：

1：
> Unlike single-agent WMs that abstract multi-agent effects into environmental non-stationarity/stochasticity [11, 85], MAWMs explicitly factor the impacts on the learned world state into individual agent contributions.

2：
> This problem is further complicated in a multi-agent setting because there are now two dimensions to the credit assignment problem—credit assignment over time and credit assignment over agents. WMs enable precise credit assignment in MASs through counterfactual advantage calculations [17, 41, 69] and transition modeling [5, 97].

3：
> The transition model predicts the evolution of the latent variables as a function of the joint action of all of the agents.

4：（第 3.2.4 节 Projective Objectives，第 16 页）
> Projective learning, on the other hand, aims to make the representations consistent by predicting related representations from one another. These related representations can be from two differing observations, observations of multiple modalities, or corrupted/masked observations. This helps in learning features that remain invariant across different views or partial inputs.

（此处概括的是相关表征之间的一致性，不要求不同 Agent 的全部观测相同；后面的 M-QMIX 例子具体采用同一 Agent 观测的随机特征 mask，不能直接据此认定它做了跨 Agent 视角对齐，上述 CTDE 说明也是适用性解释，并非这段原文的结论。）





![[Pasted image 20260905020641.png]]





## 3. Multi-Agent World Models（MAWMs）Framework

以下严格按照原文第 3 章的叙述顺序记录。

然后，作者给出形式化定义。对于 $N$ 个 Agent：

$$
M=\left\langle\{M_i\}_{i\in K},G,C\right\rangle,\qquad 1\le K\le N
$$

其中：

- $\{M_i\}_{i\in K}$ 是 $K$ 个局部 World Model 的集合。每个 $M_i$ 包含局部 observation space $\Omega_i$、action space $A_i$、latent state space $L_i$，以及 encoder $E_i$、decoder $D_i$、transition function $T_i$ 和 reward function $R_i$；
- $G(V,E)$ 是定义信息流向的 communication graph；
- $C$ 是基于通信图更新各 Agent latent state 的 communication function。

在这一定义下，原文依次列出四种特例：

1. **C-MAWM**：一个模型，采用中心化通信；
2. **D-MAWM - Full Communication**：多个局部模型全连接通信；
3. **D-MAWM - No Communication**：各 Agent 的模型相互独立；
4. **D-MAWM - Graph Communication**：采用任意图拓扑，它是能概括上述分布式情形的一般形式。

在完成定义后，论文才将后续分析分成三部分：（1）architectural design and communication；（2）learning objectives；（3）functional applications。

### 3.1 Architectures for MAWMs

原文首先介绍中心化和分布式架构。C-MAWM 使用一个中心 World Model，各 Agent 向它通信以获取预测和更新；D-MAWM 则将多个 WM 分布到各 Agent，允许它们进行局部估计，并减少对中心控制器的依赖。

随后，作者按照通信方式介绍三种分布式形态：no communication、all-to-all communication 和 graph-based communication。无通信能避免不可靠或对抗性通信的故障，但需要 Agent 通过感知和 opponent modeling 隐式协调；全连接通信给每个 Agent 完整信息，但带来最大带宽成本；图通信则是最一般的表达，可以描述任意网络拓扑。

CTDE 会模糊中心化与分布式的分类边界。作者明确表示，它按 **World Model 实际被用在哪里** 来归类：CTDE 中用于训练的 WM 归为中心化，用于在线规划的 WM 归为分布式。

#### 3.1.1 Centralized Multi-Agent World Models（C-MAWMs）

原文先将 C-MAWM 解释为单智能体 WM 的直接扩展：把多个 Agent 当作一个统一系统状态的组成部分，用一个中心模型表示联合状态，捕捉所有 Agent 及其交互的动力学。这使模型能直接访问所有 Agent 的 observations 和 actions，无需在多个模型之间通信。

然后作者指出局限：联合状态和动作空间会随 Agent 数量指数增长，仅联合动作空间的复杂度就是 $O(|A|^N)$；同时，中心化架构还存在单点故障，不适合要求去中心化执行或容错的场景。后续工作因此主要通过近似和结构约束提高可处理性，原文按顺序介绍：

1. **MARCO 的中心化近似模型**：学习能跨策略泛化的稳定模型，并在模型学习与策略优化之间交替；
2. **Sequence Modeling Approaches**：向 WM 提供 joint observation、action 或 state，用 RNN 或 Transformer 学习当前应关注的 Agent 间信息；
3. **Graph-Based Approaches**：将 Agent 表示为节点、交互表示为边，利用 GNN 的结构化归纳偏置对联合状态进行因子分解，代表方法包括 SMAWM 和 VDFD。

#### 3.1.2 Decentralized Multi-Agent World Models（D-MAWMs）

原文接着将 D-MAWM 定义为：将联合 WM 分解为 $N$ 个分布式模型，每个 Agent $i$ 维护自己的 $M_i$，处理局部观测并维护它对环境的 latent state。它可能减小动作空间和 sample complexity，但会增加计算与通信复杂度。原文随后依次讨论：

1. **No Communication**：先介绍通过观测预测其他 Agent 的 opponent modeling；然后介绍借助可观测环境变化实现隐式协调的 stigmergic coordination；最后介绍通过 CTDE 在训练阶段学到、并迁移到去中心化执行的 learned emergence（如 M-QMIX 和 MABL）。这类方法消除了执行时的带宽和延迟限制，但会把负担转移给对其他 Agent 的预测或更高成本的训练，且在高度动态或对抗场景中可能降低协调效果；
**（你主要讲未沟通的时候的情况。）**


2. **All-to-All Communication**：各 Agent 直接交换局部观测、意图和模型预测，理论上可在保留分布式容错性的同时接近中心化系统的表现。许多方法使用 Transformer 融合消息，但通信成本和 attention 计算均随 Agent 数量二次增长，且未必符合现实通信协议与约束；
3. **Graph-Based Communication**：以顶点表示 Agent、以边表示允许的通信通道，可描述通信范围、多跳传播和时变网络。Pretorius et al. 让 Agent 交换 WM 生成的未来轨迹，说明 imagined futures 的交流能改善协调。其主要问题是图算法复杂度、多跳延迟与中心节点的 latent bottleneck，以及动态拓扑变化。

### 3.2 Learning Objectives in MAWMs

在架构之后，论文按以下顺序讨论 MAWM 如何学习和维护世界表示。作者首先指出，实际方法常常会组合多种目标。
（希望模型学会什么，主要是这个点啊，着重来讲一下，就是希望它怎么学什么东西。）
#### 3.2.1 Value and Reward Prediction

（**强相关于优势 2，弱相关于优势 1、3**：Reward/Value 为时间与 Agent 的反事实贡献提供评分信号，但它本身不负责学习多 Agent 动作如何推动状态转移。）

先预测未来 reward，或估计 state/action 的 value。这与 RL 的累积回报目标直接对齐，但可能让 WM 过度专门化于特定 reward structure，限制新任务或新环境上的泛化。原文随后介绍 **Long-Horizon Value Prediction**，用 terminal value 弥补短视界规划的限制；再介绍 **Uncertainty-Aware Reward Modeling**，通过 ensemble 或 Monte Carlo dropout 估计不确定性，减少合成数据偏差。


#### 3.2.2 Reconstruction-based Objectives

（**弱相关，属于交互建模的表征基座**：尤其 masked multi-agent reconstruction 主要学习跨 Agent 的共享相关信息，为后续建模交互打底，但不直接说明各 Agent action 对状态变化的具体影响，也不直接完成 credit assignment。）

然后讨论对环境和各 Agent 观测状态的重建。这种目标能促使 WM 捕捉环境与 Agent 交互，但传统 mean squared error 会平等对待每个观测维度，可能浪费容量去重建与任务无关的特征。接下来依次是：

1. **Multi-Agent Reconstruction Objectives**：利用 masked-agent attention 在多个 Agent 之间进行 inter-predictive learning；
2. **Multi-Step Reconstruction Error**：直接学习完整行为序列，或通过多步 rollout 最小化累积预测误差，避免局部单步误差逐步放大。

#### 3.2.3 Dynamics Learning

（**强相关**：它直接对应优势 3，并为优势 1 的 Agent action 影响建模以及优势 2 的 counterfactual credit assignment 提供 transition 基础。）

再后是在压缩 latent space 中学习环境及 Agent 交互的状态转移。它避免了复杂的 observation reconstruction，可以保持 reward/task agnostic。原文随后介绍 **Self-Supervised Dynamics Learning**：在无显式标注情况下，通过 masked learning、forward dynamics 和 inverse dynamics 等目标学习 latent state evolution。

#### 3.2.4 Contrastive and Projective Learning

（**弱相关，属于辅助表征基座**：它主要改善跨 Agent、跨时间或跨视角的 latent representation，可辅助后续 dynamics learning，但不直接建模 joint action 的影响或 Agent credit。）

最后讨论不依赖完整状态重建或显式奖励的表示学习：

1. **Contrastive Objectives**：提高相关样本表示的相似性，降低无关样本的相似性，以捕捉时序关系和 Agent 间依赖；局限是正负样本选择困难，而且负样本需求可能随问题维度快速增长；
2. **Projective Objectives**：从不同观测、多模态观测或被破坏／掩码的观测中预测对应表示，使 latent representation 在视角变化或局部输入下保持一致；局限是 representation collapse 和对 corruption 方式敏感。
![[Pasted image 20260905134910.png]]
### 3.3 Applications of MAWMs

在学习目标之后，论文按以下顺序介绍 MAWM 的五类应用。

#### 3.3.1 Synthetic Data Generation

首先是从当前 observations 和 actions 预测后续 states 与 rewards，生成 synthetic trajectories，以缓解硬件成本、安全风险和采集时间带来的数据障碍。代价是额外计算和 model bias，效果取决于 trajectory quality。原文随后依次介绍：

1. **Improved Synthetic Data**：如 MAMBA 从 replay buffer 采样初始状态，再按当前策略在 latent space 生成 on-policy 轨迹；MACD 生成反事实轨迹以估计 Agent 贡献；
2. **Factorized MAWMs**：将 WM 分解为 static、controllable 和 stochastic 部分，或在 CTDE 中同时建模 global coordination information 与 local agent dynamics；
3. **Uncertainty-Aware Data Generation**：依据 transition/reward model 的置信度选择数据区域、调整 rollout 长度或在必要时返回真实环境，以提高 sample efficiency 并抑制 model bias。

#### 3.3.2 Credit Assignment

然后是信用分配。多智能体场景同时存在两个维度：时间上的 credit assignment 和 Agent 之间的 credit assignment。原文先讨论 **Advantage Estimation**，用 WM 生成反事实轨迹或估计不同 Agent coalition 的价值，从而更准确地分配贡献；再讨论 **Transition Knowledge**，利用转移模型改善 temporal-difference update、优先级更新和联合动作空间的因子分解。

#### 3.3.3 Policy Feature Extraction

接着，论文讨论学习同时表示单个 Agent 动力学与 Agent 间关系的 latent policy features。使用方式先分为两类：（1）将 WM feature 直接作为 policy input；（2）将 WM term 加入 loss function。后一种在 model-free 环境中常被视为 pseudo-WM。随后原文依次介绍：

1. **Self-Supervised Features**：在 observation、Agent 或 global state 层面做掩码预测；
2. **Communication-Aware Representations**：用 attention 学习重要消息，或压缩 imagined trajectories 后交流；
3. **Hierarchical Representations**：分离 global information 和 agent-specific information，以同时捕捉局部行为和全局模式。

#### 3.3.4 Online Planning and Control

然后是让 Agent 通过学到的模型实时预测、优化动作，并考虑其他 Agent 的行为。这支持在新任务中进行 zero/few-shot planning，但受制于联合动作空间的指数增长。原文先列出三个主要难点：partial observability、多步预测误差累积，以及随 Agent 数量增长的通信与同步成本；之后依次讨论四种规划范式：

1. **MPC Planners**：在有限视界内反复优化动作序列；
2. **MCTS Planners**：用 WM 执行状态转移和价值估计，并通过优势引导搜索缓解联合动作空间的指数复杂度；
3. **LLM and LVM Planners**：用大模型完成感知、记忆、通信与规划，或用组合式扩散 WM 显式分解多 Agent 动力学；
4. **Other Planners**：包括把 MPC 用于降低 WM 自身的多步预测误差、规划后交流压缩的预测轨迹，以及用离线规划改善 opponent modeling 和价值估计。

#### 3.3.5 Opponent Modeling

最后，论文回到对其他 Agent 的建模。若知道对手的动作，model-based 方法通常能做出更准确的预测；因此 Opponent Modeling 尝试从观测中推断其他 Agent 的联合策略。原文依次介绍了从单个 Agent 视角预测他人动作的 DSSM、学习并影响对手 latent strategy 的方法，以及根据每个 opponent model 的预测误差自适应调整 rollout 长度的 AORPO。

## 4. Selecting and Implementing MAWMs

### 4.1 When to Use MAWMs

#### 4.1.1 MAWM Benefits

原文指出，相比其他方法，MAWM 在需要样本效率、协调或任务泛化的场景中具有以下四项收益（原文第 22 页，引用编号沿用原文）：

1. **Improved Sample Efficiency（提高样本效率）**：当样本采集成本较高时，MAWM 可以显著减少训练所需的真实环境交互。例如，原文报告 MAMBPO [117] 可将所需交互量降低至原来的约 $1/1.7$ 至 $1/3.7$，并以 MARCO [130] 的多项式样本复杂度作为另一例证。
2. **Improved Training（改善训练）**：MAWM 可以通过改善 credit assignment，以及更充分地利用有限的样本数据，提高训练样本的利用效果。
3. **Improved Online Performance（改善在线表现）**：MAWM 可以通过更好的 policy features 捕捉关键的多智能体动力学，从而改善协调；也可以通过在线规划，在考虑其他 Agent 潜在动作的同时优化长时域回报，原文举例为 MAZero [69]。
4. **Task Generalization（任务泛化）**：理解世界动力学后，传统 RL Agent 可以超越单纯的反应式策略，在无需针对新问题重新训练的情况下规划解决方案，原文举例为 MA-TDMPC [112]。

## 5. Future Directions

### Distributed Modeling Challenges（分布式建模的一致性问题）

各 Agent 的本地 WM 会因局部观测、通信延迟和对其他 Agent 行为判断的差异，对同一环境产生相互矛盾的预测；如何在有限通信下持续同步信息，使各模型对共同场景的判断相容并与真实环境一致，仍是开放问题，而非仅靠表征对齐就能解决（原文第 5 章，第 23 页）。
