package test;

import src.APIGateway;

import java.util.HashMap;
import java.util.Map;

public class Test {

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    public static void main(String[] args) {
        APIGateway gateway = new APIGateway();
        gateway.use(APIGateway.requireUserHeader("/api/orders", "X-User"));
        gateway.register("/api/orders", APIGateway.orderServiceHandler());
        gateway.register("/api/inventory", APIGateway.inventoryServiceHandler());

        Map<String, String> securedHeaders = new HashMap<>();
        securedHeaders.put("X-User", "jarry");
        securedHeaders.put("X-Correlation-ID", "trace-1001");

        APIGateway.Response secured = gateway.handle(new APIGateway.Request(
                "GET",
                "/api/orders/ORD-1001",
                securedHeaders
        ));
        assertEquals(200, secured.getStatusCode(), "secured route should succeed");
        assertEquals("trace-1001", secured.getHeaders().get("X-Correlation-ID"), "trace id should be preserved");
        assertEquals("order-service", secured.getHeaders().get("X-Upstream-Service"), "order route");

        APIGateway.Response unauthorized = gateway.handle(new APIGateway.Request(
                "GET",
                "/api/orders/ORD-1002",
                new HashMap<>()
        ));
        assertEquals(401, unauthorized.getStatusCode(), "unauthorized route should fail");

        APIGateway.Response inventory = gateway.handle(new APIGateway.Request(
                "GET",
                "/api/inventory/SKU-BOOK",
                new HashMap<>()
        ));
        assertEquals(200, inventory.getStatusCode(), "inventory route should succeed");
        assertEquals("inventory-service", inventory.getHeaders().get("X-Upstream-Service"), "inventory route");

        APIGateway.Response missing = gateway.handle(new APIGateway.Request(
                "GET",
                "/api/unknown",
                new HashMap<>()
        ));
        assertEquals(404, missing.getStatusCode(), "unknown route should be missing");

        System.out.println("api-gateway(java) tests passed");
    }
}
