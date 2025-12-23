package com.microwind.knife.application.dto.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 签名 DTO，用于在应用层传递生成的签名参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignDTO {

    /**
     * 调用方编码（appCode）
     */
    private String appCode;

    /**
     * 接口路径
     */
    private String apiPath;

    /**
     * 签名
     */
    private String signValue;

    /**
     * 生成时间戳（毫秒）
     */
    private Long timestamp;

    /**
     * 签名过期时间
     */
    private LocalDateTime expireTime;
}
