# BitFieldSubCommands - Redis 位字段操作

> **来源**: `org.springframework.data.redis.connection.BitFieldSubCommands`  
> **作用**: 通过 StringRedisTemplate 执行 Redis 的 `BITFIELD` 命令，实现位级别的整数操作

---

## 1. 是什么？

`BitFieldSubCommands` 是 **Spring Data Redis** 提供的 API，用于构建和执行 Redis 的 `BITFIELD` 命令。

**核心能力**：把 Redis 字符串（String）当作**位数组**，在任意位置读写 1-64 位的整数。

---

## 2. 第一性原理

### 2.1 Redis 字符串的物理结构

```
Redis Key: "user:sign:1001"
Value (字节数组):
┌────────┬────────┬────────┬────────┐
│ Byte 0 │ Byte 1 │ Byte 2 │ Byte 3 │ ...
│ 0-7bit │ 8-15bit│16-23bit│24-31bit│
└────────┴────────┴────────┴────────┘
```

### 2.2 BITFIELD 的本质

把字节数组看作**连续的位序列**（Bit Array），你可以：
- **指定起始位置**（offset）：从第几个 bit 开始操作
- **指定位宽**（type）：操作多少位（支持 1-64 位）
- **执行操作**：读（GET）、写（SET）、递增（INCRBY）

**一句话**：像操作内存位域一样操作 Redis 字符串，实现极致的存储压缩。

---

## 3. API 详解

### 3.1 基本调用方式

```java
@Autowired
private StringRedisTemplate stringRedisTemplate;

// 执行 BITFIELD 命令
List<Long> results = stringRedisTemplate.opsForValue().bitField(
    key,
    BitFieldSubCommands.create()
        .get(BitFieldSubCommands.BitFieldType.unsigned(14))
        .valueAt(0)
);
```

### 3.2 位字段类型

```java
// 无符号整数（只能存正数）
BitFieldSubCommands.BitFieldType.unsigned(8)   // u8:  0 ~ 255
BitFieldSubCommands.BitFieldType.unsigned(16)  // u16: 0 ~ 65535
BitFieldSubCommands.BitFieldType.unsigned(32)  // u32: 0 ~ 2^32-1
BitFieldSubCommands.BitFieldType.UINT_8        // 同上，预定义常量
BitFieldSubCommands.BitFieldType.UINT_16
BitFieldSubCommands.BitFieldType.UINT_32
BitFieldSubCommands.BitFieldType.UINT_64

// 有符号整数（可存负数）
BitFieldSubCommands.BitFieldType.signed(8)     // i8:  -128 ~ 127
BitFieldSubCommands.BitFieldType.signed(16)    // i16: -32768 ~ 32767
BitFieldSubCommands.BitFieldType.INT_8         // 预定义常量
BitFieldSubCommands.BitFieldType.INT_16
BitFieldSubCommands.BitFieldType.INT_32
BitFieldSubCommands.BitFieldType.INT_64
```

### 3.3 三大操作

#### GET - 读取位字段

```java
// 从第 0 位开始，读取 14 位无符号整数
BitFieldSubCommands.create()
    .get(BitFieldSubCommands.BitFieldType.unsigned(14))
    .valueAt(0)

// 等效 Redis 命令：BITFIELD key GET u14 0
// 返回值：List<Long>，第一个元素就是读取的值
```

#### SET - 设置位字段

```java
// 将从第 8 位开始的 8 位设置为值 100
BitFieldSubCommands.create()
    .set(BitFieldSubCommands.BitFieldType.unsigned(8))
    .valueAt(8)
    .to(100)

// 等效 Redis 命令：BITFIELD key SET u8 8 100
// 返回值：旧值（被替换前的值）
```

#### INCRBY - 递增位字段

```java
// 将从第 0 位开始的 8 位无符号整数加 1
BitFieldSubCommands.create()
    .incrBy(BitFieldSubCommands.BitFieldType.unsigned(8))
    .valueAt(0)
    .by(1)

// 等效 Redis 命令：BITFIELD key INCRBY u8 0 1
```

