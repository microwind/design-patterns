package com.microwind.knife.application.services.sign.strategy.dynamicsalt;

import com.microwind.knife.application.dto.sign.DynamicSaltDTO;

/**
 * 动态盐值生成策略接口
 * <p>
 * 定义动态盐值生成的统一接口，不同的实现类对应不同的配置模式：
 * - 本地配置文件模式
 * - JPA 数据库模式
 * - JDBC 数据库模式
 */
public interface DynamicSaltGenerationStrategy {
    /**
     * 生成动态盐值
     *
     * @param appCode 应用编码
     * @param path    接口路径
     * @return 动态盐值 DTO
     */
    DynamicSaltDTO generate(String appCode, String path);
}
