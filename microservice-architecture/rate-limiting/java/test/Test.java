package test;

import src.FixedWindowLimiter;

public class Test {
    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
    }

    public static void main(String[] args) {
        FixedWindowLimiter limiter = new FixedWindowLimiter(3);
        assertEquals(true, limiter.allow(), "first");
        assertEquals(true, limiter.allow(), "second");
        assertEquals(true, limiter.allow(), "third");
        assertEquals(false, limiter.allow(), "fourth");
        limiter.advanceWindow();
        assertEquals(true, limiter.allow(), "after reset");
        System.out.println("rate-limiting(java) tests passed");
    }
}
