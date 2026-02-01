package com.github.microwind.springboot4ddd.domain.repository.order;

import com.github.microwind.springboot4ddd.domain.model.order.Order;

import java.util.List;
import java.util.Optional;

/**
 * 订单仓储接口
 *
 * @author jarry
 * @since 1.0.0
 */
public interface OrderRepository {

    /**
     * 保存订单
     */
    Order save(Order order);

    /**
     * 根据ID查找订单
     */
    Optional<Order> findById(Long id);

    /**
     * 根据订单号查找订单
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 查找用户的所有订单
     */
    List<Order> findByUserId(Long userId);

    /**
     * 查找所有订单
     */
    List<Order> findAllOrders();

    /**
     * 删除订单
     */
    void deleteById(Long id);

    /**
     * 根据订单状态和创建时间查找订单
     * @param status 订单状态
     * @param createdAtBefore 创建时间在此时间点之前的订单
     * @return 符合条件的订单列表
     */
    List<Order> findByStatusAndCreatedAtBefore(String status, java.time.LocalDateTime createdAtBefore);
}
