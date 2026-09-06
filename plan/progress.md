# 写作进度

## 2026-08-11

- 已定位 `任务-new.md` 中 2.1 缺少类别定义的问题。
- 已建立 Generative Refinement 的证据映射和段落蓝图。
- 初次修订错误地将四类 refinement 总对照表放入 2.1，造成父子层级混乱；该版本已撤销。
- 四类机制总对照表现位于 `#### 2. 串行精化` 的父级说明中；`##### 2.1 Generative Refinement` 只保留本机制的定义、公式、判别条件和案例过渡。

### Spec compliance review

- 父级总表位于第 2 节标题与 2.1 标题之间：通过。
- 2.1 内不存在 Rethinking、Verifier-Guided、World-Feedback 的总比较表：通过。
- Generative Refinement 定义和公式位于 DiffusionDrive 案例之前：通过。
- DiffusionDrive、DiffRefiner 及 2.2—2.4 原有内容均保留：通过。

### Quality review

- 2.1 围绕单一机制展开，未再承担父级分类职责。
- 定义明确了直接更新对象、生成条件和更新动力学。
- 新增正文未出现禁用过渡词；样式脚本返回 0。父级 Markdown 表格因连续非空行被脚本提示，属于表格正常结构。

### Capability-use audit

- Required skills: paper-orchestration、writing-core、obsidian-markdown、verification。
- Skills actually used: paper-orchestration、writing-core、obsidian-markdown、verification。
- Inputs consumed: 用户层级反馈、`任务-new.md` 第 2 节、现有 evidence map 与 blueprint。
- Inputs not used and why: 未重新检索外部论文，因为本次只修正现有分类层级，不新增事实或引文。
- Artifacts produced: 更新后的第 2 节与 2.1、project overview、outline、task packet、修订后的证据和蓝图记录。
- Verification run: PowerShell 标题区间、表格位置、定义位置、案例保留和禁用词检查；写作样式脚本。
- Remaining risk: 2.2 暂无具体论文案例，但不影响本次父子层级修正。

## 2026-08-11｜2.2—2.4 类别介绍补全

- 已将 2.2 的四条简略列表改为完整的 Rethinking / Iterative Reasoning 定义、公式与边界说明。
- 已在 2.3 的案例之前补入 Verifier-Guided Refinement 的定义、评价—修正公式及 Selection/World-Feedback 边界。
- 已在 2.4 的案例之前补入 World-Feedback Refinement 的定义、预测—评价—修正公式及 verifier/环境闭环边界。

### Spec compliance review

- 2.1—2.4 均包含定义、形式化过程、直接更新对象、反馈来源和分类边界：通过。
- 三个新增介绍均位于各自案例或下一小节之前：通过。
- 未在子节中重复父级总分类表：通过。
- DiffusionDrive、DiffRefiner、DriveVer、CriticVLA、SC-VLA 原有案例均保留：通过。

### Quality review

- 四节介绍长度为 515—542 个非空白字符，结构密度一致。
- 2.2 以推理状态为更新对象，2.3 以候选级评价为反馈，2.4 以预测后果为反馈，类别边界没有混用。
- 新增介绍未出现正文列表和禁用套话；写作样式脚本返回 0，目标介绍区无告警。

### Capability-use audit

- Required skills: paper-orchestration、writing-core、peer-review、obsidian-markdown、verification。
- Skills actually used: paper-orchestration、writing-core、peer-review、obsidian-markdown、verification。
- Inputs consumed: 用户补全要求、2.2 原说明、DriveVer/CriticVLA/SC-VLA 现有案例、父级分类表。
- Inputs not used and why: 未检索外部文献，因为未新增论文事实，只把文件已有机制归纳为对称定义。
- Artifacts produced: 2.2—2.4 三节介绍、任务包、蓝图、更新后的 outline/evidence/review/progress/notes。
- Verification run: 四节逐项完整性检查、标题顺序与案例保留检查、写作样式脚本。
- Remaining risk: 2.2 仍没有具体论文案例；定义本身已完整，但案例覆盖不对称。

## 2026-08-19｜2.1—2.5 分类轴重构

- 将父级分类依据由“直接更新对象 + 更新信号来源”改为“产生下一迭代状态的近端更新机制”。
- 父级总表补入 Objective-Guided Continuous Refinement，并将五类统一写成“迭代状态—近端机制—核心判别”。
- 重写 2.1—2.5 的开头定义和交叉边界，区分 feedback-conditioned refiner、显式 future feedback 与直接数学局部优化。
- 增加单标签与组合标签规则；SafeBimanual、Diffuser 等 guided diffusion 继续使用生成精化与目标引导的组合标注。

### Spec compliance review

- 用户要求保留的五个分类名称和 2.1—2.5 编号均未改变：通过。
- 五类均按同一近端更新机制判据描述：通过。
- 2.5 不再把“存在目标函数”作为归类条件：通过。
- learned verifier、world-model-derived objective 和 guided diffusion 均有明确边界：通过。
- DiffusionDrive、DriveVer、ReflectVLM、SafeBimanual 等原有案例均保留：通过。

### Quality review

