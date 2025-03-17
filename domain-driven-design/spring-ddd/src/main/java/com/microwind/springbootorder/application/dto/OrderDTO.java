package com.microwind.springbootorder.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
  private String orderNo;     // 订单编号
  private BigDecimal amount;  // 订单金额
  private String orderName;   // 订单名称
  private String status;      // 订单状态（字符串表示，如 "PAID", "CANCELLED"）
  private Long userId;        // 用户ID
  private LocalDateTime createTime; // 订单创建时间
  private List<OrderItemDTO> items; // 订单项列表

  // 订单项 DTO（如果前端需要展示订单项信息）
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class OrderItemDTO {
    private String productName;
    private Integer quantity;
    private BigDecimal price;
  }
}
