---
title: VLM+WM总章：LLM在视觉语言模型中的功能定位
aliases:
  - VLM中的LLM功能定位
  - LLM在视觉语言模型中的作用
date: 2026-03-20 00:00:00
categories:
  - 博客
tags:
  - ai/multimodal
  - ai/vlm
  - ai/llm
description: 从第一性原理理解，LLM 在视觉语言模型中承担的系统职能、适用边界与方法意义。
---

[[博客索引|返回博客索引]]




## 模块一：LLM 在视觉语言模型中的功能定位

#### 传统视觉系统的瓶颈

##### 1. 输出不固定，下游任务不同
业务真正需要的结果，有时是一句话，有时是一段报告，有时是坐标框、点、JSON、字段表或者操作步骤，而传统视觉模型更适合固定维度、固定语义的输出
##### 2. 任务偏向于理解问题
比如“谁最危险，为什么”“把这张发票整理成结构化字段”“看完几帧视频指出异常开始的时间段”。这里的瓶颈是任务理解、信息筛选、跨步骤组织和结果表达。

而LLM则是在感知结果之上提供3类高层能力。
#### 1. 任务接口层和输出层：把分裂任务统一成指令，并统一输出内容
过去常见的形式是“检测模型做检测，OCR 模型做 OCR，VQA 模型做问答”；而更一般的形式可以写为：
`图像/视频 + 文本指令 -> 结果`
#### 2. 推理组织层：把感知结果接到开放式判断上

很多视觉问题注重“基于所见，你应该如何判断、比较、解释和归纳”。LLM 能把视觉信号接到一个更擅长符号组织和语言推理的空间里，于是模型不只会报对象名称，还能解释关系、提炼重点、给出判断理由。

#### 3. 控制与编排层：支持多步交互与 agent 化执行
一旦任务从单次回答转变为“观测当前画面 -> 决定下一步 -> 调用工具 -> 再次观测”的多步过程，系统需要一个状态控制器。LLM 在这里越来越像视觉系统的调度核心，负责规划、工具调用和多步交互。GUI 操作、文档工作流、长视频检索、computer use 都属于这类问题。



### 研究
VLM ：输入instruction  




## 模块二：世界模型
用于扩展自动驾驶的训练数据  【经过一个离线的中间过程 引入监督信号 用于其他模型 如 VLA】
使用世界模型进行无监督下游规划等任务  【**端到端**：直接将世界模型引入规划（更进一步的监督信号） WAM ，**模块化**：模块化WM输出进行下游任务 】
- 模块化
	- 表空间变化太复杂--->潜空间 ---> 
							|
						（dense voxel，sparse query）	BEV密集 - (牺牲3D信息，) -> 高斯稀疏提取（折中） 
	- 信息流动链发展：
		- WM生成数据集-->WM结合VLM
							 |
		UniDrive-WM:先轨迹->轨迹condition的WM 
	    轨迹和动作联合生成:DriveDreamer->对齐空间:
- 端到端
	- DriveWorld-VLA ：1阶段WAM训练 2阶段WM-action训练 3阶段 AM-worldreward训练
	- World4Drive ： 语言信息注入少，粗轨迹-->世界模型-->细轨迹模块
	- 



**低维/表征类**
- 状态转移型世界模型
	- 当前状态加动作之后，下一步环境状态是什么【包含递推的动态信息】
	- 可以被递推、可以被 rollout、可以被 action 干预、可以服务 planning/control
	- CartPole、MuJoCo、World Models、PlaNet、Dreamer
- 表征预测型世界模型
	- 预测未来/被遮挡部分的**语义表征** 【无动态信息】
	- JEPA、V-JEPA
- 价值/规划抽象型世界模型
	- 预测 value + policy prior
	- MuZero  EfficientZero   Gumbel MuZero
- 结构化 / 对象中心 / 因果型世界模型
	- OOCDM、COMET、STICA

