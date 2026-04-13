/**
 * @file cdc.ts - 变更数据捕获（CDC Pattern）的 TypeScript 实现
 *
 * 【设计模式】
 *   - 观察者模式（Observer Pattern）：Broker 接收变更事件并分发给下游订阅者。
 *   - 代理模式（Proxy Pattern）：relayChanges 作为中间代理，解耦 DataStore 与 Broker。
 *
 * 【架构思想】
 *   TypeScript 通过 ChangeRecord 类型提供编译期安全。
 *
 * 【开源对比】
 *   - NestJS + Kafka：通过 @nestjs/microservices 消费 CDC 事件
 *   本示例用数组 + 内存 Broker 简化。
 */

/** 变更记录类型，processed 标记是否已被 Connector 处理 */
export type ChangeRecord = {
  changeId: string;
  aggregateId: string;
  changeType: string;
  processed: boolean;
};

/** DataStore - 数据存储（模拟数据库），业务写入时自动追加变更记录 */
export class DataStore {
  changes: ChangeRecord[] = [];

  /** 创建订单，同时追加一条 order_created 变更记录 */
  createOrder(orderId: string): void {
    this.changes.push({
      changeId: `CHG-${orderId}`,
      aggregateId: orderId,
      changeType: "order_created",
      processed: false
    });
  }

  /** Connector：扫描未处理变更，发布到 Broker 并标记为已处理 */
  relayChanges(broker: Broker): void {
    for (const change of this.changes) {
      if (!change.processed) {
        broker.published.push(change.changeId);
        change.processed = true;
      }
    }
  }
}

/** Broker - 消息代理（模拟 Kafka / RabbitMQ） */
export class Broker {
  published: string[] = [];
}
