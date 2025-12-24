package com.microwind.knife.application.services.sign;

import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.services.sign.strategy.dynamicsalt.DynamicSaltStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 动态盐值应用服务
 * <p>
 * 负责协调动态盐值的生成和验证。
 * 使用策略模式支持多种配置方式：
 * 1. 本地文件配置方式（适合小型项目，调用方较少，小于50个）
 * 2. JPA 数据库配置方式（适合中大型项目，使用 Spring Data JPA）
 * 3. JDBC 数据库配置方式（适合中大型项目，使用原生 JDBC）
 */
@Service
@RequiredArgsConstructor
public class DynamicSaltService {

    private final DynamicSaltStrategyFactory dynamicSaltStrategyFactory;
    private final DynamicSaltValidationService dynamicSaltValidationService;

    /**
     * 生成动态盐值
     * <p>
     * 使用策略模式根据配置自动选择合适的生成方式
     *
     * @param appCode 调用方应用编码
     * @param path    接口路径
     * @return 生成的动态盐值 DTO 对象
     */
    @Transactional
    public DynamicSaltDTO generate(String appCode, String path) {
        return dynamicSaltStrategyFactory.getStrategy().generate(appCode, path);
    }

    /**
     * 校验动态盐值
     * <p>
     * 委托给 DynamicSaltValidationService 处理
     *
     * @param appCode         应用编码
     * @param path            接口路径
     * @param dynamicSalt     动态盐值
     * @param dynamicSaltTime 动态盐值生成时间戳（毫秒）
     * @return true-校验通过，false-校验失败
     */
    public boolean validateDynamicSalt(String appCode, String path, String dynamicSalt, Long dynamicSaltTime) {
        return dynamicSaltValidationService.validate(appCode, path, dynamicSalt, dynamicSaltTime);
    }

    /**
     * 校验动态盐值（DTO版本）
     *
     * @param dto 动态盐值DTO
     * @return true-校验通过，false-校验失败
     */
    public boolean validateDynamicSalt(DynamicSaltDTO dto) {
        return dynamicSaltValidationService.validate(dto);
    }
}
