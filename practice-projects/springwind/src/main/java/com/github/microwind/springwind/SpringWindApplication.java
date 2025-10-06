package com.github.microwind.springwind;

import com.github.microwind.springwind.core.SpringWindApplicationContext;

/**
 * SpringWind框架启动类
 * 提供简单的API来启动和管理SpringWind容器
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
     * 获取Bean实例
     * @param requiredType Bean类型
     * @return Bean实例
     */
    public <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }

    /**
     * 获取Bean实例
     * @param beanName Bean名称
     * @return Bean实例
     */
    public Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    /**
     * 关闭应用上下文
     */
    public void close() {
        if (context != null) {
            context.close();
        }
    }
}