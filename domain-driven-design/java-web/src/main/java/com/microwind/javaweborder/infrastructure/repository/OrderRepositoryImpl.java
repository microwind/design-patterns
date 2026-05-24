// 基础设施层(Infrastructure) - 订单仓储内存实现
//
// 教学示例：用 ConcurrentHashMap 模拟一个数据库。
// 真实项目应替换为 JPA / MyBatis / JDBC 等持久化实现。
//
// 一个原则要点：仓储里的方法语义是"集合化"的：
// - findById 未命中应返回 Optional.empty()，而非抛异常
// - 抛不抛异常应由应用层（用例编排者）来决定
package com.microwind.javaweborder.infrastructure.repository;

import com.microwind.javaweborder.domain.order.CustomerName;
import com.microwind.javaweborder.domain.order.Order;
import com.microwind.javaweborder.domain.order.OrderId;
import com.microwind.javaweborder.domain.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class OrderRepositoryImpl implements OrderRepository {

    // 此处用 Map 示意，实际会存储到数据库中
    private final static Map<Long, Order> orders = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        orders.put(order.getId().value(), order);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        Order order = orders.get(id.value());
        return Optional.ofNullable(order);
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public void delete(OrderId id) {
        orders.remove(id.value());
    }

    @Override
    public List<Order> findByCustomerName(CustomerName customerName) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getCustomerName().equals(customerName)) {
                result.add(order);
            }
        }
        return result;
    }
}
