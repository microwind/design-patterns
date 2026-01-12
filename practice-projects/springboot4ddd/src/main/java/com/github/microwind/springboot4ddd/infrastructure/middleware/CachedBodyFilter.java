package com.github.microwind.springboot4ddd.infrastructure.middleware;

import com.github.microwind.springboot4ddd.infrastructure.config.SignConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * 请求体缓存过滤器
 * <p>
 * 功能：包装 HttpServletRequest，缓存请求体内容，使其可以被多次读取
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class CachedBodyFilter implements Filter {

    private final SignConfig signConfig;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpRequest) {
            // 检查是否需要缓存 body
            if (shouldCacheBody(httpRequest)) {
                // 包装请求，缓存请求体
                CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(httpRequest);
                log.debug("Wrapped request with CachedBodyHttpServletRequest - uri={}",
                        httpRequest.getRequestURI());
                chain.doFilter(cachedRequest, response);
                return;
            }
        }

        // 不需要缓存的请求，直接放行
        chain.doFilter(request, response);
    }

    /**
     * 判断当前请求是否需要缓存请求体（Body）
     */
    private boolean shouldCacheBody(HttpServletRequest request) {

        // 先读取是否允许缓存标识
        if (signConfig.getAllowCachedBody() == false) {
            return false;
        }

        // 1. 提取并校验签名 Header
        String appCode = request.getHeader(SignConfig.HEADER_APP_CODE);
        String sign = request.getHeader(SignConfig.HEADER_SIGN);

        if (!StringUtils.hasText(appCode) || !StringUtils.hasText(sign)) {
            return false;
        }

        // 2. HTTP 方法校验：只有可能携带 Body 的方法才需要处理
        String method = request.getMethod();
        boolean canHaveBody = "POST".equalsIgnoreCase(method) ||
                "PUT".equalsIgnoreCase(method) ||
                "PATCH".equalsIgnoreCase(method);

        if (!canHaveBody) {
            return false;
        }

        // 3. 路径模式匹配
        List<String> pathPatterns = signConfig.getCachedBodyPathPatterns();
        if (CollectionUtils.isEmpty(pathPatterns)) {
            // 如果未配置路径列表，默认不缓存
            return false;
        }

        String requestPath = request.getServletPath();
        for (String pattern : pathPatterns) {
            if (pathMatcher.match(pattern, requestPath)) {
                log.debug("Match success: caching body for path [{}] with pattern [{}]", requestPath, pattern);
                return true;
            }
        }

        return false;
    }
}
