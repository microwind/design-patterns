package com.microwind.knife.application.services.sign.strategy.dynamicsalt;

import com.microwind.knife.application.config.SignConfig;
import com.microwind.knife.application.dto.sign.DynamicSaltDTO;
import com.microwind.knife.application.dto.sign.DynamicSaltMapper;
import com.microwind.knife.domain.sign.DynamicSalt;
import com.microwind.knife.domain.sign.SignDomainService;
import lombok.RequiredArgsConstructor;

/**
 * 动态盐值生成抽象基类
 * <p>
 * 定义生成动态盐值的标准流程，使用模板方法模式。
 * 子类只需实现具体的固定盐值获取、权限检查和日志保存方式。
 * <p>
 * 标准流程：
 * 1. 获取固定盐值（子类实现）
 * 2. 检查权限（子类实现）
 * 3. 生成动态盐值（基类实现）
 * 4. 保存到数据库（子类可选实现）
 */
@RequiredArgsConstructor
public abstract class AbstractDynamicSaltStrategy implements DynamicSaltGenerationStrategy {

    protected final SignDomainService signDomainService;
    protected final SignConfig signConfig;
    protected final DynamicSaltMapper dynamicSaltMapper;

    @Override
    public final DynamicSaltDTO generate(String appCode, String path) {
        // 1. 获取固定盐值（子类实现）
        String interfaceSalt = getFixedSalt(path);

        // 2. 检查权限（子类实现）
        checkPermissions(appCode, path);

        // 3. 生成动态盐值
        Long saltTimestamp = System.currentTimeMillis();
        DynamicSalt dynamicSalt = signDomainService.generateDynamicSalt(
                appCode, path, interfaceSalt, saltTimestamp
        );

        // 4. 保存到数据库（如果需要）
        if (signConfig.isValidateDynamicSaltFromDatabase()) {
            saveDynamicSaltLog(appCode, path, dynamicSalt, saltTimestamp);
        }

        return dynamicSaltMapper.toDTO(dynamicSalt);
    }

    /**
     * 获取固定盐值
     * <p>
     * 子类实现各自的固定盐值获取方式：
     * - 本地配置：通过 ApiAuthConfig 获取
     * - JPA：通过 ApiInfoService 获取，包含 API 信息验证
     * - JDBC：通过 SignRepository 获取，包含 API 信息验证
     *
     * @param path 接口路径
     * @return 固定盐值
     * @throws IllegalArgumentException 固定盐值不存在
     */
    protected abstract String getFixedSalt(String path);

    /**
     * 检查权限
     * <p>
     * 需要检查两个权限：
     * 1. 动态盐值生成接口的访问权限
     * 2. 目标接口的访问权限
     *
     * @param appCode 应用编码
     * @param path    接口路径
     * @throws SecurityException 权限不足
     */
    protected abstract void checkPermissions(String appCode, String path);

    /**
     * 保存动态盐值日志
     * <p>
     * 默认不做任何事，子类根据需要覆盖（仅数据库策略需要）
     *
     * @param appCode       应用编码
     * @param path          接口路径
     * @param dynamicSalt   动态盐值对象
     * @param saltTimestamp 盐值时间戳
     */
    protected void saveDynamicSaltLog(String appCode, String path,
                                      DynamicSalt dynamicSalt, Long saltTimestamp) {
        // 默认不做任何事，本地配置策略不需要保存
    }
}
