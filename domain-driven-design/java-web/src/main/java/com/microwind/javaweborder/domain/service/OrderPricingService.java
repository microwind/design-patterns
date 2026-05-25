package com.microwind.javaweborder.domain.service;

import com.microwind.javaweborder.domain.order.CustomerName;
import com.microwind.javaweborder.domain.order.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 订单定价领域服务。
 *
 * <p>DDD 战术构件：<b>领域服务（Domain Service）</b>。承载那些"不便归属于
 * 某个实体或值对象"的业务逻辑，典型场景包括：
 * <ul>
 *   <li>计算逻辑涉及多个聚合</li>
 *   <li>业务策略需要替换（折扣策略、定价策略等）</li>
 *   <li>操作天然是动词式的，不属于"某个东西的行为"</li>
 * </ul>
 *
 * <h3>领域服务 vs 应用服务</h3>
 * <ul>
 *   <li><b>领域服务</b>：纯业务规则，处于领域层，无事务、无 IO、无 HTTP</li>
 *   <li><b>应用服务</b>：用例编排，处于应用层，负责事务/事件/调用领域对象</li>
 * </ul>
 *
 * <p>本类以"VIP 客户 9 折"演示策略型领域服务。
 */
public class OrderPricingService {

    /** VIP 客户折扣率（演示用，真实场景应来自配置或规则引擎）。 */
    private static final BigDecimal VIP_DISCOUNT = new BigDecimal("0.9");

    /**
     * 根据客户身份对原始金额应用折扣。
     *
     * @param customer       客户名称
     * @param originalAmount 原始金额
     * @return 折扣后的金额；非 VIP 客户返回原金额
     */
    public Money applyDiscount(CustomerName customer, Money originalAmount) {
        if (isVip(customer)) {
            BigDecimal discounted = originalAmount.amount()
                    .multiply(VIP_DISCOUNT)
                    .setScale(2, RoundingMode.HALF_UP);
            return Money.of(discounted);
        }
        return originalAmount;
    }

    /**
     * VIP 判定（演示用，真实场景应查询客户聚合或外部服务）。
     */
    private boolean isVip(CustomerName customer) {
        if (customer == null) return false;
        String name = customer.value();
        return name.contains("VIP") || name.startsWith("齐天");
    }
}
