package com.microwind.knife.domain.repository;

import com.microwind.knife.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

// 自定义OrderJpa模式Repository，除了基础方法外可以自定义方法
public interface CustomOrderJpaRepository {
    Optional<Order> findByOrderNo(String orderNo);  // 根据订单号查询
    Optional<Order> findByOrderNoWithItems(String orderNo);  // 根据订单号查询（包含订单项）
    List<Order> findByUserId(Long userId);         // 根据用户ID查询订单
    Page<Order> findAllOrders(Pageable pageable);  // 分页查询所有订单
    Page<Order> findAllOrdersWithItems(Pageable pageable);  // 分页查询所有订单（包含订单项）
    int updateOrderStatus(String orderNo, Order.OrderStatus status); // 更新订单状态
}