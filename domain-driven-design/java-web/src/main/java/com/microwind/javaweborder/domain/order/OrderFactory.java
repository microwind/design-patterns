// 领域层(Domain) - 聚合工厂：OrderFactory
//
// 工厂（Factory）是 DDD 战术模式之一，用于封装聚合"出生"过程中
// 复杂的、不属于聚合根日常职责的创建逻辑，例如：
// - ID 生成策略（雪花、UUID、时间戳）
// - 多个值对象的组装
// - 创建时的不变量检查
// - 记录"创建"领域事件
//
// 把上述步骤集中在工厂里，让聚合根的构造器保持"单纯"，
// 也避免应用层直接调用 new Order(...) 把创建语义打散到各处。
package com.microwind.javaweborder.domain.order;

public class OrderFactory {

    // 新建订单：分配 ID + 装配聚合 + 记录创建事件
    public Order create(CustomerName customerName, Money amount) {
        OrderId id = OrderId.generate();
        Order order = new Order(id, customerName, amount);
        order.recordCreated();
        return order;
    }
}
