package src;

import java.util.HashMap;
import java.util.Map;

public class IdempotencyPattern {

    public static class OrderResponse {
        private final String orderId;
        private final String sku;
        private final int quantity;
        private final String status;
        private final boolean replayed;

        public OrderResponse(String orderId, String sku, int quantity, String status, boolean replayed) {
            this.orderId = orderId;
            this.sku = sku;
            this.quantity = quantity;
            this.status = status;
            this.replayed = replayed;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getSku() {
            return sku;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getStatus() {
            return status;
        }

        public boolean isReplayed() {
            return replayed;
        }
    }

    private static class StoredResult {
        private final String fingerprint;
        private final OrderResponse response;

        private StoredResult(String fingerprint, OrderResponse response) {
            this.fingerprint = fingerprint;
            this.response = response;
        }
    }

    public static class IdempotencyOrderService {
        private final Map<String, StoredResult> store = new HashMap<>();

        public OrderResponse createOrder(String idempotencyKey, String orderId, String sku, int quantity) {
            String fingerprint = orderId + "|" + sku + "|" + quantity;
            StoredResult existing = store.get(idempotencyKey);
            if (existing != null) {
                if (!existing.fingerprint.equals(fingerprint)) {
                    return new OrderResponse(orderId, sku, quantity, "CONFLICT", false);
                }
                return new OrderResponse(
                        existing.response.getOrderId(),
                        existing.response.getSku(),
                        existing.response.getQuantity(),
                        existing.response.getStatus(),
                        true
                );
            }

            OrderResponse response = new OrderResponse(orderId, sku, quantity, "CREATED", false);
            store.put(idempotencyKey, new StoredResult(fingerprint, response));
            return response;
        }
    }
}
