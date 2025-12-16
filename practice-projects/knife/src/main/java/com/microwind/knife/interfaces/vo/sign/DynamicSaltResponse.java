package com.microwind.knife.interfaces.vo.sign;

import lombok.Data;

// 动态盐值响应DTO
@Data
public class DynamicSaltResponse {
    private String path;
    private String dynamicSalt;
    private Long dynamicSaltTime;
}

