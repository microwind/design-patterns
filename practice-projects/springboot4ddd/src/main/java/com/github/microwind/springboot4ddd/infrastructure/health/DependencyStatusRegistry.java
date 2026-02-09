package com.github.microwind.springboot4ddd.infrastructure.health;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 依赖服务状态注册表
 * 用于在启动阶段记录依赖服务健康状况
 *
 * @author jarry
 * @since 1.0.0
 */
@Component
public class DependencyStatusRegistry {

    private final Map<String, DependencyStatus> statuses = new ConcurrentHashMap<>();

    public void markUp(String name, String message) {
        statuses.put(name, DependencyStatus.up(message));
    }

    public void markDown(String name, String message) {
        statuses.put(name, DependencyStatus.down(message));
    }

    public Map<String, DependencyStatus> snapshot() {
        return new LinkedHashMap<>(statuses);
    }

    public boolean hasFailures() {
        return statuses.values().stream()
                .anyMatch(status -> "DOWN".equalsIgnoreCase(status.status()));
    }

    public List<String> failureMessages() {
        List<String> messages = new ArrayList<>();
        statuses.forEach((name, status) -> {
            if ("DOWN".equalsIgnoreCase(status.status())) {
                String message = status.message() == null ? "" : status.message();
                messages.add(name + " - " + message);
            }
        });
        return messages;
    }
}
