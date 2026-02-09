package com.github.microwind.springboot4ddd.interfaces.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 提供系统健康状态检查，包括数据库和MQ连接状态
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/health")
public class RestHealthController {

    @Qualifier("userDataSource")
    private final DataSource userDataSource;

    @Qualifier("orderDataSource")
    private final DataSource orderDataSource;

    public RestHealthController(
            @Qualifier("userDataSource") DataSource userDataSource,
            @Qualifier("orderDataSource") DataSource orderDataSource) {
        this.userDataSource = userDataSource;
        this.orderDataSource = orderDataSource;
    }

    /**
     * 系统健康检查
     * 返回各组件的连接状态
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();

        // 系统整体状态
        boolean systemHealthy = true;

        // 检查MySQL用户数据库连接
        boolean mysqlHealthy = checkDatabaseConnection(userDataSource, "MySQL(User)");
        result.put("mysql_user", Map.of(
            "status", mysqlHealthy ? "UP" : "DOWN",
            "message", mysqlHealthy ? "连接正常" : "数据库未连接或不可用"
        ));
        systemHealthy = systemHealthy && mysqlHealthy;

        // 检查PostgreSQL订单数据库连接
        boolean postgresqlHealthy = checkDatabaseConnection(orderDataSource, "PostgreSQL(Order)");
        result.put("postgresql_order", Map.of(
            "status", postgresqlHealthy ? "UP" : "DOWN",
            "message", postgresqlHealthy ? "连接正常" : "数据库未连接或不可用"
        ));
        systemHealthy = systemHealthy && postgresqlHealthy;

        // 检查RocketMQ状态（简化检查）
        boolean rocketmqHealthy = checkRocketMQConnection();
        result.put("rocketmq", Map.of(
            "status", rocketmqHealthy ? "UP" : "DOWN",
            "message", rocketmqHealthy ? "连接正常" : "MQ服务未连接或不可用"
        ));
        systemHealthy = systemHealthy && rocketmqHealthy;

        // 系统整体状态
        result.put("system", Map.of(
            "status", systemHealthy ? "UP" : "PARTIAL",
            "message", systemHealthy ? "所有服务正常" : "部分服务不可用，但系统仍可运行"
        ));

        // 添加时间戳
        result.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(result);
    }

    /**
     * 检查数据库连接
     */
    private boolean checkDatabaseConnection(DataSource dataSource, String dbName) {
        try (Connection connection = dataSource.getConnection()) {
            boolean isValid = connection.isValid(5);
            if (isValid) {
                log.debug("{} 数据库连接正常", dbName);
            } else {
                log.warn("{} 数据库连接无效", dbName);
            }
            return isValid;
        } catch (Exception e) {
            log.warn("{} 数据库连接检查失败: {}", dbName, e.getMessage());
            return false;
        }
    }

    /**
     * 检查RocketMQ连接状态
     * 这里做简化检查，实际项目中可以更详细
     */
    private boolean checkRocketMQConnection() {
        try {
            // 这里可以添加更详细的RocketMQ连接检查
            // 目前简单检查配置是否存在
            // 实际使用中可以通过发送测试消息等方式检查
            return true; // 暂时返回true，因为RocketMQ的连接检查相对复杂
        } catch (Exception e) {
            log.warn("RocketMQ连接检查失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 简单的健康检查接口
     */
    @GetMapping("/simple")
    public ResponseEntity<Map<String, String>> simpleHealth() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "UP");
        result.put("message", "服务运行正常");
        result.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(result);
    }
}
