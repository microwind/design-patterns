package com.microwind.knife.application.dto.order;

import com.microwind.knife.domain.order.Order;
import com.microwind.knife.interfaces.vo.order.CreateOrderRequest;
import com.microwind.knife.interfaces.vo.order.UpdateOrderRequest;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

/**
 * Order映射器
 * 用于在实体和DTO之间转换
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    // 从CreateOrderRequest转换为Order实体
    Order toEntity(CreateOrderRequest request);

    // 从 UpdateOrderRequest 更新 Order（只更新非null字段）
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "orderNo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "status", target = "status",
            qualifiedByName = "stringToOrderStatus")
    void updateEntityFromRequest(UpdateOrderRequest request, @MappingTarget Order order);

    // 字符串转换为OrderStatus枚举
    @Named("stringToOrderStatus")
    default Order.OrderStatus stringToOrderStatus(String status) {
        if (status == null) {
            return null;
        }
        try {
            return Order.OrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // 从Order实体转换为OrderWithItemsDTO
    OrderWithItemsDTO toDTO(Order order);
}
