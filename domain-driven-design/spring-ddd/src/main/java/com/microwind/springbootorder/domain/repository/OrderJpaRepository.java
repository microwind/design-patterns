package com.microwind.springbootorder.domain.repository;

import com.microwind.springbootorder.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

// Order仓库Jpa模式接口，定义在Domain层，继承JpaRepository基类和自定义CustomOrderJpaRepository类。
public interface OrderJpaRepository extends JpaRepository<Order, Long>, CustomOrderJpaRepository {
    // JpaRepository 提供了很多内建的方法，比如 save(), findAll(), findById() 等
    // 如果要增加其他方法，从CustomOrderJpaRepository添加
}