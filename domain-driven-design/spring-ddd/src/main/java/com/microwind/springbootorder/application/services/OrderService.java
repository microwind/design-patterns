package com.microwind.springbootorder.application.services;

import com.microwind.springbootorder.application.dto.OrderMapper;
import com.microwind.springbootorder.domain.order.Order;
import com.microwind.springbootorder.domain.order.OrderDomainService;
import com.microwind.springbootorder.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.microwind.springbootorder.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderDomainService orderDomainService;

    // 创建订单
    public Order createOrder(Order order) {
        // 业务逻辑，例如生成订单编号等
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
            String orderNo = "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            order.setOrderNo(orderNo);
        }
        if (order.getStatus() == null) {
            order.setStatus(Order.OrderStatus.CREATED);
        }
        return orderRepository.save(order);
    }

    // 根据订单编号查询订单
    public Order getByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with orderNo: " + orderNo));
    }

    // 查询用户订单列表
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    // 更新订单状态
    public Order updateOrderStatus(String orderNo, Order.OrderStatus status) {
        Order order = getByOrderNo(orderNo);
         order.setStatus(status);
        // 1. 方式1，先查并全量写入
        // return orderRepository.save(order);

        // 2. 方式2，仅更新状态，效率更高
        int rowCount = orderRepository.updateOrderStatus(orderNo, status);
        if (rowCount > 0) {
            return order;
        }
        throw new ResourceNotFoundException("Order with orderNo " + orderNo + " not found or status update failed.");
    }

    // 更新订单
    @Transactional
    public Order updateOrder(String orderNo, Order order) {
        Order existingOrder = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with orderNo: " + orderNo));
        if (order.getUserId()!= null) {
            existingOrder.setUserId(order.getUserId());
        }
        if (order.getAmount()!= null) {
            existingOrder.setAmount(order.getAmount());
        }
        if (order.getStatus()!= null) {
            existingOrder.setStatus(order.getStatus());
        }
        if (order.getOrderName()!= null) {
            existingOrder.setOrderName(order.getOrderName());
        }
        return orderRepository.save(existingOrder);
    }

    // 删除订单
    public void deleteOrder(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with orderNo: " + orderNo));
        orderRepository.delete(order);
    }

    // 获取所有订单
    public Page<Order> getAllOrders(Pageable pageable) {
        // 使用默认JPA查询
        // return orderRepository.findAll(pageable);
        // 使用自定义查询
        return orderRepository.findAllOrders(pageable);
    }

    // 支付订单
    public void payOrder(String orderNo) {
        Order order = getByOrderNo(orderNo);
        orderDomainService.payOrder(order); // 使用 OrderDomainService 处理支付逻辑
        orderRepository.save(order); // 更新数据库中的订单状态
    }

    // 取消订单
    public void cancelOrder(String orderNo) {
        Order order = getByOrderNo(orderNo);
        orderDomainService.cancelOrder(order); // 使用 OrderDomainService 处理取消逻辑
        orderRepository.save(order); // 更新数据库中的订单状态
    }
}
