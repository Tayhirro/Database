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

**代码示例（软引用）**
```java
import java.lang.ref.SoftReference;
import java.lang.ref.ReferenceQueue;

// 创建软引用（用于内存敏感的缓存）
byte[] data = new byte[1024 * 1024 * 10]; // 10MB 数据
ReferenceQueue<byte[]> queue = new ReferenceQueue<>();
SoftReference<byte[]> softRef = new SoftReference<>(data, queue);

// 使用软引用获取对象
byte[] cachedData = softRef.get();
if (cachedData != null) {
    // 对象仍然存在，可以使用
    System.out.println("缓存命中，数据大小：" + cachedData.length);
} else {
    // 对象已被回收，需要重新加载
    System.out.println("缓存已失效，重新加载数据");
}

// 清除强引用，使对象仅被软引用指向
data = null;

// 模拟内存不足（分配大量内存触发 GC）
try {
    for (int i = 0; i < 100; i++) {
        byte[] big = new byte[1024 * 1024 * 5]; // 分配 5MB
    }
} catch (OutOfMemoryError e) {
    // 忽略
}

// 检查软引用是否被清除
if (softRef.get() == null) {
    System.out.println("软引用指向的对象已被回收");
}

// 检查引用队列（可选，用于接收回收通知）
SoftReference<? extends byte[]> clearedRef = (SoftReference<? extends byte[]>) queue.poll();
if (clearedRef != null) {
    System.out.println("收到软引用清除通知");
}
```

**弱引用（WeakReference）**
- **触发条件**：对象仅被弱引用指向（无强/软引用路径）
- **行为**：下次 GC 时 referent 被清除
- **用途**： canonicalizing mappings（如 WeakHashMap）
- **队列**：可注册 ReferenceQueue 接收通知

**代码示例（弱引用）**
```java
import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;
import java.util.WeakHashMap;

// 示例 1：基本弱引用使用
Object obj = new Object();
ReferenceQueue<Object> queue = new ReferenceQueue<>();
WeakReference<Object> weakRef = new WeakReference<>(obj, queue);

// 使用弱引用获取对象
Object ref = weakRef.get();
if (ref != null) {
    System.out.println("对象仍然存在：" + ref);
}

// 清除强引用，使对象仅被弱引用指向
obj = null;

// 建议 JVM 进行 GC（仅建议，不保证立即执行）
System.gc();

// 等待 GC 完成（实际应用中不应这样做，仅用于演示）
try {
    Thread.sleep(100);
} catch (InterruptedException e) {
    e.printStackTrace();
}

// 检查弱引用是否被清除
if (weakRef.get() == null) {
    System.out.println("弱引用指向的对象已被回收");
}

// 检查引用队列
WeakReference<? extends Object> clearedRef = (WeakReference<? extends Object>) queue.poll();
if (clearedRef != null) {
    System.out.println("收到弱引用清除通知");
}

// 示例 2：WeakHashMap 典型用法（canonicalizing mappings）
WeakHashMap<String, byte[]> cache = new WeakHashMap<>();
String key = "large_data";
byte[] value = new byte[1024 * 1024]; // 1MB 数据

cache.put(key, value);
System.out.println("缓存大小：" + cache.size()); // 1

// 当 key 不再被强引用时，对应的 entry 会在下次 GC 时被移除
key = null;
value = null;

System.gc();
// 注意：WeakHashMap 的清理是在访问 map 时触发的，或者依赖后台线程
System.out.println("缓存大小（可能已清理）：" + cache.size());
```

**虚引用（PhantomReference）**
- **触发条件**：对象无可达引用路径，已决定回收但尚未回收
- **行为**：referent 不会被自动清除（需手动处理），PhantomReference 入队
- **用途**：对象回收前的清理操作（最常见：**直接内存释放**，见 [DirectMemory](../../runtime/structure/DirectMemory.md)）
- **队列**：必须注册 ReferenceQueue，用于接收回调

**为什么需要虚引用？堆外内存自动释放的关键**

