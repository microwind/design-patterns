package src;

public class OrderService {

    private final InventoryClient inventoryClient;

    public OrderService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    public Order createOrder(String orderId, String sku, int quantity) {
        boolean reserved = inventoryClient.reserve(sku, quantity);
        if (reserved) {
            return new Order(orderId, sku, quantity, "CREATED");
        }
        return new Order(orderId, sku, quantity, "REJECTED");
    }
}