- 纯条件去噪与 cost-gradient guided denoising 可分别落入单标签和组合标签。
- verifier/world feedback 交给 refiner 的路径，与对可微目标直接执行局部优化的路径已经分离。
- 世界模型产生显式 future 并交给 refiner 时归入 2.4；直接穿过世界模型求导优化时归入 2.5，并保留来源属性。
- 2.2 与其他类型组合时按实际更新式处理，不再仅凭是否使用同一 backbone 判断。

### Capability-use audit

- Required skills: paper-orchestration、writing-core、obsidian-markdown、peer-review、verification。
- Skills actually used: paper-orchestration、writing-core、obsidian-markdown、peer-review、verification。
- Inputs consumed: `任务-new.md` 第 2 节、现有五类公式与案例、既有 project overview、outline、progress 和 notes。
- Inputs not used and why: 未重新检索外部论文，因为本次未新增论文事实或引用，只重构文件内部分类判据。
- Artifacts produced: 统一后的父级总表、2.1—2.5 定义与边界、任务包、更新后的 project overview、outline、progress 和 notes。
- Verification run: 五标题存在与顺序、五行表格与列数、统一判据、旧判据清除、2.3/2.4/2.5 边界、代表案例保留、35 处 Obsidian embed 配对和新增定义禁用词检查，全部通过。安装目录未提供 `style_check.ps1`，已记录并以针对性断言替代。
- Remaining risk: 混合系统仍需依据实际推理更新式采用组合标注；仅凭论文对模块的命名无法自动归类。

## 2026-08-19｜分层方法标签与采样状态显式化

- 在章节总说明中加入 `计算拓扑 × 内层更新机制 × 候选 / 采样状态 × 评价或反馈来源`，不再让 2.x 标签承担完整方法分类。
- 将 2.1—2.5 明确限定为单条精化链的近端更新机制，并补入五种采样 / 候选状态的拓扑判定表。
- 明确 $N$ 统计参与选择、更新或跨轮传递的候选解；估计单解梯度、熵或局部统计量的临时 probes 只记为瞬时评价采样。
- 在第 3 节补强 $C=1$ 的跨候选反馈判据，并把 $N>1,R>1,C=0$ 明确写成独立多链而非 Adaptive Population。
- 跨维度索引新增“候选 / 采样状态”列；SafeBimanual 采用组合机制标签，AAC 的临时熵估计采样不再写成候选 Width。

### Spec compliance review

- 五个一级类别、2.1—2.5 的名称、编号和相对顺序均未改变：通过。
- 单链、独立多链、一次性 Best-of-$N$、反馈耦合群体和瞬时评价采样均有独立判据：通过。
- 采样方式不再单独决定 Width / Population，跨候选耦合仍以 $C$ 判定：通过。
- 跨维度索引保留全部 30 个方法条目，并为每个方法增加候选 / 采样状态：通过。

### Quality review

- 一级拓扑回答“额外计算怎样组织”，2.x 回答“单条链怎样更新”，采样状态回答“候选怎样存在”，反馈来源回答“更新依据来自哪里”，四层职责分开。
- guided diffusion 在保留随机去噪特征的同时使用生成与目标引导的组合标签；CEM 与粒子进化则通过群体状态和跨轮重采样与之区分。
- 无跨轮精化的 Width 方法不强套 2.x 标签，避免把 Selection 误写成 Refinement。
- 索引表中的候选状态采用短语式描述，未改动原论文事实、链接和训练—部署摘要。

### Capability-use audit

- Required skills: paper-orchestration、writing-core、peer-review、obsidian-markdown、verification。
- Skills actually used: paper-orchestration、writing-core、peer-review、obsidian-markdown、verification。
- Inputs consumed: 用户确认的分层方案、`任务-new.md` 的总分类说明、第 2/3 节边界与跨维度索引、既有计划和决策记录。
- Inputs not used and why: 未重新检索外部论文，因为本次只调整分类表达和索引字段，不新增论文结论或引文。
- Artifacts produced: 分层标签总说明、采样判定表、群体搜索补充边界、七列跨维度索引、新任务包及更新后的计划记录。
- Verification run: 标题顺序、分层公式、$N/C$ 判据、五种采样情形、30 行索引及七列一致性、关键案例、35 处 Obsidian embed 检查。安装目录未提供 `style_check.ps1`，以针对性断言和人工文字审阅替代。
- Remaining risk: 同一 diffusion planner 在不同实现中可能采用单链或批量独立链；完整拓扑仍应按实际部署的 $N$ 与跨链信息流标注，不能只看论文方法名。

## 2026-08-19｜第 1—3 类拓扑重叠修正

- 复核发现第 2 节一面将本类限定为 $N=1,R>1$，一面又在节内列出 Parallel Width、独立多链和 Adaptive Population，造成一级类别与跨拓扑边界混杂。
- 将第 1—3 类改为按 $R$、$C$ 互斥判定：第 1 类为 $N>1,R=1$，第 2 类为 $R>1,C=0$，第 3 类为 $N>1,R>1,C=1$。
- 第 2 节改名为“非耦合迭代精化”，明确包含 $N=1$ 的单链串行精化和 $N>1$ 的独立多链精化。
- 删除第 2 节中属于第 1、3 类的对照行，只保留本类两个子形态及瞬时评价采样的 $N$ 计数边界。
- 补充 $R$ 的操作性定义：初始 proposal 为第一轮，真实动作执行后的新观测属于下一次决策。

