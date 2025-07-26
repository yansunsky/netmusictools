package com.ysk.netmusictools.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行登录页面和登录请求
        String path = request.getRequestURI();
        if (path.equals("/login") || path.contains("/static/") || path.equals("/doLogin")) {
            return true;
        }

        // 检查Cookie中是否有用户名
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userName".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
                    return true; // 已登录，放行
                }
            }
        }

        // 未登录，重定向到登录页
        response.sendRedirect("/login");
        return false;
    }
}