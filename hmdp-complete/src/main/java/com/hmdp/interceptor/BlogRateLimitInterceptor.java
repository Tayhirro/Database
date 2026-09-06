package com.hmdp.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmdp.dto.Result;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.UserHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

/**
 * 限制博客接口在一分钟内可以被调用的次数，防止连续点击、脚本刷接口或突发流量压垮服务。
 *     1. 分别计数：Redis key 同时包含“用户（未登录时用 IP）+ 操作类型”。
 *     某个用户频繁点赞，不会消耗其他用户或发布接口的次数。
 *     2. 计数和设置 60 秒有效期一次完成：Lua 脚本让 Redis 把这两个动作作为一个整体执行，
 *     避免并发时只加了次数却漏设过期时间，留下永远不会清除的计数。
 *     3. Redis 故障时暂时放行：这里的限流只负责保护服务器容量，不负责保证业务数据正确。
 *     即使暂时放行，数据库唯一约束和事务仍会阻止重复点赞、重复关注等脏数据。
 * 
 */
@Slf4j
@Component
public class BlogRateLimitInterceptor implements HandlerInterceptor {

    private static final int WINDOW_SECONDS = 60;
    private static final DefaultRedisScript<Long> LIMIT_SCRIPT = new DefaultRedisScript<>(
            "local n=redis.call('INCR',KEYS[1]); " +
                    "if n==1 then redis.call('EXPIRE',KEYS[1],ARGV[1]); end; return n;",
            Long.class
    );

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public BlogRateLimitInterceptor(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Rule rule = resolveRule(request);
        if (rule == null) {
            return true;
        }
        String subject = UserHolder.getUser() != null && UserHolder.getUser().getId() != null
                ? "u:" + UserHolder.getUser().getId()
                : "ip:" + request.getRemoteAddr();
        String key = RedisConstants.BLOG_RATE_LIMIT_KEY + rule.name + ":" + subject;
        try {
            Long count = stringRedisTemplate.execute(
                    LIMIT_SCRIPT, Collections.singletonList(key), String.valueOf(WINDOW_SECONDS));
            if (count == null || count <= rule.limit) {
                return true;
            }
        } catch (RuntimeException e) {
            log.warn("博客接口限流不可用，本次降级放行，key={}", key, e);
            return true;
        }

        response.setStatus(429);
        response.setHeader("Retry-After", String.valueOf(WINDOW_SECONDS));
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(
                Result.fail("RATE_LIMITED", "请求过于频繁，请稍后重试", MDC.get("traceId"))));
        return false;
    }

    private Rule resolveRule(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI().substring(request.getContextPath().length());
        if ("POST".equals(method) && "/blog".equals(path)) {
            return new Rule("publish", 10);
        }
        if (("PUT".equals(method) || "DELETE".equals(method)) && path.matches("/blog/\\d+/like")) {
            return new Rule("like", 60);
        }
        if (("PUT".equals(method) || "DELETE".equals(method)) && path.matches("/blog/\\d+")) {
            return new Rule("write", 20);
        }
        if ("GET".equals(method) && "/blog/feed".equals(path)) {
            return new Rule("feed", 120);
        }
        return null;
    }

    @AllArgsConstructor
    private static class Rule {
        private final String name;
        private final long limit;
    }
}