**高维/生成类**
- 感官预测型世界模型
	- 预测未来观测
	- GAIA-1、DriveDreamer、Drive-WM、Genie
- 交互仿真型世界模型
	- Genie，cosmos
-  几何空间型世界模型
	- BEV、occupancy、point cloud、LiDAR、3D/4D scene、NeRF/3DGS 这类空间结构
	- OccWorld、Drive-OccWorld

### 问题
- 编码 （有损）--- 未来预测 ---（有损） 解码
	- vae cvae  
	- diffusion，自回归，cvae
	- diffusion，vae，。。。
- diffusion
	- 条件怎么注入
		- cross-attn 【stable diffusion】
		- 初始拼接 【svd】
		- 混合，变换
	- 回归
		- 连续多帧回归，单帧回归
		- 回归类型：latent，bev 
### WAM
**world prediction** 和 **action prediction/planning** 绑在一起训练，让模型通过“想象未来世界”来获得动作决策能力。

####  AR-DIT
- chunk 回归 
- t 回归




### 研究
 - 历史帧意图提取 + 因果提取 存储记忆功能 
	 - 历史帧的重要性选择注入
MINT Mimic Intent, Not Just Trajectories


- 场景的因果提取


- 




- 语义压缩
Back to Parsimonious Latents: Learning Task-Centric World Models from Visual Foundations




## 论文
### flamingo
- 特点：Gated xattn-dense
	- **cross-attn** textfeature Query imagefeature  然后LM
	- **Perceiver Resampler**   小型q-former

![[Pasted image 20260527183340.png]]

![[Pasted image 20260527184709.png]]








### q-former
- 目的 训练桥接，冻结VIT,LLM
	- 做**信息压缩**  patch-token 压缩   【query 固定数目】
	- 做语言相关的信息选择 【query 自主查询】
	- attn交互强，表达能力更强
>[!tip] 思考
> 对齐维度+把视觉语义转成 LLM 能读的软提示【embedding空间对齐】+压缩图像信息
> **静态映射器->带查询机制的视觉信息瓶颈**
> 1:静态信息 CNN,FFN 交互局部 -> attn交互强 全关注
> 2:attn结构优势 query自动关注 重要信息偏置



- 特点
	- **transformer-block** learned-queries 
	- ITC 图文对比学习 Uni-modal Self-Attention Mask 
		- **天然的高层空间需要匹配**    防止QT污染 ： QQ TT attn
	- ITM  图文匹配 Bi-directional Self-Attention Mask 
		- QT 互看    **低层的空间匹配**
	- ITG Multi-modal Causal Self-Attention Mask
		- 基于图像的文本生成   **自回归设计**     转换到text
		- query tokens 可以互相看，但不能看 text tokens
		- text tokens 可以看所有 query tokens，也可以看前面的 text tokens
		- text tokens 不能看未来 text tokens

![[Pasted image 20260527185841.png]]


### DINO

Self-Distillation with No Labels
![[Pasted image 20260616193145.png]]

- teacher 采用ema 指数滑动平均 更新参数 teacher + student
- teacher输入 x 采用ema center  x - xcenter （xcenter采用ema）

### DINOv2
- 引入patch级别的 预测
- student 输入被 mask 的图像 patch
	teacher 输入完整图像
	student 要预测 teacher 对应 patch 的表示



### DETR
- DETR detection transformer
	- 无需**前处理**或者**后处理**操作（设置锚框来提供参考，nms之类）
	- 结构
		- 视觉encoder -transformerEncoder-- transformerDecoder （q = 100- kv SA + CA -- （100，4） --- 匈牙利）
	- ![[Pasted image 20260618180804.png]]
- RT-DETR
	- CCFF,AIFI
	- encoder![[Pasted image 20260618203937.png|577]]![[Pasted image 20260618204204.png]]
	- decoder![[Pasted image 20260618205157.png]]
- Deformable DETR
	- mlp1输出 anchor mlp2输出 偏移 + 权重
## 世界模型论文



