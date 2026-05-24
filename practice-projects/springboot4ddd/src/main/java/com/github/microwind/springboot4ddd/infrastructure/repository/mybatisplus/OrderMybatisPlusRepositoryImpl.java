package com.github.microwind.springboot4ddd.infrastructure.repository.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.microwind.springboot4ddd.domain.model.order.Order;
import com.github.microwind.springboot4ddd.domain.page.PageRequest;
import com.github.microwind.springboot4ddd.domain.page.PageResult;
import com.github.microwind.springboot4ddd.domain.repository.order.OrderRepository;
import com.github.microwind.springboot4ddd.infrastructure.repository.order.OrderConverter;
import com.github.microwind.springboot4ddd.infrastructure.repository.order.OrderDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 订单 MyBatis Plus 仓储实现
 *
 * <p>对外实现领域定义的 {@link OrderRepository}（操作 {@code Order} + domain 分页类型），
 * 对内通过 {@link OrderMybatisPlusMapper} 直接读写 {@link OrderDO}。
 *
 * <p>由 {@code MybatisPlusConfig} 显式注册为 bean。
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
        OrderDO orderDO = OrderConverter.toDO(order);
        if (orderDO.getId() == null) {
            orderMybatisPlusMapper.insert(orderDO);
            if (orderDO.getId() != null) {
                order.markCreated(orderDO.getId());
            }
        } else {
            orderMybatisPlusMapper.updateById(orderDO);
        }
        return order;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orderMybatisPlusMapper.selectById(id))
                .map(OrderConverter::toModel);
    }

    @Override
    public Optional<Order> findByOrderNo(String orderNo) {
        return orderMybatisPlusMapper.findByOrderNo(orderNo).map(OrderConverter::toModel);
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return OrderConverter.toModelList(orderMybatisPlusMapper.findByUserId(userId));
    }

    @Override
    public PageResult<Order> findByUserId(Long userId, PageRequest pageRequest) {
        long offset = pageRequest.getOffset();
        int limit = pageRequest.getPageSize();

        // FIXME 已知问题：先查全部再内存切片，需要替换为基于 SQL 的分页（参考 selectPageData）
        List<OrderDO> allOrders = orderMybatisPlusMapper.findByUserId(userId);
        long total = allOrders.size();

        List<OrderDO> records = new ArrayList<>();
        if (!allOrders.isEmpty() && offset < total) {
            int endIndex = Math.min((int) (offset + limit), (int) total);
            records = allOrders.subList((int) offset, endIndex);
        }
        return new PageResult<>(
                OrderConverter.toModelList(records),
                total,
                pageRequest.getPageNumber(),
                pageRequest.getPageSize()
        );
    }

    @Override
    public List<Order> findAllOrders() {
        return OrderConverter.toModelList(orderMybatisPlusMapper.selectList(new QueryWrapper<>()));
    }

    @Override
    public PageResult<Order> findAllOrders(PageRequest pageRequest) {
        List<OrderDO> records = orderMybatisPlusMapper.selectPageData(pageRequest.getOffset(), pageRequest.getPageSize());
        long total = orderMybatisPlusMapper.countAll();
        return new PageResult<>(
                OrderConverter.toModelList(records),
                total,
                pageRequest.getPageNumber(),
                pageRequest.getPageSize()
        );
    }

    @Override
    public void deleteById(Long id) {
        orderMybatisPlusMapper.deleteById(id);
    }

    @Override
    public List<Order> findExpiredPendingOrders(LocalDateTime createdBefore) {
        return OrderConverter.toModelList(
                orderMybatisPlusMapper.findByStatusAndCreatedAtBefore(
                        Order.OrderStatus.PENDING.name(), createdBefore));
    }
}
