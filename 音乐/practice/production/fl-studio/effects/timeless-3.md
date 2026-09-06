---
title: "Timeless 3"
date: "2026-08-18"
updated: "2026-08-25"
categories:
  - 音乐
tags:
  - music/practice/production
  - music/practice/delay
description: FabFilter Timeless 3 完整界面说明：延迟、反馈、Taps、滤波器、五类效果、六类调制源、预设与 Send／Insert 用法。
type: concept
---
# Timeless 3

导航：[[音乐/practice/production/fl-studio/README|FL Studio 索引]]｜[[音乐/practice/production/延迟与空间|延迟与空间]]

## 这整个页面到底是什么

Timeless 3 不是只有“时间、反馈、干湿”三个旋钮的普通 Delay。它是一套**立体声延迟器 + 多抽头延迟 + 反馈回路音色塑形 + 模块化调制系统**：原声进入延迟缓冲区，延迟声可经过 Drive、Lo-Fi、Diffuse、Dynamics、Pitch 和最多 6 个滤波器；其中一部分送到湿声输出，另一部分由 Feedback / Cross Feedback 再送回延迟输入，形成下一轮重复。底部的 Slider、XY、XLFO、包络和 MIDI Source 并不直接发声，而是让上面几乎所有参数自动变化。

> [!info] 截图最上方不是 Timeless 3
> 截图顶部那条深灰色工具栏属于 **FL Studio Wrapper**，负责插件启用、Wrapper 设置和宿主混音电平等。FabFilter 自己的界面从 `fabfilter Timeless³` 标志所在的绿色标题栏开始。一般调效果时优先使用 Timeless 3 内部的 `WET / MIX`；不要把 Wrapper 的混音旋钮和插件内部 `MIX` 当成同一个参数。

### 一眼看懂信号结构

```text
输入 ─┬─→ Dry 直通信号 ───────────────────────────────┐
      │                                               │
      └─→ Drive / Delay Buffer → 其余 FX → Filters ──┼─→ Wet Level → Mix → 输出
                                  │                    │
                                  └─ Feedback / Cross ┘（返回延迟输入）

Taps：从 Delay Buffer 的不同时间点取出额外副本，混入 Wet 输出
Modulation：不走音频；用控制信号改变 Delay、Filter、FX、Wet 等参数
```

这是便于操作的概念图，不是逐采样级的内部电路图。最重要的两点是：**滤波器位于 Delay 之后、反馈返回输入之前**，因此每次重复都会继续被滤；Pitch 则可选择放在反馈回路内或回路外。

## 页面区域总览

| 位置 | 区域 | 主要任务 |
| --- | --- | --- |
| 插件顶部 | 工具栏 | 撤销／重做、A/B 对比、复制状态、浏览预设、Help、全屏 |
| 左上 | Delay Display / TAPS | 看见输入与每轮回声；编辑主延迟，或增加最多 15 个附加 Tap |
| 右上 | Filter Display | 查看频谱与总响应；建立、编辑和路由最多 6 个滤波器 |
| 左中 | FX Controls | Drive、Lo-Fi、Diffuse、Dynamics、Pitch 五类反馈音色塑形 |
| 正中 | Delay / Feedback | 延迟时间、左右时间差、同步、Freeze、Ping-Pong、反馈与交叉反馈 |
| 右中 | Delay Output | Stereo Width、Wet Level / Pan、Dry/Wet Mix 与 Mix Lock |
| 下方 | Modulation | 添加调制源，把调制拖到任意可调目标，并管理 Modulation Slots |
| 最底栏 | Global / I/O | MIDI Learn、L/R 或 M/S、自动静音自激、全局旁通、输入输出与缩放 |

## 全界面逐区说明

### 顶部工具栏：状态、预设与帮助

| 控件 | 作用 |
| --- | --- |
| Undo / Redo | 撤销或恢复最近的参数操作。 |
| A/B | 在 A、B 两套完整插件状态间切换，适合比较两个方案。 |
| Copy | 把当前 A 或 B 的状态复制到另一边；先复制再改，才能做公平对比。 |
| 左／右箭头 | 切换上一个、下一个预设。 |
| 中间预设名 | 打开预设浏览器；截图中的 `Saturated Empty Hall RV` 才是当前加载的具体预设名。 |
| Help | 打开在线帮助、版本和插件信息等。 |
| 右上四角图标 | 进入或退出全屏。界面缩放入口还在最底栏右侧。 |

### 左上：Delay Display 与 TAPS

#### 普通 Delay Display

- 最左侧竖条是 Dry 输入；有声音进入时会点亮。
- 后续竖条是延迟声和反馈重复：上半部表示左声道，下半部表示右声道。
- 横向距离表示回声出现的时间，竖向长度／亮度帮助观察电平与反馈衰减。
- 在显示区抓住回声条**左右拖**可改 Delay Time，**上下拖**可改 Feedback。
- 降低 Stereo Width 时，左右回声条会向中间靠拢，表示输出逐渐趋向 Mono。

#### TAPS：附加抽头编辑模式

`TAPS` 是“从同一个延迟缓冲区的不同位置取出额外回声”，不是再串联 15 台 Delay。单击左上 `TAPS` 进入 Tap Edit；最右边是主 Tap，横轴改为主延迟时间的 `0%–100%`。除主 Tap 外还能建立**最多 15 个附加 Tap**。

