package com.hmdp.config;

import com.hmdp.entity.Shop;
import com.hmdp.service.IShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.hmdp.utils.RedisConstants.SHOP_GEO_KEY;

/*
 * 店铺 GEO 预热说明：
 * 启动监听 ApplicationReadyEvent（应用完全启动后触发，不再用 @PostConstruct），
 * 并在单独的守护线程里异步执行 + 失败重试，保证预热绝不阻塞或拖垮应用启动：
 * Redis 连不上时最坏情况只是附近店铺接口暂时查不到数据（普通店铺查询不依赖 GEO），下次重启会重新预热。
 */

@Slf4j
@Component
public class ShopGeoDataInitializer {

    /** 预热失败最多重试次数：第 1 次执行 + 最多 2 次重试，共 3 次尝试。 */
    private static final int MAX_ATTEMPTS = 3;

    /** 两次尝试之间的间隔：10 秒，给 Redis 恢复连接留出时间。 */
    private static final long RETRY_INTERVAL_MS = 10_000L;

    @Resource
    private IShopService shopService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 应用就绪后的入口：起一条名为 shop-geo-preload 的守护线程异步预热。
     * 设为守护线程是为了不阻止 JVM 退出；线程内的任何异常都只记 WARN 日志。
     */
    @EventListener(ApplicationReadyEvent.class)
    public void preloadShopGeoAfterStartup() {
        Thread worker = new Thread(this::loadShopGeoDataWithRetry, "shop-geo-preload");
        worker.setDaemon(true);
        worker.start();
    }

    /**
     * 带重试的预热循环：最多 MAX_ATTEMPTS 次尝试（3 次），失败间隔 RETRY_INTERVAL_MS（10 秒）再试；
     * 全部失败只记 WARN，不影响应用继续运行。
     */
    private void loadShopGeoDataWithRetry() {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                loadShopGeoData();
                return;
            } catch (Exception e) {
                log.warn("店铺 GEO 预热失败（第 {}/{} 次尝试），{}后重试",
                        attempt, MAX_ATTEMPTS, RETRY_INTERVAL_MS / 1000 + "秒", e);
                if (attempt < MAX_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_INTERVAL_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.warn("店铺 GEO 预热重试被中断，放弃剩余尝试");
                        return;
                    }
                }
            }
        }
        log.warn("店铺 GEO 预热在 {} 次尝试后仍失败，应用继续运行；附近店铺查询暂时无 GEO 数据，下次重启会重新预热",
                MAX_ATTEMPTS);
    }

    /**
     * 预热本体（业务逻辑与原 @PostConstruct 版本一致）：按 typeId 分组，把全部店铺的 (x, y) -> "店铺ID"
     * 写入各自的 GEO key shop:geo:{typeId}。
     * 具体例子：14 家店铺、2 种类型会产出 2 个 key（如 shop:geo:1、shop:geo:5），每个 key 写入对应店铺成员；
     * 重复执行幂等：GEOADD 对同一成员覆盖坐标。
     */
    public void loadShopGeoData() {
        List<Shop> shops = shopService.list();
        if (shops == null || shops.isEmpty()) {
            log.info("No shop data found, skip GEO preload.");
            return;
        }

        Map<Long, List<Shop>> groupByType = shops.stream()
                .collect(Collectors.groupingBy(Shop::getTypeId));

        for (Map.Entry<Long, List<Shop>> entry : groupByType.entrySet()) {
            Long typeId = entry.getKey();
            String key = SHOP_GEO_KEY + typeId;
            List<Shop> sameTypeShops = entry.getValue();


            //  radis 注入 add( key, List<RedisGeoCommands.GeoLocation<String>>)
            List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>(sameTypeShops.size());
            for (Shop shop : sameTypeShops) {
                locations.add(new RedisGeoCommands.GeoLocation<>(
                        shop.getId().toString(),
                        new Point(shop.getX(), shop.getY())
                ));
            }
            stringRedisTemplate.opsForGeo().add(key, locations);
        }
        log.info("Shop GEO preload done. typeCount={}, shopCount={}", groupByType.size(), shops.size());
    }
}
