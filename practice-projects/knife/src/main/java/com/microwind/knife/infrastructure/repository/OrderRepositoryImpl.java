package com.microwind.knife.infrastructure.repository;

import com.microwind.knife.domain.order.Order;
import com.microwind.knife.domain.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 基础设施层 - 基于 JdbcTemplate 的订单仓储实现
 * 适用场景：需精细控制 SQL 或对性能要求较高的操作
 */
@Repository
@Primary
public class OrderRepositoryImpl implements OrderRepository {

    // 表名及列名常量，便于维护
    private static final String TABLE_ORDERS = "orders";
    private static final String COL_ORDER_ID = "order_id";
    private static final String COL_ORDER_NO = "order_no";
    private static final String COL_USER_ID = "user_id";
    private static final String COL_STATUS = "status";
    private static final String COL_ORDER_NAME = "order_name";
    private static final String COL_AMOUNT = "amount";
    private static final String COL_CREATED_AT = "created_at";
    private static final String COL_UPDATED_AT = "updated_at";

    // 注入Order数据源的JdbcTemplate
    private final JdbcTemplate jdbcTemplate;

    // 显式构造器注入，使用@Qualifier指定orderJdbcTemplate
    public OrderRepositoryImpl(@Qualifier("orderJdbcTemplate") JdbcTemplate jdbcTemplate) {
        System.out.println("initialize OrderRepositoryImpl with orderDataSource: " + jdbcTemplate.getDataSource());
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 根据订单号查询订单（精确匹配）
     * 使用 readOnly 事务优化查询
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findByOrderId(Long orderId) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_ORDERS, COL_ORDER_ID);
        try {
            Order order = jdbcTemplate.queryForObject(sql, orderRowMapper(), orderId);
            return Optional.ofNullable(order);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * 根据订单号查询订单（精确匹配）
     * 使用 readOnly 事务优化查询
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findByOrderNo(String orderNo) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_ORDERS, COL_ORDER_NO);
        try {
            Order order = jdbcTemplate.queryForObject(sql, orderRowMapper(), orderNo);
            return Optional.ofNullable(order);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * 根据用户ID查询订单列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByUserId(Long userId) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_ORDERS, COL_USER_ID);
        return jdbcTemplate.query(sql, orderRowMapper(), userId);
    }

    /**
     * 分页查询所有订单
     * 注意：COUNT(*) 在大数据量时可能较慢，可考虑分页优化策略
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAllOrders(Pageable pageable) {
        // 分页参数提取
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int offset = pageNumber * pageSize;

        String dataSql = String.format(
                "SELECT * FROM %s LIMIT ? OFFSET ?", TABLE_ORDERS
        );
        List<Order> orders = jdbcTemplate.query(dataSql, orderRowMapper(), pageSize, offset);

        String countSql = String.format("SELECT COUNT(*) FROM %s", TABLE_ORDERS);
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class);

        return new PageImpl<>(orders, pageable, total != null ? total : 0);
    }

    /**
     * 更新订单状态
     * 直接使用枚举的 name() 映射到数据库字符串
     */
    @Override
    @Transactional
    public int updateOrderStatus(String orderNo, Order.OrderStatus status) {
        String sql = String.format(
                "UPDATE %s SET %s = ? WHERE %s = ?",
                TABLE_ORDERS, COL_STATUS, COL_ORDER_NO
        );
        return jdbcTemplate.update(sql, status.name(), orderNo);
    }

    /**
     * 保存订单（新增或更新）
     * 注意：假设 orderNo 由业务层保证唯一性
     */
    @Override
    @Transactional
    public Order save(Order order) {
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
            insertOrder(order);
        } else {
            updateOrder(order);
        }
        return order;
    }

    private void insertOrder(Order order) {
        String sql = String.format(
                "INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
                TABLE_ORDERS, COL_USER_ID, COL_STATUS, COL_ORDER_NAME, COL_AMOUNT
        );
        jdbcTemplate.update(sql,
                order.getUserId(),
                order.getStatus().name(),
                order.getOrderName(),
                order.getAmount()
        );
        // 获取自增主键
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        order.setOrderId(id);
    }

    private void updateOrder(Order order) {
        String sql = String.format(
                "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                TABLE_ORDERS, COL_USER_ID, COL_STATUS, COL_ORDER_NAME, COL_AMOUNT, COL_ORDER_NO
        );
        jdbcTemplate.update(sql,
                order.getUserId(),
                order.getStatus().name(),
                order.getOrderName(),
                order.getAmount(),
                order.getOrderNo()
        );
    }

    /**
     * 删除订单（根据 orderNo 存在性检查）
     */
    @Override
    @Transactional
    public void delete(Order order) {
        if (order.getOrderNo() != null && !order.getOrderNo().isEmpty()) {
            String sql = String.format(
                    "DELETE FROM %s WHERE %s = ?",
                    TABLE_ORDERS, COL_ORDER_NO
            );
            jdbcTemplate.update(sql, order.getOrderNo());
        }
    }

    /**
     * 复用 RowMapper 实现（使用 Lambda 简化）
     */
    private RowMapper<Order> orderRowMapper() {
        return (rs, rowNum) -> {
            Order order = new Order();
            order.setOrderId(rs.getLong(COL_ORDER_ID));
            order.setOrderNo(rs.getString(COL_ORDER_NO));
            order.setUserId(rs.getLong(COL_USER_ID));
            String statusStr = rs.getString(COL_STATUS);
            if (statusStr != null) {
                try {
                    order.setStatus(Order.OrderStatus.valueOf(statusStr.trim().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    order.setStatus(Order.OrderStatus.CREATED); // 默认值
                }
            }
            order.setOrderName(rs.getString(COL_ORDER_NAME));
            order.setAmount(rs.getBigDecimal(COL_AMOUNT));
            order.setCreatedAt(rs.getObject(COL_CREATED_AT, LocalDateTime.class));
            order.setUpdatedAt(rs.getObject(COL_UPDATED_AT, LocalDateTime.class));
            return order;
        };
    }
}