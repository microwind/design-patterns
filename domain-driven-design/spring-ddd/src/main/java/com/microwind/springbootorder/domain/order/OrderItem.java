package com.microwind.springbootorder.domain.order;

import lombok.Data;

// 聚合根（Aggregate Root）管理整个聚合的生命周期：
// Order 是聚合根，OrderItem 是子聚合。
// Order 聚合控制子聚合 OrderItem 的生命周期并确保业务一致性。
// 领域层(Domain)：OrderItem 订单项实体，这里不再展开。
@Data
public class OrderItem {
}
