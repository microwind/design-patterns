package com.microwind.knife.domain.sign;

import java.time.LocalDateTime;

/**
 * @param appCode    调用方身份
 * @param apiPath    提交接口路径
 * @param signValue  签名值
 * @param timestamp  签名时间戳（毫秒）
 * @param expireTime 签名到期时间[可选]
 */
public record Sign(String appCode, String apiPath, String signValue, Long timestamp, LocalDateTime expireTime) {
}