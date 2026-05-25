package com.microwind.javaweborder.domain.order;

/**
 * 订单状态枚举。
 *
 * <p>把状态从聚合根中抽出独立为枚举，集中放置：
 * <ul>
 *   <li>所有可能的状态值</li>
 *   <li>状态间允许的迁移规则（{@link #canTransitionTo}）</li>
 *   <li>对外展示名称（{@link #displayName}）</li>
 * </ul>
 */
public enum OrderStatus {

    /** 已创建，初始状态。 */
    CREATED("已创建"),

    /** 已取消，终态。 */
    CANCELED("已取消");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return 对外展示用的中文名称
     */
    public String displayName() {
        return displayName;
    }

    /**
     * 判定能否从当前状态迁移到目标状态。
     *
     * <p>业务规则：仅 {@code CREATED → CANCELED} 是合法迁移。
     *
     * @param target 目标状态
     * @return 允许迁移返回 {@code true}
     */
    public boolean canTransitionTo(OrderStatus target) {
        if (this == CREATED && target == CANCELED) {
            return true;
        }
        return false;
    }
}
