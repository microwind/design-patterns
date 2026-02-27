package com.github.microwind.springboot4ddd.infrastructure.repository.jdbc;

import com.github.microwind.springboot4ddd.domain.model.order.Order;
import com.github.microwind.springboot4ddd.domain.repository.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        // 转换页码：Spring 分页 page 从 1 开始，需要转换为 0-indexed
        Pageable adjustedPageable = pageable.withPage(pageable.getPageNumber() - 1);
        return orderJdbcRepository.findByUserId(userId, adjustedPageable);
    }

    @Override
    public List<Order> findAllOrders() {
        return StreamSupport.stream(orderJdbcRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Order> findAllOrders(Pageable pageable) {
        // 转换页码：Spring 分页 page 从 1 开始，需要转换为 0-indexed
        Pageable adjustedPageable = pageable.withPage(pageable.getPageNumber() - 1);
        return orderJdbcRepository.findAll(adjustedPageable);
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
