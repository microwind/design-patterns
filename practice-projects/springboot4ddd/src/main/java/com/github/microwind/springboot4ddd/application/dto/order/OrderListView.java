package com.github.microwind.springboot4ddd.application.dto.order;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单列表读模型（application 层）
 *
 * <p>组合订单基础信息与跨上下文的用户简介（name/phone）。
 * 通过 {@code @JsonUnwrapped} 把 {@link OrderDTO} 字段平铺到当前层级，
 * JSON 输出形态与原 {@code OrderListResponse} 兼容。
 *
 * @author jarry
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderListView {

    @JsonUnwrapped
    private OrderDTO order;

    private String userName;
    private String userPhone;
}
