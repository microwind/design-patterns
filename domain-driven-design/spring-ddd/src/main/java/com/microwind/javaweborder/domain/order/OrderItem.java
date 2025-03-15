package com.microwind.javaweborder.domain.order;
// 领域层(Domain)：订单项实体
public class OrderItem {
    
    private long id;           // 订单项ID
    private String product;   // 产品名称
    private int quantity;     // 数量
    private double price;     // 单价
    private Order order;      // 关联的订单（聚合根）

    public OrderItem(long id, String product, int quantity, double price, Order order) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.order = order;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
