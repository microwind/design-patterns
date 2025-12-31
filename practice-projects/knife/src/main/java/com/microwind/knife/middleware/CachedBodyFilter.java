package com.microwind.knife.middleware;

import com.microwind.knife.application.config.SignConfig;
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
 * 请求体缓存过滤器（条件性）
 * <p>
 * 功能：包装 HttpServletRequest，缓存请求体内容，使其可以被多次读取
 * <p>
 * 使用原因：
 * - HTTP 请求的 InputStream 只能读取一次
 * - SignatureInterceptor 需要读取请求体进行签名验证
 * - Controller 也需要读取请求体获取业务参数
 * - 因此需要缓存请求体，使其可以被多次读取
 * <p>
 * 优化策略（两种模式）：
 * 1. 路径模式匹配（推荐）：
 * - 在配置文件中指定需要缓存的路径模式（sign.cached-body-path-patterns）
 * - 只有匹配的路径才会缓存 body
 * - 例如：["/api/payment/**", "/api/order/**"]
 * 2. Header 检测模式（兜底）：
 * - 如果未配置路径模式，则检查请求是否包含签名 header
 * - 只有包含 Sign-appCode 或 Sign-sign header 的请求才缓存 body
 * <p>
 * 注意：
 * - 此 Filter 在 WebConfig 中通过 FilterRegistrationBean 注册
 * - 设置 Order(Ordered.HIGHEST_PRECEDENCE) 确保最先执行
 * - 不使用 @Component 注解，避免与 FilterRegistrationBean 冲突
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
     * <p>
     * 过滤逻辑（需同时满足）：
     * 1. 必须包含指定的签名 Header（防止对普通请求进行无意义的缓存）
     * 2. 必须是具有请求体（Body）的 HTTP 方法（POST, PUT, PATCH）
     * 3. 请求路径必须匹配配置的路径模式（SignConfig.getCachedBodyPathPatterns）
     * </p>
     *
     * @param request 当前 HTTP 请求
     * @return true 表示需要缓存，false 表示跳过
     */
    private boolean shouldCacheBody(HttpServletRequest request) {
        // 1. 提取并校验签名 Header（基础准入门槛）
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

        // 3. 路径模式匹配：仅对配置范围内的 API 进行 Body 缓存
        List<String> pathPatterns = signConfig.getCachedBodyPathPatterns();
        if (CollectionUtils.isEmpty(pathPatterns)) {
            // 如果未配置路径列表，出于安全和兼容性考虑，默认不缓存
            return false;
        }

        // 使用 ServletPath 兼容 ContextPath 部署场景
        // 当前路径跟yml配置需要缓存body的请求路径匹配时返回true
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
