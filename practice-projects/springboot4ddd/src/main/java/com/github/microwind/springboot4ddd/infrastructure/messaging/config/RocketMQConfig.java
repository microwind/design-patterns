package com.github.microwind.springboot4ddd.infrastructure.messaging.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * RocketMQ 配置类
 * 集中管理 RocketMQ 相关配置和常量
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
    @Value("${rocketmq.name-server}")
    private String nameServer;

    /**
     * 生产者组
     */
    @Value("${rocketmq.producer.group}")
    private String producerGroup;

    /**
     * 消费者组
     */
    @Value("${rocketmq.consumer.group}")
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
     */
    @PostConstruct
    public void init() {
        log.info("========== RocketMQ 配置信息 ==========");
        log.info("NameServer: {}", nameServer);
        log.info("Producer Group: {}", producerGroup);
        log.info("Consumer Group: {}", consumerGroup);
        log.info("Order Events Topic: {}", ORDER_EVENTS_TOPIC);
        log.info("========================================");
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
}