### 3.4 溢出控制

```java
BitFieldSubCommands.create()
    .incrBy(BitFieldSubCommands.BitFieldType.unsigned(8))
    .valueAt(0)
    .by(1)
    .overflow(BitFieldSubCommands.BitFieldIncrBy.Overflow.WRAP)  // 回绕（默认）
    // .overflow(Overflow.SAT)  // 饱和：保持在最大/最小值
    // .overflow(Overflow.FAIL) // 失败：溢出时返回 null

// WRAP: 255 + 1 = 0（默认回绕）
// SAT:  255 + 1 = 255（饱和，保持在最大值）
// FAIL: 255 + 1 = null（失败，不执行）
```

### 3.5 批量操作（原子性）

```java
BitFieldSubCommands commands = BitFieldSubCommands.create()
    .get(BitFieldSubCommands.BitFieldType.unsigned(8)).valueAt(0)   // 读取第1个字段
    .set(BitFieldSubCommands.BitFieldType.unsigned(8)).valueAt(8).to(100)  // 设置第2个字段
    .incrBy(BitFieldSubCommands.BitFieldType.unsigned(16)).valueAt(16).by(1); // 递增第3个字段

List<Long> results = stringRedisTemplate.opsForValue().bitField(key, commands);
// 一个命令执行多个操作，原子性保证
```

---

## 4. 应用场景

### 4.1 用户签到系统（黑马点评场景）

**设计**：
- Key: `sign:userId:yyyyMM`（如 `sign:1001:202602`）
- 每天占 1 bit，1 个月最多 31 天 = 31 bits（约 4 字节）

**签到**（使用 setBit）：
```java
public void sign(Long userId) {
    LocalDateTime now = LocalDateTime.now();
    String key = String.format("sign:%d:%s", userId, 
        now.format(DateTimeFormatter.ofPattern("yyyyMM")));
    int dayOfMonth = now.getDayOfMonth();
    
    // SETBIT key offset 1（第1天对应 offset 0）
    stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);
}
```

**统计连续签到天数**（使用 BitFieldSubCommands）：
```java
public int signCount(Long userId) {
    LocalDateTime now = LocalDateTime.now();
    String key = String.format("sign:%d:%s", userId, 
        now.format(DateTimeFormatter.ofPattern("yyyyMM")));
    int dayOfMonth = now.getDayOfMonth();
    
    // 读取从第 0 位开始的 dayOfMonth 位
    // 例如今天14号，就读取14位，返回一个 14 位无符号整数
    List<Long> result = stringRedisTemplate.opsForValue().bitField(
        key,
        BitFieldSubCommands.create()
            .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth))
            .valueAt(0)
    );
    
    if (result == null || result.isEmpty() || result.get(0) == null) {
        return 0;
    }
    
    Long num = result.get(0);
    
    // 从低位开始统计连续 1 的个数（从"今天"往前数）
    int count = 0;
    while (true) {
        if ((num & 1) == 0) {  // 最低位是 0，停止
            break;
        }
        count++;
        num >>>= 1;  // 无符号右移
    }
    return count;
}
```

**为什么用 BITFIELD GET？**
- 一次命令读取整月签到记录（如 14 位）
- 返回一个整数，二进制形式就是签到位图
- 本地位运算统计连续天数，无需多次 Redis 调用
- 比存 31 个 key 节省 99% 空间

### 4.2 多字段紧凑存储

**场景**：一个 key 存储多个小属性

