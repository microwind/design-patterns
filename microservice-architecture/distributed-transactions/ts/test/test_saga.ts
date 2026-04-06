import { SagaCoordinator } from "../src/saga";

function assertEqual<T>(expected: T, actual: T, message: string): void {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`);
  }
}

const success = new SagaCoordinator(10, false);
const completed = success.execute("ORD-1001", "SKU-BOOK", 2);
assertEqual("COMPLETED", completed.status, "successful saga");
assertEqual(8, success.inventory.bookStock, "stock should decrease");

const failure = new SagaCoordinator(10, true);
const cancelled = failure.execute("ORD-1002", "SKU-BOOK", 2);
assertEqual("CANCELLED", cancelled.status, "failing saga should cancel");
assertEqual(10, failure.inventory.bookStock, "stock should be compensated");

console.log("distributed-transactions(ts) tests passed");
