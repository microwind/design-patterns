package com.github.microwind.springboot4ddd.infrastructure.repository.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.microwind.springboot4ddd.domain.model.order.Order;
import com.github.microwind.springboot4ddd.domain.repository.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单MyBatis Plus仓储实现
 * 实现 OrderRepository 接口，委托给 OrderMybatisPlusMapper mapper
 * 由 MybatisPlusConfig 显式注册为 bean
 *
 * @author jarry
 * @since 1.0.0
 */
@Repository("orderMybatisPlusRepositoryImpl")
public class OrderMybatisPlusRepositoryImpl implements OrderRepository {

    private final OrderMybatisPlusMapper orderMybatisPlusMapper;

    @Autowired
    public OrderMybatisPlusRepositoryImpl(@Lazy OrderMybatisPlusMapper orderMybatisPlusMapper) {
        this.orderMybatisPlusMapper = orderMybatisPlusMapper;
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            orderMybatisPlusMapper.insert(order);
        } else {
            orderMybatisPlusMapper.updateById(order);
        }
        return order;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orderMybatisPlusMapper.selectById(id));
    }

    @Override
    public Optional<Order> findByOrderNo(String orderNo) {
        return orderMybatisPlusMapper.findByOrderNo(orderNo);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderMybatisPlusMapper.findByUserId(userId);
    }

    @Override
    public List<Order> findAllOrders() {
        return orderMybatisPlusMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public void deleteById(Long id) {
        orderMybatisPlusMapper.deleteById(id);
    }

    @Override
    public List<Order> findByStatusAndCreatedAtBefore(String status, LocalDateTime createdAtBefore) {
        return orderMybatisPlusMapper.findByStatusAndCreatedAtBefore(status, createdAtBefore);
    }
}
