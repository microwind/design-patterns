package test;

import src.CircuitBreakerPattern;

public class Test {
    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
    }

    public static void main(String[] args) {
        CircuitBreakerPattern.CircuitBreaker breaker = new CircuitBreakerPattern.CircuitBreaker(2);
        assertEquals("closed", breaker.getState(), "initial");
        breaker.recordFailure();
        breaker.recordFailure();
        assertEquals("open", breaker.getState(), "open");
        breaker.probe(true);
        assertEquals("closed", breaker.getState(), "close");
        breaker.recordFailure();
        breaker.recordFailure();
        breaker.probe(false);
        assertEquals("open", breaker.getState(), "reopen");
        System.out.println("circuit-breaker(java) tests passed");
    }
}