### 综述-Understanding World or Predicting Future? A Comprehensive Survey of World Models
- 一类是构造内部表征，用来理解世界当前机制
- 一类是预测未来状态，用来模拟和指导决策







### World Models
- 直觉行动，根据边缘、亮度、方向、运动线索推断变化趋势




### Drive-WM
- 驾驶世界模型需要在高分辨率像素空间中进行建模。以往的低分辨率图像 [20]或向量化状态空间 [4] 方法无法有效表示现实世界中大量细粒度或不可向量
- 生成多视角一致的视频较为困难。以往及同期工作仅限于单视角视频 [28, 31, 63] 或多视角图像生成 [17, 53, 69]
- 难以灵活适应各种异构条件，如天气变化、光照变化、本车动作以及道路/障碍物/车辆布局的变化   【Unified Conditional Generation  接口层面的统一】
![[Pasted image 20260622172316.png]]
- 特点：
	- 多视角 ，引入分布因子分解以增强多视图一致性
		- reference views 之间重叠少，所以可以先生成；stitched views 位于 reference views 中间，和相邻 reference views 有重叠，所以后生成，并且条件化在 reference views 上。
	- 扩散模型单图能力调节后 训练多图微调view-attn temp-attn conv conv
	- Unified Conditional Generation 全部融合为token 然后注入到diffusion-decoder 
- 实现细节
	- 先训练一个 **conditional image latent diffusion model**
	- 在这个 image diffusion model 上加 temporal layers 和 multiview layers，变成 multiview video model；然后冻结原来的 image/spatial 参数，只 fine-tune 新加的 temporal 和 multiview 参数。
	- 



### BEV-WORLD
- 高质量的图像和点云生成依赖于对低层像素或体素细节以及高层场景元素（如车辆和行人）行为动态的联合建模。
- 特点：
	- 融合多来源视角的输入 如image + lidar  
	- bev层面diffusion
![[Pasted image 20260622171524.png]]


![[Pasted image 20260622171159.png]]





### DriveArena
DriveArena 要做的是一个高保真闭环 simulator，让 driving agent 可以在生成出来的真实感多视角图像里开车，并且它的轨迹会反馈回环境，改变下一帧场景
![[Pasted image 20260622183947.png]]



--- 

### 早期视频WM工作
####  GAIA-1
- 特点
	- 生成现实场景的未来样本
		- generation
		- rollout
- 结构特点 
	- 自回归生成

#### FIERY
- xit​=S(xi​,at−1​⋅at−2​⋯ai​) 表示自车从一个时刻到下一个时刻的平移和旋转。原文还说，由于 warp 后会丢掉过去 ego-motion 信息，所以它又把 broadcast 后的 action 拼回 feature 里。FIERY 后面的消融也说，如果不把过去特征变换到当前 reference frame
- 做CVAE建模，2个分布
	- condition为当前st， bev 未来为要生成的目标  
	- present distribution，只能看当前状态 sts_tst​
	- future distribution，未来H步压入后验 
	- present distribution 学会覆盖 future distribution KL散度
- ConvGRU 递归预测未来 BEV states
#### VISTA
现有的驾驶世界模型在泛化到未见环境、关键细节的预测保真度
灵活应用的动作可控性方面仍存在局限
![[Pasted image 20260623032917.png]]
- 特点 
	- 动作控制    
	- 真实度，细节，分辨率 提升

多次生成未来得到reward 评分当前action
- 结构特点
	- 动态Prior 维持长时域展开的一致性，真实度，细节提升  
	- loss设计
		- latent差值  w动态感知权重用于捕捉预测与真实值在运动上不一致的区域
	- 动作condition注入设计
		- position（目的点），意图，速度/角度，轨迹 注入
		- 傅里叶展开 
			- 表达连续能力
			- 多尺度

#### DriveDreamer

