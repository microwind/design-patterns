package com.microwind.javaweborder.domain.order;

import com.microwind.javaweborder.domain.exception.InvalidOrderInputException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 金额值对象。
 *
 * <p>DDD 战术构件：<b>值对象</b>（典型示例）。本类是 Primitive Obsession
 * （基本类型偏执）的解药：当 {@code double} 同时表示"金额""折扣率""重量"时，
 * 类型签名无法区分；{@code Money} 把语义带回类型系统。
 *
 * <h3>设计要点</h3>
 * <ul>
 *   <li>使用 {@link BigDecimal} 避免浮点精度丢失（金钱不能丢分）</li>
 *   <li>不可变；{@link #add}/{@link #multiply} 等运算返回新实例</li>
 *   <li>统一保留两位小数（{@link RoundingMode#HALF_UP}）</li>
 * </ul>
 */
public final class Money {

    /** 零金额常量，便于累加初值。 */
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidOrderInputException("金额不能为 null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOrderInputException("金额不能为负数，实际：" + amount);
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 由 {@code double} 构造 Money（接口/外部输入时常用）。
     *
     * @param value 金额数值，必须 &ge; 0
     * @return Money 实例
     * @throws InvalidOrderInputException 当为负数
     */
    public static Money of(double value) {
        return new Money(BigDecimal.valueOf(value));
    }

    /**
     * 由 {@link BigDecimal} 构造 Money。
     *
     * @param value 金额数值
     * @return Money 实例
     */
    public static Money of(BigDecimal value) {
        return new Money(value);
    }

    /**
     * 相加返回新实例，原实例不变。
     *
     * @param other 另一个金额
     * @return 相加结果
     */
    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    /**
     * 乘以整数因子返回新实例。
     *
     * @param factor 整数因子
     * @return 相乘结果
     */
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
