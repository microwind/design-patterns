package com.microwind.knife.domain.sign;

/**
 * @param appCode    调用方身份
 * @param path      提交接口路径
 * @param signValue 签名值
 * @param timestamp 签名时间戳（毫秒）
 */
public record Sign(String appCode, String path, String signValue, Long timestamp) {
}