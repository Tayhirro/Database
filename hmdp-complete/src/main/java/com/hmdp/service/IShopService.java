package com.hmdp.service;

/*
 * 现实业务背景：用户查看店铺详情、按分类/距离找店，以及运营人员修改店铺时，需要店铺领域入口。
 * 实际触发：ShopController 的详情、类型/GEO 查询、新增和更新调用本接口；关键词检索已独立到 service/search。
 * 注意：POST /shop 与 PUT /shop 由 ShopController 在方法内做"已登录 + hmdp.admin.user-ids 白名单"校验
 * （/shop/** 在登录拦截排除列表里）；
 * delete() 目前没有任何 Controller 入口，如以后开放删除接口，必须先在 Controller 补管理员校验。
 */

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;

/**
 * 店铺服务。数据存于 tb_shop；店铺详情在 Redis 有 JSON 缓存（key 为 cache:shop:{店铺 ID}，TTL 30 分钟），
 * 附近店铺按类型维护 Redis GEO（key 为 shop:geo:{typeId}）。
 * 所有方法返回 {@link Result}（本项目统一的 HTTP 响应包装：{@code success/data/errorCode/errorMsg/traceId}）。
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IShopService extends IService<Shop> {

    /**
     * 按 ID 查店铺详情（GET /shop/{id}）：先读 Redis 缓存；空字符串代表“确认不存在”的空值缓存（TTL 2 分钟），
     * 用来挡住恶意反复查询不存在的 ID；缓存未命中时用互斥锁（lock:shop:{id}）保证同一店铺只有一个请求回源 MySQL。
     */
    Result queryById(Long id);

    /**
     * 更新店铺（PUT /shop）：先更新 MySQL 并裁决影响行数（0 行返回失败），
     * 再删除该店铺的 Redis 缓存 cache:shop:{id}（先更库后删缓存）；typeId 或坐标 x/y 变化时
     * 同步 GEO（旧 key 移除、新 key 写入），Redis 同步失败只记日志不回滚 MySQL。
     */
    Result update(Shop shop);

    /**
     * 新增店铺（POST /shop）：写 MySQL 后删除该 id 可能残留的空值缓存 cache:shop:{id}，
     * 并在带坐标时向 shop:geo:{typeId} 写入成员；Redis 失败只记日志。
     */
    Result saveShop(Shop shop);

    /**
     * 按主键删除店铺：影响行数 0 返回失败；成功后删除缓存 cache:shop:{id} 并把成员从
     * shop:geo:{旧 typeId} 移除（Redis 失败只记日志）。当前没有对外接口调用。
     */
    Result delete(Long id);

    /**
     * 按店铺类型分页查询（GET /shop/of/type），页大小固定 5：
     * 1. 不传经纬度 x/y 时，按 type_id 在 MySQL 做普通页码分页（第 1 条 SQL）。
     * 2. 传经纬度时，先在 Redis GEO（key 为 shop:geo:{typeId}）里查 5 公里内由近到远的前“页码×5”家，
     *    截取本页 5 家的 ID，第 2 条 SQL 用 IN 批量查店铺详情并按 GEO 距离顺序恢复排序，同时把距离（米）写入每家店铺。
     *    例：current=2 时取最近的 10 家并跳过前 5 家，返回第 6～10 家及各自距离。
     */
    Result queryShopByType(Integer typeId, Integer current, Double x, Double y);

}
