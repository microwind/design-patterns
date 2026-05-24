package com.github.microwind.springboot4ddd.application.dto.order;

import com.github.microwind.springboot4ddd.domain.model.order.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单映射器
 *
 * <p>application 层内部的领域模型 ↔ DTO/View 转换。
 * 不依赖 interfaces 层，输出对象 controller 可直接序列化返回。
 *
 * @author jarry
 * @since 1.0.0
 */
@Component
public class OrderMapper {

    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }
        Order.OrderStatus status = order.getStatus();
        return OrderDTO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(status != null ? status.name() : null)
                .statusDesc(status != null ? status.getDescription() : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public List<OrderDTO> toDTOList(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        return orders.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * 组合订单基础信息与跨上下文的用户简介，输出列表读模型。
     */
    public OrderListView toListView(Order order, String userName, String userPhone) {
        if (order == null) {
            return null;
        }
        return OrderListView.builder()
                .order(toDTO(order))
                .userName(userName)
                .userPhone(userPhone)
                .build();
    }
}
