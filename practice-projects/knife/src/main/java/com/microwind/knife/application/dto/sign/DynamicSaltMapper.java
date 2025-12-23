package com.microwind.knife.application.dto.sign;

import com.microwind.knife.domain.apiauth.ApiDynamicSaltLog;
import com.microwind.knife.domain.sign.DynamicSalt;
import com.microwind.knife.interfaces.vo.sign.DynamicSaltRequest;
import com.microwind.knife.interfaces.vo.sign.DynamicSaltVerfiyRequest;
import com.microwind.knife.interfaces.vo.sign.SignRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * 动态盐值映射器
 * <p>
 * 负责 DynamicSalt 领域模型与 DTO、实体之间的转换
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DynamicSaltMapper {
    DynamicSaltMapper INSTANCE = Mappers.getMapper(DynamicSaltMapper.class);

    /**
     * 领域模型 -> DTO
     *
     * @param dynamicSalt 动态盐值领域模型
     * @return DTO 对象
     */
    DynamicSaltDTO toDTO(DynamicSalt dynamicSalt);

    /**
     * 领域模型 -> DTO（带 apiId）
     *
     * @param dynamicSalt 动态盐值领域模型
     * @param apiId       接口 ID
     * @return DTO 对象
     */
    default DynamicSaltDTO toDTO(DynamicSalt dynamicSalt, Long apiId) {
        DynamicSaltDTO dto = toDTO(dynamicSalt);
        if (dto != null) {
            dto.setApiId(apiId);
        }
        return dto;
    }

    @Mapping(source = "path", target = "apiPath")
    @Mapping(source = "dynamicSaltTime", target = "saltTimestamp")
    DynamicSaltDTO toDTO(DynamicSaltVerfiyRequest request);

    /**
     * DTO -> 持久化实体
     *
     * @param dto 动态盐值 DTO
     * @return 持久化实体
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "used", constant = "0")
    @Mapping(target = "createdAt", ignore = true)
    ApiDynamicSaltLog toEntity(DynamicSaltDTO dto);
}
