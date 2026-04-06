package test;

import src.TracingPattern;

public class Test {

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void main(String[] args) {
        TracingPattern.TraceContext gateway = TracingPattern.gatewayEntry("TRACE-1001");
        TracingPattern.TraceContext order = TracingPattern.childSpan(gateway, "order-service", "SPAN-ORDER");
        TracingPattern.TraceContext inventory = TracingPattern.childSpan(order, "inventory-service", "SPAN-INVENTORY");

        assertEquals(gateway.getTraceId(), order.getTraceId(), "trace should propagate to order");
        assertEquals(order.getTraceId(), inventory.getTraceId(), "trace should propagate to inventory");
        assertEquals(gateway.getSpanId(), order.getParentSpanId(), "order parent should be gateway");
        assertEquals(order.getSpanId(), inventory.getParentSpanId(), "inventory parent should be order");

        System.out.println("distributed-tracing(java) tests passed");
    }
}
