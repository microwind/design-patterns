package test;

import src.OutboxPattern;

public class Test {

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void main(String[] args) {
        OutboxPattern.OutboxService service = new OutboxPattern.OutboxService();
        OutboxPattern.MemoryBroker broker = new OutboxPattern.MemoryBroker();

        service.createOrder("ORD-1001");
        assertEquals(1, service.getOrders().size(), "order count");
        assertEquals("pending", service.getOutbox().get(0).getStatus(), "pending status");

        service.relayPending(broker);
        assertEquals(1, broker.getPublished().size(), "published count");
        assertEquals("published", service.getOutbox().get(0).getStatus(), "published status");

        service.relayPending(broker);
        assertEquals(1, broker.getPublished().size(), "should not duplicate publish");

        System.out.println("outbox-pattern(java) tests passed");
    }
}
