package com.hmdp.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.entity.User;
import com.hmdp.entity.UserInfo;
import com.hmdp.service.IUserInfoService;
import com.hmdp.sms.SmsSender;
import cn.hutool.crypto.digest.BCrypt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.invocation.Invocation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.LOGIN_CODE_KEY;
import static com.hmdp.utils.RedisConstants.LOGIN_CODE_SEND_LOCK_KEY;
import static com.hmdp.utils.RedisConstants.LOGIN_CODE_SEND_LOCK_TTL;
import static com.hmdp.utils.RedisConstants.LOGIN_CODE_TTL;
import static com.hmdp.utils.RedisConstants.LOGIN_USER_KEY;
import static com.hmdp.utils.RedisConstants.LOGIN_USER_TTL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SmsSender smsSender;

    @Mock
    private IUserInfoService userInfoService;

    @Mock
    private QueryChainWrapper<User> queryChainWrapper;

    @Spy
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void sendCode_should_fail_when_phone_invalid() {
        Result result = userService.sendCode("123", null);

        assertFalse(result.getSuccess());
        assertEquals("手机号格式错误！", result.getErrorMsg());
        verify(stringRedisTemplate, never()).opsForValue();
    }

    @Test
    void sendCode_should_fail_when_send_too_frequent() {
        String phone = "13800138000";
        String lockKey = LOGIN_CODE_SEND_LOCK_KEY + phone;

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(lockKey, "1", LOGIN_CODE_SEND_LOCK_TTL, TimeUnit.SECONDS)).thenReturn(false);
        when(stringRedisTemplate.getExpire(lockKey, TimeUnit.SECONDS)).thenReturn(45L);

        Result result = userService.sendCode(phone, null);

        assertFalse(result.getSuccess());
        assertEquals("发送太频繁，请45秒后再试", result.getErrorMsg());
        verify(smsSender, never()).sendLoginCode(anyString(), anyString());
    }

    @Test
    void sendCode_should_fail_and_release_lock_when_sms_sender_throws() {
        String phone = "13800138000";
        String lockKey = LOGIN_CODE_SEND_LOCK_KEY + phone;

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(lockKey, "1", LOGIN_CODE_SEND_LOCK_TTL, TimeUnit.SECONDS)).thenReturn(true);
        org.mockito.Mockito.doThrow(new RuntimeException("sms failed"))
                .when(smsSender).sendLoginCode(eq(phone), anyString());

        Result result = userService.sendCode(phone, null);

        assertFalse(result.getSuccess());
        assertEquals("验证码发送失败，请稍后再试", result.getErrorMsg());
        verify(stringRedisTemplate).delete(lockKey);
        verify(valueOperations, never()).set(eq(LOGIN_CODE_KEY + phone), anyString(), eq(LOGIN_CODE_TTL), eq(TimeUnit.MINUTES));
    }

    @Test
    void sendCode_should_success_when_phone_valid_and_not_limited() {
        String phone = "13800138000";
        String lockKey = LOGIN_CODE_SEND_LOCK_KEY + phone;

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(lockKey, "1", LOGIN_CODE_SEND_LOCK_TTL, TimeUnit.SECONDS)).thenReturn(true);

        Result result = userService.sendCode(phone, null);

        assertTrue(result.getSuccess());
        verify(smsSender).sendLoginCode(eq(phone), anyString());
        verify(valueOperations).set(eq(LOGIN_CODE_KEY + phone), anyString(), eq(LOGIN_CODE_TTL), eq(TimeUnit.MINUTES));
    }

    @ParameterizedTest
    @CsvSource({
            "13200132000,123456"
    })
    void login_should_success_when_phone_code_valid(String phone, String code) {
        LoginFormDTO dto = new LoginFormDTO();
        dto.setPhone(phone);
        dto.setCode(code);
        prepareRedisForLogin(phone, code);
        mockQueryOne(buildUser(phone, "acc-" + phone, "plainPass"));

        Result result = userService.login(dto, null);

        assertTrue(result.getSuccess());
        String token = (String) result.getData();
        assertNotNull(token);
        verify(valueOperations).get(LOGIN_CODE_KEY + phone);
        verify(stringRedisTemplate).delete(LOGIN_CODE_KEY + phone);
        verifyTokenScriptIssued(token);
    }

    @ParameterizedTest
    @CsvSource({
            "alice,Pass@123"
    })
    void login_should_success_when_account_password_valid(String account, String password) {
        LoginFormDTO dto = new LoginFormDTO();
        dto.setAccount(account);
        dto.setPassword(password);
        mockQueryOne(buildUser("13800138000", account, password));

        Result result = userService.login(dto, null);

        assertTrue(result.getSuccess());
        String token = (String) result.getData();
        assertNotNull(token);
        verifyTokenScriptIssued(token);
    }

    @Test
    void login_should_fail_and_cleanup_when_token_script_fails() {
        String phone = "13200132000";
        LoginFormDTO dto = new LoginFormDTO();
        dto.setPhone(phone);
        dto.setCode("123456");
        prepareRedisForLogin(phone, "123456");
        mockQueryOne(buildUser(phone, "acc-" + phone, "plainPass"));
        when(stringRedisTemplate.execute(eq(UserServiceImpl.LOGIN_TOKEN_SCRIPT), anyList(), any()))
                .thenThrow(new RuntimeException("redis down"));

        Result result = userService.login(dto, null);

        assertFalse(result.getSuccess());
        // 已用验证码正常删除，签发失败的 token key 被清理，不会留下永久或半成品登录状态
        verify(stringRedisTemplate).delete(LOGIN_CODE_KEY + phone);
        verify(stringRedisTemplate).delete(startsWith(LOGIN_USER_KEY));
    }

    @ParameterizedTest
    @CsvSource({
            "13200132000,123456"
    })
    void signUp_should_create_user_and_user_info_together_when_phone_register(String phone, String code) {
        LoginFormDTO dto = new LoginFormDTO();
        dto.setPhone(phone);
        dto.setCode(code);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(LOGIN_CODE_KEY + phone)).thenReturn(code);
        doReturn(queryChainWrapper).when(userService).query();
        when(queryChainWrapper.eq(anyString(), any())).thenReturn(queryChainWrapper);
        when(queryChainWrapper.one()).thenReturn(null);
        doAnswer(invocation -> {
            invocation.getArgument(0, User.class).setId(66L);
            return true;
        }).when(userService).save(any(User.class));
        when(userInfoService.getById(66L)).thenReturn(null, new UserInfo());
        when(userInfoService.save(any(UserInfo.class))).thenReturn(true);

        Result result = userService.signUp(dto, null);

        assertTrue(result.getSuccess());
        assertNotNull(result.getData());
        // 注册事务里必须初始化 tb_user_info，而不是等登录时防御性补建
        verify(userInfoService).save(any(UserInfo.class));
        String token = (String) result.getData();
        verifyTokenScriptIssued(token);
    }

    @ParameterizedTest
    @ValueSource(strings = {"123", "", "abc"})
    void login_should_fail_when_phone_invalid(String phone) {
        LoginFormDTO dto = new LoginFormDTO();
        dto.setPhone(phone);
        dto.setCode("123456");

        Result result = userService.login(dto, null);

        assertFalse(result.getSuccess());
        assertEquals("手机号格式错误！", result.getErrorMsg());
        verify(stringRedisTemplate, never()).opsForValue();
    }

    @Test
    void login_should_fail_when_phone_code_missing() {
        LoginFormDTO dto = new LoginFormDTO();
        dto.setPhone("13200132000");

        Result result = userService.login(dto, null);

        assertFalse(result.getSuccess());
        assertEquals("请输入验证码！", result.getErrorMsg());
    }

    @Test
    void login_should_fail_when_phone_code_expired() {
        String phone = "13200132000";
        LoginFormDTO dto = new LoginFormDTO();
        dto.setPhone(phone);
        dto.setCode("123456");
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(LOGIN_CODE_KEY + phone)).thenReturn(null);

        Result result = userService.login(dto, null);

        assertFalse(result.getSuccess());
        assertEquals("验证码已过期！", result.getErrorMsg());
    }

    @Test
    void login_should_fail_when_phone_code_mismatch() {
        String phone = "13200132000";
        LoginFormDTO dto = new LoginFormDTO();
        dto.setPhone(phone);
        dto.setCode("123456");
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(LOGIN_CODE_KEY + phone)).thenReturn("654321");

        Result result = userService.login(dto, null);

        assertFalse(result.getSuccess());
        assertEquals("验证码错误！", result.getErrorMsg());
    }

    @Test
    void login_should_fail_when_account_password_wrong() {
        LoginFormDTO dto = new LoginFormDTO();
        dto.setAccount("alice");
        dto.setPassword("wrongPass");
        mockQueryOne(buildUser("13800138000", "alice", "RightPass@123"));

        Result result = userService.login(dto, null);

        assertFalse(result.getSuccess());
        assertEquals("账号或密码错误！", result.getErrorMsg());
    }

    private void prepareRedisForLogin(String phone, String code) {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(LOGIN_CODE_KEY + phone)).thenReturn(code);
    }

    /**
     * 登录成功必须通过 Lua 脚本一次性写入 Hash 与 TTL：key 精确到 token；
     * 并扫描实际调用参数，断言 ARGV[1] 是 TTL 秒数（由常量 LOGIN_USER_TTL 决定，当前 36000）。
     */
    private void verifyTokenScriptIssued(String token) {
        verify(stringRedisTemplate).execute(
                eq(UserServiceImpl.LOGIN_TOKEN_SCRIPT),
                eq(Collections.singletonList(LOGIN_USER_KEY + token)),
                any());
        for (Invocation invocation : Mockito.mockingDetails(stringRedisTemplate).getInvocations()) {
            if ("execute".equals(invocation.getMethod().getName())
                    && Collections.singletonList(LOGIN_USER_KEY + token).equals(invocation.getArgument(1))) {
                assertEquals(String.valueOf(LOGIN_USER_TTL), invocation.getArgument(2));
                return;
            }
        }
    }

    private void mockQueryOne(User user) {
        doReturn(queryChainWrapper).when(userService).query();
        when(queryChainWrapper.eq(anyString(), any())).thenReturn(queryChainWrapper);
        when(queryChainWrapper.one()).thenReturn(user);
    }

    private User buildUser(String phone, String account, String rawPassword) {
        User user = new User();
        user.setId(1L);
        user.setPhone(phone);
        user.setAccount(account);
        user.setNickName("tester");
        user.setPassword(BCrypt.hashpw(rawPassword));
        return user;
    }
}