- 特点：
	- 生成现实场景的未来样本
		- 利用HD-map，3d-box 可以生成更细粒度，更符合要求的wm-未来帧
		- 结构图 + 文字 --> 未来帧
		- 初始图 + 动作 --> 未来帧 （可选扩展 更远未来动作）

- 结构特点
	- 整段video加噪 + attn注意
	- 一阶段训练auto-dm
		- text + HDMap、3D boxes/labels 输入
		- temporal attention 层拿掉 
	- 二阶段训练actionformer
		- action输入
	- **HDMap** + **referenc image** 条件会和 noisy latent 维度层拼接；**3D boxes + labels** 这种 position condition 会 flatten 后送进 **gated self-attention**；**text prompts** 通过 **cross-attention** 注入，用来影响视频风格


![[Pasted image 20260622133156.png]]






### DriveWorld

### RenderWorld

### GaussianWorld

### WPT
- policy distillation








### CasualDrive 因果
现有的视频生成模型在作为交互式仿真器方面仍显不足。布局条件渲染器依赖所有背景智能体的“预言”未来轨迹，导致其严重非响应性


- 特点
	- 解决模型严重依赖日志回放，依赖训练分布，自车动作引发的因果反应缺少，反应缺少
		- 自车的action 后验坍缩
			- 相当于future-latent，action互信息很低
		- 他车对自车反应不够
	- 实时性-蒸馏的概率坍缩问题
		- 将双向diffusion迁移到实时一帧/一小段一小段生成的模拟器中
		- CausVid![[Pasted image 20260626223759.png]]
	- Casual DMD 矫正(已有) SCF



### CasualVAD
- SCIS 稀疏因果干预

- 感知里的共现偏置、预测里 BEV 作为共同原因、规划里 agent 和 map 输入相关性混淆
	- 多attn的共现
		- 人行横道↔行人 施工区域↔锥桶 停车线/路口↔交通灯
		- 行人视觉特征→pedestrian label 学成  斑马线/路口上下文→pedestrian label
		- object query 会和 map query、其他 object query ，agent query交互
	- 



### C-CoT: Counterfactual Chain-of-Thought with Vision Language Models for Safe Autonomous Driving



### 早期工作
#### MILE
- CVAE建模 
	- 加入动作，观测
	- 给定历史 latent dynamics，推断当前 stochastic state condition 
	- condition 为 ht at-1  （st和ht一起生成condition ）    生成 xt  
	- qϕ​(st​∣ht​,at−1​,xt​) （xt =e(ot)）后验 
	- pθ​(st​∣ht−1​,st−1​) ---  pθ​(st​∣ht​,a^t−1​)  先验
		- ht​=fθ​(ht−1​,st−1​)
		- a^t−1​=πθ​(ht−1​,st−1​)
#### AutoVLA


#### OccWorld
- 传统流程缺少标注 
- 引入世界模型，占用预测 监督  规划
- 2023年 

![[Pasted image 20260620015018.png]]

#### ResAD
- 虚假相关，远距离loss时域困境

- 虚假相关：残差
- 远距离loss时域困境：逐点-维度归一化

![[Pasted image 20260608193640.png]]


#### LAW




 > [! info] 思考
>Ranker 对评估体系的提升来源，是它学会了 NAVSIM 那套指标偏好
>残差学习因果动作
>

### DriveWorld-VLA  【VLM】
- action - wm 联合 
- action based img 
- img based action


![[Pasted image 20260626055537.png]]


### DriveVLA-W0
- 潜空间做自监督
- moe专家架构 解耦vla与action +  统一状态空间


### CoWorld-VLA  【VLM】
- 把互补 world information 编码到 VLA/VLM 内部的 expert tokens 中，增强VLA能力
- 训练WM->训练VLA的action能力->联合训练增强VLA的action解析能力

![[Pasted image 20260706163836.png]]


![[Pasted image 20260706164454.png]]

### UniDrive-WM   【VLM】
QT-Former 编成 LLM reasoning space

- imgs作为tts/its


![[Pasted image 20260626195609.png]]


