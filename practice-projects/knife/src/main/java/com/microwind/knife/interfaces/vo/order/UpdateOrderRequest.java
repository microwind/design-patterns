package com.microwind.knife.interfaces.vo.order;

import lombok.*;

import java.math.BigDecimal;

/**
 * 更新订单请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {
    private Long userId;        // 用户ID
    private BigDecimal amount;  // 订单金额
    private String orderName;   // 订单名称
    private String status;      // 订单状态
}

