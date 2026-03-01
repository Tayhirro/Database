---
title: Obsidian Skills 总览
aliases:
  - obsidian-skills
  - Obsidian AI Skills
tags:
  - obsidian
  - ai/skills
  - codex
status: active
source: https://github.com/kepano/obsidian-skills
created: 2026-03-02
---

# Obsidian Skills 总览

> [!summary]
> `obsidian-skills` 是一组给 AI agent 用的技能说明，不是普通 Obsidian 插件。
> 它的作用是让像 Codex 这样的 agent 更准确地理解和操作 Obsidian 生态里的文件格式、命令行工具和常见工作流。

## 1. 仓库目标

这个仓库主要解决一个问题：

AI 虽然会写普通 Markdown，但它默认并不理解 Obsidian 的一些专有能力，比如：

- `[[双链]]`
- `![[嵌入]]`
- `> [!note]` callout
- `.base` 文件
- `.canvas` 文件
- `obsidian` CLI 命令
- 把网页提取成干净 Markdown 再写入笔记

所以这个仓库提供了一组专门的 skill，让 agent 在处理 Obsidian 内容时更像一个“懂 Obsidian 的助手”。

## 2. Skills 列表

1. [[#obsidian-markdown]]
2. [[#obsidian-bases]]
3. [[#json-canvas]]
4. [[#obsidian-cli]]
5. [[#defuddle]]

## 3. 每个 Skill 是干什么的

### obsidian-markdown

> [!info]
> 用来创建和编辑 Obsidian 风格 Markdown。

适用场景：

- 写 Obsidian 笔记
- 生成 frontmatter 属性
- 使用 `[[wikilink]]`
- 使用 `![[embed]]`
- 使用 callout
- 使用块引用和块 ID
- 组织标签、别名、注释等内容

它能帮助 agent 正确输出例如：

```md
---
title: Example Note
tags:
  - obsidian
  - demo
---

# Example

参见 [[Another Note]]

> [!tip]
> 这是一个提示框。

![[image.png]]
```

核心价值：

- 让 AI 输出的不是“普通 Markdown”，而是“Obsidian 可直接用的 Markdown”
- 更适合搭建知识库、文档库、项目笔记

### obsidian-bases

> [!info]
> 用来创建和编辑 Obsidian 的 `.base` 文件。

适用场景：

- 做类似数据库视图的笔记筛选
- 配置 table、cards、list、map 等视图
- 给笔记集合加过滤条件
- 写公式和汇总逻辑

它主要处理的不是普通笔记，而是这种结构化配置：

```yaml
filters:
  and:
    - 'status == "done"'

formulas:
  score: 'priority * 10'

views:
  - type: table
    name: Tasks
    order:
      - file.name
      - status
      - formula.score
```

核心价值：

- 让 AI 帮你搭建“笔记数据库视图”
- 适合任务面板、项目面板、阅读清单、客户清单等场景

### json-canvas

> [!info]
> 用来创建和编辑 Obsidian Canvas 的 `.canvas` 文件。

适用场景：

- 做脑图
- 做流程图
- 做信息架构图
- 做研究图谱
- 批量生成 canvas 节点和连线

它操作的内容本质上是 JSON，例如：

```json
{
  "nodes": [
    {
      "id": "6f0ad84f44ce9c17",
      "type": "text",
      "x": 0,
      "y": 0,
      "width": 320,
      "height": 180,
      "text": "Start"
    }
  ],
  "edges": []
}
```

核心价值：

- 让 AI 不只是写文字，还能直接生成可视化画布
- 适合需求梳理、知识地图、产品流程整理

### obsidian-cli

> [!info]
> 用来通过命令行操作正在运行的 Obsidian。

适用场景：

- 读取某个笔记
- 创建新笔记
- 搜索 vault
- 添加任务
- 修改属性
- 调试插件
- 让 agent 直接操作你的 Obsidian 工作区

典型命令形式：

```bash
obsidian read file="My Note"
obsidian create name="New Note" content="# Hello"
obsidian search query="project"
obsidian property:set name="status" value="done" file="Task Note"
```

核心价值：

- 不只是“生成内容”，而是“直接操作 Obsidian”
- 适合自动化写日报、记会议纪要、整理任务、开发插件

> [!warning]
> 这个 skill 依赖 Obsidian CLI，并且通常要求 Obsidian 正在运行。

### defuddle

> [!info]
> 用来从网页中提取干净的正文 Markdown。

适用场景：

- 抓取文档页面
- 抓取博客文章
- 去掉导航栏、广告、杂项内容
- 降低 token 消耗
- 为后续总结、摘录、入库做准备

典型命令形式：

```bash
defuddle parse https://example.com/article --md
```

核心价值：

- 把“乱网页”变成“干净 Markdown”
- 适合知识采集、阅读整理、网页转笔记

## 4. 这 5 个 Skill 的关系

可以把它们理解成一条工作链：

```text
网页内容 -> defuddle -> 干净 Markdown -> obsidian-markdown -> 结构化笔记
结构化笔记 -> obsidian-bases -> 数据视图
结构化笔记 -> json-canvas -> 可视化画布
运行中的 Obsidian -> obsidian-cli -> 自动化操作
```

也就是说：

- `defuddle` 负责取内容
- `obsidian-markdown` 负责写内容
- `obsidian-bases` 负责做数据化视图
- `json-canvas` 负责做可视化表达
- `obsidian-cli` 负责真正和 Obsidian 应用交互

## 5. 最适合的使用方式

### 笔记写作场景

优先使用：[[#obsidian-markdown]]

### 做数据库视图

优先使用：[[#obsidian-bases]]

### 做画布脑图

优先使用：[[#json-canvas]]

### 自动化操作 Obsidian

优先使用：[[#obsidian-cli]]

### 从网页采集资料

优先使用：[[#defuddle]]

## 6. 一句话总结

> [!quote]
> `obsidian-skills` 不是给人点按钮用的插件集合，而是给 AI agent 的“Obsidian 专业说明书”。

%% 如果以后要继续拆，可以把每个 skill 单独写成一页 %%
