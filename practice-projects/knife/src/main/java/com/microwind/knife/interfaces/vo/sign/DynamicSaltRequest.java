package com.microwind.knife.interfaces.vo.sign;
import lombok.Data;

// 动态盐值请求DTO
@Data
public class DynamicSaltRequest {
    private String appCode;
    private String path;
}

