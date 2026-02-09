package com.github.microwind.springboot4ddd.interfaces.controller;

import com.github.microwind.springboot4ddd.infrastructure.common.ApiResponse;
import com.github.microwind.springboot4ddd.infrastructure.health.DependencyStatusRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认页面
 *
 * @author jarry
 * @since 1.0.0
 */
@RestController
@RequestMapping("")
@Slf4j
@RequiredArgsConstructor
public class IndexController {
    private final DependencyStatusRegistry statusRegistry;

    /**
     * 首页接口
     *
     * @return 欢迎信息
     */
    @GetMapping("/")
    public ApiResponse<Map<String, Object>> index() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Spring Boot 4 DDD Project!");
        response.put("timestamp", LocalDateTime.now());
        response.put("dependencies", statusRegistry.snapshot());

        if (statusRegistry.hasFailures()) {
            List<String> errors = statusRegistry.failureMessages();
            response.put("errors", errors);
            log.warn("依赖服务异常: {}", errors);
        }

        return ApiResponse.success("Welcome to Spring Boot 4 DDD Project!", response);
    }
}
