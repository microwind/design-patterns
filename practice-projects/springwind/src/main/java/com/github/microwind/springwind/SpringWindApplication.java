package com.github.microwind.springwind;

import com.github.microwind.springwind.core.SpringWindApplicationContext;

/**
 * SpringWind框架启动类
 * 提供简单的API来启动和管理SpringWind应用
 */
public class SpringWindApplication {

    private SpringWindApplicationContext context;

    /**
     * 启动SpringWind应用
     * @param configClass 配置类
     * @return SpringWindApplication实例
     */
    public static SpringWindApplication run(Class<?> configClass) {
        SpringWindApplication app = new SpringWindApplication();
        app.context = new SpringWindApplicationContext(configClass);
        return app;
    }

    /**
     * 启动SpringWind应用（带参数）
     * @param configClass 配置类
     * @param args 启动参数
     * @return SpringWindApplication实例
     */
    public static SpringWindApplication run(Class<?> configClass, String... args) {
        SpringWindApplication app = new SpringWindApplication();
        // 构造时传入false，不自动刷新，以后用于后续手动控制
        app.context = new SpringWindApplicationContext(configClass, false);
        // 手动调用刷新
        app.context.refresh();
        return app;
    }

    /**
     * 获取Bean实例
     * @param requiredType Bean类型
     * @return Bean实例
     */
    public <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }

    /**
     * 获取Bean实例
     * @param name Bean名称
     * @return Bean实例
     */
    public Object getBean(String name) {
        return context.getBean(name);
    }

    /**
     * 关闭应用上下文
     */
    public void close() {
        if (context != null) {
            context.close();
        }
    }

    /**
     * 获取底层应用上下文（用于集成外部Web服务器等特殊场景）
     * @return SpringWindApplicationContext实例
     */
    public SpringWindApplicationContext getContext() {
        return context;
    }
}