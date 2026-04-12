/**
 * @file outbox.js - Outbox 模式（Outbox Pattern）的 JavaScript 实现
 *
 * 【设计模式】
 *   - 观察者模式：outbox 事件被 relay 扫描并发布到 broker。
 *   - 命令模式：outbox 事件将"需要发布的事件"封装为数据对象。
 *
 * 【架构思想】
 *   解决"写库成功 + 发消息失败"导致的数据与事件不一致问题。
 *
 * 【开源对比】
 *   - Bull/BullMQ：Node.js 任务队列，可配合 outbox 模式
 *   本示例用内存数组模拟。
 */

/**
 * MemoryBroker - 内存消息代理（模拟 Kafka / RabbitMQ）
 */
export class MemoryBroker {
  constructor() {
    /** @type {string[]} 已发布的事件ID列表 */
    this.published = []
  }
}

/**
 * OutboxService - Outbox 服务
 *
 * 核心流程：
 *   1. createOrder：写入 orders + outbox（同一"事务"）
 *   2. relayPending：扫描 pending → 发布 → 标记 published
 */
export class OutboxService {
  constructor() {
    /** @type {Array} 模拟 orders 表 */
    this.orders = []
    /** @type {Array} 模拟 outbox 表 */
    this.outbox = []
  }

  /**
   * 创建订单，同时写入 orders 和 outbox（模拟同一事务）。
   * @param {string} orderId 订单ID
   */
  createOrder(orderId) {
    // 写入订单
    this.orders.push({ orderId, status: 'CREATED' })
    // 写入 outbox 事件（同一"事务"）
    this.outbox.push({
      eventId: `EVT-${orderId}`,
      aggregateId: orderId,
      eventType: 'order_created',
      status: 'pending'
    })
  }

  /**
   * relay 中继：扫描 pending 事件，发布后标记 published。
   * @param {MemoryBroker} broker 消息代理
   */
  relayPending(broker) {
    for (const event of this.outbox) {
      if (event.status === 'pending') {
        // 发布到消息中间件
        broker.published.push(event.eventId)
        // 标记为已发布
        event.status = 'published'
      }
    }
  }
}
