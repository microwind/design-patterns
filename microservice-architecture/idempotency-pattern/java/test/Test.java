package test;

import src.IdempotencyPattern;

public class Test {

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void main(String[] args) {
        IdempotencyPattern.IdempotencyOrderService service = new IdempotencyPattern.IdempotencyOrderService();

        IdempotencyPattern.OrderResponse first = service.createOrder("IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 1);
        assertEquals("CREATED", first.getStatus(), "first request");
        assertEquals(false, first.isReplayed(), "first request should not replay");

        IdempotencyPattern.OrderResponse second = service.createOrder("IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 1);
        assertEquals("CREATED", second.getStatus(), "duplicate request");
        assertEquals(true, second.isReplayed(), "duplicate request should replay");

        IdempotencyPattern.OrderResponse conflict = service.createOrder("IDEMP-ORDER-1001", "ORD-1001", "SKU-BOOK", 2);
        assertEquals("CONFLICT", conflict.getStatus(), "conflicting request");
        assertEquals(false, conflict.isReplayed(), "conflict should not replay");

        System.out.println("idempotency-pattern(java) tests passed");
    }
}
