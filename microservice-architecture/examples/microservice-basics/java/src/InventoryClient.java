package src;

public interface InventoryClient {
    boolean reserve(String sku, int quantity);
}
