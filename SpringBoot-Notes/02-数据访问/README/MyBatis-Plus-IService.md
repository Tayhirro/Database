# MyBatis-Plus IService - Service 层 CRUD 封装

> **来源**: `com.baomidou.mybatisplus.extension.service.IService`  
> **作用**: 封装 Service 层常用 CRUD 操作，无需手写 SQL 即可实现数据库增删改查

---

## 1. 是什么？

**IService** 是 MyBatis-Plus 提供的**通用 Service 层接口**，封装了数据库常见的增删改查操作。通过继承该接口，你的 Service 层可以自动获得一套完整的 CRUD 方法。

### 1.1 与传统开发的对比

**传统写法（繁琐）**：
```java
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public User getById(Long id) {
        return userMapper.selectById(id);
    }
    
    public boolean save(User user) {
        return userMapper.insert(user) > 0;
    }
    
    public boolean updateById(User user) {
        return userMapper.updateById(user) > 0;
    }
    
    public boolean removeById(Long id) {
        return userMapper.deleteById(id) > 0;
    }
    // ... 还要写一堆方法
}
```

**使用 IService（简洁）**：
```java
public interface UserService extends IService<User> {
    // 无需写任何方法，IService 已提供
}

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    // 无需写任何实现，ServiceImpl 已提供
}
```

---

## 2. 核心架构

```
Controller 层
     ↓
Service 接口 extends IService<T>  ← 定义规范
     ↓
ServiceImpl extends ServiceImpl<M, T> implements Service  ← 提供实现
     ↓
Mapper extends BaseMapper<T>  ← 数据库操作
     ↓
数据库
```

**命名规范**：
- `get` 开头 → 查询单条
- `remove` 开头 → 删除操作
- `list` 开头 → 查询列表
- `page` 开头 → 分页查询
- `save` 开头 → 插入/保存
- `update` 开头 → 更新操作

---

## 3. 核心方法详解

### 3.1 Save 系列（插入）

```java
// 插入一条记录（save = insert）
boolean save(T entity);

// 批量插入（推荐，效率高）
boolean saveBatch(Collection<T> entityList);

// 批量插入，指定每批大小（分批提交，防止内存溢出）
boolean saveBatch(Collection<T> entityList, int batchSize);

// 插入或更新（根据 ID 判断，ID 存在则更新，不存在则插入）
boolean saveOrUpdate(T entity);

// 批量插入或更新
boolean saveOrUpdateBatch(Collection<T> entityList);

// 批量插入或更新，指定每批大小
boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize);
```

**使用示例**：
```java
// 单条插入
User user = new User();
user.setName("张三");
user.setAge(20);
userService.save(user);

// 批量插入
List<User> users = Arrays.asList(
    new User("张三", 20),
    new User("李四", 25),
    new User("王五", 30)
);
userService.saveBatch(users);

// 分批批量插入（每批 500 条，防止内存溢出）
userService.saveBatch(userList, 500);

// 插入或更新
User user = new User();
user.setId(1L);  // 有 ID 则更新，无 ID 则插入
user.setName("张三");
userService.saveOrUpdate(user);
```

### 3.2 Remove 系列（删除）

```java
// 根据 ID 删除
boolean removeById(Serializable id);

// 根据实体 ID 删除
boolean removeById(T entity);

// 根据实体 ID 删除（可指定是否填充逻辑删除字段）
boolean removeById(T entity, boolean useFill);

// 根据条件删除（QueryWrapper 构造条件）
boolean remove(Wrapper<T> queryWrapper);

// 根据 ID 批量删除
boolean removeByIds(Collection<?> list);

// 根据 ID 批量删除（可指定是否填充逻辑删除字段）
boolean removeByIds(Collection<?> list, boolean useFill);

// 根据 ID 批量删除（真正的批处理，效率更高）
boolean removeBatchByIds(Collection<?> list);

// 根据 ID 批量删除（真正的批处理，可指定每批大小）
boolean removeBatchByIds(Collection<?> list, int batchSize);

// 根据 ID 批量删除（真正的批处理，可指定是否填充逻辑删除字段）
boolean removeBatchByIds(Collection<?> list, boolean useFill);

// 根据 ID 批量删除（真正的批处理，完整参数版本）
boolean removeBatchByIds(Collection<?> list, int batchSize, boolean useFill);

// 根据 map 条件删除（key: 字段名, value: 字段值）
boolean removeByMap(Map<String, Object> columnMap);
```

