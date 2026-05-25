package com.microwind.javaweborder.domain.repository;

import com.microwind.javaweborder.domain.order.CustomerName;
import com.microwind.javaweborder.domain.order.Order;
import com.microwind.javaweborder.domain.order.OrderId;

import java.util.List;

/**
 * 订单聚合仓储接口。
 *
 * <p>DDD 实践：接口定义在领域层，实现位于基础设施层
 * （{@link com.microwind.javaweborder.infrastructure.repository.OrderRepositoryImpl}），
 * 体现<b>依赖倒置原则</b>。
 *
 * <p>仓储里的查询方法应当用<b>领域术语</b>表达
 * （如 {@code findByCustomerName} 而非泛泛的 {@code findByField}）。
 */
public interface OrderRepository extends Repository<Order, OrderId> {

    /**
     * 按客户名称查询订单。
     *
     * @param customerName 客户名称
     * @return 该客户的订单列表，无订单时返回空列表
     */
    List<Order> findByCustomerName(CustomerName customerName);
}
