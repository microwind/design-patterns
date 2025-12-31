package com.microwind.knife.middleware;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.dto.sign.SignDTO;
import com.microwind.knife.application.services.sign.SignService;
import com.microwind.knife.common.ApiResponse;
import com.microwind.knife.interfaces.annotation.IgnoreSignHeader;
import com.microwind.knife.interfaces.annotation.RequireSign;
import com.microwind.knife.interfaces.annotation.WithParams;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 签名验证拦截器
 * <p>
 * 功能：拦截所有带有 @RequireSign 注解的接口，进行签名验证
 * <p>
 * 工作流程：
 * 1. 检查方法或类上是否有 @RequireSign 注解
 * 2. 如果方法上有 @IgnoreSignHeader 注解，跳过验证（优先级最高）
 * 3. 从 header 中提取签名信息（Sign-appCode, Sign-sign, Sign-time, Sign-path）
 * 4. 根据注解的 withParams 参数（WithParams 枚举）决定验证算法
 * 5. 如果需要参数签名，提取参数 Map 并进行 ASCII 排序验证
 * 6. 调用 SignService 进行签名验证
 * 7. 验证失败返回 401 错误
 * <p>
 * 安全特性：
 * - 使用 WithParams 枚举而非字符串，类型安全
 * - 参数签名支持多种 HTTP 方法（POST/PUT/PATCH 从 body 读取，GET/DELETE 从 query 读取）
 * - 参数按 ASCII 排序后进行签名验证，确保一致性
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SignatureInterceptor implements HandlerInterceptor {

    private final SignService signService;
    private final SignConfig signConfig;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只处理 HandlerMethod（Controller 方法）
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 1. 检查方法上是否有 @IgnoreSignHeader 注解（优先级最高）
        if (handlerMethod.hasMethodAnnotation(IgnoreSignHeader.class)) {
            log.debug("检测到 @IgnoreSignHeader 注解，跳过签名验证");
            return true;
        }

        // 2. 检查方法或类上是否有 @RequireSign 注解
        RequireSign methodAnnotation = handlerMethod.getMethodAnnotation(RequireSign.class);
        RequireSign classAnnotation = handlerMethod.getBeanType().getAnnotation(RequireSign.class);

        if (methodAnnotation == null && classAnnotation == null) {
            // 没有 @RequireSign 注解，不需要验证
            return true;
        }

        // 3. 确定是否需要参数签名（方法注解优先于类注解）
        boolean withParams = determineWithParams(methodAnnotation, classAnnotation);

        // 4. 提取签名相关的 header
        String appCode = request.getHeader(SignConfig.HEADER_APP_CODE);
        String sign = request.getHeader(SignConfig.HEADER_SIGN);
        String timeStr = request.getHeader(SignConfig.HEADER_TIME);
        String clientPath = request.getHeader(SignConfig.HEADER_PATH);

        // 5. 基础参数校验
        if (StringUtils.isAnyBlank(appCode, sign, timeStr)) {
            log.warn("签名验证失败：缺少必需的签名 header - appCode={}, sign={}, time={}", appCode, sign, timeStr);
            throw new IllegalArgumentException("缺少必需的签名 header");
        }

        // 6. 解析时间戳
        Long time;
        try {
            time = Long.parseLong(timeStr);
        } catch (NumberFormatException e) {
            log.warn("签名验证失败：时间戳格式错误 - time={}", timeStr);
            throw new IllegalArgumentException("时间戳格式错误。");
        }

        // 7. 获取服务端匹配的模板路径
        String templatePath = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String serverPath = (templatePath != null) ? templatePath : requestURI.substring(contextPath.length());

        // 8. 路径一致性校验（可选）
        if (StringUtils.isNotBlank(clientPath) && !clientPath.equals(serverPath)) {
            log.warn("签名验证失败：路径不匹配 - 客户端={}, 服务端={}", clientPath, serverPath);
            // 可以选择严格校验或忽略
            // return false;
        }

        // 9. 构建 SignDTO
        SignDTO signDTO = new SignDTO();
        signDTO.setAppCode(appCode);
        signDTO.setSignValue(sign);
        signDTO.setTimestamp(time);
        signDTO.setApiPath(serverPath);

        // 10. 执行签名验证
        boolean isValid;
        if (withParams) {
            // 需要参数签名，提取参数 Map 并进行 ASCII 排序验证
            Map<String, Object> params = extractRequestBody(request);
            log.debug("签名验证（带参数）- appCode={}, path={}, withParams=true, paramCount={}",
                    appCode, serverPath, params.size());
            isValid = signService.validateSignWithParams(signDTO, params);
        } else {
            // 不需要参数签名
            log.debug("签名验证（不带参数）- appCode={}, path={}, withParams=false", appCode, serverPath);
            isValid = signService.validateSign(signDTO);
        }

        // 11. 验证结果处理
        if (isValid) {
            log.info("签名验证成功 - appCode={}, path={}", appCode, serverPath);
            return true;
        } else {
            log.warn("签名验证失败 - appCode={}, path={}, sign={}", appCode, serverPath, sign);
            throw new IllegalArgumentException("签名验证失败，签名不正确或已过期。");
        }
    }

    /**
     * 确定是否需要参数签名
     * <p>
     * 优先级：方法注解 > 类注解 > 配置文件默认值
     * </p>
     *
     * @param methodAnnotation 方法级别的 @RequireSign 注解
     * @param classAnnotation  类级别的 @RequireSign 注解
     * @return true 表示需要参数签名，false 表示不需要
     */
    private boolean determineWithParams(RequireSign methodAnnotation, RequireSign classAnnotation) {
        // 方法注解优先
        if (methodAnnotation != null && methodAnnotation.withParams() != WithParams.DEFAULT) {
            return methodAnnotation.withParams() == WithParams.TRUE;
        }

        // 类注解次之
        if (classAnnotation != null && classAnnotation.withParams() != WithParams.DEFAULT) {
            return classAnnotation.withParams() == WithParams.TRUE;
        }

        // 使用配置文件的默认值
        return signConfig.isDefaultWithParams();
    }

    /**
     * 从请求中提取参数（支持所有 HTTP 方法）
     * <p>
     * 提取策略：
     * 1. POST/PUT/PATCH：优先从请求体（body）读取 JSON 参数
     * 2. GET/DELETE：从 URL query 参数读取（request.getParameterMap()）
     * 3. 如果请求体为空（即使是 POST），也会尝试从 query 参数读取
     * <p>
     * 优化点：
     * 1. 支持多种 HTTP 方法（GET、POST、PUT、DELETE、PATCH 等）
     * 2. 使用 TypeReference 进行类型安全的反序列化
     * 3. 返回不可变空 Map，防止后续误操作
     * 4. 详细的异常处理和日志记录
     */
    private Map<String, Object> extractRequestBody(HttpServletRequest request) {
        String method = request.getMethod();

        // 1. 对于有 body 的请求方法（POST、PUT、PATCH），优先从 body 读取
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
            return extractFromBody(request);
        } else {
            // 2. 对于 GET、DELETE 或 body 为空的情况，从 URL query 参数读取
            return extractFromQueryParams(request);
        }
    }

    /**
     * 从请求体（body）中提取 JSON 参数
     * <p>
     * 从 CachedBodyHttpServletRequest 中读取已缓存的 body（由 CachedBodyFilter 缓存）
     */
    private Map<String, Object> extractFromBody(HttpServletRequest request) {
        // 从包装的请求中提取 CachedBodyHttpServletRequest
        CachedBodyHttpServletRequest cachedRequest = unwrapRequest(request);
        if (cachedRequest == null) {
            log.warn("Cannot find CachedBodyHttpServletRequest. This should not happen if CachedBodyFilter is properly configured.");
            return Collections.emptyMap();
        }

        String body = cachedRequest.getCachedBody();
        if (StringUtils.isBlank(body)) {
            log.debug("Request body is empty");
            return Collections.emptyMap();
        }

        try {
            // 使用 TypeReference 进行类型安全的反序列化
            Map<String, Object> params = objectMapper.readValue(body, new TypeReference<Map<String, Object>>() {});
            log.debug("Extracted {} parameters from request body", params.size());
            return params;
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse request body to JSON. Body: {}", body, e);
            return Collections.emptyMap();
        }
    }

    /**
     * 从包装的请求中递归提取 CachedBodyHttpServletRequest
     * <p>
     * Spring Security 等框架可能会重新包装请求，需要递归查找原始的 CachedBodyHttpServletRequest
     * </p>
     */
    private CachedBodyHttpServletRequest unwrapRequest(HttpServletRequest request) {
        HttpServletRequest current = request;
        while (current != null) {
            if (current instanceof CachedBodyHttpServletRequest) {
                log.debug("Found CachedBodyHttpServletRequest");
                return (CachedBodyHttpServletRequest) current;
            }
            if (current instanceof HttpServletRequestWrapper wrapper) {
                current = (HttpServletRequest) wrapper.getRequest();
            } else {
                break;
            }
        }
        log.warn("CachedBodyHttpServletRequest not found in request chain");
        return null;
    }

    /**
     * 从 URL query 参数中提取参数
     * <p>
     * 例如：/api/users?userId=123&name=test
     * 会被转换为：{"userId": "123", "name": "test"}
     */
    private Map<String, Object> extractFromQueryParams(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();

        if (parameterMap == null || parameterMap.isEmpty()) {
            return Collections.emptyMap();
        }

        // 将 String[] 转换为单值（取第一个值）
        Map<String, Object> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            if (values != null && values.length > 0) {
                // 如果只有一个值，直接使用；如果有多个值，保留数组
                if (values.length == 1) {
                    params.put(key, values[0]);
                } else {
                    params.put(key, values);
                }
            }
        }

        log.debug("Extracted {} parameters from query string", params.size());
        return params;
    }
}
