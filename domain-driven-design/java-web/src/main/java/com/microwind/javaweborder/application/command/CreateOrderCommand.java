package com.microwind.javaweborder.application.command;

/**
 * 创建订单命令。
 *
 * <p>一个 Command 对象对应一个明确的业务意图。
 * 不要把 Command 写成"通用 DTO"——每种业务用例独立成类，
 * 以便演化时各自调整（例如新增收货地址只动 CreateOrderCommand，
 * 不影响 UpdateOrderCommand）。
 */
public class CreateOrderCommand implements Command {

    private final String customerName;
    private final double amount;

    /**
     * @param customerName 客户名称
     * @param amount       订单金额（原始金额，可能在领域服务中再被折扣）
     */
    public CreateOrderCommand(String customerName, double amount) {
        this.customerName = customerName;
        this.amount = amount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getAmount() {
        return amount;
    }
}
