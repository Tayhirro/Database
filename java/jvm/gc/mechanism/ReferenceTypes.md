---
type: mechanism
tags:
  - java/jvm
  - jvm
  - gc
  - mechanism
---

# ReferenceTypes（引用类型）

## 一句话
引用类型（Reference Types）是在可达性语义之外对“对象存活与回收时机”施加额外规则的一组引用分类：强/软/弱/虚。

## 严格定义
在 Java 语言层面，除强引用（Strong Reference）外，还存在以 `java.lang.ref.Reference` 为基础的软引用（SoftReference）、弱引用（WeakReference）、虚引用（PhantomReference）。这些引用类型引入“引用对象（reference object）”与“被引用对象（referent）”的二层关系，使得 GC 对 referent 的回收与引用队列（ReferenceQueue）交互遵循实现定义但受规范约束的规则。

可达性分析以 GC Roots 为起点判定强可达路径（见 [ReachabilityAnalysis.md](ReachabilityAnalysis.md)），引用类型在此基础上对“可回收性/回收时机/回调通知”进行细化。

## 引用处理流程（GC 周期第 2 阶段）

在 GC 的引用处理阶段，JVM 根据可达性分析结果处理各类引用：

```
引用处理（Reference Processing）
├── 软引用（SoftReference）
│   └── 条件：若内存不足（实现定义）
│       ├── 清除 referent（置为 null）
│       └── Reference 入队（若注册了 ReferenceQueue）
│
├── 弱引用（WeakReference）
│   └── 条件：只要被标记为弱可达（无强/软引用路径）
│       ├── 清除 referent（置为 null）
│       └── Reference 入队（若注册了 ReferenceQueue）
│
└── 虚引用（PhantomReference）
    └── 条件：referent 已决定回收（无强/软/弱引用路径）
        └── PhantomReference 入队（必须注册 ReferenceQueue，用于回调通知）
```

### 处理规则

**软引用（SoftReference）**
- **触发条件**：对象仅被软引用指向，且 JVM 内存不足（实现定义）
- **行为**：referent 被清除，对象可被回收
- **用途**：内存敏感的缓存（如图片缓存）
- **队列**：可注册 ReferenceQueue 接收通知

**弱引用（WeakReference）**
- **触发条件**：对象仅被弱引用指向（无强/软引用路径）
- **行为**：下次 GC 时 referent 被清除
- **用途**： canonicalizing mappings（如 WeakHashMap）
- **队列**：可注册 ReferenceQueue 接收通知

**虚引用（PhantomReference）**
- **触发条件**：对象无可达引用路径，已决定回收但尚未回收
- **行为**：referent 不会被自动清除（需手动处理），PhantomReference 入队
- **用途**：对象回收前的清理操作（如直接内存释放）
- **队列**：必须注册 ReferenceQueue，用于接收回调

### 引用队列（ReferenceQueue）
- 当 referent 被 GC 决定回收时，对应的 Reference 对象会被加入注册的 ReferenceQueue
- 应用程序可通过轮询 ReferenceQueue 获知对象回收事件
- 见 [../../runtime/threading/ReferenceHandlerThread.md](../../runtime/threading/ReferenceHandlerThread.md)

## 接口：数据 + 约束
- 数据：
  - 引用对象（Reference）及其 referent
  - 引用队列（ReferenceQueue）（若使用）
- 输入：
  - 一次 GC 的可达性与内存压力（对软引用语义影响依实现）
- 输出：
  - referent 的可回收性变化
  - ReferenceQueue 入队事件（若存在）
- 约束：
  - 触发与时机依 JVM 实现与版本变化；本页只给分类边界与交互接口，不将某实现细节视为稳定规则。

## 常用构造/操作（仅列出接口与符号）
- Strong / Soft / Weak / Phantom
- ReferenceQueue
- 运行态线程（处理引用入队等实现行为）：见 [../../runtime/threading/ReferenceHandlerThread.md](../../runtime/threading/ReferenceHandlerThread.md)

## 关系：上级/下级/等价/特例/推广
- 上级：GC 概述（见 [GCOverview.md](GCOverview.md)）。
- 相关：可达性分析（见 [ReachabilityAnalysis.md](ReachabilityAnalysis.md)）。

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → mechanism → ReferenceTypes

