package com.microwind.javaweborder.application.command;

/**
 * 更新订单命令。
 */
public class UpdateOrderCommand implements Command {

    private final long orderId;
    private final String customerName;
    private final double amount;

    /**
     * @param orderId      要更新的订单 ID
     * @param customerName 新的客户名称
     * @param amount       新的订单金额
     */
    public UpdateOrderCommand(long orderId, String customerName, double amount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.amount = amount;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getAmount() {
        return amount;
    }
}
