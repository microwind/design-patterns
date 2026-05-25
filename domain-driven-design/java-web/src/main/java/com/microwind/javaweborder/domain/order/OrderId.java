package com.microwind.javaweborder.domain.order;

import com.microwind.javaweborder.domain.exception.InvalidOrderInputException;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单 ID 值对象。
 *
 * <p>DDD 战术构件：<b>值对象</b>（用作聚合根的身份标识）。
 * 包装裸 {@code long} 的好处：
 * <ul>
 *   <li><b>类型安全</b>：避免 OrderId 与 UserId、ProductId 在方法签名上混用</li>
 *   <li><b>表达力</b>：方法签名读起来就是领域语言</li>
 *   <li><b>不变约束</b>：构造时校验（非负）</li>
 *   <li><b>生成规则内聚</b>：ID 生成属于领域内的关注点，归属领域层</li>
 * </ul>
 */
public final class OrderId {

    private final long value;

    private OrderId(long value) {
        if (value <= 0) {
            throw new InvalidOrderInputException("订单 ID 必须为正数，实际：" + value);
        }
        this.value = value;
    }

    /**
     * 由已存在的数值还原 OrderId（如从数据库读出）。
     *
     * @param value 已知的 ID 数值
     * @return OrderId 实例
     * @throws InvalidOrderInputException 当 {@code value <= 0}
     */
    public static OrderId of(long value) {
        return new OrderId(value);
    }

    /**
     * 生成全新订单 ID：毫秒时间戳 × 1000 + 0-999 随机数。
     *
     * <p>生成 ID 是聚合"出生"的一部分，归属领域层而非应用层。
     *
     * @return 新生成的 OrderId
     */
    public static OrderId generate() {
        long timestamp = System.currentTimeMillis();
        int random = ThreadLocalRandom.current().nextInt(1000);
        return new OrderId(timestamp * 1000 + random);
    }

    public long value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderId)) return false;
        OrderId orderId = (OrderId) o;
        return value == orderId.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
