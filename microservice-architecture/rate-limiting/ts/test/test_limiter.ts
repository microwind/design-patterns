import { FixedWindowLimiter } from "../src/limiter";

function assertEqual<T>(expected: T, actual: T, message: string): void {
  if (expected !== actual) throw new Error(`${message} expected=${expected} actual=${actual}`);
}

const limiter = new FixedWindowLimiter(3);
assertEqual(true, limiter.allow(), "first request");
assertEqual(true, limiter.allow(), "second request");
assertEqual(true, limiter.allow(), "third request");
assertEqual(false, limiter.allow(), "fourth request");
limiter.advanceWindow();
assertEqual(true, limiter.allow(), "request after reset");
console.log("rate-limiting(ts) tests passed");
