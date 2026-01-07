package com.github.microwind.springboot4ddd.domain.model.order;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        return order;
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
    }
}
