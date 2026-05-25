package com.microwind.javaweborder.application.dto;

import com.microwind.javaweborder.domain.order.Order;

/**
 * 订单数据传输对象（DTO）。
 *
 * <p>DTO 是接口层与外界交换数据的扁平结构。把领域对象 {@link Order} 与 DTO
 * 严格区分开的好处：
 * <ul>
 *   <li><b>接口稳定</b>：领域模型演化时，对外契约不必跟着变</li>
 *   <li><b>类型安全</b>：值对象（OrderId / Money）只在领域内流通，外部用基本类型</li>
 *   <li><b>防止泄漏</b>：聚合根上的业务方法不应被 JSON 序列化暴露给客户端</li>
 * </ul>
 */
public class OrderDTO {

    private long id;
    private String customerName;
    private double totalAmount;
    private String status;

    public OrderDTO() {
        // Jackson 反序列化需要无参构造器
    }

    public OrderDTO(long id, String customerName, double totalAmount, String status) {
        this.id = id;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    /**
     * 由领域对象转换为 DTO。
     *
     * <p>把转换逻辑集中在工厂方法里，避免散落各处。
     *
     * @param order 领域聚合根
     * @return 对应的 DTO
     */
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
