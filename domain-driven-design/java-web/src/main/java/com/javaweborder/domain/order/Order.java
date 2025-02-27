// 领域层(Domain)：领域实体，聚合根
package com.javaweborder.domain.order;

import java.util.ArrayList;
import java.util.List;

// 订单状态枚举
enum OrderStatus {
    CREATED,   // 订单已创建
    CANCELED;  // 订单已取消
}

public class Order {
    
    private int id;                          // 订单ID
    private String customerName;             // 客户名称
    private double amount;                   // 订单金额
    private OrderStatus status;              // 订单状态
    private List<OrderItem> items;           // 订单项列表

    public Order(int id, String customerName, double amount) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
        this.status = OrderStatus.CREATED;
        this.items = new ArrayList<OrderItem>();
    }

    // 添加订单项
    public void addItem(OrderItem item) {
        item.setOrder(this);  // 设置订单项所属订单
        this.items.add(item);
    }

    // 删除订单项
    public void removeItem(OrderItem item) {
        this.items.remove(item);
    }

    // 计算订单总金额
    public void calculateTotalAmount() {
        this.amount = items.stream().mapToDouble(item -> item.getQuantity() * item.getPrice()).sum();
    }

    // 取消订单
    public void cancel() {
        if (status == OrderStatus.CREATED) {
            this.status = OrderStatus.CANCELED;
            System.out.println("订单 ID " + id + " 已取消");
        } else {
            System.out.println("订单 ID " + id + " 已取消，无法重复取消");
        }
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    // 状态转换为字符串
    public String statusToString() {
        switch (status) {
            case CREATED:
                return "已创建";
            case CANCELED:
                return "已取消";
            default:
                return "未知状态";
        }
    }
}