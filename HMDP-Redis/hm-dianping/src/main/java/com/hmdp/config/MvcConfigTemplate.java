package com.hmdp.config;

import com.hmdp.utils.LoginInterceptorTemplate;
import com.hmdp.utils.RefreshTokenInterceptorTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 拦截器注册（模板）
 *
 * 说明：
 * - 这个类刻意没有加 @Configuration，避免你还没补全逻辑就影响当前项目运行
 * - 你要启用它的话：改名为 MvcConfig 并加上 @Configuration（或把内容合并到现有的 MvcConfig.java）
 *
 * 常见做法（两级拦截）：
 * 1) RefreshTokenInterceptor：拦截所有请求，负责“解析 token + 放入 ThreadLocal + 刷新 TTL”
 * 2) LoginInterceptor：只拦截需要登录的接口，负责“未登录返回 401”
 */
public class MvcConfigTemplate implements WebMvcConfigurer {

    private final StringRedisTemplate stringRedisTemplate;

    public MvcConfigTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // TODO 1：注册 token 刷新拦截器（一般 addPathPatterns("/**")，并设置 order 更小优先执行）
        registry.addInterceptor(new RefreshTokenInterceptorTemplate(stringRedisTemplate))
                .addPathPatterns("/**");

        // TODO 2：注册登录拦截器（排除登录/验证码/公共查询等接口）
        registry.addInterceptor(new LoginInterceptorTemplate())
                .excludePathPatterns(
                        "/user/code",
                        "/user/login",
                        "/shop/**",
                        "/shop-type/**",
                        "/voucher/**",
                        "/blog/hot",
                        "/upload/**"
                );
    }
}

