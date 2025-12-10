package com.microwind.knife.application.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microwind.knife.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderWithItemsPageDTO {
    @JsonProperty("orders")
    private List<OrderWithItemsDTO> content;

    private int currentPage;
    private int totalPages;
    private long totalItems;

    // 从 Page<Order> 转换
    public OrderWithItemsPageDTO(Page<Order> page) {
        this.content = page.getContent().stream()
                .map(OrderWithItemsDTO::from)
                .collect(Collectors.toList());
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalItems = page.getTotalElements();
    }
}
