package com.microwind.knife.interfaces.vo.sign;

import cn.hutool.core.date.DateTime;
import lombok.Data;

import java.time.LocalDateTime;

// 签名响应DTO
@Data
public class SignResponse {
    private String appCode;
    private String path;
    private String sign;
    private Long time;
    private LocalDateTime expireTime;
}

