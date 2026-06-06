---
title: Output Contract Boundary
date: "2026-04-19"
categories:
  - agent
  - safety
description: "单智能体系统中的输出契约、执行授权与隐藏推理边界"
---
# Output Contract Boundary

导航：[README.md](README.md) | [索引.md](索引.md) | [agent/README.md](../README.md)

---

## 定义

Output Contract Boundary：单智能体系统中，模型输出从“语言结果”转化为“执行授权”的边界集合。

该边界规定：

- 哪些输出字段进入执行路径
- 哪些输出字段只进入展示、日志或回放路径
- 哪些输出字段可以回写到后续上下文
- 哪些输出字段必须被过滤、压缩或隔离

---

## 归类

| 维度 | 归类 |
|---|---|
| 上级主题 | [安全与对齐（Safety）](README.md) |
| 适用范围 | 单智能体工具使用系统 |
| 相邻主题 | [ActionValidation](ActionValidation.md)、[PermissionSystem](PermissionSystem.md)、[HumanInTheLoop](HumanInTheLoop.md) |
| 分析对象 | `content`、`tool_calls`、隐藏推理字段、history 回写规则 |

---

## 第一性原理

| 命题 | 含义 |
|---|---|
| 输出即授权 | 模型输出一旦被解释为 action 或 tool call，即进入环境副作用路径 |
| 文本即审计 | 可见文本是人工复核、日志解释、错误归因的最低成本载体 |
| 字段即边界 | 输出字段的类型与流转路径决定安全边界位置 |
| 回写即记忆 | 写回 history 的字段决定系统下一轮可见上下文 |
| 隔离即控制 | 原生推理、工具参数、执行摘要分离存储可降低泄露与漂移风险 |

---

## 边界框架

| 边界类型 | 判定对象 | 核心问题 | 主要风险 |
|---|---|---|---|
| 执行授权边界 | `tool_calls`、action string | 什么内容足以触发执行 | 无说明副作用、误执行、越权执行 |
| 可见说明边界 | `content` | 执行前是否存在可见说明 | 审计缺口、UI 空回合、人工复核弱 |
| 隐藏推理边界 | `reasoning_content`、`thinking_blocks` | 推理内容是否暴露、保留、回写 | provider 原生推理泄露、协议兼容性问题 |
| 上下文回写边界 | history reconstruction | 哪些字段进入下一轮上下文 | 信息丢失、隐式记忆、错误继承 |
| 模式边界 | answer-only、tool-enabled、compact、side-question | 子模式允许何种输出形态 | 模式错配、功能语义失败 |
| 产物边界 | trajectory、patch、log | 结果是否可回放、可审计 | 归因不足、恢复失败 |

---

## 单智能体运行时契约

| 契约层 | 输入 | 输出 | 判定目标 |
|---|---|---|---|
| 输入契约 | task、history、prompt、tool schema | model request | 模型基于何种上下文决策 |
| 输出契约 | model response | `content`、`tool_calls`、隐藏推理字段 | 哪些字段允许进入执行路径 |
| 执行契约 | parser、validator、permission gate | env action | 动作如何转成环境副作用 |
| 持久化契约 | step result | history、trajectory、artifact | 哪些内容回写上下文与日志 |
| 恢复契约 | timeout、invalid output、runtime failure | retry、abort、autosubmit | 错误如何迁移或终止 |

---

## 输出字段语义

| 字段 | 角色 | 默认用途 | 风险 |
|---|---|---|---|
| `content` | 可见文本 | 展示、解释、上下文回写 | 空文本 turn、说明不足、伪解释 |
| `tool_calls` | 执行授权信号 | 触发工具调用或动作生成 | 无说明执行、参数误配 |
| `reasoning_content` | 文本化隐藏推理 | 调试、摘要、内部展示 | 推理泄露、与 `content` 不一致 |
| `thinking_blocks` | 结构化隐藏推理 | provider 协议保真、块级回放 | 协议依赖、缓存计数偏差、隐式记忆 |
| `thought` | 运行时内部思考字段 | parser 输出、轨迹展示 | 与实际执行信号脱钩 |

