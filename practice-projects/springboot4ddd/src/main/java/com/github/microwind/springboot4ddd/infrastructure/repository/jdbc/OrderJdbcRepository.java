package com.github.microwind.springboot4ddd.infrastructure.repository.jdbc;

import com.github.microwind.springboot4ddd.domain.model.order.Order;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 订单Spring Data JDBC仓储
 *
 * @author jarry
 * @since 1.0.0
 */
@Repository
public interface OrderJdbcRepository extends CrudRepository<Order, Long> {

    /**
     * 根据订单号查找
     */
    @Query("SELECT * FROM orders WHERE order_no = :orderNo")
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 根据用户ID查找所有订单
     */
    @Query("SELECT * FROM orders WHERE user_id = :userId ORDER BY created_at DESC")
    List<Order> findByUserId(Long userId);
}
