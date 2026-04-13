/**
 * @file cdc.js - 变更数据捕获（CDC Pattern）的 JavaScript 实现
 *
 * 【设计模式】
 *   - 观察者模式（Observer Pattern）：Broker 接收变更事件并分发给下游订阅者。
 *   - 代理模式（Proxy Pattern）：relayChanges 作为中间代理，解耦 DataStore 与 Broker。
 *
 * 【架构思想】
 *   业务写入时同步追加变更日志，Connector 扫描未处理变更并发布到 Broker。
 *
 * 【开源对比】
 *   - Node.js + Kafka：通过 kafkajs 消费 Debezium 变更事件
 *   本示例用数组 + 内存 Broker 简化。
 */

/**
 * DataStore - 数据存储（模拟数据库），业务写入时自动追加变更记录。
 */
export class DataStore {
  constructor() {
    /** @type {Array<{changeId: string, aggregateId: string, changeType: string, processed: boolean}>} 变更记录列表 */
    this.changes = []
  }

  /** 创建订单，同时追加一条 order_created 变更记录 */
  createOrder(orderId) {
    this.changes.push({
      changeId: `CHG-${orderId}`,
      aggregateId: orderId,
      changeType: 'order_created',
      processed: false
    })
  }

  /** Connector：扫描未处理变更，发布到 Broker 并标记为已处理 */
  relayChanges(broker) {
    for (const change of this.changes) {
      if (!change.processed) {
        broker.published.push(change.changeId)
        change.processed = true
      }
    }
  }
}

/**
 * Broker - 消息代理（模拟 Kafka / RabbitMQ）
 */
export class Broker {
  constructor() {
    this.published = []
  }
}
