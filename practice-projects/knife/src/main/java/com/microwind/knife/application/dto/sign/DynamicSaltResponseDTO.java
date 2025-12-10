package com.microwind.knife.application.dto.sign;

import lombok.Data;

// 动态盐值响应DTO
@Data
public class DynamicSaltResponseDTO {
    private String path;
    private String dynamicSalt;
    private Long dynamicSaltTime;
}