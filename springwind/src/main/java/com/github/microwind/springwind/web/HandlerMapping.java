package com.github.microwind.springwind.web;

import java.lang.reflect.Method;

/**
 * Handler映射信息类
 */
public class HandlerMapping {
    private final Object controller;
    private final Method method;

    /**
     * 构造函数，初始化Handler映射信息
     * @param controller 控制器实例
     * @param method     处理请求的方法
     */
    public HandlerMapping(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public Object getController() { return controller; }
    public Method getMethod() { return method; }
}