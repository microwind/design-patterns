import { CircuitBreaker } from "../src/breaker";

function assertEqual<T>(expected: T, actual: T, message: string): void {
  if (expected !== actual) throw new Error(`${message} expected=${expected} actual=${actual}`);
}

const breaker = new CircuitBreaker(2);
assertEqual("closed", breaker.state, "initial");
breaker.recordFailure();
breaker.recordFailure();
assertEqual("open", breaker.state, "open");
breaker.probe(true);
assertEqual("closed", breaker.state, "close after success");
breaker.recordFailure();
breaker.recordFailure();
breaker.probe(false);
assertEqual("open", breaker.state, "reopen after failed probe");
console.log("circuit-breaker(ts) tests passed");
