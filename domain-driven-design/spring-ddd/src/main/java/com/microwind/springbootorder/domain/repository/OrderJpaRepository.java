package com.microwind.springbootorder.domain.repository;

import com.microwind.springbootorder.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order, Long>, CustomOrderRepository {
    // OrderJpaRepository和OrderRepository任选其一即可
    // JpaRepository 提供了很多内建的方法，比如 save(), findAll(), findById() 等
    // 如果要增加其他方法，从CustomOrderRepository添加
}