package com.microwind.knife.application.services.sign.strategy.secretkey;

/**
 * 秘钥获取抽象基类
 * <p>
 * 定义获取秘钥的标准流程，使用模板方法模式。
 * 子类只需实现具体的权限检查和数据获取方式。
 * <p>
 * 标准流程：
 * 1. 检查权限（子类实现）
 * 2. 获取秘钥（子类实现）
 */
public abstract class AbstractSecretKeyStrategy implements SecretKeyRetrievalStrategy {

    @Override
    public final String getSecretKey(String appCode, String path) {
        // 1. 检查权限
        checkPermission(appCode, path);

        // 2. 获取秘钥
        return doGetSecretKey(appCode, path);
    }

    /**
     * 检查权限
     * <p>
     * 子类实现各自的权限检查方式：
     * - 本地配置：通过 ApiAuthConfig 检查
     * - JPA：通过 ApiAuthService 检查，包含 API 信息验证
     * - JDBC：通过 SignRepository 检查，包含 API 信息验证
     *
     * @param appCode 应用编码
     * @param path    接口路径
     * @throws SecurityException        权限不足
     * @throws IllegalArgumentException API 信息不存在或不需要签名
     */
    protected abstract void checkPermission(String appCode, String path);

    /**
     * 获取秘钥
     * <p>
     * 子类实现各自的数据获取方式
     *
     * @param appCode 应用编码
     * @param path    接口路径
     * @return 秘钥
     * @throws IllegalArgumentException 应用不存在
     */
    protected abstract String doGetSecretKey(String appCode, String path);
}
