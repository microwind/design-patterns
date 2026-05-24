package com.github.microwind.springboot4ddd.infrastructure.repository.jdbc;

import com.github.microwind.springboot4ddd.domain.model.order.Order;
import com.github.microwind.springboot4ddd.domain.page.PageRequest;
import com.github.microwind.springboot4ddd.domain.page.PageResult;
import com.github.microwind.springboot4ddd.domain.repository.order.OrderRepository;
import com.github.microwind.springboot4ddd.infrastructure.page.PageMapper;
import com.github.microwind.springboot4ddd.infrastructure.repository.order.OrderConverter;
import com.github.microwind.springboot4ddd.infrastructure.repository.order.OrderDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 订单 Spring Data JDBC 仓储实现
 *
 * <p>对外实现领域定义的 {@link OrderRepository}（操作 {@code Order} + domain 分页类型），
 * 对内委托 {@link OrderJdbcRepository}（操作 {@link OrderDO} + Spring Data Pageable），
 * 通过 {@link OrderConverter} 与 {@link PageMapper} 在边界做显式转换。
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
        OrderDO saved = orderJdbcRepository.save(OrderConverter.toDO(order));
        if (order.getId() == null && saved.getId() != null) {
            order.markCreated(saved.getId());
        }
        return order;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderJdbcRepository.findById(id).map(OrderConverter::toModel);
    }

    @Override
    public Optional<Order> findByOrderNo(String orderNo) {
        return orderJdbcRepository.findByOrderNo(orderNo).map(OrderConverter::toModel);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return OrderConverter.toModelList(orderJdbcRepository.findByUserId(userId));
    }

    @Override
    public PageResult<Order> findByUserId(Long userId, PageRequest pageRequest) {
        Pageable pageable = PageMapper.toSpring(pageRequest);
        Page<OrderDO> page = orderJdbcRepository.findByUserId(userId, pageable);
        return new PageResult<>(
                OrderConverter.toModelList(page.getContent()),
                page.getTotalElements(),
                pageRequest.getPageNumber(),
                pageRequest.getPageSize()
        );
    }

    @Override
    public List<Order> findAllOrders() {
        List<OrderDO> all = StreamSupport.stream(orderJdbcRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        return OrderConverter.toModelList(all);
    }

    @Override
    public PageResult<Order> findAllOrders(PageRequest pageRequest) {
        Pageable pageable = PageMapper.toSpring(pageRequest);
        Page<OrderDO> page = orderJdbcRepository.findAll(pageable);
        return new PageResult<>(
                OrderConverter.toModelList(page.getContent()),
                page.getTotalElements(),
                pageRequest.getPageNumber(),
                pageRequest.getPageSize()
        );
    }

    @Override
    public void deleteById(Long id) {
        orderJdbcRepository.deleteById(id);
    }

    @Override
    public List<Order> findExpiredPendingOrders(LocalDateTime createdBefore) {
        return OrderConverter.toModelList(
                orderJdbcRepository.findByStatusAndCreatedAtBefore(
                        Order.OrderStatus.PENDING.name(), createdBefore));
    }
}
