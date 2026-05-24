package com.github.microwind.springboot4ddd.application.command.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 创建订单命令（application 层）
 *
 * <p>纯参数容器，由 interfaces 层从 HTTP Request 转换得到，传给应用服务。
 * 不携带 validation 注解 —— 那是 interfaces 层 Request 的职责。
 *
 * @author jarry
 * @since 1.0.0
 */
@Getter
@Builder
@AllArgsConstructor
public class CreateOrderCommand {

    private final Long userId;
    private final BigDecimal totalAmount;
}
