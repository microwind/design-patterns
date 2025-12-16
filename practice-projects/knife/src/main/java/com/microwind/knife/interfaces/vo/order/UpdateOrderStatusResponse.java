package com.microwind.knife.interfaces.vo.order;
public class UpdateOrderStatusResponse {
    private String orderNo;
    private String status;

    public UpdateOrderStatusResponse(String orderNo, String status) {
        this.orderNo = orderNo;
        this.status = status;
    }

    // Getters and Setters
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}