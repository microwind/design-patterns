package src;

import java.util.ArrayList;
import java.util.List;

/**
 * OutboxPattern - Outbox 模式的 Java 实现
 *
 * 【设计模式】
 *   - 观察者模式（Observer Pattern）：outbox 事件被 relay 扫描并发布到 broker。
 *   - 命令模式（Command Pattern）：OutboxEvent 将事件封装为数据对象，relay 异步执行发布。
 *
 * 【架构思想】
 *   解决"写库成功 + 发消息失败"导致的数据与事件不一致问题。
 *   在同一"事务"中写入业务数据和 outbox 事件，relay 异步发布。
 *
 * 【开源对比】
 *   - Debezium：通过 CDC 监听 outbox 表变更日志，替代轮询 relay
 *   - Eventuate Tram：Java 框架，内置 outbox + relay + 消息去重
 *   本示例用内存列表模拟数据库表，MemoryBroker 模拟消息中间件。
 */
public class OutboxPattern {

    /**
     * Order - 订单实体
     */
    public static class Order {
        private final String orderId;
        private final String status;

        public Order(String orderId, String status) {
            this.orderId = orderId;
            this.status = status;
        }

        public String getOrderId() { return orderId; }
        public String getStatus() { return status; }
    }

    /**
     * OutboxEvent - Outbox 事件记录
     *
     * 【设计模式】命令模式：将"需要发布的事件"封装为数据对象。
     * status 字段控制发布状态：pending → published。
     */
    public static class OutboxEvent {
        private final String eventId;       // 事件唯一ID
        private final String aggregateId;   // 聚合根ID（订单ID）
        private final String eventType;     // 事件类型
        private String status;              // 发布状态：pending / published

        public OutboxEvent(String eventId, String aggregateId, String eventType, String status) {
            this.eventId = eventId;
            this.aggregateId = aggregateId;
            this.eventType = eventType;
            this.status = status;
        }

        public String getEventId() { return eventId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    /**
     * MemoryBroker - 内存消息代理（模拟 Kafka / RabbitMQ）
     */
    public static class MemoryBroker {
        private final List<String> published = new ArrayList<>();

        /** 发布事件到消息中间件 */
        public void publish(String eventId) {
            published.add(eventId);
        }

        public List<String> getPublished() { return published; }
    }

    /**
     * OutboxService - Outbox 服务
     *
     * 核心流程：
     *   1. createOrder：写入 orders + outbox（模拟同一事务）
     *   2. relayPending：扫描 pending 事件 → 发布到 broker → 标记 published
     */
    public static class OutboxService {
        private final List<Order> orders = new ArrayList<>();         // 模拟 orders 表
        private final List<OutboxEvent> outbox = new ArrayList<>();   // 模拟 outbox 表

        /**
         * 创建订单。同时写入 orders 和 outbox（模拟同一数据库事务）。
         */
        public void createOrder(String orderId) {
            // 写入订单
            orders.add(new Order(orderId, "CREATED"));
            // 写入 outbox 事件（同一"事务"）
            outbox.add(new OutboxEvent("EVT-" + orderId, orderId, "order_created", "pending"));
        }

        /**
         * relay 中继：扫描 pending 事件，发布到 broker 后标记 published。
         * 已标记 published 的事件不会被重复发布（安全重跑）。
         */
        public void relayPending(MemoryBroker broker) {
            for (OutboxEvent event : outbox) {
                if ("pending".equals(event.getStatus())) {
                    // 发布到消息中间件
                    broker.publish(event.getEventId());
                    // 标记为已发布
                    event.setStatus("published");
                }
            }
        }

        public List<Order> getOrders() { return orders; }
        public List<OutboxEvent> getOutbox() { return outbox; }
    }
}
