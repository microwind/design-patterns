import {
  callWithTimeout,
  CircuitBreaker,
  CircuitOpenError,
  OperationTimeoutError,
  retry,
  ScriptedDependency
} from "../src/resilience";

function assertEqual<T>(expected: T, actual: T, message: string): void {
  if (expected !== actual) {
    throw new Error(`${message} expected=${expected} actual=${actual}`);
  }
}

async function main(): Promise<void> {
  const retryDependency = new ScriptedDependency([
    { error: new Error("temporary failure") },
    { error: new Error("temporary failure") },
    { value: "OK" }
  ]);
  const retryResult = await retry(3, () => retryDependency.call());
  assertEqual("OK", retryResult.value, "retry should eventually succeed");
  assertEqual(3, retryResult.attempts, "retry should finish on third attempt");

  const timeoutDependency = new ScriptedDependency([{ value: "SLOW_OK", delayMs: 100 }]);
  try {
    await callWithTimeout(10, () => timeoutDependency.call());
    throw new Error("timeout should have failed");
  } catch (error) {
    if (!(error instanceof OperationTimeoutError)) {
      throw error;
    }
  }

  const breaker = new CircuitBreaker(2);
  const breakerDependency = new ScriptedDependency([
    { error: new Error("dependency down") },
    { error: new Error("dependency still down") },
    { value: "RECOVERED" }
  ]);
  assertEqual("FALLBACK", await breaker.execute(() => breakerDependency.call(), "FALLBACK"), "first failure");
  assertEqual("FALLBACK", await breaker.execute(() => breakerDependency.call(), "FALLBACK"), "second failure");

  try {
    await breaker.execute(() => breakerDependency.call(), "FALLBACK");
    throw new Error("circuit should be open");
  } catch (error) {
    if (!(error instanceof CircuitOpenError)) {
      throw error;
    }
  }

  breaker.reset();
  assertEqual("RECOVERED", await breaker.execute(() => breakerDependency.call(), "FALLBACK"), "reset should recover");

  console.log("resilience-patterns(ts) tests passed");
}

void main();
