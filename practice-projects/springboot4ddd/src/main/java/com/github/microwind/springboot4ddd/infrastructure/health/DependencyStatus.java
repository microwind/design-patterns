package com.github.microwind.springboot4ddd.infrastructure.health;

import java.time.LocalDateTime;

/**
 * 依赖服务状态
 *
 * @author jarry
 * @since 1.0.0
 */
public record DependencyStatus(String status, String message, LocalDateTime checkedAt) {

    public static DependencyStatus up(String message) {
        return new DependencyStatus("UP", message, LocalDateTime.now());
    }

    public static DependencyStatus down(String message) {
        return new DependencyStatus("DOWN", message, LocalDateTime.now());
    }
}
