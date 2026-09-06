package com.hmdp.service.impl;

import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.mapper.FollowMapper;
import com.hmdp.service.IUserService;
import com.hmdp.service.follow.FollowChangedEvent;
import com.hmdp.utils.UserHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowServiceImplTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private IUserService userService;

    @Mock
    private FollowMapper followMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private FollowServiceImpl followService;

    @AfterEach
    void tearDown() {
        UserHolder.removeUser();
    }

    @Test
    void follow_should_use_idempotent_insert_and_publish_event() {
        saveCurrentUser(1L);
        when(followMapper.insertIfAbsent(1L, 2L)).thenReturn(1);

        Result result = followService.follow(2L, true);

        assertTrue(result.getSuccess());
        verify(followMapper).insertIfAbsent(1L, 2L);
        FollowChangedEvent event = capturePublishedEvent();
        assertEquals(1L, event.getUserId());
        assertEquals(2L, event.getFollowUserId());
        assertTrue(event.isFollowed());
    }

    @Test
    void repeatedFollow_should_remain_success_when_relation_already_exists() {
        saveCurrentUser(1L);
        when(followMapper.insertIfAbsent(1L, 2L)).thenReturn(0);

        Result result = followService.follow(2L, true);

        assertTrue(result.getSuccess());
        verify(followMapper).insertIfAbsent(1L, 2L);
        assertTrue(capturePublishedEvent().isFollowed());
    }

    @Test
    void repeatedUnfollow_should_remain_success_when_relation_doesNotExist() {
        saveCurrentUser(1L);
        when(followMapper.deleteRelation(1L, 2L)).thenReturn(0);

        Result result = followService.follow(2L, false);

        assertTrue(result.getSuccess());
        verify(followMapper).deleteRelation(1L, 2L);
        assertFalse(capturePublishedEvent().isFollowed());
    }

    @Test
    void follow_should_reject_selfFollow_without_writing_database() {
        saveCurrentUser(1L);

        Result result = followService.follow(1L, true);

        assertFalse(result.getSuccess());
        assertEquals("不能关注自己", result.getErrorMsg());
        verify(followMapper, never()).insertIfAbsent(1L, 1L);
        verify(eventPublisher, never()).publishEvent(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void isFollow_should_return_true_when_database_has_relation() {
        saveCurrentUser(1L);
        when(followMapper.selectCount(any())).thenReturn(2L);

        Result result = followService.isFollow(2L);

        assertTrue(result.getSuccess());
        assertEquals(Boolean.TRUE, result.getData());
        // isFollow 以数据库唯一索引为唯一事实来源，不再读写 Redis 关注 Set
        verifyNoInteractions(stringRedisTemplate);
    }

    @Test
    void isFollow_should_return_false_when_database_has_no_relation_even_if_redis_stale() {
        saveCurrentUser(1L);
        when(followMapper.selectCount(any())).thenReturn(0L);

        Result result = followService.isFollow(2L);

        assertTrue(result.getSuccess());
        assertEquals(Boolean.FALSE, result.getData());
        // 取关后 Redis Set 残留成员也不再影响结果：只信数据库
        verifyNoInteractions(stringRedisTemplate);
    }

    @Test
    void isFollow_should_reject_null_target_without_querying() {
        saveCurrentUser(1L);

        Result result = followService.isFollow(null);

        assertFalse(result.getSuccess());
        assertEquals("目标用户不能为空", result.getErrorMsg());
        verifyNoInteractions(followMapper, stringRedisTemplate);
    }

    private void saveCurrentUser(Long userId) {
        UserDTO user = new UserDTO();
        user.setId(userId);
        UserHolder.saveUser(user);
    }

    private FollowChangedEvent capturePublishedEvent() {
        ArgumentCaptor<FollowChangedEvent> captor = ArgumentCaptor.forClass(FollowChangedEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        return captor.getValue();
    }
}
