import { retry, ScriptedOperation } from "../src/retry";

function assertEqual<T>(expected: T, actual: T, message: string): void {
  if (expected !== actual) throw new Error(`${message} expected=${expected} actual=${actual}`);
}

const op1 = new ScriptedOperation(2);
const result1 = retry(3, () => op1.call());
assertEqual(true, result1.ok, "retry should succeed");
assertEqual(3, result1.attempts, "retry attempts");

const op2 = new ScriptedOperation(5);
const result2 = retry(3, () => op2.call());
assertEqual(false, result2.ok, "retry should fail");
assertEqual(3, result2.attempts, "retry max attempts");

console.log("retry-pattern(ts) tests passed");