**使用示例**：
```java
// 根据 ID 删除
userService.removeById(1L);

// 根据条件删除（删除年龄大于 30 的用户）
userService.remove(
    Wrappers.<User>lambdaQuery()
        .gt(User::getAge, 30)
);

// 批量删除（普通方式）
userService.removeByIds(Arrays.asList(1L, 2L, 3L));

// 批量删除（真正的批处理，效率更高，推荐大批量删除时使用）
userService.removeBatchByIds(Arrays.asList(1L, 2L, 3L));

// 批量删除（指定每批大小，防止 SQL 过长）
userService.removeBatchByIds(idList, 500);

// 根据 map 条件删除（删除 name=张三 且 age=20 的记录）
Map<String, Object> map = new HashMap<>();
map.put("name", "张三");
map.put("age", 20);
userService.removeByMap(map);

// 使用逻辑删除填充（需配合逻辑删除配置）
userService.removeById(1L, true);  // useFill = true
```

### 3.3 Get 系列（单条查询）

```java
// 根据 ID 查询
T getById(Serializable id);

// 根据 ID 查询，返回 Optional（推荐，避免空指针）
default Optional<T> getOptById(Serializable id);

// 根据条件查询一条记录（结果超过一条时只取第一条）
default T getOne(Wrapper<T> queryWrapper);

// 根据条件查询一条记录
// throwEx = true：结果超过一条则抛出异常
// throwEx = false：结果超过一条只取第一条
default T getOne(Wrapper<T> queryWrapper, boolean throwEx);

// 根据 Wrapper 条件查询，返回 Map（key: 字段名, value: 字段值）
Map<String, Object> getMap(Wrapper<T> queryWrapper);

// 根据 Wrapper 条件查询，返回 Object
<V> V getObj(Wrapper<T> queryWrapper, Function<? super Object, V> mapper);
```

**使用示例**：
```java
// 根据 ID 查询
User user = userService.getById(1L);

// 使用 Optional（推荐，避免空指针）
Optional<User> userOpt = userService.getOptById(1L);
userOpt.ifPresent(u -> System.out.println(u.getName()));

// 根据条件查询一条（查询 name=张三 的用户）
User user = userService.getOne(
    Wrappers.<User>lambdaQuery()
        .eq(User::getName, "张三")
);

// 查询结果强制一条（超一条抛异常）
User user = userService.getOne(
    Wrappers.<User>lambdaQuery()
        .eq(User::getStatus, 1),
    true  // throwEx = true，结果不唯一抛异常
);

// 查询返回 Map
Map<String, Object> map = userService.getMap(
    Wrappers.<User>lambdaQuery()
        .eq(User::getId, 1L)
);
// map = {id=1, name=张三, age=20}

// 查询单个字段并转换类型
Integer maxAge = userService.getObj(
    Wrappers.<User>query()
        .select("max(age)"),
    obj -> obj == null ? null : Integer.valueOf(obj.toString())
);

// 查询名称
String name = userService.getObj(
    Wrappers.<User>lambdaQuery()
        .select(User::getName)
        .eq(User::getId, 1L),
    Object::toString
);
```

### 3.4 List 系列（列表查询）

