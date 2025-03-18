// 领域层(Domain)：订单数据仓库接口
package com.microwind.javaweborder.domain.repository;

import java.util.List;
import java.util.Optional;

import com.microwind.javaweborder.domain.order.Order;

// OrderRepository 订单仓储接口，继承通用 Repository<Order>
public interface OrderRepository extends Repository<Order> {
    
    // 保存订单
    void save(Order order);

    // 根据ID查找订单
    Optional<Order> findById(long id);

    // 查找所有订单
    List<Order> findAll();

    // 删除订单
    void delete(long id);

    // 根据客户名称查找订单
    List<Order> findByCustomerName(String customerName);
}
