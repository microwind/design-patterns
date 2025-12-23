package com.microwind.knife.application.dto.apiauth;

import com.microwind.knife.domain.apiauth.ApiUsers;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * ApiUser映射器
 * 用于在实体和DTO之间转换
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ApiUserMapper {
    ApiUserMapper INSTANCE = Mappers.getMapper(ApiUserMapper.class);

    // 从ApiUserDTO转换为实体
    ApiUsers toEntity(ApiUserDTO dto);

    List<ApiUserDTO> toDTO(List<ApiUsers> users);

    // 从ApiUsers实体转换为DTO
    ApiUserDTO toDTO(ApiUsers user);
}
