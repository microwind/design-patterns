// 应用层（协调领域逻辑，处理业务用例）：订单应用服务
// 应用层（协调领域逻辑，处理业务用例）：订单应用服务
package com.javaweborder.application.services;

import com.javaweborder.application.dto.OrderDTO;
import com.javaweborder.domain.order.Order;
import com.javaweborder.domain.order.OrderRepository;

import java.util.concurrent.ThreadLocalRandom;


public class OrderService {

    private final OrderRepository orderRepository; // 订单仓储接口

    // 构造函数，初始化 OrderRepository
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 生成时间戳 + 随机数的唯一订单号 (long)
    public static long generateOrderId() {
        long timestamp = System.currentTimeMillis();  // 毫秒时间戳
        int random = ThreadLocalRandom.current().nextInt(1000); // 0-999 的随机数
        return timestamp * 1000 + random;
    }

    // 创建订单并保存到仓储中
    public OrderDTO createOrder(String customerName, double amount) throws Exception {
        // 自动生成订单 ID ，实际应用中会采用分布式ID或者采用数据库自增键
        Order newOrder = new Order(generateOrderId(), customerName, amount);

        // 保存订单
        orderRepository.save(newOrder);

        // 返回订单 DTO
        return new OrderDTO(newOrder.getId(), newOrder.getCustomerName(), newOrder.getAmount());
    }

    // 取消订单
    public void cancelOrder(long id) throws Exception {
        // 获取订单
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new Exception("订单取消失败：订单未找到"));
        // 执行领域逻辑：取消订单
        order.cancel();
        // 保存订单
        orderRepository.save(order);
    }

    // 查询订单
    public OrderDTO getOrder(long id) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new Exception("订单未找到"));

        // 返回订单 DTO
        return new OrderDTO(order.getId(), order.getCustomerName(), order.getAmount());
    }

    // 更新订单的客户信息和金额
    public OrderDTO updateOrder(long id, String customerName, double amount) throws Exception {
        // 获取订单
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new Exception("订单未找到"));

        // 更新订单的客户信息和金额
        order.updateCustomerInfo(customerName);
        order.updateAmount(amount);

        // 保存更新后的订单
        orderRepository.save(order);

        // 返回更新后的订单 DTO
        return new OrderDTO(order.getId(), order.getCustomerName(), order.getAmount());
    }

    // 删除订单
    public void deleteOrder(long id) throws Exception {
        // 获取订单
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new Exception("订单未找到"));

        // 从仓储中删除订单
        orderRepository.delete(order.getId());
    }
}

