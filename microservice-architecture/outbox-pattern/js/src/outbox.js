export class MemoryBroker {
  constructor() {
    this.published = []
  }
}

export class OutboxService {
  constructor() {
    this.orders = []
    this.outbox = []
  }

  createOrder(orderId) {
    this.orders.push({ orderId, status: 'CREATED' })
    this.outbox.push({
      eventId: `EVT-${orderId}`,
      aggregateId: orderId,
      eventType: 'order_created',
      status: 'pending'
    })
  }

  relayPending(broker) {
    for (const event of this.outbox) {
      if (event.status === 'pending') {
        broker.published.push(event.eventId)
        event.status = 'published'
      }
    }
  }
}
