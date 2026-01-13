package com.github.microwind.springboot4ddd.infrastructure.middleware;

import com.github.microwind.springboot4ddd.infrastructure.config.SignConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 请求体缓存过滤器
 * <p>
 * 仅缓存 JSON 请求体,限制大小通过配置控制
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class CachedBodyFilter extends OncePerRequestFilter {

    private final SignConfig signConfig;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final int MAX_CACHE_SIZE = 20 * 1024 * 1024; // 20MB

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 检查是否需要缓存 body
        if (shouldCacheBody(request)) {
            try {
                // 包装请求, 主动触发流读取
                CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request, MAX_CACHE_SIZE);

                log.debug("已缓存 JSON 请求体 - URI: {}, 大小: {} bytes",
                        request.getRequestURI(), cachedRequest.getCachedBodyBytes().length);

                filterChain.doFilter(cachedRequest, response);
                return;
            } catch (IllegalArgumentException e) {
                log.warn("缓存请求体失败: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, e.getMessage());
                return;
            }
        }

        // 不需要缓存的请求, 直接放行
        filterChain.doFilter(request, response);
    }

    private boolean shouldCacheBody(HttpServletRequest request) {
        // 1. 是否开启了缓存
        if (!Boolean.TRUE.equals(signConfig.getAllowCachedBody())) {
            return false;
        }

        // 2. 验证签名 Header
        String sign = request.getHeader(SignConfig.HEADER_SIGN);
        if (!StringUtils.hasText(sign)) {
            return false;
        }

        // 3. 验证 HTTP 方法与 Content-Type
        String method = request.getMethod();
        String contentType = request.getContentType();
        if (!("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method))) {
            return false;
        }
        if (contentType == null || !contentType.toLowerCase().contains("application/json")) {
            return false;
        }

        // 4. 路径模式匹配
        List<String> pathPatterns = signConfig.getCachedBodyPathPatterns();
        if (CollectionUtils.isEmpty(pathPatterns)) {
            return false;
        }

        String requestPath = request.getServletPath();
        return pathPatterns.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }
}
