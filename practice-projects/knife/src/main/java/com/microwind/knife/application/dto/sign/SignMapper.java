package com.microwind.knife.application.dto.sign;

import com.microwind.knife.domain.sign.Sign;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 签名值映射器
 * <p>
 * 负责 Sign 领域模型与 DTO、实体之间的转换
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public class SignMapper {

    /**
     * 领域模型 -> DTO
     *
     * @param sign 动态盐值领域模型
     * @return DTO 对象
     */
    public static SignDTO toDTO(Sign sign) {
        if (sign == null) {
            return null;
        }

        return SignDTO.builder()
                .appCode(sign.appCode())
                .apiPath(sign.apiPath())
                .signValue(sign.signValue())
                .timestamp(sign.timestamp())
                .expireTime(sign.expireTime())
                .build();
    }
}
