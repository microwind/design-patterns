package com.microwind.springbootorder.repository.order;

import com.microwind.springbootorder.models.order.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomOrderRepositoryImpl implements CustomOrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Order> findByOrderNo(String orderNo) {
        String query = "SELECT o FROM Order o WHERE o.orderNo = :orderNo";
        List<Order> orders = entityManager.createQuery(query, Order.class)
                .setParameter("orderNo", orderNo)
                .getResultList();
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        String query = "SELECT o FROM Order o WHERE o.userId = :userId";
        return entityManager.createQuery(query, Order.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public Page<Order> findAll(PageRequest pageRequest) {
        String query = "SELECT o FROM Order o";
        List<Order> orders = entityManager.createQuery(query, Order.class)
                .setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize())
                .setMaxResults(pageRequest.getPageSize())
                .getResultList();
        // PageImpl 可以包装成 Page 类型
        return new org.springframework.data.domain.PageImpl<>(orders, pageRequest, orders.size());
    }

    @Override
    @Transactional
    public int updateOrderStatus(String orderNo, Order.OrderStatus status) {
        return entityManager.createQuery(
                        "UPDATE Order o SET o.status = :status WHERE o.orderNo = :orderNo")
                .setParameter("status", status)
                .setParameter("orderNo", orderNo)
                .executeUpdate();
    }
}
