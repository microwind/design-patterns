package com.microwind.knife.interfaces.vo.sign;

import lombok.Data;

// 签名请求DTO
@Data
public class SignRequest {
    private String appKey;
    private String path;
    private String dynamicSalt;
    private Long dynamicSaltTime;
}

