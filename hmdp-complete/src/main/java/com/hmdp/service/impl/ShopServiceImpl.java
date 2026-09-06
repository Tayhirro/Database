package com.hmdp.service.impl;

/*
 * 现实业务背景：用户点击店铺进入详情、按分类找店或查看附近店铺，运营人员新增/更新店铺时，会进入本服务。
 * 实际触发：GET /shop/{id}、GET /shop/of/type、POST /shop 和 PUT /shop 直接触发；
 * 不存在 ID 还可能来自旧链接、过期列表或伪造 URL。写接口的管理员校验由 ShopController 在方法内完成。
 */

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_KEY;
import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TTL;
import static com.hmdp.utils.RedisConstants.CACHE_NULL_TTL;
import static com.hmdp.utils.RedisConstants.LOCK_SHOP_KEY;
import static com.hmdp.utils.RedisConstants.LOCK_SHOP_TTL;
import static com.hmdp.utils.RedisConstants.SHOP_GEO_KEY;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmdp.utils.RedisLockClient;
import com.hmdp.utils.SystemConstants;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;



/**
 *
 *  服务实现类
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisLockClient redisLockClient;

    /**
     * 按 ID 查询店铺的完整流程：先读 Redis 缓存（key 为 {@code cache:shop:<id>}）；非空 JSON 直接反序列化返回，
     * 空字符串表示“已确认不存在”的空值缓存（TTL 2 分钟，由常量 CACHE_NULL_TTL 决定）；
     * 缓存完全未命中时用 {@link RedisLockClient}（封装了 Redis 分布式锁获取/释放的工具类，SETNX 实现）抢互斥锁
     * （key 为 {@code lock:shop:<id>}，10 秒自动过期，由常量 LOCK_SHOP_TTL 决定），
     * 未抢到就 {@code Thread.sleep(10)} 睡 10 毫秒后递归重试，抢到后查询 MySQL：
     * 查到写正常缓存（TTL 30 分钟，由常量 CACHE_SHOP_TTL 决定），查不到写空值缓存，最后释放锁。
     * 具体例子：店铺 100 首次访问时查库并缓存；随后请求直接读缓存。不存在的 999 会缓存空字符串 2 分钟，避免恶意请求反复打到数据库。
     */
    @Override
    public Result queryById(Long id) {
        String key = CACHE_SHOP_KEY + id;
        String lockKey = LOCK_SHOP_KEY + id;
        String shopJson = stringRedisTemplate.opsForValue().get(key);
        // redis 查询
        if(StrUtil.isNotBlank(shopJson)){   
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }
        if(shopJson != null) return Result.fail("店铺不存在");

        
        // 数据库查询
        Shop shop = null;
        boolean isLock = false;
        try{
            isLock = redisLockClient.tryLock(lockKey);
            if(!isLock){
                Thread.sleep(10);
                return queryById(id);
            }else{
                shop = getById(id);
                if(shop == null){
                    stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                    return Result.fail("店铺不存在");
                }else{
                    stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
                }
            }
        } catch (InterruptedException e){
            Thread.currentThread().interrupt(); // Restore interrupted status
            return Result.fail("查询失败");
        } finally {
            if(isLock) redisLockClient.unlock(lockKey);
        }
        return Result.ok(shop);
    }     
    /**
     * 删除店铺的完整流程：先读出旧记录（拿到删除前的 typeId，用于定位 GEO key），
     * 再按主键删除 MySQL 并裁决影响行数，0 行视为店铺不存在返回失败；
     * 删除成功后删除缓存 cache:shop:{id}（可能残留空值缓存或正常 JSON），并把该店铺从
     * GEO key shop:geo:{typeId} 中移除。Redis 清理失败只记 WARN 日志，不回滚 MySQL。
     * 具体例子：删除店铺 100（typeId=1）成功后，cache:shop:100 被删除、shop:geo:1 中成员 "100" 被移除，
     * 附近店铺接口不会再把已删除的 100 查出来；传 null 返回"店铺id不能为空"，不存在的 999 返回"店铺不存在"。
     * 注意：当前实现没有对外 Controller 入口；如以后开放删除接口，必须在 Controller 里补管理员校验。
     */
    @Override
    public Result delete(Long id){
        if(id == null){
            return Result.fail("店铺id不能为空");
        }
        Shop old = getById(id);
        if(old == null){
            return Result.fail("店铺不存在");
        }
        boolean removed = removeById(id);
        if(!removed){
            return Result.fail("店铺不存在");
        }
        try{
            stringRedisTemplate.delete(CACHE_SHOP_KEY + id);
        }catch (RuntimeException e){
            log.warn("删除店铺后清理缓存失败，shopId={}", id, e);
        }
        if(old.getTypeId() != null){
            try{
                stringRedisTemplate.opsForGeo().remove(SHOP_GEO_KEY + old.getTypeId(), id.toString());
            }catch (RuntimeException e){
                log.warn("删除店铺后从 GEO 移除失败，shopId={}, typeId={}", id, old.getTypeId(), e);
            }
        }
        return Result.ok();
    }

    /**
     * 新增店铺的完整流程（POST /shop，Controller 已做管理员校验）：先写 MySQL（主键 AUTO 自增回填到 shop.id），
     * 然后删除该 id 的缓存 cache:shop:{id}——新店铺 ID 理论上不会命中缓存，但此前查询过这个不存在的 ID 时
     * 可能已写入空值缓存（空字符串，TTL 2 分钟），必须清掉，否则新增后 2 分钟内详情接口仍返回"店铺不存在"；
     * 最后若请求带 typeId 和坐标 x/y，向 GEO key shop:geo:{typeId} 写入成员 "店铺ID"。
     * Redis 同步失败只记 WARN 日志，不回滚 MySQL，下次预热或再次更新时修复。
     * 具体例子：新增店铺 15（typeId=1, x=120.15, y=30.32）后，cache:shop:15 被清除，
     * shop:geo:1 写入 (120.15, 30.32) -> "15"；之后 GET /shop/of/type?x=...&y=... 立刻能搜到它。
     */
    @Override
    public Result saveShop(Shop shop){
        boolean saved = save(shop);
        if(!saved || shop.getId() == null){
            return Result.fail("新增店铺失败");
        }
        Long shopId = shop.getId();
        try{
            stringRedisTemplate.delete(CACHE_SHOP_KEY + shopId);
        }catch (RuntimeException e){
            log.warn("新增店铺后清理空值缓存失败，shopId={}", shopId, e);
        }
        if(shop.getTypeId() != null && shop.getX() != null && shop.getY() != null){
            try{
                stringRedisTemplate.opsForGeo().add(SHOP_GEO_KEY + shop.getTypeId(),
                        new Point(shop.getX(), shop.getY()), shopId.toString());
            }catch (RuntimeException e){
                log.warn("新增店铺后写入 GEO 失败，shopId={}, typeId={}", shopId, shop.getTypeId(), e);
            }
        }
        return Result.ok(shopId);
    }



    /**
     * 按类型分页查询店铺的完整流程：
     * 1. 未传经纬度时，直接按 typeId 在 MySQL 使用普通页码分页。
     * 2. 传入经纬度时，在 Redis GEO（key 为 {@code shop:geo:<typeId>}）中以 5000 米为半径查从近到远的前 {@code current * 5} 个结果
     *    （5 是常量 DEFAULT_PAGE_SIZE，即每页条数），再跳过前 {@code (current - 1) * 5} 个截取本页 ID。
     * 3. 用一条 {@code id IN (...) ORDER BY FIELD(id, ...)} SQL 批量查 MySQL 店铺详情，按 GEO 返回的 ID 顺序恢复排序，并把 GEO 返回的距离写入每个 Shop 后返回。
     * 具体例子：每页 5 条、current=2 时，Redis 先取最近 10 家并跳过前 5 家；若第 6～10 家距离为 800～1500 米，
     * 返回的就是这 5 家及各自 distance。没有 x/y 时第二页只是普通数据库分页，不计算距离。
     */
    @Override
    public Result queryShopByType(Integer typeId, Integer current, Double x, Double y){
        if( x == null || y ==null){
            Page<Shop> page = query()
                .eq("type_id", typeId)
                .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            return Result.ok(page.getRecords());
        }else{
            //分页
            int from = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
            int end = current * SystemConstants.DEFAULT_PAGE_SIZE;
            
            String key = SHOP_GEO_KEY + typeId;
            // 分页查询 redis  0 --- end
            GeoResults<RedisGeoCommands.GeoLocation<String>> geoResults = stringRedisTemplate.opsForGeo().search(
                    key, 
                    GeoReference.fromCoordinate(x, y), 
                    new Distance(5000), 
                    RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().sortAscending().limit(end)
            );

            if (geoResults == null) 
                return Result.ok(Collections.emptyList());
            List<GeoResult<RedisGeoCommands.GeoLocation<String>>> geoResultList = geoResults.getContent();
            if(geoResultList.size() <= from) {
                return Result.ok(Collections.emptyList());
            }
            // 截取 id + 距离 --map id-distance --- 查shop SQL  --- shop返回
            List<Long> ids = new ArrayList<>(geoResultList.size());
            Map<String,Distance> distanceMap = new HashMap<>(geoResultList.size());
            geoResultList.stream().skip(from).forEach(
                result ->{
                    String shopIdStr = result.getContent().getName();
                    ids.add(Long.valueOf(shopIdStr));
                    Distance distance = result.getDistance();
                    distanceMap.put(shopIdStr, distance);
                }
            );
            String idsStr = StrUtil.join(",", ids);
            List<Shop> shops = query().in("id", ids).last("ORDER BY FIELD(id," + idsStr + ")").list();
            for (Shop shop : shops){
                shop.setDistance(distanceMap.get(shop.getId().toString()).getValue()); // Set distance for each shop
            }
            return Result.ok(shops);
        }
    }


    /**
     * 更新店铺的完整流程（PUT /shop，Controller 已做管理员校验）：先读旧记录拿到更新前的 typeId 和坐标 x/y，
     * 再按主键更新 MySQL 并裁决影响行数，0 行（店铺不存在或并发已被删）返回失败，不再假装成功；
     * 更新成功后删除缓存 cache:shop:{id}，下一次详情查询会回源数据库并重建缓存。
     * 若 typeId 或坐标（x/y 任一）发生变化，同步 GEO：typeId 变了先从旧 key shop:geo:{旧 typeId} 移除成员 "店铺ID"，
     * 只要坐标可用就向新 key shop:geo:{新 typeId} 写入 (x, y) -> "店铺ID"（GEOADD 对同一成员会覆盖旧坐标，
     * 所以仅坐标变化时直接重新 add 即可）。Redis 同步失败只记 WARN 日志，不回滚 MySQL；
     * 缓存删除失败同理——MySQL 已提交，旧缓存最多残留到 TTL（30 分钟）过期。
     * 具体例子：店铺 100 名称从"老店"改为"新店"，数据库更新后删除 cache:shop:100，下一位访客读到"新店"；
     * 若同时把 typeId 从 1 改成 5，则 shop:geo:1 移除 "100"，shop:geo:5 写入其坐标，附近店铺查询归属立刻切换。
     */
    @Override
    public Result update(Shop shop){
        if(shop.getId() == null) return Result.fail("店铺id不能为空");
        Shop old = getById(shop.getId());
        if(old == null) return Result.fail("店铺不存在");
        // 进行数据库更新
        boolean updated = updateById(shop);
        if(!updated){
            return Result.fail("店铺不存在");
        }
        // 删除缓存（含可能残留的空值缓存）
        try{
            stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        }catch (RuntimeException e){
            log.warn("更新店铺后删除缓存失败，shopId={}", shop.getId(), e);
        }
        syncGeoAfterUpdate(old, shop);
        return Result.ok();
    }

    /**
     * 店铺更新后的 GEO 同步：比较旧记录与请求字段，typeId 或坐标变化才动 GEO。
     * 请求里为空的字段沿用旧值（updateById 只更新非空字段，语义保持一致）。
     * 任何 Redis 异常只记 WARN，不影响已提交的 MySQL 结果。
     */
    private void syncGeoAfterUpdate(Shop old, Shop shop){
        Long oldTypeId = old.getTypeId();
        Long newTypeId = shop.getTypeId() != null ? shop.getTypeId() : oldTypeId;
        Double oldX = old.getX();
        Double oldY = old.getY();
        Double newX = shop.getX() != null ? shop.getX() : oldX;
        Double newY = shop.getY() != null ? shop.getY() : oldY;
        boolean typeChanged = newTypeId != null && !newTypeId.equals(oldTypeId);
        boolean coordChanged = (shop.getX() != null && !newX.equals(oldX))
                || (shop.getY() != null && !newY.equals(oldY));
        if(!typeChanged && !coordChanged){
            return;
        }
        try{
            if(typeChanged){
                stringRedisTemplate.opsForGeo().remove(SHOP_GEO_KEY + oldTypeId, shop.getId().toString());
            }
            if(newTypeId != null && newX != null && newY != null){
                stringRedisTemplate.opsForGeo().add(SHOP_GEO_KEY + newTypeId,
                        new Point(newX, newY), shop.getId().toString());
            }
        }catch (RuntimeException e){
            log.warn("更新店铺后同步 GEO 失败，shopId={}, oldTypeId={}, newTypeId={}",
                    shop.getId(), oldTypeId, newTypeId, e);
        }
    }
}
