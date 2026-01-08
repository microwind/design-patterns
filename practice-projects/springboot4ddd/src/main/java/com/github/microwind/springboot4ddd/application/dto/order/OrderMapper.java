package com.github.microwind.springboot4ddd.application.dto.order;

import com.github.microwind.springboot4ddd.domain.model.order.Order;
import com.github.microwind.springboot4ddd.interfaces.vo.order.OrderResponse;
import com.github.microwind.springboot4ddd.interfaces.vo.order.OrderListResponse;
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
     * 为OrderDTO填充用户信息（用于跨库查询）
     */
    public void enrichUserInfo(OrderDTO orderDTO, String userName, String userPhone) {
        if (orderDTO != null) {
            orderDTO.setUserName(userName);
            orderDTO.setUserPhone(userPhone);
        }
    }

    /**
     * 将Order实体转换为OrderResponse（不含用户信息）
     */
    public OrderResponse toOrderResponse(Order order) {
        if (order == null) {
            return null;
        }

        return OrderResponse.builder()
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
     * 将Order实体转换为OrderListResponse（包含用户信息）
     */
    public OrderListResponse toListResponse(Order order, String userName, String userPhone) {
        if (order == null) {
            return null;
        }

        return OrderListResponse.builder()
//                .id(order.getId())
//                .orderNo(order.getOrderNo())
//                .userId(order.getUserId())
                .order(toOrderResponse(order))
                .userName(userName)
                .userPhone(userPhone)
//                .totalAmount(order.getTotalAmount())
//                .status(order.getStatus())
//                .statusDesc(getStatusDescription(order.getStatus()))
//                .createdAt(order.getCreatedAt())
//                .updatedAt(order.getUpdatedAt())
                .build();
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
