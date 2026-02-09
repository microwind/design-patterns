package com.github.microwind.springboot4ddd.infrastructure.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 启动时检查外部依赖状态，确保服务可优雅启动
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Component
public class StartupDependencyChecker implements ApplicationRunner {

    @Qualifier("userDataSource")
    private final DataSource userDataSource;

    @Qualifier("orderDataSource")
    private final DataSource orderDataSource;

    private final DependencyStatusRegistry statusRegistry;

    @Value("${rocketmq.name-server:}")
    private String rocketMqNameServer;

    @Value("${dependency.check.timeout-ms:1500}")
    private int timeoutMs;

    public StartupDependencyChecker(
            @Qualifier("userDataSource") DataSource userDataSource,
            @Qualifier("orderDataSource") DataSource orderDataSource,
            DependencyStatusRegistry statusRegistry) {
        this.userDataSource = userDataSource;
        this.orderDataSource = orderDataSource;
        this.statusRegistry = statusRegistry;
    }

    @Override
    public void run(ApplicationArguments args) {
        checkDatabase(userDataSource, "MySQL(User)");
        checkDatabase(orderDataSource, "PostgreSQL(Order)");
        checkRocketMQ();

        if (statusRegistry.hasFailures()) {
            log.error("外部依赖服务存在异常，系统将以降级模式启动");
            for (String message : statusRegistry.failureMessages()) {
                log.error("依赖异常: {}", message);
            }
        } else {
            log.info("外部依赖服务检查通过");
        }
    }

    private void checkDatabase(DataSource dataSource, String name) {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(2)) {
                statusRegistry.markUp(name, "连接正常");
                log.info("{} 数据库连接正常", name);
            } else {
                statusRegistry.markDown(name, "连接无效");
                log.error("{} 数据库连接无效", name);
            }
        } catch (Exception e) {
            String message = e.getMessage() == null ? "数据库不可用" : e.getMessage();
            statusRegistry.markDown(name, "连接失败: " + message);
            log.error("{} 数据库连接失败: {}", name, message);
        }
    }

    private void checkRocketMQ() {
        if (rocketMqNameServer == null || rocketMqNameServer.trim().isEmpty()) {
            statusRegistry.markDown("RocketMQ", "rocketmq.name-server 未配置");
            log.error("RocketMQ NameServer 未配置，消息功能不可用");
            return;
        }

        List<String> endpoints = parseNameServers(rocketMqNameServer);
        String lastError = null;

        for (String endpoint : endpoints) {
            String[] parts = endpoint.split(":");
            String host = parts[0];
            int port = parts.length > 1 ? parsePort(parts[1]) : 9876;

            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), timeoutMs);
                statusRegistry.markUp("RocketMQ", "NameServer 可连接: " + endpoint);
                log.info("RocketMQ NameServer 可连接: {}", endpoint);
                return;
            } catch (Exception e) {
                lastError = e.getMessage();
                log.warn("RocketMQ NameServer 连接失败: {} - {}", endpoint, e.getMessage());
            }
        }

        String message = "无法连接 NameServer: " + rocketMqNameServer;
        if (lastError != null && !lastError.isBlank()) {
            message = message + "，原因: " + lastError;
        }
        statusRegistry.markDown("RocketMQ", message);
        log.error("RocketMQ 连接失败，消息功能不可用: {}", message);
    }

    private List<String> parseNameServers(String nameServer) {
        String[] parts = nameServer.split("[;,\\s]+");
        List<String> endpoints = new ArrayList<>();
        for (String part : parts) {
            if (part == null) {
                continue;
            }
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                endpoints.add(trimmed);
            }
        }
        return endpoints.isEmpty() ? List.of(nameServer) : endpoints;
    }

    private int parsePort(String portText) {
        try {
            return Integer.parseInt(portText);
        } catch (NumberFormatException ex) {
            return 9876;
        }
    }
}
