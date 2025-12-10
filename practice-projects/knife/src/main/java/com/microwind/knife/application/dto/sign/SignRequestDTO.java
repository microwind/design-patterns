package com.microwind.knife.application.dto.sign;

import lombok.Data;

// 签名请求DTO
@Data
public class SignRequestDTO {
    private String appKey;
    private String path;
    private String dynamicSalt;
    private Long dynamicSaltTime;
}