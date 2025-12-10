package com.microwind.knife.domain.sign;

import lombok.Value;

@Value
public class Sign {
    String appKey;          // 调用方身份
    String path;            // 提交接口路径
    String signValue;       // 签名值
    Long timestamp;         // 签名时间戳（毫秒）
}