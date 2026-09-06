---
title: 分解理论：UFD / PID / Euclidean Domain（Factorization）
date: "2026-06-19"
categories:
  - math
description: 在整环之上，按"分解行为有多好"细分出一条层级链：Euclidean Domain ⇒ PID ⇒ UFD ⇒ GCD Domain ⇒ Integral Domain。
---
# 分解理论：UFD / PID / Euclidean Domain（Factorization）

## 1. 一句话

在整环之上，按"分解行为有多好"细分出一条层级链：
Euclidean Domain ⇒ PID ⇒ UFD ⇒ GCD Domain ⇒ Integral Domain

每一级都保证更强的分解/整除性质。

## 2. 基本概念

### 2.1 整除与相伴
- `a | b`（`a` 整除 `b`）：存在 `c` 使 `b = ac`
- **相伴（associate）**：`a ~ b` ⟺ `a = ub` 其中 `u` 是可逆元（unit）。相伴是最粗的"差不多一样"
- **可逆元（unit）**：有乘法逆元的元素。`Z` 中可逆元只有 `±1`；域中所有非零元都是可逆元

### 2.2 不可约元与素元
- **不可约元（irreducible）**：`p` 非零非可逆，且 `p = ab ⇒ a` 可逆或 `b` 可逆（不能拆成两个非平凡因子）
- **素元（prime）**：`p` 非零非可逆，且 `p | ab ⇒ p | a` 或 `p | b`

关系：
- 素元 ⇒ 不可约元（在整环中恒成立）
- 不可约元 ⇏ 素元（反例：`Z[√-5]` 中 `2` 不可约但不是素元，因为 `2 | (1+√-5)(1-√-5) = 6` 但 `2` 不整除任一因子）
- 在 UFD 中：不可约元 ⇔ 素元（两者合并）

## 3. 层级链（从强到弱）

### 3.1 Euclidean Domain（欧几里得整环）
整环 `R` 上存在**欧几里得函数** `φ: R\\{0} → N`，使得对所有 `a, b∈R`（`b≠0`）：
- 存在 `q, r` 使 `a = bq + r`，且 `r = 0` 或 `φ(r) < φ(b)`

即可以做"带余除法"。
- 例子：`Z`（`φ(n) = |n|`）、`k[x]`（`φ(f) = deg(f)`）、`Z[i]`（`φ(a+bi) = a²+b²`）

### 3.2 PID（主理想整环，Principal Ideal Domain）
整环 `R` 中每个理想都是主理想（由单个元素生成）。
- Euclidean Domain ⇒ PID（用欧几里得算法证：取理想中 `φ` 值最小的元素即可生成整个理想）
- 例子：`Z`、`k[x]`、`Z[i]`
- 反例（PID 但不是 ED）：`Z[(1+√-19)/2]`（存在但不常用）

### 3.3 UFD（唯一分解整环，Unique Factorization Domain）
整环 `R` 中每个非零非可逆元都可以写成不可约元的乘积，且分解在相伴意义下**唯一**（因子排列和相伴除外）。
- PID ⇒ UFD（关键步骤：PID 中每个不可约元都是素元）
- 在 UFD 中：不可约元 ⇔ 素元
- 例子：`Z`、`k[x]`、`k[x,y]`（注意 `k[x,y]` 是 UFD 但不是 PID——`(x,y)` 不是主理想）
- **Gauss 定理**：`R` 是 UFD ⇒ `R[x]` 也是 UFD

### 3.4 GCD Domain（最大公因子整环）
整环中任意两个元素都有最大公因子（gcd）。
- UFD ⇒ GCD Domain
- 反例：存在 GCD Domain 不是 UFD

## 4. 层级总结

```
Euclidean Domain    （可以做带余除法）
      ⊆
    PID              （每个理想由一个元素生成）
      ⊆
    UFD              （唯一分解；不可约 ⇔ 素）
      ⊆
  GCD Domain         （gcd 恒存在）
      ⊆
Integral Domain      （无零因子）
```

每上升一级获得的能力：
- ED：辗转相除法、Bézout 等式
- PID：不可约元都是素元、非零素理想都是极大理想
- UFD：唯一分解定理、不可约 ⇔ 素

## 5. 与其他模块的连接
- 整环定义见 `math/离散/structures/two-op/IntegralDomain.md`
- 域的定义见 `math/离散/structures/two-op/Field.md`（域一定是 ED/PID/UFD）
- 极大理想/素理想见 `math/离散/modules/Subobject.md`（PID 中非零素理想 ⇔ 极大理想）
- 多项式环 `k[x]` 是最典型的 ED，见 `math/离散/structures/two-op/PolynomialRing.md`
- 极小多项式唯一性依赖 `F[x]` 是 PID，见 `math/离散/modules/FieldTheory.md`
