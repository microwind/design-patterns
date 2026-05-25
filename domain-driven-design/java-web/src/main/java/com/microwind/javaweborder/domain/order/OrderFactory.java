package com.microwind.javaweborder.domain.order;

/**
 * 订单聚合工厂。
 *
 * <p>DDD 战术构件：<b>工厂（Factory）</b>。封装聚合"出生"过程中
 * 复杂的、不属于聚合根日常职责的创建逻辑：
 * <ul>
 *   <li>ID 生成策略（时间戳 / 雪花 / UUID）</li>
 *   <li>多个值对象的装配</li>
 *   <li>创建时的不变量校验</li>
 *   <li>记录初始领域事件 {@link com.microwind.javaweborder.domain.event.OrderCreatedEvent}</li>
 * </ul>
 *
 * <p>把这些步骤集中在工厂里，让聚合根的构造器保持单纯，
 * 也避免应用层直接 {@code new Order(...)} 把创建语义打散到各处。
 */
public class OrderFactory {

    /**
     * 创建一个新订单：分配 ID、装配聚合、记录创建事件。
     *
     * @param customerName 客户名称
     * @param amount       订单金额（可能已被领域服务应用过折扣）
     * @return 新建的订单聚合根，内部已累积一条 OrderCreatedEvent
     */
    public Order create(CustomerName customerName, Money amount) {
        OrderId id = OrderId.generate();
        Order order = new Order(id, customerName, amount);
        order.recordCreated();
        return order;
    }
}
