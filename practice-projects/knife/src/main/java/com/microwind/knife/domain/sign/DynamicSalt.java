package com.microwind.knife.domain.sign;

// 动态盐值领域模型（值对象）

/**
 * @param appKey       调用方身份
 * @param path         提交接口路径
 * @param saltValue    动态盐值
 * @param generateTime 生成时间戳（毫秒）
 */
public record DynamicSalt(String appKey, String path, String saltValue, Long generateTime) {
}