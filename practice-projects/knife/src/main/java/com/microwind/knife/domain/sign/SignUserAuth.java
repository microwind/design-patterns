package com.microwind.knife.domain.sign;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 可以一次性查出某个用户的鉴权【可选】
 *
 * @param appCode
 * @param secretKey
 * @
 * @param permitPaths
 * @param forbiddenPath
 */
public record SignUserAuth(
        String appCode,           // 调用方身份
        String secretKey,        // 调用方秘钥
        List<String> permitPaths, // 允许访问的接口路径
        List<String> forbiddenPath // 禁止访问的几口路径，禁止的优与允许的
) {
}