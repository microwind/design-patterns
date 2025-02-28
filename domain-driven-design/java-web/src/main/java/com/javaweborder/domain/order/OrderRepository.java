// 领域层(Domain)：订单数据仓库接口
package com.javaweborder.domain.order;

import com.javaweborder.infrastructure.repository.Repository;

import java.util.List;
import java.util.Optional;

// OrderRepository 订单仓储接口，继承通用 Repository<Order>
public interface OrderRepository extends Repository<Order> {
    
    // 保存订单
    boolean save(Order order);

    // 根据ID查找订单
    Optional<Order> findById(int id);

    // 查找所有订单
    List<Order> findAll();

    // 删除订单
    void delete(int id);

    // 根据客户名称查找订单
    List<Order> findByCustomerName(String customerName);
}
