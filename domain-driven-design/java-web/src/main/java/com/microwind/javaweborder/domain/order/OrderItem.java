package com.microwind.javaweborder.domain.order;

import com.microwind.javaweborder.domain.exception.InvalidOrderInputException;

import java.util.Objects;

/**
 * 订单项值对象（Value Object）。
 *
 * <p>DDD 战术构件：<b>值对象</b>。值对象由属性值定义相等，无独立身份，
 * 一旦创建不可变。修改时返回新实例（如 {@link #withQuantity(int)}）。
 *
 * <h3>是实体还是值对象？</h3>
 * <ul>
 *   <li>"两个内容相同但 ID 不同的订单项"应视为不同 → 实体（由 ID 标识）</li>
 *   <li>"两个内容相同的订单项就是同一个东西"       → 值对象（由属性标识）</li>
 * </ul>
 *
 * <p>本工程把 {@code OrderItem} 建模为值对象：不可变、属性等价、不持有
 * 聚合根 {@link Order} 的反向引用，避免破坏聚合边界。
 */
public final class OrderItem {

    /** 产品名称。 */
    private final String product;

    /** 数量，必须为正数。 */
    private final int quantity;

    /** 单价。 */
    private final Money price;

    /**
     * 构造订单项；构造时即校验不变约束。
     *
     * @param product  产品名称，非空白
     * @param quantity 数量，必须 &gt; 0
     * @param price    单价，非 {@code null}
     * @throws InvalidOrderInputException 当任一约束被违反
     */
    public OrderItem(String product, int quantity, Money price) {
        if (product == null || product.trim().isEmpty()) {
            throw new InvalidOrderInputException("产品名称不能为空");
        }
        if (quantity <= 0) {
            throw new InvalidOrderInputException("订单项数量必须为正数");
        }
        if (price == null) {
            throw new InvalidOrderInputException("订单项单价不能为空");
        }
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    /**
     * 本订单项的小计金额（单价 × 数量）。
     *
     * @return 小计金额
     */
    public Money subtotal() {
        return price.multiply(quantity);
    }

    /**
     * 值对象的"修改"返回新实例，原实例保持不变。
     *
     * @param newQuantity 新数量
     * @return 仅 quantity 被替换的新 OrderItem
     */
    public OrderItem withQuantity(int newQuantity) {
        return new OrderItem(product, newQuantity, price);
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getPrice() {
        return price;
    }

    /**
     * 值对象的相等：基于属性值。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;
        OrderItem that = (OrderItem) o;
        return quantity == that.quantity
                && product.equals(that.product)
                && price.equals(that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity, price);
    }

    @Override
    public String toString() {
        return String.format("OrderItem{product='%s', quantity=%d, price=%s}",
                product, quantity, price);
    }
}
