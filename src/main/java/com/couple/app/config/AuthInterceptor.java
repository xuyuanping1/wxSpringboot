package com.couple.app.config;

import com.couple.app.common.CurrentUser;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 开发阶段简易鉴权：通过请求头 X-User-Id 识别用户
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isEmpty()) {
            try {
                CurrentUser.set(Long.parseLong(userId));
                return true;
            } catch (NumberFormatException e) {
                // 非数字 userId（如 H5 测试），拒绝
            }
        }
        response.setStatus(401);
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CurrentUser.clear();
    }
}
