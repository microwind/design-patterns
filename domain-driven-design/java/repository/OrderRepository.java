// repository/OrderRepository.java
package repository;

import domain.Order;
import java.util.HashMap;
import java.util.Map;

public class OrderRepository {
    private Map<Integer, Order> orders = new HashMap<>();

    public void save(Order order) {
        orders.put(order.getId(), order);
    }

    public Order findById(int id) {
        return orders.get(id);
    }

    public void findAll() {
        if (orders.isEmpty()) {
            System.out.println("暂无订单历史记录。");
        } else {
            for (Order order : orders.values()) {
                order.display();
            }
        }
    }

    public void clear() {
        orders.clear();
        System.out.println("所有订单已清理");
    }
}