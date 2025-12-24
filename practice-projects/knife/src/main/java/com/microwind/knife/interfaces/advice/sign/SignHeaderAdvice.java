package com.microwind.knife.interfaces.advice.sign;

import com.microwind.knife.interfaces.vo.sign.SignHeaderRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.HandlerMapping;

import static cn.hutool.core.util.StrUtil.isBlank;

/**
 * 控制器增强：自动提取签名相关请求头（仅对签名相关Controller生效）
 */
@Slf4j
// 缩小作用域：仅对指定包下的Controller生效（避免全局无意义处理）
@ControllerAdvice(basePackages = "com.microwind.knife.interfaces.controllers")
public class SignHeaderAdvice {

    // 常量：请求头名称（避免硬编码）
    private static final String HEADER_APP_CODE = "appCode";
    private static final String HEADER_SIGN = "sign";
    private static final String HEADER_PATH = "path"; // path如未传递，则自动获取
    private static final String HEADER_TIME = "time";

    /**
     * 自动从请求头提取签名信息，绑定到Model中（key=signHeaders）
     */
    @ModelAttribute("signHeaders")
    public SignHeaderRequest extractSignHeaders(HttpServletRequest request) {
        // 1. 提取请求头
        String appCode = request.getHeader(HEADER_APP_CODE);
        String sign = request.getHeader(HEADER_SIGN);
        String timeStr = request.getHeader(HEADER_TIME);
        String path = request.getHeader(HEADER_PATH);
        log.debug("提取的请求头 - appCode: {}, sign: {}, path: {}, time: {}",
                appCode, sign == null ? null : "***", path, timeStr);

        // 2. 处理time字段（简化逻辑 + 加日志）
        Long time = null;
        if (!isBlank(timeStr)) {
            try {
                time = Long.parseLong(timeStr);
            } catch (NumberFormatException e) {
                log.warn("请求头[{}]格式错误，值为：{}", HEADER_TIME, timeStr, e);
            }
        }

        // 3. 处理path：获取 Controller 上定义的模板路径（如 /api/users/{userId}）
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        log.debug("请求URI: {}, 请求方式: {}", requestURI, method);
        String templatePath = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        log.debug("从BEST_MATCHING_PATTERN_ATTRIBUTE获取的模板路径: {}", templatePath);

        path = templatePath;
        if (path == null || path.isEmpty()) {
            String contextPath = request.getContextPath();
            path = requestURI.replaceFirst(contextPath, "");
            log.debug("模板路径为空，兜底取实际路径: {}", path);
        }

        // 4. 构建VO（若未用Lombok Builder，需改为new SignHeaderRequest() + set方法）
        SignHeaderRequest vo = SignHeaderRequest.builder()
                .appCode(appCode)
                .sign(sign)
                .time(time)
                .path(path)
                .build();
        log.debug("构建的SignHeaderRequest: {}", vo);

        return vo;
    }
}