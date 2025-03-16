package com.microwind.springbootorder.application.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microwind.springbootorder.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPageDTO {
    @JsonProperty("orders")  // 直接通过 Jackson 注解修改content字段名
    private List<Order> content;

    private int currentPage;
    private int totalPages;
    private long totalItems;

    // 自定义构造函数：从 Page<Order> 提取数据（代替手动 Getter）
    public OrderPageDTO(Page<Order> page) {
        this.content = page.getContent();
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalItems = page.getTotalElements();
    }
}