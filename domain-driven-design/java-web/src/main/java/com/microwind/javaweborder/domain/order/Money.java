// 领域层(Domain) - 值对象：金额
//
// 经典值对象示例：用 BigDecimal 而非 double 避免精度丢失，不可变，
// 提供 add / multiply 等业务运算且返回新实例。
//
// 这是 Primitive Obsession（基本类型偏执）的解药：当 double 同时
// 表示"金额""折扣率""重量"时，类型签名无法区分；Money 把语义带回来了。
package com.microwind.javaweborder.domain.order;

import com.microwind.javaweborder.domain.exception.InvalidOrderInputException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidOrderInputException("金额不能为 null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOrderInputException("金额不能为负数，实际：" + amount);
        }
        // 统一精度：保留两位小数，四舍五入
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(double value) {
        return new Money(BigDecimal.valueOf(value));
    }

    public static Money of(BigDecimal value) {
        return new Money(value);
    }

    // 值对象的运算返回新实例，原对象不变
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money multiply(int factor) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)));
    }

    public BigDecimal amount() {
        return amount;
    }

    public double doubleValue() {
        return amount.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
