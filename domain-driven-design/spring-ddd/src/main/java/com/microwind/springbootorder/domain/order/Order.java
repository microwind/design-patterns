package com.microwind.springbootorder.domain.order;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();           // 订单项列表

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", columnDefinition = "BIGINT")
    private Long orderId;

    // 添加 getter 和 setter 方法
    @Getter
    @Setter
    @Column(nullable = false)
    private String orderNo; // 订单编号

    @Column(nullable = false)
    private Long userId; // 用户ID

    @Column(precision = 10, scale = 2)
    private BigDecimal amount; // 订单金额

    @Column(nullable = false)
    private String orderName; // 订单名称

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 订单状态

    @Column(updatable = false)
    private LocalDateTime createTime; // 创建时间

    public void markAsCancelled() {
        this.status = OrderStatus.CANCELLED;
        // 订单取消时，可添加对 OrderItem 的处理逻辑
        for (OrderItem item : items) {
            // 例如可以标记订单项为无效等操作
        }
    }

    public void markAsPaid() {
        this.status = OrderStatus.PAID;
        // 订单支付时，可添加对 OrderItem 的处理逻辑
        for (OrderItem item : items) {
            // 例如可以标记订单项为已支付等操作
        }
    }

    // 添加订单项
    public void addOrderItem(OrderItem item) {
        item.setOrder(this);
        items.add(item);
    }

    // 移除订单项
    public void removeOrderItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    // 订单状态枚举
    public enum OrderStatus {
        CREATED,   // 订单已创建
        PAID,      // 订单已支付
        DELIVERED, // 订单已发货
        COMPLETED, // 订单已完成
        CANCELLED; // 订单已取消

        /**
         * 判断当前订单状态是否允许支付。
         * 仅当订单状态为CREATED时，允许支付。
         * @return 如果可以支付返回true，否则返回false。
         */
        public boolean canPay() {
            return this == CREATED;
        }

        /**
         * 判断当前订单状态是否允许取消。
         * 只有当订单状态为CREATED或PAID时，允许取消。
         * @return 如果可以取消返回true，否则返回false。
         */
        public boolean canCancel() {
            return switch (this) {
                case CREATED, PAID -> true;
                default -> false;
            };
        }
    }

    // 持久化（persist） orderNo 为空，就会自动生成一个唯一的订单编号
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
