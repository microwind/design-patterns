package com.github.microwind.springboot4ddd.infrastructure.repository.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.microwind.springboot4ddd.domain.model.order.Order;
import com.github.microwind.springboot4ddd.domain.repository.order.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
//@Repository("orderMybatisPlusRepositoryImpl")
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
    public Page<Order> findByUserId(Long userId, Pageable pageable) {
        // 计算分页参数（page 从 1 开始）
        long offset = (long) (pageable.getPageNumber() - 1) * pageable.getPageSize();
        int limit = pageable.getPageSize();

        // 查询指定用户的所有订单（用于计算总数）
        List<Order> allOrders = orderMybatisPlusMapper.findByUserId(userId);
        long total = allOrders.size();

        // 在内存中分页（因为 selectPage 无法与自定义 SQL 的 QueryWrapper 配合工作）
        List<Order> records = new ArrayList<>();
        if (!allOrders.isEmpty() && offset < total) {
            int endIndex = Math.min((int) (offset + limit), (int) total);
            records = allOrders.subList((int) offset, endIndex);
        }

        return new PageImpl<>(records, pageable, total);
    }

    @Override
    public List<Order> findAllOrders() {
        return orderMybatisPlusMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public Page<Order> findAllOrders(Pageable pageable) {
        // 计算分页参数（page 从 1 开始）
        long offset = (long) (pageable.getPageNumber() - 1) * pageable.getPageSize();
        int limit = pageable.getPageSize();

        // 使用 Mapper 的分页查询方法
        List<Order> records = orderMybatisPlusMapper.selectPageData(offset, limit);

        // 获取总数
        long total = orderMybatisPlusMapper.countAll();

        return new PageImpl<>(records, pageable, total);
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
