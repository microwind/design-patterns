package com.microwind.knife.interfaces.advice;

import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.interfaces.annotation.IgnoreSignHeader;
import com.microwind.knife.interfaces.vo.sign.SignHeaderRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Optional;

/**
 * 控制器增强：自动提取签名相关请求头
 * <p>
 * 职责：仅负责提取和封装签名 header 信息，不负责验证
 * <p>
 * 说明：
 * - 签名验证由 SignatureInterceptor 统一处理
 * - 此类只是为了方便 Controller 通过 @ModelAttribute 获取签名信息
 * - 如果方法上有 @IgnoreSignHeader 注解，返回 null
 * - 如果签名 header 缺失，返回 null（由拦截器处理验证）
 */
@Slf4j
@ControllerAdvice(basePackages = {
        "com.microwind.knife.interfaces.controllers"
})
public class SignHeaderAdvice {

    @ModelAttribute("SignHeaders")
    public SignHeaderRequest extractSignHeaders(HttpServletRequest request) {
        // 检查注解：支持在方法上或类上标注 @IgnoreSignHeader 的跳过检查
        HandlerMethod handlerMethod = (HandlerMethod)
                request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        if (handlerMethod != null &&
                handlerMethod.hasMethodAnnotation(IgnoreSignHeader.class)) {
            log.debug("检测到 @IgnoreSignHeader，跳过签名头提取逻辑");
            return null;
        }

        // 1. 提取基础请求头
        String appCode = request.getHeader(SignConfig.HEADER_APP_CODE);
        String sign = request.getHeader(SignConfig.HEADER_SIGN);
        String timeStr = request.getHeader(SignConfig.HEADER_TIME);
        String clientPath = request.getHeader(SignConfig.HEADER_PATH);

        // 2. 如果所有签名 header 都缺失，返回 null（不抛出异常，由拦截器处理验证）
        if (StringUtils.isAllBlank(appCode, sign, timeStr)) {
            log.debug("签名请求头缺失: Sign-appCode={}, Sign-sign={}, Sign-time={}", appCode, sign, timeStr);
            return null;
        }

        // 3. 解析时间戳
        Long time = parseLongSafe(timeStr);

        // 4. 获取服务端匹配的模板路径 (Ant Path Style)
        // 注意：BEST_MATCHING_PATTERN_ATTRIBUTE 在某些 Filter 之后才可用，但在 ControllerAdvice 中是可靠的
        String templatePath = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        // 兜底逻辑：如果拿不到 templatePath（如 404 情况），取去除了 ContextPath 的 URI
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String serverPath = Optional.ofNullable(templatePath)
                .orElseGet(() -> requestURI.substring(contextPath.length()));

        // 5. 路径一致性检查（仅记录，不验证）
        // 实际验证由 SignatureInterceptor 处理
        if (StringUtils.isNoneBlank(clientPath) && !clientPath.equals(serverPath)) {
            log.debug("路径不匹配: 客户端传输={}, 服务端识别={}", clientPath, serverPath);
        }

        // 6. 构建并返回
        return SignHeaderRequest.builder()
                .appCode(appCode)
                .sign(sign)
                .time(time)
                .path(serverPath) // 统一使用服务端识别的路径参与后续签名校验
                .build();
    }

    /**
     * 安全解析 Long 类型的时间戳
     * <p>
     * 注意：此方法只负责解析，不负责验证
     * 如果格式错误，返回 null，由拦截器处理验证
     */
    private Long parseLongSafe(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.debug("时间戳格式错误: {}", value);
            return null;  // 不抛出异常，由拦截器处理验证
        }
    }
}