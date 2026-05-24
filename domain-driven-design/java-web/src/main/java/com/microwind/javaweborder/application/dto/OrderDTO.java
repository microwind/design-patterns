// 应用层(Application) - 数据传输对象 (DTO)
//
// DTO（Data Transfer Object）是接口层与外界交换数据的扁平结构。
// 把领域对象（Order）和 DTO 严格区分开，有几个好处：
// - 接口稳定：领域模型演化时，对外契约不必跟着变
// - 类型安全：值对象（OrderId / Money）只在领域内流通，外部用基本类型
// - 防止泄漏：聚合根上的业务方法不应该被 JSON 序列化暴露给客户端
package com.microwind.javaweborder.application.dto;

import com.microwind.javaweborder.domain.order.Order;

public class OrderDTO {

    private long id;
    private String customerName;
    private double totalAmount;
    private String status;

    public OrderDTO() {
        // Jackson 反序列化需要
    }

    public OrderDTO(long id, String customerName, double totalAmount, String status) {
        this.id = id;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // 由领域对象转换成 DTO 的工厂方法：转换逻辑集中在一处，避免散落
    public static OrderDTO fromDomain(Order order) {
        return new OrderDTO(
                order.getId().value(),
                order.getCustomerName().value(),
                order.getAmount().doubleValue(),
                order.getStatus().name()
        );
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
