// 应用层(Application) - 命令：更新订单
package com.microwind.javaweborder.application.command;

public class UpdateOrderCommand implements Command {

    private final long orderId;
    private final String customerName;
    private final double amount;

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