### Spec compliance review

- 第 1 类仍为 $N>1,R=1$：通过。
- 第 2 类统一为 $R>1,C=0$，不再同时声称只允许 $N=1$：通过。
- 第 3 类仍为 $N>1,R>1,C=1$：通过。
- 独立多链在第 2 节有单独子形态，且与一次性 Width 的 $R$ 条件不同：通过。
- 2.1—2.5 的名称、顺序、机制表和案例均保留：通过。

### Quality review

- 第 1—3 类在非分支单次决策搜索中分别对应一次生成、无跨链反馈的多轮更新和有跨链反馈的多轮更新，边界不再重叠。
- 第 2 节只比较单链与独立多链，不再把第 1、3 类重复列为自己的扩展形态。
- $R$ 的正文定义与 `a_i^{(1)}\rightarrow\cdots\rightarrow a_i^{(R)}` 的轮次下标一致。
- 第 5 类被说明为跨真实环境时刻的执行标签，避免与单次决策拓扑强行互斥。

### Capability-use audit

- Required skills: paper-orchestration、writing-core、peer-review、obsidian-markdown、verification。
- Skills actually used: paper-orchestration、writing-core、peer-review、obsidian-markdown、verification。
- Inputs consumed: 用户指出的 Width 重复问题、`任务-new.md` 第 1—3 类定义、跨维度索引和既有计划记录。
- Inputs not used and why: 未检索外部资料，因为修改对象是文档内部分类条件的逻辑一致性，不涉及新增论文事实。
- Artifacts produced: 重写后的总拓扑判据、第 2 类标题与定义、两行子形态表、$R$ 的定义、任务包和更新后的计划记录。
- Verification run: 第 1—3 类条件、旧跨拓扑表移除、2.1—2.5 保留、索引行列数和 Obsidian embed 检查。
- Remaining risk: 部分论文可能在不同部署配置下从单链变为独立多链，索引中的最终拓扑仍需按具体推理配置确认。

## 2026-08-19｜2.2 Rethinking / Iterative Reasoning 文献补充

- 通过学术检索筛选动作规划、VLA 与 embodied reasoning 论文；Exa 在后续查询中触发免费额度 429，随后改用论文官方 PMLR 与 arXiv 页面逐项核验。
- 在 2.2 新增 ECoT 与 CoA-VLA 两个案例：前者作为动作前具身推理链正例，后者作为 reasoning 与 diffusion action generation 的组合机制案例。
- 新增语义阶段判据，避免将任意自回归 token 数量机械地解释为 refinement round。
- 加入 ISR-LLM 排除边界，说明论文名中的 self-refinement 不能替代实际 validator 数据流判定。
- 跨维度索引新增 ECoT、CoA-VLA 两行，方法总数由 30 增至 32。

### Spec compliance review

- ECoT、CoA-VLA 均位于 2.2 与 2.3 标题之间：通过。
- ECoT 未被描述为完整候选生成后的显式纠错；CoA-VLA 保留 diffusion action head：通过。
- ISR-LLM 仅作为 2.2 排除边界，并按 validator 数据流归入 Verifier-Guided：通过。
- 2.1—2.5 的名称、顺序和原有案例均未改动：通过。
- 跨维度索引包含 32 个方法，全部保持七列：通过。

### Quality review

- 两个正例均由一手论文页面支撑；ECoT 的会议与出版元数据由 PMLR 核验，CoA-VLA 的正式录用信息由 IEEE/CVF 核验，机制由 CVF 正式论文与 arXiv v2 全文交叉核验。
- 正文区分了逐步生成语义状态、评价后修正候选和 diffusion 动作解码三种数据流，没有用论文标题代替机制判定。
- 2.2 新增内容未引入图片，原有 35 处 Obsidian embeds 数量保持不变。
- 安装目录中未找到 `style_check.ps1` 或同名脚本，改用标题区间、禁用短语、表格列数、方法计数和 embed 计数的针对性断言。

### Capability-use audit

- Required skills: agent-reach、paper-orchestration、evidence-driven-writing、literature-review、writing-core、peer-review、obsidian-markdown、verification。
- Skills actually used: agent-reach、paper-orchestration、evidence-driven-writing、literature-review、writing-core、peer-review、obsidian-markdown、verification。
- Inputs consumed: 用户给出的 2.2 定义、`任务-new.md` 第 2 节与跨维度索引、ECoT 的 PMLR 官方页与 arXiv 全文、CoA-VLA 的 IEEE Xplore 元数据、CVF ICCV 2025 正式论文与 arXiv v2 全文、ISR-LLM arXiv 页面。
- Inputs not used and why: ReflectVLM、PhysReflect-VLA、RoboReflect 涉及世界模型或真实执行反馈；Self-Refine 与 ISR-LLM 存在显式 feedback/validator；CoT-VLA 生成预测未来帧，因此均未作为纯 2.2 正例。
- Artifacts produced: 2.2 两个论文案例与边界句、两条索引记录、evidence map、段落 blueprint、evidence coverage、更新后的 overview、notes 与 progress。
- Verification run: 官方链接核验、2.1—2.5 标题顺序、2.2 案例区间、32 行七列索引、35 处 Obsidian embeds、禁用短语与 Git 工作区状态检查。
- Remaining risk: CoA-VLA 会随任务进度动态选择 affordance；本文只把它的 affordance reasoning 子链归入 2.2，并以组合标签保留其 diffusion 动作生成机制。

