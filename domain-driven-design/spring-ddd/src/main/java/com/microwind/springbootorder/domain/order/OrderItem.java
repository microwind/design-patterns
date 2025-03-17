package com.microwind.springbootorder.domain.order;

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
//需要建立对应的表
//@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    private String product;   // 产品名称
    private int quantity;     // 数量
    private double price;     // 单价
    @ManyToOne
    @JoinColumn(name = "order_order_id", insertable = false, updatable = false)
    private Order order;      // 关联的订单（聚合根）
}
