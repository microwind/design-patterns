package src;

/**
 * Order - 订单实体（值对象）
 *
 * 【设计模式】
 *   - 值对象模式：Order 是不可变对象，创建后状态不再改变。
 *     所有字段通过构造函数初始化，仅提供 getter。
 *
 * 【架构思想】
 *   在微服务中，订单实体通常作为服务间传递的数据载体（DTO）。
 *   本示例中 status 字段体现了业务结果："CREATED"（创建成功）或 "REJECTED"（库存不足被拒绝）。
 */
public class Order {

    /** 订单ID */
    private final String orderId;
    /** 商品SKU */
    private final String sku;
    /** 订购数量 */
    private final int quantity;
    /** 订单状态：CREATED / REJECTED */
    private final String status;

    public Order(String orderId, String sku, int quantity, String status) {
        this.orderId = orderId;
        this.sku = sku;
        this.quantity = quantity;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }
}