每个 Tap 可设置：

| 参数 | 含义 |
| --- | --- |
| Time Factor | 它位于主 Delay Time 的百分之多少；主时间改变时，Tap 保持相对位置。 |
| Level | 该 Tap 混入 Wet 输出的音量。 |
| Pan | 该 Tap 的左右位置。 |
| Bypass / Delete | 暂时关闭或删除 Tap；主 Tap也可调 Level、Pan 或禁用。 |

建立 Tap 有三种方法：

1. 在显示区空白处双击；第二次按住即可直接拖位置和电平。
2. 鼠标移到显示区顶部，单击浮出的 `+`，以 `0 dB` 建立 Tap。
3. 右键空白处，选择 `Add Tap`。

编辑时可单选、框选，`Ctrl` 多选，`Shift` 连选；直接拖动改变 Time Factor 和 Level，`Ctrl + 横向拖动`改变 Pan。右键菜单还能随机化、等距排列、做上升／下降音量坡、重置 Pan 或删除全部 Tap。Tap 的 Time Factor、Level、Pan 也都能接收调制。

### 右上：Filter Display

这里同时显示输入频谱、单个滤波器曲线和粗黄色总响应曲线。最多可添加 **6 个 Filter**；滤波器在 Delay 之后、Feedback 返回之前，所以反馈越多，同一段声音被滤得越多。

| 类型 | 性格与用途 |
| --- | --- |
| Low-pass / High-pass / Band-pass | 模拟式、非线性、可自激且带内部饱和；用来让重复逐渐变暗、变薄或聚焦。 |
| Bell / Notch / Shelf | 干净的 EQ 型滤波器；用于精确提升、削减、挖频或倾斜音色。 |

添加与编辑：

- 抓住黄色总曲线上下拖，或在背景双击／`Ctrl + 单击`，即可建立 Filter；落点位置会帮助插件推断类型。
- 拖圆点：横向改 Frequency，纵向改 Gain；滚轮改 LP/HP Slope 或其他形状的 Q。
- `Ctrl + 横向拖`改 Filter Pan，让左右声道的中心频率产生偏移；`Shift` 精调，`Alt` 限制为单一方向。
- 选中后出现精确参数框，可直接输入 Frequency、Gain、Q、Pan，并可改 Shape / Slope、Bypass 或 Delete。
- Frequency、Gain、Q、Pan 都可调制；圆点旁的彩色小点表示已有调制连接。
- 模拟式 LP / HP / BP 还可选择多种 Style；`Clean` 是无饱和的干净版本。

底部 Filter Routing 有三种：

| Routing | 实际含义 |
| --- | --- |
| Serial | 所有启用的 Filter 依次串联；截图当前就是此模式。 |
| Parallel | 所有 Filter 接收同一份 Delay 输出，结果再相加。 |
| Per-channel | 奇数 1/3/5 处理左声道，偶数 2/4/6 处理右声道；M/S 模式下则对应两条内部通道。 |

> [!warning] 关于 `Auto Mute Self-Osc`
> 模拟式滤波器在高共振时可以自激，即没有新输入也继续鸣叫。底栏打开 `Auto Mute Self-Osc` 后，当滤波级输入静音时会自动压住这种持续自激；做无人值守播放或普通混音时建议开启，刻意做 Drone／自激音色时再关闭。

### 左中：五个 FX Controls

这五个旋钮不是五种 Delay Time，而是给**延迟声本身**继续加工。先记住一句人话：

| 效果 | 最直白的理解 | 拧大后主要听什么 |
| --- | --- | --- |
| Drive | 把回声推得更“过载” | 更厚、更热、更脏，严重时出现失真 |
| Lo-Fi | 故意降低数字音质 | 颗粒、毛刺、金属感和老机器感 |
| Diffuse | 把一颗清楚的回声打散成一团 | 音头变软，离散回声逐渐变成雾状尾巴 |
| Dynamics | 自动改变回声的音量起伏 | 左边切碎／收尾，右边压扁并产生呼吸感 |
| Pitch | 改变回声的音高 | 回声比原声更高或更低，并可逐轮继续升降 |

单击旋钮中心可启用／禁用该效果，拖动旋钮改 Amount，双击可输入精确值。它们都能接收底部调制源。判断某个效果有没有用时，最好先把它关掉和打开反复对比，不要只看旋钮位置。

#### Drive：让回声从干净变成过载

**Drive 做的事**：把进入延迟处理的信号推得更大，让 Timeless 3 内部的 Saturation（饱和）开始工作。

“饱和”可以理解为一种比较圆滑的失真：信号太大后，波峰不再原样通过，而是被压扁，同时产生原来没有的泛音。听起来通常会经历下面的变化：

```text
干净、偏薄 → 更厚、更靠前 → 温暖／粗糙 → 明显失真、发炸
```

- **人声 Delay**：加一点 Drive，回声会比干声更粗、更容易躲到后面，不像一份完全相同的复制品。
- **鼓或 Synth**：Drive 较高会让回声更有冲击、更像被模拟硬件推过载。
- **高 Feedback 时**：同一段声音会反复经过处理，后面的重复往往越来越饱和、越来越密。
- **它不只是音量**：即使把最终输出补回同样响度，Drive 产生的泛音和压扁感仍然存在。

