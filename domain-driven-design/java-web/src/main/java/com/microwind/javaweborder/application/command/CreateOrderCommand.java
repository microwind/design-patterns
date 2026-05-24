// 应用层(Application) - 命令：创建订单
//
// 一个 Command 对象对应一个明确的"用例意图"。
// 不要把 Command 写成"通用 Dto"——每种业务意图应当独立成类，
// 以便演化时各自调整（例如未来"创建订单"需要新增收货地址，
// 只动 CreateOrderCommand，不影响 UpdateOrderCommand）。
package com.microwind.javaweborder.application.command;

public class CreateOrderCommand implements Command {

    private final String customerName;
    private final double amount;

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
