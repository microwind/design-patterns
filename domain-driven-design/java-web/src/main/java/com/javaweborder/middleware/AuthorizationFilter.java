package com.javaweborder.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweborder.interfaces.response.ResponseBody;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@WebFilter("/*") // 拦截所有请求，进行权限校验，Filter执行顺序按首字母排序
public class AuthorizationFilter implements Filter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 公开路径白名单（无需认证）
    private static final Set<String> ALLOWED_PATHS = new HashSet<>(Arrays.asList(
            "/login", "/public", "/error"
    ));

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("AuthorizationFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        System.out.println("AuthorizationFilter: Filter is executing.");
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // 白名单路径直接放行，根据需要设置
         if (isAllowed(path)) {
            chain.doFilter(request, response);
            return;
         }

        try {
            // 1. 验证用户是否登录（示例：检查Token）
            String token = httpRequest.getHeader("Authorization");
            // 如有token就校验token
            if (token != null && !validateToken(token)) {
                sendError(httpResponse, HttpServletResponse.SC_UNAUTHORIZED, "未提供有效凭证");
                return;
            }

            // 2. 验证用户权限（示例：检查角色或权限）
            String requiredRole = "admin";
            if (!hasPermission(token, requiredRole)) {
                sendError(httpResponse, HttpServletResponse.SC_FORBIDDEN, "无权访问此资源");
                return;
            }

            // 权限验证通过，继续执行后续逻辑
            chain.doFilter(request, response);
        } catch (Exception e) {
            sendError(httpResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "权限验证异常");
        }
    }

    @Override
    public void destroy() {
        // 清理资源（可选）
    }

    // 判断是否为公开路径
    private boolean isAllowed(String path) {
        return true; // 全部放行
       // return ALLOWED_PATHS.contains(path);
    }

    // 模拟Token验证逻辑（替换为实际验证逻辑）
    private boolean validateToken(String token) {
        // 示例：检查Token有效性（如JWT解析），此处假使为true
        return token.startsWith("Bearer ");
    }

    // 模拟权限验证逻辑（替换为实际业务逻辑）
    private boolean hasPermission(String token, String requiredRole) {
        // 示例：从Token中解析用户角色并验证
        String userRole = "admin"; // 实际应从数据库或缓存获取
        return requiredRole.equals(userRole);
    }

    // 返回JSON格式错误响应
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        // 检查响应是否已提交，避免重复写入
        if (response.isCommitted()) {
            return; // 响应已经提交，跳过处理
        }
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        ResponseBody errorResponse = new ResponseBody(status, message);
        objectMapper.writeValue(writer, errorResponse);
        writer.flush();
    }
}