```java
// 查询所有
List<T> list();

// 根据条件查询列表
List<T> list(Wrapper<T> queryWrapper);

// 根据 ID 列表查询
Collection<T> listByIds(Collection<? extends Serializable> idList);

// 根据 map 条件查询（key: 字段名, value: 字段值）
Collection<T> listByMap(Map<String, Object> columnMap);

// 根据 Wrapper 条件查询，返回 Map 列表
List<Map<String, Object>> listMaps(Wrapper<T> queryWrapper);

// 查询所有，返回 Map 列表
List<Map<String, Object>> listMaps();

// 查询所有，返回单个字段列表（默认取第一个字段）
default List<Object> listObjs();

// 根据 Wrapper 条件查询，返回单个字段列表
default List<Object> listObjs(Wrapper<T> queryWrapper);

// 查询所有，带转换函数（可将 Object 转为指定类型）
default <V> List<V> listObjs(Function<? super Object, V> mapper);

// 根据 Wrapper 条件查询，带转换函数
default <V> List<V> listObjs(Wrapper<T> queryWrapper, Function<? super Object, V> mapper);
```

**使用示例**：
```java
// 查询所有用户
List<User> users = userService.list();

// 根据条件查询（查询年龄大于 18 的用户）
List<User> users = userService.list(
    Wrappers.<User>lambdaQuery()
        .gt(User::getAge, 18)
);

// 根据 ID 列表查询
List<User> users = userService.listByIds(Arrays.asList(1L, 2L, 3L));

// 查询所有，返回 Map 列表（用于不确定返回结构的场景）
List<Map<String, Object>> maps = userService.listMaps();
for (Map<String, Object> map : maps) {
    System.out.println(map.get("name"));
}

// 只查询 name 字段（返回 List<Object>）
List<Object> names = userService.listObjs(
    Wrappers.<User>lambdaQuery()
        .select(User::getName)
);

// 使用转换函数，直接转为 String 列表
List<String> names = userService.listObjs(
    Wrappers.<User>lambdaQuery()
        .select(User::getName),
    Object::toString
);

// 查询所有 ID，并转为 Long 列表
List<Long> ids = userService.listObjs(
    Wrappers.<User>lambdaQuery()
        .select(User::getId),
    obj -> Long.valueOf(obj.toString())
);
```

### 3.5 Page 系列（分页查询）

```java
// 无条件分页查询
IPage<T> page(IPage<T> page);

// 根据条件分页查询
IPage<T> page(IPage<T> page, Wrapper<T> queryWrapper);

// 无条件分页查询，返回 Map
IPage<Map<String, Object>> pageMaps(IPage<T> page);

// 根据条件分页查询，返回 Map
IPage<Map<String, Object>> pageMaps(IPage<T> page, Wrapper<T> queryWrapper);
```

**使用示例**：
```java
// 分页查询（第 1 页，每页 10 条）
Page<User> page = new Page<>(1, 10);
IPage<User> userPage = userService.page(page);


System.out.println("总记录数：" + userPage.getTotal());
System.out.println("总页数：" + userPage.getPages());
System.out.println("当前页数据：" + userPage.getRecords());

// 带条件的分页查询
Page<User> page = new Page<>(1, 10);
IPage<User> userPage = userService.page(
    page,
    Wrappers.<User>lambdaQuery()
        .eq(User::getStatus, 1)
        .like(User::getName, "张")
        .orderByDesc(User::getCreateTime)
);

// 分页返回 Map（用于字段不固定的场景）
Page<User> page = new Page<>(1, 10);
IPage<Map<String, Object>> mapPage = userService.pageMaps(page);
```

### 3.6 Update 系列（更新）

```java
// 根据 ID 更新（只更新非空字段）
boolean updateById(T entity);

// 根据条件更新（entity：更新的字段，updateWrapper：更新条件）
boolean update(T entity, Wrapper<T> updateWrapper);

// 根据 UpdateWrapper 更新（只传 Wrapper，在 Wrapper 中通过 set 设置值）
default boolean update(Wrapper<T> updateWrapper);

// 批量更新（根据 ID）
boolean updateBatchById(Collection<T> entityList);

// 批量更新，指定每批大小
boolean updateBatchById(Collection<T> entityList, int batchSize);
```

