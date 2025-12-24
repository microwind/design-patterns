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

    /**
     * 领域模型 -> DTO
     *
     * @param sign 签名领域模型
     * @return DTO 对象
     */
    SignDTO toDTO(Sign sign);

    /**
     * SignRequest -> DTO
     * <p>
     * 字段映射：path -> apiPath
     *
     * @param request 签名请求
     * @return DTO 对象
     */
    @Mapping(source = "path", target = "apiPath")
    SignDTO toDTO(SignRequest request);

    /**
     * SignVerifyRequest -> DTO
     * <p>
     * 字段映射：
     * <ul>
     *   <li>path -> apiPath</li>
     *   <li>sign -> signValue</li>
     *   <li>time -> timestamp</li>
     * </ul>
     *
     * @param request 签名验证请求
     * @return DTO 对象
     */
    @Mapping(source = "path", target = "apiPath")
    @Mapping(source = "sign", target = "signValue")
    @Mapping(source = "time", target = "timestamp")
    SignDTO toDTO(SignVerifyRequest request);
}
