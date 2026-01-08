package com.github.microwind.springboot4ddd.interfaces.vo.order;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单列表响应对象
 * 用于返回订单列表，包含用户详情（跨库查询）
 *
 * @author jarry
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponse {

    /**
     * 订单基本信息
     * @JsonUnwrapped 会将 order 对象的所有字段展开到当前层级
     */
    @JsonUnwrapped  // ✅ 关键注解
    private OrderResponse order;

    /**
     * 用户姓名（跨库查询）
     */
    private String userName;

    /**
     * 用户电话（跨库查询）
     */
    private String userPhone;
}
