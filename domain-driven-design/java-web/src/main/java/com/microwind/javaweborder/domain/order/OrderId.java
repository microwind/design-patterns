// 领域层(Domain) - 值对象：订单 ID
//
// 值对象（Value Object）三大特征：不可变、属性等价、无副作用方法。
//
// 用 OrderId 而不是裸 long 的好处：
// - 类型安全：避免 OrderId 与 UserId、ProductId 在签名上被混用
// - 表达力：方法签名读起来就是领域语言
// - 不变约束：构造时校验（非负），生成规则也内聚于此
package com.microwind.javaweborder.domain.order;

import com.microwind.javaweborder.domain.exception.InvalidOrderInputException;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class OrderId {

    private final long value;

    private OrderId(long value) {
        // 不变约束：订单 ID 必须为正数
        if (value <= 0) {
            throw new InvalidOrderInputException("订单 ID 必须为正数，实际：" + value);
        }
        this.value = value;
    }

    // 从已有数值还原（如：从数据库读出来的 ID）
    public static OrderId of(long value) {
        return new OrderId(value);
    }

    // 生成新订单 ID（毫秒时间戳 * 1000 + 0-999 随机数）
    // ID 生成是聚合"出生"的一部分，归属领域层而非应用层
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
