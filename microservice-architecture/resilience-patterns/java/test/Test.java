package test;

import src.ResiliencePatterns;

import java.time.Duration;
import java.util.List;

public class Test {

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void main(String[] args) throws Exception {
        ResiliencePatterns.ScriptedDependency retryDependency = new ResiliencePatterns.ScriptedDependency(List.of(
                new ResiliencePatterns.Result(null, new RuntimeException("temporary failure"), 0),
                new ResiliencePatterns.Result(null, new RuntimeException("temporary failure"), 0),
                new ResiliencePatterns.Result("OK", null, 0)
        ));
        ResiliencePatterns.RetryOutcome outcome = ResiliencePatterns.retry(3, retryDependency::call);
        assertEquals("OK", outcome.value(), "retry result");
        assertEquals(3, outcome.attempts(), "retry attempts");

        ResiliencePatterns.ScriptedDependency timeoutDependency = new ResiliencePatterns.ScriptedDependency(List.of(
                new ResiliencePatterns.Result("SLOW_OK", null, 100)
        ));
        try {
            ResiliencePatterns.callWithTimeout(Duration.ofMillis(10), timeoutDependency::call);
            throw new RuntimeException("timeout should have failed");
        } catch (ResiliencePatterns.OperationTimeoutException ignored) {
        }

        ResiliencePatterns.CircuitBreaker breaker = new ResiliencePatterns.CircuitBreaker(2);
        ResiliencePatterns.ScriptedDependency breakerDependency = new ResiliencePatterns.ScriptedDependency(List.of(
                new ResiliencePatterns.Result(null, new RuntimeException("dependency down"), 0),
                new ResiliencePatterns.Result(null, new RuntimeException("dependency still down"), 0),
                new ResiliencePatterns.Result("RECOVERED", null, 0)
        ));
        assertEquals("FALLBACK", breaker.execute(breakerDependency::call, "FALLBACK"), "first failure fallback");
        assertEquals("FALLBACK", breaker.execute(breakerDependency::call, "FALLBACK"), "second failure fallback");

        try {
            breaker.execute(breakerDependency::call, "FALLBACK");
            throw new RuntimeException("circuit should be open");
        } catch (ResiliencePatterns.CircuitOpenException ignored) {
        }

        breaker.reset();
        assertEquals("RECOVERED", breaker.execute(breakerDependency::call, "FALLBACK"), "recovery");

        System.out.println("resilience-patterns(java) tests passed");
    }
}
