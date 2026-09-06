package com.hmdp.controller;

import com.hmdp.dto.Result;
import com.hmdp.dto.ShopReviewCreateRequest;
import com.hmdp.service.IShopReviewService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 店铺评价接口。
 *
 * 路由与登录要求：POST /shop-review、DELETE /shop-review/{id} 需要登录
 * （该前缀不在登录拦截的排除列表里）；GET list 和 GET stat 对游客开放
 * （AuthMvcConfig 里已排除这两个只读路径），店铺页未登录也能看到评价。
 */
@RestController
@RequestMapping("/shop-review")
public class ShopReviewController {

    @Resource
    private IShopReviewService shopReviewService;

    /** 发布评价：body =（shopId、rating 1~5、content、images 可选）；一人一店一评。 */
    @PostMapping
    public Result create(@RequestBody ShopReviewCreateRequest request) {
        return shopReviewService.createReview(request);
    }

    /**
     * 店铺评价列表（游客可访问）：lastTime/lastId 是上一页返回的游标，
     * pageSize 默认 10、上限 20；返回 list/hasMore/lastTime/lastId。
     */
    @GetMapping("list/{shopId}")
    public Result list(@PathVariable("shopId") Long shopId,
                       @RequestParam(value = "lastTime", required = false) Long lastTime,
                       @RequestParam(value = "lastId", required = false) Long lastId,
                       @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return shopReviewService.listByShop(shopId, lastTime, lastId, pageSize);
    }

    /** 删除本人评价；物理删除并回退统计，之后可重新评价。 */
    @DeleteMapping("{id}")
    public Result delete(@PathVariable("id") Long reviewId) {
        return shopReviewService.deleteReview(reviewId);
    }

    /** 店铺评价统计（游客可访问）：data =（shopId、reviewCount、averageScore）。 */
    @GetMapping("stat/{shopId}")
    public Result stat(@PathVariable("shopId") Long shopId) {
        return shopReviewService.statOfShop(shopId);
    }
}
