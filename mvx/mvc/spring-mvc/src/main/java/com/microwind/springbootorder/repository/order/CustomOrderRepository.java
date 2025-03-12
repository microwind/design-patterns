package com.microwind.springbootorder.repository.order;

import com.microwind.springbootorder.models.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface CustomOrderRepository {
    Optional<Order> findByOrderNo(String orderNo);  // 根据订单号查询
    List<Order> findByUserId(Long userId);         // 根据用户ID查询订单
    Page<Order> findAll(PageRequest pageRequest);  // 分页查询所有订单
    int updateOrderStatus(String orderNo, Order.OrderStatus status); // 更新订单状态
}
