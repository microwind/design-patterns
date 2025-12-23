package com.microwind.knife.application.dto.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 动态盐值 DTO，用于在应用层传递生成的动态盐值参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DynamicSaltDTO {

    /**
     * 调用方编码（appCode）
     */
    private String appCode;

    /**
     * 接口 ID（来自 api_info 表）
     */
    private Long apiId;

    /**
     * 接口路径
     */
    private String apiPath;

    /**
     * 动态盐值
     */
    private String dynamicSalt;

    /**
     * 动态盐值生成时间戳（毫秒）
     */
    private Long saltTimestamp;

    /**
     * 动态盐值过期时间
     */
    private LocalDateTime expireTime;
}
