package com.microwind.knife.application.services;

import com.microwind.knife.application.dto.order.OrderMapper;
import com.microwind.knife.domain.order.Order;
import com.microwind.knife.domain.order.OrderDomainService;
import com.microwind.knife.domain.repository.OrderJpaRepository;
import com.microwind.knife.domain.repository.OrderRepository;
import com.microwind.knife.exception.ResourceNotFoundException;
import com.microwind.knife.interfaces.request.order.CreateOrderRequest;
import com.microwind.knife.interfaces.request.order.UpdateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    // OrderRepository接口有多种实现，可任选其一

    // 1. 采用Spring Data Jpa模式，代码更加简单，数据可持久化
    private final OrderJpaRepository orderRepository;

    // 2. 采用Spring jdbcTemplate模式，纯SQL，性能更好
//    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;
    private final OrderDomainService orderDomainService;

    // 创建订单
    public Order createOrder(CreateOrderRequest request) {
        // 将Request转换为Order实体
        Order order = orderMapper.toEntity(request);

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
    @Transactional
    public Order updateOrderStatus1(String orderNo, UpdateOrderRequest request) {
        Order order = getByOrderNo(orderNo);

        // 只允许更新 status（Mapper 只做转换）
        if (request.getStatus() != null) {
            order.setStatus(
                    Order.OrderStatus.valueOf(request.getStatus())
            );
        }
        // JPA 脏检查自动 update
        return order;
    }

    // 更新订单状态
    public int updateOrderStatus(String orderNo, UpdateOrderRequest request) {
        if (request.getStatus() == null) {
            return 0;
        }
        Order.OrderStatus status;
        try {
            status = Order.OrderStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("无效的订单状态：" + request.getStatus());
        }

        // 命令式更新（CQRS 风格）
        int rowCount = orderRepository.updateOrderStatus(orderNo, status);
        if (rowCount > 0) {
            return rowCount;
        }
        throw new ResourceNotFoundException("没找到订单号：" + orderNo + "。");
    }

    // 更新订单
    @Transactional
    public Order updateOrder(String orderNo, UpdateOrderRequest request) {
        Order existingOrder = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with orderNo: " + orderNo));

        // 使用Mapper更新实体
        orderMapper.updateEntityFromRequest(request, existingOrder);

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
        // 使用默认JPA查询方法
//        return orderRepository.findAll(pageable);
        // 使用JPA自定义方法，两者均可
        return orderRepository.findAllOrders(pageable);
    }

    // 获取所有订单（包含订单项）
    public Page<Order> getAllOrdersWithItems(Pageable pageable) {
        return orderRepository.findAllOrdersWithItems(pageable);
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
