package com.github.microwind.springboot4ddd.domain.client.user;

import java.util.Objects;

/**
 * 用户上下文对外暴露的最小化只读快照（值对象）。
 *
 * <p>不可变；按属性值判等。订单上下文只关心 name / phone，
 * 不应直接接触 user 聚合根。
 *
 * @author jarry
 * @since 1.0.0
 */
public final class UserBriefInfo {

    private final Long userId;
    private final String name;
    private final String phone;

    public UserBriefInfo(Long userId, String name, String phone) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserBriefInfo)) return false;
        UserBriefInfo that = (UserBriefInfo) o;
        return Objects.equals(userId, that.userId)
                && Objects.equals(name, that.name)
                && Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, phone);
    }
}
