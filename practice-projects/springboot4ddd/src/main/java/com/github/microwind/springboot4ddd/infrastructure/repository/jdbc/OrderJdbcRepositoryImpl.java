package com.github.microwind.springboot4ddd.infrastructure.repository.jdbc;

import com.github.microwind.springboot4ddd.domain.model.order.Order;
import com.github.microwind.springboot4ddd.domain.repository.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 订单Spring Data JDBC仓储实现
 * 实现 OrderRepository 接口，委托给 OrderJdbcRepository mapper
 *
 * @author jarry
 * @since 1.0.0
 */
@Repository("orderJdbcRepositoryImpl")
public class OrderJdbcRepositoryImpl implements OrderRepository {

    private final OrderJdbcRepository orderJdbcRepository;

    @Autowired
    public OrderJdbcRepositoryImpl(@Lazy OrderJdbcRepository orderJdbcRepository) {
        this.orderJdbcRepository = orderJdbcRepository;
    }

    @Override
    public Order save(Order order) {
        return orderJdbcRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJdbcRepository.findById(id);
    }

    @Override
    public Optional<Order> findByOrderNo(String orderNo) {
        return orderJdbcRepository.findByOrderNo(orderNo);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderJdbcRepository.findByUserId(userId);
    }

    @Override
    public List<Order> findAllOrders() {
        return StreamSupport.stream(orderJdbcRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        orderJdbcRepository.deleteById(id);
    }

    @Override
    public List<Order> findByStatusAndCreatedAtBefore(String status, LocalDateTime createdAtBefore) {
        return orderJdbcRepository.findByStatusAndCreatedAtBefore(status, createdAtBefore);
    }
}
