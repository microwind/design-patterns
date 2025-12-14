package com.microwind.knife.application.dto.user;

import com.microwind.knife.domain.user.User;
import com.microwind.knife.interfaces.request.user.CreateUserRequest;
import com.microwind.knife.interfaces.request.user.UpdateUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * User映射器
 * 用于在实体和DTO之间转换
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // 从CreateUserRequest转换为User实体
    User toEntity(CreateUserRequest request);

    // 从UpdateUserRequest更新User实体（仅更新非null字段）
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    void updateEntityFromRequest(UpdateUserRequest request, @MappingTarget User user);
}
