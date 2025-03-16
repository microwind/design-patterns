package com.microwind.springbootorder.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
DomainService专注于领域内的核心业务逻辑，处理领域对象之间的交互和复杂的业务规则。
以下场景可使用 DomainService：
1. 跨实体协作：当业务逻辑需要操作多个领域对象时（如订单+优惠券+库存）
2. 核心算法：包含复杂计算逻辑（如订单价格计算策略）
3. 防腐败层：需要隔离外部系统对接的核心逻辑（如支付金额校验）
4. 模式复用：多个应用服务重复使用的领域逻辑
*/
@Service
@RequiredArgsConstructor
public class OrderDomainService {

    /**
     * 支付订单的核心业务逻辑
     * @param order 需要支付的订单实体
     */
    public void payOrder(Order order) {
        // 1. 验证订单状态（领域规则）
        if (!order.getStatus().canPay()) {
            throw new IllegalStateException("订单当前状态不可支付: " + order.getStatus());
        }

        // 2. 执行支付操作（修改领域对象状态）
        order.markAsPaid();
    }

    /**
     * 取消订单的核心业务逻辑
     * @param order 需要取消的订单实体
     */
    public void cancelOrder(Order order) {
        // 1. 验证订单状态（领域规则）
        if (!order.getStatus().canCancel()) {
            throw new IllegalStateException("订单当前状态不可取消: " + order.getStatus());
        }

        // 2. 执行取消操作（修改领域对象状态）
        order.markAsCancelled();
    }
}