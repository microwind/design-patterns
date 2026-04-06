import { IdempotencyOrderService } from "../src/idempotency";

function assertEqual<T>(expected: T, actual: T, message: string): void {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`);
  }
}

const service = new IdempotencyOrderService();

const first = service.createOrder("IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 1);
assertEqual("CREATED", first.status, "first request should create");
assertEqual(false, first.replayed, "first request should not be replayed");

const second = service.createOrder("IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 1);
assertEqual("CREATED", second.status, "duplicate request should replay");
assertEqual(true, second.replayed, "duplicate request should be replayed");

const conflict = service.createOrder("IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 2);
assertEqual("CONFLICT", conflict.status, "different payload should conflict");
assertEqual(false, conflict.replayed, "conflict should not be replayed");

console.log("idempotency-pattern(ts) tests passed");
