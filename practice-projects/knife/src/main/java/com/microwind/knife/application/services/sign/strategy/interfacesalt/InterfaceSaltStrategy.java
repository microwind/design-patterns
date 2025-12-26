package com.microwind.knife.application.services.sign.strategy.interfacesalt;

/**
 * 接口固定盐值获取策略
 * <p>
 * 定义获取接口固定盐值的标准接口
 */
public interface InterfaceSaltStrategy {

    /**
     * 获取接口固定盐值
     *
     * @param path 接口路径
     * @return 固定盐值
     * @throws IllegalArgumentException 接口信息不存在或固定盐值不存在
     */
    String getInterfaceSalt(String path);
}
