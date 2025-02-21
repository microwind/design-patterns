// domain/Order.java
package domain;

public class Order {
    private int id;
    private String customerName;
    private double amount;
    private String status;

    public Order(int id, String customerName, double amount) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
        this.status = "CREATED";
    }

    public void cancel() {
        if ("CREATED".equals(this.status)) {
            this.status = "CANCELED";
            System.out.println("订单 ID " + this.id + " 已取消");
        } else {
            System.out.println("订单 ID " + this.id + " 已经取消，无法重复操作");
        }
    }

    public void display() {
        System.out.println("订单 ID: " + this.id);
        System.out.println("客户名称: " + this.customerName);
        System.out.println("订单金额: " + String.format("%.2f", this.amount));
        System.out.println("订单状态: " + this.status);
    }

    public int getId() {
        return id;
    }
}