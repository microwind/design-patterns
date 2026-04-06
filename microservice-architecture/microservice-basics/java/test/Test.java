package test;

import src.InventoryService;
import src.Order;
import src.OrderService;

public class Test {

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    private static void assertEqualsInt(int expected, int actual, String message) {
        if (expected != actual) {
            throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void main(String[] args) {
        InventoryService inventoryService = new InventoryService();
        OrderService orderService = new OrderService(inventoryService);

        Order success = orderService.createOrder("ORD-1001", "SKU-BOOK", 2);
        assertEquals("CREATED", success.getStatus(), "status should be CREATED");
        assertEqualsInt(8, inventoryService.available("SKU-BOOK"), "stock should decrease");

        Order failed = orderService.createOrder("ORD-1002", "SKU-PEN", 2);
        assertEquals("REJECTED", failed.getStatus(), "status should be REJECTED");
        assertEqualsInt(1, inventoryService.available("SKU-PEN"), "stock should remain");

        System.out.println("microservice-basics(java) tests passed");
    }
}
