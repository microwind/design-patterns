// service/OrderService.java
package service;

import domain.Order;
import repository.OrderRepository;

public class OrderService {
    private OrderRepository orderRepository = new OrderRepository();

    public void createOrder(int id, String customerName, double amount) {
        Order order = new Order(id, customerName, amount);
        orderRepository.save(order);
        System.out.println("订单 ID " + id + " 创建成功");
    }

    public void cancelOrder(int id) {
        Order order = orderRepository.findById(id);
        if (order != null) {
            order.cancel();
        } else {
            System.out.println("未找到 ID " + id);
        }
    }

    public void queryOrder(int id) {
        Order order = orderRepository.findById(id);
        if (order != null) {
            order.display();
        } else {
            System.out.println("未找到 ID " + id);
        }
    }

    public void viewOrderHistory() {
        orderRepository.findAll();
    }
}