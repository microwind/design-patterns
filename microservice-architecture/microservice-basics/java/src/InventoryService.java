package src;

import java.util.HashMap;
import java.util.Map;

public class InventoryService implements InventoryClient {

    private final Map<String, Integer> stock = new HashMap<>();

    public InventoryService() {
        stock.put("SKU-BOOK", 10);
        stock.put("SKU-PEN", 1);
    }

    @Override
    public boolean reserve(String sku, int quantity) {
        Integer available = stock.get(sku);
        if (available == null || available < quantity) {
            return false;
        }
        stock.put(sku, available - quantity);
        return true;
    }

    public int available(String sku) {
        return stock.getOrDefault(sku, 0);
    }
}
