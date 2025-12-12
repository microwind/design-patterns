package com.microwind.knife.application.dto.apiauth;

import lombok.Data;

import java.util.Map;

// 提交请求DTO
@Data
public class SignVerifyRequestDTO {
    private String appKey;
    private String path;
    private String sign;
    private Long time;
    private Map<String, Object> data;
}
