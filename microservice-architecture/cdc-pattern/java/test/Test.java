package test;

import src.CDCPattern;

public class Test {
    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
    }

    public static void main(String[] args) {
        CDCPattern.DataStore store = new CDCPattern.DataStore();
        CDCPattern.Broker broker = new CDCPattern.Broker();
        store.createOrder("ORD-1001");
        assertEquals(false, store.getChanges().get(0).isProcessed(), "initial");
        store.relayChanges(broker);
        assertEquals(1, broker.getPublished().size(), "publish once");
        assertEquals(true, store.getChanges().get(0).isProcessed(), "processed");
        store.relayChanges(broker);
        assertEquals(1, broker.getPublished().size(), "no duplicate");
        System.out.println("cdc-pattern(java) tests passed");
    }
}
