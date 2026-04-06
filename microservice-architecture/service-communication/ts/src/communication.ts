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