> [!tip] 怎么开始
> 先把 Feedback 放在能听见三四次重复的位置，慢慢增加 Drive；听到回声刚从“透明复制”变得略厚时就先停。新手一上来拧太大，通常只会得到又响又糊的尾巴。

#### Lo-Fi：让每次重复故意掉画质

**Lo-Fi 做的事**：同时减少采样率和位深，让本来平滑、清晰的数字音频变得粗糙。

- **降低采样率**：高频不再平滑，容易出现沙粒、金属边缘和数字折叠感。
- **降低位深**：细小的音量变化被压成一格一格，产生颗粒、量化噪声和破碎感。
- **少量**：像旧采样器、老数字延迟或年代较久的设备。
- **大量**：会变成明显的 Bitcrush／故障音，不再只是“温暖”。

它和 Low-pass Filter 不一样：Filter 主要是把高频削掉，结果通常只是更暗；Lo-Fi 会产生新的数字失真，所以可能又暗、又刺、又有颗粒。

典型用途：

- 主唱保持清楚，但让回声像电话、旧收音机或低清采样，避免和原声争位置。
- 给鼓点或短采样做逐轮损坏的 Glitch Delay。
- 给过于干净的合成器增加复古数字味。

> [!warning]
> Lo-Fi 的尖锐毛刺在高频较多的素材上会很明显。觉得刺耳时，不要只减 Lo-Fi，也可以在反馈 Filter 中加 Low-pass，让后几轮同时变暗。

#### Diffuse：把“哒、哒、哒”抹成“沙——”

**Diffuse 做的事**：把一颗回声的能量分散到许多非常接近的时间点，使清楚的音头被摊开。这里的“瞬态”就是声音最前面突然爆发的那一下，例如鼓的“啪”、拨弦的“叮”和辅音的“t／k”。

```text
Diffuse 少：啪 —— 啪 —— 啪
Diffuse 中：啪 — 沙 —— 沙 ——
Diffuse 多：沙～～～～～～～～（回声边界开始消失）
```

- **少量**：主要软化尖锐音头，回声仍然一颗一颗可数。
- **中量**：每颗回声开始散开、互相粘连，听起来更宽松、更柔软。
- **大量**：离散回声会变成浓密尾巴，听感接近 Reverb。

适合用在：军鼓、打击乐、Pad、氛围人声和需要“远景”的声音。不适合用在：必须听清每个字、每个节奏落点的快速 Rap 或复杂主旋律——Diffuse 太多会把辅音和节奏轮廓全部抹掉。

> [!note] Diffuse 和 Reverb 不是一回事
> Diffuse 负责把回声颗粒打散；Feedback 决定这团声音重复／持续多久，Filter 决定它越来越亮还是越来越暗，Width 决定它铺多宽。只有几项一起配合时，才会得到比较完整的类混响尾巴。

#### Dynamics：自动整理回声的音量起伏

**Dynamics 不是“音质好坏”旋钮，也不是普通音量旋钮。**它会观察正在进入这个处理环节的声音有多大，然后自动改变增益。中间位置基本不处理；往左和往右是两种相反用途。

##### 向左：Gate / Expansion——让小声部分更小

- **Expansion（扩展）**：把大声和小声之间的差距拉大。大声的音头保留，小声的尾巴进一步变小。
- **Gate（门）**：比 Expansion 更像一道门；声音低到一定程度后，门逐渐关上，把后面的低电平内容切掉。

所以向左拧后，你会听见：

```text
处理前：啪———沙沙沙———啪———沙沙沙
向左后：啪      |       啪      |
```

它适合解决：

- 回声尾巴一直拖着，盖住下一句话。
- 鼓 Delay 太散，希望每颗重复更短、更像节奏切片。
- Feedback 中积累了底噪、呼吸声或不需要的小声残留。

向左太多的副作用是：尾巴会突然断掉，听起来僵硬、不自然；人声词尾也可能被吃掉。

##### 向右：Compression——压住大声部分，让电平更稳定

**Compression（压缩）**会在声音突然变大时自动把它压下去，缩小峰值与其余部分的差距。Timeless 3 这里故意把效果调得比较有性格，所以向右较多时，压缩器压下去再松开的过程会被听见。

这个“一压一松”的音量摆动就是 **Pump（抽吸／泵动）**：

```text
声音进来 → 增益被压下 → 压缩器松开 → 尾巴重新浮上来
听感像：呼——吸——呼——吸——
```

它适合：

- 让 Synth、Pad 或鼓的 Delay 产生明显节奏呼吸。
- 压住每颗回声最突出的峰值，使尾巴更黏、更连贯。
- 配合高 Feedback 制造夸张、会起伏的特殊效果。

向右太多的副作用是：声音会忽大忽小、音头被压扁、尾巴反而更显眼，主唱 Delay 可能听起来像故障或抢拍。

