// 基础设施层（Infrastructure）：订单仓储实现
package com.javaweborder.infrastructure.repository;

import com.javaweborder.domain.order.Order;
import com.javaweborder.domain.order.OrderRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OrderRepositoryImpl implements OrderRepository {
    
    private final Map<Integer, Order> orders = new ConcurrentHashMap<>(); // 线程安全的 HashMap

    @Override
    public void save(Order order) {
        orders.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(int id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public void delete(int id) {
        if (!orders.containsKey(id)) {
            throw new NoSuchElementException("订单 ID " + id + " 不存在，无法删除");
        }
        orders.remove(id);
    }

    @Override
    public List<Order> findByCustomerName(String customerName) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getCustomerName().equals(customerName)) {
                result.add(order);
            }
        }
        if (result.isEmpty()) {
            throw new NoSuchElementException("没有找到客户名称为 " + customerName + " 的订单");
        }
        return result;
    }
}
