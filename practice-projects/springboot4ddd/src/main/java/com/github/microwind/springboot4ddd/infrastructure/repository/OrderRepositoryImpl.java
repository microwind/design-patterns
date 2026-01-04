package com.github.microwind.springboot4ddd.infrastructure.repository;

import com.github.microwind.springboot4ddd.domain.model.Order;
import com.github.microwind.springboot4ddd.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * 订单仓储实现（适配器模式）
 *
 * @author jarry
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJdbcRepository orderJdbcRepository;

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
    public List<Order> findAll() {
        return StreamSupport.stream(orderJdbcRepository.findAll().spliterator(), false)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        orderJdbcRepository.deleteById(id);
    }
}
