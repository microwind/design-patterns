package com.microwind.knife.domain.sign;

/**
 * 用户基本信息值对象
 * 用于API签名验证场景
 *
 * @param appCode   应用编码
 * @param secretKey 应用密钥
 * @param appName   应用名称
 * @param status    状态（1=启用，0=禁用）
 */
public record UserInfo(
        String appCode,
        String secretKey,
        String appName,
        Short status
) {
}