## 2026-08-19｜CoA-VLA 正式出版信息修正

- 用户补充 IEEE Xplore 正式记录后，将正文中的“arXiv 预印本”更新为 ICCV 2025。
- 增加 IEEE Xplore、CVF Open Access 与 DOI 三个正式来源；保留 arXiv v2 作为版本和方法细节的辅助核验来源。
- 正式书目信息：ICCV 2025，pp. 9759–9769，DOI `10.1109/ICCV51701.2025.00910`。
- 验证依据：CVF ICCV 2025 官方论文页的题名、作者、会议、月份、年份和页码，与用户提供的 IEEE Xplore DOI 和会议记录一致。

## 2026-08-19｜2.2 ECoT 与 CoA-VLA 论文图补充

- 从 ECoT 官方 arXiv PDF 提取 Figure 3（reasoning steps）和 Figure 4（synthetic ECoT data pipeline）。
- 从 CoA-VLA 的 CVF ICCV 2025 正式论文 PDF 提取 Figure 1（overall framework）和 Figure 2（PourTea chain-of-affordance）。
- 四幅图片以 3× PDF 渲染尺度裁剪并存入 `assets/trajectory-planning-vla-tts/`，没有重绘、改色或使用二手图源。
- 在两篇论文条目中增加“论文图解—读图—分类含义/边界”，正文 embeds 由 35 增至 39。

### Spec compliance review

- ECoT 与 CoA-VLA 各有两幅官方论文图：通过。
- 每幅图均标明原 Figure 编号，并具有读图与分类解释：通过。
- ISR-LLM 仍只是排除边界，没有被扩写为 2.2 正例：通过。
- 四个 Obsidian embeds 全部位于 2.2 与 2.3 之间并能解析到实际文件：通过。

### Quality review

- 四幅最终图片均已逐张视觉检查，无页眉、页脚、正文残留、明显黑边、拉伸或关键标签裁切。
- ECoT Figure 4 被明确限定为离线训练标注管线，不用于推断部署拓扑。
- CoA-VLA Figure 1 支撑组合机制标签；Figure 2 的真实执行时间轴未被误算为单次决策内的 refinement rounds。
- 2.1—2.5 标题顺序、32 行七列跨维度索引和原有案例保持不变。

### Capability-use audit

- Required skills: paper-orchestration、pdf、obsidian-markdown、verification。
- Skills actually used: paper-orchestration、pdf、obsidian-markdown、verification。
- Inputs consumed: 用户的补图要求、2.2 两篇案例文字、ECoT 官方 arXiv PDF、CoA-VLA CVF ICCV 2025 正式 PDF、既有图解排版样式。
- Inputs not used and why: ISR-LLM 仅为分类反例，不需要增加图；figures-diagram 不适用，因为任务要求提取原论文图而不是重新绘图。
- Artifacts produced: 4 个 PNG assets、8 段图解说明、figure slots、更新后的 task packet、overview、notes、review 与 progress。
- Verification run: PyMuPDF 整页渲染与目标裁剪、四图视觉检查、Pillow PNG 完整性与尺寸校验、四个 embed 文件映射、39 处 embeds、2.1—2.5 顺序、32 行七列索引检查。
- Remaining risk: ECoT 正式 PMLR PDF 下载连接发生截断，因此图像取自内容一致的 arXiv 官方版本；目标页已成功渲染并与论文 Figure 3/4 标题核对。

## 2026-08-19｜2.3 Evaluation-Guided Refinement 层级重构

- 将父级近端机制由五类收敛为四类：Generative、Rethinking、Evaluation-Guided 与 Objective-Guided。
- 原 2.3 Verifier-Guided 与原 2.4 World-Feedback 合并为 2.3 Evaluation-Guided Refinement；2.3.1 负责直接候选验证，2.3.2 负责世界模型介导的后果验证。
- 在统一公式中将直接路径写为 `V(o,a)`，世界模型路径写为 `E(W(o,a))`，明确世界模型负责预测、outcome evaluator 负责把预测未来转成评价反馈。
- 原 2.5 Objective-Guided 顺延为 2.4；DriveVer、CriticVLA、ReflectVLM、SC-VLA 与 SafeBimanual 等案例和图片均保留。
- 跨维度索引把四个评价引导案例统一标为 Evaluation-Guided，并继续用 Direct Verifier / World-Model-Mediated 标记反馈子链。

### Spec compliance review

