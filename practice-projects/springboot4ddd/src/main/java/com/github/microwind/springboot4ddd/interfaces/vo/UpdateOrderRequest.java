package com.github.microwind.springboot4ddd.interfaces.vo;

import lombok.Data;

/**
 * 更新订单请求
 *
 * @author jarry
 * @since 1.0.0
 */
@Data
public class UpdateOrderRequest {

    /**
     * 订单状态：PENDING, PAID, CANCELLED, COMPLETED
     */
    private String status;
}
