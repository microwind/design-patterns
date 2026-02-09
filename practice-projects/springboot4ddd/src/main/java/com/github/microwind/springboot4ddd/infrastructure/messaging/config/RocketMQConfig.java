package com.github.microwind.springboot4ddd.infrastructure.messaging.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * RocketMQ 配置类
 * 集中管理 RocketMQ 相关配置和常量
 * 支持优雅降级：当 RocketMQ 连接失败时，系统仍能正常启动
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class RocketMQConfig {

    /**
     * RocketMQ NameServer 地址
     */
    @Value("${rocketmq.name-server:}")
    private String nameServer;

    /**
     * 生产者组
     */
    @Value("${rocketmq.producer.group:}")
    private String producerGroup;

    /**
     * 消费者组
     */
    @Value("${rocketmq.consumer.group:}")
    private String consumerGroup;

    /**
     * 订单事件主题
     */
    public static final String ORDER_EVENTS_TOPIC = "order-events";

    /**
     * 订单创建事件标签
     */
    public static final String ORDER_CREATED_TAG = "OrderCreatedEvent";

    /**
     * 订单支付事件标签
     */
    public static final String ORDER_PAID_TAG = "OrderPaidEvent";

    /**
     * 订单取消事件标签
     */
    public static final String ORDER_CANCELLED_TAG = "OrderCancelledEvent";

    /**
     * 订单完成事件标签
     */
    public static final String ORDER_COMPLETED_TAG = "OrderCompletedEvent";

    /**
     * 初始化配置，打印 RocketMQ 配置信息
     * 测试 RocketMQ 连接可用性
     */
    @PostConstruct
    public void init() {
        log.info("========== RocketMQ 配置信息 ==========");
        log.info("NameServer: {}", nameServer);
        log.info("Producer Group: {}", producerGroup);
        log.info("Consumer Group: {}", consumerGroup);
        log.info("Order Events Topic: {}", ORDER_EVENTS_TOPIC);
        log.info("========================================");
        
        // 测试 RocketMQ 连接
        testRocketMQConnection();
    }
    
    /**
     * 测试 RocketMQ 连接可用性
     * 如果连接失败，记录警告日志但不阻止应用启动
     */
    private void testRocketMQConnection() {
        try {
            // 这里可以添加简单的连接测试逻辑
            // 由于 RocketMQ 的连接测试相对复杂，这里主要做配置验证
            if (nameServer == null || nameServer.trim().isEmpty()) {
                throw new IllegalArgumentException("RocketMQ NameServer 配置为空");
            }
            
            log.info("RocketMQ 配置验证通过");
            
        } catch (Exception e) {
            log.error("RocketMQ 配置验证失败，系统将继续运行但消息功能可能不可用: {}", e.getMessage());
            log.warn("建议检查 RocketMQ NameServer 配置和服务状态: {}", nameServer);
        }
    }

    public String getNameServer() {
        return nameServer;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }
    
    /**
     * 创建虚拟 RocketMQ Template，用于优雅降级
     * 当 RocketMQ 不可用时，返回一个不会导致应用启动失败的 Template
     */
    @Bean
    @ConditionalOnProperty(name = "rocketmq.fallback.enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean(RocketMQTemplate.class)
    public RocketMQTemplate fallbackRocketMQTemplate() {
        log.warn("为 RocketMQ 创建虚拟 Template，相关消息操作将失败");
        
        return new RocketMQTemplate() {
            @Override
            public org.apache.rocketmq.client.producer.SendResult syncSend(String destination, Object payload) {
                log.error("RocketMQ 不可用，无法发送消息到: {}", destination);
                throw new RuntimeException("RocketMQ 服务不可用，请检查 RocketMQ 服务状态");
            }
            
            @Override
            public void asyncSend(String destination, Object payload, org.apache.rocketmq.client.producer.SendCallback sendCallback) {
                log.error("RocketMQ 不可用，无法异步发送消息到: {}", destination);
                if (sendCallback != null) {
                    sendCallback.onException(new RuntimeException("RocketMQ 服务不可用"));
                }
            }
            
            @Override
            public void sendOneWay(String destination, Object payload) {
                log.error("RocketMQ 不可用，无法单向发送消息到: {}", destination);
                throw new RuntimeException("RocketMQ 服务不可用，请检查 RocketMQ 服务状态");
            }
            
            // 其他必需方法的默认实现
            @Override
            public org.apache.rocketmq.client.producer.SendResult syncSend(String destination, Object payload, long timeout) {
                return syncSend(destination, payload);
            }
            
            @Override
            public void asyncSend(String destination, Object payload, org.apache.rocketmq.client.producer.SendCallback sendCallback, long timeout) {
                asyncSend(destination, payload, sendCallback);
            }
        };
    }
}