- 用户要求的“把原 2.4 放入 2.3 子链”已实现：通过。
- 2.3.1 与 2.3.2 分别容纳 DriveVer/CriticVLA 和 ReflectVLM/SC-VLA：通过。
- 原 2.4 World-Feedback 平级标题与 2.5 标题已清除，Objective-Guided 现为 2.4：通过。
- World-Feedback Selection 仍留在第 1 类，未因世界模型来源被并入 refinement：通过。
- 32 行七列跨维度索引和 39 处 Obsidian embeds 全部保留：通过。

### Quality review

- 新结构按“下一候选由哪类近端算子产生”维持互斥：learned refiner 解释评价反馈属于 2.3，gradient/投影/局部求解器直接计算下一点属于 2.4。
- 世界模型没有被直接等同于 verifier；`E∘W` 被定义为 outcome verification path，反馈来源的差异没有被抹去。
- 直接评分或 world rollout 只做固定候选 argmax 时仍属于 Selection；动作真实执行后的新观测仍属于 Environment-Feedback Replanning。
- 新增正文未出现写作规范中的禁用机械过渡词；安装目录未提供 `style_check.ps1`，已用针对性语言断言替代。

### Capability-use audit

- Required skills: paper-orchestration、writing-core、peer-review、obsidian-markdown、verification。
- Skills actually used: paper-orchestration、writing-core、peer-review、obsidian-markdown、verification。
- Inputs consumed: 用户提出的层级调整、`任务-new.md` 的总流程、原 2.3/2.4/2.5 定义与案例、跨维度索引、现有 evidence map 与 review coverage。
- Inputs not used and why: 未重新检索外部论文，因为本次调整的是文件内已有机制的数据流层级，不新增论文事实、指标或录用信息。
- Artifacts produced: 四类父级机制表、2.3 统一公式、2.3.1/2.3.2 子链、重编号的 2.4、更新后的索引、task packet、blueprint、evidence/review/notes/progress。
- Verification run: 2.1—2.4 与 2.3.1—2.3.2 标题顺序、四行机制表、案例区间、旧标题清除、32 行七列索引、39 个 embed 及全部目标文件、禁用短语检查。
- Remaining risk: 某些方法同时用 imagined outcome 训练 refiner、又在部署时把该信号作为静态条件；最终标签仍需以实际推理图中评价反馈是否控制下一候选为准。

## 2026-08-20｜2.3 评价引导父类再分层

- 阶段：S5 Review → S2 Method revision。
- 用户复核指出：现有 `Evaluation-Guided Refinement` 标题语义上已经覆盖显式目标或约束提供的评价信息，因此原 2.4 不宜继续作为平级类别。
- 处理方向：2.3 提升为评价信息回流的父类；2.3.1 区分 feedback-conditioned learned update，2.3.2 区分 objective-guided numerical update；direct verifier 与 world-model-mediated outcome feedback 下沉为 2.3.1 的反馈来源变体。
- 当前状态：正文、父级机制表、交叉引用、跨维度索引与 plan 记录已同步修改。

### Spec compliance review

- 原平级 2.4 已收入 2.3，并重编号为 2.3.2：通过。
- 2.3.1 保留 learned refiner 更新；Direct Verifier 与 World-Model-Mediated Outcome Feedback 作为反馈来源变体继续可见：通过。
- DriveVer、CriticVLA、ReflectVLM、SC-VLA、SafeBimanual 及原有图片均未删除或移位到错误子链：通过。
- 固定候选 Selection、独立多链和反馈耦合群体搜索的一级拓扑边界保持不变：通过。

### Quality review

- 2.3 父级只规定评价信息回流，不再把所有评价来源等同为 verifier。
- 2.3.1 与 2.3.2 的分界改为评价到更新的转换机制：前者由 learned refiner 解释反馈，后者由数学局部求解器直接计算下一点。
- 父级机制表以三类机制家族和四条更新行表达层级，避免标题包含关系与表格并列关系冲突。
- 新增正文没有七级 Markdown 标题或写作规范中的禁用模板短语；安装目录未提供 `style_check.ps1`，已用针对性断言替代。

### Capability-use audit

- Required skills: paper-orchestration、writing-core、peer-review、obsidian-markdown、verification。
- Skills actually used: using-research-writing、paper-orchestration、writing-core、peer-review、obsidian-markdown、verification；writing-chapters 已检查但未用于章节落盘，因为本任务是现有 Obsidian 单文件 taxonomy 的局部结构返工，不是新建 `chapters/` 稿件。
- Inputs consumed: 用户对 2.3/2.4 包含关系的复核意见、`任务-new.md` 的父级机制表、1.2、2.1—2.4、跨维度索引，以及现有 outline、evidence map、review coverage 和 blueprint。
- Inputs not used and why: 未重新检索外部论文，因为本次只调整已有材料的分类层级，不新增论文事实、指标或发表信息。
- Artifacts produced: 2.3 父类定义、2.3.1 learned correction、两种反馈来源变体、2.3.2 objective-guided update、同步后的索引、task packet、blueprint、overview、outline、evidence、review、notes 与 progress。
- Verification run: 标题与来源标记顺序、旧平级 2.4 清除、四条更新行、案例区间、32 行七列索引、39 个 embed 及目标文件、Markdown 最大标题层级、禁用短语与 plan 同步检查，全部通过。
- Remaining risk: 评价来源和更新机制仍是两个正交字段；方法同时包含 learned correction 与 objective term 时需要组合标注，不能只写父级 `Evaluation-Guided`。