> [!example] Dynamics 最快试听法
> 1. 选一段有明显停顿的人声或鼓，先让 Delay 重复三四次。
> 2. 暂时把 Wet 调到容易听清的位置。
> 3. Dynamics 从中点慢慢向左：注意每颗回声是不是变短、空隙是不是变干净。
> 4. 回到中点，再慢慢向右：注意音头是不是被压下、尾巴是不是一收一放。
> 5. 找到方向后再降低 Amount，并恢复正常 Wet／Mix。不要一开始就在整首歌里猜。

#### Pitch：让回声唱得更高或更低

**Pitch 做的事**：改变延迟声的音高，范围是 `-12` 到 `+12` 个半音。一个半音相当于钢琴上相邻的一个键；`+12` 是高一个八度，`-12` 是低一个八度，`0` 是不移调。

- 小幅移调：产生轻微音准漂移、Chorus 式宽度或不稳定感。
- `+12`：回声高一个八度，适合梦幻上升、闪亮尾巴。
- `-12`：回声低一个八度，适合低沉、阴暗或怪物化的尾巴。
- 其他音程：例如 `+7` 是上行纯五度，可让每轮回声形成旋律性堆叠。

Pitch 旁还有两个关键开关：

- **Pitch Shift Mirroring**：反转右声道的移调方向。例如设置少量正值后，左边升高、右边降低，可得到类似 Chorus 的立体声展开。
- **Pitch Shift Routing**：决定移调是在反馈回路**内**还是**外**。
  - **回路内**：每次重复都在上一轮基础上继续移调。设 `+12` 时，第 1 轮高一八度，第 2 轮再高一八度；会很快变成特殊效果。
  - **回路外**：内部重复保持原来的音高关系，最终 Wet 输出只统一移调一次，结果更可控。

启用 Pitch 后，最短 Delay Time 会从 5 ms 提高到 45 ms。这不是故障，而是移调算法需要更多缓冲时间。

### 五个效果怎么选

| 你现在听到的问题／想要的结果 | 先动哪个效果 | 调整方向 |
| --- | --- | --- |
| 回声太干净，像复制粘贴 | Drive | 少量增加，让它更厚、更有泛音 |
| 回声太高清，想要旧设备／游戏机感 | Lo-Fi | 少量到中量增加 |
| 每颗回声太尖、太清楚，想变成雾 | Diffuse | 逐渐增加，直到边界开始粘连 |
| 尾巴拖太久、空隙不干净 | Dynamics | 从中点向左 |
| 想要呼吸、抽吸、节奏起伏 | Dynamics | 从中点向右 |
| 想让回声逐轮升高或降低 | Pitch | 设音程，并把 Routing 放进反馈回路 |
| 只想把所有回声统一移调一次 | Pitch | 设音程，并把 Routing 放在反馈回路外 |

### 正中：Delay 控制

| 控件 | 作用 |
| --- | --- |
| Delay Time 大旋钮 | 设置主延迟时间。未同步时以 ms 显示，正常最短 5 ms。 |
| Delay Time Pan 外环 | 只增加左或右声道的延迟，最多到基础时间的 400%；下方 L / R 显示实际左右时间。 |
| Freeze | 停止接收新输入，把内部反馈固定为 100%，无限重复当时缓冲区里的声音；它本身也能被调制。 |
| Ping-Pong | 只向一侧起始并只使用 Cross Feedback，让声音左→右→左来回跳；可选择从左或右开始。 |
| Delay Sync | 跟随 FL Studio 工程速度；启用后主旋钮变成 Delay Offset，在同步拍值的 `50%–200%` 间缩放。 |
| Delay Read Mode | 改 Delay Time 时选择 Tape 或 Stretch 的读取行为，详见下文。 |
| `DELAY` 标签 | 未与宿主同步时可连续点几次做 Tap Tempo。 |

### Feedback：重复怎么循环

| 控件 | 作用 |
| --- | --- |
| Feedback | `0%–200%`。低值重复少；接近 100% 尾巴很长；超过 100% 会越来越响并持续推动内部饱和。 |
| Feedback Pan 外环 | 分配左右通道各自的反馈量；极端位置可能让一侧重复直接消失。 |
| Cross | 在同声道反馈与交叉反馈之间混合；越高，左声道越多送到右边，反之亦然。 |
| L / R Feedback Invert | 分别反转左、右反馈信号极性，改变相位染色和立体声演化；不是把声音倒放。 |

> [!danger] 高反馈安全线
> `Feedback > 100%`、高 Drive 或高共振 Filter 都可能让电平不断上升。设计自激音色时先降 Wet／输出、开 Limiter，并准备旁通；`Auto Mute Self-Osc` 只处理滤波器自激，不等于整个反馈回路的防爆音限幅器。

### 右中：Width、Wet 与 Mix

| 控件 | 作用 |
| --- | --- |
| Stereo Width | 控制最终 Delay 输出宽度；`100%` 为完整立体声，降低时先让输入趋于 Mono，最终输出也趋于 Mono。 |
| Wet Level | 只控制延迟湿声的最终电平，不改变 Dry。 |
| Wet Pan 外环 | 在 `Left/Right` 模式中调 L/R 平衡；在 `Mid/Side` 模式中调 M/S 平衡。 |
| Mix | Dry 与 Wet 的比例；Insert 常用中间值，Send / Return 应设 `100% Wet`。 |
| Lock Mix | 锁住 Mix，使切换预设时不会改掉当前干湿比；Send 轨尤其应该打开。 |

