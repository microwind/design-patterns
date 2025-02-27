// 数据交换
package com.javaweborder.application.dto;

public class OrderDTO {

    private int id;
    private String customerName;
    private double totalAmount;

    // 构造函数
    public OrderDTO(int id, String customerName, double totalAmount) {
        this.id = id;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
    }

    // Getters 和 Setters
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

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
