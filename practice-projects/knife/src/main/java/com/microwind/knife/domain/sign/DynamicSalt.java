package com.microwind.knife.domain.sign;

// 动态盐值领域模型（值对象）

import java.time.LocalDateTime;

/**
 * @param appCode       调用方身份
 * @param apiPath       提交接口路径
 * @param dynamicSalt   动态盐值
 * @param expireTime    到期时间[可选]
 * @param saltTimestamp 生成时间戳（毫秒）
 */
public record DynamicSalt(String appCode, String apiPath, String dynamicSalt, LocalDateTime expireTime, Long saltTimestamp) {
}