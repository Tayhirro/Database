package com.hmdp.service.impl;

import com.hmdp.dto.Result;
import com.hmdp.dto.ShopReviewCreateRequest;
import com.hmdp.dto.ShopReviewStatDTO;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Shop;
import com.hmdp.entity.ShopReview;
import com.hmdp.entity.ShopReviewStat;
import com.hmdp.exception.BusinessException;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.mapper.ShopReviewMapper;
import com.hmdp.mapper.ShopReviewStatMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.UserHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 店铺评价服务测试：校验规则、一人一店一评、统计维护与删除归属。
 */
@ExtendWith(MockitoExtension.class)
class ShopReviewServiceImplTest {

    @Mock
    private ShopMapper shopMapper;
    @Mock
    private ShopReviewStatMapper shopReviewStatMapper;
    @Mock
    private IUserService userService;
    @Mock
    private ShopReviewMapper shopReviewMapper;

    @InjectMocks
    private ShopReviewServiceImpl service;

    @BeforeEach
    void setUp() {
        UserDTO user = new UserDTO();
        user.setId(7L);
        UserHolder.saveUser(user);
        // ShopReviewServiceImpl 继承 ServiceImpl，baseMapper 由 Spring 注入；
        // 单测里用反射把 Mockito 的 Mapper 塞进父类字段
        org.springframework.test.util.ReflectionTestUtils.setField(service, "baseMapper", shopReviewMapper);
    }

    @AfterEach
    void tearDown() {
        UserHolder.removeUser();
    }

    private ShopReviewCreateRequest request(int rating, String content) {
        ShopReviewCreateRequest request = new ShopReviewCreateRequest();
        request.setShopId(100L);
        request.setRating(rating);
        request.setContent(content);
        return request;
    }

    @Test
    void createRejectsInvalidRatingAndEmptyContent() {
        assertThrows(BusinessException.class, () -> service.createReview(request(0, "好吃")));
        assertThrows(BusinessException.class, () -> service.createReview(request(6, "好吃")));
        assertThrows(BusinessException.class, () -> service.createReview(request(5, "   ")));
        verify(shopReviewMapper, never()).insert(any(ShopReview.class));
    }

    @Test
    void createRejectsMissingShopAndDuplicateReview() {
        when(shopMapper.selectById(100L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> service.createReview(request(5, "好吃")));

        Shop shop = new Shop();
        shop.setId(100L);
        when(shopMapper.selectById(100L)).thenReturn(shop);
        when(shopReviewMapper.insert(any(ShopReview.class)))
                .thenThrow(new org.springframework.dao.DuplicateKeyException("uk_shop_user"));

        assertThrows(BusinessException.class, () -> service.createReview(request(5, "好吃")));
        verify(shopReviewStatMapper, never()).incrementOnCreate(anyLong(), anyInt());
    }

    @Test
    void createAccumulatesStatInSameTransaction() {
        Shop shop = new Shop();
        shop.setId(100L);
        when(shopMapper.selectById(100L)).thenReturn(shop);
        when(shopReviewMapper.insert(any(ShopReview.class))).thenReturn(1);
        when(shopReviewStatMapper.incrementOnCreate(100L, 4)).thenReturn(1);

        service.createReview(request(4, "不错"));

        verify(shopReviewStatMapper).incrementOnCreate(100L, 4);
    }

    @Test
    void deleteOnlyOwnReviewAndRollsBackStat() {
        ShopReview own = new ShopReview();
        own.setId(9L);
        own.setUserId(7L);
        own.setShopId(100L);
        own.setRating(3);
        when(shopReviewMapper.selectById(9L)).thenReturn(own);
        when(shopReviewMapper.deleteById(9L)).thenReturn(1);

        Result ownDelete = service.deleteReview(9L);
        org.junit.jupiter.api.Assertions.assertTrue(ownDelete.getSuccess());
        verify(shopReviewStatMapper).decrementOnDelete(100L, 3);

        ShopReview foreign = new ShopReview();
        foreign.setId(10L);
        foreign.setUserId(8L);
        foreign.setShopId(100L);
        foreign.setRating(3);
        when(shopReviewMapper.selectById(10L)).thenReturn(foreign);

        Result foreignDelete = service.deleteReview(10L);
        org.junit.jupiter.api.Assertions.assertFalse(foreignDelete.getSuccess());
        verify(shopReviewStatMapper, never()).decrementOnDelete(100L, 8);
    }

    @Test
    void statComputesAverageFromTotalScore() {
        ShopReviewStat stat = new ShopReviewStat();
        stat.setShopId(100L);
        stat.setReviewCount(4);
        stat.setTotalScore(18L); // 18 / 4 = 4.5
        when(shopReviewStatMapper.selectById(100L)).thenReturn(stat);

        ShopReviewStatDTO dto = (ShopReviewStatDTO) service.statOfShop(100L).getData();

        assertEquals(4L, dto.getReviewCount());
        assertEquals(4.5D, dto.getAverageScore());
    }
}
