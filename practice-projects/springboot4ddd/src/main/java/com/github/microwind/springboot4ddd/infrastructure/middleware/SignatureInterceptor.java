package com.github.microwind.springboot4ddd.infrastructure.middleware;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.microwind.springboot4ddd.infrastructure.config.ApiAuthConfig;
import com.github.microwind.springboot4ddd.infrastructure.config.SignConfig;
import com.github.microwind.springboot4ddd.infrastructure.util.SignatureUtil;
import com.github.microwind.springboot4ddd.interfaces.annotation.IgnoreSignHeader;
import com.github.microwind.springboot4ddd.interfaces.annotation.RequireSign;
import com.github.microwind.springboot4ddd.interfaces.annotation.WithParams;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 签名验证拦截器
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SignatureInterceptor implements HandlerInterceptor {

    private final SignConfig signConfig;
    private final ApiAuthConfig apiAuthConfig;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理 HandlerMethod
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 检查 @IgnoreSignHeader 注解
        if (handlerMethod.hasMethodAnnotation(IgnoreSignHeader.class)) {
            log.debug("检测到 @IgnoreSignHeader 注解，跳过签名验证");
            return true;
        }

        // 检查 @RequireSign 注解
        RequireSign methodAnnotation = handlerMethod.getMethodAnnotation(RequireSign.class);
        RequireSign classAnnotation = handlerMethod.getBeanType().getAnnotation(RequireSign.class);

        if (methodAnnotation == null && classAnnotation == null) {
            return true;
        }

        // 确定是否需要参数签名
        boolean withParams = determineWithParams(methodAnnotation, classAnnotation);

        // 提取签名 header
        String appCode = request.getHeader(SignConfig.HEADER_APP_CODE);
        String sign = request.getHeader(SignConfig.HEADER_SIGN);
        String timeStr = request.getHeader(SignConfig.HEADER_TIME);

        // 基础参数校验
        if (StringUtils.isAnyBlank(appCode, sign, timeStr)) {
            log.warn("签名验证失败：缺少必需的签名 header");
            throw new IllegalArgumentException("缺少必需的签名 header");
        }

        // 解析时间戳
        Long time;
        try {
            time = Long.parseLong(timeStr);
        } catch (NumberFormatException e) {
            log.warn("签名验证失败：时间戳格式错误");
            throw new IllegalArgumentException("时间戳格式错误");
        }

        // 时间戳有效性检查
        long currentTime = System.currentTimeMillis();
        if (Math.abs(currentTime - time) > signConfig.getSignatureTtl()) {
            log.warn("签名验证失败：签名已过期");
            throw new IllegalArgumentException("签名已过期");
        }

        // 从ApiAuthConfig获取应用配置
        ApiAuthConfig.AppConfig appConfig = apiAuthConfig.getAppConfigByCode(appCode);
        if (appConfig == null) {
            log.warn("签名验证失败：未知的 appCode={}", appCode);
            throw new IllegalArgumentException("未知的应用编码");
        }

        String secretKey = appConfig.getSecretKey();
        if (secretKey == null) {
            log.warn("签名验证失败：appCode={} 未配置密钥", appCode);
            throw new IllegalArgumentException("应用未配置密钥");
        }

        // 获取请求路径
        String templatePath = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String serverPath = (templatePath != null) ? templatePath : requestURI.substring(contextPath.length());

        // 验证权限
        if (!apiAuthConfig.hasPermission(appCode, serverPath)) {
            log.warn("签名验证失败：appCode={} 无权访问路径 {}", appCode, serverPath);
            throw new IllegalArgumentException("无权访问该接口");
        }

        // 执行签名验证
        boolean isValid;
        if (withParams) {
            Map<String, Object> params = extractRequestParams(request);
            log.debug("签名验证（带参数）- appCode={}, path={}, paramCount={}", appCode, serverPath, params.size());
            isValid = SignatureUtil.verifySignWithParams(appCode, secretKey, serverPath, time, params, sign);
        } else {
            log.debug("签名验证（不带参数）- appCode={}, path={}", appCode, serverPath);
            isValid = SignatureUtil.verifySign(appCode, secretKey, serverPath, time, sign);
        }

        if (isValid) {
            log.info("签名验证成功 - appCode={}, path={}", appCode, serverPath);
            return true;
        } else {
            log.warn("签名验证失败 - appCode={}, path={}, sign={}", appCode, serverPath, sign);
            throw new IllegalArgumentException("签名验证失败");
        }
    }

    private boolean determineWithParams(RequireSign methodAnnotation, RequireSign classAnnotation) {
        if (methodAnnotation != null && methodAnnotation.withParams() != WithParams.DEFAULT) {
            return methodAnnotation.withParams() == WithParams.TRUE;
        }
        if (classAnnotation != null && classAnnotation.withParams() != WithParams.DEFAULT) {
            return classAnnotation.withParams() == WithParams.TRUE;
        }
        return signConfig.isDefaultWithParams();
    }

    private Map<String, Object> extractRequestParams(HttpServletRequest request) {
        String method = request.getMethod();

        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
            return extractFromBody(request);
        } else {
            return extractFromQueryParams(request);
        }
    }

    private Map<String, Object> extractFromBody(HttpServletRequest request) {
        CachedBodyHttpServletRequest cachedRequest = unwrapRequest(request);
        if (cachedRequest == null) {
            return Collections.emptyMap();
        }

        String body = cachedRequest.getCachedBody();
        if (StringUtils.isBlank(body)) {
            return Collections.emptyMap();
        }

        try {
            return objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse request body to JSON", e);
            return Collections.emptyMap();
        }
    }

    private CachedBodyHttpServletRequest unwrapRequest(HttpServletRequest request) {
        HttpServletRequest current = request;
        while (current != null) {
            if (current instanceof CachedBodyHttpServletRequest) {
                return (CachedBodyHttpServletRequest) current;
            }
            if (current instanceof HttpServletRequestWrapper wrapper) {
                current = (HttpServletRequest) wrapper.getRequest();
            } else {
                break;
            }
        }
        return null;
    }

    private Map<String, Object> extractFromQueryParams(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();

        if (parameterMap == null || parameterMap.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            if (values != null && values.length > 0) {
                if (values.length == 1) {
                    params.put(key, values[0]);
                } else {
                    params.put(key, values);
                }
            }
        }

        return params;
    }
}
