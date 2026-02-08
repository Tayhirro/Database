# MyBatis-Plus Page - 分页对象

> **来源**: `com.baomidou.mybatisplus.extension.plugins.pagination.Page<T>`  
> **作用**: 封装分页查询的参数和结果，统一分页数据格式

---

## 1. 是什么？

**Page** 是 MyBatis-Plus 提供的**分页对象**，用于：
1. **传入参数**：告诉数据库"我要第几页，每页多少条"
2. **接收结果**：数据库返回"总记录数、总页数、当前页数据"

### 1.1 核心关系

```
Controller (接收前端参数)
    ↓ 创建 Page 对象
Page<T> page = new Page<>(current, size)  ← 传入页码和每页大小
    ↓ 传给 Service
IService.page(page, wrapper)  ← 执行分页查询
    ↓ 返回填充好的 Page
Page<T> (包含 total, pages, records)
    ↓ 返回给前端
JSON: { current: 1, size: 10, total: 100, pages: 10, records: [...] }
```

---

## 2. 核心属性

| 属性 | 类型 | 说明 | 示例 |
|------|------|------|------|
| `current` | long | **当前页码**（从1开始） | 第3页 → current=3 |
| `size` | long | **每页显示条数** | 每页10条 → size=10 |
| `total` | long | **总记录数**（数据库返回） | 共100条 → total=100 |
| `pages` | long | **总页数**（自动计算） | 共10页 → pages=10 |
| `records` | List<T> | **当前页数据列表** | 当前页的10条记录 |

### 2.1 属性关系公式

```
total = 总记录数（数据库 COUNT(*) 得到）
pages = (total + size - 1) / size  ← 向上取整
offset = (current - 1) * size       ← 数据库 OFFSET
```

**示例**：
```
总记录数 total = 95 条
每页大小 size = 10 条

总页数 pages = (95 + 10 - 1) / 10 = 10 页

第1页：OFFSET = (1-1) * 10 = 0   → 查第 1-10 条
第2页：OFFSET = (2-1) * 10 = 10  → 查第 11-20 条
```

---

## 3. 构造方法

```java
// 1. 无参构造（current=1, size=10）
Page<User> page = new Page<>();

// 2. 指定当前页和每页大小（最常用）
Page<User> page = new Page<>(3, 10);  // 第3页，每页10条

// 3. 不查询总记录数（性能优化）
Page<User> page = new Page<>(3, 10, false);
```

### 3.1 泛型的作用

```java
// Page<User> 表示这个分页对象里的 records 是 List<User>
Page<User> page = new Page<>(1, 10);
// page.getRecords() 返回的是 List<User>

// Page<Order> 表示分页对象里的 records 是 List<Order>
Page<Order> page = new Page<>(1, 10);
```

---

## 4. 完整使用示例

### 4.1 Controller 层（接收前端参数）

```java
@GetMapping("/shop/list")
public Result queryShopByType(
        @RequestParam("typeId") Integer typeId,
        @RequestParam(value = "current", defaultValue = "1") Integer current,
        @RequestParam(value = "x", required = false) Double x,
        @RequestParam(value = "y", required = false) Double y) {
    
    // 1. 创建 Page 对象，传入当前页和每页大小
    Page<Shop> page = new Page<>(current, 10);
    
    // 2. 调用 Service 执行分页查询
    Page<Shop> result = shopService.queryShopByType(page, typeId, x, y);
    
    // 3. 返回给前端（包含 current, size, total, pages, records）
    return Result.ok(result);
}
```

### 4.2 Service 层（执行分页查询）

```java
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService {
    
    @Override
    public Page<Shop> queryShopByType(Page<Shop> page, Integer typeId, Double x, Double y) {
        
        if (x == null || y == null) {
            // 使用 IService 自带的分页方法
            return page(
                page,
                Wrappers.<Shop>lambdaQuery()
                    .eq(Shop::getTypeId, typeId)
                    .orderByDesc(Shop::getCreateTime)
            );
        }
        
        // 自定义 SQL 分页（多表关联、复杂查询）
        return baseMapper.selectShopByTypeAndLocation(page, typeId, x, y);
    }
}
```

### 4.3 Mapper 层（自定义分页 SQL）

