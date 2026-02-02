package com.hmdp.utils;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录校验拦截器（模板）
 *
 * 目标：
 * - 对“需要登录”的接口进行拦截
 * - 如果当前请求没有登录用户，则返回 401 并拦截
 *
 * TODO：把下面的模板逻辑补全成你自己的实现（或直接参考同目录下的 LoginInterceptor.java）。
 */
public class LoginInterceptorTemplate implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // TODO 1：判断是否已登录（通常是判断 UserHolder.getUser() 是否为空）
        // TODO 2：未登录时设置 response 状态码（通常是 401），并 return false
        // TODO 3：已登录则 return true 放行
        return true;
    }
}

