package com.microwind.springbootorder.domain.repository;

import com.microwind.springbootorder.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

// Order仓库接口文件，定义在Domain层，具体实现可以有多种方式，放在Infrastructure，体现依赖倒置
// JdbcTemplate：优点是灵活、可控性强、缺点是ORM 性能损耗，但代码冗余、需手动处理对象映射和事务。
// JPA Repository：有点是开发快、减少样板代码、内置缓存与延迟加载，缺点是规则复杂、SQL优化困难，有潜在性能陷阱。
// OrderJpaRepository和OrderRepository任选其一即可，在服务层按需指定即可。JdbcTemplate性能更好，Jpa则代码更加简单。
public interface OrderRepository {
    Optional<Order> findByOrderNo(String orderNo);  // 根据订单号查询
    List<Order> findByUserId(Long userId);         // 根据用户ID查询订单
    Page<Order> findAllOrders(Pageable pageable);  // 分页查询所有订单
    int updateOrderStatus(String orderNo, Order.OrderStatus status); // 更新订单状态
    Order save(Order order);  // 保存订单（插入或更新）
    void delete(Order order);  // 删除订单
}