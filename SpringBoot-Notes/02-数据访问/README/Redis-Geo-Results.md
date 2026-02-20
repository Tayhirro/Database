# Redis Geo 地理空间操作 - GeoResults、GeoLocation、Distance、Point

> **来源**: 
> - `org.springframework.data.redis.connection.RedisGeoCommands` 
> - `org.springframework.data.redis.core.GeoResults`
> - `org.springframework.data.geo.Distance`
> - `org.springframework.data.geo.Point`

**作用**: Redis 地理空间查询结果封装类

---

## 1. 类关系总览

```
RedisGeoCommands (接口)
    ├── GeoLocation<M> (静态嵌套类)
    └── GeoRadiusCommandArgs (静态嵌套类)

GeoResults<T> (结果包装类)
    └── List<GeoResult<T>> (结果列表)
        └── GeoResult<T> (单个结果)
            ├── content: T (实际数据)
            └── distance: Distance (距离)
```

**关键点**：
- `GeoLocation` **嵌套**在 `RedisGeoCommands` 接口里，所以用 `RedisGeoCommands.GeoLocation`
- `<M>` 泛型**只指定 `name` 的类型**（商家ID），坐标永远是 `Point`

---

## 2. 核心类详解

### 2.1 GeoLocation<M> - 位置信息


```java
// 定义在 RedisGeoCommands 接口内部
public interface RedisGeoCommands {
    class GeoLocation<M> {
        private M name;        // 泛型！可以是 String、Long 等（商家ID）
        private Point point;   // 坐标，固定是 Point 类型
        
        public M getName() { return name; }
        public Point getPoint() { return point; }
    }
}
```


**为什么用 `RedisGeoCommands.GeoLocation<String>`？**
- 因为 `GeoLocation` 是**嵌套类**（像 `Map.Entry`）
- `<String>` 只指定 `name` 字段的类型
- `point` 永远是 `Point` 对象，存经纬度

**使用**：
```java
Point point = new Point(116.397, 39.916);
RedisGeoCommands.GeoLocation<String> location = 
    new RedisGeoCommands.GeoLocation<>("shop:10086", point);

String shopId = location.getName();     // "shop:10086"
Point p = location.getPoint();          // 坐标对象
double lng = p.getX();                  // 116.397 (经度)
double lat = p.getY();                  // 39.916 (纬度)
```

---

### 2.2 GeoResults<T> 和 GeoResult<T>

```java
// 查询结果集合
GeoResults<RedisGeoCommands.GeoLocation<String>> results = 
    redisTemplate.opsForGeo().radius("shop:geo", circle, args);

// 遍历结果
for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
    Distance distance = result.getDistance();           // 距离
    RedisGeoCommands.GeoLocation<String> location = result.getContent();
    String shopId = location.getName();                 // 商家ID
    double km = distance.getValue();                    // 5.2 (公里)
}
```

---

### 2.3 Distance 和 Point

```java
// Distance - 距离
Distance distance = new Distance(5, Metrics.KILOMETERS);
double value = distance.getValue();     // 5.0
Metric metric = distance.getMetric();   // Metrics.KILOMETERS

// Point - 坐标点 (x=经度, y=纬度)
Point point = new Point(116.397, 39.916);
double longitude = point.getX();  // 116.397
double latitude = point.getY();   // 39.916
```

---

## 3. 完整查询示例

```java
public List<ShopDistanceVO> findNearbyShops(double x, double y, double radius) {
    // 1. 构建查询条件
    Point center = new Point(x, y);
    Distance distance = new Distance(radius, Metrics.KILOMETERS);
    Circle circle = new Circle(center, distance);
    
    // 2. 设置参数
    RedisGeoCommands.GeoRadiusCommandArgs args = 
        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
            .includeDistance()        // 包含距离
            .sortAscending()          // 按距离升序
            .limit(10);               // 限制10条
    
    // 3. 执行查询
    GeoResults<RedisGeoCommands.GeoLocation<String>> results = 
        redisTemplate.opsForGeo().radius("shop:geo", circle, args);
    
    // 4. 解析结果
    List<ShopDistanceVO> list = new ArrayList<>();
    if (results != null) {
        for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
            ShopDistanceVO vo = new ShopDistanceVO();
            vo.setShopId(result.getContent().getName());
            vo.setDistance(result.getDistance().getValue());
            list.add(vo);
        }
    }
    return list;
}
```

---

## 4. 关键理解

### 4.1 为什么要用 `RedisGeoCommands.GeoLocation`？

**因为它是嵌套类！**

```java
// 类似这些嵌套类：
Map.Entry<String, String>     // Entry 在 Map 里
Thread.State                  // State 在 Thread 里
RedisGeoCommands.GeoLocation  // GeoLocation 在 RedisGeoCommands 里

// 错误！编译器找不到
GeoLocation<String> location;  // ❌

// 正确！带完整路径
RedisGeoCommands.GeoLocation<String> location;  // ✅
```

### 4.2 泛型 `<String>` 的作用

**只影响 `name` 字段**：

| 字段 | 类型 | 由泛型决定？ |
|------|------|-------------|
| `name` | `String` | ✅ 是的，由 `<String>` 指定 |
| `point` | `Point` | ❌ 不是，固定类型 |

```java
RedisGeoCommands.GeoLocation<String> loc;
String id = loc.getName();     // 返回 String
Point p = loc.getPoint();      // 永远是 Point（与泛型无关）
```

### 4.3 Point 的 x 和 y

- **x = 经度**（longitude）-180 ~ 180
- **y = 纬度**（latitude）-90 ~ 90

```java
Point point = new Point(116.397, 39.916);
//        x=经度 ↑           y=纬度 ↑
```

---

## 5. 常用 API 速查

| 类 | 方法 | 返回类型 | 说明 |
|----|------|---------|------|
| `GeoLocation` | `getName()` | `M` (泛型) | 商家ID |
| `GeoLocation` | `getPoint()` | `Point` | 坐标 |
| `GeoResult` | `getContent()` | `T` | 实际数据 |
| `GeoResult` | `getDistance()` | `Distance` | 距离 |
| `Distance` | `getValue()` | `double` | 距离值 |
| `Point` | `getX()` | `double` | 经度 |
| `Point` | `getY()` | `double` | 纬度 |

---

## 6. 总结

**类关系**：
```
GeoResults (结果集合)
    └── GeoResult (单个结果)
            ├── content: GeoLocation (位置信息)
            │       ├── name: String (商家ID，泛型指定)
            │       └── point: Point (坐标，固定类型)
            └── distance: Distance (距离)
```

**核心要点**：
1. `RedisGeoCommands.GeoLocation<String>` **必须带前缀**（嵌套类）
2. **泛型 `<String>` 只管 `name`**，坐标永远是 `Point`
3. **Point.x = 经度，Point.y = 纬度**

**一句话**：这些类共同封装了 Redis Geo 查询的**完整结果**，包括位置标识、坐标和距离信息。
