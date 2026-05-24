package com.github.microwind.springboot4ddd.infrastructure.repository.order;

import com.github.microwind.springboot4ddd.domain.model.order.Order;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单领域模型 ↔ 数据对象 显式转换器
 *
 * <p>放在 infrastructure 层，避免领域模型反向依赖持久化结构。
 * 不使用 {@code BeanUtils.copyProperties}，所有字段映射在编译期可见，
 * 字段调整时编译器会立刻报错。
 *
 * @author jarry
 * @since 1.0.0
 */
public final class OrderConverter {

    private OrderConverter() {
    }

    public static OrderDO toDO(Order order) {
        if (order == null) {
            return null;
        }
        return OrderDO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public static Order toModel(OrderDO orderDO) {
        if (orderDO == null) {
            return null;
        }
        return Order.restore(
                orderDO.getId(),
                orderDO.getOrderNo(),
                orderDO.getUserId(),
                orderDO.getTotalAmount(),
                Order.OrderStatus.valueOf(orderDO.getStatus()),
                orderDO.getCreatedAt(),
                orderDO.getUpdatedAt()
        );
    }

    public static List<Order> toModelList(List<OrderDO> dos) {
        if (dos == null) {
            return null;
        }
        return dos.stream()
                .map(OrderConverter::toModel)
                .collect(Collectors.toList());
    }
}
