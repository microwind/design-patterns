package com.microwind.knife.interfaces.vo.sign;

import lombok.Data;

// 签名响应DTO
@Data
public class SignResponse {
    private String path;
    private String sign;
    private Long time;
}

