/**
 * @file communication.ts - 服务间通信模式（Service Communication Pattern）的 TypeScript 实现
 *
 * 本模块演示微服务架构中两种核心通信方式：同步通信（请求-响应）和异步通信（事件驱动）。
 *
 * 【设计模式】
 *   - 外观模式（Facade Pattern）：SynchronousOrderService 将多个服务调用封装为
 *     统一的 placeOrder 接口，调用方无需了解内部编排逻辑。
 *   - 观察者模式（Observer Pattern）：EventBus 实现发布-订阅机制，
 *     AsyncOrderService 发布事件，下游通过 subscribe 注册处理函数。
 *   - 中介者模式（Mediator Pattern）：EventBus 充当中介者，协调多服务间交互。
 *
 * 【架构思想】
 *   同步通信简单直观但耦合度高；异步通信通过事件总线解耦，支持独立扩展和故障隔离。
 *   TypeScript 版本利用类型系统提供了更好的类型安全。
 *
 * 【开源对比】
 *   - 同步：axios / node-fetch + TypeScript 类型定义
 *   - 异步：bullmq（原生 TS）、kafkajs（原生 TS）
 *   本示例省略了网络传输和序列化，聚焦于同步/异步两种编排模式的对比。
 */

export type Order = {
  orderId: string;
  sku: string;
  quantity: number;
  status: string;
};

export class InventoryService {
  private readonly stock: Record<string, number>;

  constructor(stock: Record<string, number>) {
    this.stock = { ...stock };
  }

  reserve(sku: string, quantity: number): boolean {
    const available = this.stock[sku] ?? 0;
    if (quantity <= 0 || available < quantity) {
      return false;
    }
    this.stock[sku] = available - quantity;
    return true;
  }
}

export class PaymentService {
  private readonly failOrderIds: Set<string>;

  constructor(failOrderIds: string[] = []) {
    this.failOrderIds = new Set(failOrderIds);
  }

  charge(orderId: string): boolean {
    return !this.failOrderIds.has(orderId);
  }
}

export class SynchronousOrderService {
  constructor(
    private readonly inventory: InventoryService,
    private readonly payment: PaymentService
  ) {}

  placeOrder(orderId: string, sku: string, quantity: number): Order {
    if (!this.inventory.reserve(sku, quantity)) {
      return { orderId, sku, quantity, status: "REJECTED" };
    }
    if (!this.payment.charge(orderId)) {
      return { orderId, sku, quantity, status: "PAYMENT_FAILED" };
    }
    return { orderId, sku, quantity, status: "CREATED" };
  }
}

export type Event = {
  name: string;
  orderId: string;
  sku: string;
  quantity: number;
};

export class EventBus {
  private subscribers: Record<string, Array<(event: Event) => void>> = {};
  private queue: Event[] = [];

  subscribe(eventName: string, handler: (event: Event) => void): void {
    this.subscribers[eventName] = this.subscribers[eventName] ?? [];
    this.subscribers[eventName].push(handler);
  }

  publish(event: Event): void {
    this.queue.push(event);
  }

  drain(): void {
    while (this.queue.length > 0) {
      const event = this.queue.shift()!;
      for (const handler of this.subscribers[event.name] ?? []) {
        handler(event);
      }
    }
  }
}

export class OrderStore {
  private orders: Record<string, Order> = {};

  save(order: Order): void {
    this.orders[order.orderId] = order;
  }

  updateStatus(orderId: string, status: string): void {
    const order = this.orders[orderId];
    this.orders[orderId] = { ...order, status };
  }

  get(orderId: string): Order {
    return this.orders[orderId];
  }
}

export class AsyncOrderService {
  constructor(private readonly bus: EventBus, private readonly store: OrderStore) {}

  placeOrder(orderId: string, sku: string, quantity: number): Order {
    const order = { orderId, sku, quantity, status: "PENDING" };
    this.store.save(order);
    this.bus.publish({ name: "order_placed", orderId, sku, quantity });
    return order;
  }
}

export function registerAsyncWorkflow(
  bus: EventBus,
  store: OrderStore,
  inventory: InventoryService,
  payment: PaymentService
): void {
  bus.subscribe("order_placed", (event: Event) => {
    if (!inventory.reserve(event.sku, event.quantity)) {
      store.updateStatus(event.orderId, "REJECTED");
      return;
    }
    if (!payment.charge(event.orderId)) {
      store.updateStatus(event.orderId, "PAYMENT_FAILED");
      return;
    }
    store.updateStatus(event.orderId, "CREATED");
  });
}
