/**
 * @file outbox.ts - Outbox 模式（Outbox Pattern）的 TypeScript 实现
 *
 * 【设计模式】
 *   - 观察者模式：outbox 事件被 relay 扫描并发布到 broker。
 *   - 命令模式：OutboxEvent 将事件封装为类型安全的数据对象。
 *
 * 【架构思想】
 *   TypeScript 通过类型定义保证了事件结构的编译期安全。
 *
 * 【开源对比】
 *   - BullMQ：TypeScript 原生支持的任务队列
 *   本示例用内存数组模拟。
 */

/** 订单类型 */
export type Order = {
  orderId: string;
  status: string;
};

/** Outbox 事件类型。status 控制发布状态：pending → published。 */
export type OutboxEvent = {
  eventId: string;      // 事件唯一ID
  aggregateId: string;  // 聚合根ID
  eventType: string;    // 事件类型
  status: string;       // pending / published
};

/**
 * OutboxService - Outbox 服务
 */
export class OutboxService {
  orders: Order[] = [];           // 模拟 orders 表
  outbox: OutboxEvent[] = [];     // 模拟 outbox 表

  /** 创建订单，同时写入 orders 和 outbox（模拟同一事务）。 */
  createOrder(orderId: string): void {
    this.orders.push({ orderId, status: "CREATED" });
    this.outbox.push({
      eventId: `EVT-${orderId}`,
      aggregateId: orderId,
      eventType: "order_created",
      status: "pending"
    });
  }

  /** relay 中继：扫描 pending 事件，发布后标记 published。 */
  relayPending(broker: MemoryBroker): void {
    for (const event of this.outbox) {
      if (event.status === "pending") {
        broker.published.push(event.eventId);
        event.status = "published";
      }
    }
  }
}

/** 内存消息代理（模拟 Kafka / RabbitMQ） */
export class MemoryBroker {
  published: string[] = [];
}
