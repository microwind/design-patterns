// 应用层：数据交换DTO
package com.microwind.javaweborder.application.dto;

public class OrderDTO {

    private long id;
    private String customerName;
    private double totalAmount;

    // 构造函数
    public OrderDTO(long id, String customerName, double totalAmount) {
        this.id = id;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
    }

    // Getters 和 Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
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
