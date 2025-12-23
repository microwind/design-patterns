package com.microwind.knife.application.dto.apiauth;

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
public class ApiUserDTO {

    /**
     * 调用方编码（appCode）
     */
    private String appCode;

    /**
     * 调用方名称
     */
    private String appName;

    /**
     * 调用方秘钥
     */
    private String secretKey;

    /**
     * 是否有效
     */
    private Short status = 1; // 1启用 0禁用

    /**
     * 每日调用限额
     */
    private Integer dailyLimit;

    /**
     * 密钥过期时间
     */
    private LocalDateTime expireTime;
}
