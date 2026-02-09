package com.github.microwind.springboot4ddd.interfaces.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 统一处理应用中的异常，包括数据库和MQ连接异常
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class RestGlobalExceptionHandler {

    /**
     * 处理数据库连接异常
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, Object>> handleSQLException(SQLException e) {
        log.error("数据库操作异常", e);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("code", "DATABASE_ERROR");
        response.put("message", "数据库服务不可用，请检查数据库连接状态");
        response.put("detail", e.getMessage());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * 处理RocketMQ相关异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);

        String message = e.getMessage();
        String code = "RUNTIME_ERROR";
        String userMessage = "系统处理请求时发生错误";

        // 根据异常消息判断具体错误类型
        if (message != null) {
            if (message.contains("RocketMQ") || message.contains("rocketmq")) {
                code = "MQ_ERROR";
                userMessage = "消息队列服务不可用，请检查MQ服务状态";
            } else if (message.contains("数据库") || message.contains("database") || message.contains("Database")) {
                code = "DATABASE_ERROR";
                userMessage = "数据库服务不可用，请检查数据库连接状态";
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("code", code);
        response.put("message", userMessage);
        response.put("detail", message);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        log.error("系统异常", e);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("code", "SYSTEM_ERROR");
        response.put("message", "系统内部错误");
        response.put("detail", e.getMessage());
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