**使用示例**：
```java
// 根据 ID 更新
User user = new User();
user.setId(1L);
user.setName("张三改名");
userService.updateById(user);

// 根据条件更新（把所有年龄为 20 的用户改为年龄 21）
User updateUser = new User();
updateUser.setAge(21);
userService.update(
    updateUser,
    Wrappers.<User>lambdaQuery()
        .eq(User::getAge, 20)
);

// 只传 UpdateWrapper（推荐，更灵活）
userService.update(
    Wrappers.<User>lambdaUpdate()
        .set(User::getAge, 21)
        .set(User::getUpdateTime, LocalDateTime.now())
        .eq(User::getAge, 20)
);

// 使用 setSql 执行自定义 SQL
userService.update(
    Wrappers.<User>lambdaUpdate()
        .setSql("age = age + 1")
        .eq(User::getStatus, 1)
);

// 批量更新（分批，每批 500 条）
userService.updateBatchById(userList, 500);
```

### 3.7 Count 系列（统计）

```java
// 查询总记录数
long count();

// 根据条件查询记录数
long count(Wrapper<T> queryWrapper);
```

**使用示例**：
```java
// 统计总用户数
long total = userService.count();

// 统计年龄大于 18 的用户数
long count = userService.count(
    Wrappers.<User>lambdaQuery()
        .gt(User::getAge, 18)
);
```

### 3.8 链式调用（Lambda 语法糖）

```java
// ========== 字符串字段名 链式查询 ==========
// 普通链式查询（字段名用字符串）
default QueryChainWrapper<T> query();

// 普通链式更新（字段名用字符串）
default UpdateChainWrapper<T> update();

// ========== Lambda 表达式 链式查询（推荐）==========
// Lambda 链式查询（字段名用方法引用，编译期类型安全）
default LambdaQueryChainWrapper<T> lambdaQuery();

// Lambda 链式查询（从实体对象开始构建）
default LambdaQueryChainWrapper<T> lambdaQuery(T entity);

// Lambda 链式更新（字段名用方法引用，编译期类型安全）
default LambdaUpdateChainWrapper<T> lambdaUpdate();

// Lambda 链式更新（从实体对象开始构建）
default LambdaUpdateChainWrapper<T> lambdaUpdate(T entity);

// ========== Kotlin 专用（Java 项目忽略）==========
// Kotlin 链式查询
default KtQueryChainWrapper<T> ktQuery();

// Kotlin 链式更新
default KtUpdateChainWrapper<T> ktUpdate();
```

#### 3.8.1 条件构造方法（核心）

以下方法可在 `query()` / `lambdaQuery()` / `update()` / `lambdaUpdate()` 后链式调用：

**比较条件**：
```java
// ========== 等于/不等于 ==========
eq(R column, Object val)           // 等于 =，如：eq("name", "张三") → name = '张三'
ne(R column, Object val)           // 不等于 <>，如：ne("status", 0) → status <> 0

// ========== 大小比较 ==========
gt(R column, Object val)           // 大于 >，如：gt("age", 18) → age > 18
ge(R column, Object val)           // 大于等于 >=，如：ge("age", 18) → age >= 18
lt(R column, Object val)           // 小于 <，如：lt("age", 60) → age < 60
le(R column, Object val)           // 小于等于 <=，如：le("age", 60) → age <= 60

// ========== 范围查询 ==========
between(R column, Object val1, Object val2)      // BETWEEN，如：between("age", 18, 30) → age BETWEEN 18 AND 30
notBetween(R column, Object val1, Object val2)   // NOT BETWEEN

// ========== 空值判断 ==========
isNull(R column)                   // IS NULL，如：isNull("email") → email IS NULL
isNotNull(R column)                // IS NOT NULL，如：isNotNull("email") → email IS NOT NULL
```

**模糊查询**：
```java
like(R column, Object val)         // LIKE '%值%'，如：like("name", "张") → name LIKE '%张%'
notLike(R column, Object val)      // NOT LIKE '%值%'
likeLeft(R column, Object val)     // LIKE '%值'，如：likeLeft("name", "三") → name LIKE '%三'（以xx结尾）
likeRight(R column, Object val)    // LIKE '值%'，如：likeRight("name", "张") → name LIKE '张%'（以xx开头）
notLikeLeft(R column, Object val)  // NOT LIKE '%值'
notLikeRight(R column, Object val) // NOT LIKE '值%'
```

