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

// 根据条件删除（QueryWrapper 构造条件）
boolean remove(Wrapper<T> queryWrapper);

// 根据 ID 批量删除
boolean removeByIds(Collection<? extends Serializable> idList);

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

// 批量删除
userService.removeByIds(Arrays.asList(1L, 2L, 3L));

// 根据 map 条件删除（删除 name=张三 且 age=20 的记录）
Map<String, Object> map = new HashMap<>();
map.put("name", "张三");
map.put("age", 20);
userService.removeByMap(map);
```

### 3.3 Get 系列（单条查询）

```java
// 根据 ID 查询
T getById(Serializable id);

// 根据条件查询一条记录（可能返回 null）
T getOne(Wrapper<T> queryWrapper);

// 根据条件查询一条记录，如果结果超过一条则抛出异常
T getOne(Wrapper<T> queryWrapper, boolean throwEx);

// 根据 Wrapper 条件查询，返回 Map（key: 字段名, value: 字段值）
Map<String, Object> getMap(Wrapper<T> queryWrapper);

// 根据 Wrapper 条件查询，返回 Object（通常用于查询单个字段，如 count）
Object getObj(Wrapper<T> queryWrapper);
```

**使用示例**：
```java
// 根据 ID 查询
User user = userService.getById(1L);

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

// 查询单个字段（查询最大年龄）
Integer maxAge = (Integer) userService.getObj(
    Wrappers.<User>query()
        .select("max(age) as maxAge")
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

// 根据 Wrapper 条件查询，返回单个字段列表（如只查 name）
List<Object> listObjs(Wrapper<T> queryWrapper);

// 查询所有，返回单个字段列表
List<Object> listObjs();
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

// 使用 UpdateWrapper 更新（更灵活）
userService.update(
    Wrappers.<User>lambdaUpdate()
        .set(User::getAge, 21)
        .set(User::getUpdateTime, LocalDateTime.now())
        .eq(User::getAge, 20)
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
// 链式查询（Query 结尾的方法返回 this，可继续链式调用）
default QueryChainWrapper<T> query();

// 链式更新（Update 结尾的方法返回 this，可继续链式调用）
default UpdateChainWrapper<T> update();
```

**使用示例**：
```java
// 链式查询
List<User> users = userService.query()
    .eq("status", 1)
    .like("name", "张")
    .ge("age", 18)
    .list();

// 链式更新（把所有 status=0 的用户改为 status=1）
userService.update()
    .set("status", 1)
    .eq("status", 0)
    .update();

// 链式删除
userService.query()
    .eq("status", -1)
    .remove();
```

---

## 4. 自定义 Mapper 方法

虽然 IService 提供了很多方法，但复杂业务往往需要自定义 SQL。

