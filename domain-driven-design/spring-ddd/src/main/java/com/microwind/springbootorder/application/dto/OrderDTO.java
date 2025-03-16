package com.microwind.springbootorder.application.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Setter
@Getter
public class OrderDTO {
  // Getter 和 Setter
  private String orderNo;
  private BigDecimal amount;
  private String orderName; // 订单名称

  // 无参构造器（便于 JSON 反序列化）
  public OrderDTO() {
  }

  public OrderDTO(String orderNo, BigDecimal amount, String orderName) {
    this.orderNo = orderNo;
    this.amount = amount;
    this.orderName = orderName;
  }

}
