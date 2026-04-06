package test;

import src.RetryPattern;

public class Test {
    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
    }

    public static void main(String[] args) {
        RetryPattern.ScriptedOperation op1 = new RetryPattern.ScriptedOperation(2);
        RetryPattern.RetryResult success = RetryPattern.retry(3, op1::call);
        assertEquals(true, success.ok(), "success");
        assertEquals(3, success.attempts(), "attempts");

        RetryPattern.ScriptedOperation op2 = new RetryPattern.ScriptedOperation(5);
        RetryPattern.RetryResult failure = RetryPattern.retry(3, op2::call);
        assertEquals(false, failure.ok(), "failure");
        assertEquals(3, failure.attempts(), "max attempts");

        System.out.println("retry-pattern(java) tests passed");
    }
}
