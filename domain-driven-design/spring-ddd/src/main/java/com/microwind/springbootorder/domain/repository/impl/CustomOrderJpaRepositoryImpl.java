package com.microwind.springbootorder.domain.repository.impl;

import com.microwind.springbootorder.domain.repository.CustomOrderJpaRepository;
import com.microwind.springbootorder.domain.order.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * 自定义订单仓储实现（基于JPA）
 * 本实现文件应该放在Infrastructure层以体现依赖倒置，Domain层只有接口
 * 但Spring下@Repository注解默认仅从同一个包内扫描对应同名文件进行加载，故Impl需放在同一个目录下
 * 
 * 优化点：
 * 1. JPQL集中管理，避免硬编码
 * 2. 增强参数校验与防御性编程
 * 3. 补充事务边界控制
 * 4. 添加性能优化提示
 */
@Primary
@Repository
public class CustomOrderJpaRepositoryImpl implements CustomOrderJpaRepository {

    // JPQL常量集中管理
    private static final String FIND_BY_ORDER_NO_JPQL =
            "SELECT o FROM Order o WHERE o.orderNo = :orderNo";
    private static final String FIND_BY_USER_ID_JPQL =
            "SELECT o FROM Order o WHERE o.userId = :userId";
    private static final String FIND_ALL_JPQL =
            "SELECT o FROM Order o";
    private static final String COUNT_ALL_JPQL =
            "SELECT COUNT(o) FROM Order o";
    private static final String UPDATE_STATUS_JPQL =
            "UPDATE Order o SET o.status = :status WHERE o.orderNo = :orderNo";

    // Spring会自动查找对应数据库配置
    @PersistenceContext
    private EntityManager entityManager;

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
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Order> findAllOrders(Pageable pageable) {
        // 分页数据查询
        List<Order> orders = entityManager
                .createQuery(FIND_ALL_JPQL, Order.class)
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
}