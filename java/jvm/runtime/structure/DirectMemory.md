---
type: concept
tags:
  - java/jvm
  - jvm
  - runtime
  - structure
  - direct-memory
  - off-heap
---

# DirectMemory（直接内存 / 堆外内存）

## 一句话
直接内存（Direct Memory）是 Java 进程可使用的堆外内存形态之一，不受 Java GC 直接管理，常用于 NIO、大对象缓存等场景。

## 严格定义
直接内存用于描述不在 Java 堆内分配的内存区域（off-heap）：其分配与释放由具体 API 与 JVM 实现协作完成，通常不受堆 GC 的直接管理，但其可达性与回收时机可能与 Java 对象的可达性相关联（实现相关）。

## JVM 内存布局（简化版）

```
操作系统内存（4GB/8GB/16GB...）
├── JVM 进程内存
│   ├── 堆内存（Heap，受 GC 管理）
│   │   ├── 新生代（Eden/Survivor）
│   │   └── 老年代（Old Gen）
│   │       └── 你写的 new Object() 都在这里
│   │
│   ├── 元空间（Metaspace，类信息）
│   ├── 虚拟机栈（Stack，方法调用）
│   └── 直接内存（Direct Memory，堆外内存的一种）
│
└── 其他进程内存（操作系统、其他程序）
```

**关键区别**：
- **堆内内存**：`new byte[1024]` → GC 自动回收，你只管创建，不管释放
- **堆外内存**：通过 `Unsafe.allocateMemory()` 或 `ByteBuffer.allocateDirect()` → **GC 不管**，必须手动释放（类似 C 语言的 `malloc/free`）

## 为什么会有堆外内存？

### 1. 避免 GC 停顿（大对象场景）

假设你要处理 **1GB 的视频文件**：

```java
// 方式 1：堆内内存（受 GC 管理）
byte[] data = new byte[1024 * 1024 * 1024]; // 1GB 大数组
// 问题：这个数组在堆内，GC 扫描时要遍历它，导致停顿时间变长
// 而且大对象可能直接进入老年代，难以回收
```

```java
// 方式 2：堆外内存（不受 GC 管理）
ByteBuffer data = ByteBuffer.allocateDirect(1024 * 1024 * 1024); // 1GB
// 好处：GC 扫描堆时看不到这块内存，不会增加 GC 负担
// 坏处：你必须自己记得释放，否则内存泄漏（操作系统层面）
```

### 2. 零拷贝（Zero Copy）与 IO 性能

堆外内存可以直接与操作系统内核交互，不需要在"堆内 <-> 堆外"之间复制数据：

```java
// 网络编程/文件读写常用
SocketChannel.read(ByteBuffer.allocateDirect(1024)); 
// 数据直接从网卡 DMA 到堆外内存，省去了复制到堆内的一次拷贝
```

## 堆外内存的危险：内存泄漏

因为 **GC 不管堆外内存**，如果你忘记释放，就会像 C 语言一样内存泄漏：

```java
public void process() {
    ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024 * 100); // 100MB
    // 使用 buffer...
    // 方法结束，buffer 引用消失
    // 但是！堆外内存的 100MB 还在！GC 不会帮你回收！
}
```

**重复调用这个方法 → 内存不断增长 → 系统 OOM（OutOfMemory）**

## 虚引用如何救场？

这就是虚引用的用武之地：**当堆内的 `DirectByteBuffer` 对象被 GC 回收时，自动触发堆外内存的释放**。

```java
// DirectByteBuffer 内部实现（简化）
class DirectByteBuffer {
    long memoryAddress; // 指向堆外内存的地址（C 语言指针）
    
    DirectByteBuffer(int size) {
        this.memoryAddress = Unsafe.allocateMemory(size); // 在堆外分配内存
        
        // 创建虚引用：当 this（堆内对象）被回收时，通知我
        Cleaner.create(this, () -> Unsafe.freeMemory(memoryAddress));
        // Cleaner 内部用 PhantomReference 实现
    }
}
```

**流程**：
1. 你创建 `DirectByteBuffer`（堆内小对象，持有堆外大内存的地址）
2. 使用完毕后，你不再引用它：`buffer = null`
3. **GC 回收堆内的小对象**（因为没人引用它了）
4. **虚引用感知到对象已死**，触发 `Cleaner` 线程
5. `Cleaner` 调用 `freeMemory(memoryAddress)`，释放堆外内存

**如果没有虚引用**：堆内小对象被回收了，但堆外 1GB 内存没人管，**永久泄漏**。