### 最底栏：全局与输入输出

| 控件 | 作用 |
| --- | --- |
| MIDI Learn | 把硬件 MIDI 控件直接映射到插件参数。它是“直接遥控参数”，不同于下面经过 Modulation Slot 的 MIDI Source。 |
| Channel Mode | `Left/Right` 分别处理左右；`Mid/Side` 先把立体声转换为中央信号 M 和侧边信号 S，处理后再转回立体声。 |
| Auto Mute Self-Osc | 输入静音时，自动压住非线性 Filter 的持续自激。 |
| Global Bypass | 插件内部软旁通，尽量避免切换爆音；它与 FL Studio Wrapper 的插件启用开关不是同一个按钮。 |
| In / Out | 显示并调整 Wet 路径的输入／输出增益，范围均为 `-36 dB` 到 `+36 dB`；悬停或单击可展开 Gain / Pan。 |
| Resize / Scale | 最右侧调整插件窗口大小和 UI 缩放。 |

## 底部 `+`：可添加的部件完整说明

截图中 `Size`、`Length`、`Filter` 这些竖条就是预设作者建立并重命名的 **Slider 调制源**。单击底部大 `+` 会出现以下 6 个新增项；`Sort Slots` 是整理已有调制槽的显示顺序，**不会新增调制源，也不改变声音和路由**。

| 菜单项 | 中文理解 | 自己会不会动 | 最适合控制什么 |
| --- | --- | --- | --- |
| New Slider | 一维宏旋钮／推子 | 默认手动或由 MIDI Learn 控制 | 一个手势同时推多个参数，如 Size、Length、Filter |
| New XY Controller | 二维宏控制器 | 默认手动或由 MIDI Learn 控制 | X、Y 两个维度同时控制空间、亮度、反馈等 |
| New XLFO | 循环波形／步进音序调制器 | 会自动周期运行 | 周期摆动、节奏门、滤波扫动、随机变化 |
| New Envelope Generator | 包络发生器 EG | 被音频阈值、Side-chain 或 MIDI Note 触发后运行 | 让参数按 D-A-D-S-H-R 时间曲线变化 |
| New Envelope Follower | 包络跟随器 EF | 跟随主输入或 Side-chain 的音量／瞬态 | Ducking、随鼓点开滤波、随演唱强弱改变效果量 |
| New MIDI Source | MIDI 转调制信号 | 跟随收到的 MIDI 数据 | Velocity、Pitch Bend、Mod Wheel 或任意 CC 驱动参数 |

### New Slider

Slider 是一维手动宏控件，只有一个 `Y` 输出。它本身不代表某个固定参数：把顶部的 Source Drag Button 分别拖到多个目标后，一根 Slider 就能按不同深度同时控制 Feedback、Diffuse、Filter Frequency 等。底部名称可双击重命名，所以预设里常见 `Size`、`Character`、`Motion` 这类语义名称。

- 可在右下菜单把范围设为 **Unipolar `0…1`** 或 **Bipolar `-1…1`**。
- 悬停可看当前值，双击推子可直接输入数值。
- 可对 Slider 使用 MIDI Learn，交给实体旋钮或推子控制。
- 适合把一个复杂预设压缩成少数几个“会听得懂”的宏参数。

### New XY Controller

XY Controller 是二维宏：横向 `X` 和纵向 `Y` 各有一个 Source Drag Button，可以连接到不同目标，也可以各自控制多个目标。比如 X 控制明暗与 Lo-Fi，Y 同时控制 Feedback 与 Width。

- 右下菜单可在 `XY` 与 `Slider` 模式间切换，也可选 Unipolar 或 Bipolar 范围。
- `Ctrl + 单击`回默认位置；拖动时按 `Shift` 更容易只移动一个轴。
- 双击名称可重命名；删除 XY 时，其 X、Y 关联的调制槽也会一起删除。

### New XLFO

XLFO 既能做普通 LFO，也能做**最多 16 步**的循环步进音序器。默认两个 Step 组成正弦形；点组件卡片可展开完整编辑器。

| 全局控制 | 作用 |
| --- | --- |
| Frequency | Free 模式范围 `0.02–500 Hz`；决定整轮波形速度。 |
| Host Sync / Offset | 可同步 `16 bars–1/64 bar`；同步后旋钮成为 `50%–200%` 的 Offset，并可快速选附点／三连音。 |
| Balance 外环 | 改变前半与后半波形占用的时间比例，不只是改幅度。 |
| MIDI Retrigger / Legato | Retrigger 每个 Note On 都重启；Legato 只在第一颗连奏音符时重启。 |
| Snap | 把输出量化到两八度音高网格，适合调制频率参数做琶音。 |
| Global Glide | 给所有 Step 的滑音量加统一偏移，决定何时向下一步插值。 |
| Phase Offset | 移动每轮从哪里开始；Frequency 设为 0 时也可让另一个调制源扫描 Phase。 |

Step 编辑器中可新增／删除／多选 Step，并设置：

- **Value**：这一步的输出值。
- **Random**：每次经过此 Step 都重新取随机值。
- **Curve**：向下一步插值的 `Linear / Sqr / Sqrt / Sine` 曲线。
- **Glide**：该 Step 自己的滑音量，与 Global Glide 合并。

