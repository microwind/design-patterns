package test;

import src.SagaPattern;

public class Test {

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void main(String[] args) {
        SagaPattern.SagaCoordinator success = new SagaPattern.SagaCoordinator(10, false);
        SagaPattern.SagaOrder completed = success.execute("ORD-1001", "SKU-BOOK", 2);
        assertEquals("COMPLETED", completed.getStatus(), "successful saga");
        assertEquals(8, success.getInventory().getBookStock(), "stock decrease");

        SagaPattern.SagaCoordinator failure = new SagaPattern.SagaCoordinator(10, true);
        SagaPattern.SagaOrder cancelled = failure.execute("ORD-1002", "SKU-BOOK", 2);
        assertEquals("CANCELLED", cancelled.getStatus(), "cancelled saga");
        assertEquals(10, failure.getInventory().getBookStock(), "compensated stock");

        System.out.println("distributed-transactions(java) tests passed");
    }
}
