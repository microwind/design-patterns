package com.github.microwind.springwind.web;

import java.lang.reflect.Method;

/**
 * Handler映射信息类
 */
public class HandlerMapping {
    private final Object controller;
    private final Method method;
    private final PathMatcher pathMatcher;

    /**
     * 构造函数，初始化Handler映射信息
     * @param controller 控制器实例
     * @param method     处理请求的方法
     * @param pathPattern 路径模式（可包含路径参数，如 "/user/{id}"）
     */
    public HandlerMapping(Object controller, Method method, String pathPattern) {
        this.controller = controller;
        this.method = method;
        this.pathMatcher = new PathMatcher(pathPattern);
    }

    public Object getController() { return controller; }
    public Method getMethod() { return method; }
    public PathMatcher getPathMatcher() { return pathMatcher; }
}