最直接的起点：XLFO → Delay Time 得到周期性的音高／时间摆动；XLFO → Filter Frequency 得到自动扫频；方阶波 → Wet 或 Feedback 得到节奏门式延迟。

### New Envelope Generator

EG 自己生成一条包络，不是“照抄输入音量”。触发来源可选主输入、External Side-chain 或 MIDI Note；音频触发时由 Threshold 决定何时启动，Side-chain 模式还能按住 Audition 暂听触发信号。

| 参数 | 含义 |
| --- | --- |
| Delay | 触发后等待多久才开始 Attack。 |
| Attack | 从起点上升到峰值的时间。 |
| Decay | 从峰值下降到 Sustain 的时间。 |
| Sustain | 触发保持期间停留的电平；这是电平，不是时间。 |
| Hold | 触发结束后继续保持 Sustain 多久。 |
| Release | Hold 结束后回到起点所需时间。 |
| A / D / R Slope | 分别把三段改成 Linear、偏 Log 或偏 Exp 的弯曲手感。 |

Range 可选普通 `0…1`，也可选择以 Sustain 阶段为 0 的居中输出，便于做正负方向调制。典型用法：每次鼓击触发时先压低 Wet，再缓慢 Release 回来；或每次 MIDI Note 让 Filter 打开再合上。

### New Envelope Follower

EF 不生成固定 ADSR，而是实时跟踪触发音频：声音大，输出通常也大；声音小时输出回落。

| 控制 | 作用 |
| --- | --- |
| Trigger Input | 选择主输入或 External Side-chain。 |
| Audition | 使用 Side-chain 时按住可监听触发信号。 |
| Attack | 输出追上电平上升的速度；越短越敏捷，越长越平滑。 |
| Release | 输入变小后输出回落的速度；越长，动作越黏、越不抖。 |
| Envelope / Transient | Envelope 跟随整体响度；Transient 只对鼓点等短促能量突发产生输出。 |

做 Ducking 时，把 EF 拖到 Wet Level 或 Feedback，并在 Modulation Slot 中反转极性；原声一来，延迟退后，原声停下，尾巴再浮出来。

### New MIDI Source

MIDI Source 先把 MIDI 数据变成调制信号，再通过 Slot 叠加到目标上。宿主必须先把某条 MIDI Track 的数据路由给 Timeless 3。

| 控制 | 作用 |
| --- | --- |
| MIDI Input | 选择 Velocity、Pitch Bend、Modulation Wheel 等输入类型。 |
| Controller Number | 当 Input 选 `Controller` 时指定任意 MIDI CC；例如延音踏板通常是 CC64。 |
| Response Curve | 选择 Linear、Exponential、Logarithmic、Square、Square Root、Sine 等响应，改变手感。 |

它和 `MIDI Learn` 的区别：MIDI Learn 是把硬件直接绑定到参数；MIDI Source 则经过可反相、可限深度、可与其他调制叠加的 Modulation Slot。需要“只在原旋钮附近推一点”或“一条 CC 同时控制多个目标”时，用 MIDI Source。

## 调制怎么连、怎么改、怎么删

### 建立连接

1. 在底部选择一个 Source，抓住它顶部的绿色／彩色 **Source Drag Button**。
2. 开始拖动后，界面会变暗，所有可接收调制的目标会高亮。
3. 把 Source 拖到 Delay、Feedback、FX、Filter、Tap、Wet 等目标上。
4. 松手后弹出 Modulation Slot 面板，调整本次连接的深度。

### Modulation Slot 面板

| 控件 | 作用 |
| --- | --- |
| Level | 调制深度和方向；基础旋钮值仍是中心／起点。`Shift` 精调，`Ctrl + 单击`复位。 |
| `+ / -` | 反转该 Slot 的调制极性。 |
| On / Off | 临时旁通这条连接，用来判断它实际贡献了什么。 |
| Source / Target 菜单 | 在不重拖的情况下更换来源或目标。 |
| Remove | 只删除这条 Slot 连接。 |

目标旁出现的彩色小点表示它已被调制，颜色对应 Source 类型；单击小点可重新打开相关 Slot。Source 顶部的拖拽点若带实心圆，表示当前确实已有连接。悬停 Source 卡片右上角的删除按钮会删除 Source 及其全部 Slots，操作前要确认不是只想删一条连接。

### 展开、隐藏和整理

- 单击 Source 卡片展开完整编辑器；直接拖卡片通常可快速改它的主参数。
- 双击 Source 名称可重命名；名称会保存在工程、Preset、A/B 与 Undo 状态中。
- `>> / <<` 可显示或隐藏 XLFO、EG、EF、MIDI 等调制源；Slider 与 XY 作为预设宏通常保持可见。
- 默认展开一个 Source 会折叠其他 Source；`+` 菜单中的 Auto-Collapse Sources 可控制这一行为（不同版本／界面高度下才会显示此项）。
- `Sort Slots` 只整理 Slot 列表，适合复杂预设看乱以后使用。

## 通用鼠标操作

