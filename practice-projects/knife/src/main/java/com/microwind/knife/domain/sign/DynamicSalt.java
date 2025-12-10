package com.microwind.knife.domain.sign;

// 动态盐值领域模型（值对象）
import lombok.Value;

@Value
public class DynamicSalt {
    String appKey;          // 调用方身份
    String path;            // 提交接口路径
    String saltValue;       // 动态盐值
    Long generateTime;      // 生成时间戳（毫秒）
}