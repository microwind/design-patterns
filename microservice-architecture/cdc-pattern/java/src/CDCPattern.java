package src;

import java.util.ArrayList;
import java.util.List;

/**
 * CDCPattern - 变更数据捕获（Change Data Capture）的 Java 实现
 *
 * 【设计模式】
 *   - 观察者模式（Observer Pattern）：Broker 接收变更事件并分发给下游订阅者。
 *   - 代理模式（Proxy Pattern）：Connector（RelayChanges）作为中间代理，解耦 DataStore 与 Broker。
 *
 * 【架构思想】
 *   业务写入时同步追加变更日志（ChangeRecord），Connector 扫描未处理变更
 *   并发布到 Broker，避免"双写不一致"（写库成功但发消息失败）。
 *
 * 【开源对比】
 *   - Debezium：通过解析数据库日志（MySQL Binlog / Postgres WAL）捕获变更
 *   - Canal（阿里）：MySQL Binlog 增量订阅和消费
 *   本示例用应用层变更日志 + 内存 Broker 简化，省略了日志解析和消息中间件。
 */
public class CDCPattern {

    /**
     * ChangeRecord - 变更记录
     *
     * 每次业务写入产生一条记录，processed 标记是否已被 Connector 处理。
     */
    public static class ChangeRecord {
        private final String changeId;
        private final String aggregateId;
        private final String changeType;
        private boolean processed;

        public ChangeRecord(String changeId, String aggregateId, String changeType, boolean processed) {
            this.changeId = changeId;
            this.aggregateId = aggregateId;
            this.changeType = changeType;
            this.processed = processed;
        }

        public String getChangeId() {
            return changeId;
        }

        public boolean isProcessed() {
            return processed;
        }

        public void setProcessed(boolean processed) {
            this.processed = processed;
        }
    }

    /**
     * DataStore - 数据存储（模拟数据库）
     *
     * 业务写入时自动追加未处理的变更记录。
     */
    public static class DataStore {
        private final List<ChangeRecord> changes = new ArrayList<>();

        /** 创建订单，同时追加一条 order_created 变更记录 */
        public void createOrder(String orderId) {
            changes.add(new ChangeRecord("CHG-" + orderId, orderId, "order_created", false));
        }

        /** Connector：扫描未处理变更，发布到 Broker 并标记为已处理 */
        public void relayChanges(Broker broker) {
            for (ChangeRecord change : changes) {
                if (!change.isProcessed()) {
                    broker.publish(change.getChangeId());
                    change.setProcessed(true);
                }
            }
        }

        public List<ChangeRecord> getChanges() {
            return changes;
        }
    }

    /** Broker - 消息代理（模拟 Kafka / RabbitMQ） */
    public static class Broker {
        private final List<String> published = new ArrayList<>();

        public void publish(String changeId) {
            published.add(changeId);
        }

        public List<String> getPublished() {
            return published;
        }
    }
}
