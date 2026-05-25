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

/**
 * 订单仓储内存实现。
 *
 * <p>DDD 实践：领域层定义 {@link OrderRepository} 抽象，本类位于
 * <b>基础设施层</b>，提供具体的存储实现。这就是依赖倒置原则的落地：
 * 领域代码不直接依赖任何存储技术。
 *
 * <p>教学示例：用 {@link ConcurrentHashMap} 模拟一个数据库。
 * 真实项目应替换为 JPA / MyBatis / JDBC 等持久化实现。
 *
 * <h3>仓储语义要点</h3>
 * <ul>
 *   <li>{@code findById} 未命中返回 {@link Optional#empty()}，不抛异常</li>
 *   <li>抛不抛异常由应用层决定（领域层异常如 OrderNotFoundException）</li>
 * </ul>
 */
public class OrderRepositoryImpl implements OrderRepository {

    /** 内存存储，线程安全 Map 模拟数据库。 */
    private final static Map<Long, Order> orders = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        orders.put(order.getId().value(), order);
    }

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(orders.get(id.value()));
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
