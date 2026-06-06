---
title: d-separation
date: "2026-03-26"
categories:
  - 因果推断
description: d-separation 是 DAG 上判断条件独立的图规则，是 backdoor 等识别准则的基础。
---
# d-separation

## 1. 一句话

- `d-separation` 是在图上判断“给定一组变量后，一条路径是否被阻断”的规则。

## 2. 三种基本结构

### 2.1 链（chain）

$$
X \to Z \to Y
$$

或

$$
X \leftarrow Z \leftarrow Y
$$

- 若对中间点 `Z` 条件化，这条路径被阻断。

### 2.2 叉（fork）

$$
X \leftarrow Z \to Y
$$

- 若对共同原因 `Z` 条件化，这条路径被阻断。

### 2.3 碰撞点（collider）

$$
X \to Z \leftarrow Y
$$

- 默认这条路径是阻断的。
- 但一旦对 `Z` 或 `Z` 的后代条件化，这条路径会被打开。

## 3. 判定直觉

- 一条路径只要有一个非碰撞点被条件化，就可能被阻断。
- 一条路径只要有一个碰撞点没被激活，就保持阻断。
- 两个变量被一切路径都阻断时，就说它们在给定集合 `S` 下 `d-separated`。

## 4. 和条件独立的关系

- 在常见的无环、马尔可夫型因果模型里：

$$
X \perp Y \mid Z
$$

通常可由图上的 `d-separation` 读出来。

- 但图只是 `SCM` 的一个视图；严格语义仍来自 `SCM`。

## 5. 为什么它重要

- 它是 backdoor / frontdoor / do-calculus 的图基础。
- 很多“该不该控制这个变量”的问题，本质上都在问某些路径有没有被错误打开。

## 6. 常见坑

- 看到相关就想控制变量，容易把 collider 打开
- 只记公式，不记三种基本结构
- 忘了“碰撞点的后代被条件化也会开路”

## 7. 相关页

- 对象语义：见 [../structures/StructuralCausalModel.md](../structures/StructuralCausalModel.md)
- 调整准则：见 [BackdoorCriterion.md](BackdoorCriterion.md)
