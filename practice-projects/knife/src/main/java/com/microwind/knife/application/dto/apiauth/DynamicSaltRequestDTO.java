package com.microwind.knife.application.dto.apiauth;
import lombok.Data;

// 动态盐值请求DTO
@Data
public class DynamicSaltRequestDTO {
    private String appKey;
    private String path;
}