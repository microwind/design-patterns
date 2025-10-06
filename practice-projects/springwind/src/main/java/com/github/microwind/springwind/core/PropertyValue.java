package com.github.microwind.springwind.core;

/**
 * 属性值封装类
 */
class PropertyValue {
    private String name;
    private Object value;
    private boolean isRef;

    /**
     * 构造函数
     * @param name 属性名
     * @param value 属性值
     * @param isRef 是否为引用类型
     */
    public PropertyValue(String name, Object value, boolean isRef) {
        this.name = name;
        this.value = value;
        this.isRef = isRef;
    }

    public String getName() { return name; }
    public Object getValue() { return value; }
    public boolean isRef() { return isRef; }
}
