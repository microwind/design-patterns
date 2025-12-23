package com.microwind.knife.application.dto.sign;

import com.microwind.knife.domain.apiauth.ApiDynamicSaltLog;
import com.microwind.knife.domain.sign.DynamicSalt;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 动态盐值映射器
 * <p>
 * 负责 DynamicSalt 领域模型与 DTO、实体之间的转换
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public class DynamicSaltMapper {

    /**
     * 领域模型 -> DTO
     *
     * @param dynamicSalt 动态盐值领域模型
     * @return DTO 对象
     */
    public static DynamicSaltDTO toDTO(DynamicSalt dynamicSalt) {
        if (dynamicSalt == null) {
            return null;
        }

        return DynamicSaltDTO.builder()
                .appCode(dynamicSalt.appCode())
                .apiPath(dynamicSalt.apiPath())
                .dynamicSalt(dynamicSalt.dynamicSalt())
                .saltTimestamp(dynamicSalt.saltTimestamp())
                .expireTime(dynamicSalt.expireTime())
                .build();
    }

    /**
     * 领域模型 -> DTO（带 apiId）
     *
     * @param dynamicSalt 动态盐值领域模型
     * @param apiId       接口 ID
     * @return DTO 对象
     */
    public static DynamicSaltDTO toDTO(DynamicSalt dynamicSalt, Long apiId) {
        DynamicSaltDTO dto = toDTO(dynamicSalt);
        if (dto != null) {
            dto.setApiId(apiId);
        }
        return dto;
    }

    /**
     * DTO -> 持久化实体
     *
     * @param dto 动态盐值 DTO
     * @return 持久化实体
     */
    public static ApiDynamicSaltLog toEntity(DynamicSaltDTO dto) {
        if (dto == null) {
            return null;
        }

        return ApiDynamicSaltLog.builder()
                .appCode(dto.getAppCode())
                .apiId(dto.getApiId())
                .apiPath(dto.getApiPath())
                .dynamicSalt(dto.getDynamicSalt())
                .saltTimestamp(dto.getSaltTimestamp())
                .expireTime(dto.getExpireTime())
                .used((short) 0)
                .build();
    }
}
