package com.hmdp.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmdp.dto.Result;
import com.hmdp.utils.UserHolder;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录校验拦截器（模板）。
 *
 * 目标：对“必须登录”的接口进行拦截。
 * 前置条件：AuthContextInterceptor 已经先执行，把用户放到了 UserHolder。
 */
public class LoginRequiredInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    public LoginRequiredInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (UserHolder.getUser() == null) {
            response.setStatus(401);
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(
                    Result.fail("AUTH_REQUIRED", "请先登录", MDC.get("traceId"))));
            return false;
        }
        return true;
    }
}

