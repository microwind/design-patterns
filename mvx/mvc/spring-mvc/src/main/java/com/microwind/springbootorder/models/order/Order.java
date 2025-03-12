package com.microwind.springbootorder.models.order;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 订单状态

    @Column(updatable = false)
    private LocalDateTime createTime; // 创建时间

    // 订单状态枚举
    public enum OrderStatus {
        CREATED, PAID, DELIVERED, COMPLETED, CANCELLED
    }

    // 持久化（persist） orderNo 为空，就会自动生成一个唯一的订单编号
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
