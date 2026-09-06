# SPELL 条目重写蓝图

### Paragraph group 1
- Role: 前序工作问题与研究缺口
- Main claim: diffusion 的批内重复和训练图像近复制不能由生成后过滤高效解决，已有 training-free diversity controls 也未统一覆盖任意 reference set。
- Evidence IDs: SPELL、SPELL-BASELINES
- Contrast or transition: 从事后丢弃和持续排斥过渡到预测终局上的按需 shielding。
- Forbidden content: 不把任何单一 baseline 写成 SPELL 的唯一前序工作；不把 SPELL 的所有配置都归为 D2。

### Paragraph group 2
- Role: 方法与关键公式
- Main claim: SPELL 用 predicted clean output 检查 shield violation，并施加达到半径边界所需的局部 correction。
- Evidence IDs: SPELL
- Contrast or transition: 从统一机制过渡到 reference 来源决定的 D1/D2 配置。
- Forbidden content: 不把 correction 写成训练参数更新或 resampling。

### Paragraph group 3
- Role: 实验结果与基线
- Main claim: 论文先比较 base model 加入 SPELL 前后，再把 PG、IG、CADS 作为并列 training-free baselines，并另外验证运行效率与百万级静态保护集。
- Evidence IDs: SPELL、SPELL-BASELINES
- Contrast or transition: 从自身 benchmark 过渡到横向 Pareto comparison，再说明规模与限制。
- Forbidden content: 不设置专门的 PG 比较段；不声称 SPELL 在所有任务、模型和指标上普遍优于其他方法。

### Paragraph group 4
- Role: 核心创新、图解、分类与局限
- Main claim: 同一 shielding 机制按 reference 来源分别表现为 D1 或 D2，并且不涉及模型重训或 ancestry reproduction。
- Evidence IDs: SPELL、DIFF-LOCAL
- Contrast or transition: 连接后续 D2 Particle Guidance 条目。
- Forbidden content: 不删除现有 Figure 2；不改变 D0—D4 taxonomy。
