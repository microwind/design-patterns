// 领域层(Domain)：订单数据仓库接口
package com.javaweborder.domain.order;

import java.util.List;
import java.util.Optional;

// OrderRepository 订单仓储接口，定义对订单数据的操作
public interface OrderRepository {
    
    // 保存订单
    void save(Order order) throws Exception;

    // 根据ID查找订单
    Optional<Order> findById(int id) throws Exception;

    // 查找所有订单
    List<Order> findAll() throws Exception;

    // 删除订单
    void delete(int id) throws Exception;

    // 根据客户名称查找订单
    List<Order> findByCustomerName(String customerName) throws Exception;
}
