package com.github.microwind.springboot4ddd.domain.model.order;

import com.github.microwind.springboot4ddd.domain.event.DomainEvent;
import com.github.microwind.springboot4ddd.domain.event.order.OrderCancelledEvent;
import com.github.microwind.springboot4ddd.domain.event.order.OrderCompletedEvent;
import com.github.microwind.springboot4ddd.domain.event.order.OrderCreatedEvent;
import com.github.microwind.springboot4ddd.domain.event.order.OrderPaidEvent;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单领域模型（聚合根）
 *
 * @author jarry
 * @since 1.0.0
 */
@Data
@Table("orders")
public class Order {

    /**
     * 订单ID
     */
    @Id
    private Long id;

    /**
     * 订单号
     */
    @Column("order_no")
    private String orderNo;

    /**
     * 用户ID
     */
    @Column("user_id")
    private Long userId;

    /**
     * 订单总金额
     */
    @Column("total_amount")
    private BigDecimal totalAmount;

    /**
     * 订单状态：PENDING-待支付，PAID-已支付，CANCELLED-已取消，COMPLETED-已完成
     */
    private String status;

    /**
     * 创建时间
     */
    @Column("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 领域事件列表（不持久化到数据库）
     */
    @Transient
    private List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 订单状态枚举
     */
    public enum OrderStatus {
        PENDING("待支付"),
        PAID("已支付"),
        CANCELLED("已取消"),
        COMPLETED("已完成");

        private final String description;

        OrderStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 创建新订单
     */
    public static Order create(Long userId, BigDecimal totalAmount) {
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING.name());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // 记录订单创建事件（注意：此时订单ID可能为null，需要在保存后再记录事件）
        return order;
    }

    /**
     * 记录订单创建事件（在订单保存后调用）
     */
    public void recordCreatedEvent() {
        this.recordEvent(new OrderCreatedEvent(
            this.id,
            this.orderNo,
            this.userId,
            this.totalAmount,
            this.status
        ));
    }

    /**
     * 生成订单号
     */
    private static String generateOrderNo() {
        return "ORD" + System.currentTimeMillis();
    }

    /**
     * 取消订单
     */
    public void cancel() {
        if (!OrderStatus.PENDING.name().equals(this.status)) {
            throw new IllegalStateException("只有待支付订单可以取消");
        }
        this.status = OrderStatus.CANCELLED.name();
        this.updatedAt = LocalDateTime.now();

        // 记录订单取消事件
        this.recordEvent(new OrderCancelledEvent(
            this.id,
            this.orderNo,
            this.userId,
            this.totalAmount,
            this.status
        ));
    }

    /**
     * 支付订单
     */
    public void pay() {
        if (!OrderStatus.PENDING.name().equals(this.status)) {
            throw new IllegalStateException("只有待支付订单可以支付");
        }
        this.status = OrderStatus.PAID.name();
        this.updatedAt = LocalDateTime.now();

        // 记录订单支付事件
        this.recordEvent(new OrderPaidEvent(
            this.id,
            this.orderNo,
            this.userId,
            this.totalAmount,
            this.status
        ));
    }

    /**
     * 完成订单
     */
    public void complete() {
        if (!OrderStatus.PAID.name().equals(this.status)) {
            throw new IllegalStateException("只有已支付订单可以完成");
        }
        this.status = OrderStatus.COMPLETED.name();
        this.updatedAt = LocalDateTime.now();

        // 记录订单完成事件
        this.recordEvent(new OrderCompletedEvent(
            this.id,
            this.orderNo,
            this.userId,
            this.totalAmount,
            this.status
        ));
    }

    /**
     * 记录领域事件
     *
     * @param event 领域事件
     */
    private void recordEvent(DomainEvent event) {
        if (this.domainEvents == null) {
            this.domainEvents = new ArrayList<>();
        }
        this.domainEvents.add(event);
    }

    /**
     * 获取领域事件列表
     *
     * @return 领域事件列表
     */
    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(this.domainEvents);
    }

    /**
     * 清空领域事件列表
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
