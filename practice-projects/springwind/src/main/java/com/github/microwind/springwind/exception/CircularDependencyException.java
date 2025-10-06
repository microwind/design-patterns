package com.github.microwind.springwind.exception;

import java.util.Set;

/**
 * 循环依赖异常
 * 当检测到Bean之间存在循环依赖时抛出此异常
 */
public class CircularDependencyException extends RuntimeException {

    private final String beanName;
    private final Set<String> dependencyChain;

    public CircularDependencyException(String beanName, Set<String> dependencyChain) {
        super("检测到循环依赖: " + beanName + " -> " + dependencyChain);
        this.beanName = beanName;
        this.dependencyChain = dependencyChain;
    }

    public String getBeanName() {
        return beanName;
    }

    public Set<String> getDependencyChain() {
        return dependencyChain;
    }
}
