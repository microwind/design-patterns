package com.github.microwind.springboot4ddd.infrastructure.messaging.order.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.microwind.springboot4ddd.infrastructure.messaging.order.message.OrderCancelledMessage;
import com.github.microwind.springboot4ddd.infrastructure.messaging.order.message.OrderCompletedMessage;
import com.github.microwind.springboot4ddd.infrastructure.messaging.order.message.OrderCreatedMessage;
import com.github.microwind.springboot4ddd.infrastructure.messaging.order.message.OrderPaidMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 订单事件消费者
 * 监听并处理订单相关的 RocketMQ 消息
 *
 * @author jarry
 * @since 1.0.0
 */
@Slf4j
@Component
public class OrderEventConsumer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * 订单创建事件消费者
     */
    @Slf4j
    @Component
    @RocketMQMessageListener(
            topic = "order-events",
            consumerGroup = "springboot4ddd-order-created-consumer",
            selectorExpression = "OrderCreatedEvent"
    )
    public static class OrderCreatedConsumer implements RocketMQListener<String> {

        @Override
        public void onMessage(String message) {
            try {
                log.info("收到订单创建消息：{}", message);

                OrderCreatedMessage orderMessage = OBJECT_MAPPER.readValue(message, OrderCreatedMessage.class);

                // 处理订单创建消息（演示性质）
                log.info("处理订单创建消息 - 订单号：{}，用户ID：{}，金额：{}",
                        orderMessage.getOrderNo(), orderMessage.getUserId(), orderMessage.getTotalAmount());

                // 模拟业务处理：发送通知
                sendCreationNotification(orderMessage);

                log.info("订单创建消息处理完成，eventId={}", orderMessage.getEventId());

            } catch (Exception e) {
                log.error("处理订单创建消息失败", e);
                throw new RuntimeException("订单创建消息处理失败", e);
            }
        }

        /**
         * 模拟发送订单创建通知
         */
        private void sendCreationNotification(OrderCreatedMessage message) {
            log.info("【模拟通知】向用户 {} 发送订单创建通知，订单号：{}",
                    message.getUserId(), message.getOrderNo());
        }
    }

    /**
     * 订单支付事件消费者
     */
    @Slf4j
    @Component
    @RocketMQMessageListener(
            topic = "order-events",
            consumerGroup = "springboot4ddd-order-paid-consumer",
            selectorExpression = "OrderPaidEvent"
    )
    public static class OrderPaidConsumer implements RocketMQListener<String> {

        @Override
        public void onMessage(String message) {
            try {
                log.info("收到订单支付消息：{}", message);

                OrderPaidMessage orderMessage = OBJECT_MAPPER.readValue(message, OrderPaidMessage.class);

                // 处理订单支付消息（演示性质）
                log.info("处理订单支付消息 - 订单号：{}，用户ID：{}，金额：{}",
                        orderMessage.getOrderNo(), orderMessage.getUserId(), orderMessage.getTotalAmount());

                // 模拟业务处理：发送支付确认
                sendPaymentConfirmation(orderMessage);

                // 模拟业务处理：更新库存
                updateInventory(orderMessage);

                log.info("订单支付消息处理完成，eventId={}", orderMessage.getEventId());

            } catch (Exception e) {
                log.error("处理订单支付消息失败", e);
                throw new RuntimeException("订单支付消息处理失败", e);
            }
        }

        /**
         * 模拟发送支付确认通知
         */
        private void sendPaymentConfirmation(OrderPaidMessage message) {
            log.info("【模拟通知】向用户 {} 发送支付成功通知，订单号：{}，支付金额：{}",
                    message.getUserId(), message.getOrderNo(), message.getTotalAmount());
        }

        /**
         * 模拟更新库存
         */
        private void updateInventory(OrderPaidMessage message) {
            log.info("【模拟业务】订单 {} 支付成功，更新库存", message.getOrderNo());
        }
    }

    /**
     * 订单取消事件消费者
     */
    @Slf4j
    @Component
    @RocketMQMessageListener(
            topic = "order-events",
            consumerGroup = "springboot4ddd-order-cancelled-consumer",
            selectorExpression = "OrderCancelledEvent"
    )
    public static class OrderCancelledConsumer implements RocketMQListener<String> {

        @Override
        public void onMessage(String message) {
            try {
                log.info("收到订单取消消息：{}", message);

                OrderCancelledMessage orderMessage = OBJECT_MAPPER.readValue(message, OrderCancelledMessage.class);

                // 处理订单取消消息（演示性质）
                log.info("处理订单取消消息 - 订单号：{}，用户ID：{}",
                        orderMessage.getOrderNo(), orderMessage.getUserId());

                // 模拟业务处理：释放库存
                releaseInventory(orderMessage);

                log.info("订单取消消息处理完成，eventId={}", orderMessage.getEventId());

            } catch (Exception e) {
                log.error("处理订单取消消息失败", e);
                throw new RuntimeException("订单取消消息处理失败", e);
            }
        }

        /**
         * 模拟释放库存
         */
        private void releaseInventory(OrderCancelledMessage message) {
            log.info("【模拟业务】订单 {} 已取消，释放库存", message.getOrderNo());
        }
    }

    /**
     * 订单完成事件消费者
     */
    @Slf4j
    @Component
    @RocketMQMessageListener(
            topic = "order-events",
            consumerGroup = "springboot4ddd-order-completed-consumer",
            selectorExpression = "OrderCompletedEvent"
    )
    public static class OrderCompletedConsumer implements RocketMQListener<String> {

        @Override
        public void onMessage(String message) {
            try {
                log.info("收到订单完成消息：{}", message);

                OrderCompletedMessage orderMessage = OBJECT_MAPPER.readValue(message, OrderCompletedMessage.class);

                // 处理订单完成消息（演示性质）
                log.info("处理订单完成消息 - 订单号：{}，用户ID：{}，金额：{}",
                        orderMessage.getOrderNo(), orderMessage.getUserId(), orderMessage.getTotalAmount());

                // 模拟业务处理：更新统计数据
                updateStatistics(orderMessage);

                // 模拟业务处理：发送完成通知
                sendCompletionNotification(orderMessage);

                log.info("订单完成消息处理完成，eventId={}", orderMessage.getEventId());

            } catch (Exception e) {
                log.error("处理订单完成消息失败", e);
                throw new RuntimeException("订单完成消息处理失败", e);
            }
        }

        /**
         * 模拟更新统计数据
         */
        private void updateStatistics(OrderCompletedMessage message) {
            log.info("【模拟业务】订单 {} 已完成，更新统计数据，累计金额：{}",
                    message.getOrderNo(), message.getTotalAmount());
        }

        /**
         * 模拟发送完成通知
         */
        private void sendCompletionNotification(OrderCompletedMessage message) {
            log.info("【模拟通知】向用户 {} 发送订单完成通知，订单号：{}",
                    message.getUserId(), message.getOrderNo());
        }
    }
}