## 2026-08-20｜Diffusion 采样与引导独立 taxonomy（进行中）

- 阶段：S1 Evidence。
- 用户要求新增独立 diffusion 分类体系，不与现有轨迹规划 1—5 类互相套用。
- 预定主干：单链去噪、独立多样本、连续粒子相互作用、FK/SMC 加权重采样、相互作用与重采样混合；树搜索仅作为边界项。
- FK Steering 重点核验 `reward → potential → probability weights → resampling → continued denoising`，并区分 reward、potential、normalized weight 与 parent index。
- 当前状态：任务包已建立，正在核验一手来源。

## 2026-08-20｜Diffusion 采样与引导独立 taxonomy（完成）

- 阶段：S1 Evidence → S2 Structure → S3 Draft → S4 Integration → S5 Review。
- 在 `任务-new.md` 文末新增独立顶层章节，原“轨迹规划与 VLA 的测试时扩展”taxonomy 未修改。
- 新体系以 reverse transition 中实际执行的算子为判据：D0 independent sampling、D1 pathwise guidance、D2 continuous coupling、D3 FK/SMC reproduction、D4 interaction + resampling hybrid。
- FK 部分补齐 target reward tilt、potential 乘积约束、proposal importance correction、categorical / multinomial 有放回重采样、四粒子数值例子、Difference Potential telescoping、interval resampling、确定性 transition 限定及与 Best-of-$N$/top-$k$ 的对照。

### Spec compliance review

- 新章使用 D0—D4 独立编号，未复用前文 1—5 类、2.x 或 $N/R/C$：通过。
- 原规划 taxonomy 区间 SHA-256 保持 `b2566edbbff15b2873441b776a4020228249c653392d878e0a76e32451f44317`：通过。
- SafeBimanual、Particle Guidance、SPELL、EDDY、FK Steering、IMPFM 均按 diffusion sampler 数据流归类：通过。
- `reward → potential → normalized probability → resampling → continued transition` 完整可见：通过。
- 未把 particle 写成多个 diffusion 模型、未把 FK 写成 top-$k$、未把 $G_t$ 写成额外网络：通过。
- Best-of-$N$ 与显式 diffusion tree search 均保留为边界项：通过。

### Quality review

- 主要技术风险 1：简化 $G_t=\exp(\lambda r_t)$ 可能掩盖 proposal mismatch；正文已明确它只作数值示意，并补充 $p_\theta/\tau$ importance ratio。
- 主要技术风险 2：Difference Potential 的 telescoping 需要 endpoint 条件；正文已写明 $r_\phi(x_T)=0$ 或 endpoint compensation。
- 主要技术风险 3：复制后“自然分叉”并非所有 sampler 都成立；正文已区分 stochastic reverse transition 与 deterministic DDIM/ODE。
- 次要边界风险：SPELL 同时支持静态 reference 与 current-batch dynamic reference；正文已分别归入 reference-guided D1 与 coupled D2。
- 新章中的三个 Markdown 表格列数一致，display math 与 code fences 成对，最大标题层级为 6；未新增 Obsidian embed，原有 39 个 embed 保持不变。

### Capability-use audit

- Required skills: using-research-writing、paper-orchestration、evidence-driven-writing、literature-review、writing-core、peer-review、obsidian-markdown、verification、agent-reach、academic-research-suite。
- Skills actually used: 上述技能均用于任务路由、证据映射、章节蓝图、独立 taxonomy 写作、同行式技术自审、Markdown 结构检查与完成验证；agent-reach 的外部桥接此前失败后，按其降级路径使用官方 arXiv / PMLR 一手页面核验。
- Inputs consumed: 用户附件 `pasted-text.txt`、`任务-new.md` 既有 taxonomy 与 diffusion 案例、SafeBimanual、Particle Guidance、SPELL、FK Steering、EDDY、IMPFM 的一手论文页面，以及现有 plan artifacts。
- Inputs not used and why: 未把 MCTD 并入 particle taxonomy，因为它维护显式树节点与搜索统计，只作为边界；未为新章抓取论文图片，因为本次交付重点是可独立阅读的机制 taxonomy，公式、算法流与对照表已覆盖判别需求。
- Artifacts produced: task packet、project overview、outline、evidence map、evidence coverage、chapter blueprint、notes、progress 与正文独立章节。
- Verification run: 原 taxonomy hash、D0—D4 标题、FK 必需公式与边界、标题深度、表格列数、数学/代码围栏、链接、Obsidian embeds 和文件状态检查。
- Remaining risk: D0—D4 是本文操作性命名而非领域标准；EDDY 与 IMPFM 是 2026 preprints，后续版本可能调整理论限定或实现细节。

## 2026-08-20｜Diffusion taxonomy 逐篇文献扩写（进行中）