**IN 查询**：
```java
in(R column, Collection<?> coll)   // IN，如：in("id", Arrays.asList(1,2,3)) → id IN (1,2,3)
in(R column, Object... values)     // IN，如：in("id", 1, 2, 3) → id IN (1,2,3)
notIn(R column, Collection<?> coll) // NOT IN
notIn(R column, Object... values)   // NOT IN

// 子查询形式
inSql(R column, String sqlValue)   // IN (sql)，如：inSql("id", "select id from user where status = 1")
notInSql(R column, String sqlValue) // NOT IN (sql)

// 示例：
.in(User::getId, Arrays.asList(1L, 2L, 3L))        // id IN (1, 2, 3)
.in(User::getStatus, 0, 1, 2)                       // status IN (0, 1, 2)
.inSql(User::getId, "select user_id from orders")  // id IN (select user_id from orders)
```

**分组、排序、限制**：
```java
// ========== 分组 GROUP BY ==========
groupBy(R... columns)              // 如：groupBy("dept_id") → GROUP BY dept_id
groupBy(R column)                  // 单字段分组

// ========== 排序 ORDER BY ==========
orderBy(boolean condition, boolean isAsc, R... columns)  // 通用排序
orderByAsc(R... columns)           // 升序，如：orderByAsc("age") → ORDER BY age ASC
orderByDesc(R... columns)          // 降序，如：orderByDesc("create_time") → ORDER BY create_time DESC

// ========== HAVING ==========
having(String sqlHaving, Object... params)  // 如：having("sum(age) > {0}", 100) → HAVING sum(age) > 100

// ========== 限制返回条数（MySQL）==========
last(String lastSql)               // 拼接到 SQL 末尾（有 SQL 注入风险，慎用）
                                   // 如：last("LIMIT 1") → ... LIMIT 1
                                   // 如：last("FOR UPDATE") → ... FOR UPDATE
```

**逻辑条件（AND / OR）**：
```java
// ========== OR 连接 ==========
or()                               // 下一个条件用 OR 连接（默认是 AND）
or(Consumer<Wrapper> consumer)     // OR 嵌套，如：or(w -> w.eq("a",1).ne("b",2)) → OR (a=1 AND b<>2)

// ========== AND 嵌套 ==========
and(Consumer<Wrapper> consumer)    // AND 嵌套，如：and(w -> w.eq("a",1).or().eq("b",2)) → AND (a=1 OR b=2)

// ========== 嵌套条件 ==========
nested(Consumer<Wrapper> consumer) // 普通嵌套（不带 AND/OR 前缀）

// 示例：查询 status=1 且 (age>20 或 name like '张%')
userService.lambdaQuery()
    .eq(User::getStatus, 1)
    .and(w -> w.gt(User::getAge, 20).or().likeRight(User::getName, "张"))
    .list();
// SQL: WHERE status = 1 AND (age > 20 OR name LIKE '张%')
```

**自定义 SQL 片段**：
```java
apply(String applySql, Object... params)  // 拼接自定义 SQL
                                          // 如：apply("date_format(create_time,'%Y-%m-%d') = {0}", "2024-01-01")
                                          // → date_format(create_time,'%Y-%m-%d') = '2024-01-01'

exists(String existsSql)           // EXISTS (sql)
notExists(String existsSql)        // NOT EXISTS (sql)
```

**字段选择（仅查询时用）**：
```java
select(R... columns)               // 指定查询字段，如：select("id", "name") → SELECT id, name
select(Class<T> entityClass, Predicate<TableFieldInfo> predicate)  // 过滤字段

// Lambda 用法
.select(User::getId, User::getName, User::getAge)  // 只查 id, name, age 三个字段
```

**更新专用方法**：
```java
set(R column, Object val)          // 设置字段值，如：set("status", 1) → SET status = 1
set(boolean condition, R column, Object val)  // 条件设置
setSql(String sql)                 // 设置自定义 SQL，如：setSql("age = age + 1") → SET age = age + 1

// Lambda 用法
.set(User::getStatus, 1)
.set(User::getUpdateTime, LocalDateTime.now())
.setSql("balance = balance - 100")
```

