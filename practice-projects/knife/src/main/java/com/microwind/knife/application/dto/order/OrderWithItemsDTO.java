package com.microwind.knife.application.dto.order;

import com.microwind.knife.domain.order.Order;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderWithItemsDTO {
    private Long orderId;
    private String orderNo;
    private Long userId;
    private BigDecimal amount;
    private String orderName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Long orderItemId;
        private String product;
        private int quantity;
        private double price;
    }

    // 从 Order 实体转换为 DTO
    public static OrderWithItemsDTO from(Order order) {
        return OrderWithItemsDTO.builder()
                .orderId(order.getOrderId())
                .orderNo(order.getOrderNo())
                .userId(order.getUserId())
                .amount(order.getAmount())
                .orderName(order.getOrderName())
                .status(order.getStatus() != null ? order.getStatus().name() : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems().stream()
                        .map(item -> OrderItemDTO.builder()
                                .orderItemId(item.getOrderItemId())
                                .product(item.getProduct())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