| 操作 | 结果 |
| --- | --- |
| 上下拖旋钮中心 | 调参数；慢拖更精细。 |
| 拖旋钮指针绕圆周 | 旋转模式；把鼠标拉远可更精细。 |
| 鼠标滚轮 | 调旋钮或外环。 |
| 双击旋钮／数值 | 直接输入精确值；频率可输入 `1k`、`A4`，通用参数也可输入百分比。 |
| `Ctrl + 单击` | Windows 下恢复默认值。 |
| `Shift + 拖动／滚轮` | 精调。 |
| `Alt + 拖动` | 某些成对／关联参数可同时调整。 |

## 界面旋钮与五个坐标轴

主界面直接可见的核心旋钮与《延迟与空间》的五个坐标轴一一对应：

| 界面旋钮 | 在通用模型中的作用 |
| --- | --- |
| L / R（左右延迟时间） | 决定回声多久出现一次 |
| FEEDBACK | 决定重复次数；接近自激边界时就变成循环 |
| CROSS（Cross Feedback） | 左右声道的相互回灌，回声左右跳动 |
| WIDTH（Stereo Width） | 回声的立体声宽度 |
| WET（Wet Level） | 延迟湿声自身的最终响度 |
| MIX | Dry 与 Wet 的比例 |

"重复音色"和"运动"两个轴由上面的 Filters、Drive / Lo-Fi / Diffuse、Modulation 系统负责。

## 模块与通用模型的对应

FabFilter Timeless 3 的核心控制正好对应《延迟与空间》的五个坐标轴：

| Timeless 3 模块                         | 在通用模型中的作用                   |
| ------------------------------------- | --------------------------- |
| Delay Time／Sync／Offset                | 决定重复时间及其与拍速的关系              |
| Feedback／Cross Feedback／Freeze        | 决定重复次数、左右回灌和持续循环            |
| Stereo Width／Delay Time Pan／Ping-Pong | 决定宽度、左右时间差和空间运动             |
| Wet Level／Mix                         | 决定延迟声的响度与干湿关系               |
| 最多 6 个 Filters                        | 改变反馈每一轮的频谱与共振               |
| Drive、Lo-Fi、Diffuse、Dynamics、Pitch    | 改变反馈回路中的染色、扩散、动态和音高         |
| Modulation 系统                         | 让几乎任何目标随 LFO、包络、输入或 MIDI 变化 |

官方说明中，立体声延迟信号会经过五类效果和最多六个滤波器，再以可调反馈送回输入。这就是 Timeless 3 能从普通 Echo 连续变化到 Chorus、Diffuse Tail 和特殊效果的原因。

## 预设浏览器怎么读

> [!note] 分类不是具体预设
> `Default Setting` 是复位起点；Clean、Medium、Long、Slapback、Reverb、Modulation、Special FX、Mono 更适合作为预设浏览器里的**声音分类／方向**来理解，并不表示 Timeless 3 总共只有 9 个工厂预设。顶部中央显示的完整名称（例如截图里的 `Saturated Empty Hall RV`）才是当前具体预设。不同版本安装的预设名称和数量也可能不同。

### 分类速查表

| 分类／入口            | 一句话           | 典型用途            |
| --------------- | ------------- | --------------- |
| Default Setting | 出厂默认参数，万能起点   | 从零开始调           |
| Clean           | 每次回声保持原样、清晰可数 | 要听见每一颗回声的人声／旋律  |
| Medium          | 中速中量，最标准的人声延迟 | 没想好时最不容易犯错      |
| Long            | 长尾、远距离回应      | 副歌后、段落打开        |
| Slapback        | 极短、只有一次回声     | 主唱和吉他增厚         |
| Reverb          | 回声挤成连续尾巴，像混响  | 临时空间感、省一个混响插槽   |
| Modulation      | 回声音高轻轻晃动      | Pad、Lead、合成器    |
| Special FX      | 极端设置、怪异长尾     | 转场、Sound Design |
| Mono            | 左右同时，回声居中     | 贝斯、空间拥挤时        |

### Default Setting

全部参数回到出厂值。它不是一个"效果"而是一个干净的起点 —— 从它开始，只拧 Delay Time、FEEDBACK 和 MIX 就能得到最基础的回声。想探索某个风格时，先选它复位，再按速查表换方向。

### Clean

反馈回路基本不染色、不滤波，回声**每次重复都保持原样**，所以能清晰可数地把一句话再唱一遍。适合：人声尾韵（最后一个词的重复）、Rap、想要"听到每一颗回声"的旋律动机。

用法：作为 Insert 时，MIX 先放到三到四成；加一点 Cross Feedback 让回声左右跳动。想让它退到背景，就调低 Wet Level／MIX、降低 FEEDBACK，或加滤波让回声逐次变暗 —— 对应《延迟与空间》里的 Clean → Filtered 方向。

### Medium

名字就是字面意思：中等延迟时间 + 中等反馈，听感"不远不近"的通用回声。把时间同步到拍速后（如 1/8、1/4 或附点），就是典型的人声延迟。

用法：作为 Insert 时，MIX 先放三成听它跟不跟得上节奏；时间按拍值同步（FabFilter 的 Sync 模式）。它跟 Slapback 的区别是：Slapback 只有一次、几乎听不出是"回声"，Medium 是几颗可辨认的回声。

### Long

长时间 + 高反馈 → 回声重复多次、尾巴拖得远，原声后面像有一个大房间在重复它。适合：副歌最后一句把空间"打开"、Pad 或鼓声部支撑段落。

