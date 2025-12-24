package com.microwind.knife.application.services.sign.strategy.secretkey;

/**
 * 秘钥获取策略接口
 * <p>
 * 定义应用秘钥获取的统一接口，不同的实现类对应不同的配置模式：
 * - 本地配置文件模式
 * - JPA 数据库模式
 * - JDBC 数据库模式
 */
public interface SecretKeyRetrievalStrategy {
    /**
     * 获取应用秘钥
     *
     * @param appCode 应用编码
     * @param path    接口路径（用于权限验证）
     * @return 应用秘钥
     */
    String getSecretKey(String appCode, String path);
}
