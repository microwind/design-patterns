package com.microwind.knife.domain.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 聚合根（Aggregate Root）管理整个聚合的生命周期：
// Order 是聚合根，OrderItem 是子聚合。
// Order 聚合控制子聚合 OrderItem 的生命周期并确保业务一致性。
// 领域层(Domain)：OrderItem 订单项实体，这里不再展开。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    @Column(name = "product")
    private String product;   // 产品名称

    @Column(name = "quantity")
    private int quantity;     // 数量

    @Column(name = "price")
    private double price;     // 单价

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore  // 避免序列化时的循环引用
    @JsonBackReference
    private Order order;      // 关联的订单（聚合根）
}
