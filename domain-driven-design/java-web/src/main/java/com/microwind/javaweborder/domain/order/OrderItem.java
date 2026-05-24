// 领域层(Domain) - 值对象：订单项 OrderItem
//
// 是实体还是值对象？判定标准：
// - "两个内容相同但 ID 不同的订单项" 应视为不同 → 实体（由 ID 标识）
// - "两个内容相同的订单项就是同一个东西"       → 值对象（由属性标识）
//
// 本工程把 OrderItem 建模为值对象：不可变、属性等价、不持有聚合根反向引用。
package com.microwind.javaweborder.domain.order;

import com.microwind.javaweborder.domain.exception.InvalidOrderInputException;

import java.util.Objects;

public final class OrderItem {

    private final String product;   // 产品名称
    private final int quantity;     // 数量
    private final Money price;      // 单价

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

    // 业务方法：本订单项的小计金额
    public Money subtotal() {
        return price.multiply(quantity);
    }

    // 值对象的"修改"返回新实例，原对象不变
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

    // 值对象的相等：基于属性值
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
