package com.microwind.knife.domain.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore  // 默认不序列化订单项，避免循环引用
    private List<OrderItem> items = new ArrayList<>();           // 订单项列表

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", columnDefinition = "BIGINT")
    private Long orderId;

    // 订单编号
    @Column(name = "order_no", nullable = false)
    private String orderNo;

    // 用户ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 订单金额
    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    // 订单名称
    @Column(name = "order_name", nullable = false)
    private String orderName;

    // 订单状态
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 创建时间
    @Column(name = "created_at", updatable = false)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // 更新时间
    @Column(name = "updated_at", nullable = false)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

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

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now; // 确保创建时同时初始化 updatedAt
    }

    @PreUpdate // 必须添加此注解
    public void onUpdate() { // 建议改为 public 访问级别
        this.updatedAt = LocalDateTime.now();
    }
}
