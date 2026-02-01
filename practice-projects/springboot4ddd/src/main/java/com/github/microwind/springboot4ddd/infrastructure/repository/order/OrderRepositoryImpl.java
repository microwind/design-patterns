package com.github.microwind.springboot4ddd.infrastructure.repository.order;

import com.github.microwind.springboot4ddd.domain.model.order.Order;
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
 * 根据配置自动选择 JDBC 或 MyBatis Plus 实现
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

    /**
     * 构造函数注入，根据配置选择具体实现
     *
     * @param orderJdbcRepositoryImpl JDBC实现
     * @param orderMybatisPlusRepositoryImpl MyBatis Plus实现
     * @param implementationType 配置的实现类型
     */
    public OrderRepositoryImpl(
            @Qualifier("orderJdbcRepositoryImpl") OrderRepository orderJdbcRepositoryImpl,
            @Qualifier("orderMybatisPlusRepositoryImpl") OrderRepository orderMybatisPlusRepositoryImpl,
            @Value("${order.repository.implementation:jdbc}") String implementationType) {
        
        // 根据配置选择委托对象
        this.orderRepositoryDelegate = MYBATIS_PLUS.equalsIgnoreCase(implementationType)
                ? orderMybatisPlusRepositoryImpl 
                : orderJdbcRepositoryImpl;
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
    public List<Order> findAllOrders() {
        return orderRepositoryDelegate.findAllOrders();
    }

    @Override
    public void deleteById(Long id) {
        orderRepositoryDelegate.deleteById(id);
    }

    @Override
    public List<Order> findByStatusAndCreatedAtBefore(String status, LocalDateTime createdAtBefore) {
        return orderRepositoryDelegate.findByStatusAndCreatedAtBefore(status, createdAtBefore);
    }
}