**条件控制（动态条件）**：

所有条件方法都支持第一个参数传 `boolean condition`，用于动态控制是否拼接该条件：

```java
// 第一个参数为 condition，为 true 才拼接
eq(boolean condition, R column, Object val)
like(boolean condition, R column, Object val)
in(boolean condition, R column, Collection<?> coll)
// ... 其他方法同理

// 示例：根据前端传参动态拼接条件
String name = request.getParameter("name");    // 可能为 null
Integer status = request.getParameter("status"); // 可能为 null

List<User> users = userService.lambdaQuery()
    .like(StringUtils.isNotBlank(name), User::getName, name)   // name 不为空才拼接
    .eq(status != null, User::getStatus, status)               // status 不为空才拼接
    .list();
```

#### 3.8.2 终结方法

**查询链终结方法**（`query()` / `lambdaQuery()` 专用）：
```java
list()                             // 返回 List<T>，查询列表
one()                              // 返回 T，查询单条（多条时取第一条）
count()                            // 返回 long，查询数量
exists()                           // 返回 boolean，是否存在记录
page(IPage<T> page)                // 返回 IPage<T>，分页查询
```

**更新链终结方法**（`update()` / `lambdaUpdate()` 专用）：
```java
update()                           // 执行更新，返回 boolean
update(T entity)                   // 用实体执行更新，返回 boolean（实体的非空字段作为 SET 值）
remove()                           // 执行删除，返回 boolean
```

#### 3.8.3 完整使用示例

