package com.github.microwind.springboot4ddd.infrastructure.repository.order;

import com.github.microwind.springboot4ddd.domain.model.order.Order;
import com.github.microwind.springboot4ddd.domain.repository.order.OrderRepository;
import com.github.microwind.springboot4ddd.infrastructure.repository.jdbc.OrderJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 订单仓储实现（适配器模式）
 * 注意：Repository 层不应该管理事务，事务由 Service 层控制
 *
 * @author jarry
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    // 使用Spring Data JDBC实现数据操作
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
//        return StreamSupport.stream(orderJdbcRepository.findAll().spliterator(), false)
//                .toList();
        return StreamSupport.stream(orderJdbcRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        orderJdbcRepository.deleteById(id);
    }
}
