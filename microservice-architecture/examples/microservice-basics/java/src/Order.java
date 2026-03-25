package src;

public class Order {

    private final String orderId;
    private final String sku;
    private final int quantity;
    private final String status;

    public Order(String orderId, String sku, int quantity, String status) {
        this.orderId = orderId;
        this.sku = sku;
        this.quantity = quantity;
        this.status = status;
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
}
