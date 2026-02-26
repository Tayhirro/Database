---
title: 域论（Field Theory）
date: "2026-01-21"
categories:
  - math
description: "域（定义/例子/速查）：见 math/离散/structures/two-op/Field.md 多项式环：F[x]，理想 (p(x))，商 F[x]/(p)（与 math/离散/modules/Quotient.md 呼应）"
---
# 域论（Field Theory）

## 1. 预备：域与多项式
- 域（定义/例子/速查）：见 `math/离散/structures/two-op/Field.md`
- 多项式环：`F[x]`，理想 `(p(x))`，商 `F[x]/(p)`（与 `math/离散/modules/Quotient.md` 呼应）

## 2. 域扩张（Field Extension）
设 `F ⊆ K` 且 `F, K` 都是域，则称 `K/F` 为域扩张。
- 扩张次数：`[K:F] := dim_F K`（把 `K` 当作 `F`-向量空间）
- 塔式公式：若 `F ⊆ E ⊆ K`，则 `[K:F] = [K:E]·[E:F]`
- 生成扩张：`F(α)` 表示把 `α` 加进来后得到的最小子域

## 3. 代数/超越，极小多项式
- `α` 对 `F` **代数**：存在非零 `p(x)∈F[x]` 使 `p(α)=0`
- `α` 对 `F` **超越**：不存在这样的多项式
- 极小多项式 `m_{α,F}(x)`：使 `m(α)=0` 的首一不可约多项式；并且 `F(α) ≅ F[x]/(m)`

## 4. 分裂域与正规/可分（接口级）
给定 `f(x)∈F[x]`：
- 分裂域：包含 `f` 全部根的最小扩张 `E/F`
- 常用性质（后续展开时再补证明/例子）：
  - 正规（normal）：`E` 是某个多项式的分裂域（等价口径之一）
  - 可分（separable）：根“没有重数”的可控性条件（特征 `0` 下一般都可分）

## 5. Galois 理论（你最终想要的主线）
设 `E/F` 是有限 Galois 扩张：
- Galois 群：`Gal(E/F)`（`E` 上保持 `F` 不动的域自同构群）
- 基本对应（大定理）：中间域 `L` 与子群 `H` 之间的反向对应  
  `L  ↔  Gal(E/L)`，`H ↔ E^H`
- 典型用途：用群论刻画“多项式是否可用根式解”

## 6. 有限域（应用常见入口）
- 结构：`F_{p^n}` 存在且在同构意义下唯一
- 构造：取 `F_p[x]` 的不可约多项式 `f`，令 `F_{p^n} ≅ F_p[x]/(f)`

## 7. 与其他模块的连接
- 线性代数：`[K:F]` 是维数（见 `math/线性代数/README.md`）
- 商对象/理想：`F[x]/(m)` 这类构造的统一语言（见 `math/离散/modules/Quotient.md`）
- 群论：Galois 群是群（见 `math/离散/structures/one-op/Group.md`）

