export type Order = {
  orderId: string;
  status: string;
};

export type OutboxEvent = {
  eventId: string;
  aggregateId: string;
  eventType: string;
  status: string;
};

export class OutboxService {
  orders: Order[] = [];
  outbox: OutboxEvent[] = [];

  createOrder(orderId: string): void {
    this.orders.push({ orderId, status: "CREATED" });
    this.outbox.push({
      eventId: `EVT-${orderId}`,
      aggregateId: orderId,
      eventType: "order_created",
      status: "pending"
    });
  }

  relayPending(broker: MemoryBroker): void {
    for (const event of this.outbox) {
      if (event.status === "pending") {
        broker.published.push(event.eventId);
        event.status = "published";
      }
    }
  }
}

export class MemoryBroker {
  published: string[] = [];
}
