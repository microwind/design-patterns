package com.microwind.springbootorder.domain.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

//@Repository
public interface CustomOrderRepository {
    Optional<Order> findByOrderNo(String orderNo);  // 根据订单号查询
    List<Order> findByUserId(Long userId);         // 根据用户ID查询订单
    Page<Order> findAll(Pageable pageable);   // 分页查询所有订单

    @Modifying  // 标记为修改操作
    @Query("UPDATE Order o SET o.status = :status WHERE o.orderNo = :orderNo")
    @Transactional // 事务控制
    int updateOrderStatus(@Param("orderNo") String orderNo,
                          @Param("status") Order.OrderStatus status);
}