```java
@Mapper
public interface ShopMapper extends BaseMapper<Shop> {
    
    /**
     * 根据类型和位置分页查询店铺
     * @param page 分页对象（传入 current 和 size，返回 total 和 records）
     */
    IPage<Shop> selectShopByTypeAndLocation(
        @Param("page") Page<Shop> page, 
        @Param("typeId") Integer typeId,
        @Param("x") Double x, 
        @Param("y") Double y
    );
}
```

### 4.4 XML（自定义 SQL）

```xml
<select id="selectShopByTypeAndLocation" resultType="com.example.entity.Shop">
    SELECT s.*,
        ROUND(ST_DISTANCE_SPHERE(POINT(s.x, s.y), POINT(#{x}, #{y}))) AS distance
    FROM shop s
    WHERE s.type_id = #{typeId}
    ORDER BY distance ASC
</select>
```

---

## 5. 获取分页结果

```java
Page<User> page = userService.page(new Page<>(2, 10));

// 获取各种分页信息
long current = page.getCurrent();      // 当前页码：2
long size = page.getSize();            // 每页大小：10
long total = page.getTotal();          // 总记录数：95
long pages = page.getPages();          // 总页数：10
List<User> records = page.getRecords(); // 当前页数据：List<User>

// 判断是否有上一页/下一页
boolean hasPrevious = page.hasPrevious();  // true
boolean hasNext = page.hasNext();          // true
```

---

## 6. 常用场景

### 6.1 前端分页组件需要的数据

```java
// 前端 Vue/React 分页组件通常需要：
{
    "current": 2,          // 当前第2页
    "size": 10,            // 每页10条
    "total": 95,           // 总共95条
    "pages": 10,           // 总共10页
    "records": [           // 当前页数据
        { "id": 11, "name": "店铺11" },
        ...
    ]
}

// 直接返回 Page 对象即可
@GetMapping("/list")
public Result list(@RequestParam(defaultValue = "1") int current) {
    Page<User> page = userService.page(new Page<>(current, 10));
    return Result.ok(page);
}
```

### 6.2 不分页只查总数

```java
Page<User> page = new Page<>(1, 10);
page(page, null);  // 只执行 COUNT(*)
long total = page.getTotal();
```

### 6.3 性能优化（不查总数）

```java
// 第3个参数 false 表示不查总数
Page<User> page = new Page<>(1, 10, false);
page(page, null);

// 适合"无限滚动"场景（如朋友圈、微博流）
```

---

## 7. Page vs IPage

| 对比 | Page<T> | IPage<T> |
|------|---------|----------|
| **类型** | 实现类 | 接口 |
| **用途** | 创建分页对象 | Mapper 方法返回值类型 |
| **关系** | implements IPage<T> | 被 Page 实现 |

**使用建议**：
- **Controller/Service**：用 `Page<T>`（创建对象）
- **Mapper 接口**：用 `IPage<T>`（返回值）

```java
// Controller
Page<User> page = new Page<>(current, size);

// Mapper
IPage<User> selectUserPage(Page<User> page, @Param("name") String name);
```

---

## 8. 注意事项

### 8.1 页码从 1 开始

```java
// 正确：第1页
Page<User> page = new Page<>(1, 10);
```

### 8.2 必须配置分页插件

```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 必须添加分页插件！
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

**不配置的后果**：
- `page.getTotal()` = 0（COUNT 没执行）
- `page.getPages()` = 0

### 8.3 泛型不能省

```java
// 错误：没有泛型
Page page = new Page<>(1, 10);

// 正确：指定泛型
Page<User> page = new Page<>(1, 10);
```

---

## 9. 总结

**Page 的核心作用**：

1. **作为参数**：告诉 MyBatis-Plus "我要第几页，每页几条"
2. **作为结果**：接收数据库返回的 "总记录数、总页数、当前页数据"
3. **统一格式**：前后端分页数据格式统一

**使用流程**：
```
1. 前端传 current（第几页）
2. 后端 new Page<>(current, 10) 创建分页对象
3. 传给 IService.page() 或自定义 Mapper
4. MyBatis-Plus 自动执行：COUNT(*) + SELECT ... LIMIT ... OFFSET ...
5. 返回填充好的 Page 对象给前端
```

**一句话**：Page 是 MyBatis-Plus 分页的**核心载体**，传入页码和大小，返回完整分页信息。

---

**参考**：
- 官方文档：https://baomidou.com/pages/97710a/
