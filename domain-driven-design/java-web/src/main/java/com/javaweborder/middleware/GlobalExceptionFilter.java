package com.javaweborder.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaweborder.interfaces.response.ResponseBody;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 在过滤器的 doFilter 方法中，自定义包装器来响应对象，并针对状态码进行不同处理，以返回自定义错误页面
@WebFilter("/*")
public class GlobalExceptionFilter implements Filter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("GlobalExceptionFilter initialized.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        CustomServletResponseWrapper responseWrapper = new CustomServletResponseWrapper(httpResponse);
        System.out.println("GlobalExceptionFilter: Filter is executing.");
        try {
            chain.doFilter(request, responseWrapper);
        } catch (Exception e) {
            handleException(httpResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
            return;
        }

        int status = responseWrapper.getStatus();
        if (status >= 400) {
            handleException(httpResponse, status, getErrorMessage(status));
            return;
        }

        // 正常响应写入内容
        String content = responseWrapper.getOutput();
        if (!content.isEmpty()) {
            response.getWriter().write(content);
        }
    }

    private String getErrorMessage(int status) {
        switch (status) {
            case 404:
                return "资源未找到";
            case 401:
                return "未经授权";
            case 403:
                return "禁止访问";
            case 500:
                return "服务器内部错误";
            default:
                return "请求错误";
        }
    }

    private void handleException(HttpServletResponse response, int status, String message) throws IOException {
        response.reset(); // 重置响应，确保自定义内容可写入
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        ResponseBody errorResponse = new ResponseBody(status, message);
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    @Override
    public void destroy() {
    }
}