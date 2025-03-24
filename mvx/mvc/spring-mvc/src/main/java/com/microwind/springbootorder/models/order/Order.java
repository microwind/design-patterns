package com.microwind.springbootorder.models.order;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt; // 创建时间

    @Column(nullable = false)
    private LocalDateTime updatedAt; // 更新时间

    // 订单状态枚举
    public enum OrderStatus {
        CREATED("已创建"),
        PAID("已支付"),
        DELIVERED("已发货"),
        COMPLETED("已完成"),
        CANCELLED("已取消");

        private final String description;

        OrderStatus(String description) {
            this.description = description;
        }

        @JsonValue // 序列化时返回 description
        public String getDescription() {
            return description;
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
