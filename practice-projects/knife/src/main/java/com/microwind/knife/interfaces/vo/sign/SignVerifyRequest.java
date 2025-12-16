package com.microwind.knife.interfaces.vo.sign;

import lombok.Data;

import java.util.Map;

// 提交请求DTO
@Data
public class SignVerifyRequest {
    private String appKey;
    private String path;
    private String sign;
    private Long time;
    private Map<String, Object> data;
}

