# 任务包：第 1—3 类拓扑重叠修正

## Scope

- 修复第 2 节同时声明 $N=1$、又在节内列入 Width 与 Adaptive Population 的层级矛盾。
- 用 $R$ 与 $C$ 将非分支单次决策搜索划成互斥的第 1—3 类。
- 将第 2 类改为 $R>1,C=0$ 的非耦合迭代家族，并在其中区分单链与独立多链。

## Files to read

- `Database/任务-new.md`
- `Database/plan/project-overview.md`
- `Database/plan/outline.md`
- `Database/plan/progress.md`
- `Database/plan/notes.md`

## Files allowed to edit

- 上述五个文件
- 本任务包

## Required skills

- paper-orchestration
- writing-core
- peer-review
- obsidian-markdown
- verification

## Rejection checks

- 第 2 节不得继续同时写“本类 $N=1$”和“$N$ 条独立链属于本类”。
- 第 2 节的局部判定表不得再列 Parallel Width 或 Adaptive Population。
- 第 1 类必须保持 $N>1,R=1$；第 3 类必须保持 $N>1,R>1,C=1$。
- 独立多链必须明确为 $N>1,R>1,C=0$，不能与一次性 Width 混同。
- 保留 2.1—2.5 的机制名称、案例、公式和相对顺序。

## Validation commands

- 检查第 1—3 类条件在非分支情形下互斥。
- 检查第 2 节只出现其两个子形态：单链与独立多链。
- 检查旧的五行跨拓扑表已移除。
- 检查 2.1—2.5、30 行跨维度索引和 35 处 Obsidian embed 均保留。