- 阶段：S1 Evidence → S2 Structure。
- 用户认可 D0—D4 分类，但指出各论文没有像前文 test-time scaling 条目一样展开。
- 已确定 11 篇逐篇卡片：DDPM、DDIM、classifier guidance、CFG、SafeBimanual、Particle Guidance、SPELL、EDDY、FK Steering、DAS、IMPFM。
- 已从一手页面核验论文状态与机制，并收集 11 幅原论文图；DAS 与 IMPFM 的错误网页缩略图已由官方 PDF Figure 1 替换，classifier guidance 补入 NeurIPS 正式论文 Figure 2。
- 当前状态：task packet、evidence map、coverage 与章节蓝图已同步，下一步写入正文并完成两阶段复核。

## 2026-08-20｜Diffusion taxonomy 逐篇文献扩写（完成）

- 阶段：S2 Structure → S3 Draft → S4 Integration → S5 Review。
- 保留现有 D0—D4 骨架，将 DDPM、DDIM、classifier guidance、CFG、SafeBimanual、Particle Guidance、SPELL、EDDY、FK Steering、DAS 与 IMPFM 扩成 11 张完整论文卡片。
- 每张卡片均包含前序工作问题、近端更新或 sampler 数据流、关键公式、核心创新、原论文图解、D0—D4 分类理由、相邻类别边界与训练—部署限定。
- 新增跨论文对照表，集中比较路径内 guidance、continuous cross-particle interaction 与 ancestry resampling。
- 新增 11 幅原论文 Figure；DAS、IMPFM 与 DDIM 的错误或不完整网页图已改用官方 PDF 精确裁图，SPELL SVG 另经浏览器渲染检查。

### Spec compliance review

- D0—D4 标题、顺序和原始判据保持不变：通过。
- 11 篇论文均具有 `前序工作问题`、`核心创新点（一句话）` 与 `论文图解`：通过。
- 11 个新增 Obsidian embeds 全部解析到实际图像文件：通过。
- FK 四粒子概率例子、Difference Potential、proposal correction、stochastic / deterministic 分叉限定均保留：通过。
- EDDY 与 IMPFM 继续标为 2026 arXiv preprint；正式 venue 未被虚构：通过。
- 原“轨迹规划与 VLA 的测试时扩展”taxonomy 区间 SHA-256 仍为 `b2566edbbff15b2873441b776a4020228249c653392d878e0a76e32451f44317`：通过。

### Quality review

- D0 通过 DDPM / DDIM 拉开 base transition、随机性与 sampler schedule；D1 通过 classifier guidance、CFG 与 SafeBimanual 拉开 learned / external objective guidance 来源。
- D2 每篇都说明 continuous state coupling 且不改变 offspring count；SPELL 的 static / dynamic reference 配置没有被压成单一标签。
- D3 明确把 DAS 的 reward gradient 限定为 guided proposal 子算子，主类由 multinomial ancestry update 决定；未误写为 D4。
- D4 仅在 continuous peer interaction 与 interaction-aware Feynman–Kac resampling 同时存在时使用，未把一般多模块 sampler 自动归入混合类。
- diffusion 章节共 685 行、31,699 个字符；64 个 display-math markers、8 个 code-fence markers 成对；4 张表列数一致；最大标题层级为 5。
- 文档 Obsidian embeds 由 39 增至 50；11 幅新增图全部完成格式解码，PNG 逐张视觉核验，SPELL SVG 经 headless browser 渲染核验。

### Capability-use audit

- Required skills: using-research-writing、paper-orchestration、evidence-driven-writing、literature-review、writing-core、peer-review、obsidian-markdown、verification、agent-reach、academic-research-suite、pdf。
- Skills actually used: 上述技能均用于任务路由、证据映射、逐篇结构蓝图、一手来源核验、官方 PDF 原图提取、学术技术写作、Markdown 集成与完成验证。
- Inputs consumed: 用户对“每篇不够详细”的反馈、现有 D0—D4 章节、前文 test-time scaling 条目样式、11 篇论文的一手 arXiv / NeurIPS / ICLR / PMLR / OpenReview 页面与论文 PDF、既有 plan artifacts。
- Inputs not used and why: 未增加更多结果型论文或二手综述，避免卡片数量扩张后稀释 sampler 机制主线；未生成重绘示意图，因为用户要求补原论文图。
- Artifacts produced: 完整扩写的 diffusion 章节、11 个论文图 assets、新 task packet、新 chapter blueprint，以及同步后的 overview、outline、evidence map、coverage、notes 与 progress。
- Verification run: 15 项自动断言全部通过，包括原 taxonomy hash、D0—D4 顺序、11 论文/字段/图片、embed 文件存在、数学与代码围栏、标题深度、4 张表、preprint 标签、图片解码；另完成 11 图视觉检查。
- Remaining risk: D0—D4 仍是本文操作性 taxonomy；EDDY 与 IMPFM 为 2026 preprints，未来版本若改变理论或算法需重新核验。OpenReview 页面可能触发浏览器验证，因此正文标题优先链接可直接访问的 arXiv /正式 proceedings。

