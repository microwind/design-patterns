export class DataStore {
  constructor() {
    this.changes = []
  }

  createOrder(orderId) {
    this.changes.push({
      changeId: `CHG-${orderId}`,
      aggregateId: orderId,
      changeType: 'order_created',
      processed: false
    })
  }

  relayChanges(broker) {
    for (const change of this.changes) {
      if (!change.processed) {
        broker.published.push(change.changeId)
        change.processed = true
      }
    }
  }
}

export class Broker {
  constructor() {
    this.published = []
  }
}
