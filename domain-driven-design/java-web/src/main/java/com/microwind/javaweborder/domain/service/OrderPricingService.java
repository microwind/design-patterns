// 领域层(Domain) - 领域服务：OrderPricingService
//
// 领域服务承载"不便归属于某个实体或值对象"的业务逻辑：
// 跨聚合的计算、可替换的业务策略、天然动词式的操作。
//
// 与应用服务的区别：
// - 领域服务：纯业务规则，处于领域层，无事务、无 IO、无 HTTP
// - 应用服务：用例编排，处于应用层，负责事务/事件/调用领域对象
//
// 本类以"VIP 客户 9 折"演示策略型领域服务。
package com.microwind.javaweborder.domain.service;

import com.microwind.javaweborder.domain.order.CustomerName;
import com.microwind.javaweborder.domain.order.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderPricingService {

    // VIP 客户的折扣率（演示用，真实场景应来自配置/规则引擎）
    private static final BigDecimal VIP_DISCOUNT = new BigDecimal("0.9");

    // 根据客户身份对原始金额进行折扣计算
    public Money applyDiscount(CustomerName customer, Money originalAmount) {
        if (isVip(customer)) {
            BigDecimal discounted = originalAmount.amount()
                    .multiply(VIP_DISCOUNT)
                    .setScale(2, RoundingMode.HALF_UP);
            return Money.of(discounted);
        }
        return originalAmount;
    }

    // 演示用：以名字关键字判定 VIP；真实场景应当查询客户聚合或外部服务
    private boolean isVip(CustomerName customer) {
        if (customer == null) return false;
        String name = customer.value();
        return name.contains("VIP") || name.startsWith("齐天");
    }
}
