// 领域层(Domain) - 值对象：客户名称
//
// 把字符串字段（客户名、邮箱、电话）独立为值对象，
// 校验规则集中一处，避免"贫血字符串"散落各处导致校验遗漏。
package com.microwind.javaweborder.domain.order;

import com.microwind.javaweborder.domain.exception.InvalidOrderInputException;

import java.util.Objects;

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
