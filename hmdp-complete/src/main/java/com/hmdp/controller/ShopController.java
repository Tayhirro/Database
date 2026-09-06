package com.hmdp.controller;


import com.hmdp.config.AdminProperties;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Shop;
import com.hmdp.exception.BusinessException;
import com.hmdp.service.IShopService;
import com.hmdp.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 *
 * 前端控制器
 * @author 虎哥
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    public IShopService shopService;
    @Resource
    private AdminProperties adminProperties;

    /**
     * 根据id查询商铺信息
     * @param id 商铺id
     * @return 商铺详情数据
     */
    @GetMapping("/{id}")
    public Result queryShopById(@PathVariable("id") Long id) {
        return shopService.queryById(id);
    }

    /**
     * 新增商铺信息。/shop/** 在登录拦截排除列表里（浏览类接口游客可访问），
     * 因此写接口在方法内自行校验"已登录 + 在 hmdp.admin.user-ids 白名单"。
     * @param shop 商铺数据
     * @return 商铺id
     */
    @PostMapping
    public Result saveShop(@RequestBody Shop shop) {
        requireAdmin();
        // 写入数据库并同步缓存/GEO
        return shopService.saveShop(shop);
    }

    /**
     * 更新商铺信息。写接口同样新增的"已登录 + 管理员白名单"校验。
     * @param shop 商铺数据
     * @return 无
     */
    @PutMapping
    public Result updateShop(@RequestBody Shop shop) {
        requireAdmin();
        return shopService.update(shop);
    }

    /**
     * 根据商铺类型分页查询商铺信息
     * @param typeId 商铺类型
     * @param current 页码
     * @return 商铺列表
     */
    @GetMapping("/of/type")
    public Result queryShopByType(
            @RequestParam("typeId") Integer typeId,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "x", required = false) Double x,
            @RequestParam(value = "y", required = false) Double y
    ) {
        return shopService.queryShopByType(typeId, current, x, y);
    }

    /**
     * 管理员校验：与 VoucherController.requireAdmin 相同的写法。
     * 未登录抛 401 语义异常；已登录但不在 hmdp.admin.user-ids 白名单抛 403 语义异常。
     */
    private void requireAdmin() {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            throw BusinessException.unauthorized("请先登录");
        }
        if (!adminProperties.isAdmin(user.getId())) {
            throw BusinessException.forbidden("仅运营人员可以执行该操作");
        }
    }

}