### world4drive
- latent作为tts/its
	- 世界模型 + 初始plan 影响 规划  【its/tts】
	- 规划 + 未来PlantBased-latent 影响 轨迹最终的选择 【its/tts】
- latent作为轨迹先验 + 结合多轨迹 回归出最合适轨迹 计算loss
- 信号来自于  未来imgs-latent  + 轨迹

![[Pasted image 20260609163608.png]]

### LVDrive
- VLA缺少一个关键监督，依赖稀疏的动作标签，导致大型基础模型的结构化空间场景理解与推理能力未得到充分使用。vision->action

- Vision Latent 作为 增加先验 TTS/ITS 
- 基于LLM
- 信号来自于未来轨迹+未来latent-action+未来latent
![[Pasted image 20260628224323.png]]





### ResWorld
>[! info] 思考
>当前bevworld可以近似保留静态世界”这个结构先验，再用残差建模动态部分
>世界模型为 SA（query）求和 + bevfeature 经过 tokenfuser
>本质：
>用残差结构先验建模动态结构，进入世界模型
>未来时空稀疏点监督loss 传递给future-bev


![[Pasted image 20260618052052.png]]


![[Pasted image 20260618052041.png]]
 







### DLWM
- 结构
	- stage1 训练高斯
	**高斯阶段**： (25600, D） 进行 CA 得到 (25600, 77)
	**投影阶段**：3D高斯到2D高斯，把 25600 个 3D Gaussian 用这个相机的内外参矩阵投影到 2D 图像平面上。每个 3D 高斯（一个椭球）投影后变成一个 2D 高斯（一个椭圆）。这步的张量操作是把 (25600, 3) 的 μ 乘投影矩阵得到 (25600, 2) 的 2D 中心位置，同时把 (25600, 3) 的 scale 和 (25600, 4) 的 rotation 通过 Jacobian 变换得到每个 2D 高斯的协方差矩阵
	**深度排序 + tile 分配**。把 25600 个 2D 高斯按深度排序，然后把图像分成 16×16 的 tile（对于 900×1600 的图，大概有 57×100 = 5700 个 tile）。每个高斯只覆盖它实际投影到的那几个 tile，不覆盖的 tile 就跳过。
	**alpha compositing（逐像素累加渲染）**
		对每个像素，按深度从近到远叠加所有覆盖该像素的高斯。公式：$C_{total} += \alpha_i \cdot T \cdot color_i$，$T *= (1 - \alpha_i)$，T 是透射率（初始 1.0），$\alpha_i$ 是该高斯对当前像素的实际贡献权重
		单个高斯对像素 P 的贡献权重：$d = c_p - c$（像素中心 减 高斯2D中心），$power = -\frac{1}{2} d^T \Sigma_{2D}^{-1} d$（马氏距离），$alpha_i = opacity \times exp(power)$
	> **举例**：像素 P 在 (400, 300)，三个高斯覆盖（按深度从近到远）
	> G1 红车（近）：c=(400,300)，Σ=[[36,0],[0,36]]，opacity=0.90，color=(0.9,0.1,0.1)
	> G2 灰卡车（中）：c=(408,295)，Σ=[[64,10],[10,25]]，opacity=0.80，color=(0.5,0.5,0.5)
	> G3 绿树（远）：c=(390,310)，Σ=[[100,0],[0,49]]，opacity=0.70，color=(0.1,0.7,0.2)
	>
	> **算 alpha**：
	> G1：d=(0,0)，power=0 → **alpha₁ = 0.90**（像素正好在高斯中心）
	> G2：d=(-8,5)，dᵀΣ⁻¹d=2.68，power=-1.34 → **alpha₂ = 0.80×0.262 = 0.21**
	> G3：d=(10,-10)，dᵀΣ⁻¹d=3.04，power=-1.52 → **alpha₃ = 0.70×0.219 = 0.15**
	>
	> **按深度累加**（T 初始=1.0）：
	> G1：C += 0.90×1.0×(0.9,0.1,0.1) = (0.810, 0.090, 0.090)，T → **0.10**
	> G2：C += 0.21×0.10×(0.5,0.5,0.5) = (0.011, 0.011, 0.011)，T → **0.079**
	> G3：C += 0.15×0.079×(0.1,0.7,0.2) = (0.001, 0.008, 0.002)，T → **0.067**
	>
	> **最终**：C = **(0.822, 0.109, 0.103)** 偏红，红车 alpha=0.90 几乎完全遮挡，后面被 T 层层衰减
	> 深度图同理：D = 0.90×3.0 + 0.21×0.10×8.0 + 0.15×0.079×15.0 = **3.05m**
	> 颜色来源：SH 系数 0 阶项直接给 RGB；高阶项让颜色随视角变化，BEV 投影时视角一致所以高阶影响小
	
- **Grounded SAM**+  **Grounding DINO** 自动语义标签 + depth + segment 训练


- stage2：
	- 双路径独立构建latent world bev
	- 世界动态预测 + 基于自身ego改变的世界动态预测

>[! info] 思考
>Voxel/BEV 计算开销巨大，丢失垂直的信息
>Query-based 方法  (UniAD，)


## 轨迹规划模型工作

### 早期规划工作
#### GenAD
- 单模态ego输出
#### FusionAD
- 单模态ego输出
#### VAD
- 单模态ego输出
#### UniAD

多模态轨迹预测：
- 初始注入不同prior作为 多模态先验
- WTA 监督
单模态ego轨迹planning输出 
- ego query，加高层导航 command embedding
- 单模态融合多模态轨迹输出单模态轨迹 【可能模式崩塌】



camera->bev->trackformer,mapformer->motionformer->occformer,planner
- **trackformer**:每辆车变成一个 agent query 维护一组 agent query + egp query
	- ego query不会像普通 agent 一样参加预测 GT matching，但会回归 ego 位置并供下游使用
- **MapFormer**：地图元素也变成 query
	- lane query；
	- divider query；
	- crossing query；
	- drivable-area representation。
- **motionformer**
	- 输出：k X T X 2
	- 内部显式构造不同的 motion query
	- UniAD 为不同 mode 注入 anchor prior
		- scene-level anchor
		- agent-level anchor
- planner
	- qego （1，256）与 Qctxego（6，256 6种mode） 与 action_embedding 
	- 选择max
	- 关注bev
	- 回归轨迹


### CaAD
- 多模态输出
- 先前基本依赖共享的潜在特征来隐式捕捉交互作用->显式建模交互关系捕捉推理交互式未来
	- 显式建模交互关系辅助E2E驾驶
	- N个mode 初始化embedding不同 


### RACP
- 分段 多模态预测 + 轨迹planner（risk continuer 优化）

### MIND: Multi-modal Integrated Prediction and Decision-making with Adaptive Interaction Modality Explorations
- 多模态的固有性，直接用多模态pred进行planner 会出问题

- AIME机制

### Tree-structured Policy Planning with Learned Behavior Model


### MBAPPE MCTS-Built-Around Prediction for Planning Explicitly



### DTPP
- action-based prediction  场景树
- 轨迹树
- 

### Uncertainty-Aware Motion Planning for Autonomous Driving in Mixed Traffic Environment




### GuideFlow: Constraint-Guided Flow Matching for Planning in End-to-End Autonomous Driving

- 多模态模式坍缩问题 + 单模态规划坍缩+限制鲁棒性问题
- 生成式端到端规划期难以将 关键安全和约束融入生成过程







## OneModeltoTranslateThemAl

>[!info] 思考
>假设异构 BEV 特征翻译函数并非任意高维映射，而是落在由少量 expert transformer 参数凸组合形成的低维可组合参数族中，并通过模态关系 δj→δj→i​ 动态生成对应 translator 来实现 zero-shot 泛化。




![[Pasted image 20260612054520.png]]
