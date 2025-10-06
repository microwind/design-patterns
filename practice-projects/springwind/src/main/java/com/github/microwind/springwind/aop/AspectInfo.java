package com.github.microwind.springwind.aop;

import java.lang.reflect.Method;

/**
 * 切面信息类
 */
class AspectInfo {
    private String pointcut;
    private AspectType aspectType;
    private Method adviceMethod;
    private Object aspectBean;

    /**
     * 构造函数
     * @param pointcut 切点表达式
     * @param aspectType 切面类型
     * @param adviceMethod 通知方法
     * @param aspectBean 切面Bean
     */
    public AspectInfo(String pointcut, AspectType aspectType, Method adviceMethod, Object aspectBean) {
        this.pointcut = pointcut;
        this.aspectType = aspectType;
        this.adviceMethod = adviceMethod;
        this.aspectBean = aspectBean;
    }

    // getter方法
    public String getPointcut() { return pointcut; }
    public AspectType getAspectType() { return aspectType; }
    public Method getAdviceMethod() { return adviceMethod; }
    public Object getAspectBean() { return aspectBean; }
}