---

## 设计型

| 设计型 | 授权信号 | 文本要求 | 隐藏推理处理 | 优点 | 风险 |
|---|---|---|---|---|---|
| 工具优先型 | `tool_calls` 即可执行 | 可选 | 与 `content` 分离保存或丢弃 | 贴合 function calling、延迟低 | 审计弱、无文本执行 |
| 文本耦合型 | `content + tool` 共同构成可执行回合 | 强制 | 常转成可见说明文本 | 回放清晰、人工监督强 | token 成本高、说明文本可伪造 |
| 契约分离型 | 工具字段 + 摘要字段 | 可选 | 执行摘要与原生推理分层存储 | 控制面与展示面分离 | 适配层复杂、字段同步约束高 |
| 模式约束型 | 由子模式决定 | 依模式变化 | 在特定模式下隐藏、拒绝或清空 | 子流程边界清晰 | 模式切换复杂、模式错配易失败 |

---

## 典型失效模式

| 失效模式 | 触发条件 | 后果 |
|---|---|---|
| Tool-only turn | 输出只有工具调用，没有可见文本 | 执行发生但缺少说明 |
| Text-only turn | 输出只有解释，没有 action | 对话继续但任务停滞 |
| Empty assistant content | assistant content 为空 | API 拒绝、状态损坏或 transcript 异常 |
| Whitespace-only text | 文本仅含空白字符 | 无语义输出、需清洗 |
| Hidden reasoning leak | 原生推理字段被回写或展示 | 推理泄露、合规风险 |
| Writeback mismatch | `thought` / `reasoning` 与 `content` 回写策略不一致 | 下一轮上下文漂移 |
| Tool summary drift | 可见摘要与真实 tool input 不一致 | 审计错误、复盘失真 |

---

## 控制手段

| 控制手段 | 作用 |
|---|---|
| Parser validation | 校验输出格式、工具数量、参数合法性 |
| Permission system | 在执行前增加策略审查与人工授权 |
| Action validation | 检查命令、参数、目标路径、危险级别 |
| Sandboxing | 限制工具副作用范围 |
| Human-in-the-loop | 在关键动作前插入人工确认 |
| Placeholder injection | 为非最终 assistant 空消息补占位文本 |
| Mode-specific rules | 对问答模式、压缩模式、side-question 模式设定不同契约 |
| Transcript filtering | 清理空白文本、孤立 thinking、重复 tool blocks |

---

## 实例映射

| 系统 | 设计型 | 关键特征 |
|---|---|---|
| SWE-agent | 工具优先型 | `tool_calls` 可在 `message=""` 时触发执行；隐藏推理与 `content` 分离 |
| Claude Code 主对话工具回合 | 契约分离型 / 模式约束型混合 | 工具回合合法；assistant 内容与块级 transcript 分离 |
| Claude Code `/btw` | 模式约束型 | 仅接受直接回答；tool-only 视为模式不满足 |
| Claude Code compact | 模式约束型 | 压缩子流程要求可提取 text；无 text 触发 fallback |

---

## 与其他安全主题的关系

| 主题 | 关系 |
|---|---|
| PermissionSystem | 决定执行授权边界后的人工或策略许可 |
| ActionValidation | 决定工具参数与动作是否合法 |
| Sandboxing | 决定副作用被限制在何种环境范围 |
| HumanInTheLoop | 决定是否将无文本执行转化为人工确认流程 |
| Alignment | 决定模型是否生成符合意图的说明与动作 |

---

## 最小判定表

| 问题 | 判定项 |
|---|---|
| 是否允许无文本执行 | `tool_calls` 是否单独构成执行授权 |
| 是否保留隐藏推理 | `reasoning_content` / `thinking_blocks` 是否保存 |
| 是否回写隐藏推理 | 隐藏推理字段是否进入下一轮 history |
| 是否要求摘要文本 | `content` 是否为必填字段 |
| 是否允许模式例外 | 子模式是否重写输出契约 |
| 是否允许空 assistant message | transcript 层是否补占位或过滤 |

