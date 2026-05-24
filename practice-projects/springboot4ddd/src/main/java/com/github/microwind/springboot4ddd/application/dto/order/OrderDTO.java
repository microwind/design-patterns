package com.github.microwind.springboot4ddd.application.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单数据传输对象
 *
 * <p>纯订单字段。跨上下文的用户信息组装走读模型 {@code OrderListResponse}。
 *
 * @author jarry
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private String statusDesc;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
