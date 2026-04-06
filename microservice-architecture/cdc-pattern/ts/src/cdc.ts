export type ChangeRecord = {
  changeId: string;
  aggregateId: string;
  changeType: string;
  processed: boolean;
};

export class DataStore {
  changes: ChangeRecord[] = [];

  createOrder(orderId: string): void {
    this.changes.push({
      changeId: `CHG-${orderId}`,
      aggregateId: orderId,
      changeType: "order_created",
      processed: false
    });
  }

  relayChanges(broker: Broker): void {
    for (const change of this.changes) {
      if (!change.processed) {
        broker.published.push(change.changeId);
        change.processed = true;
      }
    }
  }
}

export class Broker {
  published: string[] = [];
}
