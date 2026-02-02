package com.hmdp.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Token 刷新拦截器（模板）
 *
 * 目标（通常拦截所有请求）：
 * - 从请求头获取 token（比如 header: authorization）
 * - 基于 token 到 Redis 查询登录用户
 * - 查到后把用户信息保存到 UserHolder(ThreadLocal)
 * - 刷新 token 在 Redis 中的 TTL
 * - 请求结束后清理 UserHolder，避免线程复用串号
 *
 * TODO：把下面的模板逻辑补全成你自己的实现（或直接参考同目录下的 RefreshTokenInterceptor.java）。
 */
public class RefreshTokenInterceptorTemplate implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptorTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // TODO 1：从请求头取 token（例如 request.getHeader("authorization")）
        // TODO 2：token 为空 -> 直接 return true 放行（不影响未登录访问的接口）
        // TODO 3：用 token 拼 key 去 Redis 查用户数据
        // TODO 4：查到用户 -> 转成 UserDTO -> UserHolder.saveUser(userDTO)
        // TODO 5：刷新 TTL（expire）
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // TODO：请求结束必须清理，避免线程复用导致用户串号/内存问题
        UserHolder.removeUser();
    }
}