注意（对应库里的常见误区）：长尾会盖住后面新进来的歌词和和声 —— 收小 Wet Level／MIX，或者只在段尾几个字上用（自动控制它进出）。

### Slapback

延迟时间拧到极短（几十毫秒量级）+ 低反馈，回声只出现一次，听感像"啪"地贴在原声背后。人耳通常不把它当作"回声"，而当作**增厚**。适合：主唱加厚、复古摇滚／乡村吉他（猫王式）。

用法：作为 Insert 时，MIX 放一到两成通常就够，多了会变成模糊的双声部节奏；想要轻微立体感，把左右时间调出一点差异（Offset / Delay Time Pan）。它不抢音头，是"Slap Delay"技术的本体。

### Reverb

提高 Diffuse、使用较密的 Taps／较短重复，再配合适量 FEEDBACK，可把离散回声抹成连续、弥漫的尾巴；Cross Feedback 和 WIDTH 负责把这片尾巴铺向立体声两侧。适合：不想再挂一个混响插件、或只想要一片"模糊的远景"时。

注意：它只是"像"混响 —— 早期反射结构和空间暗示跟真混响不同（库里强调过 Diffuse ≠ Reverb）。深度空间需求还是换混响插件。

### Modulation

内部 LFO 调制延迟时间，让回声音高轻微上下浮动，产生 Chorus / Vibrato 般的"漂移"。适合：把静止的 Pad、Lead 或合成器推"动"起来，给 EP、吉他加温度。

用法：作为 Insert 时保持适中的 MIX；晃动幅度、速度在 Modulation 系统的 XLFO 与 Slot Level 里调，也可以自动化让它只在特定乐句出现。

### Special FX

时间、反馈、调制全往极端拉：怪异的长尾、嗡嗡的回声或者故障感。适合：转场音、鼓的 Fill 装饰、Sound Design 中的反常规元素。

用法：放发送轨（Send）时把 MIX 设为 100% Wet，用发送量或 Wet Level 自动化控制它只在转场时冒出来；用完收回发送量 —— 高反馈长尾非常容易盖住音乐本身。

### Mono

L、R 延迟时间完全相同，回声居中、不往两侧展开。适合：贝斯延迟、混音已经很挤时给主体加延迟（保持中心清晰）、单声道兼容检查。

想加宽就反着来：把左右时间差拉大，或开 CROSS / WIDTH。

## Tape 与 Stretch 的区别

改变 Delay Time 时，普通或磁带式延迟会产生类似变速磁带的音高滑动；Timeless 3 的 **Tape** 模式保留这种变化，**Stretch** 模式则通过时间伸缩尽量保持音高。记录自动化时应把读出模式一起记下，否则只记 Delay Time 不足以复现听感。

## Send 与 Insert

- 作为轨道 Insert 使用：Mix 决定原声与延迟声比例。
- 作为 Send／Return 使用：官方建议 Timeless 3 设为 100% Wet，由发送量控制效果多少。
- 使用预设时可锁定 Mix，避免预设把 Return 轨的 100% Wet 改掉。

## 怎么看待这些预设

每个预设都是一组参数的"快照"，不是玄学名称。用它之前先问：这团回声**重复了几次、离多远、清楚还是模糊、静止还是运动、居中还是展开**，然后朝目标方向拧旋钮。完整的选择流程见 [[音乐/practice/production/延迟与空间|延迟与空间 → 不看预设名的选择流程]]。

## 资料来源

- [FabFilter Timeless 3 产品页](https://www.fabfilter.com/products/timeless-3-delay-plug-in)
- [FabFilter Timeless 3 Help：Overview](https://www.fabfilter.com/help/timeless/using/overview)
- [FabFilter Timeless 3 Help：Delay controls](https://www.fabfilter.com/help/timeless/using/delaycontrols)
- [FabFilter Timeless 3 Help：Delay display / Taps](https://www.fabfilter.com/help/timeless/using/delaydisplay)
- [FabFilter Timeless 3 Help：Effects](https://www.fabfilter.com/help/timeless/using/effects)
- [FabFilter Timeless 3 Help：Filters](https://www.fabfilter.com/help/timeless/using/filters)
- [FabFilter Timeless 3 Help：Modulation](https://www.fabfilter.com/help/timeless/using/modulation)
- [FabFilter Timeless 3 Help：Modulation slots](https://www.fabfilter.com/help/timeless/using/modulationslots)
- [FabFilter Timeless 3 Help：XLFO](https://www.fabfilter.com/help/timeless/using/xlfo)
- [FabFilter Timeless 3 Help：Envelope generator](https://www.fabfilter.com/help/timeless/using/eg)
- [FabFilter Timeless 3 Help：Envelope follower](https://www.fabfilter.com/help/timeless/using/ef)
- [FabFilter Timeless 3 Help：MIDI source](https://www.fabfilter.com/help/timeless/using/midisource)
- [FabFilter Timeless 3 Help：XY controller / Slider](https://www.fabfilter.com/help/timeless/using/xycontroller)
- [FabFilter Timeless 3 Help：Input/output options](https://www.fabfilter.com/help/timeless/using/inputoutput)
