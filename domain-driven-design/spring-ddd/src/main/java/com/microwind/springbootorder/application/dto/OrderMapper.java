package com.microwind.springbootorder.application.dto;

import com.microwind.springbootorder.domain.order.Order;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDTO toDTO(Order order);
    Order toEntity(OrderDTO orderDTO);
}