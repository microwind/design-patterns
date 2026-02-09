package com.github.microwind.springboot4ddd.interfaces.controller;

import com.github.microwind.springboot4ddd.infrastructure.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 *
 * @author jarry
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/basic")
public class HealthController {

    /**
     * 健康检查接口
     *
     * @return 健康状态
     */
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("application", "springboot4ddd");
        healthInfo.put("version", "1.0.0");
        return ApiResponse.success(healthInfo);
    }

    /**
     * 首页接口
     *
     * @return 欢迎信息
     */
    @GetMapping("/")
    public ApiResponse<String> index() {
        return ApiResponse.success("Welcome to Spring Boot 4 DDD Project!");
    }
}
