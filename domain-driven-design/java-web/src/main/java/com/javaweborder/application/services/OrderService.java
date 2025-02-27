// 应用层（协调领域逻辑，处理业务用例）：订单应用服务
package com.javaweborder.application.services;

import com.javaweborder.application.dto.OrderDTO;
import com.javaweborder.domain.order.Order;
import com.javaweborder.domain.order.OrderRepository;

public class OrderService {

    private final OrderRepository orderRepository; // 订单仓储接口

    // 构造函数，初始化 OrderRepository
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 创建订单并保存到仓储中
    public OrderDTO createOrder(String customerName, double amount) throws Exception {
        // 创建订单
        Order newOrder = new Order(0, customerName, amount);
        if (newOrder == null) {
            throw new Exception("订单创建失败");
        }

        // 保存订单
        boolean isSaved = orderRepository.save(newOrder);
        if (!isSaved) {
            throw new Exception("订单保存失败");
        }

        // 返回订单 DTO
        return new OrderDTO(newOrder.getId(), newOrder.getCustomerName(), newOrder.getTotalAmount());
    }

    // 取消订单
    public void cancelOrder(int id) throws Exception {
        Order order = orderRepository.findById(id); // 获取订单
        if (order == null) {
            throw new Exception("订单取消失败：订单未找到");
        }

        order.cancel(); // 执行领域逻辑：取消订单
        boolean isSaved = orderRepository.save(order); // 保存更新后的订单
        if (!isSaved) {
            throw new Exception("订单取消失败：无法保存更新后的订单");
        }
    }

    // 查询订单
    public OrderDTO getOrder(int id) throws Exception {
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new Exception("订单未找到");
        }

        // 返回订单 DTO
        return new OrderDTO(order.getId(), order.getCustomerName(), order.getTotalAmount());
    }

    // 更新订单的客户信息和金额
    public OrderDTO updateOrder(int id, String customerName, double amount) throws Exception {
        // 获取订单
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new Exception("订单未找到");
        }

        // 更新订单的客户信息和金额
        order.updateCustomerInfo(customerName);
        order.updateAmount(amount);

        // 保存更新后的订单
        boolean isSaved = orderRepository.save(order);
        if (!isSaved) {
            throw new Exception("更新订单失败：无法保存更新后的订单");
        }

        // 返回更新后的订单 DTO
        return new OrderDTO(order.getId(), order.getCustomerName(), order.getTotalAmount());
    }

    // 删除订单
    public void deleteOrder(int id) throws Exception {
        // 获取订单
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new Exception("订单未找到");
        }

        // 从仓储中删除订单
        boolean isDeleted = orderRepository.delete(order.getId());
        if (!isDeleted) {
            throw new Exception("删除订单失败");
        }
    }
}

