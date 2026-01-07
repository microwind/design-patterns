package com.github.microwind.springboot4ddd.interfaces.vo.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建订单请求
 *
 * @author jarry
 * @since 1.0.0
 */
@Data
public class CreateOrderRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
}
