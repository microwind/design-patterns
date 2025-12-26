package com.microwind.knife.application.services.sign.strategy.interfacesalt;

/**
 * 接口固定盐值获取抽象基类
 * <p>
 * 定义获取接口固定盐值的标准流程，使用模板方法模式。
 * 子类只需实现具体的数据获取方式。
 * <p>
 * 标准流程：
 * 1. 获取固定盐值（子类实现）
 * 2. 验证盐值存在（基类实现）
 */
public abstract class AbstractInterfaceSaltStrategy implements InterfaceSaltStrategy {

    @Override
    public final String getInterfaceSalt(String path) {
        // 1. 获取固定盐值
        String interfaceSalt = doGetInterfaceSalt(path);

        // 2. 验证盐值存在
        if (interfaceSalt == null || interfaceSalt.isEmpty()) {
            throw new IllegalArgumentException("接口固定盐值不存在，路径：" + path);
        }

        return interfaceSalt;
    }

    /**
     * 获取固定盐值
     * <p>
     * 子类实现各自的数据获取方式：
     * - 本地配置：通过 ApiAuthConfig 获取
     * - JPA：通过 ApiInfoService 获取，包含 API 信息验证
     * - JDBC：通过 SignRepository 获取，包含 API 信息验证
     *
     * @param path 接口路径
     * @return 固定盐值（可能为null或空字符串）
     * @throws IllegalArgumentException API 信息不存在或不需要签名
     */
    protected abstract String doGetInterfaceSalt(String path);
}
