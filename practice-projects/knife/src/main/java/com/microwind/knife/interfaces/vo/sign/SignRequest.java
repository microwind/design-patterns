package com.microwind.knife.interfaces.vo.sign;

import lombok.Data;

import java.util.Map;

// 签名请求DTO
@Data
public class SignRequest {
    private String appCode;
    private String path;
    private String dynamicSalt;
    private Long dynamicSaltTime;
    // 参数可选
    private Map<String, Object> parameters;
}

