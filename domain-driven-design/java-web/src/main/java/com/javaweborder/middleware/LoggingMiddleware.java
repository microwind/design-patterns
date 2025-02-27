package com.javaweborder.middleware;

import com.javaweborder.utils.LogUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

@WebFilter("/*") // 适用于所有请求
public class LoggingMiddleware implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // 初始化操作（可选）
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 获取请求开始时间
        Instant startTime = Instant.now();

        // 转换为HttpServletRequest和HttpServletResponse，以便使用HTTP相关方法
        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 记录请求的日志（开始时间）
        LogUtils.logRequest(httpRequest, startTime);

        // 调用下一个过滤器或目标资源（如 Controller）
        chain.doFilter(request, response);

        // 记录响应的日志（结束时间）
        LogUtils.logRequest(httpRequest, startTime);
    }

    @Override
    public void destroy() {
        // 清理操作（可选）
    }
}