```java
// 位 0-7: 等级 (u8)
// 位 8-15: VIP 等级 (u8)
// 位 16-23: 状态标志 (u8)

public void setUserAttrs(Long userId) {
    String key = "user:attrs:" + userId;
    
    stringRedisTemplate.opsForValue().bitField(
        key,
        BitFieldSubCommands.create()
            .set(BitFieldSubCommands.BitFieldType.unsigned(8)).valueAt(0).to(50)
            .set(BitFieldSubCommands.BitFieldType.unsigned(8)).valueAt(8).to(3)
            .set(BitFieldSubCommands.BitFieldType.unsigned(8)).valueAt(16).to(1)
    );
}

public Map<String, Integer> getUserAttrs(Long userId) {
    String key = "user:attrs:" + userId;
    
    List<Long> results = stringRedisTemplate.opsForValue().bitField(
        key,
        BitFieldSubCommands.create()
            .get(BitFieldSubCommands.BitFieldType.unsigned(8)).valueAt(0)
            .get(BitFieldSubCommands.BitFieldType.unsigned(8)).valueAt(8)
            .get(BitFieldSubCommands.BitFieldType.unsigned(8)).valueAt(16)
    );
    
    Map<String, Integer> attrs = new HashMap<>();
    attrs.put("level", results.get(0).intValue());
    attrs.put("vipLevel", results.get(1).intValue());
    attrs.put("status", results.get(2).intValue());
    return attrs;
}
```

---

## 5. 与 BitMap 的关系

| 特性 | SETBIT/GETBIT | BITFIELD (BitFieldSubCommands) |
|------|--------------|--------------------------------|
| 操作粒度 | 单个 bit | 1-64 位整数 |
| 数值类型 | 只有 0/1 | u8, i16, u32 等整数 |
| 算术能力 | 无 | 支持 INCRBY 递增 |
| 适用场景 | 布尔标志 | 计数器、多字段存储 |

**选择建议**：
- 只存 true/false：用 `setBit` / `getBit`
- 存整数/计数器：用 `BitFieldSubCommands`
- 需要递增：必须用 BITFIELD 的 `INCRBY`

---

## 6. 性能与存储

### 6.1 存储效率

| 数据类型 | 传统方案 | BITFIELD 方案 | 节省 |
|---------|---------|--------------|------|
| 31 天签到 | 31 个 key | 31 bits (4 bytes) | ~99% |
| 3 个 u8 属性 | Hash 3 个 field | 3 bytes | ~90% |

### 6.2 时间复杂度

| 操作 | 复杂度 | 说明 |
|------|--------|------|
| GET | O(1) | 常数时间读取任意偏移 |
| SET | O(1) | 常数时间写入任意偏移 |
| INCRBY | O(1) | 常数时间递增 |
| 批量操作 | O(N) | N 为子命令数量，原子执行 |

---

## 7. 注意事项

### 7.1 返回值处理

```java
List<Long> results = stringRedisTemplate.opsForValue().bitField(key, commands);

// 必须判空
if (results == null || results.isEmpty()) {
    return 0;
}

// 每个元素可能为 null（如 OVERFLOW FAIL 时）
Long value = results.get(0);
if (value == null) {
    return 0;
}
```

### 7.2 位序（大端序）

Redis BITFIELD 使用**大端序**（高位在前）：
```
Byte 0:  bit 0-7
Byte 1:  bit 8-15
Byte 2:  bit 16-23
...
```

### 7.3 对齐建议

- 尽量按 8 位边界对齐（u8, u16, u32, u64）
- 非对齐访问（如 u5, u3）虽然支持，但实现复杂

---

## 8. 总结

**核心认知**：

1. **BitFieldSubCommands** 是 Spring Data Redis 对 Redis `BITFIELD` 命令的 Java 封装

2. **底层原理**：把 Redis 字符串当作位数组，支持任意偏移、任意位宽的整数读写和递增

3. **三大操作**：
   - `get(type).valueAt(offset)` → 读取
   - `set(type).valueAt(offset).to(value)` → 设置
   - `incrBy(type).valueAt(offset).by(delta)` → 递增

4. **核心价值**：极致存储压缩（比传统方案节省 90%+）、原子批量操作、位级内存寻址

**一句话**：BitFieldSubCommands 让你像操作内存位域一样操作 Redis 字符串，实现高效的位级数据存储和计算。

---

**参考**：
- 官方文档：https://docs.spring.io/spring-data/redis/docs/current/api/org/springframework/data/redis/connection/BitFieldSubCommands.html
- Redis BITFIELD：https://redis.io/commands/bitfield/
