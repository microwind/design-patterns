import { MemoryBroker, OutboxService } from "../src/outbox";

function assertEqual<T>(expected: T, actual: T, message: string): void {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`);
  }
}

const service = new OutboxService();
const broker = new MemoryBroker();

service.createOrder("ORD-1001");
assertEqual(1, service.orders.length, "should create order");
assertEqual("pending", service.outbox[0].status, "event should be pending");

service.relayPending(broker);
assertEqual(1, broker.published.length, "event should publish once");
assertEqual("published", service.outbox[0].status, "event should be marked published");

service.relayPending(broker);
assertEqual(1, broker.published.length, "rerun should not duplicate publish");

console.log("outbox-pattern(ts) tests passed");
