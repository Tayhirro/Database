---
title: modules（因果推断工具箱）
date: "2026-03-26"
categories:
  - 因果推断
description: 建议放：对 SCM 做操作或推理时会复用的规则与工具，例如干预、d-separation、backdoor、do-calculus、反事实。
---
# modules（因果推断工具箱）

`modules/` 主要放那些会在多张因果笔记里反复出现的可复用规则与操作。

适合放这里的内容：

- 干预语义：`do`-operator、截断因子分解、方程替换
- 图上规则：`d-separation`
- 识别准则：`backdoor`、`frontdoor`、`do-calculus`
- 查询语义：反事实、可识别性

当前已有：

- [Intervention.md](Intervention.md)
- [DSeparation.md](DSeparation.md)
- [BackdoorCriterion.md](BackdoorCriterion.md)
- [Counterfactual.md](Counterfactual.md)
- [DoCalculus.md](DoCalculus.md)
- [MediationAnalysis.md](MediationAnalysis.md)
- [Transportability.md](Transportability.md)
- [MissingDataRecovery.md](MissingDataRecovery.md)
- [CausalDiscovery.md](CausalDiscovery.md)

简单判断规则：

- 如果它在定义“对象本身是什么”，放 `structures/`
- 如果它在定义“对对象怎么操作 / 怎么推理”，放 `modules/`
