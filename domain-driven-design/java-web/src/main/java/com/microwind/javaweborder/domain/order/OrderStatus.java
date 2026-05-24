// 领域层(Domain) - 订单状态枚举
//
// 状态从聚合根 Order 中提取出来，独立为枚举。
// 这样：
// - 状态语义（哪些状态、状态间允许的迁移）集中在一处
// - 多个聚合或服务需要消费状态时不会产生重复定义
package com.microwind.javaweborder.domain.order;

public enum OrderStatus {

    CREATED("已创建"),
    CANCELED("已取消");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    // 业务规则：从 CREATED 才可以转到 CANCELED
    public boolean canTransitionTo(OrderStatus target) {
        if (this == CREATED && target == CANCELED) {
            return true;
        }
        return false;
    }
}
