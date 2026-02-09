package com.github.microwind.springboot4ddd.interfaces.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认控制器
 * 提供基础的API接口，用于验证服务是否正常启动
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class DefaultController {

    /**
     * 默认欢迎接口
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "欢迎使用 SpringBoot4DDD 系统");
        response.put("status", "RUNNING");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 系统信息接口
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "microwind.springboot4ddd");
        response.put("description", "基于Spring Boot的领域驱动设计示例项目");
        response.put("features", new String[]{
            "优雅降级支持",
            "多数据源配置", 
            "RocketMQ消息队列",
            "RESTful API"
        });
        response.put("status", "RUNNING");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 服务状态检查
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "UP");
        response.put("message", "服务运行正常，数据库和MQ连接状态请访问 /api/health");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }
}
