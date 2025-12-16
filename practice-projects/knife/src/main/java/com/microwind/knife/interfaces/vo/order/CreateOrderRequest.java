package com.microwind.knife.interfaces.vo.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

/**
 * 创建订单请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private String orderNo;     // 订单编号（可选，如果不提供会自动生成）

    @NotNull(message = "用户ID不能为空")
    private Long userId;        // 用户ID

    @NotNull(message = "订单金额不能为空")
    private BigDecimal amount;  // 订单金额

    @NotBlank(message = "订单名称不能为空")
    private String orderName;   // 订单名称
}