```java
// ============ 基础链式查询 ============
// 链式查询 - 获取列表
List<User> users = userService.query()
    .eq("status", 1)
    .like("name", "张")
    .ge("age", 18)
    .list();

// 链式查询 - 获取单条
User user = userService.query()
    .eq("name", "张三")
    .one();

// 链式查询 - 获取数量
long count = userService.query()
    .eq("status", 1)
    .count();

// 链式更新
boolean success = userService.update()
    .set("status", 1)
    .eq("status", 0)
    .update();

// 链式删除
boolean success = userService.update()
    .eq("status", -1)
    .remove();

// ============ Lambda 链式查询（推荐）============
// 获取列表
List<User> users = userService.lambdaQuery()
    .eq(User::getStatus, 1)
    .like(User::getName, "张")
    .ge(User::getAge, 18)
    .orderByDesc(User::getCreateTime)
    .list();

// 获取单条
User user = userService.lambdaQuery()
    .eq(User::getName, "张三")
    .one();

// 判断是否存在
boolean exists = userService.lambdaQuery()
    .eq(User::getEmail, "test@example.com")
    .exists();

// Lambda 链式更新
boolean success = userService.lambdaUpdate()
    .set(User::getStatus, 1)
    .set(User::getUpdateTime, LocalDateTime.now())
    .eq(User::getStatus, 0)
    .update();

// Lambda 链式删除
boolean success = userService.lambdaUpdate()
    .eq(User::getStatus, -1)
    .remove();

// 从实体对象开始构建（实体的非空字段自动作为 eq 条件）
User condition = new User();
condition.setStatus(1);
List<User> users = userService.lambdaQuery(condition)
    .like(User::getName, "张")
    .list();

// ============ IN 查询示例 ============
// 查询 id 在列表中的用户
List<Long> ids = Arrays.asList(1L, 2L, 3L, 4L, 5L);
List<User> users = userService.lambdaQuery()
    .in(User::getId, ids)
    .list();

// 查询 status 在多个值中的用户
List<User> users = userService.lambdaQuery()
    .in(User::getStatus, 0, 1, 2)
    .list();

// 子查询：查询有订单的用户
List<User> users = userService.lambdaQuery()
    .inSql(User::getId, "SELECT user_id FROM orders WHERE amount > 100")
    .list();

// ============ 复杂条件示例 ============
// OR 条件：status=1 或 status=2
List<User> users = userService.lambdaQuery()
    .eq(User::getStatus, 1)
    .or()
    .eq(User::getStatus, 2)
    .list();
// SQL: WHERE status = 1 OR status = 2

// 嵌套 AND：status=1 且 (age>20 或 name like '张%')
List<User> users = userService.lambdaQuery()
    .eq(User::getStatus, 1)
    .and(w -> w.gt(User::getAge, 20).or().likeRight(User::getName, "张"))
    .list();
// SQL: WHERE status = 1 AND (age > 20 OR name LIKE '张%')

// 嵌套 OR：status=1 或 (age>30 且 dept_id=1)
List<User> users = userService.lambdaQuery()
    .eq(User::getStatus, 1)
    .or(w -> w.gt(User::getAge, 30).eq(User::getDeptId, 1L))
    .list();
// SQL: WHERE status = 1 OR (age > 30 AND dept_id = 1)

// ============ 动态条件示例 ============
// 根据前端参数动态拼接（参数为空则不拼接）
public List<User> searchUsers(String name, Integer status, Integer minAge, Integer maxAge) {
    return userService.lambdaQuery()
        .like(StringUtils.isNotBlank(name), User::getName, name)
        .eq(status != null, User::getStatus, status)
        .ge(minAge != null, User::getAge, minAge)
        .le(maxAge != null, User::getAge, maxAge)
        .orderByDesc(User::getCreateTime)
        .list();
}

// ============ 指定查询字段 ============
// 只查询 id 和 name
List<User> users = userService.lambdaQuery()
    .select(User::getId, User::getName)
    .eq(User::getStatus, 1)
    .list();

// ============ last 用法（慎用，有 SQL 注入风险）============
// 只取一条（MySQL）
User user = userService.lambdaQuery()
    .eq(User::getStatus, 1)
    .last("LIMIT 1")
    .one();

// 加锁查询
User user = userService.lambdaQuery()
    .eq(User::getId, 1L)
    .last("FOR UPDATE")
    .one();

// ============ 自定义 SQL 片段 ============
// 使用 apply 拼接复杂条件
List<User> users = userService.lambdaQuery()
    .apply("DATE_FORMAT(create_time, '%Y-%m-%d') = {0}", "2024-01-01")
    .list();

// ============ 分组统计 ============
// 按部门分组统计人数（需配合自定义 SQL 或 Map 返回）
List<Map<String, Object>> result = userService.query()
    .select("dept_id", "COUNT(*) as count")
    .groupBy("dept_id")
    .having("COUNT(*) > 5")
    .listMaps();

// ============ 链式分页查询 ============
Page<User> page = new Page<>(1, 10);
Page<User> userPage = userService.lambdaQuery()
    .eq(User::getStatus, 1)
    .orderByDesc(User::getCreateTime)
    .page(page);

// ============ 复杂更新示例 ============
// 自增更新：年龄+1
userService.lambdaUpdate()
    .setSql("age = age + 1")
    .eq(User::getStatus, 1)
    .update();

// 条件更新：余额减少（带条件判断防止负数）
userService.lambdaUpdate()
    .setSql("balance = balance - 100")
    .eq(User::getId, 1L)
    .apply("balance >= 100")  // 防止余额变负
    .update();
```

### 3.9 其他方法（工具类）

```java
// 获取对应的 BaseMapper（用于执行自定义 SQL 或访问 Mapper 方法）
BaseMapper<T> getBaseMapper();

// 获取实体类的 Class 对象
Class<T> getEntityClass();
```

**使用示例**：
```java
// 获取 BaseMapper 执行自定义方法
UserMapper mapper = (UserMapper) userService.getBaseMapper();
List<User> users = mapper.selectByCustomSql("some condition");

// 获取实体类 Class
Class<User> clazz = userService.getEntityClass();
System.out.println(clazz.getName());  // com.example.entity.User
```

---

## 4. 自定义 Mapper 方法

虽然 IService 提供了很多方法，但复杂业务往往需要自定义 SQL。

