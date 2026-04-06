package test;

import com.sun.net.httpserver.HttpServer;
import src.HttpInventoryClient;
import src.Order;
import src.OrderService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TestHttp {

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new RuntimeException(message + " expected=" + expected + " actual=" + actual);
        }
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isBlank()) {
            return params;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(kv[0], kv[1]);
            }
        }
        return params;
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        int[] bookStock = new int[]{2};

        server.createContext("/reserve", exchange -> {
            URI uri = exchange.getRequestURI();
            Map<String, String> query = parseQuery(uri.getRawQuery());
            String sku = query.getOrDefault("sku", "");
            int qty = Integer.parseInt(query.getOrDefault("quantity", "0"));

            int code;
            String body;
            if ("SKU-BOOK".equals(sku) && qty > 0 && bookStock[0] >= qty) {
                bookStock[0] -= qty;
                code = 200;
                body = "OK";
            } else {
                code = 409;
                body = "NO_STOCK";
            }

            exchange.sendResponseHeaders(code, body.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
        });

        server.start();
        String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();

        try {
            OrderService orderService = new OrderService(new HttpInventoryClient(baseUrl));

            Order success = orderService.createOrder("ORD-2001", "SKU-BOOK", 1);
            assertEquals("CREATED", success.getStatus(), "http status should be CREATED");

            Order failed = orderService.createOrder("ORD-2002", "SKU-BOOK", 2);
            assertEquals("REJECTED", failed.getStatus(), "http status should be REJECTED");

            System.out.println("microservice-basics(java/http) tests passed");
        } finally {
            server.stop(0);
        }
    }
}
