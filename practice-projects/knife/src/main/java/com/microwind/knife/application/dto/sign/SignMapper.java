package com.microwind.knife.application.dto.sign;

import com.microwind.knife.domain.sign.Sign;
import com.microwind.knife.interfaces.vo.sign.SignRequest;
import com.microwind.knife.interfaces.vo.sign.SignVerifyRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

/**
 * 签名值映射器
 * <p>
 * 负责 Sign 领域模型与 DTO、实体之间的转换
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SignMapper {
    SignMapper INSTANCE = Mappers.getMapper(SignMapper.class);

    // 领域模型 -> DTO
    SignDTO toDTO(Sign sign);

    // 将Request转为DTO
    // DTO 字段名不同，需要映射
    @Mapping(source = "path", target = "apiPath")
    SignDTO requestToDTO(SignRequest request);

    // 领域模型 -> DTO
    @Mapping(source = "path", target = "apiPath")
    @Mapping(source = "sign", target = "signValue")
    @Mapping(source = "time", target = "timestamp")
    SignDTO signVerifyRequestToDTO(SignVerifyRequest signVerifyRequest);
}