## 2026-08-22｜SPELL 技术卡片结构返工

- 阶段：S1 Evidence → S2 Method revision → S5 Review。
- 用户要求 SPELL 恢复与相邻论文一致的“前序工作问题—动机与方法—核心创新—图解—归类—局限”结构，并明确回答论文是否直接比较 Particle Guidance。
- 正文已把原有问答式说明重组为统一技术卡片；保留 shield 公式、Figure 2 和 D1/D2 配置判定，并新增 PG 的时间关系、机制比较、Section 5.3 baseline 实验及适用范围限定。

### Spec compliance review

- SPELL 条目包含前序工作问题、核心疑问、动机与方法、原文 PG 比较、核心创新、图解、配置归类、训练—部署关系和边界局限：通过。
- 原有 Figure 2 embed、shield 公式和 D1/D2 taxonomy 未删除：通过。
- PG 被明确写为 ICLR 2024 的前序方法；SPELL 的机制、Related Work 和实验比较均有一手全文依据：通过。
- Figure 4 的结果限定在 Latent Diffusion / CC12M，未写成普遍优越性：通过。

### Quality review

- 论证顺序从问题与既有缺口推进到 shielding 数据流，再比较 PG，避免把事实核验写成脱离正文的问答。
- PG 与 SPELL 的共同点、关键机制差异、reference 范围和实验边界均保留，未用“后出”代替有效性论证。
- “soft kernel 不严格归零”明确归因于 SPELL 论文所比较的 PG baseline，避免将其泛化为所有可能的 particle potential。

### Capability-use audit

- Required skills: using-research-writing、paper-orchestration、evidence-driven-writing、obsidian-markdown。
- Skills actually used: using-research-writing、paper-orchestration、evidence-driven-writing、obsidian-markdown；学术核验使用 official arXiv/PMLR 全文证据。
- Inputs consumed: `任务-new.md` 相邻 D1/D2 技术卡片、SPELL Abstract/Introduction/Section 3/Related Work/Section 5.3/Figure 4/Appendix Tables 4—5、Particle Guidance 一手元数据与机制描述。
- Inputs not used and why: 未引入二手博客或综述，因为原论文已直接提供所需比较；未新增实验图，因为现有 Figure 2 已承担机制图解，本轮重点是正文结构和原文比较。
- Artifacts produced: 重写后的 SPELL 技术卡片、task packet、section blueprint、evidence map、evidence coverage 与 progress 记录。
- Verification run: SPELL 区间小标题、旧问答标题清除、公式与 Markdown 围栏、Figure 2 embed、PG/CC12M 限定语句及 D1/D2 交叉引用检查。
- Remaining risk: SPELL 对 PG 的性能结论来自作者的 baseline 重实现；跨模型、跨数据集或不同 potential 配置仍需独立复验。

## 2026-08-22｜SPELL 独立论文主线修正

- 阶段：S2 Method revision → S5 Review。
- 用户指出上一版过度围绕 Particle Guidance 组织 SPELL，要求恢复单篇论文介绍主线。
- 已删除“Particle Guidance 的剩余边界”和专门的 PG 比较段；前序工作改为概括 training-free diversity 方法的共同缺口，实验部分按 base-model 对照、PG/IG/CADS 并列 baselines、Pareto comparison、效率与百万级保护集组织。

### Spec compliance review

- SPELL 的问题、方法、核心创新和应用范围仍是正文主线：通过。
- Particle Guidance 不再拥有专门比较标题，只作为 Section 5.3 三个 baselines 之一：通过。
- Interval Guidance、CADS、base-model benchmark、运行效率与 1.2M ImageNet protection 均已补入：通过。
- D1/D2 配置归类、Figure 2 和 shield 公式保持不变：通过。

### Quality review

- 条目不再因用户追问 PG 而改变论文自身的叙事重心。
- 横向比较覆盖论文实际使用的主要 baselines，并将特定 Pareto 结论限制在 Latent Diffusion / CC12M。
- PG 的 joint-potential 机制仍在其独立 D2.1 卡片中完整介绍，SPELL 条目不重复展开。

### Capability-use audit

- Required skills: using-research-writing、paper-orchestration、evidence-driven-writing、obsidian-markdown、verification。
- Skills actually used: using-research-writing、paper-orchestration、evidence-driven-writing、obsidian-markdown、verification。
- Inputs consumed: 用户的叙事重心修正、SPELL Section 5.2—5.6、Figure 4、Appendix Tables 4—5、相邻论文卡片格式。
- Inputs not used and why: 未继续展开 SPELL 与 PG 的理论联系，因为该内容会再次抢占单篇介绍主线，PG 的机制已有独立卡片。
- Artifacts produced: 独立主线的 SPELL 技术卡片，以及同步更新的 task packet、blueprint、evidence map、coverage 与 progress。
- Verification run: 专门 PG 标题与旧 PG 缺口句清除、PG/IG/CADS 并列出现、base-model/效率/1.2M 结果字段、公式、图片和 D1/D2 归类检查。
- Remaining risk: Section 5.3 的横向结果仍来自作者的 baseline 重实现，不能替代跨实现复验。
