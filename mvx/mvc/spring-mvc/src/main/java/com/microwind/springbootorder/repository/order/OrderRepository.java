package com.microwind.springbootorder.repository.order;

import com.microwind.springbootorder.models.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Long>, CustomOrderRepository {
    // JpaRepository 提供了很多内建的方法，比如 save(), findAll(), findById() 等
    // 如果要增加，则从CustomOrderRepository添加
}