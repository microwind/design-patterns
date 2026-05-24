package com.github.microwind.springboot4ddd.infrastructure.repository.order;

import com.github.microwind.springboot4ddd.domain.model.order.Order;
import com.github.microwind.springboot4ddd.domain.page.PageRequest;
import com.github.microwind.springboot4ddd.domain.page.PageResult;
import com.github.microwind.springboot4ddd.domain.repository.order.OrderRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单仓储实现（委托模式）
 * 支持Spring Data JDBC和MyBatis Plus两种数据访问方式的灵活切换
 * 根据配置自动选择相应的实现。实际情况采用一种即可，推荐Spring Data JDBC。
 * 注意：Repository 层不应该管理事务，事务由 Service 层控制
 *
 * @author jarry
 * @since 1.0.0
 */
@Primary
@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderRepository orderRepositoryDelegate;
    private static final String MYBATIS_PLUS = "mybatis-plus";

    public OrderRepositoryImpl(
            @Qualifier("orderJdbcRepositoryImpl") OrderRepository orderJdbcRepositoryImpl,
            @Qualifier("orderMybatisPlusRepositoryImpl") OrderRepository orderMybatisPlusRepositoryImpl,
            @Value("${order.repository.implementation:jdbc}") String implementationType) {

        if (MYBATIS_PLUS.equalsIgnoreCase(implementationType)) {
            this.orderRepositoryDelegate = orderMybatisPlusRepositoryImpl;
        } else {
            this.orderRepositoryDelegate = orderJdbcRepositoryImpl;
        }
    }

    @Override
    public Order save(Order order) {
        return orderRepositoryDelegate.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepositoryDelegate.findById(id);
    }

    @Override
    public Optional<Order> findByOrderNo(String orderNo) {
        return orderRepositoryDelegate.findByOrderNo(orderNo);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepositoryDelegate.findByUserId(userId);
    }

    @Override
    public PageResult<Order> findByUserId(Long userId, PageRequest pageRequest) {
        return orderRepositoryDelegate.findByUserId(userId, pageRequest);
    }

    @Override
    public List<Order> findAllOrders() {
        return orderRepositoryDelegate.findAllOrders();
    }

    @Override
    public PageResult<Order> findAllOrders(PageRequest pageRequest) {
        return orderRepositoryDelegate.findAllOrders(pageRequest);
    }

    @Override
    public void deleteById(Long id) {
        orderRepositoryDelegate.deleteById(id);
    }

    @Override
    public List<Order> findExpiredPendingOrders(LocalDateTime createdBefore) {
        return orderRepositoryDelegate.findExpiredPendingOrders(createdBefore);
    }
}
