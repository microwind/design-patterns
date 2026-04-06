import { childSpan, gatewayEntry } from "../src/tracing";

function assertEqual<T>(expected: T, actual: T, message: string): void {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`);
  }
}

const gateway = gatewayEntry("TRACE-1001");
const order = childSpan(gateway, "order-service", "SPAN-ORDER");
const inventory = childSpan(order, "inventory-service", "SPAN-INVENTORY");

assertEqual(gateway.traceId, order.traceId, "gateway and order should share trace");
assertEqual(order.traceId, inventory.traceId, "order and inventory should share trace");
assertEqual(gateway.spanId, order.parentSpanId, "order parent should be gateway");
assertEqual(order.spanId, inventory.parentSpanId, "inventory parent should be order");

console.log("distributed-tracing(ts) tests passed");
