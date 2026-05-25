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

/**
 * 订单聚合根（Aggregate Root）。
 *
 * <p>DDD 战术构件：<b>聚合根</b>。聚合根是聚合对外的唯一入口，
 * 负责维护聚合内部的不变量（业务规则），并以"充血模型"承载业务方法。
 *
 * <h3>设计约束</h3>
 * <ul>
 *   <li>不暴露 setter；外部状态变更只能通过业务方法
 *       ({@link #cancel()} / {@link #update} / {@link #addItem})。</li>
 *   <li>包级私有构造器，强制走 {@link OrderFactory} 创建，
 *       保证"出生路径"（生成 ID、记录创建事件）的一致性。</li>
 *   <li>所有状态变更累积领域事件至内部列表，由应用层在事务边界外
 *       通过 {@link #pullDomainEvents()} 拉取并发布。</li>
 *   <li>子实体集合对外只暴露不可变视图，外部无法绕过聚合根修改。</li>
 * </ul>
 *
 * <h3>充血 vs 贫血</h3>
 * 贫血模型把规则散落在 Service 里；本类把"取消订单只能在 CREATED 状态下"
 * 这类业务规则封装在聚合根内部，错误条件直接抛领域异常。
 *
 * @see OrderFactory  聚合工厂，负责创建新订单
 * @see OrderItem     聚合内子实体（值对象形式）
 * @see DomainEvent   领域事件
 */
public class Order {

    /** 订单 ID（值对象，不可变）。 */
    private final OrderId id;

    /** 客户名称（值对象）。 */
    private CustomerName customerName;

    /** 订单金额（值对象，BigDecimal 精度）。 */
    private Money amount;

    /** 订单状态。 */
    private OrderStatus status;

    /** 聚合内子实体集合。 */
    private final List<OrderItem> items;

    /** 已累积但尚未发布的领域事件。 */
    private final List<DomainEvent> domainEvents;

    /**
     * 包级私有构造器：限制外部直接 new，强制走 {@link OrderFactory} 创建。
     *
     * @param id           订单 ID
     * @param customerName 客户名称
     * @param amount       订单金额
     */
    Order(OrderId id, CustomerName customerName, Money amount) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
        this.status = OrderStatus.CREATED;
        this.items = new ArrayList<>();
        this.domainEvents = new ArrayList<>();
    }

    /**
     * 从仓储还原已存在的订单。
     *
     * <p>"新建"与"还原"是两种不同的出生路径：新建会发出 {@link OrderCreatedEvent}，
     * 还原不应再发出，否则下游消费者会收到重复事件。
     *
     * @param id           订单 ID
     * @param customerName 客户名称
     * @param amount       订单金额
     * @param status       订单当前状态
     * @param items        订单项列表（可为 {@code null}）
     * @return 还原后的聚合根
     */
    public static Order restore(OrderId id, CustomerName customerName, Money amount,
                                OrderStatus status, List<OrderItem> items) {
        Order order = new Order(id, customerName, amount);
        order.status = status;
        if (items != null) {
            order.items.addAll(items);
        }
        return order;
    }

    /**
     * 仅供 {@link OrderFactory} 调用：记录订单创建事件。
     */
    void recordCreated() {
        domainEvents.add(new OrderCreatedEvent(id, customerName, amount));
    }

    // ====================== 业务方法（充血模型核心） ======================

    /**
     * 向订单中加入一个订单项。
     *
     * @param item 订单项，不能为 {@code null}
     * @throws InvalidOrderInputException 当 {@code item} 为 {@code null}
     */
    public void addItem(OrderItem item) {
        if (item == null) {
            throw new InvalidOrderInputException("订单项不能为 null");
        }
        this.items.add(item);
    }

    /**
     * 从订单中移除一个订单项。
     *
     * @param item 要移除的订单项
     */
    public void removeItem(OrderItem item) {
        this.items.remove(item);
    }

    /**
     * 根据订单项小计重新计算订单总金额。
     */
    public void recalculateAmount() {
        Money sum = Money.ZERO;
        for (OrderItem item : items) {
            sum = sum.add(item.subtotal());
        }
        this.amount = sum;
    }

    /**
     * 取消订单。
     *
     * <p>业务规则：仅 {@link OrderStatus#CREATED} 状态可被取消，
     * 重复取消视为错误并抛出领域异常。状态转换成功后累积
     * {@link OrderCanceledEvent}。
     *
     * @throws InvalidOrderStateException 当当前状态不允许取消
     */
    public void cancel() {
        if (!status.canTransitionTo(OrderStatus.CANCELED)) {
            throw new InvalidOrderStateException(
                    "订单当前状态为 " + status.displayName() + "，不允许取消");
        }
        this.status = OrderStatus.CANCELED;
        this.domainEvents.add(new OrderCanceledEvent(id));
    }

    /**
     * 更新订单的客户与金额。
     *
     * <p>业务规则：仅 {@link OrderStatus#CREATED} 状态可被修改。
     * 修改成功后累积 {@link OrderUpdatedEvent}。
     *
     * @param newCustomerName 新的客户名称
     * @param newAmount       新的订单金额
     * @throws InvalidOrderStateException 当当前状态不允许修改
     */
    public void update(CustomerName newCustomerName, Money newAmount) {
        if (status != OrderStatus.CREATED) {
            throw new InvalidOrderStateException(
                    "订单当前状态为 " + status.displayName() + "，不允许修改");
        }
        this.customerName = newCustomerName;
        this.amount = newAmount;
        this.domainEvents.add(new OrderUpdatedEvent(id, customerName, amount));
    }

    // ====================== 领域事件管理 ======================

    /**
     * 取出并清空已累积的领域事件。
     *
     * <p>由应用层在事务提交后调用，把事件一次性发布给
     * {@link com.microwind.javaweborder.domain.event.DomainEventPublisher}。
     * "拉取并清空"的语义保证同一事件不会被发布两次。
     *
     * @return 本次累积的事件快照（独立列表）
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> copy = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return copy;
    }

    // ====================== 只读访问 ======================

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

    /**
     * 返回子实体集合的不可变视图，防止外部绕过聚合根修改集合。
     *
     * @return 不可修改的订单项列表
     */
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public String statusToString() {
        return status.displayName();
    }
}
