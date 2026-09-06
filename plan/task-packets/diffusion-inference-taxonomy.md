# 任务包：Diffusion 采样与引导的独立分类体系

## Task Packet

- Scope: 在 `Database/任务-new.md` 中新增一套独立于“轨迹规划与 VLA 测试时扩展”1—5 类的 diffusion-specific taxonomy，解释单链、独立多样本、连续粒子相互作用、Feynman–Kac / SMC 加权重采样与混合粒子控制，并重点展开 FK Steering 的概率重采样数据流。
- Files to read: 用户附件 `pasted-text.txt`；`Database/任务-new.md` 的现有 diffusion 案例、主 taxonomy 与文末位置；五篇候选论文的一手页面或全文。
- Files allowed to edit: `Database/任务-new.md`；`Database/plan/project-overview.md`、`outline.md`、`progress.md`、`notes.md`、`evidence-map.md`、`review/evidence-coverage.md`；本任务包与对应 blueprint。
- Required skills: using-research-writing、paper-orchestration、evidence-driven-writing、literature-review、writing-core、peer-review、obsidian-markdown、verification、agent-reach、academic-research-suite。
- Evidence/data inputs: Particle Guidance（arXiv:2310.13102）、SPELL（arXiv:2410.06025）、FK Steering / A General Framework for Inference-time Scaling and Steering of Diffusion Models（arXiv:2501.06848 / PMLR 267）、EDDY（arXiv:2605.06553）、IMPFM（arXiv:2607.01144），以及 SafeBimanual 的 guided denoising 公式作为单链外部梯度引导例子。
- Required artifacts: 独立 taxonomy 总定义与判别表；术语表；单链和多链边界；continuous interaction、marginal-preserving interaction、FK/SMC resampling、hybrid interaction + resampling 子类；FK Steering 算法步骤、potential 与 reward 的区别、Difference Potential 的 telescoping 解释、与 terminal Best-of-N 的区别；同步后的 plan 记录。
- Rejection checks: 不得修改或重新编号现有规划 taxonomy；不得用现有 `N/R/C` 标签替代 diffusion taxonomy；不得把 particle 写成多个 diffusion 模型；不得把 FK resampling 写成 top-k；不得把 `G_t` 写成额外网络；不得把示意式 `G_t=exp(λr_t)`冒充论文唯一公式；不得把尚未正式发表的 2026 工作写成已录用论文。
- Validation commands: 检查新章位于现有跨维度索引之后且主 taxonomy 文本未改；检查五类标题和 FK 公式；检查全部引用链接；检查 Markdown 标题不超过六级、表格列数、Obsidian embeds、禁用模板短语、evidence map、blueprint、两阶段 review 与 capability-use audit。

## Stage

- S5 Complete：一手来源核验、evidence map、chapter blueprint、正文、spec review、quality review 与验证均已完成。
