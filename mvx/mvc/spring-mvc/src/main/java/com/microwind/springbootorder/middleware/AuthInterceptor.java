package com.microwind.springbootorder.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public AuthInterceptor() {
        // 这里可以注入其他服务（例如 JWT 解析器）
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");

        // 此处是模拟，实际应用可以根据需要去掉开关
        if (authHeader == null) {
            authHeader = "Bearer valid_token";
        }
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
            return false;
        }

        String token = authHeader.substring(7);
        if (!validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid Token");
            return false;
        }

        return true;
    }

    private boolean validateToken(String token) {
        return "valid_token".equals(token); // 示例逻辑
    }
}