见 [gc/mechanism/ReferenceTypes.md](../../gc/mechanism/ReferenceTypes.md) 了解虚引用详细机制。

## 代码示例

### 示例 1：基本使用（NIO Direct Buffer）

```java
import java.nio.ByteBuffer;

// 分配 100MB 堆外内存
ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024 * 1024 * 100);

// 像普通 Buffer 一样使用
directBuffer.putInt(42);
directBuffer.flip();
int value = directBuffer.getInt();

// 使用完毕后，虚引用机制会自动释放堆外内存
// 但你也可以手动提示：
directBuffer = null; // 取消引用，让 GC 可以回收
System.gc(); // 建议 GC（不保证立即执行）
```

### 示例 2：使用 Unsafe（不推荐，需小心）

```java
import sun.misc.Unsafe;
import java.lang.reflect.Field;

public class UnsafeDemo {
    public static void main(String[] args) throws Exception {
        // 通过反射获取 Unsafe 实例
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        
        // 分配堆外内存（类似 C 的 malloc）
        long address = unsafe.allocateMemory(1024 * 1024); // 1MB
        
        // 读写内存
        unsafe.putInt(address, 123);
        int value = unsafe.getInt(address);
        System.out.println("Value: " + value);
        
        // 必须手动释放！（类似 C 的 free）
        unsafe.freeMemory(address);
    }
}
```

### 示例 3：Cleaner 模式（堆外内存自动释放）

```java
import java.lang.ref.Cleaner;

public class OffHeapResource implements AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();
    private final Cleaner.Cleanable cleanable;
    private final long address;
    
    // 清理动作（必须是 Runnable，不能引用外部对象）
    private static class CleanupTask implements Runnable {
        private final long address;
        
        CleanupTask(long address) {
            this.address = address;
        }
        
        @Override
        public void run() {
            System.out.println("释放堆外内存：" + address);
            // 这里调用 Unsafe.freeMemory(address) 或 JNI 释放
        }
    }
    
    public OffHeapResource(int size) {
        // 分配堆外内存（简化，实际用 Unsafe 或 JNI）
        this.address = allocateMemory(size);
        
        // 注册 Cleaner：当 this 被回收时，执行清理任务
        this.cleanable = cleaner.register(this, new CleanupTask(address));
    }
    
    private long allocateMemory(int size) {
        // 实际实现：调用 Unsafe.allocateMemory 或 JNI
        return System.currentTimeMillis(); // 模拟地址
    }
    
    @Override
    public void close() {
        // 手动触发清理
        cleanable.clean();
    }
}

// 使用
OffHeapResource resource = new OffHeapResource(1024 * 1024 * 100); // 100MB
// 使用 resource...
resource.close(); // 或等待 GC 自动触发 Cleaner
```

## 接口：数据 + 约束

### JVM 参数
- `-XX:MaxDirectMemorySize=<size>`：设置直接内存上限（默认约等于堆最大内存）

### 内存溢出
- 直接内存不足时抛出 `OutOfMemoryError: Direct buffer memory`

- 数据：
  - 堆外内存块与其 Java 侧引用对象（例如 direct buffer，对象类型实现相关）
- 输入：
  - 堆外分配请求（例如 NIO direct buffer 创建）
- 输出：
  - 堆外内存块的生命周期变化（分配/释放）
- 约束：
  - 直接内存不属于堆的语义边界（见 [Heap.md](Heap.md)）；上限与回收机制由实现与参数决定（实现相关）。
  - 堆外内存泄漏不会触发 GC，必须通过虚引用机制或手动释放

## 常用构造/操作（仅列出接口与符号）

### 分配方式
- `ByteBuffer.allocateDirect(int capacity)`：NIO 方式，最常用
- `Unsafe.allocateMemory(long bytes)`：底层方式，需谨慎
- `JNI`：C/C++ 代码中分配，通过 native 方法暴露

### JVM 参数
- 直接内存上限参数：`-XX:MaxDirectMemorySize`（实现相关）

## 关系：上级/下级/等价/特例/推广

- 上级：Runtime / structure（运行时数据区结构）。
- 相关：
  - Heap（堆，见 [Heap.md](Heap.md)）
  - 虚引用（见 [../../gc/mechanism/ReferenceTypes.md](../../gc/mechanism/ReferenceTypes.md)）：用于堆外内存自动释放

## 把新概念挂回框架（多级索引轨迹）

java → jvm → runtime → structure → DirectMemory

