package com.github.microwind.springboot4ddd.domain.repository.order;

import com.github.microwind.springboot4ddd.domain.model.order.Order;
import com.github.microwind.springboot4ddd.domain.page.PageRequest;
import com.github.microwind.springboot4ddd.domain.page.PageResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单仓储接口
 *
 * <p>零框架依赖：分页相关类型使用 domain 自定义的 {@link PageRequest} / {@link PageResult}。
 *
 * @author jarry
 * @since 1.0.0
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    Optional<Order> findByOrderNo(String orderNo);

    List<Order> findByUserId(Long userId);

    PageResult<Order> findByUserId(Long userId, PageRequest pageRequest);

    List<Order> findAllOrders();

    PageResult<Order> findAllOrders(PageRequest pageRequest);

    void deleteById(Long id);

    /**
     * 查询超时未支付订单（创建时间早于阈值且状态为 PENDING）。
     */
    List<Order> findExpiredPendingOrders(LocalDateTime createdBefore);
}
