package com.javaweborder.application.services;

import com.javaweborder.application.dto.OrderDTO;
import com.javaweborder.domain.order.Order;
import com.javaweborder.domain.order.OrderRepository;
import com.javaweborder.infrastructure.message.MessageQueueService;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class OrderService {

    private final OrderRepository orderRepository; // 订单仓储接口
    private final MessageQueueService messageQueueService; // 消息队列服务

    // 构造函数，初始化 OrderRepository 和 MessageQueueService
    public OrderService(OrderRepository orderRepository, MessageQueueService messageQueueService) {
        this.orderRepository = orderRepository;
        this.messageQueueService = messageQueueService;
    }

    // 生成时间戳 + 随机数的唯一订单号 (long)
    public static long generateOrderId() {
        long timestamp = System.currentTimeMillis();  // 毫秒时间戳
        int random = ThreadLocalRandom.current().nextInt(1000); // 0-999 的随机数
        return timestamp * 1000 + random;
    }

    // 创建订单并保存到仓储中
    public OrderDTO createOrder(String customerName, double amount) throws Exception {
        // 自动生成订单 ID
        Order newOrder = new Order(generateOrderId(), customerName, amount);

        // 保存订单
        orderRepository.save(newOrder);

        // 发送消息
        String message = String.format("Order created: ID=%d, Customer=%s, Amount=%.2f",
                newOrder.getId(), newOrder.getCustomerName(), newOrder.getAmount());
        messageQueueService.sendMessage(message);

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

        // 发送消息
        String message = String.format("Order canceled: ID=%d", id);
        messageQueueService.sendMessage(message);
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

        // 发送消息
        String message = String.format("Order updated: ID=%d, Customer=%s, Amount=%.2f",
                order.getId(), order.getCustomerName(), order.getAmount());
        messageQueueService.sendMessage(message);

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

        // 发送消息
        String message = String.format("Order deleted: ID=%d", id);
        messageQueueService.sendMessage(message);
    }

    // 列出全部订单[此处应该分页]
    public List<Order> listOrder() throws Exception {
        // 返回订单列表
        return orderRepository.findAll();
    }
}