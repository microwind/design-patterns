// 领域层(Domain) - 订单仓储接口
//
// OrderRepository 是订单聚合的仓储接口，定义在领域层。
// 其实现 OrderRepositoryImpl 位于基础设施层（依赖倒置原则）。
//
// 仓储以"聚合根 Order"为单位，不暴露 OrderItem 等子实体的查询。
package com.microwind.javaweborder.domain.repository;

import com.microwind.javaweborder.domain.order.CustomerName;
import com.microwind.javaweborder.domain.order.Order;
import com.microwind.javaweborder.domain.order.OrderId;

import java.util.List;

public interface OrderRepository extends Repository<Order, OrderId> {

    // 业务查询：根据客户名称查询订单
    //
    // 仓储里"领域语义"的查询应当用业务术语表达，而不是泛泛的 findBy 字段。
    List<Order> findByCustomerName(CustomerName customerName);
}
