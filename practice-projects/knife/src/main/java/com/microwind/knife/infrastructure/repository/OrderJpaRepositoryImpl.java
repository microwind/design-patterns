package com.microwind.knife.infrastructure.repository;

import com.microwind.knife.domain.repository.order.CustomOrderJpaRepository;
import com.microwind.knife.domain.order.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 自定义订单仓储实现（基于JPA）
 * Infrastructure层实现，依赖Domain层接口，符合DIP原则
 * Spring Data Jpa模式，代码更加简单，数据可持久化
 *
 * 优化点：
 * 1. JPQL集中管理，避免硬编码
 * 2. 增强参数校验与防御性编程
 * 3. 补充事务边界控制
 * 4. 添加性能优化提示
 */
@Repository("jpaRepository")
@Primary
public class OrderJpaRepositoryImpl implements CustomOrderJpaRepository {

    // JPQL常量集中管理
    private static final String FIND_BY_ORDER_ID_JPQL =
            "SELECT o FROM Order o WHERE o.orderId = :orderId";
    private static final String FIND_BY_ORDER_NO_JPQL =
            "SELECT o FROM Order o WHERE o.orderNo = :orderNo";
    private static final String FIND_BY_ORDER_NO_WITH_ITEMS_JPQL =
            "SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.orderNo = :orderNo";
    private static final String FIND_BY_USER_ID_JPQL =
            "SELECT o FROM Order o WHERE o.userId = :userId";
    private static final String FIND_ALL_JPQL =
            "SELECT o FROM Order o";
    private static final String FIND_ALL_WITH_ITEMS_JPQL =
            "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items";
    private static final String COUNT_ALL_JPQL =
            "SELECT COUNT(o) FROM Order o";
    private static final String UPDATE_STATUS_JPQL =
            "UPDATE Order o SET o.status = :status WHERE o.orderNo = :orderNo";

    // Spring会自动查找对应数据库配置
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 根据订单ID精确查询（使用只读事务优化）
     * @throws IllegalArgumentException 如果orderId为空
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findByOrderId(Long orderId) {
        Assert.notNull(orderId, "Order ID must not be null");

        try {
            return Optional.of(entityManager
                    .createQuery(FIND_BY_ORDER_ID_JPQL, Order.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * 根据订单号精确查询（使用只读事务优化）
     * @throws IllegalArgumentException 如果orderNo为空
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findByOrderNo(String orderNo) {
        Assert.hasText(orderNo, "Order number must not be empty");

        try {
            return Optional.of(entityManager
                    .createQuery(FIND_BY_ORDER_NO_JPQL, Order.class)
                    .setParameter("orderNo", orderNo)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * 根据订单号精确查询（包含订单项，使用LEFT JOIN FETCH避免N+1问题）
     * @throws IllegalArgumentException 如果orderNo为空
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> findByOrderNoWithItems(String orderNo) {
        Assert.hasText(orderNo, "Order number must not be empty");

        try {
            return Optional.of(entityManager
                    .createQuery(FIND_BY_ORDER_NO_WITH_ITEMS_JPQL, Order.class)
                    .setParameter("orderNo", orderNo)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * 根据用户ID查询订单（建议对user_id字段建立索引）
     */
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByUserId(Long userId) {
        Assert.notNull(userId, "User ID must not be null");

        return entityManager
                .createQuery(FIND_BY_USER_ID_JPQL, Order.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    /**
     * 分页查询订单（大数据量时COUNT查询可能较慢）
     * 支持动态排序
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAllOrders(Pageable pageable) {
        // 构建带排序的查询
        String jpql = FIND_ALL_JPQL + buildOrderByClause(pageable.getSort());

        // 分页数据查询
        TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);
        List<Order> orders = query
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // 总数查询（可考虑缓存优化）
        Long total = entityManager
                .createQuery(COUNT_ALL_JPQL, Long.class)
                .getSingleResult();

        return new PageImpl<>(orders, pageable, total != null ? total : 0L);
    }

    /**
     * 分页查询订单（包含订单项，使用LEFT JOIN FETCH避免N+1问题）
     * 注意：使用DISTINCT避免因JOIN导致的重复记录
     * 支持动态排序
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAllOrdersWithItems(Pageable pageable) {
        // 构建带排序的查询
        String jpql = FIND_ALL_WITH_ITEMS_JPQL + buildOrderByClause(pageable.getSort());

        // 分页数据查询（包含订单项）
        TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class);
        List<Order> orders = query
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // 总数查询（可考虑缓存优化）
        Long total = entityManager
                .createQuery(COUNT_ALL_JPQL, Long.class)
                .getSingleResult();

        return new PageImpl<>(orders, pageable, total != null ? total : 0L);
    }

    /**
     * 构建 ORDER BY 子句
     * @param sort 排序对象
     * @return ORDER BY 子句字符串，如果没有排序则返回空字符串
     */
    private String buildOrderByClause(Sort sort) {
        if (sort.isUnsorted()) {
            return "";
        }

        String orderByClause = sort.stream()
                .map(order -> "o." + order.getProperty() + " " + order.getDirection().name())
                .collect(Collectors.joining(", "));

        return " ORDER BY " + orderByClause;
    }

    /**
     * 更新订单状态（自动清除一级缓存保证数据一致性）
     */
    @Override
    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    public int updateOrderStatus(String orderNo, Order.OrderStatus status) {
        Assert.hasText(orderNo, "Order number must not be empty");
        Assert.notNull(status, "Order status must not be null");

        return entityManager
                .createQuery(UPDATE_STATUS_JPQL)
                .setParameter("status", status)
                .setParameter("orderNo", orderNo)
                .executeUpdate();
    }

    /**
     * 分页查询所有订单（直接调用 findAllOrders）
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAll(Pageable pageable) {
        return findAllOrders(pageable);
    }

    /**
     * 保存订单（新增或更新）
     */
    @Override
    @Transactional
    public Order save(Order order) {
        Assert.notNull(order, "Order must not be null");

        if (order.getOrderId() == null) {
            // 新增
            entityManager.persist(order);
            return order;
        } else {
            // 更新
            return entityManager.merge(order);
        }
    }

    /**
     * 删除订单
     */
    @Override
    @Transactional
    public void delete(Order order) {
        Assert.notNull(order, "Order must not be null");

        if (entityManager.contains(order)) {
            entityManager.remove(order);
        } else {
            // 如果订单不在持久化上下文中，先查找再删除
            Order managedOrder = entityManager.find(Order.class, order.getOrderId());
            if (managedOrder != null) {
                entityManager.remove(managedOrder);
            }
        }
    }
}
