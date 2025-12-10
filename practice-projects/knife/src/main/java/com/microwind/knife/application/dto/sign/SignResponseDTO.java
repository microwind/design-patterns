package com.microwind.knife.application.dto.sign;

import lombok.Data;

// 签名响应DTO
@Data
public class SignResponseDTO {
    private String path;
    private String sign;
    private Long time;
}
