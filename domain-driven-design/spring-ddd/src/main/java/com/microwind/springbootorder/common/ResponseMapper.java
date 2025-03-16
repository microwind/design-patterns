package com.microwind.springbootorder.common;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResponseMapper {
    ApiResponse<Object> toApiResponse(Object data);
}
