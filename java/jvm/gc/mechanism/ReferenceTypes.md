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
// 记录softreference
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

// 必须创建 ReferenceQueue----
//ReferenceQueue<? extends Object> head -- null
//
ReferenceQueue<Object> queue = new ReferenceQueue<>();
// 创建虚引用（必须传入队列）
// Reference 父类的关键逻辑
//Reference(T referent, ReferenceQueue<? super T> queue) {
//    this.referent = referent;
//    this.queue = (queue == null) ? ReferenceQueue.NULL : queue;
//    // 把 queue 的引用保存下来！
//}

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
	    new Thread(()->{
		    try{
			    PhantomReference<?> ref = (PhantomReference<?>) queue.remove();
			    System.out.println("资源被回收，执行清理操作");
                // 清理直接内存（调用 Unsafe 或直接释放）
                ref.clear();
		    } catch (InterruptedException e){
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

### 引用队列使用模式与最佳实践

**问题**：`ReferenceQueue` 中拿到的 `Reference` 对象，如何知道它对应哪个业务 Key？

原生的 `SoftReference<byte[]>` 只包含：
- `referent`（指向数据的指针，已 null）
- `queue`（指向队列）
- `next`（链表指针）

**它不知道自己是"美女.jpg"还是"data_001"**。实际工程中有两种解决方案：

#### 方案 1：自定义引用子类存储元数据（推荐）

继承引用类，添加字段存储业务标识信息：

```java
// 适用于所有引用类型（Soft/Weak/Phantom）
class KeyedSoftReference extends SoftReference<byte[]> {
    String key;  // 存储业务 key，用于识别
    
    KeyedSoftReference(String key, byte[] data, ReferenceQueue<byte[]> queue) {
        super(data, queue);
        this.key = key;
    }
}

// 使用
Map<String, KeyedSoftReference> cache = new HashMap<>();
ReferenceQueue<byte[]> queue = new ReferenceQueue<>();

cache.put("美女.jpg", new KeyedSoftReference("美女.jpg", imageData, queue));

// 清理时
KeyedSoftReference deadRef = (KeyedSoftReference) queue.poll();
if (deadRef != null) {
    String key = deadRef.key;  // ← 现在你知道了！
    cache.remove(key);
    System.out.println("已清理: " + key);
}
```

**优点**：
- O(1) 时间获取 key
- 可扩展存储更多元数据（如创建时间、大小等）

**适用场景**：所有需要识别被回收引用的场景（缓存清理、连接池管理、资源追踪等）

#### 方案 2：反向查找（不推荐，性能差）

如果不想自定义类，就只能遍历查找：

```java
SoftReference<byte[]> deadRef = (SoftReference<byte[]>) queue.poll();
if (deadRef != null) {
    // 笨办法：遍历找哪个 value 等于 deadRef
    for (Map.Entry<String, SoftReference<byte[]>> entry : cache.entrySet()) {
        if (entry.getValue() == deadRef) {
            cache.remove(entry.getKey());
            break;
        }
    }
}
```

**缺点**：
- O(n) 时间复杂度
- 高并发时遍历整个 map 性能差
- 可能找不到（如果已被其他线程移除）

#### 方案 3：使用 WeakHashMap（特定场景）

如果是 key-value 映射，且希望 key 不再被引用时自动清理 entry：

```java
// key 是弱引用，value 是强引用
WeakHashMap<String, byte[]> cache = new WeakHashMap<>();

// 当 key 不再被强引用时，对应的 entry 会在下次 GC 时被移除
String key = new String("temp_key");
cache.put(key, data);

key = null; // key 不再被强引用
System.gc();
// entry 会自动从 map 中移除
```

**注意**：WeakHashMap 的 value 是强引用，如果 value 持有 key 的引用会形成循环依赖。

### 总结

| 方案 | 时间复杂度 | 适用场景 | 备注 |
|-----|-----------|---------|------|
| **自定义引用子类** | O(1) | 所有需要识别引用的场景 | 推荐，灵活可扩展 |
| **反向查找** | O(n) | 简单原型、数据量小 | 不推荐生产环境 |
| **WeakHashMap** | - | key-value 缓存，key 弱引用 | 特定场景，注意 value 引用 |

**核心原则**：`ReferenceQueue` 通常配合**自定义 Reference 子类**使用，而不是直接用原生 `SoftReference/WeakReference/PhantomReference`。

## 代码速查（Reference Code Snippets）

### 1. 基础引用创建与使用

```java
import java.lang.ref.*;

// 强引用（默认）
Object strongRef = new Object();

// 软引用
SoftReference<Object> softRef = new SoftReference<>(new Object());
Object obj = softRef.get();  // 可能返回 null（被回收）

// 弱引用
WeakReference<Object> weakRef = new WeakReference<>(new Object());
Object obj2 = weakRef.get();  // 可能返回 null

// 虚引用（get() 永远返回 null）
PhantomReference<Object> phantomRef = new PhantomReference<>(
    new Object(), 
    new ReferenceQueue<>()
);
Object obj3 = phantomRef.get();  // 永远为 null
```

### 2. 引用队列基础用法

```java
import java.lang.ref.*;

// 创建队列
ReferenceQueue<Object> queue = new ReferenceQueue<>();

// 创建引用并注册队列
WeakReference<Object> ref = new WeakReference<>(new Object(), queue);

// 取消引用，建议 GC
ref.clear();  // 或让对象变成不可达
System.gc();

// 从队列获取通知
Reference<?> deadRef = queue.poll();  // 非阻塞
Reference<?> deadRef2 = queue.remove(1000);  // 阻塞等待1秒
```

### 3. 方案一：自定义引用子类（推荐）

```java
import java.lang.ref.*;
import java.util.HashMap;
import java.util.Map;

// 自定义软引用，存储业务 key
class KeyedSoftReference extends SoftReference<byte[]> {
    final String key;  // 业务标识
    final long createTime;  // 可扩展更多字段
    
    KeyedSoftReference(String key, byte[] data, ReferenceQueue<byte[]> queue) {
        super(data, queue);
        this.key = key;
        this.createTime = System.currentTimeMillis();
    }
}

// 使用
public class CacheWithCleanUp {
    private final Map<String, KeyedSoftReference> cache = new HashMap<>();
    private final ReferenceQueue<byte[]> queue = new ReferenceQueue<>();
    
    public void put(String key, byte[] data) {
        cache.put(key, new KeyedSoftReference(key, data, queue));
    }
    
    // 清理被回收的引用
    public void cleanUp() {
        KeyedSoftReference ref;
        while ((ref = (KeyedSoftReference) queue.poll()) != null) {
            System.out.println("清理: " + ref.key + 
                             ", 存活时间: " + (System.currentTimeMillis() - ref.createTime));
            cache.remove(ref.key);  // O(1) 删除
        }
    }
}
```

### 4. 方案二：反向查找（不推荐）

```java
import java.lang.ref.*;
import java.util.*;

public class ReverseLookupExample {
    private final Map<String, SoftReference<byte[]>> cache = new HashMap<>();
    private final ReferenceQueue<byte[]> queue = new ReferenceQueue<>();
    
    public void cleanUpSlow() {
        SoftReference<byte[]> deadRef = (SoftReference<byte[]>) queue.poll();
        if (deadRef != null) {
            // O(n) 遍历查找
            for (Iterator<Map.Entry<String, SoftReference<byte[]>>> it = 
                 cache.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, SoftReference<byte[]>> entry = it.next();
                if (entry.getValue() == deadRef) {
                    it.remove();
                    System.out.println("找到并删除: " + entry.getKey());
                    break;
                }
            }
        }
    }
}
```

### 5. 方案三：WeakHashMap（特定场景）

```java
import java.util.WeakHashMap;

public class WeakHashMapExample {
    // key 是弱引用，value 是强引用
    private final WeakHashMap<String, byte[]> cache = new WeakHashMap<>();
    
    public void add(String key, byte[] data) {
        cache.put(key, data);
    }
    
    public void demonstrate() {
        String key = new String("temp_data");
        cache.put(key, new byte[1024 * 1024]);  // 1MB
        
        System.out.println("清理前大小: " + cache.size());  // 1
        
        key = null;  // 取消 key 的强引用
        System.gc();  // 建议 GC
        
        // 注意：WeakHashMap 的清理是在访问时触发的
        System.out.println("访问后大小: " + cache.size());  // 可能为 0
    }
}
```

### 6. 虚引用 + Cleaner 清理堆外内存

```java
import java.lang.ref.*;
import java.nio.ByteBuffer;

public class OffHeapCleaner {
    private final ReferenceQueue<ByteBuffer> queue = new ReferenceQueue<>();
    
    public ByteBuffer allocateDirect(int size) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);
        
        // 创建虚引用监听 buffer 的回收
        PhantomReference<ByteBuffer> phantom = 
            new PhantomReference<>(buffer, queue);
        
        // 启动清理线程
        new Thread(() -> {
            try {
                PhantomReference<?> ref = (PhantomReference<?>) queue.remove();
                System.out.println("ByteBuffer 被回收，清理堆外内存");
                // 实际项目中调用 Unsafe.freeMemory
                ref.clear();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        return buffer;
    }
}

// Java 9+ 推荐方式
import java.lang.ref.Cleaner;

public class ModernCleaner {
    private static final Cleaner cleaner = Cleaner.create();
    
    public ByteBuffer allocateWithCleaner(int size) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(size);
        
        cleaner.register(buffer, () -> {
            System.out.println("Cleaner 清理堆外内存");
            // 执行清理操作
        });
        
        return buffer;
    }
}
```

### 7. 完整缓存实现示例

```java
import java.lang.ref.*;
import java.util.*;
import java.util.concurrent.*;

public class SoftReferenceCache<K, V> {
    private final Map<K, Node<K, V>> cache = new ConcurrentHashMap<>();
    private final ReferenceQueue<V> queue = new ReferenceQueue<>();
    
    // 带 key 的软引用
    private static class Node<K, V> extends SoftReference<V> {
        final K key;
        
        Node(K key, V value, ReferenceQueue<V> queue) {
            super(value, queue);
            this.key = key;
        }
    }
    
    public void put(K key, V value) {
        cleanUp();  // 先清理
        cache.put(key, new Node<>(key, value, queue));
    }
    
    public V get(K key) {
        cleanUp();
        Node<K, V> node = cache.get(key);
        return node != null ? node.get() : null;
    }
    
    // 后台清理线程
    public void startCleaner() {
        Thread cleaner = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    Node<K, V> node = (Node<K, V>) queue.remove();
                    cache.remove(node.key);
                    System.out.println("自动清理: " + node.key);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        cleaner.setDaemon(true);
        cleaner.start();
    }
    
    // 手动清理
    public void cleanUp() {
        Node<K, V> node;
        while ((node = (Node<K, V>) queue.poll()) != null) {
            cache.remove(node.key);
        }
    }
}

// 使用
SoftReferenceCache<String, byte[]> imageCache = new SoftReferenceCache<>();
imageCache.startCleaner();  // 启动后台清理

imageCache.put("photo1.jpg", new byte[1024 * 1024 * 10]);  // 10MB
byte[] data = imageCache.get("photo1.jpg");  // 获取
```

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

