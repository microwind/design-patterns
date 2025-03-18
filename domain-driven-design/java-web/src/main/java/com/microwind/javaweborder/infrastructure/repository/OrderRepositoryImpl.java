// 基础设施层（Infrastructure）：订单仓储实现
package com.microwind.javaweborder.infrastructure.repository;

import com.microwind.javaweborder.domain.order.Order;
import com.microwind.javaweborder.domain.repository.OrderRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OrderRepositoryImpl implements OrderRepository {

    // 此处拿Map示意，实际会存储到数据库中。
    private final static Map<Long, Order> orders = new ConcurrentHashMap<>(); // 线程安全的 HashMap

    @Override
    public void save(Order order) {
        orders.put(order.getId(), order);
    }

    @Override
    public Optional<Order> findById(long id) {
        Order order = orders.get(id);
        if (order == null) {
            throw new NoSuchElementException("订单 ID " + id + " 未找到");
        }
        System.out.println("查询ID: " + id + ", 结果: " + order.getId());
        return Optional.of(order);
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public void delete(long id) {
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
