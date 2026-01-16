package com.microwind.knife.domain.repository.order;

import com.microwind.knife.domain.order.Order;
import com.microwind.knife.domain.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

// 自定义OrderJpa模式Repository，继承OrderRepository统一接口，除了基础方法外可以自定义额外的方法
public interface CustomOrderJpaRepository extends OrderRepository {
    // 额外的自定义方法：根据订单号查询（包含订单项）
    // 使用 @Query 注解显式指定查询，避免 Spring Data JPA 根据方法名自动生成查询
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.orderNo = :orderNo")
    Optional<Order> findByOrderNoWithItems(@Param("orderNo") String orderNo);

    // 重写从OrderRepository继承的方法，提供显式JPQL查询
    @Override
    @Query("SELECT o FROM Order o")
    Page<Order> findAllOrders(Pageable pageable);

    @Override
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items")
    Page<Order> findAllOrdersWithItems(Pageable pageable);

    @Override
    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.orderNo = :orderNo")
    int updateOrderStatus(@Param("orderNo") String orderNo, @Param("status") Order.OrderStatus status);
}