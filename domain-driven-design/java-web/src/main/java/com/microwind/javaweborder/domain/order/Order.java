// 领域层(Domain) - 聚合根：订单 Order
//
// 聚合根是 DDD 的核心构件：
// - 对外的唯一入口，外部对象只能持有聚合根，不能直接持有子实体/值对象
// - 负责维护聚合内部不变量；任何状态变更都必须经由聚合根的业务方法
// - 仓储以聚合根为单位，不存储孤立的子实体
//
// 充血模型：业务规则挂在聚合根上（如 cancel / update），而非散落在 Service。
// 状态变更会累积领域事件，由应用层在事务边界外统一发布。
package com.microwind.javaweborder.domain.order;

import com.microwind.javaweborder.domain.event.DomainEvent;
import com.microwind.javaweborder.domain.event.OrderCanceledEvent;
import com.microwind.javaweborder.domain.event.OrderCreatedEvent;
import com.microwind.javaweborder.domain.event.OrderUpdatedEvent;
import com.microwind.javaweborder.domain.exception.InvalidOrderInputException;
import com.microwind.javaweborder.domain.exception.InvalidOrderStateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {

    private final OrderId id;                  // 订单 ID（值对象，不可变）
    private CustomerName customerName;          // 客户名称（值对象）
    private Money amount;                       // 订单金额（值对象）
    private OrderStatus status;                 // 订单状态
    private final List<OrderItem> items;        // 订单项列表（聚合内子实体）
    private final List<DomainEvent> domainEvents; // 已累积但尚未发布的领域事件

    // 包级私有构造器：限制外部直接 new，强制走 OrderFactory
    // 保证创建流程的一致性（工厂负责生成 ID、记录创建事件）
    Order(OrderId id, CustomerName customerName, Money amount) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
        this.status = OrderStatus.CREATED;
        this.items = new ArrayList<>();
        this.domainEvents = new ArrayList<>();
    }

    // 从仓储还原已有订单：不发出 OrderCreated 事件
    // "新建" 和 "还原" 是两种不同的出生路径，必须区分
    public static Order restore(OrderId id, CustomerName customerName, Money amount,
                                OrderStatus status, List<OrderItem> items) {
        Order order = new Order(id, customerName, amount);
        order.status = status;
        if (items != null) {
            order.items.addAll(items);
        }
        return order;
    }

    // 仅供 OrderFactory 调用：发布订单创建事件
    void recordCreated() {
        domainEvents.add(new OrderCreatedEvent(id, customerName, amount));
    }

    // === 业务方法（充血模型核心）===

    public void addItem(OrderItem item) {
        if (item == null) {
            throw new InvalidOrderInputException("订单项不能为 null");
        }
        this.items.add(item);
    }

    public void removeItem(OrderItem item) {
        this.items.remove(item);
    }

    public void recalculateAmount() {
        Money sum = Money.ZERO;
        for (OrderItem item : items) {
            sum = sum.add(item.subtotal());
        }
        this.amount = sum;
    }

    // 业务规则：仅 CREATED 状态可取消，重复取消视为错误
    // 贫血模型常把这个规则写在 Service 里、还经常用 println 报错；
    // 充血模型把规则关进聚合根，错误条件直接抛领域异常
    public void cancel() {
        if (!status.canTransitionTo(OrderStatus.CANCELED)) {
            throw new InvalidOrderStateException(
                    "订单当前状态为 " + status.displayName() + "，不允许取消");
        }
        this.status = OrderStatus.CANCELED;
        this.domainEvents.add(new OrderCanceledEvent(id));
    }

    public void update(CustomerName newCustomerName, Money newAmount) {
        if (status != OrderStatus.CREATED) {
            throw new InvalidOrderStateException(
                    "订单当前状态为 " + status.displayName() + "，不允许修改");
        }
        this.customerName = newCustomerName;
        this.amount = newAmount;
        this.domainEvents.add(new OrderUpdatedEvent(id, customerName, amount));
    }

    // === 领域事件管理 ===

    // 取出并清空已累积的事件；应用层在事务提交后调用
    // "拉取并清空" 的语义保证同一事件不会被发布两次
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> copy = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return copy;
    }

    // === 只读访问 ===

    public OrderId getId() {
        return id;
    }

    public CustomerName getCustomerName() {
        return customerName;
    }

    public Money getAmount() {
        return amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    // 对外只暴露不可变视图，防止外部绕过聚合根修改子实体集合
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public String statusToString() {
        return status.displayName();
    }
}
