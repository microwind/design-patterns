package com.microwind.javaweborder.domain.order;

import com.microwind.javaweborder.domain.exception.InvalidOrderInputException;

import java.util.Objects;

/**
 * 客户名称值对象。
 *
 * <p>DDD 战术构件：<b>值对象</b>。把字符串字段（客户名、邮箱、电话等）
 * 独立为值对象，校验规则集中一处，避免"贫血字符串"散落各处造成校验遗漏。
 */
public final class CustomerName {

    private static final int MAX_LENGTH = 64;

    private final String value;

    private CustomerName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidOrderInputException("客户名称不能为空");
        }
        if (value.length() > MAX_LENGTH) {
            throw new InvalidOrderInputException("客户名称长度不能超过 " + MAX_LENGTH + " 个字符");
        }
        this.value = value;
    }

    /**
     * 构造 CustomerName。
     *
     * @param value 客户名称字符串
     * @return CustomerName 实例
     * @throws InvalidOrderInputException 当为空或超长
     */
    public static CustomerName of(String value) {
        return new CustomerName(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerName)) return false;
        CustomerName that = (CustomerName) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
