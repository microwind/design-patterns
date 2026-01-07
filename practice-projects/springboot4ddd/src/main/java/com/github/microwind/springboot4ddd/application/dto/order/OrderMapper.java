package com.github.microwind.springboot4ddd.application.dto.order;

import com.github.microwind.springboot4ddd.domain.model.order.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单映射器
 * 用于在领域模型和DTO之间转换
 *
 * @author jarry
 * @since 1.0.0
 */
@Component
public class OrderMapper {

    /**
     * 将Order实体转换为OrderDTO
     */
    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        return OrderDTO.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .statusDesc(getStatusDescription(order.getStatus()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    /**
     * 将Order实体列表转换为OrderDTO列表
     */
    public List<OrderDTO> toDTOList(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        return orders.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取状态描述
     */
    private String getStatusDescription(String status) {
        if (status == null) {
            return null;
        }
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status);
            return orderStatus.getDescription();
        } catch (IllegalArgumentException e) {
            return status;
        }
    }
}