堆外内存（Direct Memory）不受 GC 管理，需要通过虚引用实现自动释放：

```
场景：使用 ByteBuffer.allocateDirect(1GB) 分配堆外内存

堆内（受 GC 管理）          堆外（不受 GC 管理）
├─ DirectByteBuffer 对象    └─ 1GB 实际内存
│   ├─ memoryAddress 字段       (C 语言 malloc 分配)
│   └─ Cleaner（虚引用）
│
流程：
1. buffer = allocateDirect(1GB) → 堆内创建小对象，堆外分配大内存
2. buffer = null → 取消引用
3. GC 回收堆内的 DirectByteBuffer → 对象消失
4. 虚引用感知 → 触发 Cleaner
5. Cleaner 调用 free(memoryAddress) → 释放堆外 1GB
```

**如果没有虚引用**：堆内小对象被 GC 回收了，但堆外 1GB 内存没人管 → **永久泄漏**

见 [DirectMemory](../../runtime/structure/DirectMemory.md) 了解完整机制。

**代码示例（虚引用）**
```java
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.nio.ByteBuffer;

// 示例 1：基本虚引用使用（监控对象回收）
Object obj = new Object();

// 必须创建 ReferenceQueue
ReferenceQueue<Object> queue = new ReferenceQueue<>();

// 创建虚引用（必须传入队列）
PhantomReference<Object> phantomRef = new PhantomReference<>(obj, queue);

// 注意：phantomRef.get() 永远返回 null，这是虚引用的特点
System.out.println("虚引用 get() 返回：" + phantomRef.get()); // null

// 清除强引用
obj = null;

// 建议 GC
System.gc();

// 等待 GC 完成
try {
    Thread.sleep(100);
} catch (InterruptedException e) {
    e.printStackTrace();
}

// 检查引用队列（阻塞方式）
try {
    PhantomReference<?> ref = (PhantomReference<?>) queue.remove(1000);
    if (ref != null) {
        System.out.println("收到虚引用通知，对象即将被回收");
        // 此时可以执行清理操作
        ref.clear(); // 手动清除虚引用
    }
} catch (InterruptedException e) {
    e.printStackTrace();
}

// 示例 2：直接内存释放的典型用法（Cleaner 模式）
class DirectMemoryResource {
    private final ByteBuffer directBuffer;
    private final PhantomReference<DirectMemoryResource> phantomRef;
    
    public DirectMemoryResource(int size) {
        this.directBuffer = ByteBuffer.allocateDirect(size);
        
        // 创建引用队列
        ReferenceQueue<DirectMemoryResource> queue = new ReferenceQueue<>();
        
        // 创建虚引用
        this.phantomRef = new PhantomReference<>(this, queue);
        
        // 启动清理线程（实际应用中通常使用 Cleaner 类）
        new Thread(() -> {
            try {
                PhantomReference<?> ref = (PhantomReference<?>) queue.remove();
                System.out.println("资源被回收，执行清理操作");
                // 清理直接内存（调用 Unsafe 或直接释放）
                ref.clear();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}

// 示例 3：使用 Java 9+ Cleaner 类（推荐方式）
import java.lang.ref.Cleaner;

class ResourceWithCleaner {
    private static final Cleaner cleaner = Cleaner.create();
    private final Cleaner.Cleanable cleanable;
    
    public ResourceWithCleaner() {
        // 创建清理动作
        cleanable = cleaner.register(this, () -> {
            System.out.println("ResourceWithCleaner 被回收，执行清理");
            // 执行清理操作
        });
    }
}
```

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
- 相关：
  - 可达性分析（见 [ReachabilityAnalysis.md](ReachabilityAnalysis.md)）
  - 直接内存（见 [../../runtime/structure/DirectMemory.md](../../runtime/structure/DirectMemory.md)）：虚引用用于堆外内存自动释放

## 把新概念挂回框架（多级索引轨迹）
java → jvm → gc → mechanism → ReferenceTypes

