package com.microwind.knife.interfaces.advice;

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
 * 控制器增强：自动提取并校验签名相关请求头
 */
@Slf4j
@ControllerAdvice(basePackages = {
        "com.microwind.knife.interfaces.controllers.sign",
        "com.microwind.knife.interfaces.controllers.user",
        "com.microwind.knife.interfaces.controllers.admin"
})
public class SignHeaderAdvice {

    private static final String HEADER_APP_CODE = "Sign-appCode";
    private static final String HEADER_SIGN = "Sign-sign";
    private static final String HEADER_PATH = "Sign-path";
    private static final String HEADER_TIME = "Sign-time";

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
        String appCode = request.getHeader(HEADER_APP_CODE);
        String sign = request.getHeader(HEADER_SIGN);
        String timeStr = request.getHeader(HEADER_TIME);
        String clientPath = request.getHeader(HEADER_PATH);

        // 2. 快速失败校验 (Fast-Fail)
        if (StringUtils.isAllBlank(appCode, sign, timeStr)) {
            log.debug("签名请求头缺失: Sign-appCode={}, Sign-sign={}, Sign-time={}", appCode, sign, timeStr);
            // 建议抛出自定义异常，如 throw new BusinessException(ErrorCode.SIGN_HEADER_MISSING);
             throw new IllegalArgumentException("Missing mandatory signature headers");
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

        // 5. 路径一致性校验
        // 如果客户端传了 path，必须与服务端识别的路径一致，防止重放攻击或路径欺骗
        if (StringUtils.isNoneBlank(clientPath) && !clientPath.equals(serverPath)) {
            log.debug("路径不匹配: 客户端传输={}, 服务端识别={}", clientPath, serverPath);
//            throw new IllegalArgumentException("Request path mismatch for signature");
        }

        // 6. 构建并返回
        return SignHeaderRequest.builder()
                .appCode(appCode)
                .sign(sign)
                .time(time)
                .path(serverPath) // 统一使用服务端识别的路径参与后续签名校验
                .build();
    }

    private Long parseLongSafe(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid timestamp format");
        }
    }
}