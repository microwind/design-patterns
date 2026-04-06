package src;

public class SagaPattern {

    public static class SagaOrder {
        private final String orderId;
        private String status;

        public SagaOrder(String orderId, String status) {
            this.orderId = orderId;
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class InventoryService {
        private int bookStock;

        public InventoryService(int stock) {
            this.bookStock = stock;
        }

        public boolean reserve(String sku, int quantity) {
            if (!"SKU-BOOK".equals(sku) || quantity <= 0 || bookStock < quantity) {
                return false;
            }
            bookStock -= quantity;
            return true;
        }

        public void release(String sku, int quantity) {
            if ("SKU-BOOK".equals(sku) && quantity > 0) {
                bookStock += quantity;
            }
        }

        public int getBookStock() {
            return bookStock;
        }
    }

    public static class PaymentService {
        private final boolean fail;

        public PaymentService(boolean fail) {
            this.fail = fail;
        }

        public boolean charge(String orderId) {
            return !fail;
        }
    }

    public static class SagaCoordinator {
        private final InventoryService inventory;
        private final PaymentService payment;

        public SagaCoordinator(int stock, boolean paymentFails) {
            this.inventory = new InventoryService(stock);
            this.payment = new PaymentService(paymentFails);
        }

        public SagaOrder execute(String orderId, String sku, int quantity) {
            SagaOrder order = new SagaOrder(orderId, "PENDING");
            if (!inventory.reserve(sku, quantity)) {
                order.setStatus("CANCELLED");
                return order;
            }
            if (!payment.charge(orderId)) {
                inventory.release(sku, quantity);
                order.setStatus("CANCELLED");
                return order;
            }
            order.setStatus("COMPLETED");
            return order;
        }

        public InventoryService getInventory() {
            return inventory;
        }
    }
}
